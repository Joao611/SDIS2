package messages;

public class MessageFactory {

	private static String END_HEADER = "\r\n\r\n";
	
	private static String getFirstPortion(MessageType messageType, String version, String senderId) {
		return messageType.getType() + " " + version + " " + senderId;
	}
	
	public static String getHeader(MessageType messageType, String version, String senderId) {
		return getFirstPortion(messageType,version,senderId) + END_HEADER;
	}
	
	public static String getHeader(MessageType messageType, String version, String senderId, String otherArgs[]) {
		String message = getFirstPortion(messageType,version,senderId);
		for (String arg: otherArgs) {
			message += " " + arg;
		}
		return message + END_HEADER;
	}
}
