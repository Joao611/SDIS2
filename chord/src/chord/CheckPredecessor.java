package chord;

import communication.Client;
import utils.Utils;

public class CheckPredecessor implements Runnable {

	private AbstractPeerInfo predecessor;
	
	CheckPredecessor(AbstractPeerInfo predecessor){
		this.predecessor = predecessor;
	}
	
	@Override
	public void run() {
		if (predecessor.isNull()) {
			Utils.log("Predecessor not set yet");
			return;
		} 
		String response = Client.sendMessage(predecessor.getAddr(), predecessor.getPort(), "status", true);
		if (response == null) {
			Utils.log("Could not establish connection with predecessor");
			predecessor = new NullPeerInfo();
		}
	}

}
