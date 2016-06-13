package ie.droidfactory.fragstations.utils;

import android.content.Context;
import android.support.annotation.NonNull;

import org.acra.collector.CrashReportData;
import org.acra.sender.ReportSender;
import org.acra.sender.ReportSenderException;

import ie.droidfactory.fragstations.R;

/**
 * Created by kudlaty on 13/06/2016.
 *
 * followed on http://androidcocktail.blogspot.ie/2015/05/silentemail-crash-report-using-acra.html
 */
public class ACRAReporter implements ReportSender {

    private String acraUser, acraPass;

    public ACRAReporter(Context context){
        this.acraUser = context.getResources().getString(R.string.acra_user);
        this.acraPass = context.getResources().getString(R.string.acra_password);
    }
    @Override
    public void send(@NonNull Context context, @NonNull CrashReportData errorContent) throws ReportSenderException {

    }
}
