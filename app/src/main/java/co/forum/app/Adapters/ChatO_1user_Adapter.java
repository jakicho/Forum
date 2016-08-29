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
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.Collections;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import co.forum.app.Data.ChatOverviewData;
import co.forum.app.Data.ParseRequest;
import co.forum.app.MainActivity;
import co.forum.app.R;
import co.forum.app.SharedPref;
import co.forum.app.SubActivity.SubActivity;
import co.forum.app.tools.SquareRelativeLayout;

public class ChatO_1user_Adapter extends RecyclerView.Adapter<ChatO_1user_Adapter.ChatOverViewHolder>{

    private LayoutInflater inflater;

    private Context context;

    private String profilID;

    private String profilUserName;

    public List<ChatOverviewData> data = Collections.emptyList();


    public ChatO_1user_Adapter(Context context, List<ChatOverviewData> ChatOverviewList, String profilID, String profilUserName) {

        inflater = LayoutInflater.from(context);

        this.context = context;

        this.data = ChatOverviewList;

        this.profilID = profilID;

        this.profilUserName = profilUserName;

    }



    @Override
    public ChatOverViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = inflater.inflate(R.layout.item_chat_1user, parent, false);

        return new ChatOverViewHolder(view);
    }


    @Override
    public void onBindViewHolder(final ChatOverViewHolder holder, final int position) {

        String pict_URL = data.get(position).getOther_user_pict();

        set_open_chat_click(holder, position);

        set_profil_icon(holder, pict_URL);

        set_open_other_profil(holder, position);

        holder.tv_chat_count.setText(String.valueOf(data.get(position).getNb_messages()));

        if(data.get(position).getFirstTag().isEmpty()) holder.tag_topic.setVisibility(View.GONE);

        else {

            holder.tag_topic.setText("#"+data.get(position).getFirstTag());
            holder.tag_topic.setVisibility(View.VISIBLE);

        }

        // notif icon
        if(!isMainUserPage()) {

            hide_push_icon(holder);

            holder.notif_new_icon.setVisibility(View.GONE);

            holder.btn_open_chat.setBackground(ContextCompat.getDrawable(context, R.drawable.button_sl_mini_card));

        } else {

            show_push_icon(holder, position);

            set_layout_clicked(holder, position);

            set_tab_red_icon(position);

        }

        set_author_begining_comment(holder, position);

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

                if(!data.get(position).isUptodate()) {

                    ParseRequest.update_chat_checked(data.get(position).getChatID(), data.get(position).getRole());

                    holder.btn_open_chat.setBackground(ContextCompat.getDrawable(context, R.drawable.button_sl_mini_card));

                    holder.notif_new_icon.setVisibility(View.GONE);

                }

            }
        });
    }

    private void set_profil_icon(final ChatOverViewHolder holder, String pict_URL) {

        if(pict_URL == null) pict_URL = "NULL";

        if(!pict_URL.equals("NULL")) Picasso.with(MainActivity.context).load(pict_URL).into(holder.img_chat_icon);

        else holder.img_chat_icon.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.profil_pict));

    }

    private boolean isMainUserPage() {

        if(profilID.equals( ((MainActivity)MainActivity.context).localData.getUserData(SharedPref.KEY_USER_ID) ) )

            return true;

        else return false;
    }

    private void set_open_other_profil(final ChatOverViewHolder holder, final int position) {

        if( !data.get(position).getOther_user_id()
                .equals( ((MainActivity)MainActivity.context).localData.getUserData(SharedPref.KEY_USER_ID)) ) {

            holder.btn_other_profil_icon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    SubActivity.openUserProfil(context,
                            data.get(position).getOther_username(),
                            data.get(position).getOther_user_id(),
                            data.get(position).getOther_user_pict(),
                            false);
                }
            });


        } else holder.btn_other_profil_icon.setOnClickListener(null);

        holder.btn_other_profil_icon.setVisibility(View.VISIBLE);
        holder.tv_other_user.setVisibility(View.VISIBLE);
        holder.tv_other_user.setText(data.get(position).getOther_username());

    }

    private void hide_push_icon(final ChatOverViewHolder holder) {

        holder.no_push_icon.setVisibility(View.VISIBLE);
        holder.notif_push_icon.setVisibility(View.GONE);
        holder.btn_notif.setVisibility(View.GONE);

    }

    private void show_push_icon(final ChatOverViewHolder holder, final int position) {

        holder.no_push_icon.setVisibility(View.GONE);
        holder.notif_push_icon.setVisibility(View.VISIBLE);
        holder.btn_notif.setVisibility(View.VISIBLE);

        if(data.get(position).isSubscribing())
            holder.notif_push_icon.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_bell_full_black));
        else holder.notif_push_icon.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_bell_black));
        /*
        data.get(position).getRole()


         */

        holder.btn_notif.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ParseRequest.set_chatSubscription(
                        context,
                        data.get(position).getRole(),
                        data.get(position).getChatID(),
                        !data.get(position).isSubscribing());

                if(data.get(position).isSubscribing()) {

                    data.get(position).setSubscribing(false);

                    holder.notif_push_icon.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_bell_black));

                } else {

                    data.get(position).setSubscribing(true);

                    holder.notif_push_icon.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_bell_full_black));
                }
            }
        });
    }

    private void set_layout_clicked(final ChatOverViewHolder holder, final int position) {

        if(data.get(position).isUptodate()) {

            holder.notif_new_icon.setVisibility(View.GONE);

            holder.btn_open_chat.setBackground(ContextCompat.getDrawable(context, R.drawable.button_sl_mini_card));

        } else {

            holder.notif_new_icon.setVisibility(View.VISIBLE);

            holder.btn_open_chat.setBackground(ContextCompat.getDrawable(context, R.color.colorCardsIndicator));
        }
    }

    private void set_tab_red_icon(int position) {

        // NEW CHAT NOTIFICATION
        if(position == 0 &&
                !data.get(position).isUptodate() &&
                profilID.equals( ((MainActivity)MainActivity.context).localData.getUserData(SharedPref.KEY_USER_ID)) ){

            ((MainActivity)MainActivity.context).change_tab_chat_icon(false);


            // change icone

        }
    }

    private void set_author_begining_comment(final ChatOverViewHolder holder, final int position){

        String pict = "";

        if(data.get(position).getComment().isEmpty()) pict = "[pict] ";

        if((data.get(position).getLastCommenterID()).equals(profilID) && isMainUserPage()) {

            holder.tv_last_comment.setText("You: " + pict + data.get(position).getComment());

        } else if((data.get(position).getLastCommenterID()).equals(profilID) && !isMainUserPage()) {

            holder.tv_last_comment.setText(profilUserName + ": " + pict + data.get(position).getComment());

        } else holder.tv_last_comment.setText(pict + data.get(position).getComment());
    }




    class ChatOverViewHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.btn_open_chat) LinearLayout btn_open_chat;

        @Bind(R.id.img_chat_icon) ImageView img_chat_icon;
        @Bind(R.id.notif_icon) ImageView notif_new_icon;

        @Bind(R.id.btn_other_profil_icon) RelativeLayout btn_other_profil_icon;

        @Bind(R.id.tv_other_user) TextView tv_other_user;
        @Bind(R.id.tv_chat_count) TextView tv_chat_count;

        @Bind(R.id.tag_topic) TextView tag_topic;

        @Bind(R.id.tv_main_user) TextView tv_main_user;
        @Bind(R.id.tv_last_comment) TextView tv_last_comment;

        @Bind(R.id.tv_last_timestamp) TextView tv_last_timestamp;

        @Bind(R.id.no_push_icon) FrameLayout no_push_icon;
        @Bind(R.id.btn_notif) SquareRelativeLayout btn_notif;
        @Bind(R.id.notif_push_icon) ImageView notif_push_icon;


        public ChatOverViewHolder(View itemView) {

            super(itemView);

            ButterKnife.bind(this, itemView);

            tv_other_user.setTypeface(MainActivity.tfSemiBold);

            tv_main_user.setTypeface(MainActivity.tf);

            tv_last_comment.setTypeface(MainActivity.tf_serif);
        }
    }
}
