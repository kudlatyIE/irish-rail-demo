package ie.droidfactory.fragstations.utils;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
/**
 * Created by kudlaty on 02/06/2016.
 */
public class PermissionUtilsOLD {
	
	private final static String TAG = PermissionUtilsOLD.class.getSimpleName();
//	private final static ArrayList<PermEnum> list = new ArrayList<PermEnum>();
	public static  PermEnum permEnum;
	
	
	public static boolean checkSensiPermision(final Activity ac){
		boolean result = true;
		int ok = PackageManager.PERMISSION_GRANTED;
		int code, permRes=-1; 
		String perm;
		for(PermEnum e: PermEnum.values()){
			perm  = e.getPermissionName();
			code = e.getRequestCode();
			Log.d(TAG, "Pemission name: "+perm+" code: "+code);
			permRes = ActivityCompat.checkSelfPermission(ac, perm);
			if(permRes!=ok){
				Log.d(TAG, e.getRequest());
				result=false;
				if(!ActivityCompat.shouldShowRequestPermissionRationale(ac, perm)){
//					list.add(e);
					ActivityCompat.requestPermissions(ac, new String[]{perm}, code);
					click(ac, perm, code, e);
					return false;
				}
			}
		}
		return result;
	}
	
	private static void click(final Activity a, final String p, final int c, PermEnum e){
		View.OnClickListener listener = new View.OnClickListener(){

			@Override
			public void onClick(View v) {
				ActivityCompat.requestPermissions(a, new String[]{p}, c);
			}
		};
	}

}
