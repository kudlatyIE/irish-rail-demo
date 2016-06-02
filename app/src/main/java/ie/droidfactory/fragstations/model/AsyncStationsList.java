package ie.droidfactory.fragstations.model;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.TextView;

import java.util.HashMap;

import ie.droidfactory.fragstations.httputils.HttpConnect;
import ie.droidfactory.fragstations.httputils.Parser;
import ie.droidfactory.fragstations.utils.MyShared;
import ie.droidfactory.fragstations.utils.RailSingleton;
/**
 * Created by kudlaty on 02/06/2016.
 */
public class AsyncStationsList extends AsyncTask<String, Void, String>{

	@Override
	protected void onCancelled() {
		super.onCancelled();
		if(dialog!=null && dialog.isShowing()) dialog.dismiss();
	}

	private Context context;
	private TextView tvResult;
	private ProgressDialog dialog;
	private String result="";
	
	public AsyncStationsList(Context context, TextView tvResult){
		this.context=context;
		this.tvResult=tvResult;
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
			HashMap<String, Station> list = new HashMap<>();
			if(dialog!=null && dialog.isShowing()) dialog.dismiss();

			list = Parser.parseAllStationsMap(res);
			RailSingleton.setStationMap(list);

			result = "stations number: "+list.size();
			MyShared.setStationsMap(context, res);
		}catch(Exception ex){
			ex.printStackTrace();
			result = ex.getMessage();
		}
		tvResult.setText(result);
	}
	
	
}
