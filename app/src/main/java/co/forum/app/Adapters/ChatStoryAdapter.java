package co.forum.app.Adapters;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
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
import co.forum.app.SubActivity.StoryFrag;
import co.forum.app.SubActivity.SubActivity;
import co.forum.app.tools.Message;

public class ChatStoryAdapter extends RecyclerView.Adapter<ChatStoryAdapter.ChatHolder> {

    private LayoutInflater inflater;

    private Context context;

    private String story_title;

    private String authorID;

    private String chatID;

    private int user_role;

    public List<CardData> chat_list = Collections.emptyList();

    public ChatStoryAdapter(Context context, String chatID, List<CardData> listOfCards, String story_title,  String authorID, int user_role) {

        inflater = LayoutInflater.from(context);

        this.context = context;

        this.chatID = chatID;

        this.authorID = authorID;

        this.chat_list = listOfCards;

        this.story_title = story_title;

        this.user_role = user_role;

    }

    @Override
    public ChatHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view;

        if(viewType == 0 && ( (user_role == StoryFrag.AUTHOR) || !story_title.isEmpty()))

            view = inflater.inflate(R.layout.item_card_story_title, parent, false);

        else  view = inflater.inflate(R.layout.item_card_story, parent, false);

        return new ChatHolder(view);
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public void onBindViewHolder(final ChatHolder holder, final int position) {

        /*
        position 0
        cas 1 : pas de titre, spectator => ne rien mettre
        cas 2 : pas de titre, author => empty avec hint clickable
        cas 3 : titre, spectator => titre, non clickable
        cas 4 : titre, author => titre, clickable
         */

        if(position == 0 && ( (!story_title.isEmpty()) || (user_role == StoryFrag.AUTHOR)  ) ) {

            holder.tv_main_content.setTypeface(MainActivity.tfBold);

            if(user_role == StoryFrag.AUTHOR) {

                if(story_title.isEmpty()) {

                    holder.tv_main_content.setText("Set a title (optional)");

                    holder.tv_main_content.setAlpha(0.2f);

                } else holder.tv_main_content.setText(chat_list.get(position).getMain_content());

                holder.edit_title.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        String yes_button = "Add Title";
                        String no_button = "Cancel";

                        new MaterialDialog.Builder(context)
                                .inputType(InputType.TYPE_CLASS_TEXT)
                                .positiveText(yes_button)
                                .negativeText(no_button)

                                .input("Set a title for your story", story_title, new MaterialDialog.InputCallback() {
                                    @Override
                                    public void onInput(MaterialDialog dialog, CharSequence input) {

                                        if (input.length() != 0) {
                                            holder.tv_main_content.setText(input.toString());
                                            holder.tv_main_content.setAlpha(1f);

                                        } else {
                                            holder.tv_main_content.setText("Set a title (optional)");
                                            holder.tv_main_content.setAlpha(0.2f);
                                        }

                                        story_title = input.toString();

                                        ParseRequest.update_story_title(chatID, input.toString());

                                    }
                                })
                                .show();


                    }
                });

            } else holder.tv_main_content.setText(chat_list.get(position).getMain_content());

        } else {

            holder.tv_main_content.setTypeface(MainActivity.tf_serif);

            holder.tv_main_content.setText(chat_list.get(position).getMain_content());
            holder.nb_upvotes.setText(String.valueOf(chat_list.get(position).getNb_upvotes()));

            // image
            if (!chat_list.get(position).get_pict_url().isEmpty()) {

                ViewGroup.LayoutParams params = holder.scale_for_pict.getLayoutParams();
                params.width = LinearLayout.LayoutParams.MATCH_PARENT;
                holder.scale_for_pict.setLayoutParams(params);

                WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
                Display display = wm.getDefaultDisplay();
                Point size = new Point();
                display.getSize(size);
                int width = size.x;

                Picasso.with(context)
                        .load(chat_list.get(position).get_pict_url())
                        .resize(width, 0)
                        .into(holder.img_placeholder);
                holder.img_placeholder.setVisibility(View.VISIBLE);

                if (chat_list.get(position).getMain_content().isEmpty())
                    holder.tv_main_content.setVisibility(View.GONE);
                else holder.tv_main_content.setVisibility(View.VISIBLE);

            } else {

                holder.img_placeholder.setVisibility(View.GONE);

                holder.tv_main_content.setVisibility(View.VISIBLE);
            }

            // link
            final String link = chat_list.get(position).getLink_url();

            if (link.isEmpty()) holder.btn_link.setVisibility(View.GONE);

            else {

                holder.tv_link.setText(link);
                holder.btn_link.setVisibility(View.VISIBLE);
                holder.btn_link.setOnClickListener(new View.OnClickListener() {
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
            holder.card_story.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SubActivity.openCard(context, chat_list.get(position), true, false);
                }
            });

            // btn upvote
            holder.btn_mini_upvote.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (user_role == StoryFrag.AUTHOR)
                        Message.message(context, "You cannot upvote your own card ^^");

                    else {

                        //Message.message(context, "To upvote, open this card");

                        //Message.message(context, "To comment, open this card");

                        ParseRequest.check_and_insert_upvote(
                                context,
                                chat_list.get(position).getCard_id(),
                                ((MainActivity) MainActivity.context).localData.getUserData(SharedPref.KEY_USER_ID),
                                chat_list.get(position).getAuthor_id(),
                                ((MainActivity) MainActivity.context).localData.getUserData(SharedPref.KEY_FULL_NAME),
                                chat_list.get(position).getMain_content()
                        );

                        holder.nb_upvotes.setText(""+(chat_list.get(position).getNb_upvotes()+1));
                    }
                }
            });

            // btn comment
            if (user_role == StoryFrag.SPECTATOR) {

                int nb_external_comments = chat_list.get(position).getNb_replies() - 1;

                if (nb_external_comments == -1) nb_external_comments = 0;

                holder.nb_replies.setText(String.valueOf(nb_external_comments));

                holder.btn_mini_comment.setVisibility(View.VISIBLE);

                holder.btn_mini_comment.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {

                        Message.message(context, "To comment, open this card");

                    }
                });

            } else {

                if (chat_list.get(position).getNb_replies() >= 2) {

                    holder.nb_replies.setText(String.valueOf(chat_list.get(position).getNb_replies() - 1));

                    holder.btn_mini_comment.setVisibility(View.VISIBLE);

                } //else holder.btn_mini_comment.setVisibility(View.GONE);


                holder.btn_mini_comment.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        //Message.message(context, "comment chat participant");

                        ((SubActivity) context).storyFragment.story_content_panel_structure.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);

                    }
                });
            }
        }
    }

    @Override
    public int getItemCount() {
        return chat_list.size();
    }

    class ChatHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.card_story) LinearLayout card_story;

        @Nullable @Bind(R.id.edit_title) LinearLayout edit_title;

        @Bind(R.id.scale_for_pict) LinearLayout scale_for_pict;

        @Bind(R.id.btn_link) LinearLayout btn_link;

        @Bind(R.id.tv_link) TextView tv_link;

        @Bind(R.id.img_placeholder) ImageView img_placeholder;

        @Bind(R.id.tv_main_content) TextView tv_main_content;

        @Bind(R.id.nb_upvotes) TextView nb_upvotes;

        @Bind(R.id.btn_mini_upvote) LinearLayout btn_mini_upvote;

        @Bind(R.id.nb_replies) TextView nb_replies;

        @Bind(R.id.btn_mini_comment) LinearLayout btn_mini_comment;

        public ChatHolder(View itemView) {

            super(itemView);

            ButterKnife.bind(this, itemView);

            tv_main_content.setTypeface(MainActivity.tf_serif);

        }
    }
}
