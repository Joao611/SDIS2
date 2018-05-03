package chord;

import communication.Client;

public class CheckPredecessor implements Runnable {

	private AbstractPeerInfo predecessor;
	
	CheckPredecessor(AbstractPeerInfo predecessor){
		this.predecessor = predecessor;
	}
	
	@Override
	public void run() {
		if (predecessor.isNull()) return;
		String response = Client.sendMessage(predecessor.getAddr(), predecessor.getPort(), "status");
		if (response == null) {
			System.out.println("Could not establish connection with predecessor");
			predecessor = new NullPeerInfo();
		}
	}

}
