package ie.droidfactory.irishrails.model;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.util.Log;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import ie.droidfactory.irishrails.utils.RailSingleton;

/**
 * Created by kudlaty on 02/06/2016.
 */
public class Station implements Serializable {

	private final static String TAG = Station.class.getSimpleName();

	private String stationDesc, stationCode, stationId, stationAlias, city;
	private double stationLatitude, stationLongitude, distance, myLat, myLng;
	private Location stLocation;
	private String stationType;
	private String error="unknown error";
	private static Location myLocation = null;


	private Station (String id, String error){
		this.stationId=id;
		this.error=error;
	}
	private Station(Context ac, String name, String code, String id, String alias, double latitude, double longitude, String type){
		if(name.toLowerCase().contains(Cities.D.toLowerCase())) this.stationDesc=name;
		else {
			if(Cities.valueOf("_"+id).getCity().length()==0) stationDesc=name;
			else this.stationDesc=Cities.valueOf("_"+id).getCity().concat(" ").concat(name);
		}
		this.stationCode=code;
		this.stationId=id;
		this.stationAlias=alias;
		this.stationLatitude=latitude;
		this.stationLongitude=longitude;
		this.stationType=type;
		this.distance = RailSingleton.getMyLocation().distanceTo(calcLocation(latitude, longitude));
//		Log.d(TAG, name+"\t"+distance);

	}

	private Location calcLocation(double lat, double lng){
		Location location = new Location("");
		location.setLatitude(lat);
		location.setLongitude(lng);
		return location;
	}

	public static class StationCompare implements Comparator<Station> {

		private List<Comparator<Station>> compareList = new ArrayList<Comparator<Station>>();
		private int result =0;
		public void add(int i, Comparator<Station> arg){
			compareList.add(1,arg);
		}

		@Override
		public int compare(Station o1, Station o2) {
			for (int i=0; i<compareList.size();i++){
				result = compareList.get(i).compare(o1, o2);
				if (result!=0) return result;
			}
			return 0;
		}
	}

	/**
	 * sorting A-Z
	 */
	public static class stationNameUp implements Comparator<Station>{

		@Override
		public int compare(Station o1, Station o2) {
			return o1.stationDesc.compareToIgnoreCase(o2.stationDesc);
		}
	}

	/**
	 * sorting Z-A
	 */
	public static class stationNameDown implements Comparator<Station>{

		@Override
		public int compare(Station o1, Station o2) {
			return -1*(o1.stationDesc.compareToIgnoreCase(o2.stationDesc));
		}
	}
	public static class city implements Comparator<Station>{

		@Override
		public int compare(Station o1, Station o2) {
			if (o1.city!=null & o2.city!=null) return o1.city.compareToIgnoreCase(o2.city);
			else return 0;
		}
	}

	public static class distanceDown implements Comparator<Station>{

		@Override
		public int compare(Station o1, Station o2) {
//            if (o1.distance!=0 & o2.distance!=0) {
			if(o1.getDistance()>o2.getDistance()) return 1;
			if(o1.getDistance()<o2.getDistance()) return -1;
//            }
			return 0;
		}
	}
	public static class distanceUp implements Comparator<Station>{

		@Override
		public int compare(Station o1, Station o2) {
//            if (o1.distance!=0 & o2.distance!=0) {
			if(o1.getDistance()>o2.getDistance()) return -1;
			if(o1.getDistance()<o2.getDistance()) return 1;

//            }
			return 0;
		}
	}
	/**
	 *
	 * @param name String station name
	 * @param code String station code
	 * @param id String station ID
	 * @param alias String
	 * @param latitude double latitude param
	 * @param longitude double longitude param
	 * @param type station type: M for Mainline, S for suburban and D for DART
	 * @return new Station object
	 */
	public static Station makeStation(Context ac, String name, String code, String id, String alias, double latitude, double longitude, String type){
		return new Station(ac, name, code, id, alias, latitude, longitude, type);
	}

	/**
	 * return non existing station with message
	 * @param id null for not existing station
	 * @param error cause of issue
	 */
	public static Station makeStation(String id, String error){
		return new Station(id,error);
	}

	public String getStationDesc() {
		return stationDesc;
	}

	public String getStationCode() {
		return stationCode;
	}

	public String getStationId() {
		return stationId;
	}

	public String getStationAlias() {
		return stationAlias;
	}

	public double getStationLatitude() {
		return stationLatitude;
	}

	public double getStationLongitude() {
		return stationLongitude;
	}

	public String getStationType() {
		return stationType;
	}

	public String getError() {
		return error;
	}
	public void setStationDesc(String stationDesc) {
		this.stationDesc = stationDesc;
	}

	public void setStationCode(String stationCode) {
		this.stationCode = stationCode;
	}

	public void setStationId(String stationId) {
		this.stationId = stationId;
	}

	public void setStationAlias(String stationAlias) {
		this.stationAlias = stationAlias;
	}

	public void setStationLatitude(double stationLatitude) {
		this.stationLatitude = stationLatitude;
	}

	public void setStationLongitude(double stationLongitude) {
		this.stationLongitude = stationLongitude;
	}

	public Location getStLocation() {
		return stLocation;
	}

	public void setStationType(String stationType) {
		this.stationType = stationType;
	}

//    public String getCity() {
//        return city;
//    }

	public double getDistance() {
		return distance;
	}

	public void setDistance(double distance) {
		this.distance = distance;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public static Location getMyLocation() {
		return myLocation;
	}

	public static void setMyLocation(Location myLocation) {
		Station.myLocation = myLocation;
	}

	public String toString(){
		try{
//            return stationNameDown+" ID: "+stationId+", type:"+stationType;
			return stationDesc+"\t"+stationId+"\t"+stationType+"\t"+distance;
		}catch(Exception e){
			return error;
		}
	}

	//not used, now only Dublin city is added from enum Cities
	private String findCity(Context ac, String stationDesc, double lat, double lng) {
		Geocoder geoCoder = new Geocoder(ac, Locale.getDefault());
		String city;
		try {
			List<Address> addresses = geoCoder.getFromLocation(lat, lng, 1);
			if (addresses!=null && addresses.size() > 0) {
				city = addresses.get(0).getLocality();
				if(city!=null) return city;
			}
		} catch (IOException e) {
		}
		return stationDesc;
	}

}
