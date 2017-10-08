package ie.droidfactory.fragstations;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;

import ie.droidfactory.fragstations.httputils.AsyncMode;
import ie.droidfactory.fragstations.httputils.AsyncStationsList;
import ie.droidfactory.fragstations.httputils.Links;
import ie.droidfactory.fragstations.model.RailInterface;
import ie.droidfactory.fragstations.model.StationType;
import ie.droidfactory.fragstations.model.Train;
import ie.droidfactory.fragstations.model.TrainDetails;
import ie.droidfactory.fragstations.utils.DataUtils;
import ie.droidfactory.fragstations.utils.FragmentUtils;
import ie.droidfactory.fragstations.utils.RailSingleton;

/**
 * Created by kudlaty on 10/06/2016.
 */
public class TrainDetailsFragment extends MainFragment /*implements AsyncTaskResultCallback */{

    RailInterface stationFromTrainCallback;

    private AsyncStationsList.AsyncDoneCallback asyncDone = new AsyncStationsList
            .AsyncDoneCallback() {
        @Override
        public void onAsyncDone(boolean done) {
            swipeRefreshLayout.setRefreshing(false);
            if (done) {
                if(trainCode!= null) updateDetails(trainCode, msg);
                trainDetailsList = RailSingleton.getTrainDetailsList();
                Log.d(TAG, "onAsyncDone() callback, create map");
                Log.d(TAG, "onAsyncDone() callback, map size: "+trainDetailsList.size());
                TrainDetails.TranLocationOrderCompareUp compareUp = new TrainDetails.TranLocationOrderCompareUp();
                Collections.sort(trainDetailsList, compareUp);
                createDetailsList(FRAGMENT.CREATE);
            }else {
                tvInfo.setText(RailSingleton.getAsyncResult());
            }

        }
    };

    private final static String TAG = TrainDetailsFragment.class.getSimpleName();


    private enum FRAGMENT{CREATE, REFRESH};
    private String trainCode, trainName, trainDirection, link, msg="";
    private double lat, lng;
    private TextView tvInfo;
    private ListView lv;
    private SwipeRefreshLayout swipeRefreshLayout;
    private Train train;
    private ArrayList<TrainDetails> trainDetailsList;
    private MyAdapter adapter;
    private int trainCurrentPosition;
    boolean stop = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_train_details, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onViewCreated(view, savedInstanceState);
        tvInfo = (TextView) view.findViewById(R.id.fragment_train_details_info);
        lv = (ListView) view.findViewById(R.id.fragment_train_details_listview);
        swipeRefreshLayout = view.findViewById(R.id.fragment_train_details_swipe_refresh_layout);
    }


    @Override
    public void onStart() {
        // TODO Auto-generated method stub
        super.onStart();

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
//            Log.d(TAG, "onCreate() get map from singleton size: "+RailSingleton.getTrainDetailsList().size());
            createDetailsList(FRAGMENT.CREATE);
        }else{
            onRefreshListView();
        }
        //TODO: swipe refresh!
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(true);
                onRefreshListView();
            }
        });

//        if(lv!=null){
//            lv.post(new Runnable() {
//                @Override
//                public void run() {
//                    Log.d(TAG, "runnable ::: trainCurrentPosition: "+trainCurrentPosition);
//                    lv.smoothScrollToPosition(trainCurrentPosition);
//                    lv.setSelection(trainCurrentPosition);
//                }
//            });
            //-------------OR-----------
//            final Handler handler = new Handler();
////100ms wait to scroll to item after applying changes
//            handler.postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    Log.d(TAG, "runnable ::: trainCurrentPosition: "+trainCurrentPosition);
//                    lv.smoothScrollToPosition(trainCurrentPosition);
//                }}, 2000);
//        }else Log.d(TAG, "listView is NULL");

    }

    private void scrollList(int position){
        Log.d(TAG, "scrollList ::: trainCurrentPosition: "+position);
//        if(lv!=null && position!=0) {
//            lv.smoothScrollToPosition(position);
//            lv.setSelection(position);
//        }
        lv.post(new Runnable() {
                @Override
                public void run() {
                    Log.d(TAG, "runnable ::: trainCurrentPosition: "+trainCurrentPosition);
                    lv.smoothScrollToPosition(trainCurrentPosition);
                    lv.setSelection(trainCurrentPosition);
                }
            });
    }

    private void onRefreshListView(){
        try {
            link = Links.GET_TRAIN_DETAILS.getTrainDetailsLink(trainCode, DataUtils
                    .getFormatedDate(null));// return train route details for today
            AsyncStationsList async = new AsyncStationsList(getActivity(), AsyncMode.GET_TRAIN_DETAILS, asyncDone);
            async.execute(link);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        // TODO Auto-generated method stub
        super.onSaveInstanceState(outState);
        outState.putString(FragmentUtils.STATION_CODE, trainCode);
        outState.putDouble(FragmentUtils.MY_LAT, lat);
        outState.putDouble(FragmentUtils.MY_LNG, lng);
    }
    @Override
    public void onViewStateRestored(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onViewStateRestored(savedInstanceState);
        restore(savedInstanceState);
    }

    public void restore(Bundle savedInstanceState) {
        if(savedInstanceState!=null){
            lat = savedInstanceState.getDouble(FragmentUtils.MY_LAT);
            lng = savedInstanceState.getDouble(FragmentUtils.MY_LNG);
            trainCode = savedInstanceState.getString(FragmentUtils.STATION_CODE);
        }
    }

    private void setCurrentPosition(ArrayList<TrainDetails> list){
        String stationDeparted=null;
            if(stop) return;
            for(int i =0; i<list.size(); i++) {
                if (list.get(i).getDeparture().length() > 0) {
                    stationDeparted = list.get(i).getLocationCode();
                    Log.d(TAG, "order: " + i + " ::::: " + list.get(i).getLocationCode()+" passed position:::::: " + i);
                }
                try{
                    if(stationDeparted!=null){
                        if(list.get(i+1).getDeparture().length()==0){
                            this.trainCurrentPosition = i;
                            Log.d(TAG, "found train current position:::::: "+i);
                            stop=true;
                            return;
                        }
                    }
                }catch (ArrayIndexOutOfBoundsException e){
                    this.trainCurrentPosition = i;
                    Log.d(TAG, "found train current position:::::: "+i);
                    stop=true;
                }
            }


    }

    private void updateDetails(String id, String msg){
        Log.d(TAG, "updateDetails arg: "+msg);

        if(RailSingleton.getTrainMap()==null){
            try {
                link = Links.GET_TRAIN_DETAILS.getTrainDetailsLink(trainCode, DataUtils
                        .getFormatedDate(null));// return train route details for today
                AsyncStationsList async = new AsyncStationsList(getActivity(), AsyncMode.GET_TRAIN_DETAILS, asyncDone);
                async.execute(link);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }else this.train = RailSingleton.getTrainMap().get(id);
        Log.d(TAG, "train is NULL: "+(train==null));
        if(msg!=null)tvInfo.setText(msg.replace("\\n", System.getProperty("line.separator")));
    }

    private void createDetailsList(FRAGMENT todo){
//        updateDetails(trainCode, msg);
        if(lv==null) return;
        if(todo==FRAGMENT.CREATE){
            if(RailSingleton.getTrainDetailsList()!=null){
                trainDetailsList = RailSingleton.getTrainDetailsList();
//                if(trainDetailsList==null){
//                    Toast.makeText(getActivity(), "train detail list is NULL!", Toast.LENGTH_SHORT).show();
//                    return;
//                }
                TrainDetails.TranLocationOrderCompareUp compareUp = new TrainDetails.TranLocationOrderCompareUp();
                Collections.sort(trainDetailsList, compareUp);
                setCurrentPosition(trainDetailsList);
                try {
                    adapter = new MyAdapter(getActivity(), trainDetailsList);
                    lv.setAdapter(adapter);
                    lv.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
                        @Override
                        public void onLayoutChange(View view, int i, int i1, int i2, int i3, int i4, int i5, int i6, int i7) {
                            lv.removeOnLayoutChangeListener(this);
                            scrollList(trainCurrentPosition);
                        }
                    });
                    lv.setOnItemLongClickListener(new OnItemLongClickListener() {
                        @Override
                        public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
//                            Toast.makeText(getActivity(),"click on:\n"+trainDetailsList.get(position).getLocationFullName(), Toast.LENGTH_SHORT ).show();
                            return false;
                        }
                    });
                    lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            //TODO: is complicate on landscape tablet - kepp it for next time.
//                            stationFromTrainCallback.onStationSelectedFromTrain(trainDetailsList.get(position).getLocationCode());
                            String time;
                            if(trainDetailsList.get(position).getArrival().length()>0) time = "\ntrain arrived at "+trainDetailsList.get(position).getArrival();
                            else time="\ntrain is expected at "+trainDetailsList.get(position).getScheduledArrival();
                            Toast.makeText(getActivity(), trainDetailsList.get(position).getLocationFullName().concat(time), Toast.LENGTH_SHORT ).show();
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
        ArrayList<TrainDetails> list;
        String timeOrigin;

        private LayoutInflater inflater;

        MyAdapter(Context c, ArrayList<TrainDetails> list) throws Exception {
            if(list==null){
                throw new Exception("station list is NULL!");
            }else {
                for(TrainDetails td: list){
                    if(td.getLocationOrder()==1) timeOrigin=td.getExpectedDeparture();
                }
            }
            this.list=list;
            Log.d(TAG, "for this route stations number: "+list.size());
            this.inflater = LayoutInflater.from(c);

        }
        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            return list.get(position);
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
                h.imgTrainMarker = v.findViewById(R.id.adapter_train_details_img_marker);
                v.setTag(h);
            }else {
                v = convertView;
                h = (Holder) v.getTag();
            }
            TrainDetails train = list.get(position);
            String arrivaArriva = train.getArrival();
            if(arrivaArriva.length()==0) {
                if(train.getLocationOrder()==1){
                    arrivaArriva = "departure: "+train.getExpectedDeparture();
                }else arrivaArriva = train.getScheduledArrival();
            }
            h.tvArrival.setText(arrivaArriva);
            h.tvLocation.setText(train.getLocationFullName());
            h.imgTrainMarker.setImageDrawable(getTrainMarker(train, timeOrigin));
//            setCurrentPosition(train, position);
            return v;

        }

    }


    private Drawable getTrainMarker(TrainDetails train, String timeOrigin){
        Log.d(TAG, "getTrainMarker::: stopTYpe: "+train.getStopType());
//        if(train.getLocationType().equals(StationType.TYPE_O.getType()) & train.getDeparture().length()==0)
//            return getResources().getDrawable(R.drawable.ic_train_marker_start_not);
        if(train.getLocationOrder()==1 & !DataUtils.compareTrainDateNow(train.getTrainDate(), timeOrigin, train.getExpectedDeparture(), 1))//train.getDeparture().length()==0)
            return getResources().getDrawable(R.drawable.ic_train_marker_start_not);

//        if(train.getLocationType().equals(StationType.TYPE_O.getType()) & train.getDeparture().length()>0)
//            return getResources().getDrawable(R.drawable.ic_train_marker_start_departed);
        if(train.getLocationOrder()==1 & DataUtils.compareTrainDateNow(train.getTrainDate(), timeOrigin, train.getExpectedDeparture(), 1))//train.getDeparture().length()>0)
            return getResources().getDrawable(R.drawable.ic_train_marker_start_departed);
//        if(train.getLocationOrder()>1&& (
//                train.getLocationType().equals(StationType.TYPE_S.getType())
//                        || train.getLocationType().equals(StationType.TYPE_C.getType())
////                || train.getLocationType().equals(StationType.TYPE_T.getType())
//        ) & train.getArrival().length()==0 &(DataUtils.compareTrainDateNow(train.getTrainDate(), timeOrigin, train.getExpectedDeparture(), 1)))
//            return getResources().getDrawable(R.drawable.ic_train_marker_arrived);
        if(train.getLocationOrder()>1&&(
                    train.getLocationType().equals(StationType.TYPE_S.getType())
                || train.getLocationType().equals(StationType.TYPE_C.getType())
//                || train.getLocationType().equals(StationType.TYPE_T.getType())
        )
                & train.getArrival().length()>0 & train.getDeparture().length()==0)
            return  getResources().getDrawable(R.drawable.ic_train_marker_arrived);
        if(train.getLocationType().equals(StationType.TYPE_S.getType()) & (train.getDeparture().length()>0
                | (DataUtils.compareTrainDateNow(train.getTrainDate(), timeOrigin, train.getExpectedArrival(), 0))))
            return getResources().getDrawable(R.drawable.ic_train_marker_departed);
        if(train.getLocationType().equals(StationType.TYPE_D.getType()) & (train.getArrival().length()==0
                | (DataUtils.compareTrainDateNow(train.getTrainDate(), timeOrigin, train.getExpectedDeparture(), 0))))
            return getResources().getDrawable(R.drawable.ic_train_marker_destination_not);
        if(train.getLocationType().equals(StationType.TYPE_D.getType()) & (train.getArrival().length()>0
                | (DataUtils.compareTrainDateNow(train.getTrainDate(), timeOrigin, train.getExpectedArrival(), 1))))
            return getResources().getDrawable(R.drawable.ic_train_marker_destination_completed);

        return getResources().getDrawable(R.drawable.ic_train_marker_empty);
    }
    private class Holder{
        TextView tvArrival, tvLocation; //, tvDeparture;
        ImageView imgTrainMarker;
    }

}
