package ie.droidfactory.irishrails.model;

/**
 * Created by kudlaty on 2017-10-26.
 */

public class StationDetailsInfo {

    private String infoHeader, infoDetails;

    public StationDetailsInfo(String infoHeader, String infoDetails) {
        this.infoHeader = infoHeader;
        this.infoDetails = infoDetails;
    }

    public String getInfoHeader() {
        return infoHeader;
    }

    public void setInfoHeader(String infoHeader) {
        this.infoHeader = infoHeader;
    }

    public String getInfoDetails() {
        return infoDetails;
    }

    public void setInfoDetails(String infoDetails) {
        this.infoDetails = infoDetails;
    }
}
