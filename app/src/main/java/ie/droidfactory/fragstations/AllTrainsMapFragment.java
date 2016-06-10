package ie.droidfactory.fragstations;

import android.app.Activity;
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
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.HashMap;

import ie.droidfactory.fragstations.httputils.AsyncMode;
import ie.droidfactory.fragstations.httputils.AsyncStationsList;
import ie.droidfactory.fragstations.httputils.Links;
import ie.droidfactory.fragstations.model.RailInterface;
import ie.droidfactory.fragstations.model.Train;
import ie.droidfactory.fragstations.utils.LocationUtils;
import ie.droidfactory.fragstations.utils.MyShared;
import ie.droidfactory.fragstations.utils.RailSingleton;

/**
 * Created by kudlaty on 09/06/2016.
 */
public class AllTrainsMapFragment extends MainFragment {//implements AsyncStationsList.AsyncDoneCallback{

    RailInterface trainCallback;
    public void setTrainSelectedListener(RailInterface listener){
        trainCallback = listener;
    }
    public static AllTrainsMapFragment newInstance(Bundle args){
        AllTrainsMapFragment fragment = new AllTrainsMapFragment();
        fragment.setArguments(args);
        return fragment;
    }
    private AsyncStationsList.AsyncDoneCallback asyncDone = new AsyncStationsList
            .AsyncDoneCallback() {
        @Override
        public void onAsyncDone(boolean done) {
            if (done) {
                Log.d(TAG, "onAsyncDone(), create map");
                createMapFragment(FRAGMENT.CREATE);
            }
        }
    };

    private final static String TAG = AllTrainsMapFragment.class.getSimpleName();
    private final static String TAG_FULL_TRAINS_MAP="fragment_full_trains_map";
    private enum FRAGMENT{CREATE, REFRESH};
    private Button btnRefresh;
    private TextView tvInfo;
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
        tvInfo = (TextView) view.findViewById(R.id.fragment_all_trains_mapa_text_info);
        //TODO get async train list and create map fragment
        Log.d(TAG, "try download a list of current trains...");
        String link = Links.GET_ALL_TRAINS.getRailLink();
        mapFragment = (SupportMapFragment) getActivity().getSupportFragmentManager().findFragmentByTag(TAG_FULL_TRAINS_MAP);
        if(mapFragment==null){
            AsyncStationsList rail = new AsyncStationsList(getActivity(), AsyncMode.GET_ALL_TRAINS,
                    asyncDone, tvInfo);
            rail.execute(link);
        }

        btnRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "restart map!");
                createMapFragment(FRAGMENT.REFRESH);
            }
        });
    }
//    @Override
//    public void onAsyncDone(boolean done) {
//        //TODO: if done load map with markers
//        Log.d(TAG, "onAsyncDone(), create map");
//        createMap();
//    }




    private void createMapFragment(FRAGMENT todo){
        if(todo==FRAGMENT.CREATE){
            if(mapFragment==null){
                Log.d(TAG, "FRAGMENT.CREATE, mapFragment is NULL, create new!");
                FragmentManager fm = getFragmentManager();
                mapFragment = SupportMapFragment.newInstance();
                fm.beginTransaction().add(R.id.fragment_all_trains_map_mapa, mapFragment,
                        TAG_FULL_TRAINS_MAP).commit();
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
        }
        if(todo==FRAGMENT.REFRESH){
            if (mapFragment != null) {
                Log.d(TAG, "FRAGMENT.REFRESH, mapFragment is NOT NULL, kill and create new!");
                FragmentManager fm = getFragmentManager();
                fm.beginTransaction().remove(mapFragment).commit();
                fm.executePendingTransactions();
                fm = getFragmentManager();
                mapFragment = SupportMapFragment.newInstance();
                fm.beginTransaction().add(R.id.fragment_all_trains_map_mapa, mapFragment,
                        TAG_FULL_TRAINS_MAP).commit();
                mapFragment.getMapAsync(new OnMapReadyCallback(){

                    @Override
                    public void onMapReady(GoogleMap arg0) {
                        if(arg0!=null){
                            map=arg0;
                            setMap(RailSingleton.getTrainMap());
                        }
                        Log.d(TAG, "FRAGMENT.REFRESH::onMapReady arg0 is null: "+(arg0==null));

                    }

                });
            }else{
                Log.d(TAG, "FRAGMENT.REFRESH, mapFragment is NULL, create new!");
                FragmentManager fm = getFragmentManager();
                mapFragment = SupportMapFragment.newInstance();
                fm.beginTransaction().add(R.id.fragment_all_trains_map_mapa, mapFragment,
                        TAG_FULL_TRAINS_MAP).commit();
                mapFragment.getMapAsync(new OnMapReadyCallback(){

                    @Override
                    public void onMapReady(GoogleMap arg0) {
                        if(arg0!=null){
                            map=arg0;
                            setMap(RailSingleton.getTrainMap());
                        }
                        Log.d(TAG, "FRAGMENT.REFRESH::onMapReady arg0 is null: "+(arg0==null));

                    }

                });
            }
        }
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
        if(list!=null && list.size()!=1){
            for(String i: list.keySet()){
                addTrainMarker(i, new LatLng(list.get(i).getTrainLatitude(),
                                list.get(i).getTrainLongitude()),
                        list.get(i).getDirection(),//title
                        list.get(i).getTrainCode(),//snippet
                        BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));//icon
            }
        }
        //add user location as default marker
        map.addMarker(new MarkerOptions().position(this.myLocation).title("My Location"));
        map.animateCamera(CameraUpdateFactory.zoomTo(16),1000,null);
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(this.myLocation, 12.0f));
        Log.d(TAG, "setMap() map is null: "+(map==null));
    }

    private void addTrainMarker(String id, LatLng trainLatLng, String trainTitle, String code,
                            BitmapDescriptor ic){
        map.addMarker(new MarkerOptions().position(trainLatLng).title(trainTitle).snippet(code)
                .icon(ic));
        map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener(){

            @Override
            public boolean onMarkerClick(Marker arg0) {
                Toast.makeText(getActivity(), "click station"+arg0.getSnippet()
                        +":\n"+RailSingleton.getTrainMap().
                        get(arg0.getSnippet()).getPublicMessage(), Toast.LENGTH_SHORT).show();
                Log.d(TAG, "item clicked: "+arg0.getTitle()+" code: "+arg0.getSnippet());
                trainCallback.onTrainSelected(arg0.getSnippet());
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
            trainCallback = (RailInterface) activity;
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
