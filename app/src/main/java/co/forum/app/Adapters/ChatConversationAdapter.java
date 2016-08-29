package co.forum.app.Adapters;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sothree.slidinguppanel.SlidingUpPanelLayout;
import com.squareup.picasso.Picasso;

import java.util.Collections;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import co.forum.app.Data.CardData;
import co.forum.app.Data.ParseRequest;
import co.forum.app.MainActivity;
import co.forum.app.R;
import co.forum.app.SharedPref;
import co.forum.app.SubActivity.ChatFrag;
import co.forum.app.SubActivity.SubActivity;
import co.forum.app.tools.Message;
import co.forum.app.tools.SquareRelativeLayout;

public class ChatConversationAdapter extends RecyclerView.Adapter<ChatConversationAdapter.ChatHolder> {

    private LayoutInflater inflater;

    private Context context;

    private String starterID;

    private String replierID;

    private int user_role;

    public List<CardData> chat_list = Collections.emptyList();

    public ChatConversationAdapter(Context context, List<CardData> listOfMessages, String starterID, String replierID, int user_role) {

        inflater = LayoutInflater.from(context);

        this.context = context;

        this.starterID = starterID;

        this.replierID = replierID;

        this.chat_list = listOfMessages;

        this.user_role = user_role;

    }

    @Override
    public ChatHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = inflater.inflate(R.layout.item_card_chat, parent, false);

        return new ChatHolder(view);
    }

    @Override
    public void onBindViewHolder(final ChatHolder holder, final int position) {

        String pict_URL = chat_list.get(position).get_user_pict_url();

        if(pict_URL == null) pict_URL = "NULL";

        if((chat_list.get(position).getAuthor_id()).equals(starterID)) {

            holder.user_starter.setVisibility(View.VISIBLE);
            holder.user_replier.setVisibility(View.GONE);

            holder.tv_main_content_1.setText(chat_list.get(position).getMain_content());
            holder.nb_upvotes_1.setText(String.valueOf(chat_list.get(position).getNb_upvotes()));

            // image
            if(!chat_list.get(position).get_pict_url().isEmpty()) {

                ViewGroup.LayoutParams params= holder.scale_for_pict_1.getLayoutParams();
                params.width= LinearLayout.LayoutParams.MATCH_PARENT;
                holder.scale_for_pict_1.setLayoutParams(params);


                holder.square_placeholder_1.setVisibility(View.VISIBLE);
                Picasso.with(context)
                        .load(chat_list.get(position).get_pict_url())
                        .fit().centerCrop()
                        .into(holder.img_placeholder_1);

                if(chat_list.get(position).getMain_content().isEmpty()) holder.tv_main_content_1.setVisibility(View.GONE);

            } else {

                ViewGroup.LayoutParams params= holder.scale_for_pict_1.getLayoutParams();
                params.width= LinearLayout.LayoutParams.WRAP_CONTENT;
                holder.scale_for_pict_1.setLayoutParams(params);

                holder.square_placeholder_1.setVisibility(View.GONE);
                holder.tv_main_content_1.setVisibility(View.VISIBLE);
            }

            // link
            final String link = chat_list.get(position).getLink_url();

            if(link.isEmpty()) holder.btn_link_1.setVisibility(View.GONE);

            else {

                holder.tv_link_1.setText(link);
                holder.btn_link_1.setVisibility(View.VISIBLE);
                holder.btn_link_1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        try {

                            Uri uri = Uri.parse(link); // missing 'http://' will cause crashed
                            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                            SubActivity.context.startActivity(intent);

                        } catch (ActivityNotFoundException e) {

                        }
                    }
                });
            }

            // ouverture de carte
            holder.comment_content_1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SubActivity.openCard(context, chat_list.get(position), true, false);
                }
            });

            // btn upvote
            holder.btn_mini_upvote_1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (user_role == ChatFrag.STARTER)
                        Message.message(context, "You cannot upvote your own card ^^");

                    else {

                        ParseRequest.check_and_insert_upvote(
                                context,
                                chat_list.get(position).getCard_id(),
                                ((MainActivity) MainActivity.context).localData.getUserData(SharedPref.KEY_USER_ID),
                                chat_list.get(position).getAuthor_id(),
                                ((MainActivity) MainActivity.context).localData.getUserData(SharedPref.KEY_FULL_NAME),
                                chat_list.get(position).getMain_content()
                        );

                        holder.nb_upvotes_1.setText("" + (chat_list.get(position).getNb_upvotes() + 1));

                    }
                }
            });

            // btn comment
            if(user_role == ChatFrag.SPECTATOR) {

                int nb_external_comments = chat_list.get(position).getNb_replies() - 1;

                if(nb_external_comments == -1) nb_external_comments = 0;

                holder.nb_replies_1.setText(String.valueOf(nb_external_comments));

                holder.btn_mini_comment_1.setVisibility(View.VISIBLE);

                holder.btn_mini_comment_1.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {

                        Message.message(context, "To comment, open this card");

                        //SubActivity.openCard(context, chat_list.get(position), true, true);

                    }
                });

            } else {

                if(chat_list.get(position).getNb_replies() >= 2 ) {

                    holder.nb_replies_1.setText(String.valueOf(chat_list.get(position).getNb_replies() - 1));

                    holder.btn_mini_comment_1.setVisibility(View.VISIBLE);

                    holder.btn_mini_comment_1.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            Message.message(context, "To comment, open this card");

                            //((SubActivity)context).chatFragment.chat_content_panel_structure.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);

                        }
                    });

                } else holder.btn_mini_comment_1.setVisibility(View.GONE);
            }

            if(!pict_URL.isEmpty() && !pict_URL.equals("NULL")) Picasso.with(MainActivity.context).load(pict_URL).into(holder.profil_pic_1);
            else holder.profil_pic_1.setImageDrawable(ContextCompat.getDrawable(MainActivity.context, R.drawable.profil_pict));

            holder.btn_profil_1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Message.message(context, "click profil starter");
                }
            });





        } else if((chat_list.get(position).getAuthor_id()).equals(replierID)) {

            holder.user_starter.setVisibility(View.GONE);
            holder.user_replier.setVisibility(View.VISIBLE);

            holder.tv_main_content_2.setText(chat_list.get(position).getMain_content());
            holder.nb_upvotes_2.setText(String.valueOf(chat_list.get(position).getNb_upvotes()));

            // image
            // image
            if(!chat_list.get(position).get_pict_url().isEmpty()) {

                ViewGroup.LayoutParams params= holder.scale_for_pict_2.getLayoutParams();
                params.width= LinearLayout.LayoutParams.MATCH_PARENT;
                holder.scale_for_pict_2.setLayoutParams(params);


                holder.square_placeholder_2.setVisibility(View.VISIBLE);
                Picasso.with(context)
                        .load(chat_list.get(position).get_pict_url())
                        .fit().centerCrop()
                        .into(holder.img_placeholder_2);

                if(chat_list.get(position).getMain_content().isEmpty()) holder.tv_main_content_2.setVisibility(View.GONE);

            } else {

                ViewGroup.LayoutParams params= holder.scale_for_pict_2.getLayoutParams();
                params.width= LinearLayout.LayoutParams.WRAP_CONTENT;
                holder.scale_for_pict_2.setLayoutParams(params);

                holder.square_placeholder_2.setVisibility(View.GONE);
                holder.tv_main_content_2.setVisibility(View.VISIBLE);
            }




            // link
            final String link = chat_list.get(position).getLink_url();

            if(link.isEmpty()) holder.btn_link_2.setVisibility(View.GONE);

            else {

                holder.tv_link_2.setText(link);
                holder.btn_link_2.setVisibility(View.VISIBLE);
                holder.btn_link_2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        try {

                            Uri uri = Uri.parse(link); // missing 'http://' will cause crashed
                            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                            SubActivity.context.startActivity(intent);

                        } catch (ActivityNotFoundException e) {

                        }
                    }
                });
            }


            // ouverture de carte
            holder.comment_content_2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SubActivity.openCard(context, chat_list.get(position), true, false);
                }
            });

            // btn upvote
            holder.btn_mini_upvote_2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if(user_role == ChatFrag.REPLIER) Message.message(context, "You cannot upvote your own card ^^");

                    else {

                        ParseRequest.check_and_insert_upvote(
                                context,
                                chat_list.get(position).getCard_id(),
                                ((MainActivity) MainActivity.context).localData.getUserData(SharedPref.KEY_USER_ID),
                                chat_list.get(position).getAuthor_id(),
                                ((MainActivity) MainActivity.context).localData.getUserData(SharedPref.KEY_FULL_NAME),
                                chat_list.get(position).getMain_content()
                        );

                        holder.nb_upvotes_2.setText("" + (chat_list.get(position).getNb_upvotes() + 1));

                    }
                }
            });

            // btn comment
            if(user_role == ChatFrag.SPECTATOR) {

                int nb_external_comments = chat_list.get(position).getNb_replies() - 1;

                if(nb_external_comments == -1) nb_external_comments = 0;

                holder.nb_replies_2.setText(String.valueOf(nb_external_comments));

                holder.btn_mini_comment_2.setVisibility(View.VISIBLE);

                holder.btn_mini_comment_2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Message.message(context, "To comment, open this card");

                        //SubActivity.openCard(context, chat_list.get(position), true, true);

                    }
                });

            } else {

                if(chat_list.get(position).getNb_replies() >= 2 ) {

                    holder.nb_replies_2.setText(String.valueOf(chat_list.get(position).getNb_replies() - 1));

                    holder.btn_mini_comment_2.setVisibility(View.VISIBLE);

                    holder.btn_mini_comment_2.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            //Message.message(context, "comment chat participant");

                            ((SubActivity)context).chatFragment.chat_content_panel_structure.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);

                        }
                    });

                } else holder.btn_mini_comment_2.setVisibility(View.GONE);
            }


            if(!pict_URL.equals("NULL")) Picasso.with(MainActivity.context).load(pict_URL).into(holder.profil_pic_2);
            else holder.profil_pic_2.setImageDrawable(ContextCompat.getDrawable(MainActivity.context, R.drawable.profil_pict));

            holder.btn_profil_2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Message.message(context, "click profil replier");
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return chat_list.size();
    }

    class ChatHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.user_starter) LinearLayout user_starter;
        @Bind(R.id.user_replier) LinearLayout user_replier;

        @Bind(R.id.scale_for_pict_1) LinearLayout scale_for_pict_1;
        @Bind(R.id.scale_for_pict_2) LinearLayout scale_for_pict_2;



        @Bind(R.id.profil_pic_1) ImageView profil_pic_1;
        @Bind(R.id.profil_pic_2) ImageView profil_pic_2;

        @Bind(R.id.comment_content_1) RelativeLayout comment_content_1;
        @Bind(R.id.comment_content_2) RelativeLayout comment_content_2;

        @Bind(R.id.btn_profil_1) LinearLayout btn_profil_1;
        @Bind(R.id.btn_profil_2) LinearLayout btn_profil_2;

        @Bind(R.id.btn_link_1) LinearLayout btn_link_1;
        @Bind(R.id.btn_link_2) LinearLayout btn_link_2;

        @Bind(R.id.tv_link_1) TextView tv_link_1;
        @Bind(R.id.tv_link_2) TextView tv_link_2;

        @Bind(R.id.square_placeholder_1) SquareRelativeLayout square_placeholder_1;
        @Bind(R.id.square_placeholder_2) SquareRelativeLayout square_placeholder_2;

        @Bind(R.id.img_placeholder_1) ImageView img_placeholder_1;
        @Bind(R.id.img_placeholder_2) ImageView img_placeholder_2;

        @Bind(R.id.tv_main_content_1) TextView tv_main_content_1;
        @Bind(R.id.tv_main_content_2) TextView tv_main_content_2;

        @Bind(R.id.nb_upvotes_1) TextView nb_upvotes_1;
        @Bind(R.id.nb_upvotes_2) TextView nb_upvotes_2;

        @Bind(R.id.btn_mini_upvote_1) LinearLayout btn_mini_upvote_1;
        @Bind(R.id.btn_mini_upvote_2) LinearLayout btn_mini_upvote_2;

        @Bind(R.id.nb_replies_1) TextView nb_replies_1;
        @Bind(R.id.nb_replies_2) TextView nb_replies_2;

        @Bind(R.id.btn_mini_comment_1) LinearLayout btn_mini_comment_1;
        @Bind(R.id.btn_mini_comment_2) LinearLayout btn_mini_comment_2;



        @Bind(R.id.btn_mini_show_comments_1) LinearLayout btn_mini_show_comments_1;
        @Bind(R.id.btn_mini_show_comments_2) LinearLayout btn_mini_show_comments_2;

        public ChatHolder(View itemView) {

            super(itemView);

            ButterKnife.bind(this, itemView);

            //tv_username.setTypeface(MainActivity.tfBold);
            tv_main_content_1.setTypeface(MainActivity.tf_serif);
            tv_main_content_2.setTypeface(MainActivity.tf_serif);

        }
    }
}
