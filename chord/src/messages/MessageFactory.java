package messages;

public class MessageFactory {

	private String END_HEADER = "\r\n\r\n";
	
	private String getFirstPortion(String messageType, String version, String senderId) {
		return messageType + " " + version + " " + senderId;
	}
	
	public String getHeader(String messageType, String version, String senderId) {
		return getFirstPortion(messageType,version,senderId) + END_HEADER;
	}
	
	public String getHeader(String messageType, String version, String senderId, String otherArgs[]) {
		String message = getFirstPortion(messageType,version,senderId);
		for (String arg: otherArgs) {
			message += " " + arg;
		}
		return message + END_HEADER;
	}
}
