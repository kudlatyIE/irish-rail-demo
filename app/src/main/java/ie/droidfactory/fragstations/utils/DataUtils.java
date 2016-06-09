package ie.droidfactory.fragstations.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

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

	/**
	 *
	 * provide date in Rail accepted format: dd_MM_yyyy
	 * @param date (dd_MM_yyyy), if null then default date = current day
	 * @return rail format date
	 * @throws ParseException
     */
	@SuppressWarnings(">>UnusedAssignment<< - used to throw exception")
	public static String getFormatedDate(String date) throws ParseException {
		Date d;
		SimpleDateFormat sdf = new SimpleDateFormat("dd_MMM_yyyy");
		if(date==null) {
			date = new Date().toString();
			d = sdf.parse(date);
			date = sdf.format(d);
		}else{
			//check input format
			d = sdf.parse(date);
		}
		return date;
	}

}
