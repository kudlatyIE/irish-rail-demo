package ie.droidfactory.irishrails;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
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
import android.view.ContextThemeWrapper;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import org.w3c.dom.Text;

import ie.droidfactory.irishrails.model.RailInterface;
import ie.droidfactory.irishrails.model.Station;
import ie.droidfactory.irishrails.model.Train;
import ie.droidfactory.irishrails.utils.BitmapHelper;
import ie.droidfactory.irishrails.utils.CustomEndDialog;
import ie.droidfactory.irishrails.utils.FragmentUtils;
import ie.droidfactory.irishrails.utils.RailSingleton;

/**
 * Created by kudlaty on 02/06/2016.
 */
public class StationMainActivity extends AppCompatActivity implements RailInterface
        ,
        AllStationsMapFragment.RestartCallback{//}, ActivityCompat.OnRequestPermissionsResultCallback,PermissionResultCallback {

    private final static String TAG = StationMainActivity.class.getSimpleName();
    public final static String FRAG_DETAILS = "frag_details";//, FRAG_STATIONS="frag_stations";
    public final static String FRAG_MAIN="frag_main";
    private Fragment detailsFragment;
    private MainFragment mainFragment;
    private String mId = null;
    private String stationCode=null;
    private boolean isDualPane, isTablet;
    private View detailsView;
    private FragmentTransaction ft;
    private static String mainFragmentId = FragmentUtils.FRAGMENT_INFO;
    private DrawerLayout drawerLayout;
    private LinearLayout drawerHeader;
    private boolean isHeaderImageExist = false;
    private TextView tvTitle;
    public Activity suomi;
    private CustomEndDialog dialog;
    private AdView mAdView;


    private Bundle savedInstanceState;
    protected OnBackPressedListener onBackPressedListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.savedInstanceState=savedInstanceState;
//        setContentView(R.layout.main_layout);
        //TODO: try layout with drawer and toolbar
        setContentView(R.layout.drawer_layout);
        suomi = this;
        isDualPane = getResources().getBoolean(R.bool.has_two_panes);
        isTablet = getResources().getBoolean(R.bool.is_tablet);
        Log.d(TAG, "id landscape layout: "+isDualPane);

        if(!isTablet){
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
        }

        int headerImageId = getResources().getIdentifier("img_drawer_header_v2", "raw","ie.droidfactory.irishrails");
        Log.d(TAG, "real IMG detect res: "+headerImageId);
        if(headerImageId!=0) isHeaderImageExist=true;
        Toolbar toolbar = findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        drawerLayout = findViewById(R.id.drawer_layout);

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

        NavigationView navigationView = findViewById(R.id.main_navigation_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                tvTitle = findViewById(R.id.drawer_header_text_info);
                tvTitle.setVisibility(View.GONE);
                if(isHeaderImageExist){
                    drawerHeader = findViewById(R.id.drawer_header_layout_main);
                    int headerHeight = drawerHeader.getHeight();
                    int headerWidth = drawerHeader.getWidth();
                    Bitmap res = BitmapFactory.decodeResource(getResources(), R.raw.img_drawer_header_v2);
                    int resW = res.getWidth();
                    int resH = res.getHeight();
                    double ratio = resH/headerHeight;
                    headerWidth = (int) (resW*ratio);
                    Bitmap btmNew = BitmapHelper.decodeResource(suomi, R.raw.img_drawer_header_v2, headerWidth, headerHeight);
                    BitmapDrawable ob = new BitmapDrawable(btmNew);
                    drawerHeader.setBackgroundDrawable(ob);
                }

                if(item.isChecked()) item.setChecked(false);
                else item.setChecked(true);

                drawerLayout.closeDrawers();
                setFragmentFromDrawer(item.getItemId());
                return true;
            }
        });
        detailsFragment = getSupportFragmentManager().findFragmentByTag(FRAG_DETAILS);
        detailsView = findViewById(R.id.fragment_station_details_container);
        if(mainFragmentId.equals(FragmentUtils.FRAGMENT_INFO)) getSupportActionBar().setTitle(getResources().getString(R.string.news));
        loadFragments(mainFragmentId);


    }

    private void loadFragments(String key){

            //SINGLE PANE - PORTRAIT
            if(!isDualPane){
                if(detailsFragment!=null){
                    if(isTablet){
                        ft = getSupportFragmentManager().beginTransaction();
                        getSupportFragmentManager().popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                        ft.remove(detailsFragment).commit();
                        getSupportFragmentManager().executePendingTransactions();
                        ft = getSupportFragmentManager().beginTransaction();
                        if(detailsFragment==null){
//                            Log.d(TAG, "whoa! details fragment is NULL!");
                            return;
                        }else {
                            ft.replace(R.id.fragment_station_list_container, detailsFragment, FRAG_DETAILS);
                            ft.addToBackStack(null);
                            ft.commit();
                            getSupportFragmentManager().executePendingTransactions();
                        }
                    }

                }else{
                    if(findViewById(R.id.fragment_station_list_container)!=null) {
                        if(savedInstanceState!=null) return; // TODO: check is fragment is savedInstanceState
                        if(mainFragment==null){
                            ft = getSupportFragmentManager().beginTransaction();
                            mainFragment = (MainFragment) setMainFragment(key);
                            mainFragment.setStationSelectedListener(this);
                            ft.add(R.id.fragment_station_list_container, mainFragment, FRAG_MAIN).commit();
                            getSupportFragmentManager().executePendingTransactions();
                        }else{
                            return;
                        }
                    }
                }

                //DUAL PANE - LANDSCAPE
            }else{
                if(detailsView!=null) updateViews(detailsView);
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
                        getSupportFragmentManager().executePendingTransactions();
                    }else {
                        if(mainFragment==null){
                            ft = getSupportFragmentManager().beginTransaction();
                            mainFragment = (MainFragment) setMainFragment(key);
                            mainFragment.setStationSelectedListener(this);
                            ft.replace(R.id.fragment_station_list_container, mainFragment, FRAG_MAIN);
//                            ft.addToBackStack(null);
                            ft.commit();
                            getSupportFragmentManager().executePendingTransactions();
                        }else return;
                    }
                }else{
                    ft = getSupportFragmentManager().beginTransaction();
                    mainFragment = (MainFragment) setMainFragment(key);
                    mainFragment.setStationSelectedListener(this);
                    ft.replace(R.id.fragment_station_list_container, mainFragment, FRAG_MAIN);
                    ft.addToBackStack(null);
                    ft.commit();
                    getSupportFragmentManager().executePendingTransactions();
                }
            }
            getSupportFragmentManager().addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {

                @Override
                public void onBackStackChanged() {
                    updateViews(detailsView);
                }
            });
        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

    }

    @Override
    public void onBackPressed() {
        if (onBackPressedListener != null)
            onBackPressedListener.doBack();
        if(isDualPane){
            if( detailsFragment!=null){
                ft = getSupportFragmentManager().beginTransaction();
                getSupportFragmentManager().popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                ft.remove(detailsFragment);
                ft.commit();
                getSupportFragmentManager().executePendingTransactions();
                detailsFragment=null;
                updateViews(detailsView);

            }else{
//                dialog = new CustomEndDialog(this);
//                dialog.show();
                exitApp();
            }
        }else {
            if(mainFragment!=null && mainFragment.isVisible()){
//                dialog = new CustomEndDialog(this);
//                dialog.show();
                exitApp();
            }else {
                updateViews(detailsView);
                super.onBackPressed();
            }
        }
    }

    private void exitApp(){
        AlertDialog.Builder bld;

        bld = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.CustomHoloDialog));

        bld.setTitle("Exit App");
        bld.setMessage("Are you sure you want to exit?");

        bld.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) { dialog.dismiss(); }
        });
        bld.setPositiveButton("Exit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) { finish(); }
        });
        bld.setCancelable(true);
        bld.create().show();
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
            if(!detailsView.isShown()){
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
//                if(id.equals(mId)) return;
//                else {
                    ft.replace(R.id.fragment_station_details_container, detailsFragment,FRAG_DETAILS);
                    mId=id;
                    ft.addToBackStack(null);
                    ft.commit();
//                }
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

            //LANDSCAPE - DUAL PANE MODE
        }else{
            detailsView = findViewById(R.id.fragment_station_details_container);
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
            args.putString(FragmentUtils.TRAIN_MSG, msg);
            detailsFragment.setArguments(args);
            ft = getSupportFragmentManager().beginTransaction();
            if(detailsFragment!=null) {
                //TODO: dont reload the same details fragment if exist
//                if(trainId.equals(mId)) return;
//                else {
                    ft.replace(R.id.fragment_station_details_container, detailsFragment,FRAG_DETAILS);
                    mId=trainId;
                    ft.addToBackStack(null);
                    ft.commit();
//                }
            }else {
                ft.add(R.id.fragment_station_details_container, detailsFragment,FRAG_DETAILS);
                mId=trainId;
                ft.addToBackStack(null);
                ft.commit();
            }
        }
    }

    @Override
    public void onTweetSelected(String tweetUrl) {
        Log.d(TAG, "tweet selected from list: "+tweetUrl);
        detailsFragment = getSupportFragmentManager().findFragmentByTag(FRAG_DETAILS);
        FragmentTransaction ft;
        if(!isDualPane){//PORTRAIT - SINGLE PANE MDE
            this.mId=tweetUrl;

            detailsFragment = new InfoDetailsFragment();
            Bundle args = new Bundle();
            args.putString(FragmentUtils.FRAGMENT_TWEETER_URL, tweetUrl);
            detailsFragment.setArguments(args);
            ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.fragment_station_list_container, detailsFragment, FRAG_DETAILS);
            ft.addToBackStack(null);
            ft.commit();

            //LANDSCAPE - DUAL PANE MODE
        }else{
            detailsView = findViewById(R.id.fragment_station_details_container);
            if(!detailsView.isShown()){
                Log.w(TAG, "details view is GONE, restore view....");
                detailsView.setVisibility(View.VISIBLE);
            }
            detailsFragment = new InfoDetailsFragment();
            Bundle args = new Bundle();
            args.putString(FragmentUtils.FRAGMENT_TWEETER_URL, tweetUrl);
            detailsFragment.setArguments(args);
            ft = getSupportFragmentManager().beginTransaction();
            if(detailsFragment!=null) {
                //TODO: dont reload the same details fragment if exist

                ft.replace(R.id.fragment_station_details_container, detailsFragment,FRAG_DETAILS);
                mId=tweetUrl;
                ft.addToBackStack(null);
                ft.commit();

            }else {
                ft.add(R.id.fragment_station_details_container, detailsFragment,FRAG_DETAILS);
                mId=tweetUrl;
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
           ft = getSupportFragmentManager().beginTransaction();
           getSupportFragmentManager().popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
           ft.remove(detailsFragment);
           ft.commit();
           getSupportFragmentManager().executePendingTransactions();
           detailsFragment=null;
           if (detailsView!=null) updateViews(detailsView);

       }
       if(mainFragment!=null){
           ft = getSupportFragmentManager().beginTransaction();
           getSupportFragmentManager().popBackStackImmediate(null, FragmentManager
                   .POP_BACK_STACK_INCLUSIVE);
           ft.remove(mainFragment);
           ft.commit();
           getSupportFragmentManager().executePendingTransactions();
           mainFragment=null;
       }
       //create new fragment and display in empty main view
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
                arg.putString(FragmentUtils.FRAGMENT_ABOUT, null);
                frag = AboutFragment.newInstance(arg);
                break;
            case FragmentUtils.FRAGMENT_HELP:
                arg = new Bundle();
                arg.putString(FragmentUtils.FRAGMENT_HELP, null);
                frag = HelpFragment.newInstance(arg);
                break;
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
                getSupportActionBar().setTitle(getResources().getString(R.string.news));
                Log.i(TAG, "setFragmentFromDrawer:::mainFragmentId: "+mainFragmentId);
                break;

            case R.id.item_station_list:
                if(!mainFragmentId.equals(FragmentUtils.FRAGMENT_LIST)) {
                    mainFragmentId = FragmentUtils.FRAGMENT_LIST;
                    create = true;
                }
                getSupportActionBar().setTitle(getResources().getString(R.string.stations_list));
                Log.i(TAG, "setFragmentFromDrawer:::mainFragmentId: "+mainFragmentId);
                break;

            case R.id.item_station_map:
                if(!mainFragmentId.equals(FragmentUtils.FRAGMENT_ALL_MAP)) {
                    mainFragmentId = FragmentUtils.FRAGMENT_ALL_MAP;
                    create=true;
                }
                getSupportActionBar().setTitle(getResources().getString(R.string.stations_map));
                Log.i(TAG, "setFragmentFromDrawer:::mainFragmentId: "+mainFragmentId);
                break;

            case R.id.item_train_map:
                if(!mainFragmentId.equals(FragmentUtils.FRAGMENT_ALL_TRAINS_MAP)) {
                    mainFragmentId = FragmentUtils.FRAGMENT_ALL_TRAINS_MAP;
                    create=true;
                }
                getSupportActionBar().setTitle(getResources().getString(R.string.trains_map));
                Log.i(TAG, "setFragmentFromDrawer:::mainFragmentId: "+mainFragmentId);
                break;

            case R.id.item_about:
                if(!mainFragmentId.equals(FragmentUtils.FRAGMENT_ABOUT)) {
                    mainFragmentId = FragmentUtils.FRAGMENT_ABOUT;
                    create = true;
                }
                getSupportActionBar().setTitle(getResources().getString(R.string.about));
                Log.i(TAG, "setFragmentFromDrawer:::mainFragmentId: "+mainFragmentId);
                break;
            case R.id.item_help:
                if(!mainFragmentId.equals(FragmentUtils.FRAGMENT_HELP)) {
                    mainFragmentId = FragmentUtils.FRAGMENT_HELP;
                    create = true;
                }
                getSupportActionBar().setTitle(getResources().getString(R.string.help));
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
