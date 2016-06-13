package ie.droidfactory.fragstations;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;

/**
 * Created by kudlaty on 12/06/2016.
 */
public class RailApp extends Application{
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }
}
