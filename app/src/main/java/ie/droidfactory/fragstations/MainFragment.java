package ie.droidfactory.fragstations;

import android.support.v4.app.Fragment;

import ie.droidfactory.fragstations.model.StationInterface;

/**
 * Created by kudlaty on 02/06/2016.
 */
public class MainFragment extends Fragment implements StationInterface {

    @Override
    public void onStationSelected(String stationId) {
        // TODO Auto-generated method stub

    }

    StationInterface stationCallback;
    public void setStationSelectedListener(StationInterface listener){
        stationCallback = listener;
    }

}
