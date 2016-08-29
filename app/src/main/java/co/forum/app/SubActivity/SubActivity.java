package co.forum.app.SubActivity;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.r0adkll.slidr.Slidr;
import com.r0adkll.slidr.model.SlidrConfig;
import com.r0adkll.slidr.model.SlidrPosition;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import butterknife.Bind;
import butterknife.ButterKnife;
import co.forum.app.Adapters.CardStackAdapter;
import co.forum.app.ContentEditor.Comment_Editor;
import co.forum.app.Data.CardData;
import co.forum.app.Data.ParseRequest;
import co.forum.app.MainActivity;
import co.forum.app.MainSections.DeckFrag;
import co.forum.app.MainSections.ProfilFrag;
import co.forum.app.MainSections.SearchFrag;
import co.forum.app.R;
import co.forum.app.SharedPref;
import co.forum.app.tools.Message;
import co.forum.app.tools.TagView;

public class SubActivity extends AppCompatActivity {

    public static final String KEY_TYPE = "type";
    public static final String KEY_USER_ID = "key_user_id";

    public static final String KEY_CHAT_ID = "key_chat_id";
    public static final String KEY_FIRST_CHAT_CARD_ID = "key_first_chat_card_id";
    public static final String KEY_STARTER_ID = "key_starter_id";
    public static final String KEY_REPLIER_ID = "key_replier_id";


    public static final String KEY_PROFIL_URL = "key_profil_url";
    public static final String KEY_AUTHOR_FULLNAME = "author_full_name";
    public static final String KEY_FROM_COMMENT = "from_comment";
    public static final String KEY_CURATED = "key_curated";

    public static final String KEY_OPEN_EDITOR_ON_LAUNCH = "open_comment_editor";

    public static final String KEY_CARD_ID_ALONE = "parent_card_id_alone"; //

    public static final String KEY_CARD_TO_OPEN = "card_to_open";

    public static final String TYPE_CARD = "card";
    public static final String TYPE_CARD_ID = "parent_card";
    public static final String TYPE_PROFIL = "profil";
    public static final String TYPE_DECK = "deck";
    public static final String TYPE_CHAT = "chat";
    public static final String TYPE_SUBSCRIPTION = "subscription";

    public static final String  KEY_CHAT_TYPE = "chat_type";
    public static final String  KEY_STORY_TITLE = "story_title";


    public String activity_type;
    public String user_ID;
    public String profil_url;
    public String deck_author_fullname;

    @Bind(R.id.toolbar2) Toolbar toolbar2;

    public SearchFollowFrag followFragment;
    public CardFrag cardFragment;
    public ProfilFrag profilFragment;
    public DeckFrag deckFragment;
    public ChatFrag chatFragment;
    public StoryFrag storyFragment;

    CardData card;
    String parentCardID;

    public static Context context;
    String chatID;
    boolean isStory;
    CardData firstChatCard;
    String starterID;
    String replierID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        context = this;


        setContentView(R.layout.activity_sub);


        ButterKnife.bind(this);

        setSupportActionBar(toolbar2);

        toolbar2.setNavigationIcon(ContextCompat.getDrawable(this, R.drawable.ic_arrow_left_white_18dp));

        activity_type = getIntent().getExtras().getString(KEY_TYPE, TYPE_CARD);

        init_slide_to_exit();

        switch (activity_type) {

            case TYPE_CARD:
                setCard();
                break;

            case TYPE_CARD_ID:
                setCard2(); // from notification click
                break;

            case TYPE_PROFIL:
                setProfil();
                break;

            case TYPE_DECK:
                setDeck();
                break;

            case TYPE_CHAT:
                isStory = getIntent().getExtras().getBoolean(KEY_CHAT_TYPE, false);
                setChat(isStory);
                break;

            case TYPE_SUBSCRIPTION:
                setSubscriptionPage();
                break;
        }
    }




    public MenuItem action_subscribe;
    public static MenuItem action_search_subscription;

    // search
    SearchManager searchManager;
    public SearchView searchBar;
    public boolean searchPageActive = false;

    @Override
    protected void onResume() {
        super.onResume();

        ((MainActivity)MainActivity.context).listen_to_pushnotif(false);

    }

    @Override
    protected void onPause() {
        super.onPause();

        ((MainActivity)MainActivity.context).listen_to_pushnotif(true);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Comment_Editor.RESULT_REPLY_CARD_PICT && resultCode == RESULT_OK && data != null) {

            // receive pict for new card

            cardFragment.comment_editor.choose_pict_editor(data.getData());

        } else if (requestCode == Comment_Editor.RESULT_REPLY_CHAT_PICT && resultCode == RESULT_OK && data != null) {

            chatFragment.comment_editor.choose_pict_editor(data.getData());

        } else if (requestCode == Comment_Editor.RESULT_REPLY_STORY_PICT && resultCode == RESULT_OK && data != null) {

            storyFragment.comment_editor.choose_pict_editor(data.getData());

        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_sub, menu);

        action_subscribe = menu.findItem(R.id.action_subscribe);

        action_search_subscription = menu.findItem(R.id.action_search_subscription);

        if(activity_type.equals(TYPE_SUBSCRIPTION)) {

            action_search_subscription.setVisible(true);

            init_SearchViewLayout();

            init_SearchViewTextListeners();

        }

        //if(!activity_type.equals(TYPE_CARD)) action_subscribe.setVisible(false);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        // top left button <-
        if (id == android.R.id.home) {

            if (activity_type.equals(TYPE_CARD) || activity_type.equals(TYPE_CARD_ID) || activity_type.equals(TYPE_CHAT)) closeEditorPanel();

            else if (activity_type.equals(TYPE_PROFIL) || activity_type.equals(TYPE_DECK)) onBackPressed();

            else if(activity_type.equals(TYPE_SUBSCRIPTION)) onBackPressed();

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {

        if (( activity_type.equals(TYPE_CARD) || activity_type.equals(TYPE_CARD_ID) ) &&

                cardFragment.reply_content_panel_structure.getPanelState() == SlidingUpPanelLayout.PanelState.EXPANDED) {

            cardFragment.reply_content_panel_structure.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);

        } else if(activity_type.equals(TYPE_CHAT) && !isStory &&

                chatFragment.chat_content_panel_structure.getPanelState() == SlidingUpPanelLayout.PanelState.EXPANDED) {

            chatFragment.chat_content_panel_structure.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);

        } else if(activity_type.equals(TYPE_CHAT) && isStory &&

                storyFragment.story_content_panel_structure.getPanelState() == SlidingUpPanelLayout.PanelState.EXPANDED) {

            storyFragment.story_content_panel_structure.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);

        } else {

            super.onBackPressed();

            overridePendingTransition(0, R.anim.exit_right);
        }

    }


    @Override
    public void overridePendingTransition(int enterAnim, int exitAnim) {
        super.overridePendingTransition(enterAnim, exitAnim);
    }


    private void init_slide_to_exit() {

        final SlidrConfig configON = new SlidrConfig.Builder()
                .position(SlidrPosition.LEFT)
                .sensitivity(0.7f)
                .scrimColor(Color.BLACK)
                .scrimStartAlpha(0.8f)
                .scrimEndAlpha(0f)
                .velocityThreshold(2400)
                .distanceThreshold(0.4f)
                .edge(true)
                .edgeSize(0.18f) // The % of the screen that counts as the edge, default 18%
                .build();

        Slidr.attach(this, configON);
    }


    // from top left button pour cardFragment uniquement
    private void closeEditorPanel() {

        // Check if no view has focus:
        View view = this.getCurrentFocus();

        if (view != null) MainActivity.hideSoftKeyboard(view);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {

            @Override
            public void run() {
                onBackPressed();
            }

        }, 100); // 5000ms delay

    }


    private void setCard() {

        setTitle("Card");
        toolbar2.setVisibility(View.GONE);

        // 1 - transfert notifDataList to fragment

        card = getIntent().getExtras().getParcelable(KEY_CARD_TO_OPEN);

        Bundle bundle = new Bundle();

        bundle.putParcelable(KEY_CARD_TO_OPEN, card);

        bundle.putBoolean(KEY_FROM_COMMENT, getIntent().getExtras().getBoolean(KEY_FROM_COMMENT));

        bundle.putBoolean(KEY_OPEN_EDITOR_ON_LAUNCH, getIntent().getExtras().getBoolean(KEY_OPEN_EDITOR_ON_LAUNCH));

        // 2 - display fragment

        cardFragment = new CardFrag();

        cardFragment.setArguments(bundle);

        FragmentManager manager = getSupportFragmentManager();

        FragmentTransaction transaction = manager.beginTransaction();

        transaction.add(R.id.body, cardFragment, "cardFrag");

        transaction.commit();
    }


    private void setCard2() {

        setTitle("Card");
        toolbar2.setVisibility(View.GONE);

        // 1 - transfert notifDataList to fragment

        parentCardID = getIntent().getExtras().getString(KEY_CARD_TO_OPEN);

        Bundle bundle = new Bundle();

        bundle.putBoolean(KEY_CARD_ID_ALONE, true);

        bundle.putString(KEY_CARD_TO_OPEN, parentCardID);

        bundle.putBoolean(KEY_FROM_COMMENT, getIntent().getExtras().getBoolean(KEY_FROM_COMMENT));

        // 2 - display fragment

        cardFragment = new CardFrag();

        cardFragment.setArguments(bundle);

        FragmentManager manager = getSupportFragmentManager();

        FragmentTransaction transaction = manager.beginTransaction();

        transaction.add(R.id.body, cardFragment, "cardFrag");

        transaction.commit();
    }


    private void setProfil() {

        String authorFullName = getIntent().getExtras().getString(KEY_AUTHOR_FULLNAME, "NULL");

        user_ID = getIntent().getExtras().getString(KEY_USER_ID, "NULL");

        profil_url = getIntent().getExtras().getString(KEY_PROFIL_URL, "NULL");

        boolean curated = getIntent().getExtras().getBoolean(KEY_CURATED, false);

        setTitle(authorFullName.toUpperCase());

        if(curated) {

            toolbar2.setSubtitle("curated account");

            toolbar2.setSubtitleTextColor(ContextCompat.getColor(MainActivity.context, R.color.curated));
        }

        profilFragment = new ProfilFrag();

        Bundle bundle = new Bundle();

        bundle.putString(ProfilFrag.KEY_USER_ID, user_ID);
        bundle.putString(ProfilFrag.KEY_USER_NAME, authorFullName);
        bundle.putString(ProfilFrag.KEY_PROFIL_URL, profil_url);
        bundle.putBoolean(ProfilFrag.KEY_CURATED, curated);

        profilFragment.setArguments(bundle);

        FragmentManager manager = getSupportFragmentManager();

        FragmentTransaction transaction = manager.beginTransaction();

        transaction.add(R.id.body, profilFragment, "profilFragment");

        transaction.commit();

        toolbar2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                profilFragment.hideshow_profil_description();
            }
        });
    }


    private void setDeck() {

        user_ID = getIntent().getExtras().getString(KEY_USER_ID, "null");

        deck_author_fullname = getIntent().getExtras().getString(KEY_AUTHOR_FULLNAME, "user");

        setTitle(deck_author_fullname + "'s cards");

        Bundle bundle = new Bundle();

        if(user_ID.equals(((MainActivity)MainActivity.context).localData.getUserData(SharedPref.KEY_USER_ID)))

            bundle.putInt(DeckFrag.KEY_DECK_TYPE, CardStackAdapter.MAIN_USER_DECK);

        else bundle.putInt(DeckFrag.KEY_DECK_TYPE, CardStackAdapter.OTHER_USER_DECK);

        bundle.putString(DeckFrag.KEY_USER_ID, user_ID);

        deckFragment = new DeckFrag();

        deckFragment.setArguments(bundle);

        FragmentManager manager = getSupportFragmentManager();

        FragmentTransaction transaction = manager.beginTransaction();

        transaction.add(R.id.body, deckFragment, "deckFragment");

        transaction.commit();
    }


    public void setChat_next(CardData firstChatCard, String chatID, String starterID, String replierID, boolean isStory) {

        FragmentManager manager = getSupportFragmentManager();

        FragmentTransaction transaction = manager.beginTransaction();

        Bundle bundle = new Bundle();

        if(isStory) {

            bundle.putString(StoryFrag.KEY_CHAT_ID, chatID);
            bundle.putParcelable(StoryFrag.KEY_FIRST_STORY_CARD_ID, firstChatCard);
            bundle.putString(StoryFrag.KEY_AUTHOR_ID, starterID);
            bundle.putString(StoryFrag.KEY_STORY_TITLE, getIntent().getExtras().getString(KEY_STORY_TITLE, ""));

            storyFragment = new StoryFrag();
            storyFragment.setArguments(bundle);

            transaction.add(R.id.body, storyFragment, "storyFrag");

        } else {

            bundle.putString(ChatFrag.KEY_CHAT_ID, chatID);
            bundle.putParcelable(ChatFrag.KEY_FIRST_CHAT_CARD_ID, firstChatCard);
            bundle.putString(ChatFrag.KEY_STARTER_ID, starterID);
            bundle.putString(ChatFrag.KEY_REPLIER_ID, replierID);

            chatFragment = new ChatFrag();
            chatFragment.setArguments(bundle);

            transaction.add(R.id.body, chatFragment, "chatFrag");

        }
        transaction.commit();
    }


    private void setChat(boolean isStory) {

        setTitle("CHAT");

        toolbar2.setVisibility(View.GONE);

        chatID = getIntent().getExtras().getString(KEY_CHAT_ID);

        //Message.message(context, "chat " + chatID);

        ParseRequest.get_ChatElements(context, chatID, isStory);

    }


    private void setSubscriptionPage() {

        setTitle(null);

        toolbar2.setVisibility(View.VISIBLE);

        followFragment = new SearchFollowFrag();

        FragmentManager manager = getSupportFragmentManager();

        FragmentTransaction transaction = manager.beginTransaction();

        transaction.add(R.id.body, followFragment, "followFrag");

        transaction.commit();

    }


    private void init_SearchViewLayout() {

        searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);

        searchBar = (SearchView) action_search_subscription.getActionView();

        searchBar.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

        searchBar.setIconifiedByDefault(false);

        action_search_subscription.setVisible(true);

        searchBar.setQueryHint(getResources().getString(R.string.select_tags_to_follow));

        // color

        searchBar.setBackgroundColor(ContextCompat.getColor(context, R.color.white));

        ((EditText) searchBar.findViewById(android.support.v7.appcompat.R.id.search_src_text)).setHintTextColor(ContextCompat.getColor(context, R.color.white)); // hint color

        SearchView.SearchAutoComplete theTextArea = (SearchView.SearchAutoComplete) searchBar.findViewById(R.id.search_src_text); // query color

        theTextArea.setTextColor(ContextCompat.getColor(context, R.color.tag_tx_selected));

        searchBar.setBackgroundColor(ContextCompat.getColor(context, R.color.searchBar));

    }


    private void init_SearchViewTextListeners() {

        searchBar.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            private void add_tag(String tag) {

                String selection = tag.replace(" ", "");

                if (!followFragment.tags_in_header.contains(selection)) {

                    searchBar.setQuery("", false);

                    TagView tagView = new TagView(context, selection, TagView.HEADER_TAG, true);

                    followFragment.add_TagInHeader(selection, tagView);

                } else {

                    searchBar.setQuery(selection, false);

                    Message.message(context, getResources().getString(R.string.tag_already_selected));

                }
            }

            @Override
            public boolean onQueryTextSubmit(String tag) {

                //if(!tag.isEmpty() && !tag.equals(" ")) submit_Tag(tag);

                add_tag(tag);

                return false;

            }

            @Override
            public boolean onQueryTextChange(String tag) {

                if (tag.endsWith(" ")) add_tag(tag);

                else if (!tag.isEmpty()) followFragment.get_suggestions(tag);

                else if (tag.isEmpty() && followFragment.tags_in_header.isEmpty())
                    followFragment.get_suggestions("");

                /*
                if (!deckFragment.searchFragment.empty_QueryfromTagClick) {

                    deckFragment.searchFragment.set_tag_indicator(SearchFrag.LOADING);

                    deckFragment.searchFragment.tags_suggestion.removeAllViews();

                    show_tags_suggestion(tag);

                    if (tag.length() == 1 && tag.startsWith(" ")) searchBar.setQuery("", false);

                    if (tag.contains("  "))
                        searchBar.setQuery(tag.replace("  ", ""), false); // exception

                    else if (tag.endsWith(" ") && tag.length() != 1)
                        submit_Tag(tag.replace(" ", "")); // selection du tag

                } else deckFragment.searchFragment.empty_QueryfromTagClick = false;

                */

                return false;
            }

        });

    }


    //
    public void displayReplyCard_inChat(CardData replyCard) {

        if(isStory) {

            final int lastPosition = storyFragment.storyAdapter.getItemCount();

            storyFragment.cardsList.add(lastPosition, replyCard);

            //((SubActivity)getContext()).cardFragment.commentAdapter.addItem(replyCard, lastPosition);

            storyFragment.storyAdapter.notifyItemInserted(lastPosition + 1);

            if (storyFragment.story_content_panel_structure.getPanelState() != SlidingUpPanelLayout.PanelState.COLLAPSED)

                storyFragment.story_content_panel_structure.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);

            Runnable myRunnable2 = new Runnable() {
                @Override
                public void run() {

                    storyFragment.recycler_story_cards.smoothScrollToPosition(lastPosition + 1);
                }
            };

            Handler myHandler2 = new Handler();
            myHandler2.postDelayed(myRunnable2, 550);

        } else {

            final int lastPosition = chatFragment.chatAdapter.getItemCount();

            chatFragment.cardsList.add(lastPosition, replyCard);

            //((SubActivity)getContext()).cardFragment.commentAdapter.addItem(replyCard, lastPosition);

            chatFragment.chatAdapter.notifyItemInserted(lastPosition + 1);

            if (chatFragment.chat_content_panel_structure.getPanelState() != SlidingUpPanelLayout.PanelState.COLLAPSED)

                chatFragment.chat_content_panel_structure.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);

            Runnable myRunnable2 = new Runnable() {
                @Override
                public void run() {

                    chatFragment.recycler_chat_message.smoothScrollToPosition(lastPosition + 1);
                }
            };

            Handler myHandler2 = new Handler();
            myHandler2.postDelayed(myRunnable2, 550);

        }

    }






    public static void openCardWithID(Context context, String cardID) {

        Intent activity = new Intent(context, SubActivity.class);

        activity.putExtra(KEY_TYPE, TYPE_CARD_ID);

        activity.putExtra(KEY_CARD_TO_OPEN, cardID);

        activity.putExtra(KEY_FROM_COMMENT, false);

        context.startActivity(activity);

    }

    public static void openCard(Context context, CardData card, boolean fromComment, boolean openCommentEditor) {

        Intent activity = new Intent(context, SubActivity.class);

        activity.putExtra(KEY_TYPE, TYPE_CARD);

        activity.putExtra(KEY_CARD_TO_OPEN, card);

        activity.putExtra(KEY_FROM_COMMENT, fromComment);

        activity.putExtra(KEY_OPEN_EDITOR_ON_LAUNCH, openCommentEditor);

        context.startActivity(activity);
    }

    public static void openUserProfil(Context context, String authorFullName, String userID,
                                      String profil_url, boolean curatedAccount) {

        Intent activity = new Intent(context, SubActivity.class);

        activity.putExtra(KEY_TYPE, TYPE_PROFIL);

        activity.putExtra(KEY_AUTHOR_FULLNAME, authorFullName);

        activity.putExtra(KEY_USER_ID, userID);

        activity.putExtra(KEY_PROFIL_URL, profil_url);

        activity.putExtra(KEY_CURATED, curatedAccount);

        context.startActivity(activity);
    }

    public static void openUserDeck(Context context, String authorFullName, String user_ID) {

        Intent activity = new Intent(context, SubActivity.class);

        activity.putExtra(KEY_TYPE, TYPE_DECK);

        activity.putExtra(KEY_USER_ID, user_ID);

        activity.putExtra(KEY_AUTHOR_FULLNAME, authorFullName);

        context.startActivity(activity);
    }

    public static void openChat(Context context, String chat_ID, boolean isStory, String story_title) {

        Intent activity = new Intent(context, SubActivity.class);

        activity.putExtra(KEY_CHAT_TYPE, isStory);

        if(isStory) activity.putExtra(KEY_STORY_TITLE, story_title);

        activity.putExtra(KEY_TYPE, TYPE_CHAT);

        activity.putExtra(KEY_CHAT_ID, chat_ID);

        context.startActivity(activity);
    }


    public static void openSubscriptionPage(Context context) {

        Intent activity = new Intent(context, SubActivity.class);

        activity.putExtra(KEY_TYPE, TYPE_SUBSCRIPTION);

        context.startActivity(activity);

    }
}
