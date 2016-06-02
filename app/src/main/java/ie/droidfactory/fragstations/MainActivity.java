package ie.droidfactory.fragstations;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import ie.droidfactory.fragstations.httputils.Links;
import ie.droidfactory.fragstations.model.AsyncStationsList;
import ie.droidfactory.fragstations.utils.FragmentUtils;
import ie.droidfactory.fragstations.utils.MyShared;
import ie.droidfactory.fragstations.utils.PermEnum;
import ie.droidfactory.fragstations.utils.PermissionUtils;
import ie.droidfactory.fragstations.utils.RailSingleton;
/**
 * Created by kudlaty on 02/06/2016.
 */
public class MainActivity extends AppCompatActivity {

    private final static String TAG = MainActivity.class.getSimpleName();
    private String result="", link;
    private TextView tvInfo;
    private String errMsg = "";
    private boolean isPermissionGranted=false;
    private Activity suomi;

    private Button btnStation, btnTrain, btnMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        suomi = this;
        tvInfo = (TextView)findViewById(R.id.main_text_info);
//		TestUtils.makeList();
        if(MyShared.isStationMap(getApplicationContext())){
            RailSingleton.setStationMap(MyShared.getStationsMap(getApplicationContext()));
            Log.d(TAG, "get stations list from cache...");
        }else{
            link = Links.ALL_STATIONS.getRailLink();
//			AsyncRail rail = new AsyncRail();
            AsyncStationsList rail = new AsyncStationsList(this, tvInfo);
            if(RailSingleton.getStationMap()==null || RailSingleton.getStationMap().size()==1) {
                Log.d(TAG, "try download a list of stations...");
                rail.execute(link);
            }
        }


        btnStation = (Button) findViewById(R.id.main_btn_stations);
        btnMap = (Button) findViewById(R.id.main_btn_map);
        btnTrain = (Button) findViewById(R.id.main_btn_train);

        MyButtons button = new MyButtons();
        btnStation.setOnClickListener(button);
        btnMap.setOnClickListener(button);
        btnTrain.setOnClickListener(button);

    }

    @TargetApi(23)
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantedResults) {
        Log.d(TAG, "onRequestPermissionsResult....");
        Log.i(TAG, "premission: "+permissions[0]+" code: "+requestCode);

        String str = "unknown service";
        for(PermEnum en: PermEnum.values()){
            str = en.getPermissionName();
            if(requestCode!=en.getRequestCode()){
                Log.d(TAG, str+ " - got permission result: "+requestCode);
                super.onRequestPermissionsResult(requestCode, permissions, grantedResults);
                return;
            }
            if(grantedResults.length!=0 && grantedResults[0]== PackageManager.PERMISSION_GRANTED){
                Log.d(TAG, str+" - permission granted");
                return;
            }
        }

        Log.d(TAG, str+ " permission not granted: result lenght: "+grantedResults.length+" result code: "+
                (grantedResults.length>0 ? grantedResults[0] : "empty") );

        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener(){

            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("This App o_O");
        builder.setMessage("required "+str+" permission not granted. This app will be close");
        builder.setPositiveButton("OK", listener);
        builder.show();
    }


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
        boolean isPermissionGranted=false;

        @Override
        public void onClick(View v) {
            try{
                this.isPermissionGranted = PermissionUtils.checkSensiPermision(MainActivity.this);
            }catch(Exception ex){
                errMsg=ex.getMessage();
                Log.d(TAG, errMsg+ " is ready: "+isPermissionGranted);
                isPermissionGranted = false;
            }
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
//					intent = new Intent(suomi, StationMainActivity.class);
//					startActivity(intent);
                    Toast.makeText(suomi, "not yet...", Toast.LENGTH_SHORT).show();
                }
            }


        }

    }
}