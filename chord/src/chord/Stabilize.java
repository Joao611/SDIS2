package chord;

import java.util.concurrent.TimeUnit;

import communication.Client;
import program.Peer;

public class Stabilize implements Runnable {
    private Peer peer;

    public Stabilize(Peer peer) {
        this.peer = peer;
    }

    @Override
    public void run() {
        PeerInfo successor = peer.getChordManager().getSuccessor(0);

        String response = Client.sendMessage(successor.getAddr(), successor.getPort(), "stabilize");// receives my
                                                                                                    // successor's
                                                                                                    // predecessor
        response = response.trim();

        PeerInfo predecessor = new PeerInfo(response);

        if (this.peer.getChordManager().stabilize(predecessor)) {
            System.out.println("Successor updated");
        }

        peer.getThreadPool().schedule(this, 1000, TimeUnit.MILLISECONDS);
    }
}