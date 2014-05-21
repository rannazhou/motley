package edu.mit.motley.app;

import android.app.Activity;
import android.os.Bundle;

/**
 * Created by ranna on 5/13/14.
 */
public class RenderActivity extends Activity {
//    private CardArrayAdapter handCardArrayAdapter;
//    private CardArrayAdapter pileCardArrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.main);
//
//        CardListView handCardListView = (CardListView) findViewById(R.id.hand_cards);
//        handCardArrayAdapter = (CardArrayAdapter) handCardListView.getAdapter();
//        CardListView pileCardListView = (CardListView) findViewById(R.id.pile_cards);
//        pileCardArrayAdapter = (CardArrayAdapter) pileCardListView.getAdapter();
//
//
//        String data = getIntent().getExtras().getString( "com.parse.Data" );
//        try {
//            JSONObject dataJson = new JSONObject(data);
//            JSONArray cardsJsonArray = dataJson.getJSONArray("cards");
//            for (int i=0; i < cardsJsonArray.length(); i++) {
//                JSONObject cardJson = cardsJsonArray.getJSONObject(i);
//                String englishPhrase = cardJson.getString("eng");
//                String frenchPhrase = cardJson.getString("fr");
//                addCardToHand(new HandPhraseCard(this, frenchPhrase, englishPhrase));
//            }
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
    }


//    public void addCardToHand(HandPhraseCard card) {
//        handCardArrayAdapter.insert(card, 0);
//        handCardArrayAdapter.notifyDataSetChanged();
//    }
//
//    public void addCardToPile(PhraseCard card) {
//        pileCardArrayAdapter.insert(card, 0);
//        pileCardArrayAdapter.notifyDataSetChanged();
//    }
}
