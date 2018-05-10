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
<<<<<<< HEAD
		Utils.LOGGER.finest("Running Stabilize\n");
		PeerInfo nextPeer = this.chordManager.getSuccessor(0);
=======
		PeerInfo successor = this.chordManager.getSuccessor(0);
>>>>>>> fa58d92247adcf62df4e42d114d0808e05a1cfcd
		String stabilizeMessage = MessageFactory.getHeader(MessageType.STABILIZE, "1.0", this.chordManager.getPeerInfo().getId());
		String response = Client.sendMessage(nextPeer.getAddr(), nextPeer.getPort(), stabilizeMessage, true);
		AbstractPeerInfo x = parseResponse(response);

		this.chordManager.stabilize(x); //might update successor
		this.chordManager.notify(this.chordManager.getSuccessor(0)); //notify my successor that I might be his predecessor



	}
}