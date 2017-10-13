package ie.droidfactory.irishrails;

import android.app.Activity;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import ie.droidfactory.irishrails.model.RailInterface;

/**
 * Created by kudlaty on 07/06/2016.
 */
public class AboutFragment  extends MainFragment {

    private final static String TAG = InfoFragment.class.getSimpleName();
    private String verison = "default nothing";
    private TextView tvInfo, tvVersion;


    RailInterface stationCallback;
    public void setStationSelectedListener(RailInterface listener){
        stationCallback = listener;
    }

    public static AboutFragment newInstance(Bundle args) {
        AboutFragment fragment = new AboutFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_about, container, false);
    }
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        tvInfo = view.findViewById(R.id.fragment_about_text_info);
        tvVersion = view.findViewById(R.id.fragment_about_text_version);
        Log.d(TAG, "onViewCreated....");
    }

    @Override
    public  void onStart() {
        // TODO Auto-generated method stub
        super.onStart();
        Log.d(TAG, "onStart....");
        Bundle extras = getArguments();
//        if (extras != null) {
//            info = extras.getString(FragmentUtils.FRAGMENT_ABOUT);
//        }
        verison = BuildConfig.VERSION_NAME;
        Log.d(TAG, "onStart....");
//        tvInfo.setText(info);
        tvInfo.setText(Html.fromHtml(getString(R.string.help_about)));
        tvInfo.setMovementMethod(LinkMovementMethod.getInstance());
        tvVersion.setText("v."+verison);
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onActivityCreated(savedInstanceState);
        Log.d(TAG, "onActivityCreated....");
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


}
