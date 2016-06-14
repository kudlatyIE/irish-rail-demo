package ie.droidfactory.fragstations.utils;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.widget.EditText;

import org.acra.dialog.BaseCrashReportDialog;

import ie.droidfactory.fragstations.R;

/**
 * Created by kudlaty on 14/06/2016.
 */
public class ACRACrashReportDialog extends BaseCrashReportDialog implements DialogInterface.OnDismissListener,
        DialogInterface. OnClickListener {

    private static final String STATE_COMMENT = "comment";
    private String comment;
    private EditText editText;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        AlertDialog dialog  = new AlertDialog.Builder(this)
                .setView(R.layout.acra_crash_report_dialog)
                .setTitle(getString(R.string.crash_dialog_title))
                .setPositiveButton(R.string.ok, this)
                .setNegativeButton(R.string.cancel, this)
                .create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.setOnDismissListener(this);
        dialog.show();
        editText = (EditText) dialog.findViewById(R.id.acra_crash_report_dialog_custom_text);
        if(savedInstanceState!=null){
            editText.setText(savedInstanceState.getCharSequence(STATE_COMMENT));
        }
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        if(which == DialogInterface.BUTTON_POSITIVE){
            comment = editText.getText().toString();
            sendCrash(comment, "");
        }else cancelReports();
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        finish();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString(STATE_COMMENT, editText.getText().toString());
        super.onSaveInstanceState(outState);
    }
}
