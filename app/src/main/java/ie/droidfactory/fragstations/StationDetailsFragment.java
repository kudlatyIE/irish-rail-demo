package ie.droidfactory.fragstations;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ie.droidfactory.fragstations.utils.DetailsChilds;
import ie.droidfactory.fragstations.utils.FragmentUtils;

/**
 * Created by kudlaty on 02/06/2016.
 */
public class StationDetailsFragment extends Fragment {

    private final static String TAG = StationDetailsFragment.class.getSimpleName();

    private static int mCurrentPosition = -1;
    private String mId=null;
    private int mChildPosition = 0;
    private double lat=0, lo = 0;
    private String stationCode;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //Lets return pager view:
        View v  = inflater.inflate(R.layout.fragment_details_parent, container, false);
        ViewPager vp = (ViewPager) v.findViewById(R.id.fragment_details_parent_viewPager);
        //TODO: set pager adapter here....
        vp.setAdapter(new PagerAdapter(getChildFragmentManager()));
        TabLayout tabLayout = (TabLayout) v.findViewById(R.id.fragment_details_parent_tabs);
        tabLayout.setupWithViewPager(vp);
        return v;
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);

    }
    @Override
    public void onStart(){
        super.onStart();
        Bundle extras = getArguments();

        if(extras!=null) {
            stationCode = extras.getString(FragmentUtils.STATION_CODE);
            lat = extras.getDouble(FragmentUtils.STATION_LAT);
            lo = extras.getDouble(FragmentUtils.STATION_LONG);
            String temp = extras.getString(FragmentUtils.PARENT_POSITION_KEY);
            updateDetails(temp);
            Log.w(TAG, "set ID from Bundle: "+temp);
        }
        else if(mId==null){
            updateDetails(null);
            Log.w(TAG, "set default index: 0");
        }
    }

    private void updateDetails(String id){
        mId = id;
    }


    public void setStationDetails(String id){
        mId = id;
        updateDetails(id);
        Log.w(TAG, "set index from list click: "+id);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        // TODO Auto-generated method stub
        super.onSaveInstanceState(outState);
        outState.putInt(FragmentUtils.PARENT_POSITION_KEY, mCurrentPosition);
        outState.putInt(FragmentUtils.CHILD_POSITION_KEY, mChildPosition);
        outState.putDouble(FragmentUtils.STATION_LAT, lat);
        outState.putDouble(FragmentUtils.STATION_LONG, lo);
    }
    @Override
    public void onViewStateRestored(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onViewStateRestored(savedInstanceState);
        if(savedInstanceState!=null){
            mCurrentPosition = savedInstanceState.getInt(FragmentUtils.PARENT_POSITION_KEY);
            mChildPosition = savedInstanceState.getInt(FragmentUtils.CHILD_POSITION_KEY);
        }
    }


    class PagerAdapter extends FragmentStatePagerAdapter {

        DetailsChilds[] child = DetailsChilds.values();
        public PagerAdapter(FragmentManager fm) {
            super(fm);
            // TODO Auto-generated constructor stub
        }

        @Override
        public Fragment getItem(int arg0) {
            // TODO Auto-generated method stub
            Bundle args = new Bundle();
            switch(arg0){
                case 0:
                    mChildPosition = arg0;
                    args.putInt(FragmentUtils.CHILD_POSITION_KEY, arg0);
                    args.putString(FragmentUtils.PARENT_POSITION_KEY, mId);
                    args.putString(FragmentUtils.STATION_CODE, stationCode);

                    Log.d(TAG, "tab: "+child[0].getFrag_title());
                    return StationDetailsTimetableFragment.newInstance(args);
                case 1:
                    mChildPosition = arg0;
                    args.putInt(FragmentUtils.CHILD_POSITION_KEY, arg0);
                    args.putString(FragmentUtils.PARENT_POSITION_KEY, mId);
                    args.putString(FragmentUtils.STATION_CODE, stationCode);
                    args.putDouble(FragmentUtils.STATION_LAT, lat);
                    args.putDouble(FragmentUtils.STATION_LONG, lo);
                    Log.d(TAG, "tab: "+child[1].getFrag_title());
                    return StationDetailsMapaFragment.newInstance(args);
                case 2:
                    mChildPosition = arg0;
                    args.putInt(FragmentUtils.CHILD_POSITION_KEY, arg0);
                    args.putString(FragmentUtils.PARENT_POSITION_KEY, mId);
                    args.putString(FragmentUtils.STATION_CODE, stationCode);
                    Log.d(TAG, "tab: "+child[2].getFrag_title());
                    return StationDetailsOtherFragment.newInstance(args);
                default: return null;
            }
        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return child.length;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return child[position].getFrag_title();
        }
    }


    /**
     * static pager, not used anymore, will be replace by FragmentStatePagerAdapter
     * @author kudlaty
     *
     */
    class PagerAdapter_old extends FragmentPagerAdapter {

        DetailsChilds[] child = DetailsChilds.values();
        public PagerAdapter_old(FragmentManager fm) {
            super(fm);
            // TODO Auto-generated constructor stub
        }
        @Override
        public int getCount() {
            return child.length;
        }

        @Override
        public Fragment getItem(int arg0) {
            Bundle args = new Bundle();
            args.putInt(FragmentUtils.PARENT_POSITION_KEY, mCurrentPosition);
            switch(arg0){
                case 0:
                    args.putInt(FragmentUtils.CHILD_POSITION_KEY, arg0);
                    Log.d(TAG, "tab: "+child[0].getFrag_title());
                    return StationDetailsTimetableFragment.newInstance(args);
                case 1:
                    args.putInt(FragmentUtils.CHILD_POSITION_KEY, arg0);
                    Log.d(TAG, "tab: "+child[1].getFrag_title());
                    return StationDetailsMapaFragment.newInstance(args);
                case 2:
                    args.putInt(FragmentUtils.CHILD_POSITION_KEY, arg0);
                    Log.d(TAG, "tab: "+child[2].getFrag_title());
                    return StationDetailsOtherFragment.newInstance(args);

                default: return null;
            }
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return child[position].getFrag_title();
        }
    }

}

