package Server;

public class ContactInfo {
	private ImageNameSet contact;
	private int isAccept;
	
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
