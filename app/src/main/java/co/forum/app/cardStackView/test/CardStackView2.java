package co.forum.app.cardStackView.test;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import co.forum.app.MainActivity;
import co.forum.app.SubActivity.SubActivity;

public class CardStackView2 extends AbstractCardsStackView2 {

    public static final int GLOBAL_DECK = 0;
    public static final int SEND_DECK = 1;
    public static final int MAIN_USER_DECK = 2;
    public static final int OTHER_USER_DECK = 3;

    public CardStackView2(Context context) {
        super(context);
    }

    public CardStackView2(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CardStackView2(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void setTypeOfDeck(int typeOfDeck) {
        super.setTypeOfDeck(typeOfDeck);
    }

    @Override
    public void onStackGettingEmpty() {

        //DeckFrag.cardStackAdapter.reloadStack(getContext());

        //if(getTypeOfDeck() == GLOBAL_DECK) DeckFrag.cardStackAdapter.reloadStack(getContext());

        //if(getTypeOfDeck() == MAIN_USER_DECK) DeckFrag.cardStackAdapter.reloadStack(getContext());

        //if(getTypeOfDeck() == SEND_DECK) Message.message(getContext(), "send deck is about to emptied");

    }

    @Override
    public void setEmptyView(View view) {
        super.setEmptyView(view);
    }


    @Override
    public void onSwipedUp() {

        if(getTypeOfDeck() == SEND_DECK) {

            // SEND CARD
            MainActivity.newContent_editor.onSwipedUp_SendCard();

        } else {

            // UPVOTE CARD
            //Message.message(getContext(), "upvoted");

        }

        super.onSwipedUp();
    }

    @Override
    public void onSwipedDown() {

        super.onSwipedDown();

        if(getTypeOfDeck() == MAIN_USER_DECK && getChildCount() == 0)

            ((SubActivity)getContext()).onBackPressed();

    }


}