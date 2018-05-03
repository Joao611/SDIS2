package messages;

public enum MessageType {

	PUTCHUNK("PUTCHUNK"),
	PING("PING"),
	OK("OK"),
	NOTIFY("NOTIFY"),
	LOOKUP("LOOKUP"),
	SUCCESSOR("SUCCESSOR"),
	ASK("ASK");
	
	private String type;
	 
	MessageType(String type) {
        this.type = type;
    }
	
	public String getType() {
		return type;
	}
	 
}
