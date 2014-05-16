package edu.mit.motley.app;

import android.content.Context;

import it.gmariotti.cardslib.library.internal.Card;

/**
 * Created by ranna on 5/14/14.
 */
public class HandPhraseCard extends PhraseCard {
    public HandPhraseCard(Context context, String frenchPhrase, String englishPhrase) {
        this(context, frenchPhrase, englishPhrase, null, null);
    }

    public HandPhraseCard(Context context, String frenchPhrase, String englishPhrase, String venueName, String venueLink) {
        super(context, frenchPhrase, englishPhrase, venueName, venueLink);
        setOnSwipeListener(new OnSwipeListener() {
            @Override
            public void onSwipe(Card card) {
                //Toast.makeText(getContext(), "[ONSWIPE] ARE WE EVEN GETTING CALLED IN HERE", Toast.LENGTH_LONG);
                HandPhraseCard original = (HandPhraseCard) card;
                PhraseCard mPhraseCard = new PhraseCard(original.getContext(),
                        original.getFrenchPhrase(),
                        original.getEnglishPhrase(),
                        original.getVenueName(),
                        original.getVenueLink());
                ((MainActivity) getContext()).addCardToPile(mPhraseCard);
            }
        });
        setOnUndoSwipeListListener(new OnUndoSwipeListListener() {
            @Override
            public void onUndoSwipe(Card card) {
                //Toast.makeText(getContext(), "[ONUNDOSWIPE] ARE WE EVEN GETTING CALLED IN HERE", Toast.LENGTH_LONG);
                PhraseCard original = (PhraseCard) card;
                HandPhraseCard mHandPhraseCard = new HandPhraseCard(original.getContext(),
                        original.getFrenchPhrase(),
                        original.getEnglishPhrase(),
                        original.getVenueName(),
                        original.getVenueLink());
                ((MainActivity) getContext()).addCardToHand(mHandPhraseCard);
            }
        });
    }
}
