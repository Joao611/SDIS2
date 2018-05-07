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
    	Utils.log("Running Stabilize\n");
        PeerInfo successor = this.chordManager.getSuccessor(0);
        String stabilizeMessage = MessageFactory.getHeader(MessageType.STABILIZE, "1.0", this.chordManager.getPeerInfo().getId());
        String response = Client.sendMessage(successor.getAddr(), successor.getPort(), stabilizeMessage, true);
        AbstractPeerInfo predecessor = parseResponse(response);
        
        if (predecessor.isNull()) {
        	this.chordManager.notify(successor);
        } else {
        	if (this.chordManager.stabilize((PeerInfo)predecessor)) {
        		Utils.log("Successor updated");
                this.chordManager.notify((PeerInfo)predecessor);
            }
        }

        

    }
}