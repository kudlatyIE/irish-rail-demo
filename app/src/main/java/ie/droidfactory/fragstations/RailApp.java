package ie.droidfactory.fragstations;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;

import com.twitter.sdk.android.core.Twitter;

import org.acra.ACRA;
import org.acra.config.ACRAConfigurationException;

import ie.droidfactory.fragstations.utils.ACRAReporter;

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
    }
    


}
/* send report to email

@ReportsCrashes(formKey = "", // will not be used
        mailTo = "reports@yourdomain.com", // my email here
        mode = ReportingInteractionMode.TOAST,
        resToastText = R.string.crash_toast_text)

        ------------

        ACRA static declaration:

         @ReportsCrashes(
        formUri = "https://kudlatyie.cloudant.com/acra-irerail/_design/acra-storage/_update/report",
        reportType = HttpSender.Type.JSON,
        httpMethod = HttpSender.Method.POST,
        formUriBasicAuthLogin = "fortyloughtnescordelisai" ,
        formUriBasicAuthPassword = "ea3e3b25b79d6baa2f6b3c4dfa0be4d9e9909495",
//        formKey = "", // This is required for backward compatibility but not used
        customReportContent = {
                ReportField.APP_VERSION_CODE,
                ReportField.APP_VERSION_NAME,
                ReportField.ANDROID_VERSION,
                ReportField.PACKAGE_NAME,
                ReportField.REPORT_ID,
                ReportField.BUILD,
                ReportField.STACK_TRACE
        },
        mode = ReportingInteractionMode.TOAST,
        resToastText = R.string.toast_crash
)
*/
