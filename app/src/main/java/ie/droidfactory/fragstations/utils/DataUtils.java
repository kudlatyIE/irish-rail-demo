package ie.droidfactory.fragstations.utils;

import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.util.Log;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import ie.droidfactory.fragstations.model.Station;
import ie.droidfactory.fragstations.model.StationDetails;
/**
 * Created by kudlaty on 02/06/2016.
 */
public class DataUtils {

	private final static String TAG = DataUtils.class.getSimpleName();
	
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
	 * get current location
	 */
	public static Location getLocation(Context context){
		Location l= null;
		LocationManager lm = null;
		MyLocationListener listener = new MyLocationListener();

		lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
		if(lm.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
			lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, listener);
			l = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
		}
		if(lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
			lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, listener);
			l = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
		}


		if(l!=null){
			Log.d(TAG, "current lat, long: "+l.getLatitude()+", "+l.getLongitude());
		}
		lm.removeUpdates(listener);
		return l;
	}

}
