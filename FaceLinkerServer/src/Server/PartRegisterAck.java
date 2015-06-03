package Server;

public class PartRegisterAck {
	private int result;
	public PartRegisterAck(){
		
	}
	public PartRegisterAck(int result){
		this.result = result;
	}
	public int getResult() {
		return result;
	}

	public void setResult(int result) {
		this.result = result;
	}
}
