package Server;

public class Packet {
	// Message Type
	public static final String PK_JOIN_REQ = "JOIN_REQ";
	public static final String PK_JOIN_ACK = "JOIN_ACK";
	
	public static final String PK_PRO_WRITE_REQ = "PRO_WRITE_REQ";
	public static final String PK_PRO_WRITE_ACK = "PRO_WRITE_ACK";
	
	public static final String FIELD_DELIM = "|";
	public static final String PK_DELIM = "?";
	
	// ACK Type
	// About join request
	public static final int JR_SUCCESS = 0x01;
	public static final int JR_FAIL = 0x00;
	
	// About profile write request
	public static final int PWR_SUCCESS = 0x03;
	public static final int PWR_FAIL = 0x02;
	
	private String type;
	private String data;
	
	Packet(String type, String data){
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
