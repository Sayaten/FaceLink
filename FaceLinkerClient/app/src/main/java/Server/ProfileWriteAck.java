package Server;

public class ProfileWriteAck {
	private int result;
	public ProfileWriteAck(){
		
	}
	public ProfileWriteAck(int result){
		this.result = result;
	}
	public int getResult() {
		return result;
	}

	public void setResult(int result) {
		this.result = result;
	}
}
