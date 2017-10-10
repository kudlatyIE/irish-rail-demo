package ie.droidfactory.fragstations;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

import ie.droidfactory.fragstations.httputils.AsyncMode;
import ie.droidfactory.fragstations.httputils.AsyncStationsList;
import ie.droidfactory.fragstations.httputils.Links;
import ie.droidfactory.fragstations.utils.FragmentUtils;
import ie.droidfactory.fragstations.utils.LocationUtils;
import ie.droidfactory.fragstations.utils.PermissionResultCallback;
import ie.droidfactory.fragstations.utils.PermissionUtils;
import ie.droidfactory.fragstations.utils.RailSingleton;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        permissionUtils=new PermissionUtils(this);
        permissions.add(android.Manifest.permission.INTERNET);
        permissions.add(android.Manifest.permission.ACCESS_FINE_LOCATION);
        permissions.add(android.Manifest.permission.ACCESS_WIFI_STATE);
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
        Log.i("PERMISSION","GRANTED");
        Intent intent = new Intent(this, StationMainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void PartialPermissionGranted(int request_code, ArrayList<String> granted_permissions) {
        Log.i("PERMISSION PARTIALLY","GRANTED");
    }

    @Override
    public void PermissionDenied(int request_code) {
        Log.i("PERMISSION","DENIED");
    }

    @Override
    public void NeverAskAgain(int request_code) {
        Log.i("PERMISSION","NEVER ASK AGAIN");
    }

}