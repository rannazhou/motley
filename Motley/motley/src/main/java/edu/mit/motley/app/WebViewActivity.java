package edu.mit.motley.app;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.parse.ParseObject;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import edu.mit.motley.R;

public class WebViewActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.webview);
        final String deviceId = ((TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId();
        final String CALLBACK_URL = getString(R.string.foursquare_callback_url);
        final String CLIENT_ID = getString(R.string.foursquare_client_id);

        String url =
                "https://foursquare.com/oauth2/authenticate" +
                        "?client_id=" + CLIENT_ID +
                        "&response_type=token" +
                        "&redirect_uri=" + CALLBACK_URL;

        final WebView webview = (WebView)findViewById(R.id.webview);
        webview.getSettings().setJavaScriptEnabled(true);
        final WebViewActivity webViewContext = this;
        webview.setWebViewClient(new WebViewClient() {
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
            String fragment = "#access_token=";
            int start = url.indexOf(fragment);
            System.out.println("url: " + url);
            if (start > -1) {
                final String accessToken = url.substring(start + fragment.length(), url.length());
                //Toast.makeText(WebViewActivity.this, "Token: " + accessToken, Toast.LENGTH_SHORT).show();
                Thread trd = new Thread(new Runnable(){
                    @Override
                    public void run(){
                    getUserJson(accessToken, deviceId);
                    }
                });
                trd.start();
                // save that the user has authenticated successfully
//              SharedPreferences prefs = webViewContext.getSharedPreferences("com.greylock", Context.MODE_PRIVATE);
//              Editor editor = prefs.edit();
//              editor.putBoolean(webViewContext.getString(R.string.foursquare_authenticated), true);
//              editor.commit();
//              System.out.println(" IS AUTHENTICATED!!");
            }
            }
        });
        webview.loadUrl(url);

        /*
        // will wait for the redirect, and close the webview
        final WebViewActivity webViewActivity = this;
        Thread trd = new Thread(new Runnable(){
	      	@Override
	      	public void run(){
		        while (true){
		        	String webUrl = webview.getUrl();
		        	//System.out.println("webview url: " + webUrl);
		        	if (webUrl != null && webUrl.equals("http://dry-plateau-8291.herokuapp.com/redirect")){
		        		closeWebView.run();
		        	}
		        	else{
		        		try {
							Thread.sleep(300);
						} catch (InterruptedException e) {}
		        	}
		        }
	      	}
	    });
	    trd.start();
	    */
    }

    /*
    private Runnable closeWebView = new Runnable() {
        public void run() {
        	try {
				Thread.sleep(2000);
				Intent intent = new Intent(null, MainActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
				//finish();
			} catch (InterruptedException e) {}
        }
    };
    */

    // another api call to get the userId of the foursquare user
    private void getUserJson(String accessToken, String deviceId){
        String URL = "https://api.foursquare.com/v2/users/self?oauth_token=" + accessToken;
        HttpClient httpclient = new DefaultHttpClient();
        HttpGet request = new HttpGet(URL);
        request.addHeader("deviceId", deviceId);
        ResponseHandler<String> handler = new BasicResponseHandler();
        String result = "";
        try {
            result = httpclient.execute(request, handler);
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("JSON RESULT: " + result);
        // parse out the userId
        Pattern p = Pattern.compile("[0-9]+\"");
        Matcher m = p.matcher(result);
        int i=0;
        String userId = "none";
        while (m.find() && i<1){
            userId = m.group();
            userId = userId.substring(0, userId.length()-1);
            i++;
        }
        System.out.println("woops: " + userId);

        // store token and device ID
        ParseObject foursquareUser = new ParseObject("foursquareUser");
        foursquareUser.put("foursquareUserId", userId);
        foursquareUser.put("deviceId", deviceId);
        foursquareUser.put("foursquareAccessToken", accessToken);
        foursquareUser.saveInBackground();
        httpclient.getConnectionManager().shutdown();
    }


}