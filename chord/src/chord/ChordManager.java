/**
 * 
 */
package chord;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.TimeUnit;

import communication.Client;
import communication.messages.MessageFactory;
import communication.messages.MessageType;
import database.Database;
import utils.SingletonThreadPoolExecutor;
import utils.Utils;

/**
 * @author anabela
 *
 */
public class ChordManager implements Runnable {

	private static final int M = 32; // 32bits - 4 bytes
	private static final int NEXT_PEERS_MAX_SIZE = 5;
	private PeerInfo peerInfo;
	private ArrayList<PeerInfo> fingerTable = new ArrayList<PeerInfo>();
	private AbstractPeerInfo predecessor;

	private Deque<PeerInfo> nextPeers; //this is an enhancement to the protocol

	private String ASK_MESSAGE;
	private String SUCCESSOR_MESSAGE;
	private Database database;

	public ChordManager(Integer port) {

		InetAddress addr;
		try {
			addr = InetAddress.getLocalHost();
		} catch (UnknownHostException e) {
			e.printStackTrace();
			return;
		}

		MessageDigest digest;
		try {
			digest = MessageDigest.getInstance("md5");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			return;
		}

		byte[] hash = digest.digest(("" + addr + port).getBytes(StandardCharsets.ISO_8859_1));
		String id = Utils.getIdFromHash(hash, M/8);
		this.setPeerInfo(new PeerInfo(id, addr, port));

		ASK_MESSAGE = MessageFactory.getFirstLine(MessageType.ASK, "1.0", this.getPeerInfo().getId());
		SUCCESSOR_MESSAGE = MessageFactory.getFirstLine(MessageType.SUCCESSOR, "1.0", this.getPeerInfo().getId());
		nextPeers = new ConcurrentLinkedDeque<PeerInfo>();

		for (int i = 0; i < getM(); i++) {
			getFingerTable().add(getPeerInfo());
		}
		setNextPeer(getPeerInfo());
		predecessor = new NullPeerInfo();
	}

	@Override
	public void run() {

		Stabilize stabilizeThread = new Stabilize(this);
		SingletonThreadPoolExecutor.getInstance().get().scheduleAtFixedRate(stabilizeThread, 10, 5000, TimeUnit.MILLISECONDS);

		CheckPredecessor checkPredecessorThread = new CheckPredecessor(this);
		SingletonThreadPoolExecutor.getInstance().get().scheduleAtFixedRate(checkPredecessorThread, 1000, 10000, TimeUnit.MILLISECONDS);

		FixFingerTable fixFingerTableThread = new FixFingerTable(this);
		SingletonThreadPoolExecutor.getInstance().get().scheduleAtFixedRate(fixFingerTableThread, 2000, 5000, TimeUnit.MILLISECONDS);

	}

	public void join(InetAddress addr, int port) {
		String lookupMessage = MessageFactory.getFirstLine(MessageType.LOOKUP, "1.0",getPeerInfo().getId());
		lookupMessage = MessageFactory.appendLine(lookupMessage, new String[]{""+getPeerInfo().getId()});
		String response = Client.sendMessage(addr, port, lookupMessage, true);

		PeerInfo nextPeer = new PeerInfo(response);

		while (response.startsWith("Ask")) {
			response = Client.sendMessage(nextPeer.getAddr(), nextPeer.getPort(), lookupMessage, true);
			if (response == null) {
				System.err.println("Could not join the network");
				Utils.LOGGER.severe("Could not join the network");
				return;
			}
			nextPeer = new PeerInfo(response);
		}
		setNextPeer(nextPeer);
	}

	public void setNextPeer(PeerInfo nextPeer) {
		if (nextPeer == null || nextPeer.isNull()) return;
		this.getFingerTable().set(0, nextPeer);
		PeerInfo first = this.nextPeers.peek();
		if (first != null) {
			if (first.getId().equals(nextPeer.getId())) return;
		}
		nextPeers.push(nextPeer);
	}

	/**
	 * Returna o successor da key, ou a quem perguntor
	 * 
	 * @param key
	 *            a procurar
	 * @return
	 */
	public String lookup(String key) {
		if (!this.predecessor.isNull()) {
			if (Utils.inBetween(this.predecessor.getId(), this.getPeerInfo().getId(), key)) {
				return MessageFactory.appendLine(SUCCESSOR_MESSAGE, this.getPeerInfo().asArray());
			}
		}
		if (Utils.inBetween(this.getPeerInfo().getId(), this.getFingerTable().get(0).getId(), key)) {
			return MessageFactory.appendLine(SUCCESSOR_MESSAGE, this.getFingerTable().get(0).asArray());
		}
		String closestPrecedingNode = closestPrecedingNode(key);
		if (closestPrecedingNode != null) return closestPrecedingNode;
		return MessageFactory.appendLine(ASK_MESSAGE, this.getFingerTable().get(getM() - 1).asArray());
	}

	public String closestPrecedingNode(String key) {
		String fingersMsg = null;
		String nextPeersMsg = null;
		String fingerTableHighestId = null;
		String nextPeersHighestId = null;
		Iterator<PeerInfo> it = nextPeers.descendingIterator();
		AbstractPeerInfo currentPeer;
		for (int i = getM() - 1; i > 0; i--) {
			currentPeer = this.getFingerTable().get(i);
			if (Utils.inBetween(this.getPeerInfo().getId(), key, currentPeer.getId())) {
				fingerTableHighestId = currentPeer.getId();
				fingersMsg = MessageFactory.appendLine(ASK_MESSAGE, currentPeer.asArray());
				break;
			}
		}
		//TEST ME
		while(it.hasNext()) {
			currentPeer = it.next();
			if (Utils.inBetween(this.getPeerInfo().getId(), key, currentPeer.getId())) {
				nextPeersHighestId = currentPeer.getId();
				nextPeersMsg = MessageFactory.appendLine(ASK_MESSAGE, currentPeer.asArray());
				break;
			}
		}
		if (fingerTableHighestId != null || nextPeersHighestId != null) {
			String highestId = Utils.highestId(fingerTableHighestId, nextPeersHighestId);
			if (highestId == fingerTableHighestId) return fingersMsg;
			return nextPeersMsg;
		}
		return null;
	}

	/**
	 * 
	 * @param x
	 * @return True if successor was updated
	 */
	public void stabilize(AbstractPeerInfo x) {
		if (x.isNull()) return;
		PeerInfo successor = getNextPeer();
		PeerInfo potentialSuccessor = (PeerInfo) x;

		if (Utils.inBetween(this.peerInfo.getId(),
				successor.getId(),
				potentialSuccessor.getId())) {
			setNextPeer(potentialSuccessor);
		}
	}

	public void printNextPeers() {
		Iterator<PeerInfo> it = nextPeers.iterator();
		Utils.LOGGER.info("Printing next peers");
		while(it.hasNext()) {
			Utils.LOGGER.info(it.next().getId());
		}
		Utils.LOGGER.info("------------");
	}
	/**
	 * Notify newly found closer successor node that I might be its predecessor
	 * @param newSuccessorId Closer successor than previous successor.
	 */
	public void notify(PeerInfo newSuccessor) {
		String message = MessageFactory.getNotify(this.getPeerInfo().getId(), this.getPeerInfo().getPort());
		String response = Client.sendMessage(newSuccessor.getAddr(), newSuccessor.getPort(), message, true);
		if (response == null) {
			Utils.LOGGER.warning("Next peer dropped");
			this.popNextPeer();
		}
	}

	public PeerInfo getChunkOwner(String key) {
		if (Utils.inBetween(this.predecessor.getId(), this.getPeerInfo().getId(), key)) {
			return this.peerInfo;
		}
		if (Utils.inBetween(this.getPeerInfo().getId(), this.getFingerTable().get(0).getId(), key)) {
			return this.getFingerTable().get(0);
		}

		InetAddress addr = null;
		int port = -1;

		for (int i = getM() - 1; i > 0; i--) {
			if (Utils.inBetween(this.getPeerInfo().getId(), key, this.getFingerTable().get(i).getId())) {
				addr = this.getFingerTable().get(i).getAddr();
				port = this.getFingerTable().get(i).getPort();
			}
		}
		if(port == -1) {
			addr = this.getFingerTable().get(getM() - 1).getAddr();
			port = this.getFingerTable().get(getM() - 1).getPort();
		}

		String lookupMessage = MessageFactory.getLookup(this.peerInfo.getId(), key);
		String response = Client.sendMessage(addr, port, lookupMessage, true);

		PeerInfo owner = new PeerInfo(response);

		while (response.startsWith("Ask")) {
			response = Client.sendMessage(owner.getAddr(), owner.getPort(), lookupMessage, true);
			if (response == null) {
				System.err.println("Could not join the network");
				Utils.LOGGER.severe("Could not join the network");
				return null;
			}
			response = response.trim();
			owner = new PeerInfo(response);
		}

		return owner;
	}

	public List<PeerInfo> getNextPeers() {
		List<PeerInfo> nextPeersArray = new ArrayList<PeerInfo>();
		int i = 0;
		Iterator<PeerInfo> it = nextPeers.iterator();
		while(it.hasNext() && i++ < NEXT_PEERS_MAX_SIZE-1) {
			PeerInfo nextPeer = it.next();
			nextPeersArray.add(nextPeer);
		}
		return nextPeersArray;
	}

	public void popNextPeer() {
		nextPeers.pop();
		this.getFingerTable().set(0,nextPeers.peekFirst());
		try {
			String keyToLookup = FixFingerTable.getKeyToLookUp(this.getPeerInfo().getId(), 0);
			String lookupMessage = MessageFactory.getLookup(this.getPeerInfo().getId(), keyToLookup);
			String response = this.lookup(keyToLookup);
			response = response.trim();
			PeerInfo info = new PeerInfo(response);
			while(response.startsWith(MessageType.ASK.getType())) {
				response = Client.sendMessage(info.getAddr(), info.getPort(), lookupMessage, true);
				if (response == null) return;
				info = new PeerInfo(response);
			}
			this.getFingerTable().set(0, info);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public PeerInfo getNextPeer() {
		return nextPeers.peek();
	}

	/**
	 * @return the m
	 */
	public static int getM() {
		return M;
	}

	public PeerInfo getSuccessor(int index) {
		return this.fingerTable.get(index);
	}

	public AbstractPeerInfo getPredecessor() {
		return this.predecessor;
	}

	public void setPredecessor(AbstractPeerInfo nullPeerInfo) {
		this.predecessor = nullPeerInfo;
	}

	/**
	 * @return the peerInfo
	 */
	public PeerInfo getPeerInfo() {
		return peerInfo;
	}

	/**
	 * @param peerInfo
	 *            the peerInfo to set
	 */
	public void setPeerInfo(PeerInfo peerInfo) {
		this.peerInfo = peerInfo;
	}

	/**
	 * @return the fingerTable
	 */
	public ArrayList<PeerInfo> getFingerTable() {
		return fingerTable;
	}

	/**
	 * @param fingerTable
	 *            the fingerTable to set
	 */
	public void setFingerTable(ArrayList<PeerInfo> fingerTable) {
		this.fingerTable = fingerTable;
	}

	/**
	 * @return the database
	 */
	public Database getDatabase() {
		return database;
	}

	/**
	 * @param database the database to set
	 */
	public void setDatabase(Database database) {
		this.database = database;
	}

	public void updateNextPeers(Deque<PeerInfo> peersReceived) {
		PeerInfo nextPeer = nextPeers.pop();
		nextPeers.clear();
		nextPeers = peersReceived;
		nextPeers.push(nextPeer);
	}
}
