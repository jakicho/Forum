package co.forum.app.Adapters;

import android.content.Context;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.Collections;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import co.forum.app.MainActivity;
import co.forum.app.SubActivity.SubActivity;
import co.forum.app.Data.CardData;
import co.forum.app.R;
import co.forum.app.tools.Message;
import co.forum.app.tools.TextSize;

public class MiniCardAdapter extends RecyclerView.Adapter<MiniCardAdapter.CardHolder>{

    private Context context;

    private LayoutInflater inflater;

    private boolean isComment = false;

    List<CardData> data = Collections.emptyList();

    public MiniCardAdapter(Context context, List<CardData> data, boolean isComment) {

        inflater = LayoutInflater.from(context);

        this.context = context;

        this.isComment = isComment;

        this.data = data;

    }

    @Override
    public CardHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view;

        if(!isComment) view = inflater.inflate(R.layout.item_card_mini, parent, false);
        else view = inflater.inflate(R.layout.item_comment_mini, parent, false);

        CardHolder holder = new CardHolder(view);

        return holder;

    }

    @Override
    public void onBindViewHolder(final CardHolder cardHolder, final int position) {

        //if(isBookmarks) cardHolder.tv_parent_author.setText(data.get(position).getAuthor_full_name());

        if(isComment) {

            if(!data.get(position).get_pict_url().isEmpty()) {

                cardHolder.tv_main_content.setVisibility(View.GONE);

                cardHolder.frame_placeholder.setVisibility(View.VISIBLE);

                Picasso.with(MainActivity.context)
                        .load(data.get(position).get_pict_url())
                        .fit().centerCrop()
                        .into(cardHolder.img_placeholder);

            } else {

                cardHolder.frame_placeholder.setVisibility(View.GONE);
                cardHolder.tv_main_content.setVisibility(View.VISIBLE);
                cardHolder.tv_main_content.setText(data.get(position).getMain_content());

            }

        } else {

            if(!data.get(position).get_pict_url().isEmpty()) {

                cardHolder.tv_main_content.setVisibility(View.INVISIBLE);

                cardHolder.frame_placeholder.setVisibility(View.VISIBLE);

                Picasso.with(MainActivity.context)
                        .load(data.get(position).get_pict_url())
                        .fit().centerCrop()
                        .into(cardHolder.img_placeholder);

                cardHolder.icone_comment.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_comment_blue));
                cardHolder.icone_thumb.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_thumb_up_blue));
                cardHolder.tv_nb_upvotes.setTextColor(ContextCompat.getColor(context, R.color.colorAccent));
                cardHolder.tv_nb_replies.setTextColor(ContextCompat.getColor(context, R.color.colorAccent));

            } else {

                cardHolder.frame_placeholder.setVisibility(View.GONE);
                cardHolder.tv_main_content.setVisibility(View.VISIBLE);
                cardHolder.tv_main_content.setText(data.get(position).getMain_content());

                cardHolder.icone_comment.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_comment_black));
                cardHolder.icone_thumb.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_thumb_up_black));
                cardHolder.tv_nb_upvotes.setTextColor(ContextCompat.getColor(context, R.color.colorProfil_Tabs_Off));
                cardHolder.tv_nb_replies.setTextColor(ContextCompat.getColor(context, R.color.colorProfil_Tabs_Off));

            }

        }





        cardHolder.tv_nb_upvotes.setText(String.valueOf(data.get(position).getNb_upvotes()));
        cardHolder.tv_nb_replies.setText(String.valueOf(data.get(position).getNb_replies()));

        if(isComment) cardHolder.tv_parent_author.setText(data.get(position).getParent_author_full_name());

        if(!data.get(position).getParent_card_id().isEmpty())
            cardHolder.reply_indicator.setVisibility(View.VISIBLE);

        else cardHolder.reply_indicator.setVisibility(View.GONE);

        cardHolder.tv_main_content.setTextSize(TextSize.miniScale(data.get(position).getMain_content().length()));

        cardHolder.btn_mini_card.setOnClickListener(new View.OnClickListener() {

            class open_card implements Runnable{

                @Override
                public void run() {

                    SubActivity.openCard(context, data.get(position), false, false);

                }
            }

            @Override
            public void onClick(View view) {

                cardHolder.mini_card_loader.setVisibility(View.VISIBLE);


                final Handler handler = new Handler();

                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        cardHolder.mini_card_loader.setVisibility(View.INVISIBLE);
                    }
                }, 1500);

                Thread myThread = new Thread(new open_card());

                myThread.start();


            }
        });

    }

    @Override
    public int getItemCount() {
        return data.size();
    }


    class CardHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.btn_mini_card) Button btn_mini_card;
        @Bind(R.id.mini_card_loader) ProgressBar mini_card_loader;

        @Bind(R.id.frame_placeholder) FrameLayout frame_placeholder;
        @Bind(R.id.img_placeholder) ImageView img_placeholder;
        @Bind(R.id.text_placeholder) LinearLayout text_placeholder;
        @Bind(R.id.reply_indicator) LinearLayout reply_indicator;
        @Bind(R.id.tv_parent_author) TextView tv_parent_author;
        @Bind(R.id.btn_notif) FrameLayout bookmark;
        @Bind(R.id.tv_main_content) TextView tv_main_content;

        @Bind(R.id.icone_thumb) ImageView icone_thumb;
        @Bind(R.id.icone_comment) ImageView icone_comment;
        @Bind(R.id.tv_nb_upvotes) TextView tv_nb_upvotes;
        @Bind(R.id.tv_nb_replies) TextView tv_nb_replies;

        public CardHolder(View itemView) {

            super(itemView);

            ButterKnife.bind(this, itemView);

            if(isComment) tv_parent_author.setVisibility(View.VISIBLE);
            else tv_parent_author.setVisibility(View.GONE);
            bookmark.setVisibility(View.GONE);

            tv_main_content.setTypeface(MainActivity.tf_serif);

            bookmark.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Message.message(context, "bookmark click");
                }
            });

            tv_parent_author.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Message.message(context, "author click");
                }
            });

        }
    }
}
