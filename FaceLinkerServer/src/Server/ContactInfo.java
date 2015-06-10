package Server;

public class ContactInfo {
	private ImageNameSet contact;
	private int isAccept;
	
	public static final int REJECT = 0x00;
	public static final int ACCEPT = 0x01;
	public static final int STANDBY = 0x02;
	
	public ContactInfo(){
		
	}
	
	public ContactInfo(ImageNameSet contact, int isAccept){
		this.contact = contact;
		this.isAccept = isAccept;
	}
	
	public ImageNameSet getContact() {
		return contact;
	}
	public void setContact(ImageNameSet contact) {
		this.contact = contact;
	}
	public int getIsAccept() {
		return isAccept;
	}
	public void setIsAccept(int isAccept) {
		this.isAccept = isAccept;
	}
}
