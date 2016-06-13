package ie.droidfactory.fragstations;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;

import org.acra.ACRA;

import ie.droidfactory.fragstations.utils.ACRAReporter;

/**
 * Created by kudlaty on 12/06/2016.
 */


public class RailApp extends Application{
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);

//        ReportField[] fields = {ReportField.APP_VERSION_CODE,
//                ReportField.APP_VERSION_NAME,
//                ReportField.ANDROID_VERSION,
//                ReportField.PACKAGE_NAME,
//                ReportField.PHONE_MODEL,
//                ReportField.BUILD,
//                ReportField.STACK_TRACE};
//
//        ConfigurationBuilder build = new ConfigurationBuilder(this).
//                setFormUri("https://kudlatyie.cloudant.com/acra-irerail/_design/acra-storage/_update/report").
//                setHttpMethod(HttpSender.Method.POST).
//                setReportType(HttpSender.Type.JSON).
//                setFormUriBasicAuthLogin(getAcraUser()).
//                setFormUriBasicAuthPassword(getAcraPass()).
//                setCustomReportContent(fields).
//                setResDialogOkToast(R.string.toast_crash).
//                setMailTo("kudlaty.ie@gmail.com");
//
//        ACRAConfiguration conf = build.build();


        ACRA.init(this, ACRAReporter.acraConfig(this));
    }

    @Override
    public void onCreate(){
        super.onCreate();
//        ACRA.init(this);
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
