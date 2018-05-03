package chord;

import communication.Client;

public class CheckPredecessor implements Runnable {

	private AbstractPeerInfo predecessor;
	
	CheckPredecessor(AbstractPeerInfo predecessor){
		this.predecessor = predecessor;
	}
	
	@Override
	public void run() {
		String response = Client.message(predecessor.getAddr(), predecessor.getPort(), "STATUS");
		if (response == null) {
			System.out.println("Predecessor is null");
			predecessor = new NullPeerInfo();
		}
	}

}
