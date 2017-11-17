package ie.droidfactory.irishrails.utils;

import android.app.Activity;
import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.maps.model.LatLng;

/**
 * Created by kudlaty on 09/06/2016.
 */
public class LocationUtils {

    private final static String TAG = LocationUtils.class.getSimpleName();

    public static LatLng getLatLng(Activity ac) throws SecurityException, NullPointerException{
//        FusedLocationProviderClient mFusedLocationClient;
        LatLng myLatLng=null;
        double myLat=0, myLo=0;
        Location l= null;
        LocationManager lm;
        MyLocationListener listener = new MyLocationListener();

        lm = (LocationManager) ac.getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        String bestProvider = lm.getBestProvider(criteria, false);
//        Location location = lm.getLastKnownLocation(bestProvider);

        if(lm.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, listener);
            l = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        }else {
            Log.d(TAG, "GPS_PROVIDER disabled...");
            if(lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, listener);
                l = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            }else {
                Log.d(TAG, "NETWORK_PROVIDER disabled...");
                Toast.makeText(ac, "Please TURN ON location on your phone", Toast.LENGTH_SHORT).show();
                myLatLng = MyShared.getMyLastLocation(ac);
                RailSingleton.setMyLocation(myLatLng);
                return myLatLng;

            }
        }

        if(l!=null){
            myLo = l.getLongitude();
            myLat = l.getLatitude();
            myLatLng = new LatLng(myLat, myLo);
            RailSingleton.setMyLocation(myLatLng);
            MyShared.setMyLastLocation(ac, myLatLng);
        }
        Log.d(TAG, "current lat, long: "+myLat+", "+myLo);
        lm.removeUpdates(listener);

        return myLatLng;
    }

//    /**
//     * get current location
//     */
//    public static Location getLocation(Context context) throws SecurityException{
//        Location l= null;
//        LocationManager lm;
//        LatLng myLatLng;
//        double myLat, myLo;
//        MyLocationListener listener = new MyLocationListener();
//
//        lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
//        if(lm.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
//            lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, listener);
//            l = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
//        }else {
//            if(lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
//                lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, listener);
//                l = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
//            }else {
//                return null;
//            }
//        }
//
//        if(l!=null){
//            Log.d(TAG, "current lat, long: "+l.getLatitude()+", "+l.getLongitude());
//            myLo = l.getLongitude();
//            myLat = l.getLatitude();
//            myLatLng = new LatLng(myLat, myLo);
//            RailSingleton.setMyLocation(myLatLng);
//            MyShared.setMyLastLocation(context, myLatLng);
//        }
//        lm.removeUpdates(listener);
//        return l;
//    }
}
