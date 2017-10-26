package ie.droidfactory.irishrails;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import ie.droidfactory.irishrails.httputils.AsyncMode;
import ie.droidfactory.irishrails.httputils.AsyncStationsList;
import ie.droidfactory.irishrails.model.Cities;
import ie.droidfactory.irishrails.model.Station;
import ie.droidfactory.irishrails.utils.FragmentUtils;
import ie.droidfactory.irishrails.utils.RailSingleton;

/**
 * Created by kudlaty on 02/06/2016.
 */
public class StationDetailsOtherFragment extends Fragment {

    private final static String TAG = StationDetailsOtherFragment.class.getSimpleName();
    //	private int mCurrentPosition = -1;
    private String stationId;
    private int childPosition = -1;
    private TextView tvInfoGeneral, tvInfoParking, tvInfoAccessibility;
    private Station station;
    private String detailsGeneral, detailsParking, detailsAccess;

    private AsyncStationsList.AsyncDoneCallback asyncDone = new AsyncStationsList.AsyncDoneCallback() {
        @Override
        public void onAsyncDone(boolean done) {
            if(done){
                tvInfoGeneral.setText(RailSingleton.getWebStationInfo());
            }

        }
    };

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
        tvInfoGeneral = (TextView) view.findViewById(R.id.fragment_details_other_text_info_general);
        tvInfoParking = (TextView) view.findViewById(R.id.fragment_details_other_text_info_parking_transport);
        tvInfoAccessibility = (TextView) view.findViewById(R.id.fragment_details_other_text_info_accessibility);
    }


    @Override
    public void onStart() {
        // TODO Auto-generated method stub
        super.onStart();
        Bundle extras = getArguments();

        if(extras!=null) {
            stationId = extras.getString(FragmentUtils.STATION_CODE);
            Log.d(TAG, "extras station CODE: "+stationId);
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
        Log.d(TAG, "get url: "+Cities.valueOf("_".concat(station.getStationId())).getDetailsUrl());
        AsyncStationsList rail = new AsyncStationsList(getActivity(),
                AsyncMode.GET_STATION_INFO_DETAILS, asyncDone);
        rail.execute(Cities.valueOf("_".concat(station.getStationId())).getDetailsUrl());

//        tvInfo.setText(TAG+" update details for: "+
//                "\nchild ID: "+childPosition+
//                "\nAlias: "+station.getStationAlias()+" ID: "+station.getStationCode()+
//                "\nDetails: "+station.getStationDesc());
    }


    public void setStationDetails(String id){
        stationId = id;
        updateDetails(id);
    }

}
