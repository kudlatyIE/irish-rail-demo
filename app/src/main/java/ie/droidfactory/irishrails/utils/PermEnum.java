package ie.droidfactory.irishrails.utils;

import android.Manifest;
/**
 * Created by kudlaty on 02/06/2016.
 */
public enum PermEnum{
	CONN(Manifest.permission.INTERNET,11, "Access to the internet is needed"),
	WIFI(Manifest.permission.ACCESS_WIFI_STATE,12, "Access to the WIFI is needed"),
	NET(Manifest.permission.ACCESS_NETWORK_STATE,13, "Access to the Network is needed"),
//	STORAGE(Manifest.permission.WRITE_EXTERNAL_STORAGE,14, "Access to the storage is needed"),
	GPS(Manifest.permission.ACCESS_FINE_LOCATION,15, "Access to the Location service is needed");
	
	String permissionName, request;
	int requestCode;
	
	PermEnum(String name, int code, String request){
		this.permissionName=name;
		this.requestCode=code;
		this.request=request;
	}
	public String getPermissionName() {
		return permissionName;
	}

	public int getRequestCode() {
		return requestCode;
	}
	
	public String getRequest() {
		return request;
	}
	
}