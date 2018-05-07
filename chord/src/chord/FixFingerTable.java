/**
 * 
 */
package chord;

import communication.Client;
import messages.MessageFactory;
import messages.MessageType;
import utils.UnsignedByte;
import utils.Utils;

/**
 * @author anabela
 *
 */
public class FixFingerTable implements Runnable {

	private ChordManager chord;
	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		Utils.log("Running fix finger table\n");
		fix_fingerTable();
		printFingerTable();
	}

	private void printFingerTable() {
		Utils.log("Tabela de dedos: "+chord.getPeerInfo().getId());
			chord.getFingerTable().forEach((v) -> { Utils.log("\t"+v.getId()); } );
	}

	public void fix_fingerTable() {
		
		for(int i = 0; i < ChordManager.getM(); i++) {
			short keyToLookup = (short) ((chord.getPeerInfo().getId() + Math.pow(2, i))% Math.pow(2, ChordManager.getM()));
			String lookupMessage = MessageFactory.getLookup(chord.getPeerInfo().getId(), keyToLookup);
			String response = chord.lookup(new UnsignedByte(keyToLookup));
			response = response.trim();
			PeerInfo info = new PeerInfo(response);
			while(response.startsWith(MessageType.ASK.getType())) {
				response = Client.sendMessage(info.getAddr(), info.getPort(), lookupMessage, true);
				response = response.trim();
				info = new PeerInfo(response);
			}
			chord.getFingerTable().set(i, info);
		}
	}

	public FixFingerTable(ChordManager chord) {
		this.chord = chord;
	}
}
