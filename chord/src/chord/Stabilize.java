package chord;

import java.util.concurrent.TimeUnit;

import communication.Client;
import program.Peer;

public class Stabilize implements Runnable {
    private ChordManager chordManager;

    public Stabilize(ChordManager chordManager) {
        this.chordManager = chordManager;
    }

    @Override
    public void run() {
        PeerInfo successor = this.chordManager.getSuccessor(0);

        String response = Client.sendMessage(successor.getAddr(), successor.getPort(), "stabilize");// receives my
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