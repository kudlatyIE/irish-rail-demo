package ie.droidfactory.irishrails;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import ie.droidfactory.irishrails.utils.FragmentUtils;

/**
 * Created by kudlaty on 2017-10-14.
 */

public class InfoDetailsFragment extends MainFragment {

    private final static String TAG = InfoDetailsFragment.class.getSimpleName();

    private WebView webView;
    private String tweetUrl;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_info_details, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        webView = view.findViewById(R.id.fragment_info_details_webView);
    }

    @Override
    public void onStart() {
        super.onStart();

        Bundle extras = getArguments();
        if(extras!=null) {
            tweetUrl = extras.getString(FragmentUtils.FRAGMENT_TWEETER_URL);
            webView.loadUrl(tweetUrl);
            webView.setWebViewClient(new WebViewClient(){
                @Override
                public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                    return super.shouldOverrideUrlLoading(view, request);
                }

                @Override
                public void onPageFinished(WebView view, String url) {
                    super.onPageFinished(view, url);
                    Log.d(TAG, "loaded url: "+url);
                }
            });

        }
    }


}
