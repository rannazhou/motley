package edu.mit.motley;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;

import com.facebook.HttpMethod;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class HomeFragment extends Fragment {
	private static final String TAG = "HomeFragment";
    private final Activity activity = this.getActivity();
    
	@Override
	public void onCreate(Bundle savedBundleInstance) {
		super.onCreate(savedBundleInstance);
		retrieveStatus();
		//CardMethods.retrieveStatus();
	}
	
//	@Override
//	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//		super.onCreateView(inflater, container, savedInstanceState);
//		
//		(new CardMethods()).retrieveStatus();
//		
//		//inflater.inflate(R.layout.home, null);
//		//TextView text = (TextView) this.getActivity().findViewById(R.id.translation_text);
//		
//		return container;
//	}

	public void retrieveStatus() {
		Session session = Session.openActiveSessionFromCache(this.getActivity().getApplicationContext());
		System.out.println("I'm here and the session ain't null");
		new Request(session, "/me/statuses", null, HttpMethod.GET,
				new Request.Callback() {
					public void onCompleted(Response response) {
						parseStatus(response.toString());
					}
				}).executeAsync();

	}
	
	public void parseStatus(String jsonResponse) {
		Gson gson = new Gson();
		//APIResponse response = gson.fromJson(jsonResponse, APIResponse.class);
		//System.out.println(response.data.get(0).message);
		System.out.println("here is my status");
	}
}
