package chord;

import communication.Client;
import messages.MessageFactory;
import messages.MessageType;
import utils.Utils;

public class Stabilize implements Runnable {
	private ChordManager chordManager;

	public Stabilize(ChordManager chordManager) {
		this.chordManager = chordManager;
	}

	private AbstractPeerInfo parseResponse(String response) { 
		//response should be a string of PREDECESSOR Type
		response = response.trim();
		String[] args = response.split("\r\n")[1].split(" ");
		if (args.length == 3) {
			return new PeerInfo(response);
		}else {
			return new NullPeerInfo();
		}

	}

	@Override
	public void run() {
		String myPeerId = this.chordManager.getPeerInfo().getId();
		Utils.LOGGER.finest("Running Stabilize\n");
		PeerInfo nextPeer = this.chordManager.getNextPeer();
		String stabilizeMessage = MessageFactory.getHeader(MessageType.STABILIZE, "1.0", myPeerId);
		String response = Client.sendMessage(nextPeer.getAddr(), nextPeer.getPort(), stabilizeMessage, true);
		System.out.println("Received stabilize response");
		if(response.equals(MessageFactory.getErrorMessage())) {
			System.err.println("Next peer dropped");
			chordManager.popNextPeer();
			nextPeer = this.chordManager.getNextPeer();
			return;
		}
		
		AbstractPeerInfo x = parseResponse(response);

		this.chordManager.stabilize(x); //might update successor
		nextPeer = this.chordManager.getNextPeer();
		this.chordManager.notify(nextPeer); //notify my successor that I might be his predecessor
	
		
		
		// send my nextPeers to my predecessor.
		AbstractPeerInfo predecessor = chordManager.getPredecessor();
		if (myPeerId.equals(predecessor.getId())) return; //do not send to myself
		
		if (!predecessor.isNull()) {
			String successorsMsg = MessageFactory.getSuccessors(myPeerId, this.chordManager.getNextPeers());
			Client.sendMessage(predecessor.getAddr(), predecessor.getPort(), successorsMsg, false);
		}else {
			System.out.println("Predecessor is null");
		}
		

	}
}