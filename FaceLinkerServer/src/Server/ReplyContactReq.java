package Server;

public class ReplyContactReq {
	private String send_user;
	private String rec_user;
	private int Reply;

	public int getReply() {
		return Reply;
	}
	public void setReply(int reply) {
		Reply = reply;
	}
	public String getSend_user() {
		return send_user;
	}
	public void setSend_user(String send_user) {
		this.send_user = send_user;
	}
	public String getRec_user() {
		return rec_user;
	}
	public void setRec_user(String rec_user) {
		this.rec_user = rec_user;
	}
}
