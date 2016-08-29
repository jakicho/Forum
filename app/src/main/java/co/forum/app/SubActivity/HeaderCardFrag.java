package co.forum.app.SubActivity;


import android.content.ActivityNotFoundException;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sothree.slidinguppanel.SlidingUpPanelLayout;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

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
import co.forum.app.tools.Message;
import co.forum.app.tools.RowLayout;
import co.forum.app.tools.Tags;
import co.forum.app.tools.TextSize;
import de.hdodenhof.circleimageview.CircleImageView;

public class HeaderCardFrag extends Fragment {

    final Drawable upvoteON = (ContextCompat.getDrawable(MainActivity.context, R.drawable.ic_thumb_up_orange));
    final Drawable upvoteOFF = (ContextCompat.getDrawable(MainActivity.context, R.drawable.ic_thumb_up_border));
    public boolean upvoted;
    boolean notif_state;

    @Bind(R.id.indicator_post_comment) public TextView indicator_post_comment;

    @Bind(R.id.parent_indicator) LinearLayout parent_indicator; //
    @Bind(R.id.parent_author_name) TextView parent_author_name; //
    @Bind(R.id.parent_pre) TextView parent_pre; //
    @Bind(R.id.parent_post) TextView parent_post; //

    @Bind(R.id.cardSwipe_header) LinearLayout cardSwipe_header;

    @Bind(R.id.parallax_scale) LinearLayout parallax_scale; //RelativeLayout
    @Bind(R.id.parallax) LinearLayout parallax;

    @Bind(R.id.profil_pic) CircleImageView profil_pic;
    @Bind(R.id.tv_username) TextView tv_username;
    @Bind(R.id.tv_timestamp) TextView tv_timestamp;
    @Bind(R.id.tv_tagline) TextView tv_tagline;


    @Bind(R.id.row_gps) LinearLayout row_gps;
    @Bind(R.id.tv_localisation) TextView tv_localisation;
    @Bind(R.id.tv_nb_views) TextView tv_nb_views;
    @Bind(R.id.tv_nb_upvotes) TextView tv_nb_upvotes;
    @Bind(R.id.tv_nb_replies) TextView tv_nb_replies;

    @Bind(R.id.btn_notif) LinearLayout btn_notif;
    @Bind(R.id.notif_icon) public ImageView notif_icon;

    @Bind(R.id.btn_copy_content) LinearLayout btn_copy_content;
    @Bind(R.id.copy_icon) ImageView copy_icon;

    @Bind(R.id.header_pict) ImageView header_pict;
    @Bind(R.id.tv_main_content) TextView tv_main_content;
    @Bind(R.id.tv_link) TextView tv_link_url;
    @Bind(R.id.btn_link) LinearLayout btn_link;


    @Bind(R.id.icone_upvote) public ImageView icone_upvote;

    @Bind(R.id.btn_comment_header) LinearLayout btn_comment_header;
    @Bind(R.id.btn_upvote_header) LinearLayout btn_upvote_header;

    @Bind(R.id.header_tags_row) public RowLayout header_tags_row;
    @Bind(R.id.tag_footer) LinearLayout tag_footer;

    public HeaderCardFrag() {
        // Required empty public constructor
    }

    public View headerView;

    public CardData headerCardData;

    boolean fromComment = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        headerCardData = getArguments().getParcelable(SubActivity.KEY_CARD_TO_OPEN);

        fromComment = getArguments().getBoolean(SubActivity.KEY_FROM_COMMENT);

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if(!headerCardData.getParent_card_id().isEmpty())

            headerView = inflater.inflate(R.layout.item_card_header_comment, container, false);

        else headerView = inflater.inflate(R.layout.item_card_header, container, false);

        ButterKnife.bind(this, headerView);

        //card_loader.setVisibility(View.GONE);

        icone_upvote.setImageAlpha(100);

        set_Parent(getContext(), headerCardData);

        set_Content(headerCardData);

        set_Tags(headerCardData.getTags_list());

        set_Buttons(headerCardData);

        return headerView;
    }


    Transformation transformation = new Transformation() {

        @Override
        public Bitmap transform(Bitmap source) {
            int targetWidth = header_pict.getWidth();

            double aspectRatio = (double) source.getHeight() / (double) source.getWidth();
            int targetHeight = (int) (targetWidth * aspectRatio);
            Bitmap result = Bitmap.createScaledBitmap(source, targetWidth, targetHeight, false);
            if (result != source) {
                // Same bitmap is returned if sizes are the same
                source.recycle();
            }
            return result;
        }

        @Override
        public String key() {
            return "transformation" + " desiredWidth";
        }
    };


    private void set_Content(CardData cardData) {

        // Profil pict
        String pict_URL = cardData.get_user_pict_url();
        if(!pict_URL.equals("NULL")) Picasso.with(MainActivity.context).load(pict_URL).into(profil_pic);
        else profil_pic.setImageDrawable(ContextCompat.getDrawable(MainActivity.context, R.drawable.profil_pict));

        // PICT
        if(cardData.get_pict_url().isEmpty()) {

            header_pict.setVisibility(View.GONE);

        } else {

            WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
            Display display = wm.getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);
            int width = size.x;

            header_pict.setVisibility(View.VISIBLE);
            Picasso.with(MainActivity.context)
                    .load(cardData.get_pict_url())
                    .resize(width, 0)
                    .into(header_pict);
        }


        if(cardData.get_curated()) {

            tv_username.setTextColor(ContextCompat.getColor(MainActivity.context, R.color.curated));

            profil_pic.setBorderColor(ContextCompat.getColor(MainActivity.context, R.color.curated));
            profil_pic.setBorderWidth(5);

            tv_timestamp.setVisibility(View.GONE);
            row_gps.setVisibility(View.GONE);

            tv_tagline.setVisibility(View.VISIBLE);
            tv_tagline.setText(cardData.getCurated_tagline());

        } else profil_pic.setBorderWidth(0);


        // MAIN CONTENT
        tv_main_content.setTypeface(MainActivity.tf_serif);

        if(cardData.getParent_card_id().isEmpty() && cardData.get_pict_url().isEmpty()) {

            tv_main_content.setTextSize(TextSize.scale(CardData.MAX_THOUGHT_LENGTH - cardData.getMain_content().length(), false));


        } else if(!cardData.get_pict_url().isEmpty()){

            tv_main_content.setTextSize(16);

            if(cardData.getMain_content().isEmpty()) tv_main_content.setVisibility(View.GONE);
        }

        tv_main_content.setText(cardData.getMain_content());


        // URL
        final String header_url = cardData.getLink_url();

        if(cardData.getLink_url().isEmpty()) btn_link.setVisibility(View.GONE);

        else {
            btn_link.setVisibility(View.VISIBLE);
            tv_link_url.setText(cardData.getLink_url());

            btn_link.setOnClickListener(new View.OnClickListener() {
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


        //TextSize.setHeaderVerticalPadding(tv_main_content, CardData.MAX_THOUGHT_LENGTH - cardData.getMain_content().length());
        //tv_main_content.setPadding(32, 0, 32, 0);

        tv_username.setText(cardData.getAuthor_full_name());
        tv_timestamp.setText(cardData.getTimestamp());
        tv_nb_upvotes.setText(String.valueOf(cardData.getNb_upvotes()));
        tv_nb_replies.setText(String.valueOf(cardData.getNb_replies()));


        // GPS
        Geocoder gcd = new Geocoder(MainActivity.context, Locale.getDefault());
        try {

            // limite la precision
            BigDecimal bd = new BigDecimal(cardData.getLatitude()).setScale(3, RoundingMode.HALF_EVEN);
            float lat = (float) bd.doubleValue();

            List<Address> addresses = gcd.getFromLocation(lat, cardData.getLongitude(), 1);

            if (addresses.size() > 0)

                tv_localisation.setText(addresses.get(0).getLocality() + ", " + addresses.get(0).getCountryName());

            else row_gps.setVisibility(View.INVISIBLE);

        } catch (IOException e) {

            row_gps.setVisibility(View.INVISIBLE);
            e.printStackTrace();
        }


        //tv_nb_views.setText(String.valueOf(cardData.getNb_upvotes() + cardData.getNb_pass()));
    }

    private void set_Parent(final Context context, final CardData cardData) {

        if(!cardData.getParent_card_id().isEmpty()) {
            parent_pre.setTypeface(MainActivity.tf);
            parent_author_name.setTypeface(MainActivity.tfSemiBold);
            parent_post.setTypeface(MainActivity.tf);
            parent_author_name.setText(cardData.getParent_author_full_name());
            parent_indicator.setVisibility(View.VISIBLE);

            parent_indicator.setOnClickListener(new View.OnClickListener() {
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
            parent_indicator.setVisibility(View.GONE);
        }
    }

    private void set_Buttons(final CardData cardData) {

        if(((MainActivity)MainActivity.context).localData.isUserCard(cardData.getAuthor_id())) {

            set_notif_btn(headerCardData.isAuthor_listening());

            //icone_upvote.setAlpha(0.4f);

            icone_upvote.setImageAlpha(100);

            btn_upvote_header.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {

                    Message.message(getContext(), getResources().getString(R.string.no_self_upvote));
                }
            });

            btn_copy_content.setVisibility(View.GONE);

        } else {

            btn_copy_content.setVisibility(View.VISIBLE);

            btn_copy_content.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Message.message(getContext(), getResources().getString(R.string.copied_to_clipboard));

                    ClipboardManager clipboard = (ClipboardManager) MainActivity.context.getSystemService(MainActivity.CLIPBOARD_SERVICE);
                    ClipData clip = ClipData.newPlainText("text", headerCardData.getMain_content());
                    clipboard.setPrimaryClip(clip);
                }
            });

            btn_notif.setVisibility(View.GONE);

            cardSwipe_header.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    SubActivity.openUserProfil(
                            getContext(),
                            cardData.getAuthor_full_name(),
                            cardData.getAuthor_id(),
                            cardData.get_user_pict_url(),
                            cardData.get_curated());
                }
            });

            // ParseRequest.get_userVote(getContext(), cardData.getCard_id(), cardData.getAuthor_id());

        }

        btn_comment_header.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ((SubActivity)getContext()).cardFragment.reply_content_panel_structure.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);
            }
        });

        indicator_post_comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ((SubActivity)getContext()).cardFragment.reply_content_panel_structure.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);

            }
        });
    }

    public void set_icon_upvote_layout(boolean upvoted) {

        if(upvoted) {

            icone_upvote.setImageDrawable(upvoteON);
            icone_upvote.setImageAlpha(245);

        } else {

            icone_upvote.setImageDrawable(upvoteOFF);
            icone_upvote.setImageAlpha(100);

        }
    }

    private void deactivate_upvote_button() {

        //btn_upvote_header.setVisibility(GONE);

        btn_upvote_header.setOnClickListener(null);

        final Handler handler = new Handler();

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                //btn_upvote_header.setVisibility(VISIBLE);
                btn_upvote_header.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        activate_upvote_button();
                    }
                });

            }
        }, 3000);
    }

    private void activate_upvote_button() {

        if (upvoted) {

            ParseRequest.delete_upvote(
                    getContext(),
                    headerCardData.getCard_id(),
                    ((MainActivity) MainActivity.context).localData.getUserData(SharedPref.KEY_USER_ID),
                    headerCardData.getAuthor_id()
            );

            upvoted = false;

            set_icon_upvote_layout(false);

        } else {

            ParseRequest.check_and_insert_upvote(
                    getContext(),
                    headerCardData.getCard_id(),
                    ((MainActivity) MainActivity.context).localData.getUserData(SharedPref.KEY_USER_ID),
                    headerCardData.getAuthor_id(),
                    ((MainActivity) MainActivity.context).localData.getUserData(SharedPref.KEY_FULL_NAME),
                    headerCardData.getMain_content()
            );

            upvoted = true;

            set_icon_upvote_layout(true);

        }

        deactivate_upvote_button();
    }



    private void set_notif_btn(boolean listening) {

        notif_state = listening;

        if(listening) notif_icon.setImageDrawable((ContextCompat.getDrawable(MainActivity.context, R.drawable.ic_bell_full_black)));

        else notif_icon.setImageDrawable((ContextCompat.getDrawable(MainActivity.context, R.drawable.ic_bell_black)));

        btn_notif.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                if(notif_state) {

                    notif_state = false;
                    notif_icon.setImageDrawable((ContextCompat.getDrawable(MainActivity.context, R.drawable.ic_bell_black)));

                } else {

                    notif_state = true;
                    notif_icon.setImageDrawable((ContextCompat.getDrawable(MainActivity.context, R.drawable.ic_bell_full_black)));

                }

                ParseRequest.set_cardSubscription(getContext(), headerCardData.getCard_id(), notif_state);

            }
        });

    }


    public void set_btn_notif(boolean notif2) {

        notif_state = notif2;

        if(notif_state) {

            btn_notif.setVisibility(View.VISIBLE);
            notif_icon.setImageDrawable( (ContextCompat.getDrawable(MainActivity.context, R.drawable.ic_bell_full_black)));
        } else {

            btn_notif.setVisibility(View.VISIBLE);
            notif_icon.setImageDrawable( (ContextCompat.getDrawable(MainActivity.context, R.drawable.ic_bell_black)));
        }

        btn_notif.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                //activate_notif_btn(notif_state);
            }
        });


    }

    public void set_btn_upvote(boolean upvoted2) {

        upvoted = upvoted2;

        set_icon_upvote_layout(upvoted);

        //icone_upvote.setVisibility(VISIBLE);

        btn_upvote_header.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                activate_upvote_button();
            }
        });

    }

    private void set_Tags(ArrayList<String> tagsList) {

        for (int i=0; i < header_tags_row.getChildCount(); i++) {

            if(i < tagsList.size()) {

                Tags.setCardView_Layout(Tags.TAG_MINI_ADAPTER, (CardView) header_tags_row.getChildAt(i), tagsList.get(i));

                header_tags_row.getChildAt(i).setVisibility(View.VISIBLE);

            } else header_tags_row.getChildAt(i).setVisibility(View.GONE);

        }

        if(tagsList.size() == 0) {

            header_tags_row.getChildAt(11).setVisibility(View.VISIBLE);

            tag_footer.setVisibility(View.GONE);
        }


            /*for (int i=0; i < header_tags_row.getChildCount(); i++) {

                Tags.setCardView_Layout(Tags.TAG_MINI_ADAPTER, (CardView) header_tags_row.getChildAt(i), ((TextView) (((CardView) header_tags_row.getChildAt(i)).getChildAt(0))).getText().toString()) ;

                header_tags_row.getChildAt(i).setVisibility(View.VISIBLE);

            }*/
    }

}
