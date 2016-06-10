package ie.droidfactory.fragstations.httputils;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.TextView;

import java.util.HashMap;

import ie.droidfactory.fragstations.model.Station;
import ie.droidfactory.fragstations.model.Train;
import ie.droidfactory.fragstations.utils.MyShared;
import ie.droidfactory.fragstations.utils.RailSingleton;
/**
 * Created by kudlaty on 02/06/2016.
 */
public class AsyncStationsList extends AsyncTask<String, Void, String>{

	public interface AsyncDoneCallback{
		void onAsyncDone(boolean done);
	}
	private AsyncDoneCallback asyncDoneCallback;

	@Override
	protected void onCancelled() {
		super.onCancelled();
		if(dialog!=null && dialog.isShowing()) dialog.dismiss();
	}
    private final static String TAG = AsyncStationsList.class.getSimpleName();
	private Context context;
	private TextView tvResult;
	private ProgressDialog dialog;
	private String result="";
	private AsyncMode mode;
	public AsyncStationsList(Context context, AsyncMode mode, AsyncDoneCallback callback, TextView
            tvResult){
		this.context=context;
		this.tvResult=tvResult;
		this.mode=mode;
        this.asyncDoneCallback = callback;
        Log.d(TAG, "async constructor:: MODE: "+mode.toString());
    }
	
	@Override
	protected String doInBackground(String... params) {
		String xml = HttpConnect.getRailStuff(params[0]);
		return xml;
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		dialog = new ProgressDialog(context);
		dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		dialog.setIndeterminate(true);
		dialog.setCancelable(true);
		dialog.setMessage("rail connection....");
		dialog.show();
		
	}

	@Override
	protected void onPostExecute(String res) {
		// TODO Auto-generated method stub
		super.onPostExecute(res);
		try{

			if(dialog!=null && dialog.isShowing()) dialog.dismiss();
			if (mode == AsyncMode.GET_ALL_STATIONS) {
				HashMap<String, Station> list;
				list = Parser.parseAllStationsMap(res);
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


		}catch(Exception ex){
			ex.printStackTrace();
			result = ex.getMessage();
		}
        asyncDoneCallback.onAsyncDone(true);
		tvResult.setText(result);
	}


	
	
}
