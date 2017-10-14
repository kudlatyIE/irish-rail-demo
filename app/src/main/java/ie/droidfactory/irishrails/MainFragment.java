package ie.droidfactory.irishrails;

import android.support.v4.app.Fragment;

import ie.droidfactory.irishrails.model.RailInterface;

/**
 * Created by kudlaty on 02/06/2016.
 */
public class MainFragment extends Fragment implements RailInterface {

    private final static String TAG = MainFragment.class.getSimpleName();

    @Override
    public void onStationSelected(String stationId) {
        // TODO Auto-generated method stub
    }
    @Override
    public void onTrainSelected(String trainId) {
    }

    @Override
    public void onTweetSelected(String tweetUrl) {

    }


    RailInterface stationCallback, trainCallback, stationFromTrainCallback;

    public void setStationSelectedListener(RailInterface listener){
        stationCallback = listener;
    }
    public void setTrainSelectedListener(RailInterface listener){
        trainCallback = listener;
    }
    public void setStationFromTrainListener(RailInterface listener){ stationFromTrainCallback =
            listener;}


}
