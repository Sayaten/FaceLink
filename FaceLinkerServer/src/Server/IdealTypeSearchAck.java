package Server;

import java.util.ArrayList;

public class IdealTypeSearchAck {
	private ArrayList<IdealType> ideal_types;
	
	IdealTypeSearchAck() {
		ideal_types = new ArrayList<IdealType> (5);
	}
	public ArrayList<IdealType> getIdeal_types() {
		return ideal_types;
	}
	public void setIdeal_types(ArrayList<IdealType> ideal_types) {
		this.ideal_types = ideal_types;
	}
	
}
