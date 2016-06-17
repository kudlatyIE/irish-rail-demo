package ie.droidfactory.fragstations;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

import ie.droidfactory.fragstations.model.RailInterface;
import ie.droidfactory.fragstations.model.SortedObject;
import ie.droidfactory.fragstations.model.Sortownia;
import ie.droidfactory.fragstations.model.Station;
import ie.droidfactory.fragstations.utils.RailSingleton;

/**
 * Created by kudlaty on 02/06/2016.
 */
public class StationListFragment extends MainFragment {

    private final static String TAG = StationListFragment.class.getSimpleName();
    private ListView lv;
    private TextView tvInfo;
    private TextView tvSortByName, tvSortByDistance;
//    private ArrayList<String> mlist;
    private ArrayList<SortedObject> sortedByDistance;
    private ArrayList<SortedObject> sortedByName;


    RailInterface stationCallback;
//    public void setStationSelectedListener(RailInterface listener){
//        stationCallback = listener;
//    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_stations_list, container,false);
        tvInfo = (TextView) v.findViewById(R.id.fragment_stations_main_text_info);
        tvSortByName = (TextView) v.findViewById(R.id.fragment_stations_main_text_station_name);
        tvSortByDistance = (TextView) v.findViewById(R.id.fragment_stations_main_text_station_distance);
        lv = (ListView) v.findViewById(R.id.fragment_stations_main_listview);
        return v;
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onActivityCreated(savedInstanceState);

//        this.mlist = new ArrayList<>();
//        for(String key: RailSingleton.getStationMap().keySet()){
//            mlist.add(key);
//        }
        try {
            this.sortedByDistance = new Sortownia().getSortedListByDistance();
            for(SortedObject so: sortedByDistance){
                String dist = so.getValue1();
//                Log.d(TAG, "by distance : "+so.getKey()+" ::: "+dist);
            }
            this.sortedByName = new Sortownia().getSortedListByName();
            for(SortedObject so: sortedByName){
//                Log.d(TAG, "by name : "+so.getKey()+" ::: "+so.getValue1()+" ::: dist: "
//                        +so.getValue2());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        MyAdapter adapter;
        try {
            adapter = new MyAdapter(getActivity(), sortedByDistance, RailSingleton.getStationMap());
            lv.setAdapter(adapter);
            lv.setOnItemClickListener(new AdapterView.OnItemClickListener(){

                @Override
                public void onItemClick(AdapterView<?> parent, View view,
                                        int position, long id) {
                    lv.setItemChecked(position, true);
                    Log.d(TAG, "item clicked: "+position);
//                    stationCallback.onStationSelected(RailSingleton.getStationMap().get(mlist.get(position)).getStationCode());
                    stationCallback.onStationSelected(sortedByDistance.get(position).getKey());
                }

            });
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            tvInfo.setText(e.getMessage());
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


    class MyAdapter extends BaseAdapter {

        Holder h;
        ArrayList<SortedObject> mlist;
        HashMap<String, Station> mMap;
        private LayoutInflater inflater;

        MyAdapter(Context c, ArrayList<SortedObject> list, HashMap<String, Station> map) throws Exception{
            if(map==null){
                throw new Exception("station list is NULL!");
            }
            this.mlist = list;
            this.mMap=map;
            this.inflater = LayoutInflater.from(c);


        }
        @Override
        public int getCount() {
            return mlist.size();
        }

        @Override
        public Object getItem(int position) {
            return mlist.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v;
            if(convertView==null) {
                v = inflater.inflate(R.layout.adapter_stations, parent, false);
                h = new Holder();
                h.tvStationId = (TextView) v.findViewById(R.id.adapter_stations_text_station_id);
                h.tvStationName = (TextView) v.findViewById(R.id.adapter_stations_text_station_name);
//                h.tvStationCode = (TextView) v.findViewById(R.id.adapter_stations_text_station_code);
                h.tvDistance = (TextView) v.findViewById(R.id.adapter_stations_text_station_distance);
                v.setTag(h);
            }
            else {
                v = convertView;
                h = (Holder) v.getTag();
            }
            h.tvStationId.setText(String.valueOf(position));
            h.tvStationName.setText(mMap.get(mlist.get(position).getKey()).getStationDesc());
//            h.tvStationCode.setText(mMap.get(mlist.get(position).getKey()).getStationCode());
            h.tvDistance.setText(mlist.get(position).getValue1());
            return v;
        }

    }

    class Holder{
        TextView tvStationId, tvStationName,  tvDistance; //tvStationCode

    }

    class SortButton implements View.OnClickListener {

        @Override
        public void onClick(View v) {

            switch (v.getId()){
                case (R.id.fragment_stations_main_text_station_name):
                    //TODO: set lv adapter with list sorted by name
                    break;
                case  (R.id.fragment_stations_main_text_station_distance):
                    //TODO: set lv adapter with list sorted by distance...
                    break;
            }

        }
    }


}


