package ie.droidfactory.irishrails.httputils;

import android.util.Log;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.FormElement;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.List;

/**
 * Whow o_O It can't work now - to many html elements, need more time
 * Created by kudlaty on 2017-10-24.
 */

public class WebParser {

    private final static String TAG = WebParser.class.getSimpleName();
    private static String userAgent;// = "Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/53.0.2785.116 Safari/537.36";

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
            Log.d(TAG, "all table list size: "+deep.size());
            for (Element row: deep.get(0).select("tr")){
                Log.d(TAG, "TH: "+row.select("th").text());
                Log.d(TAG, "TD: "+row.select("td").text());
                result = result+row.select("th").text()+": "+row.select("td").text()+"\n";
            }

            return result;
        }

}
