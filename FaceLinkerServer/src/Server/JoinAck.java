package Server;

public class JoinAck {
	private int result;
	
	JoinAck(){
		
	}
	JoinAck(int result){
		this.result = result;
	}

	public int getResult() {
		return result;
	}

	public void setResult(int result) {
		this.result = result;
	}
}
