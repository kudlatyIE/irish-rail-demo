package ie.droidfactory.irishrails.utils;

import android.location.Location;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.HashMap;

import ie.droidfactory.irishrails.model.Station;
import ie.droidfactory.irishrails.model.StationDetails;
import ie.droidfactory.irishrails.model.Train;
import ie.droidfactory.irishrails.model.TrainDetails;

/**
 * Created by kudlaty on 02/06/2016.
 */
public class RailSingleton {

	private static HashMap<String, Station> stationMap=null;
	private static HashMap<String, Train> trainsMap=null;
//	private static HashMap<Integer, TrainDetails> trainDetailsMap=null;
	private static ArrayList<TrainDetails> trainDetailsList;
	private static HashMap<String,StationDetails> timetableMap=null;
	private static ArrayList<StationDetails> timetableList=null;
	public static String currentStationCode="unknown";
	private static Location myLocation=null;
	private static LatLng myLatLng=null;
//	private static double myLat, myLng;
	private static String asyncResult; // just for develop test


	public static HashMap<String, Station> getStationMap() {
		return stationMap;
	}

	public static void setStationMap(HashMap<String, Station> stationMap) {
		RailSingleton.stationMap = stationMap;
	}

	public static ArrayList<StationDetails> getTimetableList() {
		return timetableList;
	}

	public static void setTimetableList(ArrayList<StationDetails> timetableList) {
		RailSingleton.timetableList = timetableList;
	}

	public static HashMap<String, StationDetails> getTimetable() {
		return timetableMap;
	}

	
	public static void resetTimetable(){
		currentStationCode = "unknown";
		timetableMap = null;
	}
	public static LatLng getMyLatLng() {
		return myLatLng;
	}

	public static void setMyLatLng(LatLng myLocation) {
		RailSingleton.myLatLng = myLocation;
	}


	public static Location getMyLocation() {
		return myLocation;
	}

	public static void setMyLocation(LatLng myLocation) {
		setMyLatLng(myLocation);
		Location l = new Location("");
		l.setLatitude(myLocation.latitude);
		l.setLongitude(myLocation.longitude);
		RailSingleton.myLocation = l;
//		RailSingleton.myLat = myLocation.latitude;
//		RailSingleton.myLng = myLocation.longitude;
	}

//	public static double getMyLat() {
//		return myLat;
//	}
//
//	public static double getMyLng() {
//		return myLng;
//	}

	public static HashMap<String, Train> getTrainMap() {
		return trainsMap;
	}

	public static void setTrainMap(HashMap<String, Train> trainMap) {
		RailSingleton.trainsMap = trainMap;
	}

	public static ArrayList<TrainDetails> getTrainDetailsList() {
		return trainDetailsList;
	}

	public static void setTrainDetailsList(ArrayList<TrainDetails> trainDetailsList) {
		RailSingleton.trainDetailsList = trainDetailsList;
	}

	public static String getAsyncResult() {
		return asyncResult;
	}

	public static void setAsyncResult(String asyncResult) {
		RailSingleton.asyncResult = asyncResult;
	}
}
