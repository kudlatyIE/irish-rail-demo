package ie.droidfactory.fragstations;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

import ie.droidfactory.fragstations.httputils.HttpConnect;
import ie.droidfactory.fragstations.httputils.Links;
import ie.droidfactory.fragstations.httputils.Parser;
import ie.droidfactory.fragstations.model.SortedObject;
import ie.droidfactory.fragstations.model.Sortownia;
import ie.droidfactory.fragstations.model.Station;
import ie.droidfactory.fragstations.model.StationDetails;
import ie.droidfactory.fragstations.utils.FragmentUtils;
import ie.droidfactory.fragstations.utils.RailSingleton;

/**
 * Created by kudlaty on 02/06/2016.
 */
public class StationDetailsTimetableFragment extends Fragment {

    private final static String TAG = StationDetailsTimetableFragment.class.getSimpleName();
    //	private int mCurrentPosition = -1;
    private String stationId=null;
    private int childPosition = -1;
    private String result="no result", stationCode;
    private CharSequence link;
    private TextView tvInfo;
    private ListView lv;
    private Station station;
    private ProgressDialog dialog;
    private MyAdapter adapter;
    private HashMap<String, StationDetails> timetable = null;
    private ArrayList<SortedObject> sortedByDueTime;

    public static StationDetailsTimetableFragment newInstance(Bundle args){
        StationDetailsTimetableFragment fragment = new StationDetailsTimetableFragment();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_station_details_timetable, container, false);
        return v;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onViewCreated(view, savedInstanceState);
        tvInfo = (TextView) view.findViewById(R.id.fragment_details_timetable_text_info);
        lv = (ListView) view.findViewById(R.id.fragment_details_timetable_listview);
    }


    @Override
    public void onStart() {
        // TODO Auto-generated method stub
        super.onStart();

        Bundle extras = getArguments();
        if(extras!=null) {
//			mCurrentPosition = extras.getInt(FragmentUtils.PARENT_POSITION_KEY);
            stationId = extras.getString(FragmentUtils.PARENT_POSITION_KEY);
            childPosition = extras.getInt(FragmentUtils.CHILD_POSITION_KEY);
            stationCode = extras.getString(FragmentUtils.STATION_CODE);
            Log.d(TAG, "extras:::stationCode: "+stationCode);
        }
//		if(mCurrentPosition!= -1) updateDetails(mCurrentPosition);
        if(stationId!=null) updateDetails(stationId);
        link = Links.GET_STATION_DATA_BY_STATION_CODE.getRailLink()+stationCode;
        Log.d(TAG, "async link: "+link);
        AsyncRail rail = new AsyncRail();
        if(!RailSingleton.currentStationCode.equals(stationCode)) rail.execute(link.toString());
        else {
            timetable = RailSingleton.getTimetable();
                try {
                    setList(sortedByDueTime, timetable);
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    Log.i(TAG, e.getMessage());
                }
        }

    }

    private void setList(ArrayList<SortedObject> list, HashMap<String, StationDetails> data) throws
            Exception {
        if(data==null) throw new Exception("no input data");
        else adapter = new MyAdapter(getActivity(),list,data);
        if(lv!=null) lv.setAdapter(adapter);
    }

    private void updateDetails(String id){
        this.station = RailSingleton.getStationMap().get(id);
        tvInfo.setText(TAG+" update details for:"+
                "\nchild ID: "+childPosition+
                "\nAlias: "+station.getStationAlias()+" ID: "+station.getStationCode()+
                "\nDetails: "+station.getStationDesc());
    }


    public void setStationDetails(String id){
        stationId = id;
        updateDetails(id);
    }

    private class AsyncRail extends AsyncTask<String, Void, String> {


        @Override
        protected String doInBackground(String... params) {
            String xml = HttpConnect.getRailStuff(params[0]);
            return xml;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new ProgressDialog(getActivity());
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
                timetable = Parser.parseTimetableForStation(res);
                RailSingleton.setTimetable(timetable);
                result = "stations number: "+timetable.size();
//                RailSingleton.currentStationCode = timetable.get(0).getStationCode();
                sortedByDueTime = new Sortownia().getSorteDueTime();
                setList(sortedByDueTime, timetable);
            }catch(Exception ex){
				ex.printStackTrace();
                result = ex.getMessage();
                Log.d(TAG, result);
            }
            CharSequence temp = tvInfo.getText();
            tvInfo.setText(temp+"\n"+result);
        }

    }

    private class MyAdapter extends BaseAdapter {
        Holder h;
        HashMap<String, StationDetails> mMap;
        ArrayList<SortedObject> sort;
        private LayoutInflater inflater;

        MyAdapter(Context c, ArrayList<SortedObject> sort, HashMap<String, StationDetails> map)
                throws Exception{
            if(map==null || sort==null){
                throw new Exception("station list is NULL!");
            }
            this.sort=sort;
            this.mMap=map;
            this.inflater = LayoutInflater.from(c);

        }
        @Override
        public int getCount() {
            return sort.size();
        }

        @Override
        public Object getItem(int position) {
            return sort.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v;
            if(convertView==null) {
                v = inflater.inflate(R.layout.adapter_station_timetable, parent, false);
                h = new Holder();
                h.tvTrainType = (TextView) v.findViewById(R.id.adapter_station_timetable_text_traintype);
                h.tvDestination = (TextView) v.findViewById(R.id.adapter_station_timetable_text_destination);
                h.tvTime = (TextView) v.findViewById(R.id.adapter_station_timetable_text_timeexpected);
                v.setTag(h);
            }
            else {
                v = convertView;
                h = (Holder) v.getTag();
            }

            h.tvTrainType.setText(String.format(Locale.ENGLISH,"%d %s", position, mMap.get
                    (sort.get(position).getKey()).getTrainType()));
            h.tvDestination.setText(String.format(Locale.ENGLISH,"%s", mMap.get(sort.get(position).getKey())
                    .getDestination()));
            h.tvTime.setText(String.format(Locale.ENGLISH,"%d %s", sort.get(position)
                    .getValueDecimal(),"min"));

            return v;
        }

    }

    private class Holder{
        TextView tvDestination, tvTrainType, tvTime;
    }

}
