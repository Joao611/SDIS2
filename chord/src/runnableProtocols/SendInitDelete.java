package runnableProtocols;

import chord.ChordManager;
import chord.PeerInfo;
import communication.Client;
import communication.messages.MessageFactory;
import utils.Utils;

public class SendInitDelete implements Runnable{

	private String senderId;
	private String fileId;
	private ChordManager chordManager;
	
	public SendInitDelete(String fileId, ChordManager chordManager) {
		this.senderId = chordManager.getPeerInfo().getId();
		this.fileId = fileId;
		this.chordManager = chordManager;
	}
	
	@Override
	public void run() {
		PeerInfo successor = chordManager.getChunkOwner(fileId);
		String message = MessageFactory.getInitDelete(senderId, fileId);
		Utils.LOGGER.info("Sending DeleteInit for file: " + fileId);
		Client.sendMessage(successor.getAddr(), successor.getPort(), message, false);
		System.out.println("Sent request to delete file: " + fileId);
	}
	
	

}
