package ie.droidfactory.irishrails.utils;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import ie.droidfactory.irishrails.R;

/**
 * TODO: doesn't work - need to fing better way...
 * Created by kudlaty on 2017-09-30.
 */

public class CustomEndDialog extends Dialog implements View.OnClickListener  {

    private Button btnYes, btnCancel;
    private Activity activity;


    public CustomEndDialog(@NonNull Activity activity){
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
