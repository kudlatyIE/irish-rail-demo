package ie.droidfactory.irishrails;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import ie.droidfactory.irishrails.model.Station;
import ie.droidfactory.irishrails.utils.LocationUtils;
import ie.droidfactory.irishrails.utils.MyLocationListener;
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_station_details_mapa, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentByTag(TAG_FULL_MAP);
        if (mapFragment == null) {
            FragmentManager fm = getFragmentManager();
            mapFragment = SupportMapFragment.newInstance();
            fm.beginTransaction().replace(R.id.fragment_details_map_mapa, mapFragment, TAG_FULL_MAP).commit();

            if (RailSingleton.getStationMap() == null) {
                downloadAllStationList();
            } else createMap(RailSingleton.getStationMap());

        }
    }

    private void downloadAllStationList() {
        LocationUtils.getLocation(getActivity());
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
                        Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    private void setMap(HashMap<String, Station> list) throws Exception {

        getLocation();
        map.getUiSettings().setAllGesturesEnabled(true);
        if (list == null || list.size() == 1) {

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

    private void getLocation() throws Exception {
        Location l = null;
        LocationManager lm;
        MyLocationListener listener = new MyLocationListener();

        try {
            lm = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
            if (lm.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                if (ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                        && ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    throw new Exception("location permission not granted");

                }
                lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, listener);
                l = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            }
            if(lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, listener);
                l = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            }
            lm.removeUpdates(listener);
            if(l!=null){
                this.myLocation = new LatLng(l.getLatitude(), l.getLongitude());
                Log.d(TAG, "current lat, long: "+l.getLatitude()+", "+l.getLongitude());
            }
        }catch (NullPointerException e){
            Log.d(TAG, "capture location problem");
        }
    }


}
//NullPointerException