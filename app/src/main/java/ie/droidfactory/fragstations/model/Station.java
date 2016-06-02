package ie.droidfactory.fragstations.model;

/**
 * Created by kudlaty on 02/06/2016.
 */
public class Station {
	private String stationDesc, stationCode, stationId, stationAlias;
	private double stationLatitude, stationLongitude;
	private String stationType;
	private String error="unknown error";
	
	
	private Station (String id, String error){
		this.stationId=id;
		this.error=error;
	}
	private Station(String name, String code, String id, String alias, double latitude, double longitude, String type){
		this.stationDesc=name;
		this.stationCode=code;
		this.stationId=id;
		this.stationAlias=alias;
		this.stationLatitude=latitude;
		this.stationLongitude=longitude;
		this.stationType=type;
		
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
	public static Station makeStation(String name, String code, String id, String alias, double latitude, double longitude, String type){
		return new Station(name, code, id, alias, latitude, longitude, type);
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

	public void setStationType(String stationType) {
		this.stationType = stationType;
	}
	
	public String toString(){
		try{
			return stationDesc+" ID: "+stationId+", type:"+stationType;
		}catch(Exception e){
			return error;
		}
	}
	
	
}
