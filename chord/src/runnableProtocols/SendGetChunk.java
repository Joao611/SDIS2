/**
 * 
 */
package runnableProtocols;

import chord.ChordManager;
import chord.PeerInfo;
import communication.Client;
import communication.messages.MessageFactory;
import database.BackupRequest;

/**
 * @author anabela
 *
 */
public class SendGetChunk implements Runnable {

	BackupRequest backupRequest;
	int chunkNo;
	ChordManager chord;

	public SendGetChunk(BackupRequest backupRequest, int chunkNo, ChordManager chord) {
		super();
		this.backupRequest = backupRequest;
		this.chunkNo = chunkNo;
		this.chord = chord;
	}

	@Override
	public void run() {
		PeerInfo owner = chord.getChunkOwner(backupRequest.getFileId());
		String getChunkMessage = MessageFactory.getGetChunk(chord.getPeerInfo().getId(), chord.getPeerInfo().getAddr(),chord.getPeerInfo().getPort(), this.backupRequest.getFileId(), this.chunkNo);
		Client.sendMessage(owner.getAddr(), owner.getPort(), getChunkMessage, false);
	}

}
