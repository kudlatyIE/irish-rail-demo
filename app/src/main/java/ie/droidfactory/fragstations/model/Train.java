package ie.droidfactory.fragstations.model;

/**
 * Created by kudlaty on 09/06/2016.
 */
public class Train {

    private String trainStatus, trainCode, trainDate,
            publicMessage, direction;
    private double trainLatitude, trainLongitude;
    private String error="unknown error";


    private Train (String trainCode, String error){
        this.trainCode=trainCode;
        this.error=error;
    }
    private Train(String trainStatus, double trainLatitude, double trainLongitude, String trainCode,
                 String trainDate, String publicMessage, String direction) {
        this.trainStatus = trainStatus;
        this.trainLatitude = trainLatitude;
        this.trainLongitude = trainLongitude;
        this.trainCode = trainCode;
        this.trainDate = trainDate;
        this.publicMessage = publicMessage;
        this.direction = direction;
    }
    public static Train makeTrain(String trainCode, String error){
        return new Train(trainCode, error);
    }


    public static Train makeTrain(String trainStatus, double trainLatitude, double trainLongitude,
                                  String trainCode,
                                  String trainDate, String publicMessage, String direction){
        return new Train(trainStatus, trainLatitude, trainLongitude, trainCode, trainDate,
                publicMessage, direction);
    }

    public String getTrainStatus() {
        return trainStatus;
    }

    public void setTrainStatus(String trainStatus) {
        this.trainStatus = trainStatus;
    }

    public double getTrainLatitude() {
        return trainLatitude;
    }

    public void setTrainLatitude(double trainLatitude) {
        this.trainLatitude = trainLatitude;
    }

    public double getTrainLongitude() {
        return trainLongitude;
    }

    public void setTrainLongitude(double trainLongitude) {
        this.trainLongitude = trainLongitude;
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

    public String getPublicMessage() {
        return publicMessage;
    }

    public void setPublicMessage(String publicMessage) {
        this.publicMessage = publicMessage;
    }

    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }

    public String getError() {
        return error;
    }
}
