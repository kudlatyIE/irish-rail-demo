package ie.droidfactory.irishrails.utils;

import java.util.ArrayList;

/**
 * Created by kudlaty on 2017-08-17.
 */

public interface PermissionResultCallback
{
    void PermissionGranted(int request_code);
    void PartialPermissionGranted(int request_code, ArrayList<String> granted_permissions);
    void PermissionDenied(int request_code);
    void NeverAskAgain(int request_code);
}