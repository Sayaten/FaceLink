package Server;

public class Packet {
	// Message Type
	public static final String PK_JOIN_REQ = "JOIN_REQ";
	public static final String PK_JOIN_ACK = "JOIN_ACK";
	public static final String FIELD_DELIM = "|";
	public static final String PK_DELIM = "\n";
	
	// ACK Type
	public static final int JR_SUCCESS = 0x01;
	public static final int JR_FAIL = 0x00;
	
	private String type;
	private String data;
	
}
