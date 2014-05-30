package edu.mit.motley.app;

import android.content.Context;
import android.telephony.TelephonyManager;

import com.parse.Parse;
import com.parse.ParseInstallation;

import edu.mit.motley.R;

/**
 * Created by ranna on 5/19/14.
 */
public class Application extends android.app.Application {
    public static final boolean APPDEBUG = false;
//    private static SharedPreferences preferences;
    public String deviceId;

    @Override
    public void onCreate() {
        super.onCreate();

        deviceId = ((TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId();

        Parse.enableLocalDatastore(this);
        Parse.initialize(this, getString(R.string.secret_parse_app_id), getString(R.string.secret_parse_client_key));

        ParseInstallation.getCurrentInstallation().saveInBackground();

//        preferences = getSharedPreferences("edu.mit.motley", Context.MODE_PRIVATE);
    }
}