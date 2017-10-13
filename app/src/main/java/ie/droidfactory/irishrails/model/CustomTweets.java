package ie.droidfactory.irishrails.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by kudlaty on 2017-10-13.
 */

public class CustomTweets implements Parcelable {

    private String date, message;

    public CustomTweets(String date, String msg){
        this.date=date;
        this.message=msg;
    }

    protected CustomTweets(Parcel in) {
        this.date = in.readString();
        this.message = in.readString();
    }

    public static final Creator<CustomTweets> CREATOR = new Creator<CustomTweets>() {
        @Override
        public CustomTweets createFromParcel(Parcel in) {
            return new CustomTweets(in);
        }

        @Override
        public CustomTweets[] newArray(int size) {
            return new CustomTweets[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(date);
        parcel.writeString(message);
    }

    public String getDate() {
        return date;
    }

    public String getMessage() {
        return message;
    }
}
