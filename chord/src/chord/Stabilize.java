package chord;

import java.util.concurrent.TimeUnit;

import communication.Client;
import messages.MessageFactory;
import messages.MessageType;
import program.Peer;

public class Stabilize implements Runnable {
    private ChordManager chordManager;

    public Stabilize(ChordManager chordManager) {
        this.chordManager = chordManager;
    }

    private AbstractPeerInfo parseResponse(String response) { 
    	//response should be a string of PREDECESSOR Type
    	response = response.trim();
        String[] responseArray = response.split("\r\n");
        String secondLine = responseArray[1];
        String[] args = secondLine.split(" ");
        if (args.length == 3) {
        	return new PeerInfo(secondLine);
        }else {
        	return new NullPeerInfo();
        }
        
    }
    
    @Override
    public void run() {
        PeerInfo successor = this.chordManager.getSuccessor(0);
        String stabilizeMessage = MessageFactory.getHeader(MessageType.STABILIZE, "1.0", this.chordManager.getPeerInfo().getId());
        String response = Client.sendMessage(successor.getAddr(), successor.getPort(), stabilizeMessage);
        AbstractPeerInfo predecessor = parseResponse(response);
        
        

        if (this.chordManager.stabilize(predecessor)) {
            System.out.println("Successor updated");
        }

        //TODO: notify
    }
}