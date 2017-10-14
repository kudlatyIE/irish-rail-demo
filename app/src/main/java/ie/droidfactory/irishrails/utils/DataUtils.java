package ie.droidfactory.irishrails.utils;

import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.util.Log;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import ie.droidfactory.irishrails.model.Station;
import ie.droidfactory.irishrails.model.StationDetails;
/**
 * Created by kudlaty on 02/06/2016.
 */
public class DataUtils {

	private final static String TAG = DataUtils.class.getSimpleName();
	
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
		Date d = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("dd_MMM_yyyy", Locale.ENGLISH);
		if(date==null) {

			date = sdf.format(d);
		}else{
			//check input format
			d = sdf.parse(date);
		}
		return date;
	}

	public static String formatDistance(double distance){
		int d = (int) distance;
		if(d<=1000) return String.valueOf(d).concat("m");
		else {
			DecimalFormat df = new DecimalFormat("#.##");
			return df.format(distance/1000).concat("km");
		}
	}

    /**
     *
     * @param date train date - beware of midnight
     * @param timeOrigin origin station departed time
     * @param timeNext next station departed/arrived time
     * @param offset extra minute to compare departed time (if train is late)
     * @return
     */
	public static boolean compareTrainDateNow(String date, String timeOrigin, String timeNext, int offset){
		DateFormat format = new SimpleDateFormat("dd MMM yyyy HH:mm:ss", Locale.ENGLISH);
		Calendar now = Calendar.getInstance();
        String tOrigin;
        if(timeOrigin==null) {
            tOrigin="00:00:00";
            Log.d(TAG, "time origin is NUll, set default: "+tOrigin);
        }
        else tOrigin=timeOrigin;
		try {
            Date origin  = format.parse(date+" "+tOrigin);
			Date next  = format.parse(date+" "+timeNext);
            if(next.before(origin)){

				Calendar cNext= Calendar.getInstance();
				cNext.setTime(next);
				cNext.add(Calendar.HOUR, 24);
				return cNext.getTime().after(origin);
            }else{
                now.add(Calendar.MINUTE, offset);
				return next.before(now.getTime());
            }

		} catch (ParseException e) {
			e.printStackTrace();
			return false;
		}
	}


}
