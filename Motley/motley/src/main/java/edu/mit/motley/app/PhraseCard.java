package edu.mit.motley.app;

import android.content.Context;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.concurrent.atomic.AtomicInteger;

import edu.mit.motley.R;
import it.gmariotti.cardslib.library.internal.Card;

/**
 * Created by ranna on 5/13/14.
 */
public class PhraseCard extends Card {
    private static AtomicInteger currCardId = new AtomicInteger();

    private String frenchPhrase;
    private String englishPhrase;
    private String venueName;
    private String venueLink;

    public PhraseCard(Context context, String frenchPhrase, String englishPhrase) {
        this(context, frenchPhrase, englishPhrase, null, null);
    }

    public PhraseCard(Context context, String frenchPhrase, String englishPhrase, String venueName, String venueLink) {
        super(context, R.layout.phrase_card);
        this.setId(String.valueOf(currCardId.getAndIncrement()));
        this.frenchPhrase = frenchPhrase;
        this.englishPhrase = englishPhrase;
        this.venueName = venueName;
        this.venueLink = venueLink;
        this.setSwipeable(true);
    }

    @Override
    public void setupInnerViewElements(ViewGroup parent, View view) {

        //Retrieve elements
        TextView mFrenchPhrase = (TextView) parent.findViewById(R.id.french_phrase);
        TextView mEnglishPhrase = (TextView) parent.findViewById(R.id.english_phrase);
        TextView mReasonText = (TextView) parent.findViewById(R.id.reason_text);
        TextView mReasonLink = (TextView) parent.findViewById(R.id.reason_link);

        mFrenchPhrase.setText(frenchPhrase);
        mEnglishPhrase.setText(englishPhrase);
        if (!(venueName == null || venueName.isEmpty())) {
            mReasonText.setText(R.string.foursquare_reason_text);
            mReasonLink.setText(Html.fromHtml("<a href=\"" + venueLink + "\">" + venueName + "</a>"));
            mReasonLink.setMovementMethod(LinkMovementMethod.getInstance());
        }
    }

    public String getFrenchPhrase() { return this.frenchPhrase; }
    public String getEnglishPhrase() { return this.englishPhrase; }
    public String getVenueName() { return this.venueName; }
    public String getVenueLink() { return this.venueLink; }
}
