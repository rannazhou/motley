package edu.mit.motley;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

public class CheckActivity extends Activity {
	@Override
	protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
		Intent intent;
		SharedPreferences prefs = this.getSharedPreferences(this.getResources().getString(R.string.app_name), Context.MODE_PRIVATE);
		if (prefs.getBoolean("edu.mit.motley.loggedIn", false)) {
		    intent = new Intent(this, MainActivity.class);
		} else {
		    intent = new Intent(this, AuthActivity.class);
		}
		startActivity(intent);
		finish();
	}
}
