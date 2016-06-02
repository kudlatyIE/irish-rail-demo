package ie.droidfactory.fragstations.model;
/**
 * Created by kudlaty on 02/06/2016.
 */
public class StationDetails {
	
	private String serverTime, trainCode, stationFullname, stationCode, queryTime, trainDate,
				origin, destination, originTime, destinationTime, status, lastLocation, dueIn, 
				late, expArrival, expDepart, schArrival, schDepart, direction, trainType, locationType;

	/**
	 * 
	 * @param serverTime - the time on the server
	 * @param trainCode - Unique Id for the train
	 * @param stationFullname - Long version of Station Name (identical in every record)
	 * @param stationCode - 4 to 5 letter station abbreviation
	 * @param queryTime - The time the query was made
	 * @param trainDate - The date the service started its journey ( some trains run over midnight)
	 * @param origin 
	 * @param destination
	 * @param originTime - The time the train left (or will leave) its origin
	 * @param destinationTime - the scheduled time at its destination
	 * @param status - Latest information on this service
	 * @param lastLocation - (Arrived|Departed StationName)
	 * @param dueIn - Num of minutes till the train will arrive here
	 * @param late - Minutes late
	 * @param expArrival - the trains expected arrival time at the query station updated as the train progresses ( note will show 00:00 for trains starting from query station)
	 * @param expDepart - the trains expected departure time at the query station updated as the train progresses ( note will show 00:00 for trains terminating at query station)
	 * @param schArrival - the scheduled arrival time ( note will show 00:00 for trains starting from query station)
	 * @param schDepart - the scheduled depart time ( note will show 00:00 for trains terminating at query station)
	 * @param direction - Northbound, Southbound or To Destination
	 * @param trainType - DART - Intercity etc
	 * @param locationType - O = Origin, D = Destination, S= Stop
	 */
	public StationDetails(String serverTime, String trainCode, String stationFullname,
			String stationCode, String queryTime, String trainDate,
			String origin, String destination, String originTime,
			String destinationTime, String status, String lastLocation,
			String dueIn, String late, String expArrival, String expDepart,
			String schArrival, String schDepart, String direction,
			String trainType, String locationType) {
		super();
		this.serverTime = serverTime;
		this.trainCode = trainCode;
		this.stationFullname = stationFullname;
		this.stationCode = stationCode;
		this.queryTime = queryTime;
		this.trainDate = trainDate;
		this.origin = origin;
		this.destination = destination;
		this.originTime = originTime;
		this.destinationTime = destinationTime;
		this.status = status;
		this.lastLocation = lastLocation;
		this.dueIn = dueIn;
		this.late = late;
		this.expArrival = expArrival;
		this.expDepart = expDepart;
		this.schArrival = schArrival;
		this.schDepart = schDepart;
		this.direction = direction;
		this.trainType = trainType;
		this.locationType = locationType;
	}

	public String getServerTime() {
		return serverTime;
	}

	public String getTrainCode() {
		return trainCode;
	}

	public String getStationFullname() {
		return stationFullname;
	}

	public String getStationCode() {
		return stationCode;
	}

	public String getQueryTime() {
		return queryTime;
	}

	public String getTrainDate() {
		return trainDate;
	}

	public String getOrigin() {
		return origin;
	}

	public String getDestination() {
		return destination;
	}

	public String getOriginTime() {
		return originTime;
	}

	public String getDestinationTime() {
		return destinationTime;
	}

	public String getStatus() {
		return status;
	}

	public String getLastLocation() {
		return lastLocation;
	}

	public String getDueIn() {
		return dueIn;
	}

	public String getLate() {
		return late;
	}

	public String getExpArrival() {
		return expArrival;
	}

	public String getExpDepart() {
		return expDepart;
	}

	public String getSchArrival() {
		return schArrival;
	}

	public String getSchDepart() {
		return schDepart;
	}

	public String getDirection() {
		return direction;
	}

	public String getTrainType() {
		return trainType;
	}

	public String getLocationType() {
		return locationType;
	}
	
	

}
