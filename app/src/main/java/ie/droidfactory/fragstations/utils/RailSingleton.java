package ie.droidfactory.fragstations.utils;

import com.google.android.gms.maps.model.LatLng;

import java.util.HashMap;

import ie.droidfactory.fragstations.model.Station;
import ie.droidfactory.fragstations.model.StationDetails;
import ie.droidfactory.fragstations.model.Train;
import ie.droidfactory.fragstations.model.TrainDetails;

/**
 * Created by kudlaty on 02/06/2016.
 */
public class RailSingleton {

	private static HashMap<String, Station> stationMap=null;
	private static HashMap<String, Train> trainsMap=null;
	private static HashMap<Integer, TrainDetails> trainDetailsMap=null;
	private static HashMap<String,StationDetails> timetableMap=null;
	public static String currentStationCode="unknown";
	private static LatLng myLocation=null;
	private static int timeStampStationDetails;

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

	public static HashMap<String, StationDetails> getTimetable() {
		return timetableMap;
	}

	public static void setTimetable(HashMap<String,StationDetails> timetable) {
		RailSingleton.timetableMap = timetable;
	}
	
	public static void resetTimetable(){
		currentStationCode = "unknown";
		timetableMap = null;
	}

	public static LatLng getMyLocation() {
		return myLocation;
	}

	public static void setMyLocation(LatLng myLocation) {
		RailSingleton.myLocation = myLocation;
	}

	public static HashMap<String, Train> getTrainMap() {
		return trainsMap;
	}

	public static void setTrainMap(HashMap<String, Train> trainMap) {
		RailSingleton.trainsMap = trainMap;
	}
	public static HashMap<Integer, TrainDetails> getTrainDetailsMap() {
		return trainDetailsMap;
	}

	public static void setTrainDetailsMap(HashMap<Integer, TrainDetails> trainDetailsMap) {
		RailSingleton.trainDetailsMap = trainDetailsMap;
	}

	public static int getTimeStampStationDetails() {
		return timeStampStationDetails;
	}

	public static void setTimeStampStationDetails(int timeStampTrainDetails) {
		RailSingleton.timeStampStationDetails = timeStampTrainDetails;
	}

    public static void currentStationCode(String stationId) {
    }
}
