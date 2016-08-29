package co.forum.app.MainSections.ProfilPages;


import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import co.forum.app.Adapters.ChatO_1user_Adapter;
import co.forum.app.Adapters.ChatO_2user_Adapter;
import co.forum.app.Adapters.ChatO_Story_Adapter;
import co.forum.app.Data.ChatOverviewData;
import co.forum.app.Data.ParseRequest;
import co.forum.app.MainActivity;
import co.forum.app.MainSections.ProfilFrag;
import co.forum.app.R;
import co.forum.app.SharedPref;
import co.forum.app.tools.Message;
import co.forum.app.tools.TagView;

public class ChatOFrag extends Fragment implements SwipeRefreshLayout.OnRefreshListener{

    public static final String KEY_TYPE = "chat_type";

    public static final int CONV_U_CHAT = 0; // section conversation, tab user chat
    public static final int CONV_O_CHAT = 1; // section conversation, tab other chat

    public static final int STORIES_O = 2; // section stories

    public static final int PROFIL_U_STORIES = 3; // section main profil, tab stories

    public static final int PROFIL_O_CHAT = 4; // section other profil, tab chat
    public static final int PROFIL_O_STORIES = 5; // section other profil, tab stories

    private int chat_type;

    @Bind(R.id.generic_recycler_view) RecyclerView chatRowRecycler;
    @Bind(R.id.refreshNotificationRecycler) SwipeRefreshLayout refreshRecycler;

    @Nullable @Bind(R.id.empty_chat_indicator) LinearLayout empty_chat_indicator;
    @Nullable @Bind(R.id.empty_user_chat_indicator) LinearLayout empty_user_chat_indicator;
    @Nullable @Bind(R.id.empty_story_indicator) LinearLayout empty_story_indicator;
    @Nullable @Bind(R.id.empty_user_story_indicator) LinearLayout empty_user_story_indicator;

    private String UserID;
    private String UserName;
    private String MainUserID;

    private ChatO_1user_Adapter chat_1userAdapter;
    private ChatO_2user_Adapter chat_2userAdapter;
    private ChatO_Story_Adapter chat_StoryAdapter;

    private List<ChatOverviewData> rowList;


    public int pageOffset = 0;
    public int previousTotal = 0;
    private int visibleThreshold = 1;
    private boolean loading = true;
    int firstVisibleItem, visibleItemCount, totalItemCount;

    public ChatOFrag() {
        // Required empty public constructor
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        chat_type = getArguments().getInt(KEY_TYPE, CONV_U_CHAT);

        MainUserID = ((MainActivity) MainActivity.context).localData.getUserData(SharedPref.KEY_USER_ID);

        if(chat_type == PROFIL_O_CHAT || chat_type == PROFIL_O_STORIES) {

            UserID = getArguments().getString(ProfilFrag.KEY_USER_ID, "NULL");

            UserName = getArguments().getString(ProfilFrag.KEY_USER_NAME, "NULL");

        } else UserID = MainUserID;

        if(chat_type == STORIES_O) {

            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN)

                marginTop = getStatusBarHeight();

            else marginTop = 0;
        }

        rowList = new ArrayList<>();

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View layout;

        if(chat_type == STORIES_O) {

            layout = inflater.inflate(R.layout.section_stories, container, false);

            stories_appbar = (AppBarLayout) layout.findViewById(R.id.stories_appbar);
            toolbarLayout = (CollapsingToolbarLayout) layout.findViewById(R.id.collapseToolbar);
            story_tag_panel = (LinearLayout) layout.findViewById(R.id.story_tag_panel);

            line1 = (LinearLayout) layout.findViewById(R.id.line1);
            line2 = (LinearLayout) layout.findViewById(R.id.line2);
            line3 = (LinearLayout) layout.findViewById(R.id.line3);
            line4 = (LinearLayout) layout.findViewById(R.id.line4);
            line5 = (LinearLayout) layout.findViewById(R.id.line5);

            // margin bug
            AppBarLayout.LayoutParams params = new AppBarLayout.LayoutParams(
                    AppBarLayout.LayoutParams.WRAP_CONTENT,
                    AppBarLayout.LayoutParams.WRAP_CONTENT
            );
            params.setMargins(0, -marginTop, 0, 0);
            toolbarLayout.setLayoutParams(params);

            ViewTreeObserver vto = story_tag_panel.getViewTreeObserver();
            vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    story_tag_panel.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                    int height = story_tag_panel.getMeasuredHeight();

                    story_tag_panel.setMinimumHeight(height + marginTop);


                    //stories_appbar.setExpanded(false);

                    //hideshow_stories_selector();

                    stories_selector_is_expanded = false;
                    stories_appbar.setExpanded(false, false);
                }
            });


            display_Tags_Suggestion(1, getActivity().getResources().getStringArray(R.array.tags_suggestion11));
            display_Tags_Suggestion(2, getActivity().getResources().getStringArray(R.array.tags_suggestion22));
            display_Tags_Suggestion(3, getActivity().getResources().getStringArray(R.array.tags_suggestion33));
            display_Tags_Suggestion(4, getActivity().getResources().getStringArray(R.array.tags_suggestion44));
            display_Tags_Suggestion(5, getActivity().getResources().getStringArray(R.array.tags_suggestion55));

            init_header_parallax();

        } else layout = inflater.inflate(R.layout.generic_recycler_view, container, false);

        ButterKnife.bind(this, layout);

        refreshRecycler.setOnRefreshListener(this);

        get_Data(UserID, chat_type, 0);

        return layout;
    }

    @Override
    public void onRefresh() {

        if(UserID.equals(MainUserID)) refresh();

        if(chat_type == STORIES_O) stories_appbar.setExpanded(false, true);

        refreshRecycler.setRefreshing(false);
    }




    private void get_Data(String UserID, int chat_type, int pageOffset) {

        if(chat_type == PROFIL_U_STORIES) ParseRequest.get_UserStories(getContext(), UserID, chat_type, pageOffset); //MainUserID

        else if(chat_type == CONV_U_CHAT) ParseRequest.get_UserChats(getContext(), UserID, chat_type, pageOffset); //MainUserID

        else if(chat_type == CONV_O_CHAT) ParseRequest.get_CommunityChats(getContext(), UserID, chat_type, pageOffset); //MainUserID

        else if(chat_type == STORIES_O) ParseRequest.get_CommunityStories(getContext(), UserID, chat_type, topic, pageOffset); //MainUserID

        else if(chat_type == PROFIL_O_CHAT) ParseRequest.get_UserChats(getContext(), UserID, chat_type, pageOffset);

        else if(chat_type == PROFIL_O_STORIES) ParseRequest.get_UserStories(getContext(), UserID, chat_type, pageOffset);

    }


    public void display_rows(Context context, ArrayList<ChatOverviewData> rowList, int chat_type, int offset){

        if (offset == 0) display_firstRows(context, rowList, chat_type);

        else {

            this.rowList.addAll(rowList);

            if(chat_1userAdapter == null) {

                // bug...

                //if(isComments) Message.message(context, "Comments tab (no adapter), offset : " + offset);

                //else Message.message(context, "Cards tab (no adapter), offset : " + offset);

            } else chat_1userAdapter.notifyDataSetChanged();
        }
    }


    private void display_firstRows(final Context context, final ArrayList<ChatOverviewData> rowList, final int chat_type) {

        this.rowList = rowList;

        hideOrShowEmptyIndicator();

        if(chat_type == CONV_U_CHAT || chat_type == PROFIL_O_CHAT) {

            chat_1userAdapter = new ChatO_1user_Adapter(getActivity(), rowList, UserID, UserName);

            chatRowRecycler.setAdapter(chat_1userAdapter);

        } else if(chat_type == PROFIL_U_STORIES || chat_type == PROFIL_O_STORIES || chat_type == STORIES_O) {

            chat_StoryAdapter = new ChatO_Story_Adapter(getActivity(), rowList, UserID, UserName, chat_type);

            chatRowRecycler.setAdapter(chat_StoryAdapter);


        } else if (chat_type == CONV_O_CHAT) {

            chat_2userAdapter = new ChatO_2user_Adapter(getActivity(), rowList);

            chatRowRecycler.setAdapter(chat_2userAdapter);

        }



        final LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());

        chatRowRecycler.setLayoutManager(mLayoutManager);

        chatRowRecycler.addOnScrollListener(new RecyclerView.OnScrollListener() {

            private void loadMore() {

                visibleItemCount = chatRowRecycler.getChildCount();
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
                    get_Data(UserID, chat_type, pageOffset);


                    loading = true;
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {

                super.onScrolled(recyclerView, dx, dy);

                if (UserID.equals(MainUserID))
                    ((MainActivity) getContext()).onScrollHideShowBottomMenu(dy);

                loadMore();

            }
        });
    }


    private void hideOrShowEmptyIndicator() {

        if(rowList.size() == 0) {

            if(chat_type == CONV_U_CHAT) empty_chat_indicator.setVisibility(View.VISIBLE);

            else if(chat_type == PROFIL_U_STORIES) empty_story_indicator.setVisibility(View.VISIBLE);

            else if(chat_type == PROFIL_O_CHAT) empty_user_chat_indicator.setVisibility(View.VISIBLE);

            else if(chat_type == PROFIL_O_STORIES) empty_user_story_indicator.setVisibility(View.VISIBLE);

            //if(UserID.equals(MainUserID)) empty_chat_indicator.setVisibility(View.VISIBLE);

            //else empty_user_chat_indicator.setVisibility(View.VISIBLE);

        } else if(chat_type != STORIES_O) {

            empty_chat_indicator.setVisibility(View.GONE);

            empty_user_chat_indicator.setVisibility(View.GONE);

            empty_story_indicator.setVisibility(View.GONE);

            empty_user_story_indicator.setVisibility(View.GONE);
        }
    }


    public void refresh() {

        empty(getContext());
        reload(((MainActivity) MainActivity.context).localData.getUserData(SharedPref.KEY_USER_ID));
    }


    private void empty(Context context) {

        if(chat_type == STORIES_O)  topic = ALL_STORIES;

        display_rows(context, new ArrayList<ChatOverviewData>(), chat_type, 0);

        if(chat_type != STORIES_O) hideIndicator();

    }


    private void reload(String newUserID) {

        pageOffset = 0;
        previousTotal = 0;

        get_Data(newUserID, chat_type, pageOffset);

    }


    public void load_Stories(String topic) {

        pageOffset = 0;
        previousTotal = 0;

        this.topic = topic;

        ParseRequest.get_CommunityStories(getContext(), UserID, chat_type, topic, pageOffset);

    }


    private void hideIndicator(){

        empty_chat_indicator.setVisibility(View.GONE);
        empty_user_chat_indicator.setVisibility(View.GONE);
    }



    // STORIES TAB ONLY -----------------------------

    public static final String ALL_STORIES = "ALL-STORIES";

    int marginTop;
    CollapsingToolbarLayout toolbarLayout; // pour Stories

    LinearLayout line1;
    LinearLayout line2;
    LinearLayout line3;
    LinearLayout line4;
    LinearLayout line5;

    public String topic = ALL_STORIES;

    public AppBarLayout stories_appbar;
    LinearLayout story_tag_panel;
    public boolean stories_selector_is_expanded;

    private int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) result = getResources().getDimensionPixelSize(resourceId);
        return result;
    }


    private void init_header_parallax() {

        stories_appbar.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {

            float delta;

            private void set_Panel_AlphaScale(int verticalOffset, AppBarLayout appBarLayout) {

                delta = ((float) appBarLayout.getTotalScrollRange() - Math.abs((float) verticalOffset)) / (float) appBarLayout.getTotalScrollRange();
                toolbarLayout.setAlpha(delta);
                story_tag_panel.setScaleX(0.02F * delta + 0.98F);
                story_tag_panel.setScaleY(0.02F * delta + 0.98F);

            }

            private void set_Panel_State(int verticalOffset, AppBarLayout profil_appbar) {

                if ((Math.abs(verticalOffset) > 0) && (Math.abs(verticalOffset) < profil_appbar.getTotalScrollRange() / 2)) {

                    stories_selector_is_expanded = true;

                    //if(MainActivity.action_story_button != null) MainActivity.action_story_button.setIcon(R.drawable.ic_menu_up_white_36dp);

                } else if ((Math.abs(verticalOffset) < profil_appbar.getTotalScrollRange()) && (Math.abs(verticalOffset) > profil_appbar.getTotalScrollRange() / 2)) {

                    stories_selector_is_expanded = false;

                    //if(MainActivity.action_story_button != null) MainActivity.action_story_button.setIcon(R.drawable.ic_menu_down_white_36dp);

                } else if (Math.abs(verticalOffset) == 0) {

                    stories_selector_is_expanded = true;

                    //if(MainActivity.action_story_button != null) MainActivity.action_story_button.setIcon(R.drawable.ic_menu_up_white_36dp);

                } else if (Math.abs(verticalOffset) != profil_appbar.getTotalScrollRange()) {

                    stories_selector_is_expanded = false;

                    //if(MainActivity.action_story_button != null) MainActivity.action_story_button.setIcon(R.drawable.ic_menu_down_white_36dp);

                }

            }

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {

                set_Panel_State(verticalOffset, appBarLayout);
                set_Panel_AlphaScale(verticalOffset, appBarLayout);

            }
        });
    }


    public void hideshow_stories_selector(){

        if (stories_selector_is_expanded) {

            //MainActivity.action_story_button.setIcon(R.drawable.ic_menu_down_white_36dp);
            stories_appbar.setExpanded(false, true);

        } else {

            //MainActivity.action_story_button.setIcon(R.drawable.ic_menu_up_white_36dp);
            stories_appbar.setExpanded(true, true);
        }
    }


    private void display_Tags_Suggestion(int bundle, String[] TagsList) {

        switch (bundle) {

            case 1:
                for (final String tag : TagsList) line1.addView(new TagView(MainActivity.context, 11, tag));
                break;

            case 2:
                for (final String tag : TagsList) line2.addView(new TagView(MainActivity.context, 22, tag));
                break;

            case 3:
                for (final String tag : TagsList) line3.addView(new TagView(MainActivity.context, 33, tag));
                break;

            case 4:
                for (final String tag : TagsList) line4.addView(new TagView(MainActivity.context, 44, tag));
                break;

            case 5:
                for (final String tag : TagsList) line5.addView(new TagView(MainActivity.context, 55, tag));
                break;
        }
    }


}
