package ie.droidfactory.irishrails.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by kudlaty on 2017-08-17.
 */

public class PermissionUtils {

    private final static String TAG = PermissionUtils.class.getSimpleName();
    private Activity current_activity;
    private PermissionResultCallback permissionResultCallback;


    private ArrayList<String> permission_list=new ArrayList<>();
    private ArrayList<String> listPermissionsNeeded=new ArrayList<>();
    private String dialog_content="";
    private int req_code;

    public PermissionUtils(Context context) {
        this.current_activity= (Activity) context;
        permissionResultCallback= (PermissionResultCallback) context;
    }


    /**
     * Check the API Level & Permission
     *
     * @param permissions permissions list
     * @param dialog_content dialog text
     * @param request_code int
     */

    public void check_permission(ArrayList<String> permissions, String dialog_content, int request_code)
    {
        this.permission_list=permissions;
        this.dialog_content=dialog_content;
        this.req_code=request_code;

        if(Build.VERSION.SDK_INT >= 23) {
            if (checkAndRequestPermissions(permissions, request_code)) {
                permissionResultCallback.PermissionGranted(request_code);
                Log.i(TAG, "all permissions granted");
                Log.i(TAG, "proceed to callback");
            }
        }
        else
        {
            permissionResultCallback.PermissionGranted(request_code);

            Log.i(TAG, "all permissions granted");
            Log.i(TAG, "proceed to callback");
        }

    }


    /**
     * Check and request the Permissions
     *
     * @param permissions list
     * @param request_code int
     * @return boolean
     */

    private  boolean checkAndRequestPermissions(ArrayList<String> permissions,int request_code) {

        if(permissions.size()>0)
        {
            listPermissionsNeeded = new ArrayList<>();
            for(int i=0;i<permissions.size();i++) {
                int hasPermission = ContextCompat.checkSelfPermission(current_activity,permissions.get(i));

                if (hasPermission != PackageManager.PERMISSION_GRANTED) {
                    listPermissionsNeeded.add(permissions.get(i));
                }
            }

            if (!listPermissionsNeeded.isEmpty()) {
                ActivityCompat.requestPermissions(current_activity, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]),request_code);
                return false;
            }
        }

        return true;
    }

    /**
     *
     *
     * @param requestCode int
     * @param permissions String array
     * @param grantResults int array
     */
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults)
    {
        switch (requestCode)
        {
            case 123:
                if(grantResults.length>0) {
                    Map<String, Integer> perms = new HashMap<>();
                    for (int i = 0; i < permissions.length; i++) {
                        perms.put(permissions[i], grantResults[i]);
                    }
                    final ArrayList<String> pending_permissions=new ArrayList<>();

                    for (int i = 0; i < listPermissionsNeeded.size(); i++) {
                        if (perms.get(listPermissionsNeeded.get(i)) != PackageManager.PERMISSION_GRANTED) {
                            if(ActivityCompat.shouldShowRequestPermissionRationale(current_activity,listPermissionsNeeded.get(i)))
                                pending_permissions.add(listPermissionsNeeded.get(i));
                            else {
                                Log.i(TAG, "Go to settings and enable permissions");
                                permissionResultCallback.NeverAskAgain(req_code);
                                Toast.makeText(current_activity, "Go to settings and enable permissions", Toast.LENGTH_LONG).show();
                                return;
                            }
                        }
                    }

                    if(pending_permissions.size()>0)
                    {
                        showMessageOKCancel(dialog_content,
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                        switch (which) {
                                            case DialogInterface.BUTTON_POSITIVE:
                                                check_permission(permission_list,dialog_content,req_code);
                                                break;
                                            case DialogInterface.BUTTON_NEGATIVE:
                                                Log.i(TAG, "permisson not fully given");
                                                if(permission_list.size()==pending_permissions.size())
                                                    permissionResultCallback.PermissionDenied(req_code);
                                                else
                                                    permissionResultCallback.PartialPermissionGranted(req_code,pending_permissions);
                                                break;
                                        }
                                    }
                                });
                    }
                    else
                    {
                        Log.i(TAG, "all permissions granted");
                        Log.i(TAG, "proceed to next step");
                        permissionResultCallback.PermissionGranted(req_code);

                    }
                    
                }
                break;
        }
    }


    /**
     * Explain why the app needs permissions
     *
     * @param message
     * @param okListener
     */
    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(current_activity)
                .setMessage(message)
                .setPositiveButton("Ok", okListener)
                .setNegativeButton("Cancel", okListener)
                .create()
                .show();
    }

}
