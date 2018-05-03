package messages;

public class MessageFactory {

	private static String END_HEADER = "\r\n\r\n";
	private static String NEW_LINE = "\r\n";
	
	public static String getFirstLine(MessageType messageType, String version, short senderId) {
		return messageType.getType() + " " + version + " " + senderId + " " + NEW_LINE;
	}
	
	public static String getHeader(MessageType messageType, String version, short senderId) {
		return getFirstLine(messageType,version,senderId) + NEW_LINE;
	}
	
	public static String appendLine(String message, String args[]) {
		for (String arg: args) {
			message += arg + " ";
		}
		message += END_HEADER;
		return message;
	}
}
