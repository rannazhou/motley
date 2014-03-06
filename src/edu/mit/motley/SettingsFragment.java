package edu.mit.motley;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.util.Log;

import com.facebook.Session;

public class SettingsFragment extends PreferenceFragment {
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        addPreferencesFromResource(R.layout.preferences);
        
        // final SharedPreferences prefs = this.getActivity().getSharedPreferences(this.getResources().getString(R.string.app_name), Context.MODE_PRIVATE);
        final Activity activity = this.getActivity();
        final Context context = activity.getApplicationContext();
        final Session session = new Session(context);
        
        
		Preference logoutFB = (Preference) findPreference("fb_logout");
		logoutFB.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
			@Override
			public boolean onPreferenceClick(Preference pref) {
//				SharedPreferences.Editor editor = prefs.edit();
//		        editor.remove("edu.mit.motley.loggedIn");
//		        editor.commit();
				
				session.closeAndClearTokenInformation();
				
		        Log.d("SettingsFragment", "I'm here in teh settings fragment and i just clicked the preference yo");
		        Intent intent = new Intent(context, AuthActivity.class);
		        startActivity(intent);
		        
		        activity.finish();
		        
				return true;
			}
		});
    }
}
