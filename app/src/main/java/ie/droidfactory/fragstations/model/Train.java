package ie.droidfactory.fragstations.model;

import android.util.Log;

import java.util.ArrayList;
import java.util.Comparator;

/**
 * Created by kudlaty on 09/06/2016.
 */
public class Train {

    private final static String TAG = Train.class.getSimpleName();

    private String trainStatus, trainCode, trainDate, message, direction, route, origin, destination;
    private double latitude, longitude;
    private String error="unknown error";


    private Train (String trainCode, String error){
        this.trainCode=trainCode;
        this.error=error;
    }

    /**
     * Train constructor to store trains in DB
     * @param trainCode
     * @param direction
     */
    private Train(String trainCode, String direction, ArrayList<Station>stopStations ) {

    }

    public Train(String trainStatus, String trainCode, String trainDate, String message, String direction,
                 double latitude, double longitude){
        this.trainStatus=trainStatus;
        this.trainCode=trainCode;
        this.trainDate=trainDate;
        this.message=message;
        this.direction=direction;
        this.route=getRoute(message);
        setOriginDestination(route);
        this.latitude=latitude;
        this.longitude=longitude;
    }

    private Train(String trainStatus, double trainLatitude, double trainLongitude, String trainCode,
                  String trainDate, String publicMessage, String direction) {
        this.trainStatus = trainStatus;
        this.latitude = trainLatitude;
        this.longitude = trainLongitude;
        this.trainCode = trainCode;
        this.trainDate = trainDate;
        this.message = publicMessage;
        this.direction = direction;
    }

    public static Train makeTrain(String trainStatus, double trainLatitude, double trainLongitude,
                                  String trainCode,
                                  String trainDate, String publicMessage, String direction){
        return new Train(trainStatus, trainLatitude, trainLongitude, trainCode, trainDate,
                publicMessage, direction);
    }


    public static Train makeTrain(String trainCode, String error){
        return new Train(trainCode, error);
    }

    public static class TrainDirectionCompareUp implements Comparator<Train> {
        @Override
        public int compare(Train t1, Train t2) {
            return t1.direction.compareToIgnoreCase(t2.direction);
        }
    }
    public static class TrainDirectionCompareDown implements Comparator<Train> {
        @Override
        public int compare(Train t1, Train t2) {
            return -1*t1.direction.compareToIgnoreCase(t2.direction);
        }
    }

    public static class TraonOriginCompareDown implements Comparator<Train>{
        @Override
        public int compare(Train t1, Train t2) {
            return t1.origin.compareToIgnoreCase(t2.origin);
        }
    }
    public static class TraonOriginCompareUp implements Comparator<Train>{
        @Override
        public int compare(Train t1, Train t2) {
            return -1*t1.origin.compareToIgnoreCase(t2.origin);
        }
    }
    public static class TraonDestinationCompareDown implements Comparator<Train>{
        @Override
        public int compare(Train t1, Train t2) {
            return t1.destination.compareToIgnoreCase(t2.destination);
        }
    }
    public static class TraonDestinationCompareUp implements Comparator<Train>{
        @Override
        public int compare(Train t1, Train t2) {
            return -1*t1.destination.compareToIgnoreCase(t2.destination);
        }
    }

    private String getRoute(String str)  {
        Log.d(TAG, "spliter: "+str);

        String[] split1 = str.split("\n");
        if(split1[1].contains("-")){
            return split1[1].substring(split1[1].indexOf("-")+1, split1[1].indexOf("(")).trim();
        }else return split1[1].trim();

    }

    private void setOriginDestination(String route){
        Log.d(TAG, "split route: "+route);
        String totalSplit[] = route.split(" to ");
        this.origin = totalSplit[0].trim();
        this.destination = totalSplit[1].trim();
    }


    public String getTrainStatus() {
        return trainStatus;
    }

    public String getTrainCode() {
        return trainCode;
    }

    public String getTrainDate() {
        return trainDate;
    }

    public String getMessage() {
        return message;
    }

    public String getDirection() {
        return direction;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public String getRoute() {
        return route;
    }

    public String getOrigin() {
        return origin;
    }

    public String getDestination() {
        return destination;
    }
}
