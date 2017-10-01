package ie.droidfactory.fragstations.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import ie.droidfactory.fragstations.R;

/**
 * TODO: doesn't work - need to fing better way...
 * Created by kudlaty on 2017-09-30.
 */

public class CustomEndDialog extends Dialog implements View.OnClickListener  {

    private TextView tvInfo;
    private Button btnYes, btnCancel;
    private Activity activity;


    public CustomEndDialog(@NonNull Activity activity) {
        super(activity);
        this.activity=activity;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.custom_end_dialog_layout);
        btnYes = findViewById(R.id.custom_end_dialog_btn_yes);
        btnCancel = findViewById(R.id.custom_end_dialog_btn_cancel);
        btnYes.setOnClickListener(this);
        btnCancel.setOnClickListener(this);

    }


    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.custom_end_dialog_btn_yes:
                activity.finish();
                break;
            case R.id.custom_end_dialog_btn_cancel:
                dismiss();
                break;
        }
    }
}
