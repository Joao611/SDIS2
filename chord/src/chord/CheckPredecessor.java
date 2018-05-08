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
			Utils.LOGGER.finest("Predecessor not set yet");
			return;
		} 
		String response = Client.sendMessage(predecessor.getAddr(), predecessor.getPort(), "status", true);
		if (response == null) {
			Utils.LOGGER.finest("Could not establish connection with predecessor");
			predecessor = new NullPeerInfo();
		}
	}

}
