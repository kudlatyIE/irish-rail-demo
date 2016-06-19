package ie.droidfactory.fragstations;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

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

    @Override
    public void onStationSelectedFromTrain(String stationId) {
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

    public static boolean handleBackPressed(FragmentManager fm){
        if(fm.getFragments()!=null){
            for(Fragment f: fm.getFragments()){
                if(f!=null && f.isVisible() && f instanceof MainFragment){
                    if(((MainFragment)f).onBackPressed()) return true;
                }
            }
        }
        return false;
    }
    protected boolean onBackPressed(){
        FragmentManager fm = getChildFragmentManager();
        if(handleBackPressed(fm)) return true;
        else if(getUserVisibleHint() && fm.getBackStackEntryCount()>0){
            fm.popBackStack();
            return true;
        }
        return false;
    }

}
