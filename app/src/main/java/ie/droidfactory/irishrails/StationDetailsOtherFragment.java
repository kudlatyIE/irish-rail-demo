package ie.droidfactory.irishrails;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.zip.Inflater;

import ie.droidfactory.irishrails.httputils.AsyncMode;
import ie.droidfactory.irishrails.httputils.AsyncStationsList;
import ie.droidfactory.irishrails.model.Cities;
import ie.droidfactory.irishrails.model.Station;
import ie.droidfactory.irishrails.model.StationDetailsInfo;
import ie.droidfactory.irishrails.utils.FragmentUtils;
import ie.droidfactory.irishrails.utils.RailSingleton;

/**
 * Created by kudlaty on 02/06/2016.
 */
public class StationDetailsOtherFragment extends Fragment {

    private final static String TAG = StationDetailsOtherFragment.class.getSimpleName();
    //	private int mCurrentPosition = -1;
    private String stationId;
    private int childPosition = -1;
    private TextView tvInfoGeneral;
    private Station station;
    private String detailsGeneral, detailsParking, detailsAccess;
    private ExpandableAdapter adapter;
    private ExpandableListView expListView;
    private HashMap<String, ArrayList<StationDetailsInfo>> infoMap;

    private AsyncStationsList.AsyncDoneCallback asyncDone = new AsyncStationsList.AsyncDoneCallback() {
        @Override
        public void onAsyncDone(boolean done) {
            if(done){
//                tvInfoGeneral.setText(RailSingleton.getWebStationInfo());
                infoMap = RailSingleton.getInfoMap();
                adapter = new ExpandableAdapter(getActivity(), infoMap);
                expListView.setAdapter(adapter);
            }
        }
    };

    public static StationDetailsOtherFragment newInstance(Bundle args){
        StationDetailsOtherFragment fragment = new StationDetailsOtherFragment();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_station_details_other, container, false);
        return v;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onViewCreated(view, savedInstanceState);
        tvInfoGeneral = view.findViewById(R.id.fragment_details_other_text_info_general);
        expListView = view.findViewById(R.id.fragment_details_other_lvExp);

    }


    @Override
    public void onStart() {
        // TODO Auto-generated method stub
        super.onStart();
        Bundle extras = getArguments();

        if(extras!=null) {
            stationId = extras.getString(FragmentUtils.STATION_CODE);
            Log.d(TAG, "extras station CODE: "+stationId);
        }
        if(stationId!= null) updateDetails(stationId);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onActivityCreated(savedInstanceState);
        Log.d(TAG, "onActivityCreated....");
    }

    private void updateDetails(String id){
        this.station = RailSingleton.getStationMap().get(id);
        Log.d(TAG, "get url: "+Cities.valueOf("_".concat(station.getStationId())).getDetailsUrl());
        AsyncStationsList rail = new AsyncStationsList(getActivity(),
                AsyncMode.GET_STATION_INFO_DETAILS, asyncDone);
        rail.execute(Cities.valueOf("_".concat(station.getStationId())).getDetailsUrl());

//        tvInfo.setText(TAG+" update details for: "+
//                "\nchild ID: "+childPosition+
//                "\nAlias: "+station.getStationAlias()+" ID: "+station.getStationCode()+
//                "\nDetails: "+station.getStationDesc());
    }


    public void setStationDetails(String id){
        stationId = id;
        updateDetails(id);
    }

    private class ExpandableAdapter extends BaseExpandableListAdapter {

        Context context;
        HolderHeader hh;
        HolderDetails hd;
//        LayoutInflater layoutInflater;
        List<String> listDataHeader = new ArrayList<>();
        HashMap<String, ArrayList<StationDetailsInfo>> map;

        ExpandableAdapter(Context context, HashMap<String, ArrayList<StationDetailsInfo>> map){
            this.context=context;
//            this.layoutInflater = LayoutInflater.from(context);
            this.map=map;
            this.listDataHeader.addAll(map.keySet());
        }

        @Override
        public int getGroupCount() {
            return listDataHeader.size();
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            return map.get(listDataHeader.get(groupPosition)).size();
        }

        @Override
        public Object getGroup(int groupPosition) {
            return listDataHeader.get(groupPosition);
        }

        @Override
        public Object getChild(int groupPosition, int childPosititon) {
            return map.get(listDataHeader.get(groupPosition)).get(childPosititon);
        }

        @Override
        public long getGroupId(int groupPosition) {
            return groupPosition;
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return childPosition;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
            String headerTitle = listDataHeader.get(groupPosition);
            View v;
            if(convertView==null){
                hh = new HolderHeader();
                v = LayoutInflater.from(context).inflate(R.layout.expandable_list_group, parent, false);
                hh.tvGroupHeader = v.findViewById(R.id.list_group_listHeader);
                hh.imgIndicator = v.findViewById(R.id.list_group_indicator);
                v.setTag(hh);
            }else{
                v = convertView;
                hh = (HolderHeader) v.getTag();
            }
            hh.imgIndicator.setSelected(isExpanded);
            hh.tvGroupHeader.setTypeface(null, Typeface.BOLD);
            hh.tvGroupHeader.setText(headerTitle);

            return v;
        }

        @Override
        public View getChildView(int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {

            String infoHeader = map.get(listDataHeader.get(groupPosition)).get(childPosition).getInfoHeader();
            String infoDetails = map.get(listDataHeader.get(groupPosition)).get(childPosition).getInfoDetails();
            if(infoDetails.contains("Hours: ")){
                StringBuffer sb = new StringBuffer(infoDetails);
                sb.insert(sb.lastIndexOf("Hours: "), "\n");
                infoDetails = sb.toString();
            }
            View v;
            if(convertView==null){
                hd = new HolderDetails();
                v = LayoutInflater.from(context).inflate(R.layout.adapter_expandable_station_details, parent, false);
                hd.tvInfoHeader = v.findViewById(R.id.adapter_expandable_station_detail_header);
                hd.tvInfoDetails = v.findViewById(R.id.adapter_expandable_station_detail_value);
                v.setTag(hd);
            }else {
                v  = convertView;
                hd = (HolderDetails) v.getTag();
            }
            hd.tvInfoHeader.setText(infoHeader);
            hd.tvInfoDetails.setText(infoDetails);

           return v;
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return true;
        }
    }
    private class HolderHeader{
        TextView tvGroupHeader;
        ImageView imgIndicator;
    }
    private class HolderDetails{
        TextView tvInfoHeader, tvInfoDetails;
    }

}
