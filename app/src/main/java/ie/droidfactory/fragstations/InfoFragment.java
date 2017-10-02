package ie.droidfactory.fragstations;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

import ie.droidfactory.fragstations.httputils.AsyncMode;
import ie.droidfactory.fragstations.httputils.AsyncStationsList;
import ie.droidfactory.fragstations.httputils.Links;
import ie.droidfactory.fragstations.model.RailInterface;
import ie.droidfactory.fragstations.utils.FragmentUtils;
import ie.droidfactory.fragstations.utils.LocationUtils;
import ie.droidfactory.fragstations.utils.MyLocationListener;
import ie.droidfactory.fragstations.utils.PermissionUtils;
import ie.droidfactory.fragstations.utils.RailSingleton;

/**
 * Created by kudlaty on 06/06/2016.
 * display info about internet connection here and about a new updates, others... bla, bla
 */
public class InfoFragment extends MainFragment {

    private final static String TAG = InfoFragment.class.getSimpleName();
    private String info = "default nothing";
    private TextView tvInfo;

    private AsyncStationsList.AsyncDoneCallback asyncStationsListDone = new AsyncStationsList
            .AsyncDoneCallback() {
        @Override
        public void onAsyncDone(boolean done) {
            Log.d(TAG, "async success: "+done);
            Log.d(TAG, "async result: "+ RailSingleton.getAsyncResult());

            if(done){
                //TODO: download NEWS from twitter
            }
        }
    };

    private AsyncStationsList.AsyncDoneCallback asyncNewsDone = new AsyncStationsList
            .AsyncDoneCallback() {
        @Override
        public void onAsyncDone(boolean done) {
            Log.d(TAG, "async success: "+done);
            Log.d(TAG, "async result: "+ RailSingleton.getAsyncResult());

            if(done){
                //TODO: download NEWS from twitter
            }
        }
    };


    RailInterface stationCallback;
    public void setStationSelectedListener(RailInterface listener){
        stationCallback = listener;
    }

    public static InfoFragment newInstance(Bundle args) {
        InfoFragment fragment = new InfoFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public  void onStart() {
        // TODO Auto-generated method stub
        super.onStart();
        Log.d(TAG, "onStart....");

        Bundle extras = getArguments();
        if (extras != null) {
            info = extras.getString(FragmentUtils.FRAGMENT_INFO);
        }
        Log.d(TAG, "onStart....");
        tvInfo.setText(info);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_info, container, false);
        tvInfo = (TextView) v.findViewById(R.id.fragment_info_text_info);
        return v;
    }
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d(TAG, "onViewCreated....");

    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onActivityCreated(savedInstanceState);
        getLocation();
        downloadAllStationList();

    }

    @Override
    public void onAttach(Activity activity) {
        // TODO Auto-generated method stub
        super.onAttach(activity);
        try{
            stationCallback = (RailInterface) activity;
        }catch(ClassCastException e){
            throw new ClassCastException(activity.toString()+ "OnStationSelected Listener is not " +
                    "implemented...");
        }
    }

    private void downloadAllStationList(){
        LocationUtils.getLocation(getActivity());
        AsyncStationsList rail = new AsyncStationsList(getActivity(), AsyncMode.GET_ALL_STATIONS, asyncStationsListDone);
        rail.execute(Links.ALL_STATIONS.getRailLink());
    }



    private void getLocation(){
        Location l= null;
        LocationManager lm = null;
        MyLocationListener listener = new MyLocationListener();

        try{
            lm = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
            if(lm.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, listener);
                l = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            }
            if(lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, listener);
                l = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            }
            lm.removeUpdates(listener);
            if(l!=null){

                RailSingleton.setMyLocation(new LatLng(l.getLatitude(), l.getLongitude()));
            }
        }catch (NullPointerException e){
            e.printStackTrace();
        }
    }


    private void updateDetails(String info){
        this.info =info;
        tvInfo.setText(info);
    }

}
