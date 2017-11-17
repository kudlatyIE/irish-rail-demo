package ie.droidfactory.irishrails;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import ie.droidfactory.irishrails.db.DbRailSource;
import ie.droidfactory.irishrails.httputils.AsyncMode;
import ie.droidfactory.irishrails.httputils.AsyncStationsList;
import ie.droidfactory.irishrails.httputils.Links;
import ie.droidfactory.irishrails.model.RailInterface;
import ie.droidfactory.irishrails.model.Station;
import ie.droidfactory.irishrails.utils.FragmentUtils;
import ie.droidfactory.irishrails.utils.LocationUtils;
import ie.droidfactory.irishrails.utils.MyLocationListener;
import ie.droidfactory.irishrails.utils.MyShared;
import ie.droidfactory.irishrails.utils.RailSingleton;

/**
 * Created by kudlaty on 02/06/2016.
 */
public class AllStationsMapFragment extends MainFragment {//implements AsyncStationsList.AsyncDoneCallback{


    RailInterface stationCallback;

    public void setStationSelectedListener(RailInterface listener) {
        stationCallback = listener;
    }


    public interface RestartCallback {
        void onRestartButtonClicked(boolean isClicked, String fragmentName);
    }

    private RestartCallback restartCallback;

    private AsyncStationsList.AsyncDoneCallback asyncDone = new AsyncStationsList
            .AsyncDoneCallback() {
        @Override
        public void onAsyncDone(boolean done) {
            if (done) {
                Log.d(TAG, "onAsyncDone() callback, create map");
                createMap(RailSingleton.getStationMap());
            } else {
                createMap(null);
            }
        }
    };

    public static AllStationsMapFragment newInstance(Bundle args) {
        AllStationsMapFragment fragment = new AllStationsMapFragment();
        fragment.setArguments(args);
        return fragment;
    }

    private final static String TAG = AllStationsMapFragment.class.getSimpleName();
    private final static String TAG_FULL_MAP = "fragment_full_map";
    private SupportMapFragment mapFragment;
    private GoogleMap map;
    private LatLng myLocation = null;
//    private HashMap<String, Station> stationMap;
    private List<String> favList;
    private ImageView imgFavStation;
    private TextView tvInfo;
    private RelativeLayout topBox;
    private FloatingActionButton btnFabFav;
    private boolean isShownFav;

    private float dX, newX;
    private float dY, newY;
    private int lastAction;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_station_details_mapa, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentByTag(TAG_FULL_MAP);
        imgFavStation = view.findViewById(R.id.fragment_details_mapa_img_show_fav);
        tvInfo = view.findViewById(R.id.fragment_details_mapa_text_info);
        btnFabFav = view.findViewById(R.id.fragment_details_mapa_btn_fab);
        imgFavStation.setVisibility(View.GONE);
//        tvInfo.setVisibility(View.GONE);
//        topBox = view.findViewById(R.id.fragment_details_mapa_top_box);
        if (mapFragment == null) {
            FragmentManager fm = getFragmentManager();
            mapFragment = SupportMapFragment.newInstance();
            fm.beginTransaction().replace(R.id.fragment_details_map_mapa, mapFragment, TAG_FULL_MAP).commit();

            if (RailSingleton.getStationMap() == null) {
                downloadAllStationList();
            } else createMap(RailSingleton.getStationMap());
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onActivityCreated(savedInstanceState);

        tvInfo.setText(R.string.all_stations);
        favList = getFavList();
        isShownFav=false;
        if(savedInstanceState!=null){
            restore(savedInstanceState);
        }
        btnFabFav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "floating action button onClick");
                showFavStations();
            }
        });

        btnFabFav.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                int thersold=0;
                switch (event.getActionMasked()) {
                    case MotionEvent.ACTION_DOWN:
                        dX = view.getX() - event.getRawX();
                        dY = view.getY() - event.getRawY();
                        lastAction = MotionEvent.ACTION_DOWN;
                        break;
                    case MotionEvent.ACTION_MOVE:

                        newX = view.getX() - event.getRawX();
                        newY = view.getY() - event.getRawY();
                        view.setY(event.getRawY() + dY);
                        view.setX(event.getRawX() + dX);
                        lastAction = MotionEvent.ACTION_MOVE;
                        break;
                    case MotionEvent.ACTION_UP:
//                        if (lastAction == MotionEvent.ACTION_DOWN)
//                            showFavStations(isShownFav);
//                        break;
                        Log.d(TAG, "FAV acc UP!");
                        Log.d(TAG, "dx: "+Math.abs(dX-newX));
                        Log.d(TAG, "dy: "+Math.abs(dY-newY));
                        if(getActivity().getResources().getBoolean(R.bool.is_tablet)) thersold=0;
                        else thersold=5;

                        if(lastAction==MotionEvent.ACTION_DOWN || (Math.abs(dX-newX)<=thersold && (Math.abs(dY-newY))<=thersold)){
                                view.performClick();
                            return false;
                        }else return true;
                    default:
                        return false;
                }
                return true;
            }

        });


    }

    private List<String> getFavList(){
        List<String> list = new ArrayList<>();
        DbRailSource dbRailSource = new DbRailSource(getActivity());
        dbRailSource.open();
        list = dbRailSource.getFavoritiesStationIds();
        dbRailSource.close();
        return list;
    }

    private LatLng setLatLng() {
        LatLng ll = LocationUtils.getLatLng(getActivity());
        if (ll==null){
            //TODO: dialog turn on Location - if dismiss get default Dublin location from Shared Pref
            ll = MyShared.getMyLastLocation(getActivity());
        }
        return ll;
    }

    private void downloadAllStationList() {
//        LocationUtils.getLocation(getActivity());
        setLatLng();

        AsyncStationsList rail = new AsyncStationsList(getActivity(), AsyncMode.GET_ALL_STATIONS, asyncDone);
        rail.execute(Links.ALL_STATIONS.getRailLink());
    }

    private void createMap(final HashMap<String, Station> list) {
        mapFragment.getMapAsync(new OnMapReadyCallback() {

            @Override
            public void onMapReady(GoogleMap arg0) {
                if (arg0 != null) {
                    map = arg0;
                    try {
                        setMap(list);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }



    private void showFavStations(){
        favList = getFavList();
        if(favList==null || favList.size()==0) Toast.makeText(getActivity(), "Fav list is empty", Toast.LENGTH_SHORT).show();
        if(!isShownFav){
            isShownFav=true;
            HashMap<String, Station> stationMap = new HashMap<>();
            for (String key: favList){
                if(RailSingleton.getStationMap().containsKey(key)) stationMap.put(key, RailSingleton.getStationMap().get(key));
            }
            Log.d(TAG, "fav list size: "+favList.size());
            Log.d(TAG, "fav map size: "+stationMap.size());

//            imgFavStation.setImageDrawable(getResources().getDrawable(R.drawable.ic_show_favorit));
            btnFabFav.setImageDrawable(getResources().getDrawable(R.drawable.ic_show_favorit));
            tvInfo.setText(R.string.fav_stations);
            createMap(stationMap);
        }else {
            isShownFav=false;
//            imgFavStation.setImageDrawable(getResources().getDrawable(R.drawable.ic_show_favorit_no));
            btnFabFav.setImageDrawable(getResources().getDrawable(R.drawable.ic_show_favorit_no));
            tvInfo.setText(R.string.all_stations);
            createMap(RailSingleton.getStationMap());
        }
    }

    private void setMap(HashMap<String, Station> list) throws Exception {

        try{
            this.myLocation = LocationUtils.getLatLng(getActivity());
        }catch (NullPointerException e){
            this.myLocation = RailSingleton.getMyLatLng();
            if ((myLocation==null)){
                Toast.makeText(getActivity(), "unable to get location", Toast.LENGTH_SHORT).show();
                return;
            }
        }
        map.getUiSettings().setAllGesturesEnabled(true);
        map.clear();
        if (list == null || list.size() == 0) {
            map.addMarker(new MarkerOptions().position(this.myLocation).title("My Location"));
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(this.myLocation, 12.0f));
            map.animateCamera(CameraUpdateFactory.zoomTo(16), 1000, null);
            return;
        } else {
            for (String i : list.keySet()) {

                addStationsMarker(i, new LatLng(list.get(i).getStationLatitude(),
                                list.get(i).getStationLongitude()),
                        list.get(i).getStationDesc(),//title
                        list.get(i).getStationCode(),//snippet
                        BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));//icon
            }
        }
        map.animateCamera(CameraUpdateFactory.zoomTo(16), 1000, null);
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(this.myLocation, 12.0f));

    }

    private void addStationsMarker(String id, LatLng stationLatLng, String stationTitle, String code, BitmapDescriptor ic) {
        map.addMarker(new MarkerOptions().position(stationLatLng).title(stationTitle).snippet(code).icon(ic));
        map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {

            @Override
            public boolean onMarkerClick(Marker arg0) {

                Log.d(TAG, "item clicked: " + arg0.getTitle() + " code: " + arg0.getSnippet());
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
        try {
            stationCallback = (RailInterface) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + "OnStationSelected Listener is not imolemented...");
        }
        Log.d(TAG, "onAttach end...");
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (map != null) {
            getActivity().getSupportFragmentManager().beginTransaction().remove(getFragmentManager().findFragmentByTag(TAG_FULL_MAP));
            map = null;
            Log.d(TAG, "Nick, you bad boy, do not destroy map anymore!");
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        // TODO Auto-generated method stub
        super.onSaveInstanceState(outState);
        outState.putBoolean(FragmentUtils.FAV_LIST, isShownFav);
    }
    @Override
    public void onViewStateRestored(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onViewStateRestored(savedInstanceState);
        getActivity().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        restore(savedInstanceState);
    }

    public void restore(Bundle savedInstanceState) {
        if(savedInstanceState!=null){
            isShownFav = savedInstanceState.getBoolean(FragmentUtils.FAV_LIST);
        }
    }



}
//NullPointerException