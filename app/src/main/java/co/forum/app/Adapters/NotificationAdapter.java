package co.forum.app.Adapters;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.Collections;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import co.forum.app.Data.NotifData;
import co.forum.app.Data.ParseRequest;
import co.forum.app.MainActivity;
import co.forum.app.R;
import co.forum.app.SharedPref;
import co.forum.app.SubActivity.SubActivity;
import co.forum.app.tools.CustomTypefaceSpan;


public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.NotificationHolder> {

    private LayoutInflater inflater;

    private Context context;

    public List<NotifData> notifDataList = Collections.emptyList();

    int row_opened, row_comment, row_upvote;


    public NotificationAdapter(Context context, List<NotifData> listOfNotifications) {

        inflater = LayoutInflater.from(context);

        this.context = context;

        this.notifDataList = listOfNotifications;

        row_opened = ContextCompat.getColor(context, R.color.notif_row_bg_open);

        row_comment = ContextCompat.getColor(context, R.color.notif_row_bg_new_comment);

        row_upvote = ContextCompat.getColor(context, R.color.notif_row_bg_new_upvote);

    }

    @Override
    public NotificationHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = inflater.inflate(R.layout.item_notification, parent, false);

        NotificationHolder holder = new NotificationHolder(view);

        return holder;
    }


    @Override
    public void onBindViewHolder(final NotificationAdapter.NotificationHolder viewHolder, final int index) {

        setContent(viewHolder,
                notifDataList.get(index).card_content,
                notifDataList.get(index).notif_type,
                index,
                notifDataList.get(index).sender_name,
                notifDataList.get(index).senderProfilURL);

        setColor(viewHolder, index);

        setClick(viewHolder, index);

    }


    @Override
    public int getItemCount() {
        return notifDataList.size();
    }


    private String reduceContent(String content) {

        String content60;

        if (content.length() > 60) content60 = content.substring(0, 60) + "...";

        else content60 = content;

        return content60;

    }


    private SpannableStringBuilder setTextStyle(String notificationText, String senderName, int excerptLength) {

        SpannableStringBuilder SpanBuilder = new SpannableStringBuilder(notificationText);

        int beginFirstBold = 0;

        int endFirstBold = senderName.length() + 1;

        SpanBuilder.setSpan(

                new CustomTypefaceSpan("", MainActivity.tfSemiBold),

                beginFirstBold, endFirstBold,

                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        SpanBuilder.setSpan(

                new ForegroundColorSpan(ContextCompat.getColor(context, R.color.colorAccent)),

                beginFirstBold, endFirstBold,

                Spannable.SPAN_INCLUSIVE_INCLUSIVE);


        SpanBuilder.setSpan(

                new CustomTypefaceSpan("", MainActivity.tfSemiBoldItalic),

                notificationText.length() - excerptLength - 2, notificationText.length(),

                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        return SpanBuilder;
    }


    private void setColor(NotificationAdapter.NotificationHolder viewHolder, final int index) {

        if(notifDataList.get(index).is_opened) viewHolder.row.setBackgroundColor(row_opened);

        else viewHolder.row.setBackgroundColor(ContextCompat.getColor(context, R.color.notif_row_bg_new_comment));


        //else if(notifDataList.get(index).notif_type == NotifData.NOTIF_REPLY )

            //viewHolder.row.setBackgroundColor(ContextCompat.getColor(context, R.color.notif_row_bg_new_comment));

        //else if(notifDataList.get(index).notif_type == NotifData.NOTIF_UPVOTE )

            //viewHolder.row.setBackgroundColor(ContextCompat.getColor(context, R.color.notif_row_bg_new_upvote));

    }



    private void setContent(NotificationAdapter.NotificationHolder viewHolder,
                            String content, int type, final int index, String senderName, String pict_URL) {

        //Drawable icon = ContextCompat.getDrawable(context, co.mentor.app.R.drawable.ic_notif_swipes);

        Drawable notif_icon = new Drawable() {
            @Override
            public void draw(Canvas canvas) {

            }

            @Override
            public void setAlpha(int i) {

            }

            @Override
            public void setColorFilter(ColorFilter colorFilter) {

            }

            @Override
            public int getOpacity() {
                return 0;
            }
        };

        String notificationText = "error";

        String content60 = "error";


        if (type == NotifData.NOTIF_UPVOTE) {

            notif_icon = ContextCompat.getDrawable(context, R.drawable.ic_notif_upvote_new);

            content60 = reduceContent(content);

            notificationText = senderName + " upvoted your card : \"" + content60 + "\"";

        }


        if (type == NotifData.NOTIF_REPLY) {

            notif_icon = ContextCompat.getDrawable(context, R.drawable.ic_notif_comment_new);

            content60 = reduceContent(content);

            notificationText = senderName + " replied to your card : \"" + content60 + "\"";

        }


        if (type == NotifData.NOTIF_FAVORITE) {

            notif_icon = ContextCompat.getDrawable(context, R.drawable.ic_notif_profil);

            content60 = reduceContent(content);

            notificationText = senderName + " has upvoted your curated thought : \"" + content60 + "\"";

        }


        if (type == NotifData.NOTIF_DISCUSSION) {

            notif_icon = ContextCompat.getDrawable(context, R.drawable.ic_notif_profil);

            content60 = reduceContent(content);

            notificationText = senderName + " has replied to your curated thought : \"" + content60 + "\"";

        }


        SpannableStringBuilder SpanBuilder = setTextStyle(notificationText, senderName, content60.length());

        if(!pict_URL.equals("NULL")) Picasso.with(MainActivity.context).load(pict_URL).into(viewHolder.profil_icon);

        else viewHolder.profil_icon.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.profil_pict));

        viewHolder.notif_icon.setImageDrawable(notif_icon);

        viewHolder.notif_text.setText(SpanBuilder);

        viewHolder.notif_timestamp.setText(notifDataList.get(index).timestamp);

        if(!notifDataList.get(index).localisation.equals("")) {

            viewHolder.row_gps.setVisibility(View.VISIBLE);

            viewHolder.tv_localisation.setText(notifDataList.get(index).localisation);

        } else viewHolder.row_gps.setVisibility(View.GONE);

    }


    private void setClick(final NotificationAdapter.NotificationHolder viewHolder, final int index) {

        if(!notifDataList.get(index).senderID.equals(
                ((MainActivity)MainActivity.context).localData.getUserData(SharedPref.KEY_USER_ID))) {

            viewHolder.btn_profil_icon.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {

                    SubActivity.openUserProfil(
                            context,
                            notifDataList.get(index).sender_name,
                            notifDataList.get(index).senderID,
                            notifDataList.get(index).senderProfilURL,
                            false);
                }
            });

        }


        viewHolder.btn_open_card.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                viewHolder.row.setBackgroundColor(ContextCompat.getColor(context, R.color.notif_row_bg_open));

                notifDataList.get(index).is_opened = true;

                ParseRequest.change_Notification_To_Opened(notifDataList.get(index).notifID);

                SubActivity.openCardWithID(context, notifDataList.get(index).cardID);
            }
        });
    }



    class NotificationHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.row) LinearLayout row;
        @Bind(R.id.row_gps) LinearLayout row_gps;
        @Bind(R.id.profil_icon) ImageView profil_icon;
        @Bind(R.id.btn_profil_icon) RelativeLayout btn_profil_icon;
        @Bind(R.id.notif_icon) ImageView notif_icon;
        @Bind(R.id.icone_upvote) LinearLayout btn_open_card;
        @Bind(R.id.notif_text) TextView notif_text;
        @Bind(R.id.tv_localisation) TextView tv_localisation;
        @Bind(R.id.tv_last_timestamp) TextView notif_timestamp;

        public NotificationHolder(View itemView) {

            super(itemView);

            ButterKnife.bind(this, itemView);

            notif_text.setTypeface(MainActivity.tf);
            notif_timestamp.setTypeface(MainActivity.tf);
        }
    }

}