package ie.droidfactory.irishrails.utils;

import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
/**
 * Created by kudlaty on 02/06/2016.
 */
public class MyLocationListener  implements LocationListener{
	
	public static double myLongitude, myLatitude;

	@Override
	public void onLocationChanged(Location location) {
//		location.getLatitude();
//		location.getLongitude();
		
		myLatitude = location.getLatitude();
		myLongitude = location.getLongitude();
		
		
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub
		
	}

}
