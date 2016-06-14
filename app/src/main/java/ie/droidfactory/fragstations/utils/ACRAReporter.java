package ie.droidfactory.fragstations.utils;

import android.app.Application;
import android.content.Context;

import org.acra.ReportField;
import org.acra.ReportingInteractionMode;
import org.acra.config.ACRAConfiguration;
import org.acra.config.ACRAConfigurationException;
import org.acra.config.ConfigurationBuilder;
import org.acra.sender.HttpSender;

import ie.droidfactory.fragstations.R;

/**
 * Created by kudlaty on 13/06/2016.
 *
 * followed on http://androidcocktail.blogspot.ie/2015/05/silentemail-crash-report-using-acra.html
 */
public class ACRAReporter  {

    private static ReportField[] fields = {ReportField.APP_VERSION_CODE,
            ReportField.APP_VERSION_NAME,
            ReportField.ANDROID_VERSION,
            ReportField.PACKAGE_NAME,
            ReportField.PHONE_MODEL,
            ReportField.BUILD,
            ReportField.STACK_TRACE,
            ReportField.CUSTOM_DATA};

   public static ACRAConfiguration acraConfig(Application app) throws ACRAConfigurationException {
       ConfigurationBuilder build = new ConfigurationBuilder(app)
               .setFormUri("https://kudlatyie.cloudant" +
                       ".com/acra-irerail/_design/acra-storage/_update/report")
               .setHttpMethod(HttpSender.Method.POST)
               .setReportType(HttpSender.Type.JSON)
               .setFormUriBasicAuthLogin(getAcraUser(app.getBaseContext()))
               .setFormUriBasicAuthPassword(getAcraPass(app.getBaseContext()))
               .setCustomReportContent(fields)
//               setResDialogOkToast(R.string.toast_crash).
      //TODO: create custom dialog here
              .setReportingInteractionMode(ReportingInteractionMode.DIALOG)
              .setReportDialogClass(ACRACrashReportDialog.class);
//               .setMailTo("kudlaty.ie@gmail.com");

       return build.build();

   }

    private static String getAcraUser(Context context){
        return context.getResources().getString(R.string.acra_user);
    }
    private static String getAcraPass(Context context){
        return context.getResources().getString(R.string.acra_password);
    }

}
