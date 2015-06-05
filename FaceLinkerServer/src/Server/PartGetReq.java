package Server;

public class PartGetReq {
	public static final String EYES = "eyes";
	public static final String NOSE = "face";
	public static final String MOUTH = "mouth";
	public static final String FACE = "face";
	public static final String STOP = "stop";
	
	private String part_type;
	private int user_id;
	
	public String getPart_type() {
		return part_type;
	}
	public void setPart_type(String part_type) {
		this.part_type = part_type;
	}
	public int getUser_id() {
		return user_id;
	}
	public void setUser_id(int user_id) {
		this.user_id = user_id;
	}
}
