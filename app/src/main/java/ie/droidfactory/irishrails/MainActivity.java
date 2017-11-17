package ie.droidfactory.irishrails;


import android.*;
import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.ContextThemeWrapper;

import java.util.ArrayList;

import ie.droidfactory.irishrails.utils.LocationUtils;
import ie.droidfactory.irishrails.utils.PermissionResultCallback;
import ie.droidfactory.irishrails.utils.PermissionUtils;

/**
 * now main activity works as a splash screen to check permissions
 * Created by kudlaty on 02/06/2016.
 */
public class MainActivity extends Activity implements
        ActivityCompat.OnRequestPermissionsResultCallback,PermissionResultCallback {

    private final static String TAG = MainActivity.class.getSimpleName();
    public  static final int PERMISSIONS_MULTIPLE_REQUEST = 123;

    private ArrayList<String> permissions=new ArrayList<>();
    private PermissionUtils permissionUtils;
    private AlertDialog.Builder bld;
    private static Activity suomi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        suomi = this;

        permissionUtils=new PermissionUtils(this);
        permissions.add(android.Manifest.permission.INTERNET);
        permissions.add(android.Manifest.permission.ACCESS_FINE_LOCATION);
        permissions.add(Manifest.permission.ACCESS_COARSE_LOCATION);
        permissions.add(android.Manifest.permission.ACCESS_WIFI_STATE);
        permissionUtils.check_permission(permissions,"dialog context", PERMISSIONS_MULTIPLE_REQUEST );

    }

    @Override
    protected void onResume() {
        super.onResume();
        permissionUtils.check_permission(permissions,"dialog context", PERMISSIONS_MULTIPLE_REQUEST );
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        Log.d(TAG, "onRequestPermissionsResult, request code: "+requestCode);
        permissionUtils.onRequestPermissionsResult(requestCode,permissions,grantResults);
    }

    @Override
    public void PermissionGranted(int request_code) {
        Log.i(TAG, "PERMISSION GRANTED");
//        LocationUtils.getLatLng(this);
        //TODO: ask TURN ON location and shows dialog
        checkLocationOnOff();

    }

    @Override
    public void PartialPermissionGranted(int request_code, ArrayList<String> granted_permissions) {
        Log.i(TAG, "PERMISSION PARTIALLY GRANTED");
    }

    @Override
    public void PermissionDenied(int request_code) {
        Log.i(TAG, "PERMISSION DENIED");
    }

    @Override
    public void NeverAskAgain(int request_code) {
        Log.i(TAG, "PERMISSION NEVER ASK AGAIN");
    }

    private void checkLocationOnOff(){
        boolean gps_enabled = false, network_enabled = false;
        LocationManager lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        }catch (Exception ex){}
        try{
            network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        }catch (Exception ex){}


        if(!gps_enabled && !network_enabled){
            bld = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.CustomHoloDialog));
            bld.setTitle("Location Alert");
            bld.setMessage(getResources().getString(R.string.gps_network_not_enabled));

            bld.setPositiveButton(getResources().getString(R.string.open_location_settings), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    Intent myIntent = new Intent( Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(myIntent);
                }
            });
            bld.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    Intent intent = new Intent(suomi, StationMainActivity.class);
                    startActivity(intent);
                    finish();

                }
            });
            bld.setCancelable(true);
            bld.create().show();

        }else {
            Intent intent = new Intent(suomi, StationMainActivity.class);
            startActivity(intent);
            finish();
        }







    }

}