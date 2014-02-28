package edu.mit.motley;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.facebook.widget.LoginButton;

public class AuthActivity extends Activity {
	
	private static final String[] PERMS = new String[] { "user_status" };
	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
//        String parseID = "gPYpf4OEgnqrVMlZI9rwQQ0KIiIWayLCjH7q0CP5";
//        String parseClientKey = "LniNPQr3CwSKaFNIpyaM0GZOp2rqpCe89vNs6a19";

		setContentView(R.layout.auth);
		LoginButton loginButton = (LoginButton) findViewById(R.id.login_button);
		loginButton.setReadPermissions(PERMS);
		
		
//		// start Facebook Login
//		Session.openActiveSession(this, true, new Session.StatusCallback() {
//			// callback when session changes state
//			@Override
//			public void call(Session session, SessionState state, Exception exception) {
//				if (session.isOpened()) {
//					Request.newMeRequest(session, new Request.GraphUserCallback() {
//						// callback after Graph API response with user object
//						@Override
//						public void onCompleted(GraphUser user, Response response) {
//						}
//					}).executeAsync();
//				}}
//			});
//		
//		NewPermissionsRequest statusPermission = new Session.NewPermissionsRequest(this, Arrays.asList("user_status")).setDefaultAudience(SessionDefaultAudience.FRIENDS);
//		Session.openActiveSession(this, false, null).requestNewReadPermissions(statusPermission);
    }
  
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        
        SharedPreferences prefs = this.getSharedPreferences(this.getResources().getString(R.string.app_name), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean("edu.mit.motley.loggedIn", true);
        editor.commit();
        
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
		
    }
}

