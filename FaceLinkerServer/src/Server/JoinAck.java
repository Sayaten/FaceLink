package Server;

public class JoinAck {
	private int result;
	private int user_id;
	
	JoinAck(){
		
	}
	JoinAck(int result, int user_id){
		this.result = result;
		this.user_id = user_id;
	}
	
	public int getUser_id() {
		return user_id;
	}

	public void setUser_id(int user_id) {
		this.user_id = user_id;
	}

	public int getResult() {
		return result;
	}

	public void setResult(int result) {
		this.result = result;
	}
}
