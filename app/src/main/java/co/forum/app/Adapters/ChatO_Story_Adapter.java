package co.forum.app.Adapters;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.Collections;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import co.forum.app.Data.ChatOverviewData;
import co.forum.app.MainActivity;
import co.forum.app.MainSections.ProfilPages.ChatOFrag;
import co.forum.app.R;
import co.forum.app.SharedPref;
import co.forum.app.SubActivity.SubActivity;

public class ChatO_Story_Adapter extends RecyclerView.Adapter<ChatO_Story_Adapter.StoryViewHolder> {

    private LayoutInflater inflater;

    private Context context;

    private String profilID;

    private String profilUserName;

    private int chat_type;

    public List<ChatOverviewData> data = Collections.emptyList();


    public ChatO_Story_Adapter(Context context, List<ChatOverviewData> ChatOverviewList, String profilID, String profilUserName, int chat_type) {

        inflater = LayoutInflater.from(context);

        this.context = context;

        this.data = ChatOverviewList;

        this.profilID = profilID;

        this.profilUserName = profilUserName;

        this.chat_type = chat_type;

    }

    @Override
    public StoryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = inflater.inflate(R.layout.item_chat_story, parent, false);

        return new StoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ChatO_Story_Adapter.StoryViewHolder holder, int position) {

        set_open_story_click(holder, position);

        // Header Author
        if(chat_type == ChatOFrag.STORIES_O) {

            String pict_URL = data.get(position).getOther_user_pict();

            set_profil_icon(holder, pict_URL);

            set_open_other_profil(holder, position);

            holder.tv_last_timestamp_header.setText(data.get(position).getLast_timestamp());

        } else holder.tv_last_timestamp_body.setText(data.get(position).getLast_timestamp());


        // Pict
        if(!data.get(position).getFirst_pict().isEmpty()) {

            Picasso.with(MainActivity.context)
                    .load(data.get(position)
                            .getFirst_pict())
                    .fit().centerCrop()
                    .into(holder.first_pict);

            holder.first_pict.setVisibility(View.VISIBLE);

            holder.no_header.setVisibility(View.GONE);

        } else holder.first_pict.setVisibility(View.GONE);


        // Title
        if(data.get(position).getStory_title() == null || data.get(position).getStory_title().isEmpty()) {

            holder.tv_story_title.setVisibility(View.GONE);

        } else {

            holder.tv_story_title.setText(data.get(position).getStory_title());

            holder.tv_story_title.setVisibility(View.VISIBLE);

        }

        // Excerpt
        if(data.get(position).getComment().isEmpty()) {

            holder.tv_story_excerpt.setVisibility(View.GONE);

        } else {

            holder.tv_story_excerpt.setText(data.get(position).getComment());

            holder.tv_story_excerpt.setVisibility(View.VISIBLE);
        }

        if(data.get(position).getFirstTag().isEmpty()) holder.tag_topic.setVisibility(View.GONE);

        else {

            holder.tag_topic.setText("#"+data.get(position).getFirstTag());
            holder.tag_topic.setVisibility(View.VISIBLE);

        }


        holder.tv_lenght_count.setText(String.valueOf(data.get(position).getNb_messages()));

    }

    @Override
    public int getItemCount() {
        return data.size();
    }




    private void set_open_story_click(final StoryViewHolder holder, final int position) {

        holder.btn_open_story.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SubActivity.openChat(context, data.get(position).getChatID(), true, data.get(position).getStory_title());

            }
        });
    }


    private void set_profil_icon(final StoryViewHolder holder, String pict_URL) {

        if(pict_URL == null || pict_URL.isEmpty()) pict_URL = "NULL";

        if(!pict_URL.equals("NULL")) Picasso.with(MainActivity.context).load(pict_URL).into(holder.img_profil_icon);

        else holder.img_profil_icon.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.profil_pict));

    }


    private void set_open_other_profil(final StoryViewHolder holder, final int position) {

        if( !data.get(position).getOther_user_id()
                .equals( ((MainActivity)MainActivity.context).localData.getUserData(SharedPref.KEY_USER_ID)) ) {

            holder.btn_other_profil.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    SubActivity.openUserProfil(context,
                            data.get(position).getOther_username(),
                            data.get(position).getOther_user_id(),
                            data.get(position).getOther_user_pict(),
                            false);
                }
            });


        } else holder.btn_other_profil.setOnClickListener(null);

        holder.tv_other_user.setText(data.get(position).getOther_username());

    }



    class StoryViewHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.btn_open_story) LinearLayout btn_open_story;

        @Bind(R.id.no_header) FrameLayout no_header;

        @Bind(R.id.btn_other_profil) LinearLayout btn_other_profil;

        @Bind(R.id.img_profil_icon) ImageView img_profil_icon;

        @Bind(R.id.tv_other_user) TextView tv_other_user;

        @Bind(R.id.tag_topic) TextView tag_topic;

        @Bind(R.id.tv_last_timestamp_header) TextView tv_last_timestamp_header;

        @Bind(R.id.tv_last_timestamp_body) TextView tv_last_timestamp_body;

        @Bind(R.id.first_pict) ImageView first_pict;

        @Bind(R.id.tv_story_title) TextView tv_story_title;

        @Bind(R.id.tv_story_excerpt) TextView tv_story_excerpt;

        @Bind(R.id.tv_lenght_count) TextView tv_lenght_count;

        public StoryViewHolder(View itemView) {

            super(itemView);

            ButterKnife.bind(this, itemView);

            tv_other_user.setTypeface(MainActivity.tfSemiBold);

            tv_story_title.setTypeface(MainActivity.tfBold);

            tv_story_excerpt.setTypeface(MainActivity.tf_serif);


            if(chat_type == ChatOFrag.PROFIL_U_STORIES || chat_type == ChatOFrag.PROFIL_O_STORIES) {

                btn_other_profil.setVisibility(View.GONE);

                tv_last_timestamp_body.setVisibility(View.VISIBLE);

                no_header.setVisibility(View.VISIBLE);

            } else if(chat_type == ChatOFrag.STORIES_O) {

                btn_other_profil.setVisibility(View.VISIBLE);

                tv_last_timestamp_body.setVisibility(View.GONE);

                no_header.setVisibility(View.GONE);
            }
        }
    }
}
