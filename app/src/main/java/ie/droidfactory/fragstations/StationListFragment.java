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

import ie.droidfactory.fragstations.model.Station;
import ie.droidfactory.fragstations.model.StationInterface;
import ie.droidfactory.fragstations.utils.RailSingleton;

/**
 * Created by kudlaty on 02/06/2016.
 */
public class StationListFragment extends MainFragment {

    private final static String TAG = StationListFragment.class.getSimpleName();
    private ListView lv;
    private TextView tvInfo;
    private ArrayList<String> mlist;


    StationInterface stationCallback;
    public void setStationSelectedListener(StationInterface listener){
        stationCallback = listener;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_stations_list, container,false);
        tvInfo = (TextView) v.findViewById(R.id.fragment_stations_main_text_info);
        lv = (ListView) v.findViewById(R.id.fragment_stations_main_listview);
        return v;
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onActivityCreated(savedInstanceState);
        this.mlist = new ArrayList<>();
        for(String key: RailSingleton.getStationMap().keySet()){
            mlist.add(key);
        }
        MyAdapter adapter = null;
        try {
            adapter = new MyAdapter(getActivity(), mlist, RailSingleton.getStationMap());
            lv.setAdapter(adapter);
            lv.setOnItemClickListener(new AdapterView.OnItemClickListener(){

                @Override
                public void onItemClick(AdapterView<?> parent, View view,
                                        int position, long id) {
                    lv.setItemChecked(position, true);
                    Log.d(TAG, "item clicked: "+position);
                    stationCallback.onStationSelected(RailSingleton.getStationMap().get(mlist.get(position)).getStationCode());
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
            stationCallback = (StationInterface) activity;
        }catch(ClassCastException e){
            throw new ClassCastException(activity.toString()+ "OnStationSelected Listener is not imolemented...");
        }
        Log.d(TAG, "onAttach end...");
    }


    class MyAdapter extends BaseAdapter {

        Holder h;
        ArrayList<String> mlist;
        HashMap<String, Station> mMap;
        private LayoutInflater inflater;

        MyAdapter(Context c, ArrayList<String> list, HashMap<String, Station> map) throws Exception{
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
//			Log.d(TAG, "adapter getView...");
            View v=null;
            if(convertView==null) {
                v = inflater.inflate(R.layout.adapter_stations, parent, false);
                h = new Holder();
                h.tvStationId = (TextView) v.findViewById(R.id.adapter_stations_text_station_id);
                h.tvStationName = (TextView) v.findViewById(R.id.adapter_stations_text_station_name);
                h.tvStationCode = (TextView) v.findViewById(R.id.adapter_stations_text_station_code);
                v.setTag(h);
            }
            else {
                v = convertView;
                h = (Holder) v.getTag();
            }

            h.tvStationId.setText(position+" | ID: "+mlist.get(position));
            h.tvStationName.setText(mMap.get(mlist.get(position)).getStationDesc());
            h.tvStationCode.setText(mMap.get(mlist.get(position)).getStationCode());
            return v;
        }

    }

    class Holder{
        TextView tvStationId, tvStationName, tvStationCode;

    }


}


