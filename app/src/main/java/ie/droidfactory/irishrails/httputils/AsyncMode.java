package ie.droidfactory.irishrails.httputils;

/**
 * Created by kudlaty on 10/06/2016.
 */
public enum AsyncMode {
    GET_ALL_TRAINS,
    GET_ALL_STATIONS,
    GET_TRAIN_DETAILS, //train route details
    GET_STATION_DETAIL, //timetable
    GET_STATION_INFO_DETAILS;//station details - parse html
}
