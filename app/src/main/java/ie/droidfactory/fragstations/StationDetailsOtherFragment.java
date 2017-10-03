package ie.droidfactory.fragstations;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import ie.droidfactory.fragstations.model.Station;
import ie.droidfactory.fragstations.utils.FragmentUtils;
import ie.droidfactory.fragstations.utils.RailSingleton;

/**
 * Created by kudlaty on 02/06/2016.
 */
public class StationDetailsOtherFragment extends Fragment {

    private final static String TAG = StationDetailsOtherFragment.class.getSimpleName();
    //	private int mCurrentPosition = -1;
    private String stationId=null;
    private int childPosition = -1;
    private TextView tvInfo;
    private Station station;

    public static StationDetailsOtherFragment newInstance(Bundle args){
        StationDetailsOtherFragment fragment = new StationDetailsOtherFragment();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_station_details_other, container, false);
        return v;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onViewCreated(view, savedInstanceState);
        tvInfo = (TextView) view.findViewById(R.id.fragment_details_other_text_info);
    }


    @Override
    public void onStart() {
        // TODO Auto-generated method stub
        super.onStart();
        Bundle extras = getArguments();

        if(extras!=null) {
            stationId = extras.getString(FragmentUtils.PARENT_POSITION_KEY);
            childPosition = extras.getInt(FragmentUtils.CHILD_POSITION_KEY);
        }
        if(stationId!= null) updateDetails(stationId);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onActivityCreated(savedInstanceState);
        Log.d(TAG, "onActivityCreated....");
    }

    private void updateDetails(String id){
        this.station = RailSingleton.getStationMap().get(id);
        tvInfo.setText(TAG+" update details for: "+
                "\nchild ID: "+childPosition+
                "\nAlias: "+station.getStationAlias()+" ID: "+station.getStationCode()+
                "\nDetails: "+station.getStationDesc());
    }


    public void setStationDetails(String id){
        stationId = id;
        updateDetails(id);
    }

}
