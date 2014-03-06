package edu.mit.motley;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.facebook.Session;
import com.facebook.widget.LoginButton;

public class AuthActivity extends Activity {
	
	private static final String[] PERMS = new String[] { "user_status" };
	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        Log.d("AuthActivity", "Just got created again bro");
        
//        String parseID = "gPYpf4OEgnqrVMlZI9rwQQ0KIiIWayLCjH7q0CP5";
//        String parseClientKey = "LniNPQr3CwSKaFNIpyaM0GZOp2rqpCe89vNs6a19";

		setContentView(R.layout.auth);
		LoginButton loginButton = (LoginButton) findViewById(R.id.login_button);
		loginButton.setReadPermissions(PERMS);
		
    }
  
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
		
    }
}

