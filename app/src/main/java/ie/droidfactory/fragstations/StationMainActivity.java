package ie.droidfactory.fragstations;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.ArrayList;

import ie.droidfactory.fragstations.model.RailInterface;
import ie.droidfactory.fragstations.model.Station;
import ie.droidfactory.fragstations.model.Train;
import ie.droidfactory.fragstations.utils.CustomEndDialog;
import ie.droidfactory.fragstations.utils.FragmentUtils;
import ie.droidfactory.fragstations.utils.PermissionUtils;
import ie.droidfactory.fragstations.utils.RailSingleton;

/**
 * Created by kudlaty on 02/06/2016.
 */
public class StationMainActivity extends AppCompatActivity implements RailInterface,
        AllStationsMapFragment.RestartCallback{//}, ActivityCompat.OnRequestPermissionsResultCallback,PermissionResultCallback {

    private final static String TAG = StationMainActivity.class.getSimpleName();
//    public  static final int PERMISSIONS_MULTIPLE_REQUEST = 123;
    public final static String FRAG_SUBDETAILS = "frag_subdetails";
    public final static String FRAG_DETAILS = "frag_details";//, FRAG_STATIONS="frag_stations";
    public final static String FRAG_MAIN="frag_main";
    private Fragment detailsFragment;
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

    public static Activity suomi;
    private CustomEndDialog dialog;

    private boolean isPermissionGranted=false;
    private ArrayList<String> permissions=new ArrayList<>();
    private PermissionUtils permissionUtils;
    private Bundle savedInstanceState;
    protected OnBackPressedListener onBackPressedListener;


//    public OnBackPressedListener killListener = new OnBackPressedListener() {
//        @Override
//        public void doBack() {
//
//        }
//
//        @Override
//        public void doKill(boolean kill) {
//            if(kill) {
//                ft = getSupportFragmentManager().beginTransaction();
//                if(detailsFragment!=null) ft.remove(detailsFragment);
//                if(mainFragment!=null) ft.remove(mainFragment);
//                ft.commit();
//                getSupportFragmentManager().executePendingTransactions();
//                finish();
//            }
//            else dialog.dismiss();
//        }
//    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.savedInstanceState=savedInstanceState;
//        setContentView(R.layout.main_layout);
        //TODO: try layout with drawer and toolbar

//        permissionUtils=new PermissionUtils(this);
//        permissions.add(android.Manifest.permission.INTERNET);
//        permissions.add(android.Manifest.permission.ACCESS_FINE_LOCATION);
//        permissions.add(android.Manifest.permission.ACCESS_WIFI_STATE);
//
//        permissionUtils.check_permission(permissions,"dialog context", PERMISSIONS_MULTIPLE_REQUEST );
        loadFragments(true);
    }

    private void loadFragments(boolean isPermissionGranted){

        if(isPermissionGranted){
            setContentView(R.layout.drawer_layout);
            suomi = this;
//        MainActivity.suomi.finish();
            isDualPane = getResources().getBoolean(R.bool.has_two_panes);
            isTablet = getResources().getBoolean(R.bool.is_tablet);
            Log.d(TAG, "id landscape layout: "+isDualPane);

        /*
        add toolbar and drawer... and make them work!
        1. initialization drawer....
         */
            isDrawerVisible = getResources().getBoolean(R.bool.mini_drawer_visible);

            Toolbar toolbar = (Toolbar) findViewById(R.id.main_toolbar);
            setSupportActionBar(toolbar);

            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

//        getSupportActionBar().setTitle(R.string.test_drawer_main_text);
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

////                    if(item.getGroupId() == R.id.menu_group_main){
//                        navigationView.getMenu().setGroupCheckable(R.id.menu_group_main, (item.getGroupId() == R.id.menu_group_main), true);
//                        navigationView.getMenu().setGroupCheckable(R.id.menu_group_other, (item.getGroupId() == R.id.menu_group_other), true);
////                    }else {
////                        navigationView.getMenu().setGroupCheckable(R.id.menu_group_main, true, true);
////                        navigationView.getMenu().setGroupCheckable(R.id.menu_group_other, false, true);
////                    }
//                    item.setChecked(true);
                    if(item.isChecked()) item.setChecked(false);
                    else item.setChecked(true);

                    drawerLayout.closeDrawers();
                    setFragmentFromDrawer(item.getItemId());
                    return true;
                }
            });
            detailsFragment = getSupportFragmentManager().findFragmentByTag(FRAG_DETAILS);

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
                detailsView = findViewById(R.id.fragment_station_details_container);
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

    }

//    @Override
//    public void onBackPressed() {
//        Log.d(TAG, "onBackPresed invoked...");
//        if(!MainFragment.handleBackPressed(getSupportFragmentManager())){
//            super.onBackPressed();
//        }else {
//            dialog = new CustomEndDialog(this);
//            dialog.show();
//        }
//    }
    @Override
    public void onBackPressed() {
        Log.d(TAG, "onBackPressed() call...............");
        if (onBackPressedListener != null)
            onBackPressedListener.doBack();
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
//                super.onBackPressed();
                dialog = new CustomEndDialog(this);//, killListener);
                dialog.show();
            }
        }else {
//            if(detailsFragment!=null && detailsFragment.isVisible()) super.onBackPressed();
            if(mainFragment!=null && mainFragment.isVisible()){
                dialog = new CustomEndDialog(this);//, killListener);
                dialog.show();
            }else {
                updateViews(detailsView);
                super.onBackPressed();
            }
        }
    }



    private void runAlertDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure you want to exit?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        finish();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }


    @Override
    public void onStationSelected(String id) {
        RailSingleton.resetTimetable();
        Station station = RailSingleton.getStationMap().get(id);
        stationCode = station.getStationCode();
        double lat = station.getStationLatitude();
        double lo = station.getStationLongitude();

        Log.d(TAG, "station selected on list: "+id+" code: "+stationCode);
        detailsFragment = getSupportFragmentManager().findFragmentByTag(FRAG_DETAILS);
        FragmentTransaction ft;

        if(!isDualPane){//PORTRAIT - SINGLE PANE MDE
            this.mId=id;
            Log.d(TAG, "click PORT, list container NULL: "+(findViewById(R.id
                    .fragment_station_list_container)==null));
            Log.d(TAG, "click PORT, details container NULL: "+(findViewById(R.id
                    .fragment_station_details_container)==null));

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
            detailsView = findViewById(R.id.fragment_station_details_container);
            Log.d(TAG, "click LAND, list container NULL: "+(findViewById(R.id
                    .fragment_station_list_container)==null));
            Log.d(TAG, "LAND, details container NULL: "+(findViewById(R.id
                    .fragment_station_details_container)==null));
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

    @Override
    public void onTrainSelected(String trainId) {
        RailSingleton.resetTimetable();
        Train train = RailSingleton.getTrainMap().get(trainId);
        String trainCode = train.getTrainCode();
        String direction = train.getDirection();
        //TODO: fix break line char from XML
        String msg = train.getMessage();
        double lat = train.getLatitude();
        double lo = train.getLongitude();

        Log.d(TAG, "train selected on list: "+trainCode+" code: "+trainId);
        detailsFragment = getSupportFragmentManager().findFragmentByTag(FRAG_DETAILS);
        FragmentTransaction ft;

        if(!isDualPane){//PORTRAIT - SINGLE PANE MDE
            this.mId=trainId;
            Log.d(TAG, "click PORT, list container NULL: "+(findViewById(R.id
                    .fragment_station_list_container)==null));
            Log.d(TAG, "click PORT, details container NULL: "+(findViewById(R.id
                    .fragment_station_details_container)==null));

            detailsFragment = new TrainDetailsFragment();
            Bundle args = new Bundle();
            args.putString(FragmentUtils.PARENT_POSITION_KEY, trainId);
            args.putString(FragmentUtils.STATION_CODE, trainCode);
            args.putDouble(FragmentUtils.STATION_LAT, lat);
            args.putDouble(FragmentUtils.STATION_LONG, lo);
            args.putString(FragmentUtils.TRAIN_DESCRIPTION, direction);
            args.putString(FragmentUtils.TRAIN_MSG, msg);
            detailsFragment.setArguments(args);
            ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.fragment_station_list_container, detailsFragment, FRAG_DETAILS);
            ft.addToBackStack(null);
            ft.commit();

//			}
            //LANDSCAPE - DUAL PANE MODE
        }else{
            detailsView = findViewById(R.id.fragment_station_details_container);
            Log.d(TAG, "click LAND, list container NULL: "+(findViewById(R.id
                    .fragment_station_list_container)==null));
            Log.d(TAG, "LAND, details container NULL: "+(findViewById(R.id
                    .fragment_station_details_container)==null));
            if(!detailsView.isShown()){
                Log.w(TAG, "details view is GONE, restore view....");
                detailsView.setVisibility(View.VISIBLE);
            }
            detailsFragment = new TrainDetailsFragment();
            Bundle args = new Bundle();
            args.putString(FragmentUtils.PARENT_POSITION_KEY, trainId);
            args.putString(FragmentUtils.STATION_CODE, trainCode);
            args.putDouble(FragmentUtils.STATION_LAT, lat);
            args.putDouble(FragmentUtils.STATION_LONG, lo);
            args.putString(FragmentUtils.TRAIN_DESCRIPTION, direction);
            detailsFragment.setArguments(args);
            ft = getSupportFragmentManager().beginTransaction();
            if(detailsFragment!=null) {
                //TODO: dont reload the same details fragment if exist
                if(trainId==mId) return;
                else {
                    ft.replace(R.id.fragment_station_details_container, detailsFragment,FRAG_DETAILS);
                    mId=trainId;
                    ft.addToBackStack(null);
                    ft.commit();
                }
            }else {
                ft.add(R.id.fragment_station_details_container, detailsFragment,FRAG_DETAILS);
                mId=trainId;
                ft.addToBackStack(null);
                ft.commit();
            }

        }
    }

    @Override
    public void onStationSelectedFromTrain(String stationId) {
        RailSingleton.resetTimetable();
        Station station = RailSingleton.getStationMap().get(stationId);
        try{
            stationCode = station.getStationCode();
        }catch(NullPointerException e){
            //TODO: load extra data for this station, now just return null
            Toast.makeText(getApplicationContext(), "it is just a technical station",
                    Toast.LENGTH_SHORT).show();
            return;
        }
        double lat = station.getStationLatitude();
        double lo = station.getStationLongitude();

        Log.d(TAG, "station selected on list: "+stationId+" code: "+stationCode);
        detailsFragment = getSupportFragmentManager().findFragmentByTag(FRAG_DETAILS);
        FragmentTransaction ft;
        Fragment currentFragment;
        if(!isDualPane){//PORTRAIT - SINGLE PANE MDE
            //remove preview view
            this.mId=stationId;
            Log.d(TAG, "click PORT, list container NULL: "+(findViewById(R.id
                    .fragment_station_list_container)==null));
            Log.d(TAG, "click PORT, details container NULL: "+(findViewById(R.id
                    .fragment_station_details_container)==null));

            detailsFragment = new StationDetailsFragment();
            Bundle args = new Bundle();
            args.putString(FragmentUtils.PARENT_POSITION_KEY, stationId);
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
            detailsView = findViewById(R.id.fragment_station_details_container);
            Log.d(TAG, "click LAND, list container NULL: "+(findViewById(R.id
                    .fragment_station_list_container)==null));
            Log.d(TAG, "LAND, details container NULL: "+(findViewById(R.id
                    .fragment_station_details_container)==null));
            if(!detailsView.isShown()){
                Log.w(TAG, "details view is GONE, restore view....");
                detailsView.setVisibility(View.VISIBLE);
            }
            detailsFragment = new StationDetailsFragment();
            Bundle args = new Bundle();
            args.putString(FragmentUtils.PARENT_POSITION_KEY, stationId);
            args.putString(FragmentUtils.STATION_CODE, stationCode);
            args.putDouble(FragmentUtils.STATION_LAT, lat);
            args.putDouble(FragmentUtils.STATION_LONG, lo);
            detailsFragment.setArguments(args);
            ft = getSupportFragmentManager().beginTransaction();
            if(detailsFragment!=null) {
                //TODO: dont reload the same details fragment if exist
                if(stationId==mId) return;
                else {
                    ft.add(R.id.fragment_station_details_container, detailsFragment,FRAG_DETAILS);
                    if((currentFragment = getSupportFragmentManager().findFragmentById(R.id
                            .fragment_station_details_container))!=null){
                        ft.hide(currentFragment);
                    }
                    mId=stationId;
                    ft.addToBackStack(null);
                    ft.commit();
                }
            }else {
                ft.add(R.id.fragment_station_details_container, detailsFragment,FRAG_DETAILS);
                if((currentFragment = getSupportFragmentManager().findFragmentById(R.id
                        .fragment_station_list_container))!=null){
                    ft.hide(currentFragment);
                }
                mId=stationId;
                ft.addToBackStack(null);
                ft.commit();
            }

        }
    }

    private void updateViews(View detail){
        if(isDualPane){
            if(detail!=null){
                if(getSupportFragmentManager().findFragmentByTag(FRAG_DETAILS)==null){
                    detail.setVisibility(View.GONE);
                }else detail.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(dialog!=null && dialog.isShowing()){
            dialog.dismiss();
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
       ft.disallowAddToBackStack();
       ft.setTransitionStyle(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
       mainFragment = (MainFragment) setMainFragment(key);
       mainFragment.setStationSelectedListener(this);
       ft.replace(R.id.fragment_station_list_container, mainFragment, FRAG_MAIN).commit();
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
            case FragmentUtils.FRAGMENT_ALL_TRAINS_MAP:
                arg = new Bundle();
                frag = AllTrainsMapFragment.newInstance(arg);
                break;
            case FragmentUtils.FRAGMENT_INFO:
                arg = new Bundle();
                arg.putString(FragmentUtils.FRAGMENT_INFO, null);
                frag = InfoFragment.newInstance(arg);
                break;
            case FragmentUtils.FRAGMENT_ABOUT:
                arg = new Bundle();
                arg.putString(FragmentUtils.FRAGMENT_ABOUT, "that is about everything!");
                frag = AboutFragment.newInstance(arg);
                break;
//            case FragmentUtils.FRAGMENT_HELP:
//                arg = new Bundle();
//                arg.putString(FragmentUtils.FRAGMENT_SETTINGS, "my little help here!");
//                frag = SettingsFragment.newInstance(arg);
//                break;
            default: frag = new StationListFragment();
        }
        return frag;
    }

    private void setFragmentFromDrawer(int index){
        boolean create = false;
        switch (index){
            case R.id.item_info:
                if(!mainFragmentId.equals(FragmentUtils.FRAGMENT_INFO)) {
                    mainFragmentId = FragmentUtils.FRAGMENT_INFO;
                    create = true;
                }
                Log.i(TAG, "setFragmentFromDrawer:::mainFragmentId: "+mainFragmentId);
                break;

            case R.id.item_station_list:
                if(!mainFragmentId.equals(FragmentUtils.FRAGMENT_LIST)) {
                    mainFragmentId = FragmentUtils.FRAGMENT_LIST;
                    create = true;
                }
                Log.i(TAG, "setFragmentFromDrawer:::mainFragmentId: "+mainFragmentId);
                break;

            case R.id.item_station_map:
                if(!mainFragmentId.equals(FragmentUtils.FRAGMENT_ALL_MAP)) {
                    mainFragmentId = FragmentUtils.FRAGMENT_ALL_MAP;
                    create=true;
                }
                Log.i(TAG, "setFragmentFromDrawer:::mainFragmentId: "+mainFragmentId);
                break;

            case R.id.item_train_map:
                if(!mainFragmentId.equals(FragmentUtils.FRAGMENT_ALL_TRAINS_MAP)) {
                    mainFragmentId = FragmentUtils.FRAGMENT_ALL_TRAINS_MAP;
                    create=true;
                }
                Log.i(TAG, "setFragmentFromDrawer:::mainFragmentId: "+mainFragmentId);
                break;

//            case R.id.item_help:
//                if(!mainFragmentId.equals(FragmentUtils.FRAGMENT_HELP)) {
//                    mainFragmentId = FragmentUtils.FRAGMENT_HELP;
//                    create = true;
//                }
//                Log.i(TAG, "setFragmentFromDrawer:::mainFragmentId: "+mainFragmentId);
//                break;
            case R.id.item_about:
                if(!mainFragmentId.equals(FragmentUtils.FRAGMENT_ABOUT)) {
                    mainFragmentId = FragmentUtils.FRAGMENT_ABOUT;
                    create = true;
                }
                Log.i(TAG, "setFragmentFromDrawer:::mainFragmentId: "+mainFragmentId);
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
