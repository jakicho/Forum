package co.forum.app.MainSections;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import co.forum.app.Adapters.NavigationAdapter;
import co.forum.app.Data.ParseRequest;
import co.forum.app.Data.ParseTagsSubscription;
import co.forum.app.MainActivity;
import co.forum.app.R;
import co.forum.app.SharedPref;
import co.forum.app.tools.Message;
import co.forum.app.tools.Tags;

/**
 * A simple {@link Fragment} subclass.
 */
public class NavigationFeed extends Fragment {

    public static final String PAGE = "type";

    private int page;
    private String userID;

    private List<ParseTagsSubscription> user_subscriptions;
    private List<ParseUser> authorsList;

    public NavigationAdapter navigationAdapter;

    public int pageOffset = 0;
    public int previousTotal = 0;
    private int visibleThreshold = 1;
    private boolean loading = true;
    int firstVisibleItem, visibleItemCount, totalItemCount;

    @Bind(R.id.subscription_recycler_view) RecyclerView subscription_recycler;



    public NavigationFeed() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        user_subscriptions = new ArrayList<>();

        page = getArguments().getInt(PAGE);

        userID = ((MainActivity)MainActivity.context).localData.getUserData(SharedPref.KEY_USER_ID);

    }




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.panel_navigation_recycler, container, false);

        ButterKnife.bind(this, view);

        subscription_recycler.setVisibility(View.INVISIBLE);

        if(page == 0) {

            ParseRequest.get_UserSubscriptions(getContext(), userID, 0);

        } else if (page == 1) {

            List<ParseTagsSubscription> forum_feeds = get_forum_feeds();

            display_subscriptions(getContext(), forum_feeds, 0);

        } else if(page == 2) ParseRequest.get_Curated_Accounts(getContext(), 0);

        return view;
    }


    public void add_subscription(ParseTagsSubscription subscription) {

        Message.message(getContext(), "> " + subscription.getTags().get(0) + "\n" +user_subscriptions.get(2) );

        navigationAdapter.user_subscriptions.add(1, subscription);

        navigationAdapter.notifyDataSetChanged();

        //navigationAdapter.notifyItemInserted(1);

        //notifyItemRangeChanged(1, getItemCount());

        //user_subscriptions.size();

    }


    public void display_authors(Context context, List<ParseUser> authorsList, int offset) {

        if (offset == 0) display_FirstAuthors(context, authorsList);

        else {

            this.authorsList.addAll(authorsList);

            if(navigationAdapter == null) {

            } else navigationAdapter.notifyDataSetChanged();
        }
    }

    private void display_FirstAuthors(final Context context, final List<ParseUser> authorsList) {

        subscription_recycler.setVisibility(View.VISIBLE);

        this.authorsList = authorsList;

        navigationAdapter = new NavigationAdapter(getActivity(), null, authorsList, page);

        subscription_recycler.setAdapter(navigationAdapter);

        final LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());

        subscription_recycler.setLayoutManager(mLayoutManager);

        subscription_recycler.addOnScrollListener(new RecyclerView.OnScrollListener() {

            private void loadMore() {

                visibleItemCount = subscription_recycler.getChildCount();
                totalItemCount = mLayoutManager.getItemCount();
                firstVisibleItem = mLayoutManager.findFirstVisibleItemPosition();

                if (loading) {
                    if (totalItemCount > previousTotal) {
                        loading = false;
                        previousTotal = totalItemCount;
                    }
                }
                if (!loading && (totalItemCount - visibleItemCount)
                        <= (firstVisibleItem + visibleThreshold)) {
                    // End has been reached
                    ++pageOffset;

                    //Message.message(context,"loadMore : " + pageOffset);
                    ParseRequest.get_Curated_Accounts(context, pageOffset);

                    loading = true;
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {

                super.onScrolled(recyclerView, dx, dy);

                loadMore();

            }
        });
    }

    public void display_subscriptions(Context context, List<ParseTagsSubscription> subscriptionList, int offset) {

        //if(page == 0 && offset == 0) Message.message(context, "display_subscriptions >> " + subscriptionList.size());

        if (offset == 0) display_FirstSubscriptions(context, subscriptionList);

        else {

            this.user_subscriptions.addAll(subscriptionList);

            if(navigationAdapter == null) {

            } else navigationAdapter.notifyDataSetChanged();
        }
    }


    public void reload_after_onboarding() {

        //Message.message(getContext(), "launch");

        /*
        if(page == 0 && !ParseUser.getCurrentUser().getBoolean("init_first_subscription"))

            ParseRequest.insert_default_subscription();

        else ParseRequest.get_UserSubscriptions(getContext(), ParseUser.getCurrentUser().getObjectId(), 0);

        */

        ParseRequest.get_UserSubscriptions(getContext(), ParseUser.getCurrentUser().getObjectId(), 0);

    }

    private void display_FirstSubscriptions(final Context context, final List<ParseTagsSubscription> subscriptionList) {

        subscription_recycler.setVisibility(View.VISIBLE);

        this.user_subscriptions = subscriptionList;

        navigationAdapter = new NavigationAdapter(getActivity(), user_subscriptions, null, page);

        subscription_recycler.setAdapter(navigationAdapter);

        final LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());

        subscription_recycler.setLayoutManager(mLayoutManager);

        subscription_recycler.addOnScrollListener(new RecyclerView.OnScrollListener() {

            private void loadMore() {

                visibleItemCount = subscription_recycler.getChildCount();
                totalItemCount = mLayoutManager.getItemCount();
                firstVisibleItem = mLayoutManager.findFirstVisibleItemPosition();

                if (loading) {
                    if (totalItemCount > previousTotal) {
                        loading = false;
                        previousTotal = totalItemCount;
                    }
                }
                if (!loading && (totalItemCount - visibleItemCount)
                        <= (firstVisibleItem + visibleThreshold)) {
                    // End has been reached
                    ++pageOffset;

                    //Message.message(context,"loadMore : " + pageOffset);
                    ParseRequest.get_UserSubscriptions(context, userID, pageOffset);

                    loading = true;
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {

                super.onScrolled(recyclerView, dx, dy);

                loadMore();

            }
        });

        //if(page == 0) Message.message(context, page + "display_FirstSubscriptions > " + subscriptionList.size());

        //if(page == 0 && ParseUser.getCurrentUser() != null && !ParseUser.getCurrentUser().getBoolean("init_first_subscription"))

            //ParseRequest.insert_default_subscription(); //subscriptionList.isEmpty() &&

    }


    private List<ParseTagsSubscription> get_forum_feeds() {

        ArrayList<ParseTagsSubscription> data = new ArrayList<>();

        String[] tags = getActivity().getResources().getStringArray(R.array.forum_feeds_tags);
        String[] description = getActivity().getResources().getStringArray(R.array.forum_feeds_description);


        for(int i=0; i< tags.length; i++){

            ParseTagsSubscription current = new ParseTagsSubscription();

            current.setTags(Tags.Spliter(tags[i]));
            current.set_description(description[i]);

            data.add(current);
        }
        return data;

    }

}
