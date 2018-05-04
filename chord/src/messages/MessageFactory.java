package messages;

import chord.PeerInfo;

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
}
