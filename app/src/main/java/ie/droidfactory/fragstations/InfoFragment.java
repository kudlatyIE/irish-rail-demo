package ie.droidfactory.fragstations;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
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
    private UserTimeline userTimeline;
    private TweetTimelineListAdapter tweetAdapter;
    private List<Tweet> tweetList;
    private ProgressDialog dialog;

    private AsyncStationsList.AsyncDoneCallback asyncStationsListDone = new AsyncStationsList
            .AsyncDoneCallback() {
        @Override
        public void onAsyncDone(boolean done) {
            Log.d(TAG, "async success: "+done);
            Log.d(TAG, "async result: "+ RailSingleton.getAsyncResult());

            if(done){
                //TODO: now download NEWS from twitter
            }
        }
    };

    private AsyncStationsList.AsyncDoneCallback asyncNewsDone = new AsyncStationsList
            .AsyncDoneCallback() {
        @Override
        public void onAsyncDone(boolean done) {
            Log.d(TAG, "async success: "+done);
            Log.d(TAG, "async result: "+ RailSingleton.getAsyncResult());

            if(done){
                //TODO: download NEWS from twitter
            }
        }
    };

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
        View v = inflater.inflate(R.layout.fragment_info, container, false);
        tvInfo = (TextView) v.findViewById(R.id.fragment_info_text_info);
        lvTweets = v.findViewById(R.id.fragment_info_tweet_list);
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
    }
    @Override
    public  void onStart() {
        // TODO Auto-generated method stub
        super.onStart();
        Bundle extras = getArguments();
        if (extras != null) {
            info = extras.getString(FragmentUtils.FRAGMENT_INFO);
            tvInfo.setText(info);
        }

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onActivityCreated(savedInstanceState);
        downloadTweets();
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

    private void downloadTweets(){
        runDialog();
        tweetList = new ArrayList<>();
        TwitterApiClient twitterApiClient = TwitterCore.getInstance().getApiClient();
        StatusesService statusesService = twitterApiClient.getStatusesService();
        Call<List<Tweet>> call = statusesService.userTimeline(null, "IrishRail", 20, null, null, false, false, false, false);
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
                if(dialog!=null && dialog.isShowing()){
                    dialog.dismiss();
                }
                if(result.data!= null) {
                    Log.d(TAG, "twitter response size: "+result.data.size());
//                    tweetList = result.data;
                    for (Tweet t: result.data){
                        if(!t.text.contains("@")) tweetList.add(t);
                    }
                    loadTweetsList(getActivity(), lvTweets, tweetList);
                }

            }
            @Override
            public void failure(TwitterException exception) {
                if(dialog!=null && dialog.isShowing()){
                    dialog.dismiss();
                }
                tvInfo.setText(exception.getMessage());
                exception.printStackTrace();
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
    private void loadTweetsList(Context context, ListView listView, List<Tweet> list){
        TweetAdapter adapter = new TweetAdapter(context, list);
        listView.setAdapter(adapter);
    }

    private class TweetAdapter extends BaseAdapter{

        Holder h;
        List<Tweet> tweets;
        LayoutInflater inflater;
        DateFormat df =  new SimpleDateFormat("EEE MMM dd HH:mm:ss Z yyyy", Locale.ENGLISH);
        DateFormat sf = new SimpleDateFormat("dd MMM HH:mm", Locale.ENGLISH);

        TweetAdapter(Context c, List<Tweet> tweets){
            this.tweets=tweets;
            this.inflater = LayoutInflater.from(c);
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
                v = inflater.inflate(R.layout.adapter_twitter, viewGroup,false);
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

    private void downloadAllStationList(){
        LocationUtils.getLocation(getActivity());
        AsyncStationsList rail = new AsyncStationsList(getActivity(), AsyncMode.GET_ALL_STATIONS, asyncStationsListDone);
        rail.execute(Links.ALL_STATIONS.getRailLink());
    }


    private void updateDetails(String info){
        this.info =info;
        tvInfo.setText(info);
    }

}
