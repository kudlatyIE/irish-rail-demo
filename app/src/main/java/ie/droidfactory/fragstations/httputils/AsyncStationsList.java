package ie.droidfactory.fragstations.httputils;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;

import ie.droidfactory.fragstations.model.Station;
import ie.droidfactory.fragstations.model.StationDetails;
import ie.droidfactory.fragstations.model.Train;
import ie.droidfactory.fragstations.model.TrainDetails;
import ie.droidfactory.fragstations.utils.MyShared;
import ie.droidfactory.fragstations.utils.RailSingleton;
/**
 * Created by kudlaty on 02/06/2016.
 */
public class AsyncStationsList extends AsyncTask<String, Void, String> {

	public interface AsyncDoneCallback{
		void onAsyncDone(boolean done);
	}
	private AsyncDoneCallback asyncDoneCallback;


    private final static String TAG = AsyncStationsList.class.getSimpleName();
	private Context context;
//	private TextView tvResult;
	private ProgressDialog dialog;
	private String result="";
	private AsyncMode mode;
	private Links link;
//	private AsyncTaskResultCallback asyncTaskResultCallback;

	public AsyncStationsList(Context context , AsyncMode mode , AsyncDoneCallback callback /*, TextView tvResult */){
		this.context=context;
//		this.tvResult=tvResult;
		this.mode=mode;
        this.asyncDoneCallback = callback;
//		this.asyncTaskResultCallback = (AsyncTaskResultCallback) context;
        Log.d(TAG, "async :: MODE: "+mode.toString());
    }
	
	@Override
	protected String doInBackground(String... params) {
//		this.link = params[0];
		return HttpConnect.getRailStuff(params[0]);
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		if(context!=null){
			dialog = new ProgressDialog(context);
			dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			dialog.setIndeterminate(true);
			dialog.setCancelable(true);
			dialog.setMessage("connection....");
			dialog.show();
		}
		
	}

//	@Override
//	protected void onPostExecute(String res) {
//		// TODO Auto-generated method stub
//		super.onPostExecute(res);
//		try{
//			if(dialog!=null && dialog.isShowing()) dialog.dismiss();
//
//			if(link==Links.ALL_STATIONS){
//				HashMap<String, Station> list = new HashMap<>();
//				list = Parser.parseAllStationsMap(res);
//				RailSingleton.setStationMap(list);
//				MyShared.setStationsMap(context, res);
//				result = "stations number: "+list.size();
//			}
//
//			if(link==Links.GET_ALL_TRAINS){
//				HashMap<String, Train> list;
//				list = Parser.parseRunningTrains(res);
//				RailSingleton.setTrainMap(list);
//				result = "trains number: "+list.size();
//			}
//			if (link == Links.GET_TRAIN_DETAILS) {
//				HashMap<Integer, TrainDetails> list;
//				list = Parser.parseTrainDetails(res);
//				RailSingleton.setTrainDetailsMap(list);
//				result = "on route stations number: "+list.size();
//			}
//
//
//
//		}catch(Exception ex){
//			ex.printStackTrace();
//			result = ex.getMessage();
//			asyncTaskResultCallback.asyncDone(false);
//		}
//		Log.d(TAG, "async result:\n"+result);
//		RailSingleton.setAsyncResult(result);
//		asyncTaskResultCallback.asyncDone(true);
////        tvResult.setText(result);
//	}

	@Override
	protected void onPostExecute(String res) {
		// TODO Auto-generated method stub
		super.onPostExecute(res);
		boolean success = false;
//		Log.d(TAG, "XML:\n"+res);
		if(dialog!=null && dialog.isShowing()) dialog.dismiss();
		try{

			if(dialog!=null && dialog.isShowing()) dialog.dismiss();
			if (mode == AsyncMode.GET_ALL_STATIONS) {
				HashMap<String, Station> list;
				list = Parser.parseAllStationsMap(context, res);
				RailSingleton.setStationMap(list);
				MyShared.setStationsMap(context, res);
				result = "stations number: "+list.size();
			}
			if (mode == AsyncMode.GET_ALL_TRAINS) {
				HashMap<String, Train> list;
				list = Parser.parseRunningTrains(res);
				RailSingleton.setTrainMap(list);
				result = "trains number: "+list.size();
			}
			if (mode == AsyncMode.GET_TRAIN_DETAILS) {
				ArrayList<TrainDetails> list;
				list = Parser.parseTrainDetails(res);
				RailSingleton.setTrainDetailsList(list);
				result = "on route stations number: "+list.size();
			}
			if(mode==AsyncMode.GET_STATION_DETAIL){
				ArrayList<StationDetails> list;
				list = Parser.parseTimetableForStation(res);
				RailSingleton.setTimetableList(list);
				result = "trains due in number: "+list.size();
			}
			success=true;

		}catch(Exception ex){
			ex.printStackTrace();
			result = ex.getMessage();
			success=false;
		}
//		asyncTaskResultCallback.asyncDone(success);
		asyncDoneCallback.onAsyncDone(success);
		RailSingleton.setAsyncResult(result);
	}

	@Override
	protected void onCancelled() {
		super.onCancelled();
		if(dialog!=null && dialog.isShowing()) dialog.dismiss();
	}

	
}
