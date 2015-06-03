package Server;

public class ProfileModifyAck {
	private int result;
	public ProfileModifyAck(){
		
	}
	public ProfileModifyAck(int result){
		this.result = result;
	}
	public int getResult() {
		return result;
	}

	public void setResult(int result) {
		this.result = result;
	}
}
