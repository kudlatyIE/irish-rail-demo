package ie.droidfactory.fragstations.utils;

import java.util.ArrayList;

import ie.droidfactory.fragstations.model.Station;
import ie.droidfactory.fragstations.model.StationDetails;
/**
 * Created by kudlaty on 02/06/2016.
 */
public class DataUtils {
	
	public final static int SORT_AZ = 0, SORT_ZA=1, SORT_DISTANCE=3;
	
	/**
	 * sorting timatable by dueIn - num of minutes till the train will arrive here
	 * @param list
	 * @return
	 * @throws Exception 
	 */
	public static ArrayList<StationDetails> sortTimetable(ArrayList<StationDetails> list) throws Exception{
		if(list==null) throw new Exception("station list is null");
		ArrayList<StationDetails> result = new ArrayList<StationDetails>();
		
		return result;
	}
	
	/**
	 * sorting stations list alphabetically (A-Z, Z-A) or by distance
	 * @param list
	 * @param sortingMode
	 * @return
	 * @throws Exception 
	 */
	public static ArrayList<Station> sortStations(ArrayList<Station> list, int sortingMode) throws Exception{
		if(list==null) throw new Exception("timetable list is null");
		ArrayList<Station> result = new ArrayList<Station>();
		
		return result;
	}

}
