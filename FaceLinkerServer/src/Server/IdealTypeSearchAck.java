package Server;

import java.util.ArrayList;

public class IdealTypeSearchAck {
	private ArrayList<ImageNameSet> ideal_types;
	
	public IdealTypeSearchAck() {
		ideal_types = new ArrayList<ImageNameSet> (5);
	}
	public ArrayList<ImageNameSet> getIdeal_types() {
		return ideal_types;
	}
	public void setIdeal_types(ArrayList<ImageNameSet> ideal_types) {
		this.ideal_types = ideal_types;
	}
	
}
