package ie.droidfactory.irishrails.httputils;

/**
 * Created by kudlaty on 09/06/2016.
 */
public class XmlKeyHolder {
    public final String ARRAY_STATION_DATA = "ArrayOfObjStationData",
            OBJECT_STATION_DATA="objStationData",
            ARRAY_TRAIN_POSITION="ArrayOfObjTrainPositions", OBJECT_TRAIN_POSITION="objTrainPositions",
            ARRAY_STATIONS="ArrayOfObjStation", OBJECT_STATION="objStation",
            OBJECT_TRAIN_MOVEMENTS="objTrainMovements";
    //Get All Stations with Type
    public final String STATION_DESC="StationDesc",// STATION_CODE="StaionCode",
            STATION_ID="StationId", STATION_ALIAS="StationAlias",
            STATION_LATITUDE="StationLatitude", STATION_LONGITUDE="StationLongitude";
    //Get Station Data by StationCode with number of minutes usage
    public final String SERVER_TIME="Servertime",STATION_TRAIN_CODE="Traincode",
            STATION_FULL_NAME="Stationfullname", STATION_CODE="StationCode",
            QUERY_TIME="Querytime",STATION_TRAIN_DATE="Traindate", ORIGIN="Origin",
            DESTINATION="Destination", ORIGIN_TIME="Origintime",
            DESTINATION_TIME="Destinationtime", STATION_TRAIN_STATUS="Status",
            LAST_LOCATION="Lastlocation",DUE_IN="Duein",LATE="Late",
            EXP_ARRIVAL="Exparrival", EXP_DEPART="Expdepart", SCHEDULE_ARRIVAL="Scharrival", SCHEDULE_DEPART="Schdepart",
            DIRECTION="Direction", TRAIN_TYPE="Traintype", LOCATION_TYPE="Locationtype", STATION_CODE_DETAILS="Stationcode";
    //Get Current Trains with Type
    public final String TYPE_TRAIN_LATITUDE="TrainLatitude",
            TYPE_TRAIN_LONGITUDE="TrainLongitude", TYPE_PUBLIC_MESSAGE="PublicMessage";

    //Get all trains (R)unning and (N)ot running yet - for current day
    public final String TR_TRAIN_STATUS = "TrainStatus", TR_TRAIN_LATITUDE="TrainLatitude",
            TR_TRAIN_LONGITUDE="TrainLongitude", TR_TRAIN_CODE="TrainCode",
            TR_TRAIN_DATE="TrainDate", TR_PUBLIC_MSG="PublicMessage", TR_DIRECTION="Direction";

    //selected train movement details
    public final String TD_TRAIN_CODE="TrainCode", TD_TRAIN_DATE="TrainDate",
            TD_LOCATION_CODE="LocationCode", TD_LOCATION_FULL_NAME="LocationFullName",
            TD_LOCATION_ORDER="LocationOrder", TD_LOCATION_TYPE="LocationType",
            TD_TRAIN_ORIGIN="TrainOrigin", TD_TRAIN_DESTINATION="TrainDestination",
            TD_SCHEUDLE_ARRIVAL="ScheduledArrival", TD_SCHEUDLE_DEPARTURE="ScheduledDeparture",
            TD_EXPECTED_ARRIVAL="ExpectedArrival", TD_EXPECTED_DEPARTURE="ExpectedDeparture",
            TD_ARRIVAL="Arrival", TD_DEPARTURE="Departure", TD_AUTOARRIVAL="AutoArrival",
            TD_AUTODEPART="AutoDepart", TD_STOP_TYPE="StopType";

    public final String OBJECT_ERROR="error", ERR_CODE="ErrorCode", ERR_MESSAGE="ErrorMessage";
    public final String errNoData="no data from ire rail server";

    public final String LOCATION_ORIGIN = "O", LOCATION_STOP = "S", LOCATION_TIMMINGPOINT = "T",
                        LOCATION_DESTINATION = "D";

    public final String EMPTY = "";
}
