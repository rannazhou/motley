package edu.mit.motley;

import com.facebook.Session;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

public class MainActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedBundleInstance) {
		super.onCreate(savedBundleInstance);
		
		//Session.OpenRequest();
		if (Session.openActiveSessionFromCache(this.getApplicationContext()) == null){
			Log.d("MainActivity", "Cache must not have any tokens");
		} else {
			Log.d("MainActivity", "WE HAZ TOKENS BRAH");
		}
		
		// Set up the action bar.
		final ActionBar actionBar = getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		Tab homeTab = actionBar.newTab().setText("Home");
		Tab settingsTab = actionBar.newTab().setText("Settings");
		
		homeTab.setTabListener(new TabListener<HomeFragment>(this, "HomeFragment", HomeFragment.class));
		settingsTab.setTabListener(new TabListener<SettingsFragment>(this, "SettingsFragment", SettingsFragment.class));
		
		actionBar.addTab(homeTab);	
		actionBar.addTab(settingsTab);
	}
}
