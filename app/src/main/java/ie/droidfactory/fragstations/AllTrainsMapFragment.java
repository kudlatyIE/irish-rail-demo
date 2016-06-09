package ie.droidfactory.fragstations;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.HashMap;

import ie.droidfactory.fragstations.model.Station;
import ie.droidfactory.fragstations.model.StationInterface;
import ie.droidfactory.fragstations.model.Train;
import ie.droidfactory.fragstations.utils.FragmentUtils;
import ie.droidfactory.fragstations.utils.LocationUtils;
import ie.droidfactory.fragstations.utils.MyShared;
import ie.droidfactory.fragstations.utils.RailSingleton;

/**
 * Created by kudlaty on 09/06/2016.
 */
public class AllTrainsMapFragment extends MainFragment{

    StationInterface stationCallback;
    public void setStationSelectedListener(StationInterface listener){
        stationCallback = listener;
    }
    public interface RestartCallback{
        void onRestartButtonClicked(boolean isClicked, String fragmentName);
    }
    private RestartCallback restartCallback;

    private final static String TAG = AllTrainsMapFragment.class.getSimpleName();
    private final static String TAG_FULL_TRAINS_MAP="fragment_full_trains_map";
    private enum FRAGMENT{CREATE, REFRESH};
    private Button btnRefresh;
    private SupportMapFragment mapFragment;
    private GoogleMap map;
    private LatLng myLocation=null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState ){
        View v = inflater.inflate(R.layout.fragment_map_all_trains, container, false);
        return v;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);
        Log.d(TAG, "onViewCreated beginning...");
        Log.d(TAG, "onViewCreated map is null: "+(map==null));
        btnRefresh = (Button) view.findViewById(R.id.fragment_all_trains_mapa_btn_refresh);
        //TODO create map fragment
        if(mapFragment==null){
            Log.d(TAG, "FRAGMENT.CREATE, mapFragment is NULL, create new!");
            FragmentManager fm = getFragmentManager();
            mapFragment = SupportMapFragment.newInstance();
            fm.beginTransaction().add(R.id.fragment_all_map_mapa, mapFragment, TAG_FULL_TRAINS_MAP)
                    .commit();
            mapFragment.getMapAsync(new OnMapReadyCallback(){

                @Override
                public void onMapReady(GoogleMap arg0) {
                    if(arg0!=null){
                        map=arg0;
                        setMap(RailSingleton.getTrainMap());
                    }
                    Log.d(TAG, "FRAGMENT.CREATE::onMapReady arg0 is null: "+(arg0==null));

                }

            });
        }else{
            Log.d(TAG, "FRAGMENT.CREATE, mapFragment is not null...");
        }
        btnRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "restart callback!");
                restartCallback.onRestartButtonClicked(true, FragmentUtils.FRAGMENT_ALL_TRAINS_MAP);
            }
        });
    }

    private void setMap(HashMap<String, Train> list){
        Log.d(TAG, "setMap beginning...");
        try{
            myLocation = LocationUtils.getLocation(getActivity());
        }catch(NullPointerException e){
            if(RailSingleton.getMyLocation()!=null) this.myLocation = RailSingleton.getMyLocation();
            else this.myLocation = MyShared.getMyLastLocation(getActivity());
        }
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
        Log.d(TAG, "setMap map is null: "+(map==null));
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
            stationCallback = (StationInterface) activity;
            restartCallback = (RestartCallback) activity;
        }catch(ClassCastException e){
            throw new ClassCastException(activity.toString()+ e.getMessage()+" is not " +
                    "implemented...");
        }

        Log.d(TAG, "onAttach end...");
    }

    @Override
    public void onDestroyView(){
        super.onDestroyView();
        Log.d(TAG, "onDestroyView, beginning...");
        if(map!=null){
            getActivity().getSupportFragmentManager().beginTransaction().remove
                    (getFragmentManager().findFragmentByTag(TAG_FULL_TRAINS_MAP)).commit();
            map=null;
        }
    }



}
