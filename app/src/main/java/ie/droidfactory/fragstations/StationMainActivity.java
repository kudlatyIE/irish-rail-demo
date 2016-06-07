package ie.droidfactory.fragstations;

import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import ie.droidfactory.fragstations.model.Station;
import ie.droidfactory.fragstations.model.StationInterface;
import ie.droidfactory.fragstations.utils.FragmentUtils;
import ie.droidfactory.fragstations.utils.RailSingleton;

/**
 * Created by kudlaty on 02/06/2016.
 */
public class StationMainActivity extends AppCompatActivity implements StationInterface {

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
    private LinearLayout maineContainer;
    private FragmentTransaction ft;
    private static String mainFragmentId = FragmentUtils.FRAGMENT_INFO;

    private boolean isDrawerVisible=false;
    private NavigationView navigationView;
    private DrawerLayout drawerLayout;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.main_layout);
        //TODO: try layout with drawer and toolbar
        setContentView(R.layout.drawer_layout);
        isDualPane = getResources().getBoolean(R.bool.has_two_panes);
        isTablet = getResources().getBoolean(R.bool.is_tablet);
        Log.d(TAG, "id landscape layout: "+isDualPane);

        detailsView = findViewById(R.id.fragment_station_details_container);
        detailsFragment = (StationDetailsFragment) getSupportFragmentManager().findFragmentByTag(FRAG_DETAILS);


        /*
        add toolbar and drawer... and make them work!
        1. initialization drawer....
         */
        isDrawerVisible = getResources().getBoolean(R.bool.mini_drawer_visible);
        Toolbar toolbar = (Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.test_drawer_main_text);
    //1.
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle drawerToggle = new ActionBarDrawerToggle(this, drawerLayout,
                toolbar,R.string.open_drawer, R.string.close_drawer){

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }
        };
        drawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.main_navigation_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                boolean res=false;
                if(item.isChecked()) item.setChecked(false);
                else item.setChecked(true);
                drawerLayout.closeDrawers();
                res = setFragmentFromDrawer(item.getItemId());
                //TODO: create selected fragment and replace view...
                createFragment(mainFragmentId);
                return res;
            }
        });
        /*
        this will be handle by drawer menu
         */
//        Bundle extras = getIntent().getExtras();
//        if(extras!=null) mainFragmentId = extras.getString(FragmentUtils.FRAGMENT);
//        else mainFragmentId = FragmentUtils.FRAGMENT_LIST;

        //handle fragments....

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
            if(detailsView!=null) updateViews(detailsView);
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
            if(detailsView!=null){
                if(getSupportFragmentManager().findFragmentByTag(FRAG_DETAILS)==null){
                    detail.setVisibility(View.GONE);
                }else detail.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle arg0) {
        // TODO Auto-generated method stub
        super.onSaveInstanceState(arg0);
        arg0.putString(FragmentUtils.PARENT_POSITION_KEY, mId);
    }

   private void createFragment(String key){
       //clean fragment containers
       if( detailsFragment!=null){
           Log.d(TAG, "details fragment is not null!");
           ft = getSupportFragmentManager().beginTransaction();
           getSupportFragmentManager().popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
           ft.remove(detailsFragment);
           ft.commit();
           getSupportFragmentManager().executePendingTransactions();
           detailsFragment=null;
//           updateViews(detailsView);

       }else{
           Log.d(TAG, "details fragment is NULL!");
           }
           //and clean main fragment
       if(mainFragment!=null){
           ft = getSupportFragmentManager().beginTransaction();
           getSupportFragmentManager().popBackStackImmediate(null, FragmentManager
                   .POP_BACK_STACK_INCLUSIVE);
           ft.remove(mainFragment);
           ft.commit();
           getSupportFragmentManager().executePendingTransactions();
           mainFragment=null;
       }else{
              Log.d(TAG, "main fragment is NULL!");
          }
       //display new main fragment in empty view
       ft = getSupportFragmentManager().beginTransaction();
       mainFragment = (MainFragment) setMainFragment(key);
       mainFragment.setStationSelectedListener(this);
       ft.add(R.id.fragment_station_list_container, mainFragment, FRAG_STATIONS).commit();
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

    private boolean setFragmentFromDrawer(int index){

        switch (index){
            case R.id.item_station_list:
                mainFragmentId = FragmentUtils.FRAGMENT_LIST;
                return true;
            case R.id.item_station_map:
                mainFragmentId = FragmentUtils.FRAGMENT_MAP;
                return true;
            case R.id.item_station_search:
                Toast.makeText(getApplicationContext(),"click at search station..", Toast
                        .LENGTH_SHORT).show();
                return true;
            case R.id.item_train_map:
                Toast.makeText(getApplicationContext(),"click at train map..", Toast
                        .LENGTH_SHORT).show();
                return true;
            case R.id.item_train_search:
                Toast.makeText(getApplicationContext(),"click at train station..", Toast
                        .LENGTH_SHORT).show();
                return true;
            case R.id.item_about:
                Toast.makeText(getApplicationContext(),"click at about..", Toast
                        .LENGTH_SHORT).show();
                return true;
            case R.id.item_settings:
                Toast.makeText(getApplicationContext(),"click at settings..", Toast
                        .LENGTH_SHORT).show();
                return true;
        }
        return false;

    }

}
