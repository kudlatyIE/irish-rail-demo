package ie.droidfactory.irishrails;

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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ie.droidfactory.irishrails.db.DbRailSource;
import ie.droidfactory.irishrails.httputils.AsyncMode;
import ie.droidfactory.irishrails.httputils.AsyncStationsList;
import ie.droidfactory.irishrails.httputils.Links;
import ie.droidfactory.irishrails.model.Station;
import ie.droidfactory.irishrails.model.StationDetails;
import ie.droidfactory.irishrails.utils.FragmentUtils;
import ie.droidfactory.irishrails.utils.RailSingleton;

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

                sortTimetable(Sort.TIME_UP, timetableList);
                adapter = new MyAdapter(getActivity(), R.layout.adapter_station_timetable, timetableList);
                lv.setAdapter(adapter);
            }

        }
    };

    private final static String TAG = StationDetailsTimetableFragment.class.getSimpleName();
    private String stationId=null;
    private String result="no result", stationCode;
    private CharSequence link;
    private TextView tvInfo;
    private EditText editSearch;
    private ImageView imgSortDestination, imgSortTime, imgCancel, imgFav;
    private LinearLayout llDestination, llDueIn;
    private ListView lv;
    private SwipeRefreshLayout swipeRefreshLayout;
    private Station station;
    private static MyAdapter adapter;
//    private HashMap<String, StationDetails> timetable = null;
    private static ArrayList<StationDetails> timetableList, filteredList;
    private List<String> favList;
    private Sort sortMode = Sort.UNSORTED;

    public static StationDetailsTimetableFragment newInstance(Bundle args){
        StationDetailsTimetableFragment fragment = new StationDetailsTimetableFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_station_details_timetable, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        tvInfo = view.findViewById(R.id.fragment_details_timetable_text_info);
        lv = view.findViewById(R.id.fragment_details_timetable_listview);
        imgCancel = view.findViewById(R.id.fragment_details_timetable_img_cancel);
        imgSortDestination = view.findViewById(R.id.fragment_details_timetable_img_sort_destination);
        imgSortTime = view.findViewById(R.id.fragment_details_timetable_img_sort_time);
        imgFav = view.findViewById(R.id.fragment_details_timetable_img_fav);
        llDestination = view.findViewById(R.id.fragment_details_timetable_header_destination);
        llDueIn = view.findViewById(R.id.fragment_details_timetable_header_due_in);
        editSearch = view.findViewById(R.id.fragment_details_timetable_edit_search);
        swipeRefreshLayout = view.findViewById(R.id.fragment_details_timetable_swipe_refresh_layout);

        editSearch.setVisibility(View.GONE);
        imgCancel.setVisibility(View.GONE);

    }


    @Override
    public void onStart() {
        super.onStart();

        //TODO: load favorities here!!!!!!
        DbRailSource dbRailSource = new DbRailSource(getActivity());
        dbRailSource.open();
        favList = dbRailSource.getFavoritiesStationIds();
        dbRailSource.close();
        Bundle extras = getArguments();
        if(extras!=null) {
            stationId = extras.getString(FragmentUtils.PARENT_POSITION_KEY);
            stationCode = extras.getString(FragmentUtils.STATION_CODE);
            Log.d(TAG, "extras:::stationCode: "+stationCode);
        }
        if(stationId!=null) updateDetails(stationId, favList);
        link = Links.GET_STATION_DATA_BY_STATION_CODE.getRailLink()+stationCode;
        Log.d(TAG, "async link: "+link);
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
        llDueIn.setOnClickListener(click);
        llDestination.setOnClickListener(click);
        imgFav.setOnClickListener(click);

        //disable search -  is not really useful here
        editSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s!=null){
                    filteredList = new ArrayList<>();
                    for(StationDetails station: timetableList){
                        if(station.getDestination().toLowerCase().contains(s.toString().toLowerCase())){
                            filteredList.add(station);
                        }
                    }
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
                case R.id.fragment_details_timetable_header_destination:
                    if(sortMode!=Sort.DESTINATION_DOWN) sortMode=Sort.DESTINATION_DOWN;
                    else sortMode=Sort.DESTINATION_UP;
                    sortTimetable(sortMode, temp);
                    break;
                case R.id.fragment_details_timetable_img_sort_time:
                    if(sortMode!=Sort.TIME_DOWN) sortMode=Sort.TIME_DOWN;
                    else sortMode=Sort.TIME_UP;
                    sortTimetable(sortMode, temp);
                    break;
                case R.id.fragment_details_timetable_header_due_in:
                    if(sortMode!=Sort.TIME_DOWN) sortMode=Sort.TIME_DOWN;
                    else sortMode=Sort.TIME_UP;
                    sortTimetable(sortMode, temp);
                    break;
                case R.id.fragment_details_timetable_img_fav:
                    //TODO: add/remove Fav......
                    updateFavoriteStation(favList, stationId);
                    break;
            }
        }
    }

    private void updateFavoriteStation(List<String > list, String id){
        //TODO: add/remove
        DbRailSource dbRailSource = new DbRailSource(getActivity());
        dbRailSource.open();
        favList = dbRailSource.getFavoritiesStationIds();

        if(list!=null){
            //remove Fav
            if(list.contains(id)){
                list.remove(id);
                dbRailSource.deleteFavId(id);
                imgFav.setImageDrawable(getResources().getDrawable(R.drawable.ic_favorit_no));
            }else {
                //add Fav
                list.add(id);
                dbRailSource.addFavStation(id);
                imgFav.setImageDrawable(getResources().getDrawable(R.drawable.ic_favorit));
            }
        }else {
            //TODO: just add first Fav
            list = new ArrayList<>();
            list.add(id);
            dbRailSource.addFavStation(id);
            imgFav.setImageDrawable(getResources().getDrawable(R.drawable.ic_favorit));
        }
        dbRailSource.close();
    }


    private void sortTimetable(Sort mode, ArrayList<StationDetails> list){
        Log.d(TAG, "sorting timetable by: "+mode.name());
        if(list==null) return;
        if(mode==Sort.DESTINATION_UP){
            StationDetails.TimetableDestinationDown compare = new StationDetails
                    .TimetableDestinationDown();
            Collections.sort(list, compare);
            imgSortDestination.setImageDrawable(getResources().getDrawable(R.drawable
                    .ic_sort_up));
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
            imgSortTime.setImageDrawable(getResources().getDrawable(R.drawable.ic_sort_down));
        }
        if(mode==Sort.TIME_DOWN){
            StationDetails.TimetableDueInTimeUp compare = new StationDetails.TimetableDueInTimeUp();
            Collections.sort(list, compare);
            imgSortTime.setImageDrawable(getResources().getDrawable(R.drawable.ic_sort_up));
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

    private void updateDetails(String id, List<String> list){ //temporary stuff
        this.station = RailSingleton.getStationMap().get(id);
//        tvInfo.setText(TAG+" update details for:"+
//                "\nchild ID: "+childPosition+
//                "\nAlias: "+station.getStationAlias()+" ID: "+station.getStationCode()+
//                "\nDetails: "+station.getStationDesc());
        tvInfo.setText(station.getStationDesc());
        if(list!=null){
            if(list.contains(id)) imgFav.setImageDrawable(getResources().getDrawable(R.drawable.ic_favorit));
        }
    }

    private enum Sort{
        DESTINATION_UP, DESTINATION_DOWN, TIME_UP, TIME_DOWN, UNSORTED;
    }

    private class MyAdapter  extends ArrayAdapter{

        private ArrayList<StationDetails> list;
        private LayoutInflater inflater;
        Holder h;
        MyAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull
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
                h.tvTrainType =  v.findViewById(R.id.adapter_station_timetable_text_traintype);
                h.tvDestination = v.findViewById(R.id.adapter_station_timetable_text_destination);
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

    private class Holder{
        TextView tvDestination, tvTrainType, tvTime;
    }

}
