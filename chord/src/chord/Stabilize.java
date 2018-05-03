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

    @Override
    public void run() {
        PeerInfo successor = this.chordManager.getSuccessor(0);
        String stabilizeMessage = MessageFactory.getHeader(MessageType.STABILIZE, "1.0", this.chordManager.getPeerInfo().getId());
        String response = Client.sendMessage(successor.getAddr(), successor.getPort(), stabilizeMessage);// receives my
                                                                                                    // successor's
                                                                                                    // predecessor
        response = response.trim();

        PeerInfo predecessor = new PeerInfo(response);

        if (this.chordManager.stabilize(predecessor)) {
            System.out.println("Successor updated");
        }

        //TODO: notify
    }
}