package ie.droidfactory.fragstations;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

import ie.droidfactory.fragstations.httputils.AsyncMode;
import ie.droidfactory.fragstations.httputils.Links;
import ie.droidfactory.fragstations.httputils.AsyncStationsList;
import ie.droidfactory.fragstations.utils.FragmentUtils;
import ie.droidfactory.fragstations.utils.LocationUtils;
import ie.droidfactory.fragstations.utils.MyShared;
import ie.droidfactory.fragstations.utils.PermEnum;
import ie.droidfactory.fragstations.utils.PermissionResultCallback;
import ie.droidfactory.fragstations.utils.PermissionUtils;
import ie.droidfactory.fragstations.utils.PermissionUtilsOLD;
import ie.droidfactory.fragstations.utils.RailSingleton;
/**
 * Created by kudlaty on 02/06/2016.
 */
public class MainActivity extends Activity implements
        ActivityCompat.OnRequestPermissionsResultCallback,PermissionResultCallback {

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

        permissionUtils.check_permission(permissions,"dialog context", PERMISSIONS_MULTIPLE_REQUEST );
        isPermissionGranted = true;
        //--------------------
        suomi = this;
        tvInfo = (TextView)findViewById(R.id.main_text_info);
//		TestUtils.makeList();
//        try{
//            this.isPermissionGranted = PermissionUtilsOLD.checkSensiPermision(MainActivity.this);
//        }catch(Exception ex){
//            errMsg=ex.getMessage();
//            Log.d(TAG, errMsg+ " is ready: "+isPermissionGranted);
//            isPermissionGranted = false;
//        }

//        processLocation(isPermissionGranted);
        executeStationsList(isPermissionGranted);

        btnStation = (Button) findViewById(R.id.main_btn_stations);
        btnMap = (Button) findViewById(R.id.main_btn_map);
        btnTrain = (Button) findViewById(R.id.main_btn_train);

        MyButtons button = new MyButtons();
        btnStation.setOnClickListener(button);
        btnMap.setOnClickListener(button);
        btnTrain.setOnClickListener(button);

        Intent in = new Intent(getApplicationContext(), StationMainActivity.class);
        in.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        in.putExtra(FragmentUtils.FRAGMENT, FragmentUtils.FRAGMENT_INFO);
        startActivity(in);

    }


    private void executeStationsList(boolean isPermissionGranted){
        if (isPermissionGranted){
            Log.d(TAG, "try download a list of stations...");
            LocationUtils.getLocation(this);
            AsyncStationsList rail = new AsyncStationsList(this, AsyncMode.GET_ALL_STATIONS,
                    null, tvInfo);
            rail.execute(link);
        }else {
            Log.d(TAG, "try download a list of stations... permissions not granted!");
        }
    }
    private void processLocation(boolean isPermissionGranted){
        if(isPermissionGranted){
            LocationUtils.getLocation(this);
            if(MyShared.isStationMap(getApplicationContext())){
                RailSingleton.setStationMap(MyShared.getStationsMap(getApplicationContext()));
                Log.d(TAG, "get stations list from cache...");
            }else{
                link = Links.ALL_STATIONS.getRailLink();
//			AsyncRail rail = new AsyncRail();
                AsyncStationsList rail = new AsyncStationsList(this, AsyncMode.GET_ALL_STATIONS,
                        null, tvInfo);
                if(RailSingleton.getStationMap()==null || RailSingleton.getStationMap().size()==1) {
                    Log.d(TAG, "try download a list of stations...");
                    rail.execute(link);
                }
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        // redirects to utils
        permissionUtils.onRequestPermissionsResult(requestCode,permissions,grantResults);

    }

    // Callback functions

    @Override
    public void PermissionGranted(int request_code) {
        Log.i("PERMISSION","GRANTED");
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

//    @TargetApi(23)
//    @Override
//    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantedResults) {
//        Log.d(TAG, "onRequestPermissionsResult....");
//        Log.i(TAG, "premission: "+permissions[0]+" code: "+requestCode);
//
//        String str = "unknown service";
//        for(PermEnum en: PermEnum.values()){
//            str = en.getPermissionName();
//            if(requestCode!=en.getRequestCode()){
//                Log.d(TAG, str+ " - got permission result: "+requestCode);
//                super.onRequestPermissionsResult(requestCode, permissions, grantedResults);
//                return;
//            }
//            if(grantedResults.length!=0 && grantedResults[0]== PackageManager.PERMISSION_GRANTED){
//                Log.d(TAG, str+" - permission granted");
//                return;
//            }
//        }
//
//        Log.d(TAG, str+ " permission not granted: result lenght: "+grantedResults.length+" result code: "+
//                (grantedResults.length>0 ? grantedResults[0] : "empty") );
//
//        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener(){
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                finish();
//            }
//
//        };
//
//        AlertDialog.Builder builder = new AlertDialog.Builder(suomi);
//        builder.create();
//        builder.setTitle("This App o_O");
//        builder.setMessage("required "+str+" permission not granted. This app will be close");
//        builder.setPositiveButton("OK", listener);
//
//        builder.show();
//    }


//	private class AsyncRail extends AsyncTask<String, Void, String>{
//
//
//		@Override
//		protected String doInBackground(String... params) {
//			String xml = HttpConnect.getRailStuff(params[0]);
//			return xml;
//		}
//
//		@Override
//		protected void onPreExecute() {
//			super.onPreExecute();
//			dialog = new ProgressDialog(MainActivity.this);
//			dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
//			dialog.setIndeterminate(true);
//			dialog.setCancelable(true);
//			dialog.setMessage("rail connection....");
//			dialog.show();
//
//		}
//
//		@Override
//		protected void onPostExecute(String res) {
//			// TODO Auto-generated method stub
//			super.onPostExecute(res);
//			try{
//				if(dialog!=null && dialog.isShowing()) dialog.dismiss();
////				Log.v("RAIL", "------------------------------------------");
////				Log.v("XML", "rail response:\n"+res);
//				stationList = Parser.parseAllStations(res);
//				RailSingleton.setStationList(stationList);
////				for(Station s: stationList){
////					result = result+s.toString()+"\n";
////				}
////				Log.i(TAG, result);
//				result = "stations number: "+stationList.size();
//				MyShared.setStationsList(suomi, res);
//			}catch(Exception ex){
//				ex.printStackTrace();
//				result = ex.getMessage();
//			}
//			tvInfo.setText(result);
//		}
//
//	}
//	@Override
//	protected void onDestroy(){
//		super.onDestroy();
//		if(dialog!=null && dialog.isShowing()) dialog.dismiss();
//	}

    private class MyButtons implements View.OnClickListener {

        Intent intent;
//        boolean isPermissionGranted=false;

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