package ie.droidfactory.fragstations.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import java.util.HashMap;

import ie.droidfactory.fragstations.httputils.Parser;
import ie.droidfactory.fragstations.model.Station;
/**
 * Created by kudlaty on 02/06/2016.
 */
public class MyShared {
	private final static String TAG = MyShared.class.getSimpleName();
	
	private static SharedPreferences settings;
	private static SharedPreferences.Editor editor;
	
	private final static String PREF="MY_SETTINGS";
	public static final String KEY_STATIONS = "stations_list";
	public static final String KEY_LOCATION_LAT = "my_last_lat";
	public static final String KEY_LOCATION_LNG = "my_last_lng";
	
	
	public static boolean setStationsMap(Context context, String myXml){
		Log.d(TAG, "setStationsList..");
		settings = context.getSharedPreferences(PREF, Context.MODE_PRIVATE);
		editor = settings.edit();
		editor.putString(KEY_STATIONS, myXml);
		return editor.commit();
		
	}
	
	public static HashMap<String, Station> getStationsMap(Context context){
		Log.d(TAG, "getStationsList...");
		settings = context.getSharedPreferences(PREF, Context.MODE_PRIVATE);
		String myXml = settings.getString(KEY_STATIONS, null);
		return Parser.parseAllStationsMap(context, myXml);
	}

	public static boolean setMyLastLocation(Context context, LatLng mLocation){
		Log.d(TAG, "set my last known location...");
		settings = context.getSharedPreferences(PREF, Context.MODE_PRIVATE);
		editor = settings.edit();
		editor.putString(KEY_LOCATION_LAT, String.valueOf(mLocation.latitude));
		editor.putString(KEY_LOCATION_LNG, String.valueOf(mLocation.longitude));
		return editor.commit();

	}

	public static LatLng getMyLastLocation(Context context){
		Log.d(TAG, "get my last known location...");
		settings = context.getSharedPreferences(PREF, Context.MODE_PRIVATE);
		double lat = Double.parseDouble(settings.getString(KEY_LOCATION_LAT,"0"));
		double lng = Double.parseDouble(settings.getString(KEY_LOCATION_LNG,"0"));
		return new LatLng(lat, lng);
	}

	public static boolean isStationMap(Context context){
		settings = context.getSharedPreferences(PREF, Context.MODE_PRIVATE);
		return settings.contains(KEY_STATIONS);
	}
}
