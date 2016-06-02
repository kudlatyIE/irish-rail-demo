package ie.droidfactory.fragstations;

import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import ie.droidfactory.fragstations.model.Station;
import ie.droidfactory.fragstations.utils.FragmentUtils;
import ie.droidfactory.fragstations.utils.MyLocationListener;
import ie.droidfactory.fragstations.utils.RailSingleton;

/**
 * Created by kudlaty on 02/06/2016.
 */
public class StationDetailsMapaFragment extends Fragment {//implements OnMapReadyCallback {

    //TODO: default mam position is my current or last selected station!

    private final static String TAG = StationDetailsMapaFragment.class.getSimpleName();
    private final static String TAG_MAP="fragment_map";
    private SupportMapFragment mapFragment;
    private double myLo=0, myLat=0;
    private double stationLo = 0, stationLat = 0;
    private LatLng myLocation=null, stationLocation=null;
    //	private int mCurrentPosition = -1;
    private String stationId=null;
    private int childPosition = -1;
    private TextView tvInfo;
    private Station station;
    private GoogleMap map;


    public static StationDetailsMapaFragment newInstance(Bundle args){
        StationDetailsMapaFragment fragment = new StationDetailsMapaFragment();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_station_details_mapa, container, false);
        return v;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onViewCreated(view, savedInstanceState);
        tvInfo = (TextView) view.findViewById(R.id.fragment_details_mapa_text_info);

        mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentByTag(TAG_MAP);
        if(mapFragment==null){
            Log.d(TAG, "map NULL - TEST!");
            FragmentManager fm = getFragmentManager();
            mapFragment = SupportMapFragment.newInstance();
            fm.beginTransaction().replace(R.id.fragment_details_map_mapa, mapFragment).commit();
            mapFragment.getMapAsync(new OnMapReadyCallback(){

                @Override
                public void onMapReady(GoogleMap arg0) {
                    // TODO Auto-generated method stub
                    Log.d(TAG, "onMapReady callback - TEST!");
                    if(arg0!=null){
                        map = arg0;
                        setMap();
                    }
                }
            });
        }else Log.d(TAG, "map exist - TEST!");
    }

    private void setMap(){
        if(stationLocation==null){
            getLocation();
            this.stationLocation = this.myLocation;
        }
        map.getUiSettings().setAllGesturesEnabled(true);
        map.addMarker(new MarkerOptions().position(stationLocation).title
                (RailSingleton.getStationMap().get(stationId).getStationDesc()));
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(stationLocation, 15.0f));
        map.animateCamera(CameraUpdateFactory.zoomTo(16),1000,null);
    }


    @Override
    public void onDestroyView(){
        super.onDestroyView();
        if(map!=null){
            getActivity().getSupportFragmentManager().beginTransaction().remove(getFragmentManager().findFragmentByTag(TAG_MAP));
            map=null;
        }
    }


    @Override
    public void onStart() {
        super.onStart();
        Bundle extras = getArguments();
        if(extras!=null) {
            this.stationId = extras.getString(FragmentUtils.PARENT_POSITION_KEY);
            this.childPosition = extras.getInt(FragmentUtils.CHILD_POSITION_KEY);
            this.stationLat = extras.getDouble(FragmentUtils.STATION_LAT);
            this.stationLo = extras.getDouble(FragmentUtils.STATION_LONG);
            this.stationLocation = new LatLng(this.stationLat, this.stationLo);
        }
        if(stationId!= null) updateDetails(stationId);
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


    /**
     * set current location
     */
    private void getLocation() throws SecurityException{
        Location l= null;
        LocationManager lm = null;
        MyLocationListener listener = new MyLocationListener();

        lm = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        if(lm.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, listener);
            l = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        }
        if(lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, listener);
            l = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        }


        if(l!=null){
            this.myLo = l.getLongitude();
            this.myLat = l.getLatitude();
            this.myLocation = new LatLng(this.myLat, this.myLo);
        }
        Log.d(TAG, "current lat, long: "+this.myLat+", "+this.myLo);

        lm.removeUpdates(listener);
    }


}


