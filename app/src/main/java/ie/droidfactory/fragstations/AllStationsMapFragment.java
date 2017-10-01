package ie.droidfactory.fragstations;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.HashMap;

import ie.droidfactory.fragstations.model.RailInterface;
import ie.droidfactory.fragstations.model.Station;
import ie.droidfactory.fragstations.utils.FragmentUtils;
import ie.droidfactory.fragstations.utils.LocationUtils;
import ie.droidfactory.fragstations.utils.MyLocationListener;
import ie.droidfactory.fragstations.utils.MyShared;
import ie.droidfactory.fragstations.utils.RailSingleton;

/**
 * Created by kudlaty on 02/06/2016.
 */
public class AllStationsMapFragment extends MainFragment {//implements AsyncStationsList.AsyncDoneCallback{


    RailInterface stationCallback;
    public void setStationSelectedListener(RailInterface listener){
        stationCallback = listener;
    }


    public interface RestartCallback{
        void onRestartButtonClicked(boolean isClicked, String fragmentName);
    }
    private RestartCallback restartCallback;

    public static AllStationsMapFragment newInstance(Bundle args){
        AllStationsMapFragment fragment = new AllStationsMapFragment();
        fragment.setArguments(args);
        return fragment;
    }
    private enum FRAGMENT{CREATE, REFRESH};

    private final static String TAG = AllStationsMapFragment.class.getSimpleName();
    private final static String TAG_FULL_MAP="fragment_full_map";
    private SupportMapFragment mapFragment;
    private GoogleMap map;
    private double myLat=0, myLo=0;
    private LatLng myLocation=null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState ){
        View v = inflater.inflate(R.layout.fragment_station_details_mapa, container, false);
        return v;
    }

//    @Override
//    public void onViewCreated(View view, Bundle savedInstanceState){
//        super.onViewCreated(view, savedInstanceState);
//        Log.d(TAG, "onViewCreated beginning...");
//        Log.d(TAG, "onViewCreated map is null: "+(googleMap==null));
//        btnRefresh = (Button) view.findViewById(R.id.fragment_all_mapa_btn_refresh);
//        //TODO create map fragment
//    }
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);
        mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentByTag(TAG_FULL_MAP);
        if(mapFragment==null){
            FragmentManager fm = getFragmentManager();
            mapFragment = SupportMapFragment.newInstance();
            fm.beginTransaction().replace(R.id.fragment_details_map_mapa, mapFragment, TAG_FULL_MAP).commit();
            mapFragment.getMapAsync(new OnMapReadyCallback(){

                @Override
                public void onMapReady(GoogleMap arg0) {
                    if(arg0!=null){
                        map=arg0;
                        setMap(RailSingleton.getStationMap());
                    }

                }

            });

        }
    }
//    @Override
//    public void onViewCreated(View view, Bundle savedInstanceState) {
//        // TODO Auto-generated method stub
//        super.onViewCreated(view, savedInstanceState);
//        btnRefresh = (Button) view.findViewById(R.id.fragment_all_mapa_btn_refresh);
//    }


//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        // retain this fragment
//        setRetainInstance(true);
//    }
//
//    @Override
//    public void onActivityCreated(Bundle savedInstanceState) {
//        // TODO Auto-generated method stub
//        super.onActivityCreated(savedInstanceState);
//        createMapFragment(FRAGMENT.CREATE);
//        btnRefresh.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Log.d(TAG, "restart callback!");
//                restartCallback.onRestartButtonClicked(true, FragmentUtils.FRAGMENT_ALL_MAP);
//            }
//        });
//    }

//    private void initializeMap(SupportMapFragment fragment){
//        if(fragment!=null){
//            mapFragment.getMapAsync(new OnMapReadyCallback(){
//
//                @Override
//                public void onMapReady(GoogleMap arg0) {
//                    if(arg0!=null){
//                        googleMap=arg0;
//                        setMap(RailSingleton.getStationMap());
//                    }
//                    Log.d(TAG, "FRAGMENT.CREATE::onMapReady arg0 is null: "+(arg0==null));
//                }
//            });
//        }else{
//            Log.e(TAG, "unable initalize map, mapFragment is null");
//        }
//    }

    private void setMap(HashMap<String, Station> list){
        getLocation();
        map.getUiSettings().setAllGesturesEnabled(true);
        if(list==null || list.size()==1){

            map.addMarker(new MarkerOptions().position(this.myLocation).title("My Location"));
            map.animateCamera(CameraUpdateFactory.zoomTo(16),1000,null);
            return;
        }else{
            for(String i: list.keySet()){

                addStationsMarker(i, new LatLng(list.get(i).getStationLatitude(),
                                list.get(i).getStationLongitude()),
                        list.get(i).getStationDesc(),//title
                        list.get(i).getStationCode(),//snippet
                        BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));//icon
            }
        }
        map.animateCamera(CameraUpdateFactory.zoomTo(16),1000,null);
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(this.myLocation, 12.0f));

    }

    private void addStationsMarker(String id, LatLng stationLatLng, String stationTitle, String code, BitmapDescriptor ic){
        map.addMarker(new MarkerOptions().position(stationLatLng).title(stationTitle).snippet(code).icon(ic));
        map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener(){

            @Override
            public boolean onMarkerClick(Marker arg0) {
                Toast.makeText(getActivity(), "click station"+arg0.getSnippet()+":\n"+RailSingleton.getStationMap().
                        get(arg0.getSnippet()).getStationDesc(), Toast.LENGTH_SHORT).show();
                Log.d(TAG, "item clicked: "+arg0.getTitle()+" code: "+arg0.getSnippet());
                stationCallback.onStationSelected(arg0.getSnippet());
                return false;
            }

        });
    }
    @SuppressWarnings("deprecation")
    @Override
    public void onAttach(Activity activity) {
        // TODO Auto-generated method stub
        super.onAttach(activity);
        try{
            stationCallback = (RailInterface) activity;
        }catch(ClassCastException e){
            throw new ClassCastException(activity.toString()+ "OnStationSelected Listener is not imolemented...");
        }
        Log.d(TAG, "onAttach end...");
    }


    @Override
    public void onDestroyView(){
        super.onDestroyView();
        if(map!=null){
            getActivity().getSupportFragmentManager().beginTransaction().remove(getFragmentManager().findFragmentByTag(TAG_FULL_MAP));
            map=null;
            Log.d(TAG, "Nick, you bad boy, do not destroy map anymore!");
        }
    }
//    private void setMap(HashMap<String, Station> list){
//        Log.d(TAG, "setMap beginning...");
//        LatLng location;
//        try{
//            myLocation = LocationUtils.getLocation(getActivity());
//        }catch(NullPointerException e){
//            if(RailSingleton.getMyLocation()!=null) this.myLocation = RailSingleton.getMyLatLng();
//            else this.myLocation = MyShared.getMyLastLocation(getActivity());
//        }
//        if(lastLocation!=null) location = lastLocation;
//        else location = myLocation;
//        googleMap.getUiSettings().setAllGesturesEnabled(true);
//        if(list!=null && list.size()!=1){
//            for(String i: list.keySet()){
//                addStationsMarker(i, new LatLng(list.get(i).getStationLatitude(),
//                                list.get(i).getStationLongitude()),
//                        list.get(i).getStationDesc(),//title
//                        list.get(i).getStationCode(),//snippet
//                        BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));//icon
//            }
//        }
//        //add user location as default marker
//        googleMap.addMarker(new MarkerOptions().position(this.myLocation).title(FragmentUtils.TAG_USER)
//                .snippet(FragmentUtils.TAG_USER));
//        googleMap.animateCamera(CameraUpdateFactory.zoomTo(16),1000,null);
//        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 12.0f));
//        Log.d(TAG, "setMap() map is null: "+(googleMap==null));
//    }

//    private void addStationsMarker(String id, LatLng stationLatLng, String stationTitle, String code, BitmapDescriptor ic){
//        googleMap.addMarker(new MarkerOptions().position(stationLatLng).title(stationTitle).snippet(code).icon(ic));
//        googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener(){
//
//            @Override
//            public boolean onMarkerClick(Marker arg0) {
//                Log.d(TAG, "item on: "+arg0.getTitle()+" code: "+arg0.getSnippet());
//                if(!arg0.getSnippet().equals(FragmentUtils.TAG_USER)) stationCallback.onStationSelected(arg0.getSnippet());
//                else Toast.makeText(getActivity(), "click on"+arg0.getSnippet(), Toast.LENGTH_SHORT).show();
//                return false;
//            }
//
//        });
//    }


//    private void createMapFragment(FRAGMENT todo){
//        mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentByTag(TAG_FULL_MAP);
//        View view = getActivity().findViewById(R.id.fragment_all_map_mapa);
////        if()
//        Log.d(TAG, "fragment_all_mapa_map getVisiblity: "+(view.getVisibility()));
//
//        if(todo==FRAGMENT.CREATE){
//            if(mapFragment==null){
//                Log.d(TAG, "FRAGMENT.CREATE, mapFragment is NULL, create new!");
//                FragmentManager fm = getChildFragmentManager();
//                mapFragment = SupportMapFragment.newInstance();
//                fm.beginTransaction().replace(R.id.fragment_all_map_mapa, mapFragment, TAG_FULL_MAP)
//                        .commit();
//                fm.executePendingTransactions();
//                initializeMap(mapFragment);
//            }else{
//                Log.d(TAG, "FRAGMENT.CREATE, mapFragment is not null...");
//            }
//        }
//        if(todo==FRAGMENT.REFRESH){
//            if(mapFragment!=null){
//                Log.d(TAG, "FRAGMENT.REFRESH, mapFragment exist, kill and create new!");
//                FragmentManager fm = getChildFragmentManager();
//                fm.beginTransaction().remove( mapFragment).commit();
//                fm.executePendingTransactions();
//                fm = getChildFragmentManager();
//                mapFragment = SupportMapFragment.newInstance();
//                fm.beginTransaction().add(R.id.fragment_all_map_mapa, mapFragment, TAG_FULL_MAP)
//                        .commit();
//                fm.executePendingTransactions();
//                initializeMap(mapFragment);
//
//            }else{
//                Log.d(TAG, "FRAGMENT.REFRESH, mapFragment is null...");
//                FragmentManager fm = getChildFragmentManager();
//                mapFragment = SupportMapFragment.newInstance();
//                fm.beginTransaction().add(R.id.fragment_all_map_mapa, mapFragment, TAG_FULL_MAP)
//                        .commit();
//                fm.executePendingTransactions();
//                initializeMap(mapFragment);
//            }
//        }
//    }

//    @SuppressWarnings("deprecation")
//    @Override
//    public void onAttach(Activity activity) {
//        // TODO Auto-generated method stub
//        super.onAttach(activity);
//        try{
//            stationCallback = (RailInterface) activity;
//            restartCallback = (RestartCallback) activity;
//        }catch(ClassCastException e){
//            throw new ClassCastException(activity.toString()+ e.getMessage()+" is not " +
//                    "implemented...");
//        }
//
//        Log.d(TAG, "onAttach end...");
//    }

//    @Override
//    public void onDestroyView(){
//        super.onDestroyView();
//        Log.d(TAG, "onDestroyView, beginning...");
//        if(googleMap!=null)  cameraPosition = googleMap.getCameraPosition();
//        lastLocation = cameraPosition.target;
//    }

//    @Override
//    public void onDestroy(){
//        SupportMapFragment mf = (SupportMapFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id
//                .fragment_all_map_mapa);
//        if(mf!=null && mf.isResumed()) getFragmentManager().beginTransaction().remove(mf)
//                .commitAllowingStateLoss();
//        super.onDestroy();
//    }

    private void getLocation(){
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
