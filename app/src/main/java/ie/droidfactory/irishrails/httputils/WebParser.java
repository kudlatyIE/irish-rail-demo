package ie.droidfactory.irishrails.httputils;

import android.util.Log;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.FormElement;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import ie.droidfactory.irishrails.model.StationDetailsInfo;

/**
 * Whow o_O It can't work now - to many html elements, need more time
 * Created by kudlaty on 2017-10-24.
 */

public class WebParser {

    private final static String TAG = WebParser.class.getSimpleName();
    private static String userAgent;// = "Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/53.0.2785.116 Safari/537.36";
    private final static int DETAILS_NUMBER = 3;

    /**
     * simple parser for test
     * @param url station details url
     * @return full details
     * @throws IOException
     */
        public static String parseGeneralStationDetails(String url) throws IOException {
            userAgent = System.getProperty("http.agent");
            Log.d(TAG, "user agent: "+userAgent);
            String result="";
            Connection.Response res = Jsoup
                    .connect(url)
                    .followRedirects(true).userAgent(userAgent).referrer("http://irishrail.ie")
                    .method(Connection.Method.GET).header("User-Agent", "http://irishrail.ie")
                    .execute();

            Document doc = Jsoup
                    .connect(url)
                    .userAgent(userAgent).timeout(10000).header("User-Agent", "http://irishrail.ie")
                    .cookies(res.cookies())
                    .referrer(url)
                    .get();
            Elements eDetails = doc.getElementsByClass("station-detail");
            result = eDetails.select("a[href]").get(0).text().toUpperCase()+":\n";
            Elements deep = eDetails.select("table");
//            Log.d(TAG, "all table list size: "+deep.size());
            for (Element row: deep.get(0).select("tr")){
//                Log.d(TAG, "TH: "+row.select("th").text());
//                Log.d(TAG, "TD: "+row.select("td").text());
                result = result+row.select("th").text()+": "+row.select("td").text()+"\n";
            }
            return result;
        }

    /**
     * Full details parser for station
     * @param stationUrl station details url
     * @return
     */
    public static HashMap<String, ArrayList<StationDetailsInfo>> parseStationDetailsInfo(String stationUrl){
            HashMap<String, ArrayList<StationDetailsInfo>> map = new HashMap<>();
            ArrayList<StationDetailsInfo> list;
            Elements details;
            String section;
            userAgent = System.getProperty("http.agent");
            Log.d(TAG, "user agent: "+userAgent);
            String result="", extras = "http://irishrail.ie";
            try {
                Connection.Response res = Jsoup
                        .connect(stationUrl)
                        .followRedirects(true).userAgent(userAgent).referrer(extras)
                        .method(Connection.Method.GET).header("User-Agent", extras)
                        .execute();

                Document doc = Jsoup
                        .connect(stationUrl)
                        .userAgent(userAgent).timeout(10000).header("User-Agent", extras)
                        .cookies(res.cookies())
                        .referrer(stationUrl)
                        .get();
                details = doc.getElementsByClass("station-detail");

                Elements deep = details.select("table");
                for(int i=0; i<DETAILS_NUMBER; i++){
                    list = new ArrayList<>();
                    section = details.select("a[href=\"#\"]").get(i).text().toUpperCase();
//                    Log.d(TAG, "all table list size: "+deep.size());
                    for (Element row: deep.get(i).select("tr")){
//                        Log.d(TAG, "TH: "+row.select("th").text());
//                        Log.d(TAG, "TD: "+row.select("td").text());
                        list.add(new StationDetailsInfo(result+row.select("th").text(), row.select("td").text()));

                    }
                    Log.d(TAG, "section: "+section);
                    map.put(section, list);
                }

            } catch (IOException e) {
                e.printStackTrace();
                map = null;
            }

            return map;
        }

}
