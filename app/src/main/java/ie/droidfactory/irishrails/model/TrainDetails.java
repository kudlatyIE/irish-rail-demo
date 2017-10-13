package ie.droidfactory.irishrails.model;

import java.util.Comparator;

/**
 * Created by kudlaty on 09/06/2016.
 */
public class TrainDetails {
    private Integer locationOrder;
    private String trainCode, trainDate, locationCode, locationFullName,
            locationType, trainOrigin, trainDestination, scheduledArrival, scheduledDeparture,
            expectedArrival, expectedDeparture, arrival, departure, autoArrival, autoDepart,
            stopType;
    private String error="unknown error";

    public TrainDetails(String trainCode, String error){
        this.trainCode=trainCode;
        this.error=error;
    }

    /**
     *
     * @param trainCode
     * @param trainDate
     * @param locationCode current/last location code
     * @param locationFullName current/last location full name
     * @param locationOrder from first =1 to destination
     * @param locationType LocationType O= Origin, S= Stop, T= TimingPoint (non stopping location) D = Destination
     * @param trainOrigin
     * @param trainDestination
     * @param scheduledArrival
     * @param scheduledDeparture
     * @param expectedArrival
     * @param expectedDeparture
     * @param arrival actual
     * @param departure actual
     * @param autoArrival was information automatically generated
     * @param autoDepart
     * @param stopType C= Current N = Next
     */
    public TrainDetails(String trainCode, String trainDate, String locationCode, String locationFullName,
                        Integer locationOrder, String locationType, String trainOrigin,
                        String trainDestination, String scheduledArrival, String scheduledDeparture,
                        String expectedArrival, String expectedDeparture,
                        String arrival, String departure, String autoArrival, String autoDepart, String stopType) {
        this.trainCode = trainCode;
        this.trainDate = trainDate;
        this.locationCode = locationCode;
        this.locationFullName = locationFullName;
        this.locationOrder = locationOrder;
        this.locationType = locationType;
        this.trainOrigin = trainOrigin;
        this.trainDestination = trainDestination;
        this.scheduledArrival = scheduledArrival;
        this.scheduledDeparture = scheduledDeparture;
        this.expectedArrival=expectedArrival;
        this.expectedDeparture=expectedDeparture;
        this.arrival = arrival;
        this.departure = departure;
        this.autoArrival = autoArrival;
        this.autoDepart = autoDepart;
        this.stopType = stopType;
    }
    public static TrainDetails makeTrainDetails(String trainCode, String error){
        return new TrainDetails(trainCode, error);
    }

    @Override
    public String toString() {
        return "TrainDetails{" +
                "trainCode='" + trainCode + '\'' +
                ", trainDate='" + trainDate + '\'' +
                ", locationCode='" + locationCode + '\'' +
                ", locationFullName='" + locationFullName + '\'' +
                ", locationOrder='" + locationOrder + '\'' +
                ", locationType='" + locationType + '\'' +
                ", trainOrigin='" + trainOrigin + '\'' +
                ", trainDestination='" + trainDestination + '\'' +
                ", scheduledArrival='" + scheduledArrival + '\'' +
                ", scheduledDeparture='" + scheduledDeparture + '\'' +
                ", arrival='" + arrival + '\'' +
                ", departure='" + departure + '\'' +
                ", autoArrival='" + autoArrival + '\'' +
                ", autoDepart='" + autoDepart + '\'' +
                ", stopType='" + stopType + '\'' +
                ", error='" + error + '\'' +
                '}';
    }

    public static TrainDetails makeTrainDetails(String trainCode, String trainDate, String locationCode, String locationFullName,
                  Integer locationOrder, String locationType, String trainOrigin, String
                                                        trainDestination, String scheduledArrival,
                  String scheduledDeparture, String expectedArrival, String expectedDeparture,
                  String arrival, String departure, String autoArrival, String autoDepart, String stopType){

        return new TrainDetails(trainCode, trainDate, locationCode, locationFullName, locationOrder,
                locationType, trainOrigin, trainDestination, scheduledArrival, scheduledDeparture,
                expectedArrival, expectedDeparture,arrival, departure, autoArrival, autoDepart, stopType);
    }

    public static class TranLocationOrderCompareUp implements Comparator<TrainDetails>{
        @Override
        public int compare(TrainDetails t1, TrainDetails t2) {
            if(t1.locationOrder>t2.locationOrder) return 1;
            if(t1.locationOrder<t2.locationOrder) return -1;
            return 0;
        }
    }

    public String getTrainCode() {
        return trainCode;
    }

    public void setTrainCode(String trainCode) {
        this.trainCode = trainCode;
    }

    public String getTrainDate() {
        return trainDate;
    }

    public void setTrainDate(String trainDate) {
        this.trainDate = trainDate;
    }

    public String getLocationCode() {
        return locationCode;
    }

    public void setLocationCode(String locationCode) {
        this.locationCode = locationCode;
    }

    public String getLocationFullName() {
        return locationFullName;
    }

    public void setLocationFullName(String locationFullName) {
        this.locationFullName = locationFullName;
    }

    public Integer getLocationOrder() {
        return locationOrder;
    }

    public void setLocationOrder(Integer locationOrder) {
        this.locationOrder = locationOrder;
    }

    public String getLocationType() {
        return locationType;
    }

    public void setLocationType(String locationType) {
        this.locationType = locationType;
    }

    public String getTrainOrigin() {
        return trainOrigin;
    }

    public void setTrainOrigin(String trainOrigin) {
        this.trainOrigin = trainOrigin;
    }

    public String getTrainDestination() {
        return trainDestination;
    }

    public void setTrainDestination(String trainDestination) {
        this.trainDestination = trainDestination;
    }

    public String getScheduledArrival() {
        return scheduledArrival;
    }

    public void setScheduledArrival(String scheduledArrival) {
        this.scheduledArrival = scheduledArrival;
    }

    public String getScheduledDeparture() {
        return scheduledDeparture;
    }

    public void setScheduledDeparture(String scheduledDeparture) {
        this.scheduledDeparture = scheduledDeparture;
    }

    public String getArrival() {
        return arrival;
    }

    public void setArrival(String arrival) {
        this.arrival = arrival;
    }

    public String getDeparture() {
        return departure;
    }

    public void setDeparture(String departure) {
        this.departure = departure;
    }

    public String getAutoArrival() {
        return autoArrival;
    }

    public void setAutoArrival(String autoArrival) {
        this.autoArrival = autoArrival;
    }

    public String getAutoDepart() {
        return autoDepart;
    }

    public void setAutoDepart(String autoDepart) {
        this.autoDepart = autoDepart;
    }

    public String getStopType() {
        return stopType;
    }

    public void setStopType(String stopType) {
        this.stopType = stopType;
    }

    public String getError() {
        return error;
    }

    public String getExpectedArrival() {
        return expectedArrival;
    }

    public void setExpectedArrival(String expectedArrival) {
        this.expectedArrival = expectedArrival;
    }

    public String getExpectedDeparture() {
        return expectedDeparture;
    }

    public void setExpectedDeparture(String expectedDeparture) {
        this.expectedDeparture = expectedDeparture;
    }
}
