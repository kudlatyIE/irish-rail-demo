package ie.droidfactory.irishrails.utils;

import android.location.Location;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.HashMap;

import ie.droidfactory.irishrails.model.Station;
import ie.droidfactory.irishrails.model.StationDetails;
import ie.droidfactory.irishrails.model.StationDetailsInfo;
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
	private static String webStationInfo;
	private static HashMap<String, ArrayList<StationDetailsInfo>> infoMap;


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
		Location loc = new Location("");
		loc.setLatitude(myLocation.latitude);
		loc.setLongitude(myLocation.longitude);
		RailSingleton.myLocation = loc;
	}

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

	public static String getWebStationInfo() {
		return webStationInfo;
	}

	public static void setWebStationInfo(String webStationInfo) {
		RailSingleton.webStationInfo = webStationInfo;
	}

	public static HashMap<String, ArrayList<StationDetailsInfo>> getInfoMap() {
		return infoMap;
	}

	public static void setInfoMap(HashMap<String, ArrayList<StationDetailsInfo>> infoMap) {
		RailSingleton.infoMap = infoMap;
	}

	public static String getAsyncResult() {
		return asyncResult;
	}

	public static void setAsyncResult(String asyncResult) {
		RailSingleton.asyncResult = asyncResult;
	}
}
