package ie.droidfactory.fragstations;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import ie.droidfactory.fragstations.model.Station;
import ie.droidfactory.fragstations.model.StationInterface;
import ie.droidfactory.fragstations.utils.FragmentUtils;
import ie.droidfactory.fragstations.utils.RailSingleton;

/**
 * Created by kudlaty on 02/06/2016.
 */
public class StationMainActivity extends FragmentActivity implements StationInterface {

    private final static String TAG = StationMainActivity.class.getSimpleName();
    public final static String FRAG_DETAILS = "frag_details", FRAG_STATIONS="frag_stations";
    public final static String FRAG_MAIN="frag_main";
    private StationDetailsFragment detailsFragment;
    private MainFragment mainFragment;
    private int mPosition = -1;
    private String mId = null;
    private String stationCode=null;
    private boolean isDualPane, isTablet;
    private View detailsView, fragmentContainer;
    private LinearLayout mainContainer;
    private FragmentTransaction ft;
    private String mainFragmentId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.main_layout);
        setContentView(R.layout.drawer_layout); //TODO: try layout wit drawer and toolbar
        isDualPane = getResources().getBoolean(R.bool.has_two_panes);
        isTablet = getResources().getBoolean(R.bool.is_tablet);
        Log.d(TAG, "id landscape layout: "+isDualPane);


        LayoutInflater inflater = LayoutInflater.from(getApplicationContext());
        fragmentContainer = inflater.inflate(R.layout.main_layout, null, false);
        mainContainer = (LinearLayout) findViewById(R.id.main_in_drawer_container);
        mainContainer.addView(fragmentContainer);

        detailsView = findViewById(R.id.fragment_station_details_container);
        Bundle extras = getIntent().getExtras();
        if(extras!=null) mainFragmentId = extras.getString(FragmentUtils.FRAGMENT);
        else mainFragmentId = FragmentUtils.FRAGMENT_LIST;

        detailsFragment = (StationDetailsFragment) getSupportFragmentManager().findFragmentByTag(FRAG_DETAILS);
        //SINGLE PANE - PORTRAIT
        if(!isDualPane){
            Log.d(TAG, "onCreate, PORT, list cointainer NULL: "+(findViewById(R.id.fragment_station_list_container)==null));
            Log.d(TAG, "onCreate, PORT, details cointainer NULL: "+(findViewById(R.id.fragment_station_details_container)==null));
            if(detailsFragment!=null){
                if(isTablet){
                    ft = getSupportFragmentManager().beginTransaction();
                    getSupportFragmentManager().popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                    ft.remove(detailsFragment).commit();
                    getSupportFragmentManager().executePendingTransactions();
                    ft = getSupportFragmentManager().beginTransaction();
                    ft.replace(R.id.fragment_station_list_container, detailsFragment, FRAG_DETAILS);
                    ft.addToBackStack(null);
                    ft.commit();
                }

            }else{
                if(findViewById(R.id.fragment_station_list_container)!=null) {
                    if(savedInstanceState!=null) return; // TODO: check is fragment is savedInstanceState
                    ft = getSupportFragmentManager().beginTransaction();
                    mainFragment = (MainFragment) setMainFragment(mainFragmentId);
                    mainFragment.setStationSelectedListener(this);
                    ft.add(R.id.fragment_station_list_container, mainFragment, FRAG_STATIONS).commit();
                }
            }

            //DUAL PANE - LANDSCAPE
        }else{
            updateViews(detailsView);
            Log.d(TAG, "onCreate, LAND, list cointainer NULL: "+(findViewById(R.id.fragment_station_list_container)==null));
            Log.d(TAG, "onCreate, LAND, details cointainer NULL: "+(findViewById(R.id.fragment_station_details_container)==null));
            if(findViewById(R.id.fragment_station_list_container)!=null){
                if(detailsFragment!=null){
                    detailsView.setVisibility(View.VISIBLE);
                    ft = getSupportFragmentManager().beginTransaction();
                    getSupportFragmentManager().popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                    ft.remove(detailsFragment).commit();
                    getSupportFragmentManager().executePendingTransactions();
                    ft = getSupportFragmentManager().beginTransaction();
                    ft.replace(R.id.fragment_station_details_container, detailsFragment, FRAG_DETAILS);
                    ft.addToBackStack(null);
                    ft.commit();
                    if(mainFragment!=null){
                        ft = getSupportFragmentManager().beginTransaction();
                        ft.replace(R.id.fragment_station_list_container, mainFragment, FRAG_STATIONS).commit();
                    }
                }else {
                    if(mainFragment==null){
                        ft = getSupportFragmentManager().beginTransaction();
//						listFragment = new StationListFragment();
                        mainFragment = (MainFragment) setMainFragment(mainFragmentId);
                        mainFragment.setStationSelectedListener(this);
                        ft.add(R.id.fragment_station_list_container, mainFragment, FRAG_STATIONS).commit();
                    }else return;
                }
            }else{
                ft = getSupportFragmentManager().beginTransaction();
                mainFragment = (MainFragment) setMainFragment(mainFragmentId);
                mainFragment.setStationSelectedListener(this);
                ft.add(R.id.fragment_station_list_container, mainFragment, FRAG_STATIONS).commit();
            }
        }
        getSupportFragmentManager().addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {

            @Override
            public void onBackStackChanged() {
                updateViews(detailsView);
                Log.d(TAG, "onBackStackChanged....");
            }
        });
    }

    @Override
    public void onBackPressed() {
        if(isDualPane){
            if( detailsFragment!=null){
                Log.d(TAG, "onBackPressed(): details fragment is not null!");
                ft = getSupportFragmentManager().beginTransaction();
                getSupportFragmentManager().popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                ft.remove(detailsFragment);
                ft.commit();
                getSupportFragmentManager().executePendingTransactions();
                detailsFragment=null;
                updateViews(detailsView);

            }else{
                Log.d(TAG, "onBackPressed(): details fragment is NULL!");
                super.onBackPressed();
            }
        }else super.onBackPressed();

    }

    @Override
    public void onStationSelected(String id) {
        View detailsView = findViewById(R.id.fragment_station_details_container);
        // TODO Auto-generated method stub
        RailSingleton.resetTimetable();
        Station station = RailSingleton.getStationMap().get(id);
        stationCode = station.getStationCode();
        double lat = station.getStationLatitude();
        double lo = station.getStationLongitude();

        Log.d(TAG, "station selected on list: "+id+" code: "+stationCode);
        detailsFragment = (StationDetailsFragment) getSupportFragmentManager().findFragmentByTag(FRAG_DETAILS);
        FragmentTransaction ft;

        if(!isDualPane){//PORTRAIT - SINGLE PANE MDE
            this.mId=id;
            Log.d(TAG, "click PORT, list cointainer NULL: "+(findViewById(R.id.fragment_station_list_container)==null));
            Log.d(TAG, "click PORT, details cointainer NULL: "+(findViewById(R.id.fragment_station_details_container)==null));

            detailsFragment = new StationDetailsFragment();
            Bundle args = new Bundle();
            args.putString(FragmentUtils.PARENT_POSITION_KEY, id);
            args.putString(FragmentUtils.STATION_CODE, stationCode);
            args.putDouble(FragmentUtils.STATION_LAT, lat);
            args.putDouble(FragmentUtils.STATION_LONG, lo);
            detailsFragment.setArguments(args);
            ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.fragment_station_list_container, detailsFragment, FRAG_DETAILS);
            ft.addToBackStack(null);
            ft.commit();

//			}
            //LANDSCAPE - DUAL PANE MODE
        }else{
            Log.d(TAG, "click LAND, list cointainer NULL: "+(findViewById(R.id.fragment_station_list_container)==null));
            Log.d(TAG, "LAND, details cointainer NULL: "+(findViewById(R.id.fragment_station_details_container)==null));
            if(!detailsView.isShown()){
                Log.w(TAG, "details view is GONE, restore view....");
                detailsView.setVisibility(View.VISIBLE);
            }
            detailsFragment = new StationDetailsFragment();
            Bundle args = new Bundle();
            args.putString(FragmentUtils.PARENT_POSITION_KEY, id);
            args.putString(FragmentUtils.STATION_CODE, stationCode);
            args.putDouble(FragmentUtils.STATION_LAT, lat);
            args.putDouble(FragmentUtils.STATION_LONG, lo);
            detailsFragment.setArguments(args);
            ft = getSupportFragmentManager().beginTransaction();
            if(detailsFragment!=null) {
                //TODO: dont reload the same details fragment if exist
                if(id==mId) return;
                else {
                    ft.replace(R.id.fragment_station_details_container, detailsFragment,FRAG_DETAILS);
                    mId=id;
                    ft.addToBackStack(null);
                    ft.commit();
                }
            }else {
                ft.add(R.id.fragment_station_details_container, detailsFragment,FRAG_DETAILS);
                mId=id;
                ft.addToBackStack(null);
                ft.commit();
            }

        }
    }

    private void updateViews(View detail){
        if(isDualPane){
            if(getSupportFragmentManager().findFragmentByTag(FRAG_DETAILS)==null){
                detail.setVisibility(View.GONE);
            }else detail.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle arg0) {
        // TODO Auto-generated method stub
        super.onSaveInstanceState(arg0);
        arg0.putString(FragmentUtils.PARENT_POSITION_KEY, mId);
    }

    private Fragment setMainFragment(String key){
        Fragment frag;
        switch(key){
            case FragmentUtils.FRAGMENT_LIST:
                frag = new StationListFragment();
                break;
            case FragmentUtils.FRAGMENT_MAP:
                frag = new AllStationsMapFragment();
                break;
            case FragmentUtils.FRAGMENT_INFO:
//                frag = new InfoFragment();
                Bundle arg = new Bundle();
                arg.putString(FragmentUtils.FRAGMENT_INFO, "njus, njus, HOT njus!");
                frag = InfoFragment.newInstance(arg);
                break;
            default: frag = new StationListFragment();
        }
        return frag;
    }

}
