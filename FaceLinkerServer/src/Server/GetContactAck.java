package Server;

import java.util.ArrayList;

public class GetContactAck {
	private ArrayList<ContactInfo> contacts = new ArrayList<ContactInfo> ();

	public ArrayList<ContactInfo> getContacts() {
		return contacts;
	}

	public void setContacts(ArrayList<ContactInfo> contacts) {
		this.contacts = contacts;
	}
}
