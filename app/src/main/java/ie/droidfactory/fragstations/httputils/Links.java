package ie.droidfactory.fragstations.httputils;
/**
 * Created by kudlaty on 02/06/2016.
 */
public enum Links {
	LINK("http://api.irishrail.ie/realtime/realtime.asmx/"),
	ALL_STATIONS ("getAllStationsXML"),
	GET_ALL_STATION_BY_TYPE ("getAllStationsXM_WithStationType?StationType="),
	GET_CURRENT_TRAINS ("getCurrentTrainsXML"),
	GET_CURRENT_TRAIN_BY_TYPE ("getCurrentTrainsXML_WithTrainType?TrainType="),
	GET_STATION_DATA_BY_STATION_NAME ("getStationDataByNameXML?StationDesc="),
	GET_STATION_DATA_BY_STATION_NAME_TIME_RANGE
			("getStationDataByNameXML?StationDesc=_name_&_minutes_="),//minutes = 5-90
	GET_STATION_DATA_BY_STATION_CODE ("getStationDataByCodeXML?StationCode="),
	GET_STATION_DATA_BY_STATION_CODE_TIME_RANGE
			("getStationDataByCodeXML_WithNumMins?StationCode=mhide&NumMins="),

	GET_ALL_TRAINS ("getCurrentTrainsXML"),
	GET_TRAIN_DETAILS("getTrainMovementsXML?TrainId=");
	
	
	private String railLink;
	Links (String link){
		this.railLink=link;
	}
	public String getRailLink() {
		return railLink;
	}

	public String getTrainDetailsLink(String trainCode, String date){
		return railLink.concat(trainCode).concat("&TrainDate=").concat(date);
	}

}