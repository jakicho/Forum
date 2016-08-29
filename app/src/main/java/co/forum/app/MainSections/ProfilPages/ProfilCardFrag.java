package co.forum.app.MainSections.ProfilPages;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import co.forum.app.Adapters.MiniCardAdapter;
import co.forum.app.Data.CardData;
import co.forum.app.Data.ParseRequest;
import co.forum.app.MainActivity;
import co.forum.app.MainSections.ProfilFrag;
import co.forum.app.R;
import co.forum.app.SharedPref;

public class ProfilCardFrag extends Fragment implements SwipeRefreshLayout.OnRefreshListener{

    @Bind(R.id.generic_recycler_view) RecyclerView miniCardRecycler;
    @Bind(R.id.refreshNotificationRecycler) SwipeRefreshLayout refreshNotificationRecycler;

    @Bind(R.id.empty_post_indicator) LinearLayout empty_post_indicator;
    @Bind(R.id.empty_comment_indicator) LinearLayout empty_comment_indicator;

    @Bind(R.id.empty_user_post_indicator) LinearLayout empty_user_post_indicator;
    @Bind(R.id.empty_user_comment_indicator) LinearLayout empty_user_comment_indicator;

    public MiniCardAdapter miniCardAdapter;

    private List<CardData> profilCardsList;

    private boolean isBookmarks = false;
    private boolean isComments = false;

    //private boolean is_other_user = false;
    private String UserID;
    private String MainUserID;
    int caller;

    public ProfilCardFrag() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        isBookmarks = getArguments().getBoolean("isBookmarks");

        isComments = getArguments().getBoolean(CardData.CARD_TYPE);

        UserID = getArguments().getString(ProfilFrag.KEY_USER_ID, "NULL");

        MainUserID = ((MainActivity) MainActivity.context).localData.getUserData(SharedPref.KEY_USER_ID);

        if(UserID.equals(MainUserID)) {

            if(!isComments) caller = ParseRequest.MAIN_USER_CARD_GRID;

            else caller = ParseRequest.MAIN_USER_COMMENT_GRID;

        } else {

            if(!isComments) caller = ParseRequest.OTHER_USER_CARD_GRID;

            else  caller = ParseRequest.OTHER_USER_COMMENT_GRID;

        }

        profilCardsList = new ArrayList<>();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View layout = inflater.inflate(R.layout.generic_recycler_view, container, false);

        ButterKnife.bind(this, layout);

        refreshNotificationRecycler.setOnRefreshListener(this);

        if(!isBookmarks) ParseRequest.get_UserCards(getContext(), UserID, 0, caller);

        return layout;
    }


    public List<CardData> getProfilCardsList() {
        return profilCardsList;
    }

    public void setProfilCardsList(List<CardData> profilCardsList) {
        this.profilCardsList = profilCardsList;
    }

    public List<CardData> getDefaultCardsList(){

        List<CardData> data = new ArrayList<>();

        String[] default_card_author = getActivity().getResources().getStringArray(R.array.profil_card_author);
        int[] default_upvotes = getActivity().getResources().getIntArray(R.array.profil_card_nb_upvotes);
        int[] default_replies = getActivity().getResources().getIntArray(R.array.profil_card_nb_replies);
        String[] default_time = getActivity().getResources().getStringArray(R.array.profil_card_timestamp);
        String[] default_main_content = getActivity().getResources().getStringArray(R.array.profil_card_content);

        for(int i=0; i<default_time.length; i++){

            CardData current = new CardData();
            current.setAuthor_full_name(default_card_author[i]);
            current.setMain_content(default_main_content[i]);
            current.setNb_replies(default_replies[i]);
            current.setNb_upvotes(default_upvotes[i]);
            current.setTimestamp(default_time[i]);
            data.add(current);
        }
        return data;
    }


    public int pageOffset = 0;
    public int previousTotal = 0;
    private int visibleThreshold = 1;
    private boolean loading = true;
    int firstVisibleItem, visibleItemCount, totalItemCount;

    public void displayCards(Context context, ArrayList<CardData> cardsList, int offset){

        //if(isComments) Message.message(context, "Comments tab, offset : " + offset);

        //else Message.message(context, "Cards tab, offset : " + offset);

        if (offset == 0) displayFirstCards(context, cardsList);

        else {

            //if(!isComments) Message.message(context,"add card");

            profilCardsList.addAll(cardsList);

            if(miniCardAdapter == null) {

                // bug...

                //if(isComments) Message.message(context, "Comments tab (no adapter), offset : " + offset);

                //else Message.message(context, "Cards tab (no adapter), offset : " + offset);

            } else miniCardAdapter.notifyDataSetChanged();

        }

    }

    private void hideOrShowEmptyIndicator() {

        if(profilCardsList.size() == 0) {

            if(UserID.equals(MainUserID)) {

                if(isComments) empty_comment_indicator.setVisibility(View.VISIBLE);

                else empty_post_indicator.setVisibility(View.VISIBLE);

            } else {

                if(isComments) empty_user_comment_indicator.setVisibility(View.VISIBLE);

                else empty_user_post_indicator.setVisibility(View.VISIBLE);

            }

        } else {

            empty_post_indicator.setVisibility(View.GONE);

            empty_comment_indicator.setVisibility(View.GONE);

            empty_user_post_indicator.setVisibility(View.GONE);

            empty_user_comment_indicator.setVisibility(View.GONE);

        }
    }

    private void displayFirstCards(final Context context, final ArrayList<CardData> profilCardsList) {

        //if(!isComments) Message.message(context,"displayFirstCards called");

        this.profilCardsList = profilCardsList;

        hideOrShowEmptyIndicator();

        //miniCardAdapter = new MiniCardAdapter(getActivity(), profilCardsList, isBookmarks);
        miniCardAdapter = new MiniCardAdapter(getActivity(), profilCardsList, isComments);

        miniCardRecycler.setAdapter(miniCardAdapter);

        final LinearLayoutManager mLayoutManager = new GridLayoutManager(getActivity(), 2);

        miniCardRecycler.setLayoutManager(mLayoutManager);

        miniCardRecycler.addOnScrollListener(new RecyclerView.OnScrollListener() {

            private void loadMore(int dy){

                if(UserID.equals(MainUserID)) ((MainActivity)getContext()).onScrollHideShowBottomMenu(dy);

                visibleItemCount = miniCardRecycler.getChildCount();
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
                    ParseRequest.get_UserCards(context, UserID, pageOffset, caller);

                    loading = true;
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {

                super.onScrolled(recyclerView, dx, dy);

                loadMore(dy);


            }
        });
    }

    private void hideIndicator(){

        empty_post_indicator.setVisibility(View.GONE);
        empty_comment_indicator.setVisibility(View.GONE);

        empty_user_post_indicator.setVisibility(View.GONE);
        empty_user_comment_indicator.setVisibility(View.GONE);
    }

    public void addNewCardinGrid(CardData cardData) {

        // new card or reply

        profilCardsList.add(0, cardData);

        hideIndicator();

        miniCardAdapter.notifyDataSetChanged();

        ((MainActivity)MainActivity.context).localData.incrementUserCount(SharedPref.KEY_CARDS_COUNT);

        ((MainActivity)MainActivity.context).profilFragment.update_MainProfilHeader(SharedPref.KEY_CARDS_COUNT);

    }


    public void reload(Context context, int caller, String newUserID) {

        pageOffset = 0;
        previousTotal = 0;

        ParseRequest.get_UserCards(context, newUserID, 0, caller);

    }

    public void empty(Context context) {

        displayCards(context, new ArrayList<CardData>(), 0);

        hideIndicator();

    }

    @Override
    public void onRefresh() {

        refreshNotificationRecycler.setRefreshing(false);
    }
}
