package co.forum.app.cardStackView;


import android.content.Context;
import android.support.v4.app.Fragment;
import android.util.AttributeSet;
import android.view.View;

import co.forum.app.Adapters.CardStackAdapter;
import co.forum.app.Data.CardData;
import co.forum.app.Data.ParseRequest;
import co.forum.app.MainActivity;
import co.forum.app.MainSections.DeckFrag;
import co.forum.app.OnBoardingActivity.OnBoardingActivity;
import co.forum.app.SharedPref;
import co.forum.app.SubActivity.SubActivity;
import co.forum.app.tools.Message;

public class CardStackView extends AbstractCardsStackView {


    public CardStackView(Context context) {
        super(context);
    }

    public CardStackView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CardStackView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void setTypeOfDeck(int typeOfDeck) {
        super.setTypeOfDeck(typeOfDeck);
    }

    //|| !((MainActivity)MainActivity.context).deckFragment.isSubscription)

    @Override
    public void onStackGettingEmpty() {

        if(getTypeOfDeck() == CardStackAdapter.GLOBAL_DECK &&

                !((MainActivity)MainActivity.context).deckFragment.isSearchResult

                ) {

            ((MainActivity) MainActivity.context).deckFragment.cardStackAdapter.reloadStack(getContext());

            //Message.message(getContext(), "stack get empty");

        }


        if(getTypeOfDeck() == CardStackAdapter.MAIN_USER_DECK || getTypeOfDeck() == CardStackAdapter.OTHER_USER_DECK)

            ((SubActivity)getContext()).deckFragment.cardStackAdapter.reloadStack(getContext());

        if(getTypeOfDeck() == CardStackAdapter.DEMO_DECK) {

            Fragment fragment = ((OnBoardingActivity)OnBoardingActivity.context).getSupportFragmentManager().findFragmentByTag("demo_deck");

            ((DeckFrag) fragment).cardStackAdapter.reloadStack(OnBoardingActivity.context);

        }

        //if(getTypeOfDeck() == SEND_DECK) Message.message(getContext(), "send deck is about to emptied");

    }

    @Override
    public void setEmptyView(View view) {
        super.setEmptyView(view);
            }

    @Override
    public void onSwipedLeft() {

        super.onSwipedLeft();
    }

    @Override
    public void onSwipedRight() {

        super.onSwipedRight();
    }


    private CardData get_swiped_card() {

        if(getChildCount() == 0) return ((CardStackAdapter)getAdapter()).getSwipedCard(0);

        else if(getChildCount() == 1) return ((CardStackAdapter)getAdapter()).getSwipedCard(1);

        else return ((CardStackAdapter)getAdapter()).getSwipedCard(2);
    }


    @Override
    public void onSwipedUp() {

        super.onSwipedUp();

        // SEND CARD
        if(getTypeOfDeck() == CardStackAdapter.SEND_DECK) MainActivity.newContent_editor.onSwipedUp_SendCard();

        // UPVOTE CARD
        else if(getTypeOfDeck() != CardStackAdapter.DEMO_DECK){

            if(get_swiped_card().getAuthor_id()
                    .equals( ((MainActivity)MainActivity.context).localData.getUserData(SharedPref.KEY_USER_ID)) ) {

                Message.message(getContext(), "you cannot upvote your own card");

            } else if(getTypeOfDeck() == CardStackAdapter.GLOBAL_DECK || getTypeOfDeck() == CardStackAdapter.OTHER_USER_DECK) {

                CardData cardToUpvote = get_swiped_card();

                String upvoterID = ((MainActivity)MainActivity.context).localData.getUserData(SharedPref.KEY_USER_ID);

                String upvoterFullName = ((MainActivity)MainActivity.context).localData.getUserData(SharedPref.KEY_FULL_NAME);

                String authorID = cardToUpvote.getAuthor_id();

                String cardID = cardToUpvote.getCard_id();

                String cardContent = cardToUpvote.getMain_content();

                ParseRequest.check_and_insert_upvote(MainActivity.context,

                        cardID, upvoterID, authorID,
                        upvoterFullName, cardContent);

            }

            if(getTypeOfDeck() == CardStackAdapter.OTHER_USER_DECK && getChildCount() == 0)

                ((SubActivity)getContext()).onBackPressed();
        }
    }

    @Override
    public void onSwipedDown() {

        super.onSwipedDown();


        if(getTypeOfDeck() != CardStackAdapter.MAIN_USER_DECK && getTypeOfDeck() != CardStackAdapter.DEMO_DECK) {

            CardData cardToUpvote = get_swiped_card();

            String downswiperID = ((MainActivity)MainActivity.context).localData.getUserData(SharedPref.KEY_USER_ID);

            String authorID = cardToUpvote.getAuthor_id();

            String cardID = cardToUpvote.getCard_id();

            ParseRequest.insert_pass(getContext(), cardID, downswiperID, authorID,
                    ((MainActivity)MainActivity.context).localData.getGPS(SharedPref.KEY_LATITUDE),
                    ((MainActivity)MainActivity.context).localData.getGPS(SharedPref.KEY_LONGITUDE));
        }

        if((getTypeOfDeck() == CardStackAdapter.MAIN_USER_DECK || getTypeOfDeck() == CardStackAdapter.OTHER_USER_DECK) && getChildCount() == 0)

            ((SubActivity) getContext()).onBackPressed();
    }

    @Override
    public void onClickCard() {

        Message.message(MainActivity.context, "loading");

        if(getTypeOfDeck() == CardStackAdapter.MAIN_USER_DECK
                || getTypeOfDeck() == CardStackAdapter.GLOBAL_DECK
                || getTypeOfDeck() == CardStackAdapter.OTHER_USER_DECK) {

            if(getChildCount() == 1) {

                //Message.message(MainActivity.context, "open card 0");

                SubActivity.openCard(
                        ((CardStackAdapter) getAdapter()).context,
                        ((CardStackAdapter) getAdapter()).getCurrentCard(true),
                        false, false);

            } else SubActivity.openCard(
                    ((CardStackAdapter) getAdapter()).context,
                    ((CardStackAdapter) getAdapter()).getCurrentCard(false),
                    false, false);
        }
    }
}