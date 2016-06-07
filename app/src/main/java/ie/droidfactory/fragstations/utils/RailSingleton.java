package ie.droidfactory.fragstations.utils;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.HashMap;

import ie.droidfactory.fragstations.model.Station;
import ie.droidfactory.fragstations.model.StationDetails;
/**
 * Created by kudlaty on 02/06/2016.
 */
public class RailSingleton {
	
//	private static ArrayList<Station> stationList=null;
	private static HashMap<String, Station> stationMap=null;
	private static ArrayList<StationDetails> timetable=null;
	public static String currentStationCode="unknown";
	private static LatLng myLocation=null;

//	public static ArrayList<Station> getStationList() {
////		if(stationList==null) stationList = new ArrayList<Station>();
//		return stationList;
//	}
//
//	public static void setStationList(ArrayList<Station> stationList) {
//		RailSingleton.stationList = stationList;
//	}

	public static HashMap<String, Station> getStationMap() {
		return stationMap;
	}

	public static void setStationMap(HashMap<String, Station> stationMap) {
		RailSingleton.stationMap = stationMap;
	}

	public static ArrayList<StationDetails> getTimetable() {
		return timetable;
	}

	public static void setTimetable(ArrayList<StationDetails> timetable) {
		RailSingleton.timetable = timetable;
	}
	
	public static void resetTimetable(){
		currentStationCode = "unknown";
		timetable = null;
	}

	public static LatLng getMyLocation() {
		return myLocation;
	}

	public static void setMyLocation(LatLng myLocation) {
		RailSingleton.myLocation = myLocation;
	}
}
