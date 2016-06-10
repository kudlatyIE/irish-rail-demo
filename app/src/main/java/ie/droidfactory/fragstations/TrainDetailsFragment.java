package ie.droidfactory.fragstations;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import ie.droidfactory.fragstations.model.Train;
import ie.droidfactory.fragstations.utils.FragmentUtils;
import ie.droidfactory.fragstations.utils.RailSingleton;

/**
 * Created by kudlaty on 10/06/2016.
 */
public class TrainDetailsFragment extends Fragment {

    private final static String TAG = TrainDetailsFragment.class.getSimpleName();

    private String trainCode, trainName, trainDirection;
    private double lat, lng;
    private TextView tvInfo;
    private Train train;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_train_details, container, false);
        return v;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onViewCreated(view, savedInstanceState);
        tvInfo = (TextView) view.findViewById(R.id.fragment_train_details_info);
    }

    @Override
    public void onStart() {
        // TODO Auto-generated method stub
        super.onStart();
        Bundle extras = getArguments();

        if(extras!=null) {
            trainCode = extras.getString(FragmentUtils.STATION_CODE);
            lat = extras.getDouble(FragmentUtils.STATION_LAT);
            lng = extras.getDouble(FragmentUtils.STATION_LONG);
            trainDirection = extras.getString(FragmentUtils.TRAIN_DESCRIPTION);
            Log.d(TAG, "from bundle: trainCode: "+trainCode+", direction: "+trainDirection+"\nLAT:"
            +lat+" LNG:" + lng);
        }
        if(trainCode!= null) updateDetails(trainCode);
    }

    private void updateDetails(String id){
        Log.d(TAG, "updateDetails arg: "+id);
        this.train = RailSingleton.getTrainMap().get(id);
        Log.d(TAG, "train is NULL: "+(train==null));
        tvInfo.setText(TAG+" update details for: "+
                "\ntrainCode ID: "+trainCode+
                "\nLAT: "+train.getTrainLatitude()+" LNG: "+train.getTrainLongitude()+
                "\nDirection: "+train.getDirection());
    }

}
