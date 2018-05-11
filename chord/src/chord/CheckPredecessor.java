package chord;

import communication.Client;
import messages.MessageFactory;
import utils.Utils;

public class CheckPredecessor implements Runnable {

	private AbstractPeerInfo predecessor;
	private String myPeerId;
	
	CheckPredecessor(AbstractPeerInfo predecessor, String myPeerId){
		this.predecessor = predecessor;
		this.myPeerId = myPeerId;
	}
	
	@Override
	public void run() {
		if (predecessor.isNull()) {
			System.out.println("Predecessor not set");
			return;
		}
		if (predecessor.getId().equals(myPeerId)) return;
		String pingMessage = MessageFactory.getPing(myPeerId);
		String response = Client.sendMessage(predecessor.getAddr(), predecessor.getPort(), pingMessage, true);
		if (response.equals(MessageFactory.getErrorMessage())) {
			System.out.println("Predecessor is unreachable");
			Utils.LOGGER.finest("Could not establish connection with predecessor");
			predecessor = new NullPeerInfo();
		}
	}

}
