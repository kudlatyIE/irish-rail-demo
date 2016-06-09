package ie.droidfactory.fragstations.utils;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by kudlaty on 09/06/2016.
 */
public class LocationUtils {

    private final static String TAG = LocationUtils.class.getSimpleName();

    public static LatLng getLocation(Activity ac) throws SecurityException, NullPointerException{
        LatLng myLocation=null;
        double myLat=0, myLo=0;
        Location l= null;
        LocationManager lm = null;
        MyLocationListener listener = new MyLocationListener();

        lm = (LocationManager) ac.getSystemService(Context.LOCATION_SERVICE);
        if(lm.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, listener);
            l = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        }
        if(lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, listener);
            l = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        }


        if(l!=null){
            myLo = l.getLongitude();
            myLat = l.getLatitude();
            myLocation = new LatLng(myLat, myLo);
            RailSingleton.setMyLocation(myLocation);
            MyShared.setMyLastLocation(ac, myLocation);
        }
        Log.d(TAG, "current lat, long: "+myLat+", "+myLo);

        lm.removeUpdates(listener);

        return myLocation;
    }
}
