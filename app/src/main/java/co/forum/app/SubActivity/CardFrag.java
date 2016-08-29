package co.forum.app.SubActivity;


import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import co.forum.app.Adapters.CommentRecyclerAdapter;
import co.forum.app.ContentEditor.Comment_Editor;
import co.forum.app.Data.CardData;
import co.forum.app.Data.ParseRequest;
import co.forum.app.MainActivity;
import co.forum.app.R;

public class CardFrag extends Fragment {

    public View layout;

    public Comment_Editor comment_editor;

    public CardData headerCardData;
    public boolean fromComment;

    // seul cas ou l'on doit faire une requete pour obtenir la carte
    public boolean parent_card_id_alone;
    public String parent_card_id;

    public CardFrag() {
        // Required empty public constructor
    }


    @Bind(R.id.reply_content_panel_structure) public SlidingUpPanelLayout reply_content_panel_structure;
    @Bind(R.id.recycler_replies) public RecyclerView repliesRecyclerView;


    boolean open_editor_on_launch = false;

    //public HeaderCardFrag headerCardFrag; todo a virer

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        parent_card_id_alone = getArguments().getBoolean(SubActivity.KEY_CARD_ID_ALONE, false);

        if(!parent_card_id_alone) { // normal access click

            headerCardData = getArguments().getParcelable(SubActivity.KEY_CARD_TO_OPEN);

            fromComment = getArguments().getBoolean(SubActivity.KEY_FROM_COMMENT);

            if(fromComment) open_editor_on_launch = getArguments().getBoolean(SubActivity.KEY_OPEN_EDITOR_ON_LAUNCH);



        } else { // from notification click

            parent_card_id = getArguments().getString(SubActivity.KEY_CARD_TO_OPEN);

            fromComment = getArguments().getBoolean(SubActivity.KEY_FROM_COMMENT);

        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        layout = inflater.inflate(R.layout.fragment_card, container, false);

        ButterKnife.bind(this, layout);


        if(!parent_card_id_alone) {

            //implement_header();

            init_RepliesList();

            init_ReplyEditor();

            Handler handlerTimer = new Handler();

            handlerTimer.postDelayed(new Runnable() {
                public void run() {
                    if (open_editor_on_launch)

                        reply_content_panel_structure.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);
                }
            }, 600);


        } else {

            ParseRequest.get_Card(getContext(), parent_card_id);
        }


        return layout;
    }


    // todo a virer
    private void implement_header() {

        Bundle bundle = new Bundle();

        bundle.putParcelable(SubActivity.KEY_CARD_TO_OPEN, headerCardData);
        bundle.putBoolean(SubActivity.KEY_FROM_COMMENT, fromComment);

        //headerCardFrag = new HeaderCardFrag();

        //headerCardFrag.setArguments(bundle);

        FragmentManager manager = getFragmentManager();

        FragmentTransaction transaction = manager.beginTransaction();

        //transaction.add(R.id.card_header_container, headerCardFrag, "headerCardFrag"); // todo card_header_container viré

        transaction.commit();

    }

    // todo : cas a revoir si c'est bon
    public void set_Delayed_Header(CardData headerCardData) {

        this.headerCardData = headerCardData;

        init_RepliesList();

        init_ReplyEditor();
    }


    public void displayReplies(Context context, ArrayList<CardData> cardsList) {

        //loader.setVisibility(View.GONE); todo loader pour les commentaires

        commentAdapter.repliesList.addAll(cardsList); //repliesList.add(headerCardData);

        if(cardsList.size() == 0 ) {

            //headerCardFrag.indicator_post_comment.setVisibility(View.VISIBLE);
            //headerCardFrag.indicator_post_comment.setText("Be the first to comment");

        } else //headerCardFrag.indicator_post_comment.setVisibility(View.GONE);


        //commentAdapter.repliesList.addAll(cardsList);

        commentAdapter.notifyDataSetChanged();

    }

    public ArrayList<CardData> repliesList;

    public CommentRecyclerAdapter commentAdapter;

    public void init_RepliesList() {

        repliesList = new ArrayList<>();

        ParseRequest.get_CardReplies(getContext(), headerCardData.getCard_id());

        commentAdapter = new CommentRecyclerAdapter(getActivity(), headerCardData, fromComment, repliesList);

        repliesRecyclerView.setAdapter(commentAdapter);

        final LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());

        repliesRecyclerView.setLayoutManager(mLayoutManager);

    }



    private void init_ReplyEditor() {

        FragmentManager manager;

        android.support.v4.app.FragmentTransaction transaction;

        comment_editor = new Comment_Editor();

        comment_editor.setEditor_type(Comment_Editor.CARD_COMMENT_EDITOR);
        comment_editor.setParent_card(headerCardData);

        comment_editor.setReply_to_who(String.format(getResources().getString(R.string.reply_to), headerCardData.getAuthor_full_name()));
        comment_editor.setHint_tags(getActivity().getResources().getString(R.string.et_hint_tags_optional));
        comment_editor.setHint_content(getResources().getString(R.string.write_reply_to) + headerCardData.getAuthor_full_name());
        comment_editor.setTagsArrayList(new ArrayList<String>());

        manager = getActivity().getSupportFragmentManager();
        transaction = manager.beginTransaction();
        transaction.add(R.id.panel_editor_container_comment, comment_editor, "reply_content_editor");

        transaction.commit();


        reply_content_panel_structure.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                MainActivity.hideSoftKeyboard(view);

                //onBackPressed();

                return false;
            }
        });

        reply_content_panel_structure.setPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {

            @Override
            public void onPanelSlide(View panel, float slideOffset) {

                /*

                if(slideOffset > 0.5f) {

                    comment_editor.getHeader_reply().setVisibility(View.GONE);

                } else if(slideOffset < 0.5f) {

                    comment_editor.getHeader_reply().setVisibility(View.VISIBLE);

                }
                */

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





    public class SubCommentViewHolder extends RecyclerView.ViewHolder {

        public SubCommentViewHolder(View itemView) {
            super(itemView);
        }
    }

}
