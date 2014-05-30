package edu.mit.motley.app;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by ranna on 5/19/14.
 */
public class MotleyBroadcastReceiver extends BroadcastReceiver {
    private static final int NOTIFICATION_ID = 1;
    public static int numMessages = 0;

    @Override
    public void onReceive(Context context, Intent intent) {

        System.out.println("Calling BroadcastReceiver onReceive");

        String data = intent.getExtras().getString( "com.parse.Data" );
        try {
            JSONObject dataJson = new JSONObject(data);
            System.out.println("cam.parse.Data json is "+dataJson);
//            String header = dataJson.getString("header");
//            String message = dataJson.getString("message");
            String venue = dataJson.getString("venue");
            String link = dataJson.getString("link");
            //System.out.println("venue is "+venue);
            //System.out.println("link is"+link);

            SharedPreferences sPrefs = context.getApplicationContext().getSharedPreferences("edu.mit.motley", Context.MODE_PRIVATE);
            String currHand = sPrefs.getString("hand", null);
            JSONArray newHand = new JSONArray();
            if (currHand != null) {
                newHand = new JSONArray(currHand);
            }

            JSONArray newCards = dataJson.getJSONArray("cards");
            //System.out.println("new cards = "+dataJson.getString("cards"));
            for (int i=0; i < newCards.length(); i++) {
                JSONObject cardJson = newCards.getJSONObject(i);
                cardJson.put("venue", venue);
                cardJson.put("link", link);
                //System.out.println("This new card is "+cardJson);
                newHand.put(cardJson);
            }

            SharedPreferences.Editor mEditor = sPrefs.edit();
            mEditor.putString("hand", newHand.toString());
            mEditor.commit();
            //System.out.println("broadcastreceiver has finished putting shit in sharedprefs. now tell em why this shouldn't work");

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

//    private void generateNotification(Context context, String title, JSONObject json) {
//        Intent intent = new Intent(context, MainActivity.class);
//        PendingIntent contentIntent = PendingIntent.getActivity(context, 0, intent, 0);
//
//        numMessages = 0;
//        NotificationManager mNotifManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
//
//        Notification.Builder mBuilder = new Notification.Builder(context)
//                .setSmallIcon(R.drawable.ic_launcher)
//                .setContentTitle(title)
//                .setContentText("New cards available!")
//                .setNumber(++numMessages)
//                .setStyle(new Notification.InboxStyle()
//                    .setBigContentTitle("You have " + ++numMessages + " new cards available!")
//                );
//
//        mBuilder.setContentIntent(contentIntent);
//
//        mNotifManager.notify(NOTIFICATION_ID, mBuilder.build());
//
//    }
}
