package edu.mit.motley;

import com.facebook.widget.UserSettingsFragment;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.Activity;
import android.os.Bundle;

public class MainActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedBundleInstance) {
		super.onCreate(savedBundleInstance);
		
		// Set up the action bar.
		final ActionBar actionBar = getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		Tab homeTab = actionBar.newTab().setText("Home");
		Tab settingsTab = actionBar.newTab().setText("Settings");
		
		homeTab.setTabListener(new TabListener<HomeFragment>(this, "HomeFragment", HomeFragment.class));
		settingsTab.setTabListener(new TabListener<UserSettingsFragment>(this, "UserSettingsFragment", UserSettingsFragment.class));
		
		actionBar.addTab(homeTab);	
		//actionBar.addTab(settingsTab);
	}
}
