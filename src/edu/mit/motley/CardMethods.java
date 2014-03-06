package edu.mit.motley;

import org.json.JSONException;

import android.util.Log;

import com.facebook.HttpMethod;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;

public class CardMethods {

	public static void retrieveStatus() {
		//Session session = Session.getActiveSession();
		
		Session session = new Session (null);
		if (session != null) {
			System.out.println("I'm here and the session ain't null");
			new Request(session, "/me/statuses", null, HttpMethod.GET,
					new Request.Callback() {
						public void onCompleted(Response response) {
							parseStatus(response);
						}
					}).executeAsync();
		}
		System.out.println("I'm here and the session is sadly null");
		
		
		
	}

	public static void parseStatus(Response _response) {
//		String status = "";
//		if (_response.getGraphObject() != null) {
//			
//			try {
//
//				status = _response.getGraphObject().getInnerJSONObject().getString("message");
//				
//			} catch (JSONException e) {
//				Log.v("JSONException ", e.toString());
//			}
//			
//		}
		
		System.out.println("Reponse is: "+_response);
		
	}
}
