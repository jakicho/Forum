package co.forum.app.MainSections;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import butterknife.Bind;
import butterknife.ButterKnife;
import co.forum.app.Adapters.NavigationAdapter;
import co.forum.app.Data.CardData;
import co.forum.app.Data.ParseCard;
import co.forum.app.Data.ParseRequest;
import co.forum.app.MainActivity;
import co.forum.app.R;
import co.forum.app.tools.Message;
import co.forum.app.tools.RowLayout;
import co.forum.app.tools.TagView;
import co.forum.app.tools.Tags;
import co.forum.app.tools.tools;

public class SearchFrag extends Fragment{

    // tab title
    //public static final String MOST_USED_TAGS = "MOST USED TAGS";
    //public static final String RELATED_TAGS = "RELATED TAGS";

    // tag indicators
    public static final int FIRST_CHOICE = 0;
    public static final int MATCH_CHOICE = 1;
    public static final int UPDATE_TAGS = 2;
    public static final int NO_TAGS = 3;
    public static final int HIDE_INDICATOR = 4;
    public static final int LOADING = 5;

    // tag click
    public static final int REMOVE_ALL = -1;

    // query not confirm
    public ArrayList<String> tagsSelected_NC;
    public List<ParseCard> parseCardsList_NC;
    public ArrayList<ParseCard> resultCardsList;

    // query active
    public List<ParseCard> cardsList_Active;
    public ArrayList<String> tagsSelected_Active;


    public  Map<String, Integer> tagsListSuggestion_map;

    public ArrayList<String> defaultTags;

    public boolean empty_QueryfromTagClick;

    View layout;

    public SearchFrag() {}

    @Bind(R.id.shadow) public LinearLayout shadow;
    @Bind(R.id.search_page) public LinearLayout search_page;

    // result header
    @Bind(R.id.header_tags_selected) public RowLayout header_tags_selected;
    @Bind(R.id.cardsCount) public TextView cardsCount;

    @Bind(R.id.tabTags) public TextView tabTags; // header indicator

    // tags body
    @Bind(R.id.tags_suggestion) public RowLayout tags_suggestion;
    @Bind(R.id.notag_indicator) public LinearLayout notag_indicator;
    @Bind(R.id.tv_notag_indicator) public TextView tv_notag_indicator;
    @Bind(R.id.tags_scrollLayout) public ScrollView tags_scrollLayout;


    // bouton et reset result
    @Bind(R.id.bottom_btns) public LinearLayout bottom_btns;
    @Bind(R.id.ic_reset) public ImageView ic_reset;
    @Bind(R.id.ic_add) public ImageView ic_add;
    @Bind(R.id.ic_result) public ImageView ic_result;
    @Bind(R.id.show_reset_page) public LinearLayout btn_reset;
    @Bind(R.id.btn_add_search) public LinearLayout btn_add_search;
    @Bind(R.id.btn_show_result) public LinearLayout btn_show_result;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        layout = inflater.inflate(R.layout.panel_search, container, false);

        ButterKnife.bind(this, layout);

        empty_QueryfromTagClick = false;

        parseCardsList_NC = new ArrayList<>();

        resultCardsList = new ArrayList<>();

        tagsSelected_NC = new ArrayList<>(); // header tags

        // body tags
        defaultTags = new ArrayList<>();

        tagsListSuggestion_map = new Map<String, Integer>() {
            @Override
            public void clear() {

            }

            @Override
            public boolean containsKey(Object key) {
                return false;
            }

            @Override
            public boolean containsValue(Object value) {
                return false;
            }

            @NonNull
            @Override
            public Set<Entry<String, Integer>> entrySet() {
                return null;
            }

            @Override
            public Integer get(Object key) {
                return null;
            }

            @Override
            public boolean isEmpty() {
                return false;
            }

            @NonNull
            @Override
            public Set<String> keySet() {
                return null;
            }

            @Override
            public Integer put(String key, Integer value) {
                return null;
            }

            @Override
            public void putAll(Map<? extends String, ? extends Integer> map) {

            }

            @Override
            public Integer remove(Object key) {
                return null;
            }

            @Override
            public int size() {
                return 0;
            }

            @NonNull
            @Override
            public Collection<Integer> values() {
                return null;
            }
        };

        ParseRequest.get_MostUsedTags(defaultTags);

        return layout;

    }



    public static void add_searchFrag(SearchFrag searchFragment, FrameLayout search_container, android.support.v4.app.FragmentManager manager) {

        FragmentTransaction transaction = manager.beginTransaction();

        transaction.add(R.id.search_container, searchFragment, "searchFragment");

        transaction.commit();

        search_container.setTranslationY(-200);

    }


    // header
    public void add_TagInHeader(final String tag, TagView tagView) {

        tagsSelected_NC.add(tag);

        header_tags_selected.addView(tagView);

        deactivate_btn_follow();

        load_related_tags(true);
    }


    public void remove_TagFromHeader(final String tag, TagView tagView) {

        if(tag == null && tagView == null) {

            tagsSelected_NC.clear();

            header_tags_selected.removeAllViews();

        } else {

            tagsSelected_NC.remove(tag);

            header_tags_selected.removeView(tagView);

            if(tagsSelected_NC.size() == 0)
                ((MainActivity)MainActivity.context).deckFragment.isSearchResult = false;

        }

        deactivate_btn_follow();

        load_related_tags(false);
    }


    public void set_RowCounters() {

        if(parseCardsList_NC.size() != 0) {

            if(parseCardsList_NC.size() == 1) cardsCount.setText(parseCardsList_NC.size() + " card");

            else cardsCount.setText(parseCardsList_NC.size() + " cards");

        } else cardsCount.setText("");
    }



    // body
    public void set_tag_indicator(int type) {

        if (type == HIDE_INDICATOR) notag_indicator.setVisibility(View.GONE);

        if (type == LOADING) {

            tv_notag_indicator.setText("loading...");

            notag_indicator.setVisibility(View.VISIBLE);

        }

        if (type == NO_TAGS) {

            tv_notag_indicator.setText("There is no card with the tag '#" + MainActivity.searchBar.getQuery().toString()+"' yet");

            notag_indicator.setVisibility(View.VISIBLE);

        }

        /*
        if (type == FIRST_CHOICE) {

            if(firstPossibleTags.size() == 0 && MainActivity.searchBar.getQuery().length() != 0) {

                tv_notag_indicator.setText("yo, There is no card with the tag '#" +MainActivity.searchBar.getQuery().toString()+"' yet");

                notag_indicator.setVisibility(View.VISIBLE);

            } else notag_indicator.setVisibility(View.GONE);
        }
        */

        if (type == MATCH_CHOICE) {

            if(tags_suggestion.getChildCount() == 0 && MainActivity.searchBar.getQuery().length() != 0) {

                tv_notag_indicator.setText("There is no card with this additional tag '#" +MainActivity.searchBar.getQuery().toString()+"' for your search");

                notag_indicator.setVisibility(View.VISIBLE);

            } else notag_indicator.setVisibility(View.GONE);
        }


        if (type == UPDATE_TAGS) {

            /*
            tv_notag_indicator.setText("3- tags suggestion :" + tagsListSuggestion.size());


            if(tags_suggestion.getChildCount() == 0) notag_indicator.setVisibility(View.VISIBLE);

            else notag_indicator.setVisibility(View.GONE);
            */
        }
    }

    public void reset_SearchEngine() {

        // masquer icone loupe

        ((MainActivity)MainActivity.context).deckFragment.search_indicator.setVisibility(View.GONE);

        ((MainActivity) MainActivity.context).deckFragment.isSearchResult = false;

        ((MainActivity)MainActivity.context).deckFragment.cardStackAdapter.offset = 0;


        display_Most_Used_Tags(defaultTags);


        MainActivity.setSearchBar_hint();

        MainActivity.hideSoftKeyboard(MainActivity.searchBar);

        parseCardsList_NC.clear();

        saveActiveQuery();

        tabTags.setText(getResources().getString(R.string.most_used_tags));

        set_RowCounters();

        activate_btn_result();
    }


    public void load_related_tags(boolean tagAdded) {

        if(tagsSelected_NC.isEmpty()) reset_SearchEngine(); // tagAdded = false

        else  {

            if(tagAdded) tags_suggestion.removeAllViews();

            set_tag_indicator(SearchFrag.LOADING);

            ParseRequest.get_RelatedTags(tagsSelected_NC);

            if(MainActivity.searchBar.getQuery().length() != 0) {

                ((MainActivity)MainActivity.context).deckFragment.searchFragment.empty_QueryfromTagClick = true;

                MainActivity.searchBar.setQuery("", false); // re-init
            }

            MainActivity.hideSoftKeyboard(MainActivity.searchBar);

        }
    }


    public void display_Most_Used_Tags(ArrayList<String> TagsList) {

        set_tag_indicator(SearchFrag.HIDE_INDICATOR);

        tags_suggestion.removeAllViews();

        for (final String tag : TagsList)

            tags_suggestion.addView(new TagView(MainActivity.context, tag, TagView.SUGGESTED_TAG));

    }


    public void display_Related_Tags(Map<String, Integer> TagsListMap) {

        tags_suggestion.removeAllViews();

        for (final Map.Entry<String, Integer> pair : TagsListMap.entrySet())

            tags_suggestion.addView(new TagView(MainActivity.context, pair.getKey(), pair.getValue()));
    }


    public void load_tags_beginning_with(String tagBeginning) {

        //firstPossibleTags = new ArrayList<>();

        if(tagBeginning.length() != 0) ParseRequest.getFirstTagsList(tagBeginning); //firstPossibleTags =

        else Message.message(MainActivity.context, "load_tags_beginning_with - empty");
    }


    public void display_tags_beginning_with(ArrayList<String> firstTagsList) {

        if (firstTagsList.isEmpty()) set_tag_indicator(SearchFrag.NO_TAGS);

        else set_tag_indicator(SearchFrag.HIDE_INDICATOR);

        tags_suggestion.removeAllViews();

        for (final String word : firstTagsList) {

            tags_suggestion.addView(new TagView(MainActivity.context, word, TagView.SUGGESTED_TAG));

        }
    }


    static Map<String, Integer> matchTags;

    public void display_Related_Tags_beginning_with(String tagBeginning) {

        if(tagBeginning.isEmpty()) matchTags = tagsListSuggestion_map;

        else {

            matchTags = new HashMap<>(tagsListSuggestion_map);

            for (Map.Entry<String, Integer> pair : tagsListSuggestion_map.entrySet()) {

                if(!pair.getKey().startsWith(tagBeginning)) {

                    //matchTags.put(pair.getKey(), pair.getValue());

                    matchTags.remove(pair.getKey());

                } else {

                    //Message.message(MainActivity.context, "valid: " + pair.getKey());
                }
            }
        }

        tags_suggestion.removeAllViews();

        for (final Map.Entry<String, Integer> pair : matchTags.entrySet())

            tags_suggestion.addView(new TagView(MainActivity.context, pair.getKey(), pair.getValue()));


        set_tag_indicator(MATCH_CHOICE);

    }



    public void set_RelatedTags(List<ParseCard> parseCardList, ArrayList<String> tagsChosen) {

        parseCardsList_NC = parseCardList;

        tabTags.setText(getResources().getString(R.string.related_tags));

        set_tag_indicator(SearchFrag.HIDE_INDICATOR);

        // affichage des suggestions de tags

        Map<String, Integer> tagsMapSorted = tools.sortByComparator(Tags.tagsMap_from_list(parseCardList, tagsChosen), tools.DESC);

        display_Related_Tags(tagsMapSorted);

        tagsListSuggestion_map = tagsMapSorted;

        set_RowCounters();

        MainActivity.setSearchBar_hint();
        //getResultThoughtsList(parseCardList, displayNow);

        activate_btn_result();

    }

    private void deactivate_btn_follow() {

        ic_add.setImageDrawable(ContextCompat.getDrawable(MainActivity.context, R.drawable.ic_plus_off_48dp));
        ((TextView)btn_add_search.getChildAt(1)).setTextColor(ContextCompat.getColor(MainActivity.context, R.color.btn_off));

        btn_add_search.setOnClickListener(null);

    }


    public void activate_btn_result() {

        if(parseCardsList_NC.size() == 0) {

            ic_reset.setImageDrawable(ContextCompat.getDrawable(MainActivity.context, R.drawable.ic_close_off_48dp));
            ic_add.setImageDrawable(ContextCompat.getDrawable(MainActivity.context, R.drawable.ic_plus_off_48dp));
            ic_result.setImageDrawable(ContextCompat.getDrawable(MainActivity.context, R.drawable.ic_chevron_right_off_48dp));

            ((TextView)btn_reset.getChildAt(1)).setTextColor(ContextCompat.getColor(MainActivity.context, R.color.btn_off));
            ((TextView)btn_add_search.getChildAt(1)).setTextColor(ContextCompat.getColor(MainActivity.context, R.color.btn_off));
            ((TextView)btn_show_result.getChildAt(0)).setTextColor(ContextCompat.getColor(MainActivity.context, R.color.btn_off));

            btn_reset.setOnClickListener(null);
            btn_add_search.setOnClickListener(null);
            btn_show_result.setOnClickListener(null);

        } else {

            ic_reset.setImageDrawable(ContextCompat.getDrawable(MainActivity.context, R.drawable.ic_close_white_48dp));
            ic_add.setImageDrawable(ContextCompat.getDrawable(MainActivity.context, R.drawable.ic_plus_white_48dp));
            ic_result.setImageDrawable(ContextCompat.getDrawable(MainActivity.context, R.drawable.ic_chevron_right_white_48dp));

            ((TextView)btn_reset.getChildAt(1)).setTextColor(ContextCompat.getColor(MainActivity.context, R.color.white));
            ((TextView)btn_add_search.getChildAt(1)).setTextColor(ContextCompat.getColor(MainActivity.context, R.color.white));
            ((TextView)btn_show_result.getChildAt(0)).setTextColor(ContextCompat.getColor(MainActivity.context, R.color.white));

            if(tagsSelected_NC.size() == 0) {

                (btn_show_result.getChildAt(0)).setAlpha(0.5f);

                ic_result.setAlpha(0.5f);

            } else {

                (btn_show_result.getChildAt(0)).setAlpha(1f);

                ic_result.setAlpha(1f);
            }


            bottom_btns.setVisibility(View.VISIBLE);

            btn_show_result.setOnClickListener(new View.OnClickListener() {

                private void show_result() {

                    if (tagsSelected_NC.size() != 0) {

                        saveActiveQuery();

                        ((MainActivity) MainActivity.context).show_SearchPage(false);

                        ((MainActivity) MainActivity.context).deckFragment.order = DeckFrag.TIMESTAMP;

                        MainActivity.action_rankResult.setIcon(R.drawable.ic_rank_recent);

                        MainActivity.action_rankResult.setVisible(true);

                        ArrayList<CardData> cardsfounded = CardData.getCardsList(cardsList_Active, "", false);

                        ((MainActivity) MainActivity.context).deckFragment.display_AllCardsFromSearch(cardsfounded);

                        NavigationAdapter.deselect_other_row(-1);

                    }
                }

                @Override
                public void onClick(View v) {

                    show_result();
                }
            });

            btn_add_search.setOnClickListener(new View.OnClickListener() {

                private void subscribe() {

                    if (tagsSelected_NC.size() != 0) {

                        ParseRequest.insert_tags_subscription(getContext(), tagsSelected_NC);
                    }
                }

                @Override
                public void onClick(View v) {

                    subscribe();

                }
            });

            btn_reset.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    MainActivity.action_rankResult.setIcon(R.drawable.ic_rank_recent);

                    MainActivity.action_rankResult.setVisible(false);

                    remove_TagFromHeader(null, null);

                    reset_SearchEngine();
                }
            });
        }
    }

    private void saveActiveQuery() {

        cardsList_Active = parseCardsList_NC;

        tagsSelected_Active = tagsSelected_NC;

    }

}
