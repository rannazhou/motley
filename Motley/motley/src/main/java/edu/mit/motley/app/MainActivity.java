package edu.mit.motley.app;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TabHost;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import com.foursquare.android.nativeoauth.FoursquareCancelException;
import com.foursquare.android.nativeoauth.FoursquareDenyException;
import com.foursquare.android.nativeoauth.FoursquareInvalidRequestException;
import com.foursquare.android.nativeoauth.FoursquareOAuth;
import com.foursquare.android.nativeoauth.FoursquareOAuthException;
import com.foursquare.android.nativeoauth.FoursquareUnsupportedVersionException;
import com.foursquare.android.nativeoauth.model.AccessTokenResponse;
import com.foursquare.android.nativeoauth.model.AuthCodeResponse;
import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.PushService;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import edu.mit.motley.R;
import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.internal.CardArrayAdapter;
import it.gmariotti.cardslib.library.view.CardListView;
import it.gmariotti.cardslib.library.view.listener.UndoBarController;
import uk.co.senab.actionbarpulltorefresh.library.ActionBarPullToRefresh;
import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshLayout;
import uk.co.senab.actionbarpulltorefresh.library.listeners.OnRefreshListener;


public class MainActivity extends Activity implements OnRefreshListener {
    private static final int REQUEST_CODE_FSQ_CONNECT = 200;
    private static final int REQUEST_CODE_FSQ_TOKEN_EXCHANGE = 201;

    private static String deviceId;
    private ViewSwitcher mViewSwitcher;

    private CardArrayAdapter handCardArrayAdapter;
    private CardArrayAdapter pileCardArrayAdapter;

    public String userlessReqParams;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        try {
//            PackageInfo info = getPackageManager().getPackageInfo("edu.mit.motley", PackageManager.GET_SIGNATURES);
//            for (Signature signature : info.signatures) {
//                MessageDigest md = MessageDigest.getInstance("SHA");
//                md.update(signature.toByteArray());
//                String sign= Base64.encodeToString(md.digest(), Base64.DEFAULT);
//                Log.e("MY KEY HASH:", sign);
//                Toast.makeText(getApplicationContext(),sign, Toast.LENGTH_LONG).show();
//            }
//        } catch (PackageManager.NameNotFoundException e) {
//        } catch (NoSuchAlgorithmException e) {
//        }

        userlessReqParams = "&client_id="+getString(R.string.foursquare_client_id)+"&client_secret="+getString(R.string.foursquare_client_secret);


        deviceId = ((TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId();

        setContentView(R.layout.main);
        mViewSwitcher = (ViewSwitcher) findViewById(R.id.view_switcher);

        Parse.initialize(this, getString(R.string.secret_parse_app_id), getString(R.string.secret_parse_client_key));
        ParseInstallation.getCurrentInstallation().saveInBackground();
        Parse.setLogLevel(Parse.LOG_LEVEL_VERBOSE);

        SharedPreferences prefs = this.getSharedPreferences("edu.mit.motley", Context.MODE_PRIVATE);
        // boolean foursquare_authenticated = prefs.getBoolean(getString(R.string.foursquare_authenticated), false);

        final Activity mMainActivity = this;
        final Button foursquare_reg_button = (Button)findViewById(R.id.btnLogin);
        foursquare_reg_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                ParseQuery<ParseObject> query = ParseQuery.getQuery("FoursquareUser");
                query.whereEqualTo("deviceId", deviceId);
                query.findInBackground(new FindCallback<ParseObject>() {
                    public void done(List<ParseObject> resultsList, ParseException e) {
                    if (resultsList != null && resultsList.size()>0) { //user exists
                        System.out.println("USER EXISTS!");
                    }
                    else {
                        System.out.println("USER DOESN'T EXIST!");
                        Intent intent = FoursquareOAuth.getConnectIntent(mMainActivity.getApplicationContext(), getString(R.string.foursquare_client_id));

                        // If the device does not have the Foursquare app installed, we'd
                        // get an intent back that would open the Play Store for download.
                        // Otherwise we start the auth flow.
                        if (FoursquareOAuth.isPlayStoreIntent(intent)) {
                            toastMessage(MainActivity.this, getString(R.string.foursquare_not_installed_message));
                            startActivity(intent);
                        } else {
                            startActivityForResult(intent, REQUEST_CODE_FSQ_CONNECT);
                        }
                    }

                    mViewSwitcher.showNext();
                    }
                });
            }
        });

        PushService.subscribe(this.getApplicationContext(), "user_"+deviceId, RenderActivity.class);

        setupUI();

    }

    private void setupUI() {
        TabHost mTabHost = (TabHost) findViewById(android.R.id.tabhost);
        mTabHost.setup();
        TabHost.TabSpec handTab = mTabHost.newTabSpec("hand");
        handTab.setContent(R.id.hand);
        handTab.setIndicator("Hand");
        mTabHost.addTab(handTab);
        TabHost.TabSpec pileTab = mTabHost.newTabSpec("pile");
        pileTab.setContent(R.id.pile);
        pileTab.setIndicator("Pile");
        mTabHost.addTab(pileTab);

        PullToRefreshLayout mPullToRefreshLayout = (PullToRefreshLayout) findViewById(R.id.ptr_layout);
        ActionBarPullToRefresh.from(this)
            // Mark All Children as pullable
            .allChildrenArePullable()
            // Set an OnRefreshListener
            .listener(this)
            // Finally commit the setup to our PullToRefreshLayout
            .setup(mPullToRefreshLayout);

        ArrayList<Card> handCards = new ArrayList<Card>();
        handCardArrayAdapter = new CardArrayAdapter(this, handCards);
        CardListView handCardListView = (CardListView) findViewById(R.id.hand_cards);
        if (handCardListView !=null){
            handCardListView.setAdapter(handCardArrayAdapter);
        }

        handCardArrayAdapter.setUndoBarUIElements(new UndoBarController.UndoBarUIElements() {
            @Override
            public int getUndoBarId() {
                return R.id.undo_hand_to_pile_swipe_bar;
            }

            @Override
            public int getUndoBarMessageId() {
                return R.id.undo_hand_to_pile_swipe_bar_message;
            }

            @Override
            public int getUndoBarButtonId() {
                return R.id.undo_hand_to_pile_swipe_bar_button;
            }
        });
        handCardArrayAdapter.setEnableUndo(true);

        // TODO: needs to be moved to RenderActivity
        addCardToHand(new HandPhraseCard(this, "Welcome!", "Bienvenue!", "Foursquare", "http://m.foursquare.com/checkIn"));
        addCardToHand(new HandPhraseCard(this,"French", "le francais"));

        ArrayList<Card> pileCards = new ArrayList<Card>();
        pileCardArrayAdapter = new CardArrayAdapter(this, pileCards);
        CardListView pileCardListView = (CardListView) findViewById(R.id.pile_cards);
        if (pileCardListView !=null){
            pileCardListView.setAdapter(pileCardArrayAdapter);
        }

        pileCardArrayAdapter.setUndoBarUIElements(new UndoBarController.UndoBarUIElements() {
            @Override
            public int getUndoBarId() {
                return R.id.undo_delete_from_pile_swipe_bar;
            }

            @Override
            public int getUndoBarMessageId() {
                return R.id.undo_delete_from_pile_swipe_bar_message;
            }

            @Override
            public int getUndoBarButtonId() {
                return R.id.undo_delete_from_pile_swipe_bar_button;
            }
        });
        pileCardArrayAdapter.setEnableUndo(true);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        return id == R.id.action_settings || super.onOptionsItemSelected(item);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CODE_FSQ_CONNECT:
                onCompleteConnect(resultCode, data);
                break;

            case REQUEST_CODE_FSQ_TOKEN_EXCHANGE:
                onCompleteTokenExchange(resultCode, data);
                break;

            default:
                super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void onCompleteConnect(int resultCode, Intent data) {
        AuthCodeResponse codeResponse = FoursquareOAuth.getAuthCodeFromResult(resultCode, data);
        Exception exception = codeResponse.getException();

        if (exception == null) {
            // Success.
            String code = codeResponse.getCode();
            performTokenExchange(code);

        } else {
            if (exception instanceof FoursquareCancelException) {
                // Cancel.
                toastMessage(this, "Canceled");

            } else if (exception instanceof FoursquareDenyException) {
                // Deny.
                toastMessage(this, "Denied");

            } else if (exception instanceof FoursquareOAuthException) {
                // OAuth error.
                String errorMessage = exception.getMessage();
                String errorCode = ((FoursquareOAuthException) exception).getErrorCode();
                toastMessage(this, errorMessage + " [" + errorCode + "]");

            } else if (exception instanceof FoursquareUnsupportedVersionException) {
                // Unsupported Fourquare app version on the device.
                toastError(this, exception);

            } else if (exception instanceof FoursquareInvalidRequestException) {
                // Invalid request.
                toastError(this, exception);

            } else {
                // Error.
                toastError(this, exception);
            }
        }
    }

    private void onCompleteTokenExchange(int resultCode, Intent data) {
        System.out.println("Completing token exchange....");
        AccessTokenResponse tokenResponse = FoursquareOAuth.getTokenFromResult(resultCode, data);
        Exception exception = tokenResponse.getException();

        if (exception == null) {
            final String accessToken = tokenResponse.getAccessToken();
            // Success.
            toastMessage(this, "Access token: " + accessToken);

            new Thread(new Runnable() {
                @Override
                public void run() {
                    System.out.println("updating parse database...");
                    String urlString = reqSelfData(accessToken);
                    System.out.println("URL is" + urlString);
                    HttpClient httpclient = new DefaultHttpClient();
                    HttpGet request = new HttpGet(urlString);
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
                    int i = 0;
                    String userId = "none";
                    while (m.find() && i < 1) {
                        userId = m.group();
                        userId = userId.substring(0, userId.length() - 1);
                        i++;
                    }
                    System.out.println("woops: " + userId);

                    // store token and device ID
                    ParseObject foursquareUser = new ParseObject("FoursquareUser");
                    foursquareUser.put("foursquareUserId", userId);
                    foursquareUser.put("deviceId", deviceId);
                    foursquareUser.put("foursquareAccessToken", accessToken);
                    foursquareUser.saveInBackground();
                    httpclient.getConnectionManager().shutdown();

                    System.out.println("parse database updated.");
                }
            }).start();

            // Go to the next screen yo
//            mViewSwitcher.showNext();

        } else {
            if (exception instanceof FoursquareOAuthException) {
                // OAuth error.
                String errorMessage = exception.getMessage();
                String errorCode = ((FoursquareOAuthException) exception).getErrorCode();
                toastMessage(this, errorMessage + " [" + errorCode + "]");

            } else {
                // Other exception type.
                toastError(this, exception);
            }
        }
    }

    /**
     * Exchange a code for an OAuth Token. Note that we do not recommend you
     * do this in your app, rather do the exchange on your server. Added here
     * for demo purposes.
     *
     * @param code
     *          The auth code returned from the native auth flow.
     */
    private void performTokenExchange(String code) {
        System.out.println("Performing token exchange");

        Intent intent = FoursquareOAuth.getTokenExchangeIntent(this.getApplicationContext(), getString(R.string.foursquare_client_id), getString(R.string.foursquare_client_secret), code);
        startActivityForResult(intent, REQUEST_CODE_FSQ_TOKEN_EXCHANGE);
    }

    public static void toastMessage(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    public static void toastError(Context context, Throwable t) {
        Toast.makeText(context, t.getMessage(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRefreshStarted(View view) {
        // TODO: get random new cards

    }

    public void addCardToHand(HandPhraseCard card) {
        handCardArrayAdapter.insert(card, 0);
        handCardArrayAdapter.notifyDataSetChanged();
    }

    public void addCardToPile(PhraseCard card) {
        pileCardArrayAdapter.insert(card, 0);
        pileCardArrayAdapter.notifyDataSetChanged();
    }








    // here are all the URL things

    public static String dateFoursquareAPIVerified = "?v=20140515";

    public static String showVenue (String venueId) {
        return "http://m.foursquare.com/venue/"+venueId;
    }

//    public static String checkIn (String venueId) {
//        return "http://m.foursquare.com/checkin";
//    }

    public String reqVenueTips (String venueId, int limit, int offset) {
        return "https://api.foursquare.com/v2/venues/"+venueId+"/tips"
                +dateFoursquareAPIVerified+"&sort=recent&limit="+String.valueOf(limit)+"&offset="+String.valueOf(offset)+userlessReqParams;
    }

    public String reqSelfData (String accessToken) {
        return "https://api.foursquare.com/v2/users/self"+dateFoursquareAPIVerified+"&oauth_token="+accessToken;
    }
}