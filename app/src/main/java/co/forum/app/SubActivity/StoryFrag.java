package co.forum.app.SubActivity;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sothree.slidinguppanel.SlidingUpPanelLayout;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import co.forum.app.Adapters.ChatConversationAdapter;
import co.forum.app.Adapters.ChatStoryAdapter;
import co.forum.app.ContentEditor.Comment_Editor;
import co.forum.app.Data.CardData;
import co.forum.app.Data.ChatMessageData;
import co.forum.app.Data.ParseRequest;
import co.forum.app.MainActivity;
import co.forum.app.R;
import co.forum.app.SharedPref;
import co.forum.app.tools.Message;

public class StoryFrag extends Fragment {

    public static final String KEY_CHAT_ID = "key_chat_id";
    public static final String KEY_FIRST_STORY_CARD_ID = "key_first_story_card_id";
    public static final String KEY_AUTHOR_ID = "key_starter_id";
    public static final String KEY_STORY_TITLE = "key_story_title";

    public View layout;

    @Bind(R.id.story_content_panel_structure) public SlidingUpPanelLayout story_content_panel_structure;
    @Bind(R.id.recycler_story_cards) public RecyclerView recycler_story_cards;

    @Bind(R.id.tv_author_name) public TextView tv_author_name;
    @Bind(R.id.tv_date) public TextView tv_date;


    @Bind(R.id.author_pict) ImageView author_pict;

    @Bind(R.id.btn_author) LinearLayout btn_author;


    public ChatStoryAdapter storyAdapter;

    public Comment_Editor comment_editor;

    public ArrayList<CardData> cardsList = new ArrayList<>();

    public StoryFrag() {
        // Required empty public constructor
    }

    String chatID;
    String story_title = "";
    CardData firstChatCard;
    String authorID;

    String author_username = "";
    String author_pict_url = "";

    public static int AUTHOR = 3;
    public static int SPECTATOR = 4;

    private int user_role = -1;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        chatID = getArguments().getString(KEY_CHAT_ID, "");

        authorID = getArguments().getString(KEY_AUTHOR_ID, "");

        story_title = getArguments().getString(KEY_STORY_TITLE, "");

        firstChatCard = getArguments().getParcelable(KEY_FIRST_STORY_CARD_ID);

        if((((MainActivity)MainActivity.context).localData.getUserData(SharedPref.KEY_USER_ID)).equals(authorID))

            user_role = AUTHOR;

        else user_role = SPECTATOR;

        if(!story_title.isEmpty() || (user_role == AUTHOR)) this.cardsList.add(new CardData(story_title));

        this.cardsList.add(firstChatCard);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        layout = inflater.inflate(R.layout.fragment_story, container, false);

        ButterKnife.bind(this, layout);

        tv_author_name.setTypeface(MainActivity.tfBold);
        tv_date.setTypeface(MainActivity.tfSemiBold);

        ParseRequest.get_ChatThread(getContext(), chatID, true);

        return layout;
    }

    //card.getAuthor_id()
    //card.getAuthor_full_name()
    //card.get_user_pict_url()
    //
    private void set_toolbar() {

        for (CardData card : cardsList) {

            if (card.getChat_id()!= null && (card.getAuthor_id()).equals(authorID) && author_username.isEmpty()) {

                // 1er condition pour card title

                author_username = card.getAuthor_full_name();

                author_pict_url = card.get_user_pict_url();

            }

            tv_date.setText(card.getTimestamp());


            if(!author_username.isEmpty()) break;
        }

        tv_author_name.setText(author_username);


        if( (author_pict_url != null)  &&  !author_pict_url.isEmpty() && !author_pict_url.equals("NULL") )

            Picasso.with(MainActivity.context).load(author_pict_url).into(author_pict);

        else author_pict.setImageDrawable(ContextCompat.getDrawable(MainActivity.context, R.drawable.profil_pict));

        btn_author.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!authorID.equals(((MainActivity) MainActivity.context).localData.getUserData(SharedPref.KEY_USER_ID)))

                    SubActivity.openUserProfil(getContext(), author_username, authorID, author_pict_url, false); // todo verifier false
            }
        });

    }


    public void displayCards(Context context, ArrayList<CardData> cardsList) {

        //loader.setVisibility(View.GONE); todo loader pour les commentaires

        this.cardsList.addAll(cardsList);

        set_toolbar();

        storyAdapter = new ChatStoryAdapter(getActivity(), chatID, this.cardsList, story_title, authorID, user_role);

        recycler_story_cards.setAdapter(storyAdapter);

        /*
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        layoutManager.setReverseLayout(true);// Scroll to bottom
        recycler_chat_message.setLayoutManager(layoutManager);
        */

        recycler_story_cards.setLayoutManager(new LinearLayoutManager(getActivity()));
        //recycler_story_cards.scrollToPosition(cardsList.size());

        init_ReplyEditor();
    }


    private void init_ReplyEditor() {

        if(user_role == SPECTATOR) {

            //panel_editor_container_chat.setVisibility(View.GONE);

            story_content_panel_structure.setPanelHeight(0);

            //comment_editor.setChat_editor_visible(false);

        } else {

            story_content_panel_structure.setPanelHeight(90); // TODO position

            FragmentManager manager;

            android.support.v4.app.FragmentTransaction transaction;

            comment_editor = new Comment_Editor();
            comment_editor.setHint_tags(getResources().getString(R.string.et_hint_tags_optional));
            comment_editor.setTagsArrayList(new ArrayList<String>());

            comment_editor.setEditor_type(Comment_Editor.CHAT_COMMENT_EDITOR);
            comment_editor.setIs_story(true);

            comment_editor.setChatID(chatID);

            if(user_role == AUTHOR) {

                comment_editor.setChat_editor_visible(true);

                comment_editor.setHint_content(getResources().getString(R.string.continue_writing));
                comment_editor.setReply_to_who(getResources().getString(R.string.continue_writing));

            }

            manager = getActivity().getSupportFragmentManager();
            transaction = manager.beginTransaction();
            transaction.add(R.id.panel_editor_container_chat, comment_editor, "message_content_editor");

            transaction.commit();


            story_content_panel_structure.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {

                    MainActivity.hideSoftKeyboard(view);

                    //onBackPressed();

                    return false;
                }
            });

            story_content_panel_structure.setPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {

                @Override
                public void onPanelSlide(View panel, float slideOffset) {

                    if (slideOffset > 0.5f) {

                        comment_editor.getHeader_reply().setVisibility(View.GONE);

                    } else if (slideOffset < 0.5f) {

                        comment_editor.getHeader_reply().setVisibility(View.VISIBLE);

                    }

                }

                @Override
                public void onPanelCollapsed(View panel) {

                    comment_editor.et_Tags.clearFocus();
                    comment_editor.et_Content.clearFocus();

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

            //reply_content_panel_structure.setTouchEnabled(false); //todo 3.3.0

        }



    }


    public List<ChatMessageData> getData(){

        List<ChatMessageData> data = new ArrayList<>();

        int[] user_position = getActivity().getResources().getIntArray(R.array.user_position);
        String[] username = getActivity().getResources().getStringArray(R.array.username);
        String[] chat_message = getActivity().getResources().getStringArray(R.array.chat_message);
        int[] nb_replies = getActivity().getResources().getIntArray(R.array.nb_replies_message);
        String[] last_timestamp = getActivity().getResources().getStringArray(R.array.last_timestamp_message);

        for(int i=0; i < user_position.length && i < username.length; i++){

            ChatMessageData current = new ChatMessageData();
            current.user_position = user_position[i];
            current.username = username[i];
            current.chat_message = chat_message[i];
            current.nb_replies = nb_replies[i];
            current.last_timestamp = last_timestamp[i];
            data.add(current);
        }
        return data;
    }

}
