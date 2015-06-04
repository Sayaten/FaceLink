package Server;

import java.util.ArrayList;

public class GetContactAck {
	private ArrayList<ImageNameSet> contacts = new ArrayList<ImageNameSet> ();

	public ArrayList<ImageNameSet> getContacts() {
		return contacts;
	}

	public void setContacts(ArrayList<ImageNameSet> contacts) {
		this.contacts = contacts;
	}
}
