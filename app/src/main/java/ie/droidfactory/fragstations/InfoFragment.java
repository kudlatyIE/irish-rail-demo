package ie.droidfactory.fragstations;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterApiClient;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.models.Tweet;
import com.twitter.sdk.android.core.services.StatusesService;
import com.twitter.sdk.android.tweetui.TweetTimelineListAdapter;
import com.twitter.sdk.android.tweetui.UserTimeline;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import ie.droidfactory.fragstations.httputils.AsyncMode;
import ie.droidfactory.fragstations.httputils.AsyncStationsList;
import ie.droidfactory.fragstations.httputils.Links;
import ie.droidfactory.fragstations.model.RailInterface;
import ie.droidfactory.fragstations.utils.FragmentUtils;
import ie.droidfactory.fragstations.utils.LocationUtils;
import ie.droidfactory.fragstations.utils.RailSingleton;
import retrofit2.Call;

/**
 * Created by kudlaty on 06/06/2016.
 * display info about internet connection here and about a new updates, others... bla, bla
 */
public class InfoFragment extends MainFragment {

    private final static String TAG = InfoFragment.class.getSimpleName();
    private String info = "default nothing";
    private TextView tvInfo;
    private ListView lvTweets;
    private LinearLayout layout1, layout2, layout3;
    private SwipeRefreshLayout swipeRefreshLayout;
    private UserTimeline userTimeline;
    private TweetTimelineListAdapter tweetAdapter;
    private TweetAdapter twAdapter;
    private ArrayList<Tweet> tweetList = null;
    private ProgressDialog dialog;

    private final static int maxTweetsSearch = 200;

    RailInterface stationCallback;
    public void setStationSelectedListener(RailInterface listener){
        stationCallback = listener;
    }

    public static InfoFragment newInstance(Bundle args) {
        InfoFragment fragment = new InfoFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_stations_list, container, false);
        tvInfo = (TextView) v.findViewById(R.id.fragment_stations_main_text_info);
        tvInfo.setVisibility(View.GONE);
        layout1 = v.findViewById(R.id.fragme_station_list_top_1);
        layout2 = v.findViewById(R.id.fragme_station_list_top_2);
        layout3 = v.findViewById(R.id.fragme_station_list_top_3);
        layout1.setVisibility(View.GONE);
        layout2.setVisibility(View.GONE);
        layout3.setVisibility(View.GONE);
        lvTweets = v.findViewById(R.id.fragment_stations_main_listview);
        swipeRefreshLayout = v.findViewById(R.id.fragment_stations_main_swipe_refresh_layout);
//        userTimeline = new UserTimeline.Builder()
//                .screenName("IrishRail")
//                .build();
//        tweetAdapter = new TweetTimelineListAdapter.Builder(getActivity())
//                .setTimeline(userTimeline)
//                .build();
//        lvTweets.setAdapter(tweetAdapter);
        return v;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d(TAG, "onViewCreated....");
        runDialog();
    }
    @Override
    public  void onStart() {
        // TODO Auto-generated method stub
        super.onStart();

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onActivityCreated(savedInstanceState);

        downloadTweets(tweetList);
    }
    @Override
    public void onSaveInstanceState(Bundle outState) {
        // TODO Auto-generated method stub
        super.onSaveInstanceState(outState);
        outState.putSerializable(FragmentUtils.FRAGMENT_TWEETER_LIST, tweetList);

    }
    @Override
    public void onViewStateRestored(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onViewStateRestored(savedInstanceState);
        restore(savedInstanceState);
    }

    public void restore(Bundle savedInstanceState) {
        if(savedInstanceState!=null){
            tweetList = (ArrayList<Tweet>) savedInstanceState.getSerializable(FragmentUtils.FRAGMENT_TWEETER_LIST);
        }
    }

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
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(dialog!=null && dialog.isShowing()){
            dialog.dismiss();
        }
    }

    private void downloadTweets(ArrayList<Tweet> list){
        if(list==null){
            TwitterApiClient twitterApiClient = TwitterCore.getInstance().getApiClient();
            StatusesService statusesService = twitterApiClient.getStatusesService();
            Call<List<Tweet>> call = statusesService.userTimeline(null, "IrishRail", maxTweetsSearch, null, null, false, false, false, false);
//        call.enqueue(new Callback<>() {
//            @Override
//            public void success(Result<Tweet> result) {
//                //Do something with result
//                Log.d(TAG, "tweets: \n"+result.data.text);
//            }
//
//            public void failure(TwitterException exception) {
//                tvInfo.setText(exception.getMessage());
//                exception.printStackTrace();
//            }
//        });
            call.enqueue(new Callback<List<Tweet>>() {
                @Override
                public void success(Result<List<Tweet>> result) {
                    swipeRefreshLayout.setRefreshing(false);
                    if(dialog!=null && dialog.isShowing()){
                        dialog.dismiss();
                    }
                    if(result.data!= null) {
                        ArrayList<Tweet> newList = new ArrayList<>();
                        Log.d(TAG, "twitter response size: "+result.data.size());

                        int count=0;
                        for (Tweet t: result.data){
                            if(!t.text.contains("@")) {
                                ++count;
                                newList.add(t);
                                if(count==10) break;
                            }
                        }
                        tweetList = newList;
                        loadTweets(newList);
                    }
                }
                @Override
                public void failure(TwitterException exception) {
                    swipeRefreshLayout.setRefreshing(false);
                    if(dialog!=null && dialog.isShowing()){
                        dialog.dismiss();
                    }
                    exception.printStackTrace();
                }
            });
        }else {
            loadTweets(list);
        }
    }

    private void loadTweets(ArrayList<Tweet> list){
        twAdapter = new TweetAdapter( list);
        lvTweets.setAdapter(twAdapter);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener(){

            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(true);
            }
        });
    }
    private void runDialog(){
        dialog = new ProgressDialog(getActivity());
        dialog.setMessage("loading news...");
        dialog.setCancelable(false);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.show();
    }


    private class TweetAdapter extends BaseAdapter{

        Holder h;
        List<Tweet> tweets;
//        LayoutInflater inflater;
        DateFormat df =  new SimpleDateFormat("EEE MMM dd HH:mm:ss Z yyyy", Locale.ENGLISH);
        DateFormat sf = new SimpleDateFormat("dd MMM HH:mm", Locale.ENGLISH);

        TweetAdapter(List<Tweet> tweets){
            this.tweets=tweets;
//            this.inflater = LayoutInflater.from(c);
        }
        @Override
        public int getCount() {
            return tweets.size();
        }

        @Override
        public Object getItem(int i) {
            return tweets.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            View v;
            if(view==null){
                this.h = new Holder();
                v = getActivity().getLayoutInflater().inflate(R.layout.adapter_twitter, viewGroup,false);
                h.tvTwDate = v.findViewById(R.id.adapter_twitter_text_date);
                h.tvTwMessage = v.findViewById(R.id.adapter_twitter_text_message);
                v.setTag(h);
            }else {
                v = view;
                h = (Holder) v.getTag();
            }
            try {
                Date date = df.parse(tweets.get(i).createdAt);
                h.tvTwDate.setText(sf.format(date));
                h.tvTwMessage.setText(tweets.get(i).text);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            return v;
        }
    }
    private class Holder{
        TextView tvTwDate, tvTwMessage;
    }

}
