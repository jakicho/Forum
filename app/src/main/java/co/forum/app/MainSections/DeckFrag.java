package co.forum.app.MainSections;


import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import co.forum.app.Adapters.NavigationAdapter;
import co.forum.app.Data.CardData;
import co.forum.app.Data.ParseRequest;
import co.forum.app.MainActivity;
import co.forum.app.R;
import co.forum.app.Adapters.CardStackAdapter;
import co.forum.app.cardStackView.CardStackView;
import co.forum.app.cardStackView.SwipeTouchListener;
import co.forum.app.tools.Message;
import co.forum.app.tools.Tags;


public class DeckFrag extends Fragment {

    public static final String KEY_DECK_TYPE = "key_deck_type";
    public static final String KEY_USER_ID = "key_user_id";

    public static final int DEMO_DECK = 777;

    public View layout;
    private LayoutInflater inflater;
    ViewGroup container;

    public int deck_type;
    public boolean isCuratedDeck;

    public boolean isSearchResult;
    public boolean isSerchResult_BeforePanelOpen;
    public String userId;
    public int caller;

    public CardStackAdapter cardStackAdapter;

    public List<CardData> cardDataset;

    public static int bottom_margin;

    public DeckFrag() {
        // Required empty public constructor
    }

    @Bind(R.id.loading_deck) public LinearLayout loading_deck;
    @Bind(R.id.loaderDeck) public LinearLayout loaderDeck;
    @Bind(R.id.no_swipeup_indicator) public FrameLayout no_swipeup_indicator;
    @Bind(R.id.main_layout) public FrameLayout main_layout;
    @Bind(R.id.reload_home) public LinearLayout reload_home;
    @Bind(R.id.search_indicator) public ImageView search_indicator;
    @Bind(R.id.card_stack_view) public CardStackView card_stack_view;
    @Bind(R.id.no_deck_indicator) public LinearLayout no_deck_indicator;
    @Bind(R.id.search_container) public FrameLayout search_container;

    public SearchFrag searchFragment;


    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        isSearchResult = false;

        deck_type = getArguments().getInt(KEY_DECK_TYPE, CardStackAdapter.GLOBAL_DECK);

        if(deck_type == CardStackAdapter.MAIN_USER_DECK) caller = ParseRequest.MAIN_USER_PROFIL_DECK;

        else if(deck_type == CardStackAdapter.OTHER_USER_DECK) caller = ParseRequest.OTHER_USER_PROFIL_DECK;

        else if(deck_type == CardStackAdapter.DEMO_DECK) caller = DEMO_DECK;

        userId = getArguments().getString(KEY_USER_ID, "null");

    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        layout = inflater.inflate(R.layout.fragment_deck, container, false);

        this.container = container;

        this.inflater = inflater;

        ButterKnife.bind(this, layout);

        loaderDeck.setVisibility(View.GONE);

        loading_deck.setVisibility(View.VISIBLE);

        if(deck_type == CardStackAdapter.MAIN_USER_DECK) no_swipeup_indicator.setVisibility(View.VISIBLE);

        boolean curatedAccount = ((MainActivity)MainActivity.context).localData.getCurated();

        if(deck_type == CardStackAdapter.GLOBAL_DECK) {

            isCuratedDeck = false;

            // TODO ICI LA MESURE
            ViewTreeObserver vto = ((MainActivity)MainActivity.context).tabLayout.getViewTreeObserver();
            vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {

                    removeOnGlobalLayoutListener(((MainActivity) MainActivity.context).tabLayout, this); //header_reply.getViewTreeObserver().removeGlobalOnLayoutListener(this);

                    bottom_margin = ((MainActivity) MainActivity.context).tabLayout.getMeasuredHeight();

                    /// ----

                    main_layout.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.deck_bg));

                    searchFragment = new SearchFrag();

                    SearchFrag.add_searchFrag(searchFragment, search_container, getFragmentManager());

                    if (((MainActivity) MainActivity.context).localData.userIsConnected())

                        ParseRequest.get_GlobalDeck(getContext(), 0, isCuratedDeck);

                    reload_home.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View view) {

                            searchFragment.remove_TagFromHeader(null, null);

                            searchFragment.reset_SearchEngine();

                            reload_home.setVisibility(View.GONE);

                            reset_toGlobalDeck();
                        }
                    });

                }
            });





        } else if(deck_type == DEMO_DECK) {

            loading_deck.setVisibility(View.GONE);

            displayCards(getContext(), getDemoDeck(), 0);

        }  else ParseRequest.get_UserCards(getContext(), userId, 0, caller); // user deck main/other


        return layout;
    }


    // global deck
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public static void removeOnGlobalLayoutListener(View v, ViewTreeObserver.OnGlobalLayoutListener listener){
        if (Build.VERSION.SDK_INT < 16) {
            v.getViewTreeObserver().removeGlobalOnLayoutListener(listener);
        } else {
            v.getViewTreeObserver().removeOnGlobalLayoutListener(listener);
        }
    }


    public void displayCards(Context context, ArrayList<CardData> cardsList, int offset) {

        loaderDeck.setVisibility(View.GONE);

        if(isNavQuoteDeck) reload_home.setVisibility(View.VISIBLE);

        if (offset == 0) {

            if (cardsList.size() == 0) {

                //no_deck_indicator.setVisibility(View.VISIBLE); //no cards

            } else displayFirstCards(context, cardsList);

        } else add_Cards(cardsList);
    }

    public void createEmptyAdapter() {

        this.cardDataset = new ArrayList<>();

        cardStackAdapter = new CardStackAdapter(getActivity(), cardDataset, deck_type, userId);

        card_stack_view.setOrientation(SwipeTouchListener.Orientation.Vertical);

        card_stack_view.setTypeOfDeck(deck_type);

        card_stack_view.setAdapter(cardStackAdapter);

        cardStackAdapter.reloadStack(getContext());

    }

    public void displayFirstCards(final Context context, final ArrayList<CardData> cardDataset) {

        this.cardDataset = cardDataset;

        cardStackAdapter = new CardStackAdapter(getActivity(), cardDataset, deck_type);

        card_stack_view.setOrientation(SwipeTouchListener.Orientation.Vertical);

        card_stack_view.setTypeOfDeck(deck_type);

        card_stack_view.setAdapter(cardStackAdapter);

        //final View emptyView = inflater.inflate(R.layout.item_no_more_card, null);

        //card_stack_view.setEmptyView(emptyView);

    }


    public void add_Cards(final ArrayList<CardData> cardToAdd) {

        cardDataset.addAll(cardToAdd);

        cardStackAdapter.notifyDataSetChanged();

    }


    public void display_CardsFromFollow(Context context, final ArrayList<CardData> cardToAdd, ArrayList<String> tagsSelected_Active, int offset) {

        loading_deck.setVisibility(View.GONE); //loaderDeck.setVisibility(View.GONE);

        reload_home.setVisibility(View.VISIBLE);

        isSearchResult = false;

        isSubscription = true;

        if (offset == 0) {

            tags_subscriptionList = tagsSelected_Active;

            if (cardToAdd.size() == 0) {

                //no_deck_indicator.setVisibility(View.VISIBLE); //no cards

            } else displayFirstCards(context, cardToAdd);

        } else add_Cards(cardToAdd);
    }



    //public ArrayList<String> tagsSelected_Active;

    public String navAuthorID;

    public boolean isNavQuoteDeck;

    public boolean isSubscription;

    public ArrayList<String> tags_subscriptionList;

    public void display_authorNavCards(Context context, String navAuthorID) {

        loading_deck.setVisibility(View.VISIBLE);

        reload_home.setVisibility(View.GONE);

        this.navAuthorID = navAuthorID;

        //search_indicator.setVisibility(View.VISIBLE); // TODO QUOTE INDICATOR

        isNavQuoteDeck = true;

        isSearchResult = false;

        isSubscription = false;

        ParseRequest.get_AuthorCards(context, navAuthorID, 0);

    }


    public void display_AllCardsFromSearch(final ArrayList<CardData> cardToAdd) {

        //this.tagsSelected_Active = tagsSelected_Active;

        // afficher icone loupe

        loading_deck.setVisibility(View.GONE);

        reload_home.setVisibility(View.VISIBLE);

        //search_indicator.setVisibility(View.VISIBLE);

        isSearchResult = true;

        isSubscription = false;


        cardStackAdapter.offset = 0;

        card_stack_view.removeAllViews();

        cardDataset.clear();

        cardDataset.addAll(cardToAdd);

        card_stack_view.setAdapter(cardStackAdapter);

        //Message.message(getContext(), "" + cardToAdd.size());

    }


    public void empty(Context context) {

        displayCards(context, new ArrayList<CardData>(), 0);

    }



    public void reset_toGlobalDeck() {

        MainActivity.action_rankResult.setVisible(false);

        loading_deck.setVisibility(View.VISIBLE);

        isCuratedDeck = false;

        isNavQuoteDeck = false;

        isSubscription = false;

        isSearchResult = false;

        Message.message(getContext(), "Reset Stack");

        ParseRequest.get_GlobalDeck(getContext(), 0, isCuratedDeck);

        NavigationAdapter.deselect_other_row(-1);

    }

    public static final String UPVOTE = "upvote_count";
    public static final String TIMESTAMP = "createdAt";

    public String order = TIMESTAMP;

    public void rankResult() {

        if(isSearchResult) {

            if (order.equals(UPVOTE)) {

                order = TIMESTAMP;

                MainActivity.action_rankResult.setIcon(R.drawable.ic_rank_recent);

            } else if (order.equals(TIMESTAMP)) {

                order = UPVOTE;

                MainActivity.action_rankResult.setIcon(R.drawable.ic_rank_best);

            }

            card_stack_view.removeAllViews();

            loading_deck.setVisibility(View.VISIBLE);
            reload_home.setVisibility(View.GONE);

            ParseRequest.get_Cards_by_ranking(searchFragment.tagsSelected_Active, order);

        } else if(isSubscription) {

            if (order.equals(UPVOTE)) {

                order = TIMESTAMP;

                MainActivity.action_rankResult.setIcon(R.drawable.ic_rank_recent);

            } else if (order.equals(TIMESTAMP)) {

                order = UPVOTE;

                MainActivity.action_rankResult.setIcon(R.drawable.ic_rank_best);

            }

            card_stack_view.removeAllViews();

            loading_deck.setVisibility(View.VISIBLE);

            reload_home.setVisibility(View.GONE);

            ParseRequest.get_subscriptionCards(getContext(), tags_subscriptionList, order, 0);

        }
    }




    public ArrayList<CardData> getDemoDeck(){

        ArrayList<CardData> data = new ArrayList<>();

        String[] default_author_name = getActivity().getResources().getStringArray(R.array.author_name);
        String[] default_pict_url = getActivity().getResources().getStringArray(R.array.string_pict);
        String[] default_tag_line = getActivity().getResources().getStringArray(R.array.tag_line);
        String[] default_main_content = getActivity().getResources().getStringArray(R.array.main_content);
        int[] default_nb_upvotes = getActivity().getResources().getIntArray(R.array.nb_upvotes);
        int[] default_nb_replies = getActivity().getResources().getIntArray(R.array.nb_replies);
        String[] default_tags = getActivity().getResources().getStringArray(R.array.tags);


        for(int i=0; i<default_author_name.length; i++){

            CardData current = new CardData();

            current.setAuthor_full_name(default_author_name[i]);
            current.set_user_pict_url(default_pict_url[i]);
            current.set_curated(true);
            current.setCurated_tagline(default_tag_line[i]);

            current.setMain_content(default_main_content[i]);
            current.setNb_upvotes(default_nb_upvotes[i]);
            current.setNb_replies(default_nb_replies[i]);
            current.setTags_list(Tags.Spliter(default_tags[i]));

            data.add(current);
        }
        return data;
    }

}
