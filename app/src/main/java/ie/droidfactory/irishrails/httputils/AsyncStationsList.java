package ie.droidfactory.irishrails.httputils;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import ie.droidfactory.irishrails.model.RailInterface;
import ie.droidfactory.irishrails.model.Station;
import ie.droidfactory.irishrails.model.StationDetails;
import ie.droidfactory.irishrails.model.Train;
import ie.droidfactory.irishrails.model.TrainDetails;
import ie.droidfactory.irishrails.utils.MyShared;
import ie.droidfactory.irishrails.utils.RailSingleton;
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
	private ProgressDialog dialog;
	private String result="";
	private AsyncMode mode;
//	private String detailsLink;
	RailInterface tweetCallback;

	/**
	 *
	 * @param context activity context
	 * @param mode API rail mode or web mode
	 * @param callback response callback
//	 * @param detailsLink web link to station details info
	 */
	public AsyncStationsList(Context context , AsyncMode mode , AsyncDoneCallback callback/*, TextView tvResult */){
		this.context=context;
		this.mode=mode;
        this.asyncDoneCallback = callback;
//		this.detailsLink=detailsLink;
        Log.d(TAG, "async :: MODE: "+mode.toString());
    }
	
	@Override
	protected String doInBackground(String... params) {
//		this.link = params[0];
        Log.d(TAG, "async execute with: "+params[0]);
		if(mode==AsyncMode.GET_STATION_INFO_DETAILS){
			String result="async web parser...";
			RailSingleton.setInfoMap(WebParser.parseStationDetailsInfo(params[0]));
//			try {
//				Document doc = Jsoup.connect(params[0]).get();
//				result = WebParser.parseGeneralStationDetails(params[0]);

//			} catch (IOException e) {
//				e.printStackTrace();
//			}
		return result;
		}else return HttpConnect.getRailStuff(params[0]);

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

	@Override
	protected void onPostExecute(String res) {
		// TODO Auto-generated method stub
		super.onPostExecute(res);
		boolean success ;
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
			if(mode==AsyncMode.GET_STATION_INFO_DETAILS){
				//TODO: parse web info station details - jsoup
//				result = res;
//                RailSingleton.setWebStationInfo(res);
			}
			success=true;

		}catch(Exception ex){
			ex.printStackTrace();
			result = ex.getMessage();
			success=false;
		}
		asyncDoneCallback.onAsyncDone(success);
		RailSingleton.setAsyncResult(result);
	}

	@Override
	protected void onCancelled() {
		super.onCancelled();
		if(dialog!=null && dialog.isShowing()) dialog.dismiss();
	}

	
}
