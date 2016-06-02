package ie.droidfactory.fragstations.model;
/**
 * Created by kudlaty on 02/06/2016.
 */
public enum StationType {

	TYPE_A ("A", "All"),
	TYPE_M ("M","Mainline"),
	TYPE_S ("S","suburban"),
	TYPE_D ("D","DART");
	
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