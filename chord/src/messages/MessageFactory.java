package messages;

import java.io.UnsupportedEncodingException;

import chord.PeerInfo;
import utils.Utils;

public class MessageFactory {

	private static String END_HEADER = "\r\n\r\n";
	private static String NEW_LINE = "\r\n";
	
	public static String getFirstLine(MessageType messageType, String version, short senderId) {
		return messageType.getType() + " " + version + " " + senderId + " " + NEW_LINE;
	}
	
	public static String getHeader(MessageType messageType, String version, short senderId) {
		return getFirstLine(messageType,version,senderId) + NEW_LINE;
	}
	
	public static String appendLine(String message, Object args[]) {
		for (Object arg: args) {
			message += arg.toString() + " ";
		}
		message += END_HEADER;
		return message;
	}
	public static String appendBody(String message, byte [] body) throws UnsupportedEncodingException {
		String bodyStr = new String(body, Utils.ENCODING_TYPE);
		message += bodyStr;
		return message;
	}
	public static String getLookup(short senderId, short lookupKey) {
		String msg = getFirstLine(MessageType.LOOKUP,"1.0",senderId);
		return appendLine(msg, new String[] {""+lookupKey});
	}
	public static String getSuccessor(short senderId, PeerInfo peer) {
		String msg = getFirstLine(MessageType.SUCCESSOR,"1.0",senderId);
		return appendLine(msg, new Object[] {peer.getId(),peer.getAddr().getHostAddress(),peer.getPort()});
	}
	public static String getPredecessor(short senderId, PeerInfo peer) {
		String msg = getFirstLine(MessageType.PREDECESSOR,"1.0",senderId);
		return appendLine(msg, new Object[] {peer.getId(),peer.getAddr().getHostAddress(),peer.getPort()});
	}
	public static String getAsk(short senderId, PeerInfo peer) {
		String msg = getFirstLine(MessageType.ASK,"1.0",senderId);
		return appendLine(msg, new Object[] {peer.getId(),peer.getAddr().getHostAddress(),peer.getPort()});
	}
	public static String getPutChunk(short senderId, PeerInfo peer, String fileID, int chunkNo, int replicationDeg, byte[] body) {
		String msg = getFirstLine(MessageType.PUTCHUNK,"1.0",senderId);
		String msg2 = appendLine(msg, new Object[] {peer.getId(),peer.getAddr().getHostAddress(),peer.getPort(), fileID, chunkNo, replicationDeg});
		try {
			return appendBody(msg2, body);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return null;
		}
	}
	public static String getStored(short senderId, String fileID, int chunkNo, int replicationDeg) {
		String msg = getFirstLine(MessageType.STORED,"1.0",senderId);
		String msg2 = appendLine(msg, new Object[] {fileID, chunkNo, replicationDeg});
		return msg2;
	}
}
