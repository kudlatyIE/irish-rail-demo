package ie.droidfactory.fragstations.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

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
	
	
	public static boolean setStationsMap(Context context, String myXml){
		Log.d(TAG, "setStationsList..");
		settings = context.getSharedPreferences(PREF, Context.MODE_PRIVATE);
		editor = settings.edit();
		editor.putString(KEY_STATIONS, myXml);
		return editor.commit();
		
	}
	
	public static HashMap<String, Station> getStationsMap(Context context){
		Log.d(TAG, "setStationsList...");
		settings = context.getSharedPreferences(PREF, Context.MODE_PRIVATE);
		String myXml = settings.getString(KEY_STATIONS, null);
		return Parser.parseAllStationsMap(myXml);
	}

	public static boolean isStationMap(Context context){
		settings = context.getSharedPreferences(PREF, Context.MODE_PRIVATE);
		return settings.contains(KEY_STATIONS);
	}
}
