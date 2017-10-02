package ie.droidfactory.fragstations;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import ie.droidfactory.fragstations.httputils.AsyncMode;
import ie.droidfactory.fragstations.httputils.AsyncStationsList;
import ie.droidfactory.fragstations.httputils.Links;
import ie.droidfactory.fragstations.model.RailInterface;
import ie.droidfactory.fragstations.model.Station;
import ie.droidfactory.fragstations.utils.DataUtils;
import ie.droidfactory.fragstations.utils.FragmentUtils;
import ie.droidfactory.fragstations.utils.LocationUtils;
import ie.droidfactory.fragstations.utils.RailSingleton;

/**
 * Created by kudlaty on 02/06/2016.
 */
public class StationListFragment extends MainFragment {



    private final static String TAG = StationListFragment.class.getSimpleName();
    private ListView lv;
    private SwipeRefreshLayout swipeRefreshLayout;
    private TextView tvInfo;
    private EditText editSearch;
    private ImageView imgCancel, imgSortName, imgSortDistance;
    private ArrayList<Station> allStations, stationList, filteredList;
    private HashMap<String, Station> stationHashMap;
    private MyAdapter adapter = null;
    private StationFilter stationFilter;
    private Location myLocation;
    private double myLat, myLng;
    private Sort sortMode = Sort.UNSORTED;

    private AsyncStationsList.AsyncDoneCallback asyncDone = new AsyncStationsList
            .AsyncDoneCallback() {
        @Override
        public void onAsyncDone(boolean done) {
            Log.d(TAG, "async success: "+done);
            Log.d(TAG, "async result: "+RailSingleton.getAsyncResult());
            swipeRefreshLayout.setRefreshing(false);
            if(done){
                stationHashMap = RailSingleton.getStationMap();
                stationList = new ArrayList<>(stationHashMap.values());
                sortStation(sortMode, stationList);
            }
        }
    };

    RailInterface stationCallback;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_stations_list, container,false);
        tvInfo = (TextView) v.findViewById(R.id.fragment_stations_main_text_info);
        editSearch = (EditText) v.findViewById(R.id.fragment_stations_main_edit_search);
        imgCancel = v.findViewById(R.id.fragment_stations_main_img_cancel);
        imgSortDistance = v.findViewById(R.id.fragment_stations_main_img_sort_distance);
        imgSortName = v.findViewById(R.id.fragment_stations_main_img_sort_station_name);
        lv = (ListView) v.findViewById(R.id.fragment_stations_main_listview);
        swipeRefreshLayout = v.findViewById(R.id.fragment_stations_main_swipe_refresh_layout);
        return v;
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onActivityCreated(savedInstanceState);

        getActivity().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        if(savedInstanceState!=null){
            restore(savedInstanceState);
        }else {
            this.stationHashMap = RailSingleton.getStationMap();
            this.myLocation = DataUtils.getLocation(getActivity());
            this.myLat = myLocation.getLatitude();
            this.myLng = myLocation.getLongitude();
        }


        stationList = new ArrayList<>(stationHashMap.values());
        sortStation(Sort.DISTANCE_UP, stationList);

        try {
            adapter = new MyAdapter(getActivity(), R.layout.adapter_stations, stationList);
            lv.setAdapter(adapter);
            lv.setOnItemClickListener(new AdapterView.OnItemClickListener(){

                @Override
                public void onItemClick(AdapterView<?> parent, View view,
                                        int position, long id) {
                    lv.setItemChecked(position, true);
                    Log.d(TAG, "item clicked: "+position);
                    stationCallback.onStationSelected(stationList.get(position).getStationCode());
                }

            });

            Cliker click = new Cliker();
            imgCancel.setOnClickListener(click);
            imgSortDistance.setOnClickListener(click);
            imgSortName.setOnClickListener(click);

            editSearch.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if(s!=null){
                        filteredList = new ArrayList<Station>();
                        for(Station station: stationList){
                            if(station.getStationDesc().toLowerCase().contains(s.toString().toLowerCase())){
                                filteredList.add(station);
                            }
                        }
                        sortStation(sortMode, filteredList);
                        adapter = new MyAdapter(getActivity(), R.layout.adapter_stations, filteredList);
                        lv.setAdapter(adapter);

                    }
                }

                @Override
                public void afterTextChanged(Editable s) {
                }
            });
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            tvInfo.setText(e.getMessage());
        }
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener(){

            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(true);
                LocationUtils.getLocation(getActivity());
                AsyncStationsList rail = new AsyncStationsList(getActivity(), AsyncMode.GET_ALL_STATIONS, asyncDone);
                rail.execute(Links.ALL_STATIONS.getRailLink());
            }
        });
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        // TODO Auto-generated method stub
        super.onSaveInstanceState(outState);
        outState.putSerializable(FragmentUtils.FRAGMENT_STATION_MAP, stationHashMap);
        outState.putDouble(FragmentUtils.MY_LAT, myLat);
        outState.putDouble(FragmentUtils.MY_LNG, myLng);
    }
    @Override
    public void onViewStateRestored(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onViewStateRestored(savedInstanceState);
        restore(savedInstanceState);
    }

    public void restore(Bundle savedInstanceState) {
        if(savedInstanceState!=null){
            myLat = savedInstanceState.getDouble(FragmentUtils.MY_LAT);
            myLng = savedInstanceState.getDouble(FragmentUtils.MY_LNG);
            myLocation = new Location("");
            myLocation.setLatitude(myLat);
            myLocation.setLongitude(myLng);
            RailSingleton.setMyLocation(new LatLng(myLat, myLng));
            stationHashMap = (HashMap<String, Station>) savedInstanceState.getSerializable(FragmentUtils.FRAGMENT_STATION_MAP);
        }
    }


    @SuppressWarnings("deprecation")
    @Override
    public void onAttach(Activity activity) {
        // TODO Auto-generated method stub
        super.onAttach(activity);
        try{
            stationCallback = (RailInterface) activity;
        }catch(ClassCastException e){
            throw new ClassCastException(activity.toString()+ "OnStationSelected Listener is not " +
                    "implemented...");
        }
        Log.d(TAG, "onAttach end...");
    }

    private void sortStation(Sort mode, ArrayList<Station> list){
        Log.d(TAG, "sort mode: "+mode.name());
        if(mode==Sort.NAME_UP){
            Station.stationNameDown compare = new Station.stationNameDown();
            Collections.sort(list, compare);
            imgSortName.setImageDrawable(getResources().getDrawable(R.drawable.ic_sort_down));
            for (Station s: stationList){
                Log.d("name:",s.toString());
            }
        }
        if(mode==Sort.NAME_DOWN){
            Station.stationNameUp compare = new Station.stationNameUp();
            Collections.sort(list, compare);
            imgSortName.setImageDrawable(getResources().getDrawable(R.drawable.ic_sort_up));
            for (Station s: stationList){
                Log.d("name:",s.toString());
            }
        }
        if(mode==Sort.DISTANCE_UP){
            Station.distanceDown compare = new Station.distanceDown();
            Collections.sort(list, compare);
            imgSortDistance.setImageDrawable(getResources().getDrawable(R.drawable.ic_sort_down));
            for (Station s: stationList){
                Log.d("distanceUp: ",s.toString());
            }
        }
        if(mode==Sort.DISTANCE_DOWN){
            Station.distanceUp compare = new Station.distanceUp();
            Collections.sort(list, compare);
            imgSortDistance.setImageDrawable(getResources().getDrawable(R.drawable.ic_sort_up));
            for (Station s: stationList){
                Log.d("distanceDown: ",s.toString());
            }
        }
        adapter = new MyAdapter(getActivity(), R.layout.adapter_stations, list);
        lv.setAdapter(adapter);
    }
    private class Cliker implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            ArrayList<Station> temp;
            if(editSearch.getText().toString().trim().length()>0) temp = filteredList;
            else temp = stationList;

            switch (view.getId()){
                case R.id.fragment_stations_main_img_cancel:
//                    stationList = allStations;
                    sortStation(sortMode, temp);
                    editSearch.setText("");
                    break;
                case R.id.fragment_stations_main_img_sort_station_name:
                    if(sortMode!=Sort.NAME_DOWN) sortMode=Sort.NAME_DOWN;
                    else sortMode=Sort.NAME_UP;
                    sortStation(sortMode, temp);
                    break;
                case R.id.fragment_stations_main_img_sort_distance:
                    if(sortMode!=Sort.DISTANCE_DOWN) sortMode=Sort.DISTANCE_DOWN;
                    else sortMode=Sort.DISTANCE_UP;
                    sortStation(sortMode, temp);
                    break;
            }
        }
    }

    private enum Sort{
        NAME_UP, NAME_DOWN, DISTANCE_UP, DISTANCE_DOWN, UNSORTED;
    }

    private class MyAdapter extends ArrayAdapter {

        ArrayList<Station> stations;
        private LayoutInflater inflater;
        Holder h;
        public MyAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull ArrayList<Station> objects) {
            super(context, resource, objects);
            this.stations=objects;
            this.inflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return stations.size();
        }

        @Nullable
        @Override
        public Object getItem(int position) {
            return stations.get(position);
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

            View v;
            if(convertView==null){
                v = inflater.inflate(R.layout.adapter_stations, parent, false);
                h = new Holder();
                h.tvStationId = (TextView) v.findViewById(R.id.adapter_stations_text_station_id);
                h.tvStationName = (TextView) v.findViewById(R.id.adapter_stations_text_station_name);
                v.setTag(h);
            }else{
                v = convertView;
                h = (Holder) v.getTag();
            }
            h.tvStationId.setText(DataUtils.formatDistance(stations.get(position).getDistance()));
            h.tvStationName.setText(stations.get(position).getStationDesc());
            return v;
        }
    }

    class Holder{
        TextView tvStationId, tvStationName,  tvDistance;
    }

    private class StationFilter extends Filter {

        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            FilterResults filterResults = new FilterResults();
            if (charSequence!=null && charSequence.length()>0){
                ArrayList<Station> tempList = new ArrayList<Station>();
                for(Station s: stationList){
                    if(s.getStationDesc().toLowerCase().contains(charSequence.toString().toLowerCase())){
                        tempList.add(s);
                    }
                }
                filterResults.count = tempList.size();
                filterResults.values = stationList;
            }
            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            filteredList = (ArrayList<Station>) filterResults.values;
            adapter.notifyDataSetChanged();
        }
    }


}


