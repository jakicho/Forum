package co.forum.app.cardStackView;


import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.OvershootInterpolator;

import co.forum.app.Adapters.CardStackAdapter;
import co.forum.app.R;

public class SwipeTouchListener implements View.OnTouchListener {

    private static final float CARDS_SWIPE_LENGTH = 200;
    public enum Orientation {Horizontal, Vertical};
    private float originalX = 0;
    private float originalY = 0;
    private float startMoveX = 0;
    private float startMoveY = 0;
    private View view;
    private OnCardMovement listener;
    private Orientation orientation = Orientation.Horizontal;

    private View text_send;
    private View text_upvote;
    private View text_pass;

    private View border_send;
    private View border_upvote;
    private View border_pass;

    private int typeOfDeck;

    public SwipeTouchListener(View view, OnCardMovement listener, Orientation orientation, int typeOfDeck) {
        this.view = view;
        this.listener = listener;
        originalX = view.getX();
        originalY = view.getY();
        this.orientation = orientation;
        this.typeOfDeck = typeOfDeck;
    }

    public void setStartPoints(float x, float y) {
        startMoveX = x;
        startMoveY = y;
    }



    public boolean onTouch(View v, MotionEvent event) {

        text_send = view.findViewById(R.id.indicator_send);
        text_upvote = view.findViewById(R.id.indicator_upvote);
        text_pass = view.findViewById(R.id.indicator_pass);

        border_send = view.findViewById(R.id.border_send);
        border_upvote = view.findViewById(R.id.border_upvote);
        border_pass = view.findViewById(R.id.border_pass);


        boolean processed = true;
        float X = event.getRawX();
        float Y = event.getRawY();
        float deltaX = X - startMoveX;
        float deltaY = Y - startMoveY;
        float distance = orientation == Orientation.Horizontal? deltaX: deltaY;
        int action = event.getAction() & MotionEvent.ACTION_MASK;

        switch (action) {

            case MotionEvent.ACTION_DOWN:

                //Message.message(MainActivity.context, "action down : ");

                originalX = view.getX();
                originalY = view.getY();
                startMoveX = X;
                startMoveY = Y;

                break;

            case MotionEvent.ACTION_UP:
                boolean swiped = false;
                if ( Math.abs(distance) > CARDS_SWIPE_LENGTH ) { // Moved far enough to be an event

                    if ( orientation == Orientation.Vertical ) {

                        swiped = true;
                        if (deltaY > 0) {
                            if(typeOfDeck == CardStackAdapter.SEND_DECK) handleSwipeBack(v);
                            else handleSwipeDown();
                        } else {
                            if(typeOfDeck == CardStackAdapter.MAIN_USER_DECK) handleSwipeBack(v);
                            else handleSwipeUp();
                        }
                    } else { // Horizontal move
                        swiped = true;
                        if ( deltaX > 0 ) {
                            handleSwipeRight();
                        } else {
                            handleSwipeLeft();
                        }
                    }
                }

                if ( !swiped ) {
                    handleSwipeBack(v);
                }

                    // CLICK
                if (Math.abs(distance) < 3 &&
                        (typeOfDeck == CardStackAdapter.MAIN_USER_DECK
                                || typeOfDeck == CardStackAdapter.GLOBAL_DECK
                                || typeOfDeck == CardStackAdapter.OTHER_USER_DECK) ) {

                    listener.onClickCard();

                    //CardStackAdapter.openCard();

                }
                /*
                if (Math.abs(distance) < 3 && typeOfDeck != CardStackView.SEND_DECK) {

                    CardStackAdapter.openCard();
                    //Message.message(MainActivity.context, "open card " + );
                }
                */


                break;

            case MotionEvent.ACTION_MOVE:
                if ( orientation == Orientation.Vertical ) {

                    view.setTranslationY(deltaY);

                    handleIndicatorsFade(distance);



                }
                if ( orientation == Orientation.Horizontal ) {
                    view.setTranslationX(deltaX);
                }
                break;

            default:
                processed = false;
                break;
        }
        listener.onMovedFromCenter(distance);
        if (processed) {
            view.invalidate();
        }
        return processed;
    }

    private void handleIndicatorsFade(float distance) {

        if(typeOfDeck == CardStackAdapter.GLOBAL_DECK ||
                typeOfDeck == CardStackAdapter.OTHER_USER_DECK ||
                typeOfDeck == CardStackAdapter.DEMO_DECK) {

            if (distance>50) {

                text_upvote.setAlpha(0);
                border_upvote.setAlpha(0);

                text_pass.setAlpha(1 - (CARDS_SWIPE_LENGTH - distance/2.5f) / CARDS_SWIPE_LENGTH);

                if(Math.abs(distance) > CARDS_SWIPE_LENGTH) border_pass.setAlpha(0.3f);

                else border_pass.setAlpha(0);

            }


            if (distance<50) {

                text_pass.setAlpha(0);
                border_pass.setAlpha(0);

                text_upvote.setAlpha(1-(CARDS_SWIPE_LENGTH+ distance/2.5f) / CARDS_SWIPE_LENGTH);

                if(Math.abs(distance) > CARDS_SWIPE_LENGTH) border_upvote.setAlpha(0.3f);

                else border_upvote.setAlpha(0);
            }

        } else if(typeOfDeck == CardStackAdapter.SEND_DECK){

            if (distance<50) {

                text_send.setAlpha(1-(CARDS_SWIPE_LENGTH+ distance/2.5f) / CARDS_SWIPE_LENGTH);

                if(Math.abs(distance) > CARDS_SWIPE_LENGTH) border_send.setAlpha(0.3f);

                else border_send.setAlpha(0);
            }
        }
    }

    private void handleSwipeBack(View v) {

        text_send.setAlpha(0);
        text_upvote.setAlpha(0);
        text_pass.setAlpha(0);

        border_send.setAlpha(0);
        border_upvote.setAlpha(0);
        border_pass.setAlpha(0);

        if(orientation == Orientation.Vertical) {
            v.animate()
                    .setDuration(200)
                    .setInterpolator(new OvershootInterpolator(1.5f))
                    .y(originalY);

        } else {
            //view.setX(originalX);
            //view.setY(originalY);
        }
    }

    private void handleSwipeLeft() {
        Animation animation = AnimationUtils.loadAnimation(view.getContext(), R.anim.slide_left);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                listener.onSwipedLeft();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        view.startAnimation(animation);
    }

    private void handleSwipeRight() {
        Animation animation = AnimationUtils.loadAnimation(view.getContext(), R.anim.slide_right);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                listener.onSwipedRight();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        view.startAnimation(animation);
    }

    private void handleSwipeUp() {
        Animation animation = AnimationUtils.loadAnimation(view.getContext(), R.anim.slide_up);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                listener.onSwipedUp();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        view.startAnimation(animation);

    }

    private void handleSwipeDown() {

        Animation animation = AnimationUtils.loadAnimation(view.getContext(), R.anim.slide_down);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                listener.onSwipedDown();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        view.startAnimation(animation);
    }

    public interface OnCardMovement {
        public void onClickCard();
        public void onSwipedLeft();
        public void onSwipedRight();
        public void onSwipedUp();
        public void onSwipedDown();
        public void onMovedFromCenter(float distance);
    }

}