package edu.mit.motley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.util.JsonReader;
import android.util.Log;

import com.facebook.HttpMethod;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.android.Facebook;

public class CardMethods {

	public void retrieveStatus() {
		Session session = Session.getActiveSession();
		if (session != null) {

			new Request(session, "/me/statuses", null, HttpMethod.GET,
					new Request.Callback() {
						public void onCompleted(Response response) {
							parseStatus(response);
						}
					}).executeAsync();
		}

	}

	public void parseStatus(Response _response) {
		String status = "";
		if (_response.getGraphObject() != null) {
			
			try {

				status = _response.getGraphObject().getInnerJSONObject().getString("message");
				
			} catch (JSONException e) {
				Log.v("JSONException ", e.toString());
			}
			
		}
		
		System.out.println("Status is: "+status);
		
	}
}
