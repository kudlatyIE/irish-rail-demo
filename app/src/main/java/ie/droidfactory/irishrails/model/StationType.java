package ie.droidfactory.irishrails.model;
/**
 * Created by kudlaty on 02/06/2016.
 */
public enum StationType {

	TYPE_O ("O", "Origin"),
	TYPE_T ("T","TimingPoint"),
	TYPE_S ("S","Stop"),
	TYPE_C ("C","Cos"),
	TYPE_D ("D","Destination");
	
	private String type, name;
	
	StationType(String type, String name){
		this.type=type;
		this.name=name;
	}

	public String getType() {
		return type;
	}

	public String getName() {
		return name;
	}
	
}