package co.forum.app.MainSections;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.AppBarLayout.OnOffsetChangedListener;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.TabLayout;
import android.support.design.widget.TabLayout.Tab;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v7.widget.CardView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;
import co.forum.app.Data.ParseRequest;
import co.forum.app.MainActivity;
import co.forum.app.MainSections.ProfilPages.ProfilCardFrag;
import co.forum.app.MainSections.ProfilPages.ChatOFrag;
import co.forum.app.R;
import co.forum.app.SharedPref;
import co.forum.app.SubActivity.SubActivity;
import co.forum.app.tools.Message;
import co.forum.app.tools.RowLayout;
import co.forum.app.tools.Tags;
import com.parse.ParseUser;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;
import java.util.ArrayList;
import java.util.List;

public class ProfilFrag extends Fragment {

    public static final String KEY_CURATED = "key_curated";
    public static final String KEY_IS_MAIN_USER = "key_is_mainuser";
    public static final String KEY_PROFIL_URL = "key_profil_url";
    public static final String KEY_USER_ID = "key_user_id";
    public static final String KEY_USER_NAME = "key_user_name";
    public static final int RESULT_LOAD_IMAGE = 10;

    public String UserID;
    public String UserName;
    public boolean curated_account;

    public boolean isMainUser;
    public ParseUser otherUser;

    public String pict_url;
    public String bio_url;

    public boolean profil_is_expanded = true;

    @Bind(R.id.profil_appbar) public AppBarLayout profil_appbar;
    @Bind(R.id.collapseToolbar) public CollapsingToolbarLayout collapseToolbar;
    @Bind(R.id.profil_panel) LinearLayout profil_panel;

    @Bind(R.id.btn_change_icon) public FrameLayout btn_change_icon;
    @Bind(R.id.icon_profil) public CircleImageView icon_profil;

    @Bind(R.id.btn_replies) LinearLayout btn_replies;
    @Bind(R.id.btn_upvotes) LinearLayout btn_upvotes;
    @Bind(R.id.user_deck) LinearLayout btn_user_deck;

    @Bind(R.id.tv_cards) TextView tv_cards;
    @Bind(R.id.tv_replies) TextView tv_replies;
    @Bind(R.id.tv_upvotes) TextView tv_upvotes;

    @Bind(R.id.tv_bio_text) public TextView tv_bio_text;
    @Bind(R.id.tv_bio_link) public TextView tv_bio_link;
    @Bind(R.id.header_tags_row) public RowLayout header_tags_row;

    @Bind(R.id.profil_tablayout) public TabLayout profil_tablayout;
    @Bind(R.id.profil_viewpager) public ViewPager profil_viewpager;

    ViewPagerAdapter adapter;

    public ProfilCardFrag userCardsTab;
    public ChatOFrag userStoriesTab;
    public ChatOFrag otherUserChatsTab;
    public NotifFrag notifFrag;


    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_profil, container, false);

        ButterKnife.bind(this, view);

        isMainUser = getArguments().getBoolean(KEY_IS_MAIN_USER, false);


        init_profil_data(isMainUser);
        init_profil_content(isMainUser);
        init_profil_btn_deck(isMainUser);
        init_profil_editors(isMainUser);
        init_profil_icon(isMainUser);


        init_header_parallax();
        init_viewPager();

        return view;
    }



    private void init_profil_data(boolean isMainUser) {

        if (isMainUser) {

            UserID = ((MainActivity)getContext()).localData.getUserData(SharedPref.KEY_USER_ID);
            UserName = ((MainActivity)getContext()).localData.getUserData(SharedPref.KEY_FULL_NAME);
            pict_url = ((MainActivity)getContext()).localData.getUserData(SharedPref.KEY_PICT_URL);
            bio_url = ((MainActivity)getContext()).localData.getUserData(SharedPref.KEY_BIO_URL);
            curated_account = ((MainActivity)getContext()).localData.getCurated();

        } else {

            UserID = getArguments().getString(KEY_USER_ID, "NULL");
            UserName = getArguments().getString(KEY_USER_NAME, "NONAME");
            pict_url = getArguments().getString(KEY_PROFIL_URL, "NULL");
            bio_url = getArguments().getString(KEY_PROFIL_URL, "");
            curated_account = getArguments().getBoolean(KEY_CURATED, false);
        }
    }


    private void init_profil_content(boolean isMainUser) {

        if (isMainUser) {

            if (((MainActivity)MainActivity.context).localData.userIsConnected())

                update_MainProfilHeader("all");

        } else ParseRequest.get_OtherUserProfil(getContext(), UserID);

        tv_bio_text.setTypeface(MainActivity.tf);
        tv_bio_link.setTypeface(MainActivity.tf);
    }


    private void init_profil_btn_deck(final boolean isMainUser) {

        btn_user_deck.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                if (isMainUser)
                    SubActivity.openUserDeck(
                        getContext(),
                        ((MainActivity)getContext()).localData.getUserData(SharedPref.KEY_FULL_NAME),
                        ((MainActivity)getContext()).localData.getUserData(SharedPref.KEY_USER_ID));

                else SubActivity.openUserDeck(getContext(), UserName, UserID);

            }
        });

        btn_upvotes.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                Message.message(MainActivity.context, getResources().getString(R.string.nb_upvotes_received));

            }
        });


        btn_replies.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                Message.message(MainActivity.context, getResources().getString(R.string.nb_replies_received));

            }
        });
    }


    private void init_profil_editors(boolean isMainUser) {

        if (isMainUser) {

            btn_change_icon.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent intent = Intent.createChooser(new Intent("android.intent.action.PICK", MediaStore.Images.Media.EXTERNAL_CONTENT_URI), getResources().getString(R.string.upload_profil_pict));
                    ((MainActivity)MainActivity.context).startActivityForResult(intent, RESULT_LOAD_IMAGE);
                }
            });

            tv_bio_text.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {

                    ((MainActivity)getContext()).new_content_panel_structure.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);
                    ((MainActivity)getContext()).slide_MenuAndFab(MainActivity.SLIDE_OUT);


                    Fragment editor = getFragmentManager().findFragmentByTag(MainActivity.NEW_CONTENT_EDITOR_TAG);
                    editor.getView().findViewById(R.id.username_editor).setVisibility(View.GONE); // edit username
                    editor.getView().findViewById(R.id.new_content_editor).setVisibility(View.GONE); // card editor
                    editor.getView().findViewById(R.id.bio_editor).setVisibility(View.VISIBLE); // edit bio

                }
            });
        }
    }


    private void init_header_parallax() {

        profil_appbar.addOnOffsetChangedListener(new OnOffsetChangedListener() {

            private void setProfil_Panel_AlphaScale(int verticalOffset, AppBarLayout appBarLayout) {

                float delta = ((float) appBarLayout.getTotalScrollRange() - Math.abs((float) verticalOffset)) / (float) appBarLayout.getTotalScrollRange();
                collapseToolbar.setAlpha(delta);
                profil_panel.setScaleX(0.02F * delta + 0.98F);
                profil_panel.setScaleY(0.02F * delta + 0.98F);

            }

            private void setProfil_Panel_State(int verticalOffset, AppBarLayout profil_appbar) {

                if ((Math.abs(verticalOffset) > 0) && (Math.abs(verticalOffset) < profil_appbar.getTotalScrollRange() / 2)) {

                    profil_is_expanded = true;

                } else if ((Math.abs(verticalOffset) < profil_appbar.getTotalScrollRange()) && (Math.abs(verticalOffset) > profil_appbar.getTotalScrollRange() / 2)) {

                    profil_is_expanded = false;

                } else if (Math.abs(verticalOffset) == 0) {

                    profil_is_expanded = true;

                } else if (Math.abs(verticalOffset) != profil_appbar.getTotalScrollRange()) {

                    profil_is_expanded = false;

                }

                /*

                if ((Math.abs(verticalOffset) > 0) && (Math.abs(verticalOffset) < profil_appbar.getTotalScrollRange() / 2)) {
                    profil_is_expanded = true;
                }
                do {
                    if ((Math.abs(verticalOffset) < profil_appbar.getTotalScrollRange()) && (Math.abs(verticalOffset) > profil_appbar.getTotalScrollRange() / 2)) {
                        profil_is_expanded = false;
                    }
                    if (Math.abs(verticalOffset) == 0) {
                        profil_is_expanded = true;
                        return;
                    }
                }
                while (Math.abs(verticalOffset) != profil_appbar.getTotalScrollRange());
                profil_is_expanded = false;

                */

            }

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {

                setProfil_Panel_State(verticalOffset, appBarLayout);
                setProfil_Panel_AlphaScale(verticalOffset, appBarLayout);

            }
        });
    }


    private void init_profil_icon(boolean isMainUser) {

        if (isMainUser) pict_url = ((MainActivity)getActivity()).localData.getUserData(SharedPref.KEY_PICT_URL);

        if (!pict_url.equals("NULL") && !pict_url.isEmpty()) Picasso.with(MainActivity.context).load(this.pict_url).into(this.icon_profil);

        else icon_profil.setImageDrawable(ContextCompat.getDrawable(MainActivity.context, R.drawable.profil_pict));

        if(curated_account) {

            icon_profil.setBorderColor(ContextCompat.getColor(MainActivity.context, R.color.curated));
            icon_profil.setBorderWidth(7);

        } else icon_profil.setBorderWidth(0);
    }




    private void init_viewPager() {

        // if (!isMainUser) header_tags_row.setVisibility(View.GONE); TODO ????

        adapter = new ViewPagerAdapter(getFragmentManager());

        if ((curated_account) && (!isMainUser)) {

            // curated_account

            Bundle bundle0 = new Bundle();
            bundle0.putBoolean("isBookmarks", false);
            bundle0.putBoolean("card_type", false);
            bundle0.putString(KEY_USER_ID, UserID);

            userCardsTab = new ProfilCardFrag();
            userCardsTab.setArguments(bundle0);
            adapter.addFragment(userCardsTab, "Quotes");

            profil_viewpager.addOnPageChangeListener(new OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                }

                @Override
                public void onPageSelected(int position) {

                }

                @Override
                public void onPageScrollStateChanged(int state) {

                }
            });

            profil_viewpager.setAdapter(adapter);
            profil_viewpager.setOffscreenPageLimit(1);
            profil_tablayout.setupWithViewPager(profil_viewpager);
            profil_tablayout.setVisibility(View.GONE);

        } else {

            if(isMainUser) {

                // POSTS
                Bundle bundle1 = new Bundle();
                bundle1.putBoolean("isBookmarks", false);
                bundle1.putBoolean("card_type", false);
                bundle1.putString(KEY_USER_ID, this.UserID);
                userCardsTab = new ProfilCardFrag();
                userCardsTab.setArguments(bundle1);

                // COMMENTS
                /*
                Bundle bundle2 = new Bundle();
                bundle2.putBoolean("isBookmarks", false);
                bundle2.putBoolean("card_type", true);
                bundle2.putString(KEY_USER_ID, UserID);
                userCommentsTab = new ProfilCardFrag();
                userCommentsTab.setArguments(bundle2);
                adapter.addFragment(userCommentsTab, "Comments");*/

                // STORIES
                Bundle bundle2 = new Bundle();
                bundle2.putInt(ChatOFrag.KEY_TYPE, ChatOFrag.PROFIL_U_STORIES);
                userStoriesTab = new ChatOFrag();
                userStoriesTab.setArguments(bundle2);

                // NOTIFICATIONS
                notifFrag = new NotifFrag();

                adapter.addFragment(userCardsTab, "Posts");
                adapter.addFragment(userStoriesTab, "Stories");
                adapter.addFragment(notifFrag, "Notifications");

                profil_viewpager.addOnPageChangeListener(new OnPageChangeListener() {

                    @Override
                    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                    }

                    @Override
                    public void onPageSelected(int position) {

                        profil_appbar.setExpanded(false, true);

                        //if (position == 2) ((MainActivity) MainActivity.context).new_notification_chat_has_been_seen = true;
                        // TODO a desactiver
                    }

                    @Override
                    public void onPageScrollStateChanged(int state) {

                    }

                });
                profil_viewpager.setAdapter(adapter);
                profil_viewpager.setOffscreenPageLimit(2);

                profil_tablayout.setupWithViewPager(profil_viewpager);
                profil_tablayout.setOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(profil_viewpager) {

                    @Override
                    public void onTabSelected(Tab tab) {
                        super.onTabSelected(tab);
                    }

                    @Override
                    public void onTabUnselected(Tab tab) {

                    }

                    @Override
                    public void onTabReselected(Tab tab) {

                        if (profil_is_expanded) profil_appbar.setExpanded(false, true);
                    }
                });

            } else {

                // posts
                Bundle bundle1 = new Bundle();
                bundle1.putBoolean("isBookmarks", false);
                bundle1.putBoolean("card_type", false);
                bundle1.putString(KEY_USER_ID, this.UserID);
                userCardsTab = new ProfilCardFrag();
                userCardsTab.setArguments(bundle1);

                // comments
                /*Bundle bundle2 = new Bundle();
                bundle2.putBoolean("isBookmarks", false);
                bundle2.putBoolean("card_type", true);
                bundle2.putString(KEY_USER_ID, UserID);
                userCommentsTab = new ProfilCardFrag();
                userCommentsTab.setArguments(bundle2);
                adapter.addFragment(userCommentsTab, "Comments");*/

                // chats
                Bundle bundle3 = new Bundle();
                bundle3.putString(KEY_USER_ID, UserID);
                bundle3.putString(KEY_USER_NAME, UserName);
                bundle3.putInt(ChatOFrag.KEY_TYPE, ChatOFrag.PROFIL_O_CHAT);
                otherUserChatsTab = new ChatOFrag();
                otherUserChatsTab.setArguments(bundle3);

                // stories
                Bundle bundle4 = new Bundle();
                bundle4.putString(KEY_USER_ID, UserID);
                //bundle4.putString(KEY_USER_NAME, UserName);
                bundle4.putInt(ChatOFrag.KEY_TYPE, ChatOFrag.PROFIL_O_STORIES);
                userStoriesTab = new ChatOFrag();
                userStoriesTab.setArguments(bundle4);


                adapter.addFragment(userCardsTab, "Posts");
                adapter.addFragment(otherUserChatsTab, "Chats");
                adapter.addFragment(userStoriesTab, "Stories");

                profil_viewpager.addOnPageChangeListener(new OnPageChangeListener() {

                    @Override
                    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                    }

                    @Override
                    public void onPageSelected(int position) {

                        profil_appbar.setExpanded(false, true);

                        if (position == 2) ((MainActivity) MainActivity.context).new_notification_chat_has_been_seen = true;
                    }

                    @Override
                    public void onPageScrollStateChanged(int state) {

                    }

                });
                profil_viewpager.setAdapter(adapter);
                profil_viewpager.setOffscreenPageLimit(2);

                profil_tablayout.setupWithViewPager(profil_viewpager);
                profil_tablayout.setOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(profil_viewpager) {

                    @Override
                    public void onTabSelected(Tab tab) {
                        super.onTabSelected(tab);
                    }

                    @Override
                    public void onTabUnselected(Tab tab) {

                    }

                    @Override
                    public void onTabReselected(Tab tab) {

                        if (profil_is_expanded) profil_appbar.setExpanded(false, true);
                    }
                });

            }

        }
    }


    public void empty(Context context) {

        profil_appbar.setExpanded(true);
        header_tags_row.setVisibility(View.GONE);

        userCardsTab.empty(context);
        //userCommentsTab.empty(context);
        //otherUserChatsTab.empty(context);
    }


    public void hideshow_profil_description(){

        if (profil_is_expanded) profil_appbar.setExpanded(false, true);

        else profil_appbar.setExpanded(true, true);
    }


    public void setupMainUserProfil(Context context){

        String newUserID = ((MainActivity) context).localData.getUserData(SharedPref.KEY_USER_ID);
        curated_account = ((MainActivity) context).localData.getCurated();

        update_MainProfilHeader("all");

        userCardsTab.reload(context, 2, newUserID);
        //userCommentsTab.reload(context, 3, newUserID);
        //otherUserChatsTab.reload(context, newUserID);
    }


    public void update_MainProfilHeader(String type) {

        if (type.equals("all")) {

            String bio = String.valueOf(((MainActivity)getActivity()).localData.getUserData(SharedPref.KEY_BIO));

            update_bio(bio);

            update_bio_url(String.valueOf(((MainActivity) getActivity()).localData.getUserData(SharedPref.KEY_BIO_URL)));

            init_profil_icon(isMainUser);

            tv_cards.setText(String.valueOf(((MainActivity) getActivity()).localData.getUserCount(SharedPref.KEY_CARDS_COUNT)));
            tv_upvotes.setText(String.valueOf(((MainActivity) getActivity()).localData.getUserCount(SharedPref.KEY_UPVOTES_COUNT)));
            tv_replies.setText(String.valueOf(((MainActivity) getActivity()).localData.getUserCount(SharedPref.KEY_REPLIES_COUNT)));

            ParseRequest.get_UserTopics(getContext(), ((MainActivity) getActivity()).localData.getUserData(SharedPref.KEY_USER_ID), true);

            // editor
            Fragment editor = getFragmentManager().findFragmentByTag("new_content_editor");

            ((EditText) editor.getView().findViewById(R.id.et_update_first_name)).setText(((MainActivity) getActivity()).localData.getUserData(SharedPref.KEY_NAME));
            ((EditText) editor.getView().findViewById(R.id.et_update_family_surname)).setText(((MainActivity) getActivity()).localData.getUserData(SharedPref.KEY_SURNAME));
            ((EditText) editor.getView().findViewById(R.id.bio_edit_text)).setText(bio);

        } else if (type.equals(SharedPref.KEY_CARDS_COUNT)) {

            tv_cards.setText(String.valueOf(((MainActivity) getActivity()).localData.getUserCount(SharedPref.KEY_CARDS_COUNT)));

        } else if (type.equals(SharedPref.KEY_UPVOTES_COUNT)) {

            tv_upvotes.setText(String.valueOf(((MainActivity)getActivity()).localData.getUserCount(SharedPref.KEY_UPVOTES_COUNT)));

        } else if (type.equals(SharedPref.KEY_REPLIES_COUNT)) {

            tv_replies.setText(String.valueOf(((MainActivity)getActivity()).localData.getUserCount(SharedPref.KEY_REPLIES_COUNT)));

        } else if (type.equals(SharedPref.KEY_BIO)) {

            tv_bio_text.setText(String.valueOf(((MainActivity)getActivity()).localData.getUserCount(SharedPref.KEY_BIO)));

            tv_bio_link.setText(String.valueOf(((MainActivity)getActivity()).localData.getUserCount(SharedPref.KEY_BIO_URL)));

            tv_bio_link.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {

                    try {

                        Uri uri = Uri.parse(tv_bio_link.getText().toString()); // missing 'http://' will cause crashed
                        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                        MainActivity.context.startActivity(intent);

                    } catch (ActivityNotFoundException e) {

                    }
                }
            });
        }
    }


    public void update_OtherProfilHeader() {

        tv_cards.setText(String.valueOf(otherUser.getNumber("cards_count")));
        tv_bio_text.setText(otherUser.getString("bio"));

        String bio_link = otherUser.getString("bio_link");
        if(bio_link == null) bio_link = "";

        if(!bio_link.isEmpty()) {

            tv_bio_link.setText(bio_link);

            tv_bio_link.setVisibility(View.VISIBLE);

            tv_bio_link.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {

                    try {

                        Uri uri = Uri.parse(tv_bio_link.getText().toString()); // missing 'http://' will cause crashed
                        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                        SubActivity.context.startActivity(intent);

                    } catch (ActivityNotFoundException e) {

                    }
                }
            });

        } else tv_bio_link.setVisibility(View.GONE);

        ParseRequest.get_UpvotesCount(otherUser.getObjectId(), tv_upvotes);
        ParseRequest.get_RepliesCount(otherUser.getObjectId(), tv_replies);
        ParseRequest.get_UserTopics(getContext(), otherUser.getObjectId(), false);
    }


    public void update_bio(String bio) {
        if (bio.isEmpty()) {
            tv_bio_text.setTextColor(1711276032);
            tv_bio_text.setAlpha(1.0F);
            tv_bio_text.setText("Edit your bio");
            tv_bio_text.setGravity(Gravity.CENTER);
            tv_bio_text.setTextSize(20.0F);
            tv_bio_text.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.button_sl_empty_bio));

        } else {
            //tv_bio_text.setPadding(10, 8, 10, 8);
            tv_bio_text.setTextColor(ContextCompat.getColor(getContext(), 2131623971));
            tv_bio_text.setAlpha(1.0F);
            tv_bio_text.setText(bio);
            tv_bio_text.setGravity(Gravity.LEFT);
            tv_bio_text.setTextSize(15.0F);
            tv_bio_text.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.button_sl_bio));
        }
    }


    public void update_bio_url(String bio_url) {

        if (bio_url.isEmpty()) tv_bio_link.setVisibility(View.GONE);

        else {

            tv_bio_link.setVisibility(View.VISIBLE);

            tv_bio_link.setText(bio_url);

            tv_bio_link.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {

                    try {

                        String url = tv_bio_link.getText().toString();

                        if(url.startsWith("http://") || url.startsWith("https://")) {

                            Uri uri = Uri.parse(tv_bio_link.getText().toString()); // missing 'http://' will cause crashed
                            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                            MainActivity.context.startActivity(intent);

                        } else Message.message(getContext(), getResources().getString(R.string.not_valid_link));

                    } catch (ActivityNotFoundException e) {

                    }
                }
            });
        }
    }


    public void update_profil_topics(ArrayList<String> paramArrayList){

        header_tags_row.setVisibility(View.VISIBLE);

        int i = 1;

        for(String tag : paramArrayList) {

            Tags.setCardView_Layout(Tags.PROFIL_TAG, (CardView) header_tags_row.getChildAt(i), tag);
            header_tags_row.getChildAt(i).setVisibility(View.VISIBLE);

            i++;

        }

        while (i < header_tags_row.getChildCount() - 1) {
            header_tags_row.getChildAt(i).setVisibility(View.GONE);
            i++;
        }
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {

        private final List<Fragment> mFragmentList = new ArrayList();
        private final List<String> mFragmentTitleList = new ArrayList();

        public ViewPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getCount()
        {
            return this.mFragmentList.size();
        }

        @Override
        public Fragment getItem(int position)
        {
            return mFragmentList.get(position);
        }

        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }
    }
}