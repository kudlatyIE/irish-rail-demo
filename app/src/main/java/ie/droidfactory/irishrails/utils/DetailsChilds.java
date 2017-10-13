package ie.droidfactory.irishrails.utils;
/**
 * Created by kudlaty on 02/06/2016.
 */
public enum DetailsChilds {
	
	DETAILS_TIMETABLE ("timetable"), DETAILS_MAP ("map");//, DETAILS_OTHER ("other");
	
	private String frag_title;
	
	DetailsChilds(String title){
		this.frag_title=title;
	}

	public String getFrag_title() {
		return frag_title;
	}
	

}
