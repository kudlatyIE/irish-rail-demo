package ie.droidfactory.fragstations;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.TreeSet;

import ie.droidfactory.fragstations.httputils.AsyncMode;
import ie.droidfactory.fragstations.httputils.AsyncStationsList;
import ie.droidfactory.fragstations.httputils.Links;
import ie.droidfactory.fragstations.model.RailInterface;
import ie.droidfactory.fragstations.model.Train;
import ie.droidfactory.fragstations.model.TrainDetails;
import ie.droidfactory.fragstations.utils.DataUtils;
import ie.droidfactory.fragstations.utils.FragmentUtils;
import ie.droidfactory.fragstations.utils.RailSingleton;

/**
 * Created by kudlaty on 10/06/2016.
 */
public class TrainDetailsFragment extends MainFragment {

    RailInterface stationFromTrainCallback;

    private AsyncStationsList.AsyncDoneCallback asyncDone = new AsyncStationsList
            .AsyncDoneCallback() {
        @Override
        public void onAsyncDone(boolean done) {
            if (done) {
                if(trainCode!= null) updateDetails(trainCode, msg);
                Log.d(TAG, "onAsyncDone() callback, create map");
                Log.d(TAG, "onAsyncDone() callback, map size: "+RailSingleton.getTrainDetailsMap
                        ().size());
                createDetailsList(FRAGMENT.CREATE);
            }
        }
    };

    private final static String TAG = TrainDetailsFragment.class.getSimpleName();
    private enum FRAGMENT{CREATE, REFRESH};
    private String trainCode, trainName, trainDirection, link, msg="";
    private double lat, lng;
    private TextView tvInfo;
    private ListView lv;
    private Train train;
    private ArrayList<Integer> mlist;
    private MyAdapter adapter;
    private int stampNow, stampOld;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_train_details, container, false);
        return v;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onViewCreated(view, savedInstanceState);
        tvInfo = (TextView) view.findViewById(R.id.fragment_train_details_info);
        lv = (ListView) view.findViewById(R.id.fragment_train_details_listview);
    }

    @Override
    public void onStart() {
        // TODO Auto-generated method stub
        super.onStart();
//        stampNow = new Date().getSeconds();
//        if(RailSingleton.getTimeStampStationDetails()!=0){
//            stampOld = RailSingleton.getTimeStampStationDetails();
//            long diff = (stampNow-stampOld)/60000;
//            if(diff>60){
//                //TODO: load new timetable
//            }else{
//                //TODO: load data from singleton
//            }
//        }else RailSingleton.setTimeStampStationDetails(new Date().getSeconds());//and load timetable

        Bundle extras = getArguments();
        if(extras!=null) {
            trainCode = extras.getString(FragmentUtils.STATION_CODE);
            lat = extras.getDouble(FragmentUtils.STATION_LAT);
            lng = extras.getDouble(FragmentUtils.STATION_LONG);
            trainDirection = extras.getString(FragmentUtils.TRAIN_DESCRIPTION);
            msg = extras.getString(FragmentUtils.TRAIN_MSG);
            Log.d(TAG, "from bundle: trainCode: "+trainCode+", direction: "+trainDirection+"\nLAT:"
            +lat+" LNG:" + lng);
        }
        if(RailSingleton.getTimetable()!=null){
            if(trainCode!= null) updateDetails(trainCode, msg);
            Log.d(TAG, "onCreate() get map from singleton size: "+RailSingleton.getTrainDetailsMap
                    ().size());
            createDetailsList(FRAGMENT.CREATE);
        }else{
            try {
                link = Links.GET_TRAIN_DETAILS.getTrainDetailsLink(trainCode, DataUtils
                        .getFormatedDate(null));// return train route details for today
                AsyncStationsList async = new AsyncStationsList(getActivity(), AsyncMode
                        .GET_TRAIN_DETAILS, asyncDone, null);
                async.execute(link);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    }

    private void updateDetails(String id, String msg){
        Log.d(TAG, "updateDetails arg: "+msg);
        this.train = RailSingleton.getTrainMap().get(id);
        Log.d(TAG, "train is NULL: "+(train==null));
//        tvInfo.setText(train.getTrainCode()+":\n"+train.getPublicMessage());
        tvInfo.setText(msg);
    }

    private void createDetailsList(FRAGMENT todo){
//        updateDetails(trainCode, msg);
        if(lv==null) return;
        if(todo==FRAGMENT.CREATE){
            if(RailSingleton.getTrainDetailsMap()!=null){
                try {
                    adapter = new MyAdapter(getActivity(), RailSingleton.getTrainDetailsMap());
                    lv.setAdapter(adapter);
                    lv.setOnItemLongClickListener(new OnItemLongClickListener() {
                        @Override
                        public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                            Toast.makeText(getActivity(),"click on:\n"+RailSingleton.getTrainDetailsMap()
                                    .get(mlist.get(position)).getLocationFullName(), Toast.LENGTH_SHORT ).show();
                            return false;
                        }
                    });
                    lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            stationFromTrainCallback.onStationSelectedFromTrain(RailSingleton
                                    .getTrainDetailsMap().get(mlist.get(position)).getLocationCode());
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
    @Override
    public void onAttach(Activity activity) {
        // TODO Auto-generated method stub
        super.onAttach(activity);
        try{
            stationFromTrainCallback = (RailInterface) activity;
        }catch(ClassCastException e){
            throw new ClassCastException(activity.toString()+ "OnStationFromTrainSelected " +
                    "Listener is not implemented...");
        }
        Log.d(TAG, "onAttach end...");
    }

    private class MyAdapter extends BaseAdapter{

        Holder h;
        ArrayList<Integer> list;
        HashMap<Integer, TrainDetails> mMap;
        private LayoutInflater inflater;

        MyAdapter(Context c, HashMap<Integer, TrainDetails> map) throws Exception {
            if(map==null){
                throw new Exception("station list is NULL!");
            }
            this.mMap=map;
            this.list = new ArrayList<>();
            for(Integer key: new TreeSet<>(map.keySet())){
                list.add(key);
            }
            if(list!=null) mlist = list;
            Log.d(TAG, "for this route stations number: "+list.size());
            this.inflater = LayoutInflater.from(c);

        }
        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            return mMap.get(list.get(position));
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v;
            if(convertView==null){
                v = inflater.inflate(R.layout.adapter_train_details, parent, false);
                h = new Holder();
                h.tvArrival = (TextView) v.findViewById(R.id.adapter_train_details_arrival);
                h.tvLocation = (TextView) v.findViewById(R.id.adapter_train_details_location);
                h.tvDeparture = (TextView) v.findViewById(R.id.adapter_train_details_depart);
                v.setTag(h);
            }else {
                v = convertView;
                h = (Holder) v.getTag();
            }
            //stuff belowe doesnt work: empty rows are still visible
//            if(!mMap.get(list.get(position)).getLocationType().equals(StationType.TYPE_T.getType())){
//                v.setVisibility(View.VISIBLE);
//                h.tvArrival.setText(mMap.get(list.get(position)).getArrival());
//                h.tvLocation.setText(mMap.get(list.get(position)).getLocationFullName());
//                h.tvDeparture.setText(mMap.get(list.get(position)).getDeparture());
//                return v;
//            }else v.setVisibility(View.GONE);
            h.tvArrival.setText(mMap.get(list.get(position)).getArrival());
            h.tvLocation.setText(String.format(Locale.ENGLISH, "%s %s",
                    mMap.get(list.get(position)).getLocationCode(),
//                    mMap.get(list.get(position)).getLocationType(),
                    mMap.get(list.get(position)).getLocationFullName()));
            h.tvDeparture.setText(mMap.get(list.get(position)).getDeparture());
            return v;

        }
    }
    private class Holder{
        TextView tvArrival, tvLocation, tvDeparture;
    }

}
