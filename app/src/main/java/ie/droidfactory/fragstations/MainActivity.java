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
import ie.droidfactory.fragstations.utils.AsyncTaskResultCallback;
import ie.droidfactory.fragstations.utils.FragmentUtils;
import ie.droidfactory.fragstations.utils.LocationUtils;
import ie.droidfactory.fragstations.utils.PermissionResultCallback;
import ie.droidfactory.fragstations.utils.PermissionUtils;
import ie.droidfactory.fragstations.utils.RailSingleton;
/**
 * Created by kudlaty on 02/06/2016.
 */
public class MainActivity extends Activity implements
        ActivityCompat.OnRequestPermissionsResultCallback,PermissionResultCallback /*, AsyncTaskResultCallback */{

    private AsyncStationsList.AsyncDoneCallback asyncDone = new AsyncStationsList
            .AsyncDoneCallback() {
        @Override
        public void onAsyncDone(boolean done) {
            Log.d(TAG, "async success: "+done);
            Log.d(TAG, "async result: "+RailSingleton.getAsyncResult());
            if(done){
                Intent in = new Intent(getApplicationContext(), StationMainActivity.class);
                in.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                in.putExtra(FragmentUtils.FRAGMENT, FragmentUtils.FRAGMENT_INFO);
                startActivity(in);
            }
        }
    };

    private final static String TAG = MainActivity.class.getSimpleName();
    public  static final int PERMISSIONS_MULTIPLE_REQUEST = 123;
    private String result="", link;
    private TextView tvInfo;
    private String errMsg = "";
    private boolean isPermissionGranted=false;
    private ArrayList<String> permissions=new ArrayList<>();
    private PermissionUtils permissionUtils;
    public Activity suomi;

    private Button btnStation, btnTrain, btnMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //--------------------------
        permissionUtils=new PermissionUtils(this);
        permissions.add(android.Manifest.permission.INTERNET);
        permissions.add(android.Manifest.permission.ACCESS_FINE_LOCATION);
        permissions.add(android.Manifest.permission.ACCESS_WIFI_STATE);

        isPermissionGranted = true;
        permissionUtils.check_permission(permissions,"dialog context", PERMISSIONS_MULTIPLE_REQUEST );

        //--------------------
        suomi = this;
        tvInfo = (TextView)findViewById(R.id.main_text_info);

        btnStation = (Button) findViewById(R.id.main_btn_stations);
        btnMap = (Button) findViewById(R.id.main_btn_map);
        btnTrain = (Button) findViewById(R.id.main_btn_train);

        MyButtons button = new MyButtons();
        btnStation.setOnClickListener(button);
        btnMap.setOnClickListener(button);
        btnTrain.setOnClickListener(button);


    }


    private void executeStationsList(boolean isPermissionGranted){
        if (isPermissionGranted){
            Log.d(TAG, "try download a list of stations...");
            LocationUtils.getLocation(this);
            String temp = Links.ALL_STATIONS.getRailLink();
            AsyncStationsList rail = new AsyncStationsList(this, AsyncMode.GET_ALL_STATIONS, asyncDone);
            rail.execute(temp);
        }else {
            Log.d(TAG, "try download a list of stations... permissions not granted!");
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        // redirects to utils
        Log.d(TAG, "onRequestPermissionsResult, request code: "+requestCode);
        permissionUtils.onRequestPermissionsResult(requestCode,permissions,grantResults);
    }

    // Callback functions

    @Override
    public void PermissionGranted(int request_code) {
        Log.i("PERMISSION","GRANTED");
        isPermissionGranted = true;
        executeStationsList(isPermissionGranted);
    }

    @Override
    public void PartialPermissionGranted(int request_code, ArrayList<String> granted_permissions) {
        Log.i("PERMISSION PARTIALLY","GRANTED");
        isPermissionGranted = false;
    }

    @Override
    public void PermissionDenied(int request_code) {
        Log.i("PERMISSION","DENIED");
        isPermissionGranted = false;
    }

    @Override
    public void NeverAskAgain(int request_code) {
        Log.i("PERMISSION","NEVER ASK AGAIN");
    }

//    @Override
//    public void asyncDone(boolean done) {
//        Log.d(TAG, "async success: "+done);
//        Log.d(TAG, "async result: "+RailSingleton.getAsyncResult());
//        if(done){
//            Intent in = new Intent(getApplicationContext(), StationMainActivity.class);
//            in.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//            in.putExtra(FragmentUtils.FRAGMENT, FragmentUtils.FRAGMENT_INFO);
//            startActivity(in);
//        }
//    }


    private class MyButtons implements View.OnClickListener {

        Intent intent;
        @Override
        public void onClick(View v) {

            if(isPermissionGranted){
                if(v.getId()==R.id.main_btn_stations){
                    intent = new Intent(suomi, StationMainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.putExtra(FragmentUtils.FRAGMENT, FragmentUtils.FRAGMENT_LIST);
                    startActivity(intent);
                }
                if(v.getId()==R.id.main_btn_map){
                    intent = new Intent(suomi, StationMainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.putExtra(FragmentUtils.FRAGMENT, FragmentUtils.FRAGMENT_MAP);
                    startActivity(intent);
                }
                if(v.getId()==R.id.main_btn_train){
					intent = new Intent(suomi, StationMainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.putExtra(FragmentUtils.FRAGMENT, FragmentUtils.FRAGMENT_INFO);
					startActivity(intent);
//                    Toast.makeText(suomi, "not yet...", Toast.LENGTH_SHORT).show();
                }
            }


        }

    }
}