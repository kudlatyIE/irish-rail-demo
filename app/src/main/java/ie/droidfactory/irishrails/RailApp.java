package ie.droidfactory.irishrails;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;

import com.google.android.gms.ads.MobileAds;
import com.twitter.sdk.android.core.Twitter;

import org.acra.ACRA;
import org.acra.config.ACRAConfigurationException;

import ie.droidfactory.irishrails.utils.ACRAReporter;

/**
 * Created by kudlaty on 12/06/2016.
 */

public class RailApp extends Application{
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);

        try {
            ACRA.init(this, ACRAReporter.acraConfig(this));
        } catch (ACRAConfigurationException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onCreate(){
        super.onCreate();
        Twitter.initialize(this);
        MobileAds.initialize(this, getResources().getString(R.string.admob_app_id));
    }

}
