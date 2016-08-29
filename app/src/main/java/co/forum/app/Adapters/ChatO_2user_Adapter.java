package co.forum.app.Adapters;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import co.forum.app.R;
import co.forum.app.SubActivity.SubActivity;

public class ChatO_2user_Adapter extends RecyclerView.Adapter<ChatO_2user_Adapter.ChatOverViewHolder>{

    private LayoutInflater inflater;

    private Context context;

    public List<ChatOverviewData> data = Collections.emptyList();


    public ChatO_2user_Adapter(Context context, List<ChatOverviewData> ChatOverviewList) {

        inflater = LayoutInflater.from(context);

        this.context = context;

        this.data = ChatOverviewList;

    }



    @Override
    public ChatOverViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = inflater.inflate(R.layout.item_chat_2user, parent, false);

        return new ChatOverViewHolder(view);
    }


    @Override
    public void onBindViewHolder(final ChatOverViewHolder holder, final int position) {

        set_open_chat_click(holder, position);

        set_profil_icon(holder, data.get(position).getStarter_pict(), data.get(position).getReplier_pict());

        holder.tv_users.setText(data.get(position).getStarter_name() + " / " + data.get(position).getReplier_name());

        holder.tv_last_comment.setText(data.get(position).getComment());

        holder.tv_chat_count.setText(String.valueOf(data.get(position).getNb_messages()));

        if(data.get(position).getFirstTag().isEmpty()) holder.tag_topic.setVisibility(View.GONE);

        else {

            holder.tag_topic.setText("#"+data.get(position).getFirstTag());
            holder.tag_topic.setVisibility(View.VISIBLE);

        }

        holder.tv_last_timestamp.setText(data.get(position).getLast_timestamp());

    }

    @Override
    public int getItemCount() {
        return data.size();
    }



    private void set_open_chat_click(final ChatOverViewHolder holder, final int position) {

        holder.btn_open_chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SubActivity.openChat(context, data.get(position).getChatID(), false, "");

            }
        });
    }

    private void set_profil_icon(final ChatOverViewHolder holder, String pict_URL_1, String pict_URL_2) {

        if(pict_URL_1 == null) pict_URL_1 = "NULL";

        if(!pict_URL_1.equals("NULL")) Picasso.with(MainActivity.context).load(pict_URL_1).into(holder.img_profil_1);

        else holder.img_profil_1.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.profil_pict));

        if(pict_URL_2 == null) pict_URL_2 = "NULL";

        if(!pict_URL_2.equals("NULL")) Picasso.with(MainActivity.context).load(pict_URL_2).into(holder.img_profil_2);

        else holder.img_profil_2.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.profil_pict));

    }

    class ChatOverViewHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.btn_open_chat) LinearLayout btn_open_chat;

        @Bind(R.id.img_profil_1) ImageView img_profil_1;
        @Bind(R.id.img_profil_2) ImageView img_profil_2;

        @Bind(R.id.tv_users) TextView tv_users;
        @Bind(R.id.tv_last_timestamp) TextView tv_last_timestamp;

        @Bind(R.id.tv_last_comment) TextView tv_last_comment;

        @Bind(R.id.tv_chat_count) TextView tv_chat_count;

        @Bind(R.id.tag_topic) TextView tag_topic;

        public ChatOverViewHolder(View itemView) {

            super(itemView);

            ButterKnife.bind(this, itemView);

            tv_users.setTypeface(MainActivity.tfSemiBold);

            tv_last_comment.setTypeface(MainActivity.tf_serif);
        }
    }
}
