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
import android.view.ViewTreeObserver;
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
import co.forum.app.ContentEditor.Comment_Editor;
import co.forum.app.Data.CardData;
import co.forum.app.Data.ChatMessageData;
import co.forum.app.Data.ParseRequest;
import co.forum.app.MainActivity;
import co.forum.app.R;
import co.forum.app.SharedPref;
import co.forum.app.tools.Message;

public class ChatFrag extends Fragment {

    public static final String KEY_CHAT_ID = "key_chat_id";
    public static final String KEY_FIRST_CHAT_CARD_ID = "key_first_chat_card_id";
    public static final String KEY_STARTER_ID = "key_starter_id";
    public static final String KEY_REPLIER_ID = "key_replier_id";

    public View layout;

    @Bind(R.id.chat_content_panel_structure) public SlidingUpPanelLayout chat_content_panel_structure;
    @Bind(R.id.recycler_chat_message) public RecyclerView recycler_chat_message;

    @Bind(R.id.tv_starter_name) public TextView tv_starter_name;
    @Bind(R.id.tv_replier_name) public TextView tv_replier_name;

    @Bind(R.id.profil_pic_1) ImageView profil_pic_1;
    @Bind(R.id.profil_pic_2) ImageView profil_pic_2;

    @Bind(R.id.btn_profil_1) LinearLayout btn_profil_1;
    @Bind(R.id.btn_profil_2) LinearLayout btn_profil_2;

    @Bind(R.id.bottom_contribute) LinearLayout bottom_contribute;



    public ChatConversationAdapter chatAdapter;

    public Comment_Editor comment_editor;

    public ArrayList<CardData> cardsList = new ArrayList<>();

    public ChatFrag() {
        // Required empty public constructor
    }

    String chatID;
    CardData firstChatCard;
    String starterID;
    String replierID;

    String starter_username = "";
    String replier_username = "";

    String starter_pict = "";
    String replier_pict = "";

    public static int STARTER = 0;
    public static int REPLIER = 1;
    public static int SPECTATOR = 2;

    private int user_role = -1;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        chatID = getArguments().getString(KEY_CHAT_ID, "");

        starterID = getArguments().getString(KEY_STARTER_ID, "");

        replierID = getArguments().getString(KEY_REPLIER_ID, "");

        firstChatCard = getArguments().getParcelable(KEY_FIRST_CHAT_CARD_ID);

        this.cardsList.add(firstChatCard);

        if((((MainActivity)MainActivity.context).localData.getUserData(SharedPref.KEY_USER_ID)).equals(starterID))

            user_role = STARTER;

        else if((((MainActivity)MainActivity.context).localData.getUserData(SharedPref.KEY_USER_ID)).equals(replierID))

            user_role = REPLIER;

        else user_role = SPECTATOR;

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        layout = inflater.inflate(R.layout.fragment_chat, container, false);

        ButterKnife.bind(this, layout);

        tv_starter_name.setTypeface(MainActivity.tfSemiBold);
        tv_replier_name.setTypeface(MainActivity.tfSemiBold);

        ParseRequest.get_ChatThread(getContext(), chatID, false);

        return layout;
    }


    private void set_toolbar() {

        for (CardData card : cardsList) {

            if ((card.getAuthor_id()).equals(starterID) && starter_username.isEmpty()) {

                starter_username = card.getAuthor_full_name();

                starter_pict = card.get_user_pict_url();

            } else if ((card.getAuthor_id()).equals(replierID) && replier_username.isEmpty()) {

                replier_username = card.getAuthor_full_name();

                replier_pict = card.get_user_pict_url();
            }

            if(!starter_username.isEmpty() && !replier_username.isEmpty()) break;
        }

        tv_starter_name.setText(starter_username);


        if( (starter_pict != null)  &&  !starter_pict.isEmpty() && !starter_pict.equals("NULL") ) Picasso.with(MainActivity.context).load(starter_pict).into(profil_pic_1);
        else profil_pic_1.setImageDrawable(ContextCompat.getDrawable(MainActivity.context, R.drawable.profil_pict));

        btn_profil_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!starterID.equals( ((MainActivity)MainActivity.context).localData.getUserData(SharedPref.KEY_USER_ID)))

                    SubActivity.openUserProfil(getContext(), starter_username, starterID, starter_pict, false); // todo verifier false
            }
        });

        if(starterID.equals(replierID)) btn_profil_2.setVisibility(View.GONE);

        else {

            tv_replier_name.setText(replier_username);

            if (!replier_pict.isEmpty() && !replier_pict.equals("NULL"))
                Picasso.with(MainActivity.context).load(replier_pict).into(profil_pic_2);
            else
                profil_pic_2.setImageDrawable(ContextCompat.getDrawable(MainActivity.context, R.drawable.profil_pict));

            btn_profil_2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (!replierID.equals(((MainActivity) MainActivity.context).localData.getUserData(SharedPref.KEY_USER_ID)))

                        SubActivity.openUserProfil(getContext(), replier_username, replierID, replier_pict, false); // todo verifier false
                }
            });

        }

    }


    public void displayReplies(Context context, ArrayList<CardData> cardsList) {

        //loader.setVisibility(View.GONE); todo loader pour les commentaires

        this.cardsList.addAll(cardsList);

        set_toolbar();

        chatAdapter = new ChatConversationAdapter(getActivity(), this.cardsList, starterID, replierID, user_role);

        recycler_chat_message.setAdapter(chatAdapter);

        /*
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        layoutManager.setReverseLayout(true);// Scroll to bottom
        recycler_chat_message.setLayoutManager(layoutManager);
        */

        recycler_chat_message.setLayoutManager(new LinearLayoutManager(getActivity()));
        recycler_chat_message.scrollToPosition(cardsList.size());

        init_ReplyEditor();
    }


    private void init_ReplyEditor() {

        if(user_role == SPECTATOR) {

            //panel_editor_container_chat.setVisibility(View.GONE);

            chat_content_panel_structure.setPanelHeight(0);

            bottom_contribute.setVisibility(View.VISIBLE);

            //comment_editor.setChat_editor_visible(false);

        } else {

            bottom_contribute.setVisibility(View.GONE);



            FragmentManager manager;

            android.support.v4.app.FragmentTransaction transaction;

            comment_editor = new Comment_Editor();
            comment_editor.setHint_tags(getActivity().getResources().getString(R.string.et_hint_tags_optional));
            comment_editor.setTagsArrayList(new ArrayList<String>());

            comment_editor.setEditor_type(Comment_Editor.CHAT_COMMENT_EDITOR);
            comment_editor.setIs_story(false);

            comment_editor.setChatID(chatID);

            if(user_role == STARTER && !starterID.equals(replierID)) {

                comment_editor.setChat_editor_visible(true);

                comment_editor.setHint_content("Reply to " + replier_username);
                comment_editor.setReply_to_who("Reply to " + replier_username);

            } else if(user_role == STARTER && starterID.equals(replierID)) {

                comment_editor.setChat_editor_visible(true);

                comment_editor.setHint_content("Continue writing...");
                comment_editor.setReply_to_who("Continue writing...");

            } else if(user_role == REPLIER) {

                comment_editor.setChat_editor_visible(true);

                comment_editor.setHint_content("Reply to " + starter_username);
                comment_editor.setReply_to_who("Reply to " + starter_username);
            }




            manager = getActivity().getSupportFragmentManager();
            transaction = manager.beginTransaction();
            transaction.add(R.id.panel_editor_container_chat, comment_editor, "message_content_editor");

            transaction.commit();


            /*
            ViewTreeObserver vto = comment_editor.header_reply.getViewTreeObserver();
            vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    comment_editor.header_reply.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                    int height = comment_editor.header_reply.getMeasuredHeight();

                    chat_content_panel_structure.setPanelHeight(height); //90

                }
            });
            */




            //Message.message(getContext(), ">> " + Comment_Editor.header_editor_height);

            //chat_content_panel_structure.setPanelHeight(Comment_Editor.header_editor_height); //90



            chat_content_panel_structure.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {

                    MainActivity.hideSoftKeyboard(view);

                    //onBackPressed();

                    return false;
                }
            });

            chat_content_panel_structure.setPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {

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
