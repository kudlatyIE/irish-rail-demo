package ie.droidfactory.fragstations;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;

import ie.droidfactory.fragstations.httputils.AsyncMode;
import ie.droidfactory.fragstations.httputils.AsyncStationsList;
import ie.droidfactory.fragstations.httputils.Links;
import ie.droidfactory.fragstations.model.SortedObject;
import ie.droidfactory.fragstations.model.Station;
import ie.droidfactory.fragstations.model.StationDetails;
import ie.droidfactory.fragstations.utils.FragmentUtils;
import ie.droidfactory.fragstations.utils.RailSingleton;

/**
 * Created by kudlaty on 02/06/2016.
 */
public class StationDetailsTimetableFragment extends Fragment {

    private AsyncStationsList.AsyncDoneCallback asyncDone = new AsyncStationsList.AsyncDoneCallback() {
        @Override
        public void onAsyncDone(boolean done) {
            swipeRefreshLayout.setRefreshing(false);
            if(done){
                timetableList = RailSingleton.getTimetableList();
                StationDetails.TimetableDestinationDown compare = new StationDetails
                        .TimetableDestinationDown();
                Collections.sort(timetableList, compare);
                adapter = new MyAdapter(getActivity(), R.layout.adapter_station_timetable, timetableList);
                lv.setAdapter(adapter);
            }

        }
    };

    private final static String TAG = StationDetailsTimetableFragment.class.getSimpleName();
    //	private int mCurrentPosition = -1;
    private String stationId=null;
    private int childPosition = -1;
    private String result="no result", stationCode;
    private CharSequence link;
    private TextView tvInfo;
    private EditText editSearch;
    private ImageView imgSortDestination, imgSortTime, imgCancel;
    private static ListView lv;
    private SwipeRefreshLayout swipeRefreshLayout;
    private Station station;
    private ProgressDialog dialog;
    private static MyAdapter adapter;
//    private HashMap<String, StationDetails> timetable = null;
    private static ArrayList<StationDetails> timetableList, filteredList;
//    private ArrayList<SortedObject> sortedByDueTime;
    private Sort sortMode = Sort.UNSORTED;

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
        super.onViewCreated(view, savedInstanceState);
        tvInfo = (TextView) view.findViewById(R.id.fragment_details_timetable_text_info);
        lv = (ListView) view.findViewById(R.id.fragment_details_timetable_listview);
        imgCancel = view.findViewById(R.id.fragment_details_timetable_img_cancel);
        imgSortDestination = view.findViewById(R.id.fragment_details_timetable_img_sort_destination);
        imgSortTime = view.findViewById(R.id.fragment_details_timetable_img_sort_time);
        editSearch = view.findViewById(R.id.fragment_details_timetable_edit_search);
        swipeRefreshLayout = view.findViewById(R.id.fragment_details_timetable_swipe_refresh_layout);

    }


    @Override
    public void onStart() {
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
//        AsyncRail rail = new AsyncRail();
        AsyncStationsList rail = new AsyncStationsList(getActivity(), AsyncMode
                .GET_STATION_DETAIL, asyncDone);
        if(!RailSingleton.currentStationCode.equals(stationCode)) rail.execute(link.toString());
        else {
            timetableList = RailSingleton.getTimetableList();
            try {
                sortTimetable(Sort.TIME_UP, timetableList);
            } catch (Exception e) {
                Log.i(TAG, e.getMessage());
            }
        }

        Cliker click = new Cliker();
        imgCancel.setOnClickListener(click);
        imgSortDestination.setOnClickListener(click);
        imgSortTime.setOnClickListener(click);

        editSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s!=null){
                    filteredList = new ArrayList<StationDetails>();
                    for(StationDetails station: timetableList){
                        if(station.getDestination().toLowerCase().contains(s.toString().toLowerCase())){
                            filteredList.add(station);
                        }
                    }
//                        stationList = tempList;
                    sortTimetable(sortMode, filteredList);
                    adapter = new MyAdapter(getActivity(), R.layout.adapter_stations, filteredList);
                    lv.setAdapter(adapter);

                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener(){

            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(true);
                refreshDetailsData();
            }
        });
    }

    private void refreshDetailsData(){
        link = Links.GET_STATION_DATA_BY_STATION_CODE.getRailLink()+stationCode;
        Log.d(TAG, "refresh async link: "+link);
        AsyncStationsList rail = new AsyncStationsList(getActivity(), AsyncMode
                .GET_STATION_DETAIL, asyncDone);
        rail.execute(link.toString());

    }

    private class Cliker implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            ArrayList<StationDetails> temp;
            if(editSearch.getText().toString().trim().length()>0) temp = filteredList;
            else temp = timetableList;

            switch (view.getId()){
                case R.id.fragment_details_timetable_img_cancel:
//                    stationList = allStations;
                    sortTimetable(sortMode, temp);
                    editSearch.setText("");
                    break;
                case R.id.fragment_details_timetable_img_sort_destination:
                    if(sortMode!=Sort.DESTINATION_DOWN) sortMode=Sort.DESTINATION_DOWN;
                    else sortMode=Sort.DESTINATION_UP;
                    sortTimetable(sortMode, temp);
                    break;
                case R.id.fragment_details_timetable_img_sort_time:
                    if(sortMode!=Sort.TIME_DOWN) sortMode=Sort.TIME_DOWN;
                    else sortMode=Sort.TIME_UP;
                    sortTimetable(sortMode, temp);
                    break;
            }
        }
    }

    private void sortTimetable(Sort mode, ArrayList<StationDetails> list){
        Log.d(TAG, "sorting timetable by: "+mode.name());
        if(mode==Sort.DESTINATION_UP){
            StationDetails.TimetableDestinationDown compare = new StationDetails
                    .TimetableDestinationDown();
            Collections.sort(list, compare);
            imgSortDestination.setImageDrawable(getResources().getDrawable(R.drawable.ic_sort_up));
        }
        if(mode==Sort.DESTINATION_DOWN){
            StationDetails.TimetableDestinationUp compare = new StationDetails
                    .TimetableDestinationUp();
            Collections.sort(list, compare);
            imgSortDestination.setImageDrawable(getResources().getDrawable(R.drawable
                    .ic_sort_down));
        }
        if(mode==Sort.TIME_UP){
            StationDetails.TimetableDueInTimeDown compare = new StationDetails
                    .TimetableDueInTimeDown();
            Collections.sort(list, compare);
            imgSortTime.setImageDrawable(getResources().getDrawable(R.drawable.ic_sort_up));
        }
        if(mode==Sort.TIME_DOWN){
            StationDetails.TimetableDueInTimeUp compare = new StationDetails.TimetableDueInTimeUp();
            Collections.sort(list, compare);
            imgSortTime.setImageDrawable(getResources().getDrawable(R.drawable.ic_sort_down));
        }
        setList(list);
    }

    private void setList(ArrayList<StationDetails> list) {
        if(list==null) {
            Log.d(TAG, "settings listView error::: empty list");
            return;
        } else adapter = new MyAdapter(getActivity(), R.layout.adapter_station_timetable, list);
        if(lv!=null) lv.setAdapter(adapter);
    }

//    private void setList(ArrayList<SortedObject> list, HashMap<String, StationDetails> data) throws
//            Exception {
//        if(data==null) throw new Exception("no input data");
//        else adapter = new MyAdapter(getActivity(),list,data);
//        if(lv!=null) lv.setAdapter(adapter);
//    }

    private void updateDetails(String id){ //temporary stuff
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

    private enum Sort{
        TYPE_UP, TYPE_DOWN, DESTINATION_UP, DESTINATION_DOWN, TIME_UP, TIME_DOWN, UNSORTED;
    }

//    private class AsyncRail extends AsyncTask<String, Void, String> {
//        @Override
//        protected String doInBackground(String... params) {
//            String xml = HttpConnect.getRailStuff(params[0]);
//            return xml;
//        }
//
//        @Override
//        protected void onPreExecute() {
//            super.onPreExecute();
//            dialog = new ProgressDialog(getActivity());
//            dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
//            dialog.setIndeterminate(true);
//            dialog.setCancelable(true);
//            dialog.setMessage("rail connection....");
//            dialog.show();
//
//        }
//
//        @Override
//        protected void onPostExecute(String res) {
//            // TODO Auto-generated method stub
//            super.onPostExecute(res);
//            try{
//                if(dialog!=null && dialog.isShowing()) dialog.dismiss();
//                timetable = Parser.parseTimetableForStation(res);
//                RailSingleton.setTimetable(timetable);
//                result = "stations number: "+timetable.size();
////                RailSingleton.currentStationCode = timetable.get(0).getStationCode();
//                sortedByDueTime = new Sortownia().getSorteDueTime();
//                setList(sortedByDueTime, timetable);
//            }catch(Exception ex){
//				ex.printStackTrace();
//                result = ex.getMessage();
//                Log.d(TAG, result);
//            }
//            CharSequence temp = tvInfo.getText();
//            tvInfo.setText(temp+"\n"+result);
//        }
//    }

    private class MyAdapter  extends ArrayAdapter{

        private ArrayList<StationDetails> list;
        private LayoutInflater inflater;
        Holder h;
        public MyAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull
                         ArrayList<StationDetails> list){
            super(context, resource, list);
            this.list=list;
            this.inflater = LayoutInflater.from(context);
        }
        @Override
        public int getCount() {
            return list.size();
        }

        @Nullable
        @Override
        public Object getItem(int position) {
            return list.get(position);
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

            View v;
            if(convertView==null){
                v = inflater.inflate(R.layout.adapter_station_timetable, parent, false);
                h = new Holder();
                h.tvTrainType = (TextView) v.findViewById(R.id.adapter_station_timetable_text_traintype);
                h.tvDestination = (TextView) v.findViewById(R.id.adapter_station_timetable_text_destination);
                h.tvTime = v.findViewById(R.id.adapter_station_timetable_text_timeexpected);
                v.setTag(h);
            }else{
                v = convertView;
                h = (Holder) v.getTag();
            }

            h.tvTrainType.setText(list.get(position).getTrainType());
            h.tvDestination.setText(list.get(position).getDestination());
            h.tvTime.setText(list.get(position).getDueIn());
            return v;
        }

    }

    private class MyAdapter2 extends BaseAdapter {
        Holder h;
        HashMap<String, StationDetails> mMap;
        ArrayList<SortedObject> sort;
        private LayoutInflater inflater;

        MyAdapter2(Context c, ArrayList<SortedObject> sort, HashMap<String, StationDetails> map)
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
