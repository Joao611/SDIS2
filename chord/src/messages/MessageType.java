package messages;

public enum MessageType {

	ASK("ASK"),
	DELETE("DELETE"), INITDELETE("INITDELETE"),
	PUTCHUNK("PUTCHUNK"),
	KEEPCHUNK("KEEPCHUNK"),
	PING("PING"),
	OK("OK"),
	NOTIFY("NOTIFY"),
	LOOKUP("LOOKUP"),
	PREDECESSOR("PREDECESSOR"),
	STABILIZE("STABILIZE"),
	SUCCESSOR("SUCCESSOR"),
	STORED("STORED"), CONFIRMSTORED("CONFIRMSTORED");
	
	
	private String type;
	 
	MessageType(String type) {
        this.type = type;
    }
	
	public String getType() {
		return type;
	}
	 
}
