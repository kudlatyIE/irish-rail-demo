package ie.droidfactory.fragstations;

import android.support.v4.app.Fragment;

import ie.droidfactory.fragstations.model.RailInterface;

/**
 * Created by kudlaty on 02/06/2016.
 */
public class MainFragment extends Fragment implements RailInterface {

    @Override
    public void onStationSelected(String stationId) {
        // TODO Auto-generated method stub

    }
    @Override
    public void onTrainSelected(String trainId) {

    }
    RailInterface stationCallback, trainCallback;

    public void setStationSelectedListener(RailInterface listener){
        stationCallback = listener;
    }
    public void setTrainSelectedListener(RailInterface listener){
        trainCallback = listener;
    }

}
