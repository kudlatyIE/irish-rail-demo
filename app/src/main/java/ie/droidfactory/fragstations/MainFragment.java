package ie.droidfactory.fragstations;

import android.app.Activity;
import android.content.DialogInterface;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import ie.droidfactory.fragstations.model.RailInterface;

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
            Log.d(TAG, "handleBackPressed :::: fragments number: "+fm.getFragments().size());
            for(Fragment f: fm.getFragments()){
                if(f!=null && f.isVisible() && f instanceof MainFragment && f.getTag().equals(StationMainActivity.FRAG_MAIN)){
//                    if(((MainFragment)f).onBackPressed()) return true;
                    return true;
                }
            }
        }
        return false;
    }

    protected boolean onBackPressed(){
        FragmentManager fm = getChildFragmentManager();
        Log.d(TAG, "onBackPresed :::: back stack entry count: "+fm.getBackStackEntryCount());
        if(handleBackPressed(fm)) return true;
        else if(getUserVisibleHint() && fm.getBackStackEntryCount()>0){
            Log.d(TAG, "onBackPresed :::: entry count:"+fm.getBackStackEntryCount());
                fm.popBackStack();
                return true;
        }
        return false;
    }


}
