package messages;

public enum MessageType {

	ASK("ASK"),
	PUTCHUNK("PUTCHUNK"),
	PING("PING"),
	OK("OK"),
	NOTIFY("NOTIFY"),
	LOOKUP("LOOKUP"),
	PREDECESSOR("PREDECESSOR"),
	STABILIZE("STABILIZE"),
	SUCCESSOR("SUCCESSOR"),
	STORED("STORED");
	
	
	private String type;
	 
	MessageType(String type) {
        this.type = type;
    }
	
	public String getType() {
		return type;
	}
	 
}
