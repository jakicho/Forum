package co.forum.app.Adapters;

import android.content.ActivityNotFoundException;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sothree.slidinguppanel.SlidingUpPanelLayout;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;
import co.forum.app.Data.CardData;
import co.forum.app.Data.ParseRequest;
import co.forum.app.MainActivity;
import co.forum.app.R;
import co.forum.app.SharedPref;
import co.forum.app.SubActivity.SubActivity;
import co.forum.app.tools.Message;
import co.forum.app.tools.RowLayout;
import co.forum.app.tools.SquareRelativeLayout;
import co.forum.app.tools.Tags;
import co.forum.app.tools.TextSize;

public class CommentRecyclerAdapter extends RecyclerView.Adapter<CommentRecyclerAdapter.CommentViewHolder>{

    // for header only
    final Drawable upvoteON = (ContextCompat.getDrawable(MainActivity.context, R.drawable.ic_thumb_up_orange));
    final Drawable upvoteOFF = (ContextCompat.getDrawable(MainActivity.context, R.drawable.ic_thumb_up_border));

    boolean user_upvote_is_set = false;
    public boolean upvoted;
    boolean notif_state;
    boolean fromComment = false;
    CardData headerCardData;

    private Context context;

    private LayoutInflater inflater;

    //List<CardData> data = Collections.emptyList();

    public ArrayList<CardData> repliesList = new ArrayList<>();

    public CommentRecyclerAdapter(Context context, CardData headerCardData, boolean fromComment, ArrayList<CardData> repliesList) {

        inflater = LayoutInflater.from(context);

        this.context = context;

        this.headerCardData = headerCardData;

        this.fromComment = fromComment;

        this.repliesList.add(headerCardData);

        this.repliesList.addAll(repliesList);


    }

    @Override
    public CommentViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view;

        if(viewType == 0) {

            if(headerCardData.getChat_id() == null || headerCardData.getChat_id().isEmpty()) view = inflater.inflate(R.layout.item_card_header_new, parent, false);

            else view = inflater.inflate(R.layout.item_card_header_comment, parent, false);
        }

        else  view = inflater.inflate(R.layout.item_card_comment, parent, false);

        CommentViewHolder holder = new CommentViewHolder(view);

        return holder;

    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }


    private void set_Parent(final Context context, final CardData cardData, final CommentViewHolder viewHolder) {

        if(!cardData.getParent_card_id().isEmpty()) {
            viewHolder.parent_pre.setTypeface(MainActivity.tf);
            viewHolder.parent_author_name.setTypeface(MainActivity.tfSemiBold);
            viewHolder.parent_post.setTypeface(MainActivity.tf);
            viewHolder.parent_author_name.setText(cardData.getParent_author_full_name());
            viewHolder.parent_indicator.setVisibility(View.VISIBLE);

            viewHolder.parent_indicator.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if (fromComment) ((SubActivity) context).onBackPressed();

                    else {

                        SubActivity.openCardWithID(context, cardData.getParent_card_id());

                        //Message.message(context, "ouverture de la carte : " + cardData.getParent_card_id());
                    }
                }
            });
        } else {
            viewHolder.parent_indicator.setVisibility(View.GONE);
        }
    }


    public void set_header_btn_upvote(boolean upvoted2, final CommentViewHolder viewHolder) {

        user_upvote_is_set = true;

        upvoted = upvoted2;

        set_header_icon_upvote_layout(upvoted, viewHolder);

        //icone_upvote.setVisibility(VISIBLE);

        viewHolder.btn_mini_upvote.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                activate_header_upvote_button(viewHolder);
            }
        });

    }


    private void deactivate_header_upvote_button(final CommentViewHolder viewHolder) {

        //btn_upvote_header.setVisibility(GONE);

        viewHolder.btn_mini_upvote.setOnClickListener(null);

        final Handler handler = new Handler();

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                //btn_upvote_header.setVisibility(VISIBLE);
                viewHolder.btn_mini_upvote.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        activate_header_upvote_button(viewHolder);
                    }
                });

            }
        }, 3000);
    }

    private void activate_header_upvote_button(final CommentViewHolder viewHolder) {

        if (upvoted) {

            ParseRequest.delete_upvote(
                    context,
                    headerCardData.getCard_id(),
                    ((MainActivity) MainActivity.context).localData.getUserData(SharedPref.KEY_USER_ID),
                    headerCardData.getAuthor_id()
            );

            upvoted = false;

            set_header_icon_upvote_layout(false, viewHolder);

        } else {

            ParseRequest.check_and_insert_upvote(
                    context,
                    headerCardData.getCard_id(),
                    ((MainActivity) MainActivity.context).localData.getUserData(SharedPref.KEY_USER_ID),
                    headerCardData.getAuthor_id(),
                    ((MainActivity) MainActivity.context).localData.getUserData(SharedPref.KEY_FULL_NAME),
                    headerCardData.getMain_content()
            );

            upvoted = true;

            set_header_icon_upvote_layout(true, viewHolder);

        }

        deactivate_header_upvote_button(viewHolder);
    }

    public void set_header_icon_upvote_layout(boolean upvoted, final CommentViewHolder viewHolder) {

        if(upvoted) {

            viewHolder.icone_upvote.setImageDrawable(upvoteON);
            viewHolder.icone_upvote.setImageAlpha(245);

        } else {

            viewHolder.icone_upvote.setImageDrawable(upvoteOFF);
            viewHolder.icone_upvote.setImageAlpha(100);

        }
    }

    private void set_header_Tags(final CommentViewHolder viewHolder, ArrayList<String> tagsList) {

        for (int i=0; i < viewHolder.header_tags_row.getChildCount(); i++) {

            if(i < tagsList.size()) {

                Tags.setCardView_Layout(Tags.TAG_MINI_ADAPTER, (CardView) viewHolder.header_tags_row.getChildAt(i), tagsList.get(i));

                viewHolder.header_tags_row.getChildAt(i).setVisibility(View.VISIBLE);

            } else viewHolder.header_tags_row.getChildAt(i).setVisibility(View.GONE);

        }

        if(tagsList.size() == 0) {

            viewHolder.header_tags_row.getChildAt(11).setVisibility(View.VISIBLE);

            viewHolder.tag_footer.setVisibility(View.GONE);
        }


            /*for (int i=0; i < header_tags_row.getChildCount(); i++) {

                Tags.setCardView_Layout(Tags.TAG_MINI_ADAPTER, (CardView) header_tags_row.getChildAt(i), ((TextView) (((CardView) header_tags_row.getChildAt(i)).getChildAt(0))).getText().toString()) ;

                header_tags_row.getChildAt(i).setVisibility(View.VISIBLE);

            }*/
    }

    private void set_header_Buttons(final CommentViewHolder viewHolder, final CardData cardData) {

        if(((MainActivity)MainActivity.context).localData.isUserCard(cardData.getAuthor_id())) {

            set_header_notif_btn(viewHolder, cardData, cardData.isAuthor_listening());

            //icone_upvote.setAlpha(0.4f);

            viewHolder.icone_upvote.setImageAlpha(100);

            viewHolder.btn_mini_upvote.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {

                    Message.message(context, "You cannot upvote your own card :-)");
                }
            });

            viewHolder.btn_copy_content.setVisibility(View.GONE);

        } else {

            viewHolder.btn_copy_content.setVisibility(View.VISIBLE);

            viewHolder.btn_copy_content.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Message.message(context, "Copied to clipboard");

                    ClipboardManager clipboard = (ClipboardManager) MainActivity.context.getSystemService(MainActivity.CLIPBOARD_SERVICE);
                    ClipData clip = ClipData.newPlainText("text", cardData.getMain_content());
                    clipboard.setPrimaryClip(clip);
                }
            });

            viewHolder.btn_notif.setVisibility(View.GONE);

            viewHolder.btn_profil.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    SubActivity.openUserProfil(
                            context,
                            cardData.getAuthor_full_name(),
                            cardData.getAuthor_id(),
                            cardData.get_user_pict_url(),
                            cardData.get_curated());
                }
            });

            if(!user_upvote_is_set) ParseRequest.get_userVote(context, cardData.getCard_id(), cardData.getAuthor_id(), viewHolder);

            else set_header_btn_upvote(upvoted, viewHolder);

        }

        viewHolder.btn_mini_comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ((SubActivity)context).cardFragment.reply_content_panel_structure.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);
            }
        });

        if(getItemCount() == 1) {

            viewHolder.indicator_post_comment.setVisibility(View.VISIBLE);

        } else viewHolder.indicator_post_comment.setVisibility(View.GONE);

        viewHolder.indicator_post_comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ((SubActivity)context).cardFragment.reply_content_panel_structure.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);

            }
        });
    }


    private void set_header_notif_btn(final CommentViewHolder viewHolder, final CardData cardData, boolean listening) {

        notif_state = listening;

        if(listening) viewHolder.notif_icon.setImageDrawable((ContextCompat.getDrawable(MainActivity.context, R.drawable.ic_bell_full_black)));

        else viewHolder.notif_icon.setImageDrawable((ContextCompat.getDrawable(MainActivity.context, R.drawable.ic_bell_black)));

        viewHolder.btn_notif.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                if (notif_state) {

                    notif_state = false;
                    viewHolder.notif_icon.setImageDrawable((ContextCompat.getDrawable(MainActivity.context, R.drawable.ic_bell_black)));

                } else {

                    notif_state = true;
                    viewHolder.notif_icon.setImageDrawable((ContextCompat.getDrawable(MainActivity.context, R.drawable.ic_bell_full_black)));

                }

                ParseRequest.set_cardSubscription(context, cardData.getCard_id(), notif_state);

            }
        });

    }


    @Override
    public void onBindViewHolder(final CommentViewHolder viewHolder, final int position) {

        if(position == 0) {

            set_Parent(context, headerCardData, viewHolder);

            // ID
            String pict_URL = headerCardData.get_user_pict_url();
            if (!pict_URL.equals("NULL")) Picasso.with(MainActivity.context).load(pict_URL).into(viewHolder.profil_pic);
            else viewHolder.profil_pic.setImageDrawable(ContextCompat.getDrawable(MainActivity.context, R.drawable.profil_pict));

            viewHolder.tv_username.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
            viewHolder.tv_username.setText(headerCardData.getAuthor_full_name());

            // GPS
            Geocoder gcd = new Geocoder(MainActivity.context, Locale.getDefault());

            try {

                // limite la precision
                BigDecimal bd = new BigDecimal(headerCardData.getLatitude()).setScale(3, RoundingMode.HALF_EVEN);
                float lat = (float) bd.doubleValue();

                List<Address> addresses = gcd.getFromLocation(lat, headerCardData.getLongitude(), 1);

                if (addresses.size() > 0)

                    viewHolder.tv_localisation.setText(addresses.get(0).getLocality() + ", " + addresses.get(0).getCountryName());

                else viewHolder.row_gps.setVisibility(View.INVISIBLE);

            } catch (IOException e) {

                viewHolder.row_gps.setVisibility(View.INVISIBLE);
                e.printStackTrace();
            }


            // PICT
            if(headerCardData.get_pict_url().isEmpty())

                viewHolder.image_placeholder.setVisibility(View.GONE);

            else {

                WindowManager wm = (WindowManager) SubActivity.context.getSystemService(Context.WINDOW_SERVICE);
                Display display = wm.getDefaultDisplay();
                Point size = new Point();
                display.getSize(size);
                int width = size.x;

                viewHolder.image_placeholder.setVisibility(View.VISIBLE);
                Picasso.with(MainActivity.context)
                        .load(headerCardData.get_pict_url())
                        .resize(width, 0)
                        .into(viewHolder.image_placeholder);
            }

            // MAIN CONTENT
            if(headerCardData.getParent_card_id().isEmpty() && headerCardData.get_pict_url().isEmpty()) {

                viewHolder.tv_main_content.setTextSize(TextSize.scale(CardData.MAX_THOUGHT_LENGTH - headerCardData.getMain_content().length(), false));


            } else if(!headerCardData.get_pict_url().isEmpty()){

                viewHolder.tv_main_content.setTextSize(16);

                if(headerCardData.getMain_content().isEmpty()) viewHolder.tv_main_content.setVisibility(View.GONE);
            }

            viewHolder.tv_main_content.setText(headerCardData.getMain_content());


            // URL
            final String header_url = headerCardData.getLink_url();

            if(headerCardData.getLink_url().isEmpty()) viewHolder.btn_link.setVisibility(View.GONE);

            else {
                viewHolder.btn_link.setVisibility(View.VISIBLE);
                viewHolder.tv_link.setText(headerCardData.getLink_url());

                viewHolder.btn_link.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        try {

                            Uri uri = Uri.parse(header_url); // missing 'http://' will cause crashed
                            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                            SubActivity.context.startActivity(intent);

                        } catch (ActivityNotFoundException e) {

                        }
                    }
                });
            }

            viewHolder.tv_timestamp.setText(headerCardData.getTimestamp());
            viewHolder.nb_upvotes.setText(String.valueOf(headerCardData.getNb_upvotes()));
            viewHolder.nb_replies.setText(String.valueOf(headerCardData.getNb_replies()));

            viewHolder.icone_upvote.setImageAlpha(100);

            set_header_Tags(viewHolder, headerCardData.getTags_list());

            set_header_Buttons(viewHolder, headerCardData);

        } else {

            // ID
            String pict_URL = repliesList.get(position).get_user_pict_url();

            if (!pict_URL.equals("NULL")) Picasso.with(MainActivity.context).load(pict_URL).into(viewHolder.profil_pic);
            else viewHolder.profil_pic.setImageDrawable(ContextCompat.getDrawable(MainActivity.context, R.drawable.profil_pict));

            viewHolder.tv_username.setTypeface(MainActivity.tfSemiBold);
            viewHolder.tv_username.setText(repliesList.get(position).getAuthor_full_name());

            // GPS
            if(repliesList.get(position).getLocalisation().equals(""))

                viewHolder.row_gps.setVisibility(View.INVISIBLE);

            else viewHolder.tv_localisation.setText(repliesList.get(position).getLocalisation());

            // CHAT or STORY
            if (repliesList.get(position).getChat_count() < 3) viewHolder.btn_open_chat.setVisibility(View.GONE);

            else {

                viewHolder.chat_count.setText(String.valueOf(repliesList.get(position).getChat_count()));

                // TODO changer icone chat/story
                if(repliesList.get(position).isStory())

                    viewHolder.ic_story_chat.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_script_black_48dp));

                else viewHolder.ic_story_chat.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_conversation_black2));

                viewHolder.btn_open_chat.setVisibility(View.VISIBLE);
            }

            // image
            if (repliesList.get(position).get_pict_url().isEmpty())

                viewHolder.square_placeholder.setVisibility(View.GONE);

            else {

                viewHolder.square_placeholder.setVisibility(View.VISIBLE);
                Picasso.with(context)
                        .load(repliesList.get(position).get_pict_url())
                        .fit().centerCrop()
                        .into(viewHolder.image_placeholder);
            }

            // main text
            if (repliesList.get(position).getMain_content().isEmpty())

                viewHolder.tv_main_content.setVisibility(View.GONE);

            else {

                viewHolder.tv_main_content.setVisibility(View.VISIBLE);
                viewHolder.tv_main_content.setText(repliesList.get(position).getMain_content());
            }


            viewHolder.nb_upvotes.setText(String.valueOf(repliesList.get(position).getNb_upvotes()));
            viewHolder.nb_replies.setText(String.valueOf(repliesList.get(position).getNb_replies()));

            // link
            final String link = repliesList.get(position).getLink_url();

            if (link.isEmpty()) viewHolder.btn_link.setVisibility(View.GONE);

            else {

                viewHolder.tv_link.setText(repliesList.get(position).getLink_url());
                viewHolder.btn_link.setVisibility(View.VISIBLE);

                viewHolder.btn_link.setOnClickListener(new View.OnClickListener() {
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

            if (!repliesList.get(position).getAuthor_id()
                    .equals(((MainActivity) MainActivity.context)
                            .localData.getUserData(SharedPref.KEY_USER_ID))) {

                (viewHolder).btn_profil.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        SubActivity.openUserProfil(
                                context,
                                repliesList.get(position).getAuthor_full_name(),
                                repliesList.get(position).getAuthor_id(),
                                repliesList.get(position).get_user_pict_url(),
                                false);
                    }
                });
            }


            viewHolder.btn_open_chat.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    SubActivity.openChat(context, repliesList.get(position).getChat_id(), repliesList.get(position).isStory(), "");

                    //SubActivity.openChat(context, repliesList.get(position).getChat_id(), false, "");
                }
            });


            viewHolder.comment_content.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    SubActivity.openCard(context, repliesList.get(position), true, false);

                }
            });

            viewHolder.btn_mini_upvote.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    ParseRequest.check_and_insert_upvote(
                            context,
                            repliesList.get(position).getCard_id(),
                            ((MainActivity) MainActivity.context).localData.getUserData(SharedPref.KEY_USER_ID),
                            repliesList.get(position).getAuthor_id(),
                            ((MainActivity) MainActivity.context).localData.getUserData(SharedPref.KEY_FULL_NAME),
                            repliesList.get(position).getMain_content()
                    );

                    viewHolder.nb_upvotes.setText(""+(repliesList.get(position).getNb_upvotes()+1));

                }
            });

            viewHolder.btn_mini_comment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    SubActivity.openCard(context, repliesList.get(position), true, true);

                }
            });


            viewHolder.btn_show_all_comments.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SubActivity.openCard(context, repliesList.get(position), true, false);
                }
            });


            if (repliesList.get(position).getNb_replies() != 0)

                viewHolder.btn_mini_show_comments.setVisibility(View.VISIBLE);

            else viewHolder.btn_mini_show_comments.setVisibility(View.GONE);

            viewHolder.btn_mini_show_comments.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (((TextView) (viewHolder).btn_mini_show_comments.getChildAt(0)).getText().equals("show comments")) {

                        show_subcomments(true, repliesList.get(position).getCard_id(), viewHolder, repliesList.get(position).getNb_replies());


                    } else if (((TextView) (viewHolder).btn_mini_show_comments.getChildAt(0)).getText().equals("hide comments")) {

                        show_subcomments(false, repliesList.get(position).getCard_id(), viewHolder, repliesList.get(position).getNb_replies());

                    }
                }
            });
        }
    }

    private void show_subcomments(boolean show, String commentID, CommentViewHolder viewHolder, int nb_subcomments) {

        //Message.message(context, "commentID : " + commentID);

        if(show) {

            viewHolder.loader_subcomments.setVisibility(View.VISIBLE);
            viewHolder.btn_mini_show_comments.setVisibility(View.GONE);

            ParseRequest.get_subComments(context, commentID, viewHolder, nb_subcomments);

        } else {

            viewHolder.subcomments_frame.setVisibility(View.GONE);
            ((TextView)(viewHolder).btn_mini_show_comments.getChildAt(0)).setText("show comments");

        }
    }

    @Override
    public int getItemCount() {
        return repliesList.size();
    }


    public class CommentViewHolder extends RecyclerView.ViewHolder {

        // parent comment header only
        @Nullable @Bind(R.id.parent_indicator) LinearLayout parent_indicator;
        @Nullable @Bind(R.id.parent_author_name) TextView parent_author_name;
        @Nullable @Bind(R.id.parent_pre) TextView parent_pre;
        @Nullable @Bind(R.id.parent_post) TextView parent_post;

        @Nullable @Bind(R.id.btn_show_all_comments) LinearLayout btn_show_all_comments; // not in header

        @Nullable @Bind(R.id.ic_story_chat) ImageView ic_story_chat;

        @Bind(R.id.btn_profil) LinearLayout btn_profil;
        @Bind(R.id.profil_pic) ImageView profil_pic;

        @Nullable @Bind(R.id.subcomments_frame) public LinearLayout subcomments_frame;  // not in header
        @Nullable @Bind(R.id.loader_subcomments) public ProgressBar loader_subcomments; // not in header

        @Nullable @Bind(R.id.chat_count) TextView chat_count; // not in header

        @Bind(R.id.row_gps) LinearLayout row_gps;
        @Bind(R.id.tv_localisation) TextView tv_localisation;

        @Nullable @Bind(R.id.header_tags_row) public RowLayout header_tags_row; // not in comment



        @Nullable @Bind(R.id.notif_icon) ImageView notif_icon; // not in comment
        @Nullable @Bind(R.id.indicator_post_comment) TextView indicator_post_comment; // not in comment
        @Nullable @Bind(R.id.btn_notif) LinearLayout btn_notif; // not in comment
        @Nullable @Bind(R.id.btn_copy_content) LinearLayout btn_copy_content; // not in comment
        @Nullable @Bind(R.id.tv_timestamp) TextView tv_timestamp; // not in comment
        @Nullable @Bind(R.id.tag_footer) LinearLayout tag_footer; // not in comment

        @Bind(R.id.tv_username) TextView tv_username;
        @Nullable @Bind(R.id.square_placeholder) SquareRelativeLayout square_placeholder; // not in header
        @Bind(R.id.image_placeholder) ImageView image_placeholder;
        @Bind(R.id.tv_main_content) TextView tv_main_content;
        @Bind(R.id.nb_upvotes) TextView nb_upvotes;
        @Bind(R.id.nb_replies) TextView nb_replies;
        @Bind(R.id.icone_upvote) ImageView icone_upvote;
        @Nullable @Bind(R.id.comment_content) RelativeLayout comment_content; // not in header

        @Bind(R.id.btn_link) LinearLayout btn_link;
        @Bind(R.id.tv_link) TextView tv_link;


        @Bind(R.id.btn_mini_upvote) LinearLayout btn_mini_upvote;
        @Bind(R.id.btn_mini_comment) LinearLayout btn_mini_comment;
        @Nullable @Bind(R.id.btn_mini_show_comments) public LinearLayout btn_mini_show_comments; // not in header

        @Nullable @Bind(R.id.btn_open_chat) LinearLayout btn_open_chat; // not in header

        public CommentViewHolder(View itemView) {

            super(itemView);

            ButterKnife.bind(this, itemView);

            tv_main_content.setTypeface(MainActivity.tf_serif);

            tv_username.setTypeface(MainActivity.tfSemiBold);

            /*
            tv_username.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    SubActivity.open(getActivity(), SubActivity.TYPE_PROFIL,"null");
                }
            });
            */

        }
    }
}
