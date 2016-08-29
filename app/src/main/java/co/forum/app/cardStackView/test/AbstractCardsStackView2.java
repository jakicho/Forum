package co.forum.app.cardStackView.test;

import android.content.Context;
import android.support.v4.view.MotionEventCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.OvershootInterpolator;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;

import co.forum.app.Adapters.CardStackAdapter;
import co.forum.app.MainActivity;
import co.forum.app.R;
import co.forum.app.tools.Message;

public class AbstractCardsStackView2 extends FrameLayout{

    private static final int STACK_BUFFER = 5;
    private int index = 0;
    private View emptyStackView = null;
    private boolean emptyPageAdded = false;
    private BaseAdapter adapter;
    private Orientation orientation = Orientation.Horizontal;

    private int typeOfDeck;


    public AbstractCardsStackView2(Context context) {
        super(context);
    }

    public AbstractCardsStackView2(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AbstractCardsStackView2(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void setAdapter(BaseAdapter adapter) {
        this.adapter = adapter;
        index = 0;
        loadNext();
        appendBottomView();
    }

    public BaseAdapter getAdapter() {
        return adapter;
    }

    public void setOrientation(Orientation orientation) {
        this.orientation = orientation;
    }


    public void setTypeOfDeck(int typeOfDeck){
        this.typeOfDeck = typeOfDeck;
    }

    public int getTypeOfDeck() {
        return typeOfDeck;
    }

    public void setEmptyView(View view) {
        emptyStackView = view;
    }

    private void loadNext() {
        removeTopView();
        appendBottomView();
    }

    private boolean removeTopView() {
        boolean removed = false;
        int children = getChildCount();
        if ( children > 0 ) {
            View topView = getChildAt(children - 1);
            if ( topView != emptyStackView ) {
                removeView(topView);
                removed = true;
            }
            children = getChildCount();
            if ( children > 0 ) {
                topView = getChildAt(children - 1);
                if ( topView.getAlpha() < 0.98f ) {
                    topView.setAlpha(1.0f);
                }
            }
        }
        return removed;
    }

    private boolean appendBottomView() {

        boolean added = false;
        if ( index < adapter.getCount() ) {
            View nextBottomView = adapter.getView(index++, null, this);
            if (nextBottomView != null) {
                //nextBottomView.setOnTouchListener(new SwipeTouchListener(nextBottomView, this, orientation, typeOfDeck));
                addView(nextBottomView, 0);
                added = true;
                if (index + STACK_BUFFER > adapter.getCount()) {
                    onStackGettingEmpty();
                }
            }
        } else if ( emptyStackView != null && !emptyPageAdded ) {
            addView(emptyStackView, 0);
            emptyPageAdded = true;
            added = true;
        }
        return added;
    }

    // ---------------------

    public void onSwipedUp() {
        loadNext();
    }

    public void onSwipedDown() {
        loadNext();
    }

    public void onMovedFromCenter(float distance) {
        if ( getChildCount() > 1 ) {
            View bottomView = getChildAt(0);
            /*
            float alpha = Math.max(-1.0f, Math.min(1.0f, distance / 300.0f));
            bottomView.setAlpha(Math.abs(alpha));
            */

            float scale;
            float alpha;
            if(Math.abs(distance) < 300f) {
                scale = 0.95f + (Math.abs(distance)/300f * 0.05f);
                alpha = 0.8f + (Math.abs(distance)/300f * 0.2f);
            } else {
                scale = 1f;
                alpha = 1f;
            }

            View sub = getChildAt(0).findViewById(R.id.forShadow);

            if(sub != null) sub.setAlpha(alpha);

            bottomView.setScaleX(Math.abs(scale));
            bottomView.setScaleY(Math.abs(scale));
        }
    }

    public void onStackGettingEmpty() {

    }


    // -----------------------------

    private static final float CARDS_SWIPE_LENGTH = 200;
    public enum Orientation {Horizontal, Vertical};
    private float originalX = 0;
    private float originalY = 0;
    private float startMoveX = 0;
    private float startMoveY = 0;


    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {

        return true;

        //return super.onInterceptTouchEvent(ev);
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {

        Message.message(MainActivity.context, "triangle");


        boolean processed = true;
        float X = event.getRawX();
        float Y = event.getRawY();
        float deltaX = X - startMoveX;
        float deltaY = Y - startMoveY;
        float distance = orientation == Orientation.Horizontal? deltaX: deltaY;
        int action = event.getAction() & MotionEvent.ACTION_MASK;


        switch(action & MotionEventCompat.ACTION_MASK) {

            case MotionEvent.ACTION_DOWN:

                originalX = getX();
                originalY = getY();
                startMoveX = X;
                startMoveY = Y;

                break;

            case MotionEvent.ACTION_UP:
                boolean swiped = false;
                if ( Math.abs(distance) > CARDS_SWIPE_LENGTH ) { // Moved far enough to be an event
                    if ( orientation == Orientation.Vertical ) {
                        swiped = true;
                        if (deltaY > 0) {
                            if(typeOfDeck == CardStackAdapter.SEND_DECK) handleSwipeBack();
                            else handleSwipeDown();
                        } else {
                            if(typeOfDeck == CardStackAdapter.MAIN_USER_DECK) handleSwipeBack();
                            else handleSwipeUp();
                        }
                    } else { // Horizontal move
                        swiped = true;
                        if ( deltaX > 0 ) {
                            //handleSwipeRight();
                        } else {
                            //handleSwipeLeft();
                        }
                    }
                }

                if ( !swiped ) {
                    handleSwipeBack();
                }

                // CLICK
                if (Math.abs(distance) < 3 && typeOfDeck == CardStackAdapter.MAIN_USER_DECK) {

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

                    setTranslationY(deltaY);

                    handleIndicatorsFade(distance);

                }

                if ( orientation == Orientation.Horizontal ) {
                    setTranslationX(deltaX);
                }
                break;

            default:
                processed = false;
                break;
        }

        onMovedFromCenter(distance);
        if (processed) {
            invalidate();
        }
        return processed;
    }



    private void handleIndicatorsFade(float distance) {

        /*
        if(typeOfDeck == CardStackView.GLOBAL_DECK) {

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

        } else if(typeOfDeck == CardStackView.SEND_DECK){

            if (distance<50) {

                text_send.setAlpha(1-(CARDS_SWIPE_LENGTH+ distance/2.5f) / CARDS_SWIPE_LENGTH);

                if(Math.abs(distance) > CARDS_SWIPE_LENGTH) border_send.setAlpha(0.3f);

                else border_send.setAlpha(0);
            }
        }

        */
    }

    private void handleSwipeBack() {
        /*
        todo

        text_send.setAlpha(0);
        text_upvote.setAlpha(0);
        text_pass.setAlpha(0);

        border_send.setAlpha(0);
        border_upvote.setAlpha(0);
        border_pass.setAlpha(0);
        */

        if(orientation == Orientation.Vertical) {
            animate()
                    .setDuration(200)
                    .setInterpolator(new OvershootInterpolator(1.5f))
                    .y(originalY);

        } else {
            //view.setX(originalX);
            //view.setY(originalY);
        }
    }



    private void handleSwipeUp() {
        Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.slide_up);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                onSwipedUp();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        startAnimation(animation);

    }

    private void handleSwipeDown() {

        Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.slide_down);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                onSwipedDown();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        startAnimation(animation);
    }


}