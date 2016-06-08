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
public class StationMainActivity extends AppCompatActivity implements StationInterface,
        AllStationsMapFragment.RestartCallback {

    private final static String TAG = StationMainActivity.class.getSimpleName();
    public final static String FRAG_DETAILS = "frag_details";//, FRAG_STATIONS="frag_stations";
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
                setFragmentFromDrawer(item.getItemId());
                //TODO: create selected fragment and replace view...
//                createFragment(mainFragmentId);
                return true;
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
                    if(mainFragment==null){
                        ft = getSupportFragmentManager().beginTransaction();
                        mainFragment = (MainFragment) setMainFragment(mainFragmentId);
                        mainFragment.setStationSelectedListener(this);
                        ft.add(R.id.fragment_station_list_container, mainFragment, FRAG_MAIN).commit();
                    }
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
                        ft.replace(R.id.fragment_station_list_container, mainFragment, FRAG_MAIN)
                                .commit();
                    }
                }else {
                    if(mainFragment==null){
                        ft = getSupportFragmentManager().beginTransaction();
//						listFragment = new StationListFragment();
                        mainFragment = (MainFragment) setMainFragment(mainFragmentId);
                        mainFragment.setStationSelectedListener(this);
                        ft.add(R.id.fragment_station_list_container, mainFragment, FRAG_MAIN).commit();
                    }else return;
                }
            }else{
                ft = getSupportFragmentManager().beginTransaction();
                mainFragment = (MainFragment) setMainFragment(mainFragmentId);
                mainFragment.setStationSelectedListener(this);
                ft.add(R.id.fragment_station_list_container, mainFragment, FRAG_MAIN).commit();
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


    /**
     * for drawer only
     * remove all views and create new fragment
     * @param key
     */
   private void createFragment(String key){
       if(key == null) return;
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
       //create new fragment and display in empty main view
       Log.d(TAG, "createFragment::::key: "+key);
       ft = getSupportFragmentManager().beginTransaction();
       mainFragment = (MainFragment) setMainFragment(key);
       mainFragment.setStationSelectedListener(this);
       ft.add(R.id.fragment_station_list_container, mainFragment, FRAG_MAIN).commit();
   }

    private Fragment setMainFragment(String key){
        Fragment frag;
        Bundle arg;
        switch(key){
            case FragmentUtils.FRAGMENT_LIST:
                frag = new StationListFragment();
                break;
            case FragmentUtils.FRAGMENT_ALL_MAP:
                arg = new Bundle();
                frag = AllStationsMapFragment.newInstance(arg);
                break;
            case FragmentUtils.FRAGMENT_INFO:
//                frag = new InfoFragment();
                arg = new Bundle();
                arg.putString(FragmentUtils.FRAGMENT_INFO, "njus, njus, HOT njus!");
                frag = InfoFragment.newInstance(arg);
                break;
            case FragmentUtils.FRAGMENT_ABOUT:
//                frag = new InfoFragment();
                arg = new Bundle();
                arg.putString(FragmentUtils.FRAGMENT_ABOUT, "that ia about everything!");
                frag = AboutFragment.newInstance(arg);
                break;
            case FragmentUtils.FRAGMENT_SETTINGS:
//                frag = new InfoFragment();
                arg = new Bundle();
                arg.putString(FragmentUtils.FRAGMENT_SETTINGS, "my little settings here!");
                frag = SettingsFragment.newInstance(arg);
                break;
            default: frag = new StationListFragment();
        }
        return frag;
    }

    private void setFragmentFromDrawer(int index){
        boolean create = false;
        switch (index){
            case R.id.item_station_list:
                if(mainFragmentId != FragmentUtils.FRAGMENT_LIST) {
                    mainFragmentId = FragmentUtils.FRAGMENT_LIST;
                    create = true;
                }
                break;

            case R.id.item_station_map:
                if(mainFragmentId != FragmentUtils.FRAGMENT_ALL_MAP) {
                    mainFragmentId = FragmentUtils.FRAGMENT_ALL_MAP;
                    create=true;
                }
                break;
            case R.id.item_station_search:
                Toast.makeText(getApplicationContext(),"click at search station..", Toast
                        .LENGTH_SHORT).show();
                break;
            case R.id.item_train_map:
                Toast.makeText(getApplicationContext(),"click at train map..", Toast
                        .LENGTH_SHORT).show();
                break;

            case R.id.item_train_search:
//                Toast.makeText(getApplicationContext(),"click at train station..", Toast
//                        .LENGTH_SHORT).show();
                break;
            case R.id.item_info:
                if(mainFragmentId != FragmentUtils.FRAGMENT_INFO) {
                    mainFragmentId = FragmentUtils.FRAGMENT_INFO;
                    create = true;
                }
                break;
            case R.id.item_about:
                if(mainFragmentId != FragmentUtils.FRAGMENT_ABOUT) {
                    mainFragmentId = FragmentUtils.FRAGMENT_ABOUT;
                    create = true;
                }
                break;
            case R.id.item_settings:
                if(mainFragmentId != FragmentUtils.FRAGMENT_SETTINGS) {
                    mainFragmentId = FragmentUtils.FRAGMENT_SETTINGS;
                    create = true;
                }
                break;
        }
        if(create) createFragment(mainFragmentId);

    }

    @Override
    public void onRestartButtonClicked(boolean isClicked, String fragmentName) {
        Log.d(TAG, "restart callback!");
        if(isClicked) createFragment(fragmentName);
    }
}

//    @Override
//    public void onBackPressed() {
//        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
//        if (drawer.isDrawerOpen(GravityCompat.START)) {
//            drawer.closeDrawer(GravityCompat.START);
//        }
//        if (!viewIsAtHome) { //if the current view is not the News fragment
//            displayView(R.id.nav_news); //display the News fragment
//        } else {
//            moveTaskToBack(true);  //If view is in News fragment, exit application
//        }
//    }
