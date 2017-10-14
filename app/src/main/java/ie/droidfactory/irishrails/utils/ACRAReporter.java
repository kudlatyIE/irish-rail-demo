package ie.droidfactory.irishrails.utils;

import android.app.Application;
import android.content.Context;

import org.acra.ReportField;
import org.acra.ReportingInteractionMode;
import org.acra.config.ACRAConfiguration;
import org.acra.config.ACRAConfigurationException;
import org.acra.config.ConfigurationBuilder;
import org.acra.sender.HttpSender;

import ie.droidfactory.irishrails.R;

/**
 * Created by kudlaty on 13/06/2016.
 *
 * it works o_O
 */
public class ACRAReporter  {

    private final static String URL="url", USER="user", PASS="pass";

    private static ReportField[] fields = {ReportField.APP_VERSION_CODE,
            ReportField.APP_VERSION_NAME,
            ReportField.ANDROID_VERSION,
            ReportField.PACKAGE_NAME,
            ReportField.PHONE_MODEL,
            ReportField.BUILD,
            ReportField.STACK_TRACE,
            ReportField.CUSTOM_DATA};

   public static ACRAConfiguration acraConfig(Application app) throws ACRAConfigurationException {

       ConfigurationBuilder cb = new ConfigurationBuilder(app)
               .setFormUri(getACRA(app, URL))
               .setHttpMethod(HttpSender.Method.POST)
               .setReportType(HttpSender.Type.JSON)
               .setFormUriBasicAuthLogin(getACRA(app, USER))
               .setFormUriBasicAuthPassword(getACRA(app, PASS))
               .setCustomReportContent(fields)
//               setResDialogOkToast(R.string.toast_crash).
      //TODO: create custom dialog here
              .setReportingInteractionMode(ReportingInteractionMode.DIALOG)
              .setReportDialogClass(ACRACrashReportDialog.class)
//              .setMailTo("kudlaty.ie@gmail.com")
               ;

       return cb.build();
   }

    private static String getACRA(Application a, String res){
        Context c = a.getBaseContext();
        switch(res){
            case URL:
                return c.getResources().getString(R.string.acra_db_url);
            case USER:
                return c.getResources().getString(R.string.acra_user);
            case PASS:
                return c.getResources().getString(R.string.acra_password);
        }
        return null;
    }

}
