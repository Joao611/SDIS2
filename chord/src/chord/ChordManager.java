/**
 * 
 */
package chord;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

import communication.Client;
import utils.UnsignedByte;
import utils.Utils;

/**
 * @author anabela
 *
 */
public class ChordManager implements Runnable {

	private static final int M = 8;
	private PeerInfo peerInfo;
	private ArrayList<PeerInfo> fingerTable = new ArrayList<PeerInfo>();
	private PeerInfo previous;

	public void join(InetAddress addr, int port) {
		String response = Client.sendMessage(addr, port, "lookup " + peerInfo.getId());
		response = response.trim();
		PeerInfo info = new PeerInfo(response);
		if(response.startsWith("Ask")) {
			//TODO: Repeat to the new Node
		} else {
			this.fingerTable.set(0, info);
		}
	}

	public ChordManager(Integer port) {
		this.peerInfo = new PeerInfo(null,null, null);
		try {
			this.peerInfo.setAddr(InetAddress.getLocalHost());
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		}
		this.peerInfo.setPort(port);
		MessageDigest digest;
		try {
			digest = MessageDigest.getInstance("SHA-256");
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		}
		byte[] hash = digest.digest((this.peerInfo.getAddr().getHostAddress() + this.peerInfo.getPort()).getBytes(StandardCharsets.ISO_8859_1));
		this.peerInfo.setId(new UnsignedByte(ByteBuffer.wrap(hash).getShort()));
		
		for (int i = 0; i < getM(); i++) {
			fingerTable.add(peerInfo);
//			TODO: null design pattern
		}
		previous = peerInfo; //TODO null, design
	}

	@Override
	public void run() {
		
	}

	/**
	 * Returna o successor da key, ou a quem perguntor
	 * @param key a procurar
	 * @return 
	 */
	public String lookup(UnsignedByte key) {
		if(Utils.inBetween(this.previous.getId(),this.peerInfo.getId(), key.getUsignedByte())) {
			return "Successor "+ this.peerInfo.toString();
		}
		if(Utils.inBetween(this.peerInfo.getId(), this.fingerTable.get(0).getId(), key.getUsignedByte())) {
			return "Successor "+ this.fingerTable.get(0).toString();
		}
		for(int i = getM()-1; i >= 0; i--) {
			if(Utils.inBetween(this.peerInfo.getId(), key.getUsignedByte(), this.fingerTable.get(i).getId())) {
				return "Ask "+ this.fingerTable.get(i).toString();
			}
		}
		return "Ask "+ this.fingerTable.get(getM()-1).toString();
	}

	/**
	 * @return the m
	 */
	public static int getM() {
		return M;
	}


}
