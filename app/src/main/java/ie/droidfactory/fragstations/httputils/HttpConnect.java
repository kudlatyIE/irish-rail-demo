package ie.droidfactory.fragstations.httputils;

import android.net.Uri;
import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
/**
 * Created by kudlaty on 02/06/2016.
 */
public class HttpConnect {
	
//	private HttpClient client;
//	private HttpGet get;
	private final static String TAG = HttpConnect.class.getSimpleName();
	private static String code = "UTF-8", linkMain = "http://api.irishrail.ie/realtime/realtime.asmx/";
	private static URL url;
	private static HttpURLConnection conn;
	private static InputStreamReader input;
	private static StringBuffer buffer;
	private static BufferedReader reader;
	private static int responseCode;
	private static String result, errMsg="http: shit happen";
	
	public static String getRailStuff(String request){
		String line;
		buffer = new StringBuffer();
		String link="";
		
		try{
			String ALLOWED_URI_CHARS = "@#&=*+-_.,:!?()/~'%";
			String query = Uri.encode(request,ALLOWED_URI_CHARS);
			link = linkMain+ query;
//			Log.v("LINKS", "enum link == String link: "+link.equals(link2));
			Log.v(TAG,"Http link: "+link);
			url = new URL(link);
			conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			responseCode = conn.getResponseCode();
			Log.v(TAG, "HTTP Response Code: "+responseCode);
			if (responseCode!=200){
				input = new InputStreamReader(conn.getErrorStream());
//				result = errorXml(responseCode,errMsg) ;
				while((line=reader.readLine())!=null){
					buffer.append(line+"\n");
				}
				reader.close();
				result = buffer.toString();
				Log.e("HTTP", "HTTP ERROR: "+result);
			}else{
				input = new InputStreamReader(conn.getInputStream());
				
			}
			reader = new BufferedReader(input);
			while((line=reader.readLine())!=null){
				buffer.append(line+"\n");
			}
			reader.close();
			result = buffer.toString();
			
		}catch(Exception ex){
			result = errorXml(responseCode,ex.getMessage());
			ex.printStackTrace();
		}
		return result;
	}
	
	
	private static String errorXml(int code, String msg){
		XmlKeyHolder h = new XmlKeyHolder();
		return "<"+h.OBJECT_ERROR+">\n<"+h.ERR_CODE+">"+code+"</"+h.ERR_CODE+">\n"+
				"<"+h.ERR_MESSAGE+">"+msg+"</"+h.ERR_MESSAGE+">\n"+
				"</"+h.OBJECT_ERROR+">";
	}

}
