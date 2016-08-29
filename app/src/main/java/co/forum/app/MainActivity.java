package co.forum.app;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.design.widget.TabLayout.Tab;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Display;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.google.android.gms.analytics.Tracker;
import com.parse.ParseInstallation;
import com.parse.ParseUser;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import co.forum.app.Adapters.CardStackAdapter;
import co.forum.app.ContentEditor.NewContent_Editor;
import co.forum.app.MainSections.ConvFrag;
import co.forum.app.MainSections.DeckFrag;
import co.forum.app.MainSections.ProfilFrag;
import co.forum.app.MainSections.NavigationSelector;
import co.forum.app.MainSections.ProfilPages.ChatOFrag;
import co.forum.app.MainSections.SearchFrag;
import co.forum.app.OnBoardingActivity.OnBoardingActivity;
import co.forum.app.tools.GPSTracker;
import co.forum.app.tools.Message;
import co.forum.app.tools.NonSwipeableViewPager;

public class MainActivity extends AppCompatActivity {

    public static final String ADMIN_ID = "g47gSRs2u7";

    public static Context context;

    public SharedPref localData;


    @Bind(R.id.toolbar) public Toolbar toolbar;
    @Bind(R.id.drawerLayout) public DrawerLayout drawerLayout;

    @Bind(R.id.tabLayout) public TabLayout tabLayout;
    @Bind(R.id.viewPager) NonSwipeableViewPager viewPager;


    public static NewContent_Editor newContent_editor;

    @Bind(R.id.fab) FloatingActionButton fab;
    @Bind(R.id.new_content_panel_structure) public SlidingUpPanelLayout new_content_panel_structure;

    public static Typeface tf, tfSemiBold, tfSemiBoldItalic, tfBold;
    public static Typeface tf_serif;

    private int[] tabIcons = {
            R.drawable.ic_tab_home,
            R.drawable.ic_tab_conversation,//R.drawable.ic_tab_notification,
            R.drawable.ic_script_white_48dp,
            R.drawable.ic_tab_profil};

    public String[] tabTitle = {"FORUM", "CONVERSATIONS", "STORIES", "USER PROFIL"};

    public DeckFrag deckFragment;
    public ChatOFrag storyFragment;
    public ConvFrag convFragment;
    public ProfilFrag profilFragment;

    public NavigationSelector navigationSelector;

    public static Tracker mTracker;

    // bottom menu slider
    public final static int HIDE_BOTTOM_MENU = 0;
    public final static int SHOW_BOTTOM_MENU = 1;

    public static final int SLIDE_OUT = 0;
    public static final int SLIDE_IN = 1;

    // action top right
    public static MenuItem action_search_tag;
    public static MenuItem action_story_button;
    public static MenuItem action_logout;
    public static MenuItem action_searchButton;
    public static MenuItem action_rankResult;


    // search
    SearchManager searchManager;
    public static SearchView searchBar;
    public boolean searchPageActive = false;

    // search bar visibility
    public static final int SEARCH_HIDDEN = 0;
    public static final int SEARCH_VISIBLE = 1;
    public static int SEARCH_BAR_STATE = SEARCH_HIDDEN;


    // bottom menu scroller
    public static final int HIDE_THRESHOLD = 20;
    public static int scrolledDistance = 0;
    public static boolean controlsVisible = true;


    public static GPSTracker gps;

    private static String push_page="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        context = this;

        localData = new SharedPref(context);

        open_from_push();

        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        setSupportActionBar(toolbar);

        init_Font(context);

        init_drawer();

        init_toolbar_click(0);

        getSupportActionBar().setDisplayShowHomeEnabled(true);

        init_viewPager();

        init_tabLayout();

        init_fab();

        init_new_content_panel();


        init_current_user();

        init_tracker();

    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        action_search_tag = menu.findItem(R.id.action_search_tag);

        action_searchButton = menu.findItem(R.id.action_search_button);

        action_story_button = menu.findItem(R.id.action_story_button);

        action_rankResult = menu.findItem(R.id.action_rankResult);

        action_rankResult.setVisible(false);

        action_logout = menu.findItem(R.id.action_logout);

        action_logout.setVisible(false);

        init_SearchViewLayout();

        init_SearchViewTextListeners();


        if(push_page.equals("comment")) {

            action_rankResult.setVisible(false);
            action_searchButton.setVisible(false);
            action_story_button.setVisible(false);
            action_logout.setVisible(false);

            push_page = "";

        } else if(push_page.equals("chat")) {

            action_rankResult.setVisible(false);
            action_searchButton.setVisible(false);
            action_story_button.setVisible(false);
            action_logout.setVisible(true);

            push_page = "";
        }


        return true;
    }


    @Override
    protected void onResume() {

        super.onResume();

        //Message.message(context, "" + Locale.getDefault().getLanguage());
        //Message.message(context, "" + Resources.getSystem().getConfiguration().locale);


        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        listen_to_pushnotif(false);

        ForumApp.trackPage(mTracker, ForumApp.P_MAIN);

        set_gps();

        if(profilFragment.otherUserChatsTab != null) profilFragment.otherUserChatsTab.refresh();


        if(push_page.equals("message")) {
            viewPager.setCurrentItem(1);
            //convFragment.chat_viewpager.setCurrentItem(0);
            push_page = "";

        } else if(push_page.equals("comment")) {
            viewPager.setCurrentItem(3);
            //profilFragment.profil_viewpager.setCurrentItem(2);
            push_page = "";
        }

    }


    @Override
    protected void onPause() {

        super.onPause();

        listen_to_pushnotif(true);

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == NewContent_Editor.RESULT_NEW_CARD_PICT && resultCode == RESULT_OK && data != null) {

            // receive pict for new card
            newContent_editor.choose_pict_editor(data.getData());

        }

        if (requestCode == ProfilFrag.RESULT_LOAD_IMAGE && resultCode == RESULT_OK) {

            // go to crop activity

            Uri selectedImage = data.getData();

            Intent cropIntent = new Intent(context, CropActivity.class);

            cropIntent.setData(selectedImage);

            ((MainActivity) (MainActivity.context)).startActivityForResult(cropIntent, CropActivity.RESULT_CROP_IMAGE);

        }

        if (requestCode == CropActivity.RESULT_CROP_IMAGE && data != null) {

            // receive image cropped

            Picasso.with(MainActivity.context)

                    .load(localData.getUserData(SharedPref.KEY_PICT_URL))

                    .placeholder(ContextCompat.getDrawable(MainActivity.context, R.drawable.profil_pict))

                    .into(profilFragment.icon_profil);

        } else if (resultCode != RESULT_CANCELED) {

            // from login activity

            int result = data.getIntExtra(OnBoardingActivity.KEY_BACK_FROM_ONBOARDING, -1);

            if( result == OnBoardingActivity.LOGIN_FORM) {

                tabTitle[3] = (localData.getUserData(SharedPref.KEY_FULL_NAME)).toUpperCase();

                setTitle(tabTitle[tabLayout.getSelectedTabPosition()]);


                if (!((MainActivity) MainActivity.context).localData.getCurated()) deckFragment.reset_toGlobalDeck();

                // notifFragment.setupMainUserNotif(context); TODO pour CONVERSATION

                profilFragment.setupMainUserProfil(context);

                ForumApp.trackAction(mTracker, ForumApp.A_NEW_USER);

            }

            else if (result == OnBoardingActivity.SIGNUP_FORM) {

                tabTitle[3] = (localData.getUserData(SharedPref.KEY_FULL_NAME)).toUpperCase();

                setTitle(tabTitle[tabLayout.getSelectedTabPosition()]);


                if(!((MainActivity)MainActivity.context).localData.getCurated())

                    deckFragment.reset_toGlobalDeck();

                // notifFragment.setupMainUserNotif(context); TODO pour CONVERSATION

                profilFragment.setupMainUserProfil(context);

                ForumApp.trackAction(mTracker, ForumApp.A_COME_BACK);

            }

            navigationSelector.page00.reload_after_onboarding();
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();


        // top left button <-
        if (id == android.R.id.home) {

            return true;
        }

        if (id == R.id.action_search_button) {

            deckFragment.isSerchResult_BeforePanelOpen = deckFragment.isSearchResult;

            show_SearchPage(true);
        }

        if(id == R.id.action_story_button) {

            storyFragment.hideshow_stories_selector();

            return true;
        }


        if (id == R.id.action_rankResult) {

            deckFragment.rankResult();

            return true;
        }


        //noinspection SimplifiableIfStatement
        if (id == R.id.action_logout) {

            logout();

            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onBackPressed() {

        if (new_content_panel_structure.getPanelState() == SlidingUpPanelLayout.PanelState.EXPANDED) {

            //SearchFrag searchFrag = (SearchFrag)getSupportFragmentManager().findFragmentByTag("searchFragment");

            //searchFrag.tags_scrollLayout.setPadding(0, 0, 0, searchFrag.bottom_btns.getHeight());
            //searchFrag.bottom_btns.setTranslationY(-searchFrag.bottom_btns.getHeight());


            //toolbar.setVisibility(View.VISIBLE);

            if(newContent_editor.tag_helper_is_visible) newContent_editor.close_tag_editor();

            else if(newContent_editor.preview_card_include.getVisibility() == View.VISIBLE ) newContent_editor.backToEditor();


            else hideEditor_ShowMenu();

            if (viewPager.getCurrentItem() == 0) navigationSelector.mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);

        } else if (!controlsVisible) {

            setMenuAndFabVisibility(SLIDE_IN);

            controlsVisible = true;

        } else if (searchPageActive) {

            show_SearchPage(false);

            if (!deckFragment.isSearchResult && deckFragment.isSerchResult_BeforePanelOpen) {

                deckFragment.cardStackAdapter.offset = 0;

                deckFragment.card_stack_view.removeAllViews();

                deckFragment.cardDataset.clear();

                deckFragment.reset_toGlobalDeck();

            }

        } else if (navigationSelector.mDrawerLayout.isDrawerVisible(GravityCompat.START)) {

            navigationSelector.mDrawerLayout.closeDrawer(GravityCompat.START);

        }


        //
        if(viewPager.getCurrentItem() == 2) {

            storyFragment.stories_appbar.setExpanded(false ,true);

        }
        //else super.onBackPressed();

    }


    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {

        boolean isSearchClicked = false;

        boolean isOutSideDrawerClicked = false;

        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            if (navigationSelector.mDrawerLayout.isDrawerOpen(Gravity.LEFT)) {

                View content = findViewById(R.id.frag_navigation_drawer);
                int[] contentLocation = new int[2];
                content.getLocationOnScreen(contentLocation);
                Rect rect = new Rect(contentLocation[0],
                        contentLocation[1],
                        contentLocation[0] + content.getWidth(),
                        contentLocation[1] + content.getHeight());

                View toolbarView = findViewById(R.id.toolbar);
                int[] toolbarLocation = new int[2];
                toolbarView.getLocationOnScreen(toolbarLocation);
                Rect toolbarViewRect = new Rect(toolbarLocation[0],
                        toolbarLocation[1],
                        toolbarLocation[0] + toolbarView.getWidth(),
                        toolbarLocation[1] + toolbarView.getHeight());


                if (!(rect.contains((int) event.getX(), (int) event.getY())) && !toolbarViewRect.contains((int) event.getX(), (int) event.getY())) {
                    isOutSideDrawerClicked = true;
                } else {
                    isOutSideDrawerClicked = false;

                    if (!(rect.contains((int) event.getX(), (int) event.getY())) &&
                            toolbarViewRect.contains((int) event.getX(), (int) event.getY())) {

                        isSearchClicked = true;
                    }
                }

            } else {
                return super.dispatchTouchEvent(event);
            }
        }

        if (isOutSideDrawerClicked) navigationSelector.mDrawerLayout.closeDrawer(Gravity.LEFT);

        if (isSearchClicked) show_SearchPage(true);

        return super.dispatchTouchEvent(event);
    }


    @Override
    protected void onDestroy() {

        super.onDestroy();

        ParseInstallation installation = ParseInstallation.getCurrentInstallation();
        installation.put("listening", true);
        installation.saveInBackground();
    }


    private void set_gps() {

        gps = new GPSTracker(this, localData.getGPS_authorisation());
        //Message.message(context, "lat: " + gps.getLatitude() + "\nlong: " + gps.getLongitude());

        localData.setUserData(SharedPref.KEY_LATITUDE, (float) gps.getLatitude());
        localData.setUserData(SharedPref.KEY_LONGITUDE, (float) gps.getLongitude());

    }

    private void init_current_user() {



        if (localData.userIsConnected()) {

            ParseUser currentUser = ParseUser.getCurrentUser();

            if (currentUser == null) {

                Message.message(getBaseContext(), "RE-LOGIN");

                OnBoardingActivity.launch_fromMain(context);

            } else {

                ParseUser.getCurrentUser().increment("RunCount");
                ParseUser.getCurrentUser().put("appVersion", getResources().getString(R.string.app_version)); //ParseUser.getCurrentUser().put("appVersion", APP_VERSION);
                ParseUser.getCurrentUser().put("api", android.os.Build.VERSION.SDK_INT);
                ParseUser.getCurrentUser().saveInBackground();

                tabTitle[3] = (localData.getUserData(SharedPref.KEY_FULL_NAME)).toUpperCase();
            }

            //Message.message(context, "user is logged in");

        } else {

            OnBoardingActivity.launch_fromMain(context);
        }

    }


    private void init_drawer() {

        navigationSelector = (NavigationSelector) getSupportFragmentManager().findFragmentById(R.id.frag_navigation_drawer);

        navigationSelector.setup_drawer(R.id.frag_navigation_drawer, drawerLayout, toolbar);

    }


    private void init_tracker() {

        ForumApp application = (ForumApp) getApplication();

        mTracker = application.getDefaultTracker();

    }

    // PUSH

    public void listen_to_pushnotif(boolean state) {

        ParseInstallation installation = ParseInstallation.getCurrentInstallation();
        installation.put("listening", state);
        installation.saveInBackground();
    }

    private void open_from_push() {

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();

        if (extras != null) {

            String jsonData = extras.getString("com.parse.Data");

            JSONObject json = null;

            if(jsonData!= null) {
                try {
                    json = new JSONObject(jsonData);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                String fragment = null;
                try {
                    fragment = json.getString("data");

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                if (fragment != null) push_page = fragment;

            }
        }
    }


    // SEARCH

    private void slide_searchPage(boolean in) {

        if(in) {

            Display display = getWindowManager().getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);

            int height = size.y;

            deckFragment.searchFragment.shadow.animate()
                    .alpha(0)
                    .setInterpolator(new AccelerateInterpolator(30f))
                    .setInterpolator(new DecelerateInterpolator(1f))
                    .start();

            deckFragment.search_container.animate()
                    .translationY(0)
                    .setInterpolator(new AccelerateInterpolator(20f))
                    .setInterpolator(new DecelerateInterpolator(1f))
                    .start();

            deckFragment.main_layout.animate()
                    .translationYBy(height - 150)
                    .setInterpolator(new AccelerateInterpolator(1f))
                    .setInterpolator(new DecelerateInterpolator(1f))
                    .start();

        } else {

            deckFragment.searchFragment.shadow.animate()
                    .alpha(0.8f)
                    .setInterpolator(new AccelerateInterpolator(30f))
                    .setInterpolator(new DecelerateInterpolator(1f))
                    .start();

            deckFragment.search_container.animate()
                    .translationY(-200f)
                    .setInterpolator(new AccelerateInterpolator(30f))
                    .setInterpolator(new DecelerateInterpolator(1f))
                    .start();

            deckFragment.main_layout.animate()
                    .translationY(0)
                    .setInterpolator(new AccelerateInterpolator(30f))
                    .setInterpolator(new DecelerateInterpolator(1f))
                    .start();

        }
    }


    public void show_SearchPage(boolean show) {

        if (show) {

            hide_sidenav_btn();

            searchPageActive = true;

            set_SearchBarTo(SEARCH_VISIBLE);

            slide_searchPage(true);

            slide_MenuAndFab(SLIDE_OUT);

            toolbar.setNavigationIcon(ContextCompat.getDrawable(context, R.drawable.ic_arrow_left_white_18dp));

            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (new_content_panel_structure.getPanelState() == SlidingUpPanelLayout.PanelState.EXPANDED) {

                        closeEditorPanel();

                    } else if (action_search_tag.isVisible()) {

                        // Check if no view has focus:
                        View view = getCurrentFocus();

                        if (view != null) {

                            hideSoftKeyboard(view);

                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {

                                @Override
                                public void run() {
                                    show_SearchPage(false);
                                }

                            }, 100); // 5000ms delay

                        } else show_SearchPage(false);


                        if (!deckFragment.isSearchResult && deckFragment.isSerchResult_BeforePanelOpen) {

                            deckFragment.cardStackAdapter.offset = 0;

                            deckFragment.card_stack_view.removeAllViews();

                            deckFragment.cardDataset.clear();

                            deckFragment.reset_toGlobalDeck();
                        }
                    }

                    navigationSelector.drawerToggle.setDrawerIndicatorEnabled(true);

                }
            });

            action_searchButton.setVisible(false);

        } else {

            // Check if no view has focus:
            //View view = this.getCurrentFocus();

            //if (view != null) hideSoftKeyboard(view);

            show_sidenav_btn();

            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    navigationSelector.mDrawerLayout.openDrawer(Gravity.LEFT);
                }
            });



            searchPageActive = false;

            set_SearchBarTo(SEARCH_HIDDEN);

            slide_searchPage(false);

            slide_MenuAndFab(SLIDE_IN);

            //toolbar.setNavigationIcon(null);

            action_searchButton.setVisible(true);


        }

    }


    private void init_SearchViewLayout() {

        searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);

        searchBar = (SearchView) action_search_tag.getActionView();

        searchBar.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

        searchBar.setIconifiedByDefault(false);

        action_search_tag.setVisible(false);

        searchBar.setQueryHint(getResources().getString(R.string.et_search_tag));

        // color

        searchBar.setBackgroundColor(ContextCompat.getColor(context, R.color.white));

        ((EditText) searchBar.findViewById(android.support.v7.appcompat.R.id.search_src_text)).setHintTextColor(ContextCompat.getColor(context, R.color.white)); // hint color

        SearchView.SearchAutoComplete theTextArea = (SearchView.SearchAutoComplete) searchBar.findViewById(R.id.search_src_text); // query color

        theTextArea.setTextColor(ContextCompat.getColor(context, R.color.tag_tx_selected));

        searchBar.setBackgroundColor(ContextCompat.getColor(context, R.color.searchBar));

    }


    private void init_SearchViewTextListeners() {

        searchBar.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            private void show_tags_suggestion(String tag) {

                // - suggestion : si pas de tag selectionne,
                if (deckFragment.searchFragment.tagsSelected_NC.size() == 0)

                    if (tag.isEmpty()) { // afficher les meilleurs tags

                        deckFragment.searchFragment.display_Most_Used_Tags(deckFragment.searchFragment.defaultTags);

                        deckFragment.searchFragment.set_tag_indicator(SearchFrag.HIDE_INDICATOR);

                    } else
                        deckFragment.searchFragment.load_tags_beginning_with(tag.replace(" ", "")); // afficher tous les tags possibles


                    // - suggestion : si tags deja selectionne en base, afficher les tags possibles en fonction de la query et des tags
                else
                    deckFragment.searchFragment.display_Related_Tags_beginning_with(tag.replace(" ", ""));
            }

            private void submit_Tag(String tag) {

                //Message.message(getApplicationContext(), "submit");

                // a ete desactive
                /*

                if(deckFragment.searchFragment.tagsSelected_NC.contains(tag)) {

                    //Message.message(MainActivity.context, "tag is already selected");

                    MainActivity.searchBar.setQuery(tag, false);
                }

                else if(deckFragment.searchFragment.tagsSelected_NC.isEmpty() && !deckFragment.searchFragment.firstPossibleTags.contains(tag)) {

                    MainActivity.searchBar.setQuery(tag, false);
                }

                else if(!deckFragment.searchFragment.tagsSelected_NC.isEmpty() && !deckFragment.searchFragment.firstPossibleTags.contains(tag)) {


                    MainActivity.searchBar.setQuery(tag, false);
                }

                else {

                    Message.message(getApplicationContext(), "activer le submit");

                    //Tags.setCardView_Click(Tags.ADD_TAG_TO_HEADER_FROM_SEARCH, null, tag);
                }
                */

            }


            @Override
            public boolean onQueryTextSubmit(String tag) {

                //if(!tag.isEmpty() && !tag.equals(" ")) submit_Tag(tag);

                return false;

            }

            @Override
            public boolean onQueryTextChange(String tag) {

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

                return false;
            }

        });

    }


    public void set_SearchBarTo(int state) {

        if (state == SEARCH_VISIBLE) {

            action_rankResult.setVisible(false);

            action_search_tag.setVisible(true);


            ((MainActivity) context).getSupportActionBar().setDisplayHomeAsUpEnabled(true);

            //searchBar.setBackgroundColor(ContextCompat.getColor(context, R.color.colorPrimaryDark));

            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN); // TODO A VOIR


        }

        if (state == SEARCH_HIDDEN) {

            if (deckFragment.isSearchResult || deckFragment.isSubscription) action_rankResult.setVisible(true);

            else action_rankResult.setVisible(false);

            action_search_tag.setVisible(false);

            //((MainActivity) context).getSupportActionBar().setDisplayHomeAsUpEnabled(false);

            //searchBar.setBackgroundColor(ContextCompat.getColor(context, R.color.colorPrimary));

            searchBar.clearFocus();

            //getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        }

        SEARCH_BAR_STATE = state;

    }


    public static void setSearchBar_hint() {

        /*

        if(MainPage.tagsSelected_NC.isEmpty()) {

            if(MainPage.authorsSelected_NC.size() == 1) searchBar.setQueryHint("Select a tag");

            if(MainPage.authorsSelected_NC.size() > 1) searchBar.setQueryHint("Select some related tags");

            if(MainPage.authorsSelected_NC.isEmpty()) searchBar.setQueryHint("Type or select a tag");

        }

        else if(SuggestionTags.tagsListSuggestion_map.isEmpty()) searchBar.setQueryHint("No more tag to select");

        else searchBar.setQueryHint("Select some related tags");

        */

    }




    private void init_Font(Context context) {

        tf = Typeface.createFromAsset(context.getAssets(), "fonts/OpenSans-Regular.ttf");

        tfSemiBold = Typeface.createFromAsset(context.getAssets(), "fonts/OpenSans-Semibold.ttf");

        tfSemiBoldItalic = Typeface.createFromAsset(context.getAssets(), "fonts/OpenSans-SemiboldItalic.ttf");

        tfBold = Typeface.createFromAsset(context.getAssets(), "fonts/OpenSans-Bold.ttf");

        tf_serif = Typeface.createFromAsset(context.getAssets(), "fonts/Andada-Regular.ttf");

    }

    private void hide_sidenav_btn() {

        navigationSelector.mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);

        navigationSelector.drawerToggle.setDrawerIndicatorEnabled(false);

    }

    private void show_sidenav_btn() {

        navigationSelector.mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);

        navigationSelector.drawerToggle.setDrawerIndicatorEnabled(true);

    }

    private void init_toolbar_click(int position) {

        if(position == 0) {

            //toolbar.setNavigationIcon(ContextCompat.getDrawable(context, android.R.drawable.ic_menu_manage));

            show_sidenav_btn();


            toolbar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    // reload

                    action_rankResult.setIcon(R.drawable.ic_rank_recent);

                    action_rankResult.setVisible(false);

                    deckFragment.searchFragment.remove_TagFromHeader(null, null);

                    deckFragment.searchFragment.reset_SearchEngine();


                    deckFragment.cardStackAdapter.offset = 0;

                    deckFragment.reload_home.setVisibility(View.GONE);

                    deckFragment.card_stack_view.removeAllViews();

                    deckFragment.cardDataset.clear();

                    deckFragment.reset_toGlobalDeck();

                }
            });

        } else if(position == 1 || position == 2) {

            hide_sidenav_btn();

            toolbar.setOnClickListener(null);

        } else if(position == 3) {

            hide_sidenav_btn();

            toolbar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    //setMenuAndFabVisibility(SLIDE_IN);
                    //profilFragment.hideshow_profil_description();

                    // todo show editor
                    getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING);

                    new_content_panel_structure.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);

                    slide_MenuAndFab(MainActivity.SLIDE_OUT);

                    Fragment fragment = getSupportFragmentManager().findFragmentByTag(NEW_CONTENT_EDITOR_TAG);
                    fragment.getView().findViewById(R.id.new_content_editor).setVisibility(View.GONE);
                    fragment.getView().findViewById(R.id.bio_editor).setVisibility(View.GONE);
                    fragment.getView().findViewById(R.id.username_editor).setVisibility(View.VISIBLE);

                }
            });

        }

    }

    private void init_viewPager() {

        viewPager.setOffscreenPageLimit(3);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

                setTitle(tabTitle[position]);

                tabLayout.getTabAt(0).getIcon().setAlpha(80);
                tabLayout.getTabAt(1).getIcon().setAlpha(80); //
                tabLayout.getTabAt(2).getIcon().setAlpha(80);
                tabLayout.getTabAt(3).getIcon().setAlpha(80); //

                tabLayout.getTabAt(position).getIcon().setAlpha(255);



                if (position == 0) {
                    // click sur DECK

                    if (deckFragment.isSearchResult || deckFragment.isSubscription) action_rankResult.setVisible(true);

                    action_searchButton.setVisible(true);
                    action_story_button.setVisible(false);
                    action_logout.setVisible(false);

                    init_toolbar_click(0);


                } else if (position == 1) {
                    // click sur CONVERSATION
                    if (push_page.equals("")) {
                        action_rankResult.setVisible(false);
                        action_searchButton.setVisible(false);
                        action_story_button.setVisible(false);
                        action_logout.setVisible(false);
                    }

                    if ((((MainActivity) MainActivity.context).convFragment.chat_viewpager != null) &&

                            ((MainActivity) MainActivity.context).convFragment.chat_viewpager.getCurrentItem() == 0)

                        new_notification_chat_has_been_seen = true;

                    init_toolbar_click(1);

                } else if (position == 2) {
                    // click sur STORIES
                    action_rankResult.setVisible(false);
                    action_searchButton.setVisible(false);
                    action_story_button.setVisible(true);
                    action_logout.setVisible(false);

                    init_toolbar_click(2);

                } else if (position == 3) {
                    // click sur PROFIL
                    if(push_page.equals("")) {
                        action_rankResult.setVisible(false);
                        action_searchButton.setVisible(false);
                        action_story_button.setVisible(false);
                        action_logout.setVisible(true);
                    }


                    if( (((MainActivity)MainActivity.context).profilFragment.profil_viewpager != null) &&

                            ((MainActivity)MainActivity.context).profilFragment.profil_viewpager.getCurrentItem() == 2)

                        new_notification_has_been_seen = true;

                    init_toolbar_click(3);


                } else toolbar.setOnClickListener(null);

                change_tab_profil_icon(new_notification_has_been_seen);
                change_tab_chat_icon(new_notification_chat_has_been_seen);

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }

        });

        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());

        // deck
        Bundle bundle = new Bundle();
        bundle.putInt(DeckFrag.KEY_DECK_TYPE, CardStackAdapter.GLOBAL_DECK);
        deckFragment = new DeckFrag();
        deckFragment.setArguments(bundle);

        // conversations
        convFragment = new ConvFrag();

        // stories
        Bundle bundle2 = new Bundle();
        bundle2.putInt(ChatOFrag.KEY_TYPE, ChatOFrag.STORIES_O);
        storyFragment = new ChatOFrag();
        storyFragment.setArguments(bundle2);

        // profil
        Bundle bundle3 = new Bundle();
        bundle3.putBoolean(ProfilFrag.KEY_IS_MAIN_USER, true);
        profilFragment = new ProfilFrag();
        profilFragment.setArguments(bundle3);


        adapter.addFragment(deckFragment, tabTitle[0]);
        adapter.addFragment(convFragment, tabTitle[1]);
        adapter.addFragment(storyFragment, tabTitle[2]);
        adapter.addFragment(profilFragment, tabTitle[3]);
        adapter.addFragment(new Fragment(), "");

        viewPager.setAdapter(adapter);

    }


    private void init_tabLayout() {

        tabLayout.setupWithViewPager(viewPager);

        tabLayout.setOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(viewPager) {
            @Override
            public void onTabSelected(Tab tab) {

                //Message.message(context, "> " + tab.getPosition());

                if (tab.getPosition() != 4) super.onTabSelected(tab);

                if(tab.getPosition() != 2 && storyFragment.stories_selector_is_expanded) storyFragment.hideshow_stories_selector();

            }

            @Override
            public void onTabUnselected(Tab tab) {


            }

            @Override
            public void onTabReselected(Tab tab) {

                if (tab.getPosition() == 0) navigationSelector.mDrawerLayout.openDrawer(Gravity.LEFT);

                else if (tab.getPosition() == 1) {
                    //convFragment.notification_recycler.smoothScrollToPosition(0); //notifFragment.notification_recycler.smoothScrollToPosition(0);

                } else if (tab.getPosition() == 2) storyFragment.hideshow_stories_selector();

                else if (tab.getPosition() == 3) profilFragment.hideshow_profil_description();

            }
        });

        tabLayout.getTabAt(0).setIcon(tabIcons[0]);
        tabLayout.getTabAt(1).setIcon(tabIcons[1]);
        tabLayout.getTabAt(2).setIcon(tabIcons[2]);
        tabLayout.getTabAt(3).setIcon(tabIcons[3]);

        tabLayout.getTabAt(1).getIcon().setAlpha(80);
        tabLayout.getTabAt(2).getIcon().setAlpha(80);
        tabLayout.getTabAt(3).getIcon().setAlpha(80);

    }

    private boolean new_notification_has_been_seen = true;
    public boolean new_notification_chat_has_been_seen = true;

    public void change_tab_profil_icon(boolean alert) {

        int tab_profil = 3;

        new_notification_has_been_seen = alert;

        if(!new_notification_has_been_seen) {

            ((MainActivity)MainActivity.context).profilFragment.profil_viewpager.setCurrentItem(2, true);

            ((MainActivity)MainActivity.context).profilFragment.profil_tablayout.setScrollPosition(2, 0f, true);

            tabLayout.getTabAt(tab_profil).getIcon().setAlpha(255);

            if(viewPager.getCurrentItem() == tab_profil) {

                tabLayout.getTabAt(tab_profil).setIcon(R.drawable.ic_tab_profil_alert); //ic_tab_notification_alert

                new_notification_has_been_seen = true;

            } else tabLayout.getTabAt(tab_profil).setIcon(R.drawable.ic_tab_profil_alert_alpha);

        } else {

            tabLayout.getTabAt(tab_profil).setIcon(tabIcons[tab_profil]);

            if(viewPager.getCurrentItem() == tab_profil) tabLayout.getTabAt(tab_profil).getIcon().setAlpha(255);

            else tabLayout.getTabAt(tab_profil).getIcon().setAlpha(80);

        }
    }


    public void change_tab_chat_icon(boolean alert) {

        int tab_chat = 1;

        new_notification_chat_has_been_seen = alert;

        if(!new_notification_chat_has_been_seen) {

            ((MainActivity)MainActivity.context).convFragment.chat_viewpager.setCurrentItem(0, true);

            ((MainActivity)MainActivity.context).convFragment.chat_tablayout.setScrollPosition(0, 0f, true);

            tabLayout.getTabAt(tab_chat).getIcon().setAlpha(255);

            if(viewPager.getCurrentItem() == tab_chat) {

                tabLayout.getTabAt(tab_chat).setIcon(R.drawable.ic_tab_conversation_alert);

                new_notification_chat_has_been_seen = true;

            } else tabLayout.getTabAt(tab_chat).setIcon(R.drawable.ic_tab_conversation_alpha);

        } else {

            tabLayout.getTabAt(tab_chat).setIcon(tabIcons[tab_chat]);

            if(viewPager.getCurrentItem() == tab_chat) tabLayout.getTabAt(tab_chat).getIcon().setAlpha(255);

            else tabLayout.getTabAt(tab_chat).getIcon().setAlpha(80);


        }
    }




    private void init_fab() {

        fab.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                navigationSelector.mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);

                getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

                //action_rankResult.setVisible(false);
                //action_searchButton.setVisible(false);

                //action_logout.setVisible(false);

                //contentEditor.showEditorPanelAndMenu(ContentEditor.EDITOR_TYPE_NEW);

                //FragmentManager manager = ((MainActivity) MainActivity.context).getSupportFragmentManager();

                //FragmentTransaction transaction = manager.beginTransaction();

                //transaction.show(newContent_editor); //transaction.show(resultPage);


                Fragment fragment = getSupportFragmentManager().findFragmentByTag(NEW_CONTENT_EDITOR_TAG);

                fragment.getView().findViewById(R.id.new_content_editor).setVisibility(View.VISIBLE);
                fragment.getView().findViewById(R.id.username_editor).setVisibility(View.GONE);
                fragment.getView().findViewById(R.id.bio_editor).setVisibility(View.GONE);

                //transaction.commit();


                new_content_panel_structure.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);

                //toolbar.setVisibility(View.GONE);

                //toolbar.setNavigationIcon(ContextCompat.getDrawable(context, R.drawable.ic_arrow_left_white_18dp));

                //setTitle("Share something");

                slide_MenuAndFab(SLIDE_OUT);

                /*
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                */
            }
        });
    }


    public static final String NEW_CONTENT_EDITOR_TAG = "new_content_editor";

    private void init_new_content_panel() {

        FragmentManager manager;

        android.support.v4.app.FragmentTransaction transaction;

        newContent_editor = new NewContent_Editor();

        newContent_editor.setHint_tags(getResources().getString(R.string.et_hint_tags));
        //newContent_editor.setHint_tags("Enter 1 to 7 tags (required)");
        newContent_editor.setHint_content(getResources().getString(R.string.et_hint_share_something));
        newContent_editor.setTagsArrayList(new ArrayList<String>());

        manager = ((MainActivity) context).getSupportFragmentManager();
        transaction = manager.beginTransaction();
        transaction.add(R.id.panel_editor_container_main, newContent_editor, NEW_CONTENT_EDITOR_TAG);

        transaction.commit();


        new_content_panel_structure.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                if (!searchPageActive) {

                    hideSoftKeyboard(view);

                    onBackPressed();

                }

                return false;
            }
        });

        new_content_panel_structure.setPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {

            @Override
            public void onPanelSlide(View panel, float slideOffset) {

            }

            @Override
            public void onPanelCollapsed(View panel) {

                newContent_editor.et_Tags.clearFocus();
                newContent_editor.et_Content.clearFocus();

            }

            @Override
            public void onPanelExpanded(View panel) {

            }

            @Override
            public void onPanelAnchored(View panel) {

            }

            @Override
            public void onPanelHidden(View panel) {

            }
        });

        new_content_panel_structure.setTouchEnabled(false); //todo 3.3.0
    }


    // bottom menu slider
    public void setMenuAndFabVisibility(int state) {

        if (state == HIDE_BOTTOM_MENU) {

            slide_MenuAndFab(SLIDE_OUT);

        }

        if (state == SHOW_BOTTOM_MENU) {

            Runnable myRunnable2 = new Runnable() {
                @Override
                public void run() {

                    slide_MenuAndFab(SLIDE_IN);
                }
            };

            Handler myHandler2 = new Handler();

            myHandler2.postDelayed(myRunnable2, 150);

        }

    }


    // bottom menu slider
    public void slide_MenuAndFab(int direction) {

        if (direction == SLIDE_IN) {

            // slide out du bouton
            fab
                    .animate()
                    .translationY(0)
                    .setInterpolator(new AccelerateInterpolator(1.5f))
                    .setInterpolator(new DecelerateInterpolator(1.5f))
                    .start();

            // slide out du bottom menu
            tabLayout
                    .animate()
                    .translationY(0)
                    .setInterpolator(new AccelerateInterpolator(1.5f))
                    .setInterpolator(new DecelerateInterpolator(1.5f))
                    .start();

        } else if (direction == SLIDE_OUT) {


            // slide out du bouton
            fab
                    .animate()
                    .translationY(tabLayout.getHeight() * 2)
                    .setInterpolator(new AccelerateInterpolator(1.5f))
                    .setInterpolator(new DecelerateInterpolator(1.5f))
                    .start();

            // slide out du bottom menu
            tabLayout
                    .animate()
                    .translationY(tabLayout.getHeight())
                    .setInterpolator(new AccelerateInterpolator(1.5f))
                    .setInterpolator(new DecelerateInterpolator(1.5f))
                    .start();
        }

    }


    // bottom menu scroller
    public void onScrollHideShowBottomMenu(int dy) {

        // ADDONS pour masquer les bottoms tabs

        if (scrolledDistance > HIDE_THRESHOLD && controlsVisible) {

            setMenuAndFabVisibility(SLIDE_OUT);

            controlsVisible = false;
            scrolledDistance = 0;

        } else if (scrolledDistance < -HIDE_THRESHOLD && !controlsVisible) {

            setMenuAndFabVisibility(SLIDE_IN);

            controlsVisible = true;
            scrolledDistance = 0;
        }

        if ((controlsVisible && dy > 0) || (!controlsVisible && dy < 0)) {
            scrolledDistance += dy;
        }
    }


    public void hideEditor_ShowMenu() {

        new_content_panel_structure.setPanelState(SlidingUpPanelLayout.PanelState.HIDDEN);

        /*
        toolbar.setNavigationIcon(null);

        setTitle(tabTitle[tabLayout.getSelectedTabPosition()]);

        if (tabLayout.getSelectedTabPosition() == 0) {

            action_searchButton.setVisible(true);

            if (deckFragment.isSearchResult) action_rankResult.setVisible(true);
        } else if (tabLayout.getSelectedTabPosition() == 2) action_logout.setVisible(true);
        */

        slide_MenuAndFab(SLIDE_IN);

    }


    public void hideSoftKeyboard() {
        if (getCurrentFocus() != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }


    public static void hideSoftKeyboard(View view) {

        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);

        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

    }






    // from top left button
    public void closeEditorPanel() {

        // Check if no view has focus:
        View view = this.getCurrentFocus();

        if (view != null) hideSoftKeyboard(view);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {

            @Override
            public void run() {
                onBackPressed();
            }

        }, 200); // 5000ms delay

    }


    private void logout() {

        new MaterialDialog.Builder(this)
                .title(getResources().getString(R.string.dialog_logout))
                .positiveText(getResources().getString(R.string.dialog_logout_yes))
                .negativeText(getResources().getString(R.string.dialog_logout_no))
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                        localData.setUserConnectedStatus(false);
                        localData.storeUserData(new ParseUser());

                        setMenuAndFabVisibility(SLIDE_IN);

                        deckFragment.card_stack_view.removeAllViews();
                        deckFragment.empty(context);
                        //convFragment.empty(context); //notifFragment.empty(context); TODO CONVERSATION
                        profilFragment.empty(context);

                        OnBoardingActivity.launch_fromMain(context);

                        ParseUser.logOut();

                    }
                })
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        // Nothing
                    }
                })
                /*
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {

                        ParseRequest.logout();

                        localData.clearUserData();

                        localData.setUserConnectedStatus(false);

                        thoughtEditor.thoughtEditorFragment.setLogin_Invit_visibility();

                        toolbar.setSubtitle("");

                        ProfilPage.setLoginInvit_visibility();

                        NotificationsPage.setLoginInvit_visibility();

                        NotificationsPage.cleanRecycler();

                        PersonalThoughts.cleanRecycler();

                        CuratedThoughts.cleanRecycler();


                    }

                    @Override
                    public void onNegative(MaterialDialog dialog) {
                    }
                })
                */
                .show();

    }


    class ViewPagerAdapter extends FragmentPagerAdapter {

        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return null;
            //return mFragmentTitleList.get(position);
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }
    }
}