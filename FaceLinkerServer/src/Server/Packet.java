package Server;

public class Packet {
	// Message Type
	// about join
	public static final String PK_JOIN_REQ = "JOIN_REQ";
	public static final String PK_JOIN_ACK = "JOIN_ACK";
	
	// about login
	public static final String PK_LOGIN_REQ = "LOGIN_REQ";
	public static final String PK_LOGIN_ACK = "LOGIN_ACK";
	
	// about profile write
	public static final String PK_PRO_WRITE_REQ = "PRO_WRITE_REQ";
	public static final String PK_PRO_WRITE_ACK = "PRO_WRITE_ACK";

	// about profile modify
	public static final String PK_PRO_MODIFY_REQ = "PRO_MODIFY_REQ";
	public static final String PK_PRO_MODIFY_ACK = "PRO_MODIFY_ACK";
	
	// about part register
	public static final String PK_PART_REG_REQ = "PART_REGISTER_REQ";
	public static final String PK_PART_REG_ACK = "PART_REGISTER_ACK";
	
	// about part get
	public static final String PK_PART_GET_REQ = "PART_GET_REQ";
	public static final String PK_PART_GET_ACK = "PART_GET_ACK";
	public static final String PK_PART_GET_CON = "PART_GET_CON";
	
	// about ideal type register
	public static final String PK_IDEAL_REG_REQ = "IDEAL_REGISTER_REQ";
	public static final String PK_IDEAL_REG_ACK = "IDEAL_REGISTER_ACK";
	
	// about ideal type search
	public static final String PK_IDEAL_SCH_REQ = "IDEAL_SEARCH_REQ";
	public static final String PK_IDEAL_SCH_ACK = "IDEAL_SEARCH_ACK";
	
	// about Contact
	public static final String PK_CONTACT_REQ = "CONTACT_REQ";
	public static final String PK_CONTACT_ACK = "CONTACT_ACK";
	
	// about Contact reply
	public static final String PK_REPLY_CON_REQ = "REPLY_CONTACT_REQ";
	public static final String PK_REPLY_CON_ACK = "REPLY_CONTACT_ACK";
	
	// about get Contact
	public static final String PK_GET_CON_REQ = "GET_CONTACT_REQ";
	public static final String PK_GET_CON_ACK = "GET_CONTACT_ACK";
	
	// about packet field delimiter
	public static final String FIELD_DELIM = "|";
	public static final String PK_DELIM = "?";
	
	// ACK Type
	public static final int SUCCESS = 0x01;
	public static final int FAIL = 0x00;
	
	
	private String type;
	private String data;
	
	public Packet(String type, String data){
		this.type = type;
		this.data = data;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}
}
