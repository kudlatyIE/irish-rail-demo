package ie.droidfactory.irishrails;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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

import ie.droidfactory.irishrails.httputils.AsyncMode;
import ie.droidfactory.irishrails.httputils.AsyncStationsList;
import ie.droidfactory.irishrails.httputils.Links;
import ie.droidfactory.irishrails.model.RailInterface;
import ie.droidfactory.irishrails.model.Train;
import ie.droidfactory.irishrails.utils.LocationUtils;
import ie.droidfactory.irishrails.utils.MyLocationListener;
import ie.droidfactory.irishrails.utils.MyShared;
import ie.droidfactory.irishrails.utils.RailSingleton;

/**
 * Created by kudlaty on 09/06/2016.
 */
public class AllTrainsMapFragment extends MainFragment {

    RailInterface trainCallback;
    public void setStationSelectedListener(RailInterface listener){
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
                Log.d(TAG, "onAsyncDone() callback, create map");
                setMap(RailSingleton.getTrainMap());
            }
        }
    };

    private final static String TAG = AllTrainsMapFragment.class.getSimpleName();
    public final static String TAG_FULL_TRAINS_MAP="fragment_full_trains_map";

    private enum FRAGMENT{CREATE, REFRESH, RECREATE_MARKS};

    private SupportMapFragment mapFragment;
    private GoogleMap map;
    private double myLat=0, myLo=0;
    private LatLng myLocation=null;
    private ImageView imgFav;
    private TextView tvInfo;
    private FloatingActionButton btnFabFav;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState ){
        View v = inflater.inflate(R.layout.fragment_station_details_mapa, container, false);
        imgFav = v.findViewById(R.id.fragment_details_mapa_img_show_fav);
        tvInfo = v.findViewById(R.id.fragment_details_mapa_text_info);
        btnFabFav = v.findViewById(R.id.fragment_details_mapa_btn_fab);
        btnFabFav.setVisibility(View.GONE);
        imgFav.setVisibility(View.GONE);
        tvInfo.setVisibility(View.GONE);
        return v;
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);
        mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentByTag(TAG_FULL_TRAINS_MAP);
        if(mapFragment==null){
            FragmentManager fm = getFragmentManager();
            mapFragment = SupportMapFragment.newInstance();
            fm.beginTransaction().replace(R.id.fragment_details_map_mapa, mapFragment, TAG_FULL_TRAINS_MAP).commit();
            mapFragment.getMapAsync(new OnMapReadyCallback(){

                @Override
                public void onMapReady(GoogleMap arg0) {
                    if(arg0!=null){
                        map=arg0;
                        AsyncStationsList rail = new AsyncStationsList(getActivity(), AsyncMode.GET_ALL_TRAINS, asyncDone);
                        rail.execute(Links.GET_ALL_TRAINS.getRailLink());
//                        setMap(RailSingleton.getTrainMap());
                    }

                }
            });

        }
    }

    private void setMap(HashMap<String, Train> list){

        try{
            this.myLocation = LocationUtils.getLatLng(getActivity());
            if(myLocation==null){
                this.myLocation=MyShared.getMyLastLocation(getActivity());
                //TODO: alert to enable Location
            }
            this.myLo = myLocation.longitude;
            this.myLat = myLocation.latitude;
        }catch (NullPointerException e){
//            myLocation = MyShared.getMyLastLocation(getActivity());
            myLocation = RailSingleton.getMyLatLng();
            if(myLocation!=null){
                this.myLo = myLocation.longitude;
                this.myLat = myLocation.latitude;
            }else {
                Toast.makeText(getActivity(), "unable to get location", Toast.LENGTH_SHORT).show();
                return;
            }
        }
        if(map==null) return;
        map.getUiSettings().setAllGesturesEnabled(true);
        if(list==null || list.size()==1){
            Log.d(TAG, "my LatLng:"+myLocation.latitude+" :: "+myLocation.longitude);
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(this.myLocation, 12.0f));
            map.addMarker(new MarkerOptions().position(this.myLocation).title("My Location"));
            map.animateCamera(CameraUpdateFactory.zoomTo(16),1000,null);
            return;
        }else{
            for(String i: list.keySet()){

                addTrainsMarker(i, new LatLng(list.get(i).getLatitude(),
                                list.get(i).getLongitude()),
                        list.get(i).getDirection(),//title
                        list.get(i).getTrainCode(),//snippet
                        BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));//icon
            }
        }
        map.animateCamera(CameraUpdateFactory.zoomTo(16),1000,null);
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(this.myLocation, 12.0f));

    }

    private void addTrainsMarker(String id, LatLng stationLatLng, String stationTitle, String code, BitmapDescriptor ic){
        map.addMarker(new MarkerOptions().position(stationLatLng).title(stationTitle).snippet(code).icon(ic));
        map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener(){

            @Override
            public boolean onMarkerClick(Marker arg0) {

//                Log.d(TAG, "item clicked: "+arg0.getTitle()+" code: "+arg0.getSnippet());
                stationCallback.onTrainSelected(arg0.getSnippet());
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
//            getActivity().getSupportFragmentManager().beginTransaction().remove(getFragmentManager().findFragmentByTag(TAG_FULL_TRAINS_MAP));
//            map=null;
            Log.d(TAG, "Nick, you bad boy, do not destroy map anymore!");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // retain this fragment
        setRetainInstance(true);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString(FRAGMENT.RECREATE_MARKS.name(), "doIt");//recreate markers
    }





}
