package co.forum.app.tools.parallaxrecyclerview;


import android.content.Context;
import android.graphics.Canvas;
import android.os.Build;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.TranslateAnimation;
import android.widget.RelativeLayout;

import java.util.List;

import co.forum.app.tools.endlessRecycler.EndlessRecyclerOnScrollListener;


public class ParallaxRecyclerAdapter<T> extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public static boolean HIDE_TAB_BAR = true; //addons pour masquer la tab bar si necessaire (utilise par on hide et onshow)
    private final float SCROLL_MULTIPLIER = 0.7f;


    public static class VIEW_TYPES {
        public static final int NORMAL = 1;
        public static final int HEADER = 2;
        public static final int FIRST_VIEW = 3;
    }

    public interface RecyclerAdapterMethods {
        void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i);

        RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i);

        int getItemCount();
    }


    public interface OnClickEvent {
        /**
         * Event triggered when you click on a item of the adapter
         *
         * @param v        view
         * @param position position on the array
         */
        void onClick(View v, int position);
    }

    public interface OnParallaxScroll {
        /**
         * Event triggered when the parallax is being scrolled.
         *
         * @param percentage
         * @param offset
         * @param parallax
         */
        void onParallaxScroll(float percentage, float offset, View parallax);
    }

    private List<T> mData;
    private CustomRelativeWrapper mHeader;
    private RecyclerAdapterMethods mRecyclerAdapterMethods;
    private OnClickEvent mOnClickEvent;
    private OnParallaxScroll mParallaxScroll;
    private RecyclerView mRecyclerView;
    private int mTotalYScrolled;
    private boolean mShouldClipView = true;

    /**
     * Translates the adapter in Y
     *
     * @param of offset in px
     */
    public void translateHeader(float of) {


        float ofCalculated = of * SCROLL_MULTIPLIER;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            mHeader.setTranslationY(ofCalculated);
        } else {
            TranslateAnimation anim = new TranslateAnimation(0, 0, ofCalculated, ofCalculated);
            anim.setFillAfter(true);
            anim.setDuration(0);
            mHeader.startAnimation(anim);
        }
        mHeader.setClipY(Math.round(ofCalculated));
        if (mParallaxScroll != null) {
            float left = Math.min(1, ((ofCalculated) / (mHeader.getHeight() * SCROLL_MULTIPLIER)));
            mParallaxScroll.onParallaxScroll(left, of, mHeader);
        }

    }

    /**
     * Set the view as header.
     *
     * @param header The inflated header
     * @param view   The RecyclerView to set scroll listeners
     */
    public static boolean controlsVisible = true;
    public void setParallaxHeader(View header, final RecyclerView view) {

        mRecyclerView = view;
        mHeader = new CustomRelativeWrapper(header.getContext(), mShouldClipView);
        mHeader.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        mHeader.addView(header, new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));


        // NEW
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(header.getContext());

        final Context context = header.getContext();

        view.addOnScrollListener(new EndlessRecyclerOnScrollListener(linearLayoutManager) {

            @Override
            public void onLoadMore(int current_page) {
                // todo
                // chargement contribution et commentaires
            }

            // ADDONS pour masquer les bottoms tabs (mis pour l'occasion en static)
            private static final int HIDE_THRESHOLD = 20;
            private int scrolledDistance = 0;

            private float alpha;

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                if (mHeader != null) {

                    mTotalYScrolled += dy;
                    translateHeader(mTotalYScrolled);

                    //alpha = (float)(1000 - mTotalYScrolled) / 1000;
                    //((ThoughtActivity) context).thoughtFragment.headerView.fadeLayer.setAlpha(alpha);

                }


                // ADDONS pour masquer les bottoms tabs todo
                /*
                if (scrolledDistance > HIDE_THRESHOLD && controlsVisible) {

                    ((ThoughtActivity)context).thoughtFragment.thoughtEditor.edit_thought_panel_structure.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED); // slide in du panel
                    //((ThoughtActivity)context).thoughtFragment.thoughtEditor.slideEditorPanel(MainActivity.SLIDE_IN);

                    controlsVisible = false;
                    scrolledDistance = 0;

                } else if (scrolledDistance < -HIDE_THRESHOLD && !controlsVisible) {

                    ((ThoughtActivity)context).thoughtFragment.thoughtEditor.edit_thought_panel_structure.setPanelState(SlidingUpPanelLayout.PanelState.HIDDEN);
                    //((ThoughtActivity)context).thoughtFragment.thoughtEditor.slideEditorPanel(MainActivity.SLIDE_OUT);

                    controlsVisible = true;
                    scrolledDistance = 0;
                }
                */

                if ((controlsVisible && dy > 0) || (!controlsVisible && dy < 0)) {
                    scrolledDistance += dy;
                }

                /*
                if (HIDE_TAB_BAR) {
                    if (scrolledDistance > HIDE_THRESHOLD && controlsVisible) {
                        onHide();
                        controlsVisible = false;
                        scrolledDistance = 0;
                    } else if (scrolledDistance < -HIDE_THRESHOLD && !controlsVisible) {
                        onShow();
                        controlsVisible = true;
                        scrolledDistance = 0;
                    }

                    if((controlsVisible && dy>0) || (!controlsVisible && dy<0)) {
                        scrolledDistance += dy;
                    }
                }

                */
            }

        });


    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, final int i) {
        if (mRecyclerAdapterMethods == null)
            throw new NullPointerException("You must call implementRecyclerAdapterMethods");

        if (i != 0 && mHeader != null) {
            mRecyclerAdapterMethods.onBindViewHolder(viewHolder, i - 1);
        } else if ((i != 0))
            mRecyclerAdapterMethods.onBindViewHolder(viewHolder, i);

        if (mOnClickEvent != null)
            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnClickEvent.onClick(v, i - (mHeader == null ? 0 : 1));
                }
            });
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        if (mRecyclerAdapterMethods == null)
            throw new NullPointerException("You must call implementRecyclerAdapterMethods");

        if (i == VIEW_TYPES.HEADER && mHeader != null)
            return new ViewHolder(mHeader);

        if (i == VIEW_TYPES.FIRST_VIEW && mHeader != null && mRecyclerView != null) {
            RecyclerView.ViewHolder holder = mRecyclerView.findViewHolderForPosition(0);

            if (holder != null) {
                translateHeader(-holder.itemView.getTop());
                mTotalYScrolled = -holder.itemView.getTop();
            }
        }

        return mRecyclerAdapterMethods.onCreateViewHolder(viewGroup, i);
    }

    /**
     * @return true if there is a header on this adapter, false otherwise
     */
    public boolean hasHeader() {
        return mHeader != null;
    }

    public void setOnClickEvent(OnClickEvent onClickEvent) {
        mOnClickEvent = onClickEvent;
    }


    public boolean isShouldClipView() {
        return mShouldClipView;
    }

    /**
     * Defines if we will clip the authorLayout or not. MUST BE CALLED BEFORE {@link #setParallaxHeader(android.view.View, android.support.v7.widget.RecyclerView)}
     *
     * @param shouldClickView
     */
    public void setShouldClipView(boolean shouldClickView) {
        mShouldClipView = shouldClickView;
    }

    public void setOnParallaxScroll(OnParallaxScroll parallaxScroll) {
        mParallaxScroll = parallaxScroll;
        mParallaxScroll.onParallaxScroll(0, 0, mHeader);
    }

    public ParallaxRecyclerAdapter(List<T> data) {
        mData = data;
    }

    public List<T> getData() {
        return mData;
    }

    public void setData(List<T> data) {
        mData = data;
        notifyDataSetChanged();
    }

    public void addItem(T item, int position) {
        mData.add(position, item);
        notifyItemInserted(position + (mHeader == null ? 0 : 1));
    }

    public void removeItem(T item) {
        int position = mData.indexOf(item);
        if (position < 0)
            return;
        mData.remove(item);
        notifyItemRemoved(position + (mHeader == null ? 0 : 1));
    }


    public int getItemCount() {
        if (mRecyclerAdapterMethods == null)
            throw new NullPointerException("You must call implementRecyclerAdapterMethods");
        return mRecyclerAdapterMethods.getItemCount() + (mHeader == null ? 0 : 1);
    }



    @Override
    public int getItemViewType(int position) {
        if (mRecyclerAdapterMethods == null)
            throw new NullPointerException("You must call implementRecyclerAdapterMethods");

        if (position == 1)
            return VIEW_TYPES.FIRST_VIEW;
        return position == 0 ? VIEW_TYPES.HEADER : VIEW_TYPES.NORMAL;
    }

    /**
     * You must call this method to set your normal adapter methods
     * @param callbacks
     */
    public void implementRecyclerAdapterMethods(RecyclerAdapterMethods callbacks) {
        mRecyclerAdapterMethods = callbacks;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(View itemView) {
            super(itemView);
        }
    }

    static class CustomRelativeWrapper extends RelativeLayout {

        private int mOffset;
        private boolean mShouldClip;

        public CustomRelativeWrapper(Context context, boolean shouldClick) {
            super(context);
            mShouldClip = shouldClick;
        }

        @Override
        protected void dispatchDraw(Canvas canvas) {
            if (mShouldClip) {
                //canvas.clipRect(new Rect(getLeft(), getTop(), getRight(), getBottom() + mOffset));
            }
            super.dispatchDraw(canvas);
        }

        public void setClipY(int offset) {
            mOffset = offset;
            invalidate();
        }
    }
}