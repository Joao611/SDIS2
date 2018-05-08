/**
 * 
 */
package chord;

import java.math.BigInteger;

import communication.Client;
import messages.MessageFactory;
import messages.MessageType;
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
		Utils.LOGGER.finest("Running fix finger table\n");
		fix_fingerTable();
		printFingerTable();
	}

	private void printFingerTable() {
		Utils.LOGGER.finest("Tabela de dedos: "+chord.getPeerInfo().getId());
			chord.getFingerTable().forEach((v) -> { Utils.LOGGER.finest("\t"+v.getId()); } );
	}

	public void fix_fingerTable() {
		
		for(int i = 0; i < ChordManager.getM(); i++) {
			String keyToLookup = getKeyToLookUp(chord.getPeerInfo().getId(), i);
			String lookupMessage = MessageFactory.getLookup(chord.getPeerInfo().getId(), keyToLookup);
			String response = chord.lookup(keyToLookup);
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

	private String getKeyToLookUp(String id, int i) {
		
		BigInteger _id = new BigInteger(id, 16);
		BigInteger add =  new BigInteger((Math.pow(2, i)+"").getBytes());
		BigInteger mod =  new BigInteger((Math.pow(2, ChordManager.getM())+"").getBytes());
			
		BigInteger res = _id.add(add).mod(mod);
		return res.toString(16);
	}

	public FixFingerTable(ChordManager chord) {
		this.chord = chord;
	}
}
