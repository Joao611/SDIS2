package chord;

import communication.Client;

public class CheckPredecessor implements Runnable {

	private AbstractPeerInfo predecessor;
	
	CheckPredecessor(AbstractPeerInfo predecessor){
		this.predecessor = predecessor;
	}
	
	@Override
	public void run() {
		if (predecessor.isNull()) {
			System.out.println("Predecessor not set yet");
			return;
		} 
		String response = Client.sendMessage(predecessor.getAddr(), predecessor.getPort(), "status", true);
		if (response == null) {
			System.out.println("Could not establish connection with predecessor");
			predecessor = new NullPeerInfo();
		}
	}

}
