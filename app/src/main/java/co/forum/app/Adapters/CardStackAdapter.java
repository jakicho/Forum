package co.forum.app.Adapters;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;
import co.forum.app.ContentEditor.NewContent_Editor;
import co.forum.app.SharedPref;
import de.hdodenhof.circleimageview.CircleImageView;
import co.forum.app.Data.ParseRequest;
import co.forum.app.MainSections.DeckFrag;
import co.forum.app.OnBoardingActivity.OnBoardingActivity;
import co.forum.app.SubActivity.SubActivity;
import co.forum.app.Data.CardData;
import co.forum.app.MainActivity;
import co.forum.app.R;
//import network.codex.app2.cardStackView.test.CardStackView2;
import co.forum.app.tools.HorizontalScrollView2;
import co.forum.app.tools.TextSize;
import co.forum.app.tools.Message;
import co.forum.app.tools.RowLayout;
import co.forum.app.tools.Tags;


public class CardStackAdapter extends BaseAdapter {

    public static final int GLOBAL_DECK = 0;
    public static final int DEMO_DECK = 777;
    public static final int SEND_DECK = 1; //is_main_editor
    public static final int MAIN_USER_DECK = 2;
    public static final int OTHER_USER_DECK = 3;

    public int deck_type;
    public String userID;
    public int offset;
    public int caller;

    public Context context;

    public static View container;

    List<CardData> data = Collections.emptyList();

    private int current;



    // preview editor
    public CardStackAdapter(Context context, List<CardData> data, int deck_type) {

        this.context = context;
        this.data = data;
        this.deck_type = deck_type;
        caller = init_caller(deck_type);
        offset = 0;
    }

    // deck
    public CardStackAdapter(Context context, List<CardData> data, int deck_type, String userID) {

        this.context = context;
        this.data = data;
        this.userID = userID;
        this.deck_type = deck_type;
        caller = init_caller(deck_type);
        offset = 0;



    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(final int position, View view, ViewGroup viewGroup) {

        current = position;

        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());

        if(deck_type == CardStackAdapter.DEMO_DECK)

            container = inflater.inflate(R.layout.item_card_swipe_demo, viewGroup, false);

        else if(data.get(position).getParent_author_full_name() != null)

            container = inflater.inflate(R.layout.item_card_swipe_comment, viewGroup, false);

        else container = inflater.inflate(R.layout.item_card_swipe, viewGroup, false);


        ButterKnife.bind(this, container);

        populateCard(position);

        customizeCard(deck_type, position);

        return container;
    }


    private void populateCard(int position) {

        // profil pict
        String pict_URL = data.get(position).get_user_pict_url();

        if (!pict_URL.equals("NULL")) Picasso.with(MainActivity.context)
                .load(pict_URL)
                .placeholder(ContextCompat.getDrawable(context, R.drawable.profil_pict))
                .into(profil_pic);

        else profil_pic.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.profil_pict));

        if (data.get(position).get_curated()) {

            profil_pic.setBorderColor(ContextCompat.getColor(MainActivity.context, R.color.curated));
            profil_pic.setBorderWidth(5);

            tv_author_name.setTextColor(ContextCompat.getColor(MainActivity.context, R.color.curated));

            tv_timestamp.setVisibility(View.GONE);
            row_gps.setVisibility(View.GONE);

            tv_tagline.setVisibility(View.VISIBLE);
            tv_tagline.setText(data.get(position).getCurated_tagline());

        } else {

            tv_timestamp.setText(data.get(position).getTimestamp());

            profil_pic.setBorderWidth(0);

            Geocoder gcd = new Geocoder(context, Locale.getDefault());

            try {

                BigDecimal bd = new BigDecimal(data.get(position).getLatitude()).setScale(3, RoundingMode.HALF_EVEN);
                float lat = (float) bd.doubleValue();

                List<Address> addresses = null;
                addresses = gcd.getFromLocation(lat, data.get(position).getLongitude(), 1);

                if (addresses.size() > 0)

                    tv_localisation.setText(addresses.get(0).getLocality() + ", " + addresses.get(0).getCountryName());

                else row_gps.setVisibility(View.INVISIBLE);

            } catch (IOException e) {

                row_gps.setVisibility(View.INVISIBLE);
                e.printStackTrace();
            }

        }

        if (deck_type == DEMO_DECK)

            tv_main_content.setTextSize(TextSize.miniScale(data.get(position).getMain_content().length()));

        else tv_main_content.setTextSize
                (TextSize.scale(CardData.MAX_THOUGHT_LENGTH - data.get(position).getMain_content().length(), false));

        tv_author_name.setText(data.get(position).getAuthor_full_name());


        if ((data.get(position).get_pict_url()).equals("")) {

            // CARD TEXT
            content_text.setVisibility(View.VISIBLE);
            content_legend.setVisibility(View.GONE);
            tv_main_content.setText(data.get(position).getMain_content());

            // url
            if(data.get(position).getLink_url().isEmpty()) btn_link.setVisibility(View.GONE);
            else {
                btn_link.setVisibility(View.VISIBLE);
                tv_link_url.setText(data.get(position).getLink_url());
            }

        } else {

            // CARD IMAGE
            content_text.setVisibility(View.INVISIBLE);
            content_legend.setVisibility(View.VISIBLE);

            if ((data.get(position).getMain_content()).isEmpty())
                tv_legend_content.setVisibility(View.GONE);
            else tv_legend_content.setText(data.get(position).getMain_content());

            // url - nope
            /*
            if (data.get(position).getLink_url().isEmpty()) btn_link2.setVisibility(View.GONE);
            else {
                btn_link2.setVisibility(View.VISIBLE);
                tv_link_url2.setText(data.get(position).getLink_url());
            }
            */

            // main pict
            if ((data.get(position).get_pict_url()).equals(CardData.PICT_NOT_UPLOADED_YET)) {

                // card preview to send

                Picasso.with(context)
                        .load(NewContent_Editor.selectedImage)
                        .fit().centerCrop()
                        .into(image);

            } else if(!data.get(position).get_pict_url().isEmpty()){

                Picasso.with(context)
                        .load(data.get(position).get_pict_url())
                        .fit().centerCrop()
                        .into(image);

            }


        }



        tv_nb_upvotes.setText(String.valueOf(data.get(position).getNb_upvotes()));
        tv_nb_replies.setText(String.valueOf(data.get(position).getNb_replies()));

        if(data.get(position).getParent_author_full_name() != null)

            tv_parent_author.setText(data.get(position).getParent_author_full_name());

            //reply_indicator.setVisibility(View.VISIBLE);

        //else reply_indicator.setVisibility(View.GONE);

        tv_main_content.setTypeface(MainActivity.tf_serif);
        //tv_legend_content.setTypeface(MainActivity.tf_serif);

    }





    private void customizeCard(int deck_type, final int position) {

        if(deck_type == SEND_DECK) {

            tv_timestamp.setText("Just now");

            item_frame.setGravity(Gravity.CENTER);

            //item_frame.setTranslationY(40);


            Tags.populateTagsRow(context, tags_row, data.get(position).getTags_list());
        }

        if(deck_type == GLOBAL_DECK) {

            item_frame.setGravity(Gravity.CENTER);

            item_frame.setPadding(0, 0, 0, (int) (DeckFrag.bottom_margin * 1.5));


            //item_frame.setPadding(0, 0, 0, 200);

            if (((MainActivity) context).deckFragment.isSearchResult)

                Tags.populateTagsRowSearch(context, tags_row,

                        data.get(position).getTags_list(),

                        ((MainActivity) context).deckFragment.searchFragment.tagsSelected_Active);

            else if(((MainActivity) context).deckFragment.isSubscription)

                Tags.populateTagsRowSearch(context, tags_row,

                        data.get(position).getTags_list(),

                        ((MainActivity) context).deckFragment.tags_subscriptionList);

            else Tags.populateTagsRow(context, tags_row, data.get(position).getTags_list());

            if(!data.get(position).getAuthor_id()

                    .equals(((MainActivity) MainActivity.context).localData.getUserData(SharedPref.KEY_USER_ID))) {

                cardSwipe_header.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        SubActivity.openUserProfil(
                                context,
                                data.get(position).getAuthor_full_name(),
                                data.get(position).getAuthor_id(),
                                data.get(position).get_user_pict_url(),
                                data.get(position).get_curated());
                    }
                });

                btn_copy_content.setVisibility(View.VISIBLE);
                btn_copy_content.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Message.message(context, "Copied to clipboard");

                        ClipboardManager clipboard = (ClipboardManager) MainActivity.context.getSystemService(MainActivity.CLIPBOARD_SERVICE);
                        ClipData clip = ClipData.newPlainText("text", data.get(position).getMain_content());
                        clipboard.setPrimaryClip(clip);

                    }
                });
            }


        } else if(deck_type == DEMO_DECK) {

            Tags.populateTagsRow(context, tags_row, data.get(position).getTags_list());

            cardSwipe_header.setOnClickListener(null);

            bookmark.setVisibility(View.GONE);

            item_frame.setGravity(Gravity.CENTER);

            ViewGroup.MarginLayoutParams params2 = (ViewGroup.MarginLayoutParams) cardToSwipe.getLayoutParams();

            params2.topMargin = 0;

            cardToSwipe.setLayoutParams(params2);

        } else if(deck_type == MAIN_USER_DECK) {

            Tags.populateTagsRow(context, tags_row, data.get(position).getTags_list());

            cardSwipe_header.setOnClickListener(null);

            bookmark.setVisibility(View.GONE);

            item_frame.setGravity(Gravity.CENTER);

            ViewGroup.MarginLayoutParams params2 = (ViewGroup.MarginLayoutParams) cardToSwipe.getLayoutParams();

            params2.topMargin = 0;

            cardToSwipe.setLayoutParams(params2);

            btn_copy_content.setVisibility(View.GONE);


        } else if(deck_type == OTHER_USER_DECK) {

            Tags.populateTagsRow(context, tags_row, data.get(position).getTags_list());

            cardSwipe_header.setOnClickListener(null);

            bookmark.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Message.message(MainActivity.context, "bookmark");
                }
            });

            item_frame.setGravity(Gravity.CENTER);

            ViewGroup.MarginLayoutParams params2 = (ViewGroup.MarginLayoutParams) cardToSwipe.getLayoutParams();

            params2.topMargin = 0;

            cardToSwipe.setLayoutParams(params2);

            btn_copy_content.setVisibility(View.VISIBLE);
            btn_copy_content.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Message.message(context, "Copied to clipboard");

                    ClipboardManager clipboard = (ClipboardManager) MainActivity.context.getSystemService(MainActivity.CLIPBOARD_SERVICE);
                    ClipData clip = ClipData.newPlainText("text", data.get(position).getMain_content());
                    clipboard.setPrimaryClip(clip);

                }
            });
        }

    }


    public CardData getSwipedCard(int type) {

        int index2 = 0;

        if(type == 0) index2 = data.size();

        else if(type == 1) index2 = current;

        else if(type == 2) index2 = current-1;

        return data.get(index2-1);
    }


    public CardData getCurrentCard(boolean lastItem) {

        if (lastItem) return data.get(current);

        else {

            if (current - 1 == -1) return data.get(0); //exception 1 carte

            else return data.get(current - 1);
        }
    }


    @Bind(R.id.reply_indicator) LinearLayout reply_indicator;


    @Nullable @Bind(R.id.tv_parent_author) TextView tv_parent_author;
    @Bind(R.id.item_frame) LinearLayout item_frame;
    @Bind(R.id.cardToSwipe) CardView cardToSwipe;

    //@Bind(R.id.btn_profil_pic) LinearLayout btn_profil_pic;
    @Bind(R.id.profil_pic) CircleImageView profil_pic;
    @Bind(R.id.btn_notif) FrameLayout bookmark;

    @Bind(R.id.cardSwipe_header) LinearLayout cardSwipe_header;
    @Bind(R.id.tv_username) TextView tv_author_name;
    @Bind(R.id.tv_timestamp) TextView tv_timestamp;
    @Bind(R.id.tv_tagline) TextView tv_tagline;

    @Bind(R.id.row_gps) LinearLayout row_gps;
    @Bind(R.id.tv_localisation) TextView tv_localisation;

    // CARD TEXT ONLY
    @Bind(R.id.content_text) LinearLayout content_text;
    @Bind(R.id.tv_main_content) TextView tv_main_content;
    @Bind(R.id.btn_link) LinearLayout btn_link;
    @Bind(R.id.tv_link) TextView tv_link_url;

    // CARD IMAGE
    @Bind(R.id.image) ImageView image;
    @Bind(R.id.content_legend) LinearLayout content_legend;
    @Bind(R.id.tv_legend_content) TextView tv_legend_content;
    //@Bind(R.id.btn_link2) LinearLayout btn_link2;
    //@Bind(R.id.tv_link2) TextView tv_link_url2;



    @Bind(R.id.tv_nb_upvotes) TextView tv_nb_upvotes;
    @Bind(R.id.tv_nb_replies) TextView tv_nb_replies;
    @Bind(R.id.tags_scroller) HorizontalScrollView2 tags_scroller;
    @Bind(R.id.header_tags_row) RowLayout tags_row;


    @Bind(R.id.btn_copy_content) LinearLayout btn_copy_content;


    @Bind(R.id.indicator_upvote) public TextView indicator_upvote;
    @Bind(R.id.indicator_pass) public TextView indicator_pass;


    private int init_caller(int deck_type) {

        // redondant

        if(deck_type == GLOBAL_DECK) return ParseRequest.GLOBAL_DECK;

        else if (deck_type == MAIN_USER_DECK) return ParseRequest.MAIN_USER_PROFIL_DECK;

        else if (deck_type == OTHER_USER_DECK) return ParseRequest.OTHER_USER_PROFIL_DECK;

        else if (deck_type == DEMO_DECK) return ParseRequest.DEMO_DECK;

        return -1;

    }


    public void reloadStack(Context context) {

        //Message.message(context, "reload\n" + "deck type : " + deck_type + "\n caller : " + caller);

        offset++;

        if (caller == -1) Message.message(context, "deck type : " + deck_type + "\n caller : " + caller);


        else if (caller == ParseRequest.GLOBAL_DECK) {

            if (((MainActivity) context).deckFragment.isSearchResult) {

                //Message.message(context, "search deck adapter"); // non recharger on affiche tout todo a faire plus tard

                //((MainActivity) context).deckFragment.loader.setVisibility(View.VISIBLE);

                ParseRequest.get_CardsFromTags(((MainActivity) context)

                        .deckFragment.searchFragment.tagsSelected_Active, offset);


            } else if(((MainActivity) context).deckFragment.isNavQuoteDeck) {

                ParseRequest.get_AuthorCards(context, ((MainActivity) context).deckFragment.navAuthorID, offset);

            } else if(((MainActivity) context).deckFragment.isSubscription) {

                ParseRequest.get_subscriptionCards(context, ((MainActivity) context).deckFragment.tags_subscriptionList, "createdAt", offset);

            } else {

                //Message.message(context, "reload global deck adapter : " + offset);

                //((MainActivity) context).deckFragment.loaderDeck.setVisibility(View.VISIBLE);

                if (!((MainActivity) MainActivity.context).deckFragment.isCuratedDeck)

                    ParseRequest.get_GlobalDeck(context, offset, false);

                else ParseRequest.get_GlobalDeck(context, offset, true);

            }

        } else if(caller == ParseRequest.DEMO_DECK) {

            Fragment fragment = ((OnBoardingActivity)OnBoardingActivity.context).getSupportFragmentManager().findFragmentByTag("demo_deck");

            ((DeckFrag) fragment).displayCards(OnBoardingActivity.context, ((DeckFrag) fragment).getDemoDeck(), offset);


        } else {

            if(caller == ParseRequest.MAIN_USER_PROFIL_DECK)

                ((SubActivity) context).deckFragment.loaderDeck.setVisibility(View.VISIBLE);


            else if(caller == ParseRequest.OTHER_USER_PROFIL_DECK)

                ((SubActivity) context).deckFragment.loaderDeck.setVisibility(View.VISIBLE);


            ParseRequest.get_UserCards(context, userID, offset, caller);
        }

    }


    // global deck only
    public void reset_offset_for_curation() {

        offset = 0;

        if(((MainActivity)MainActivity.context).deckFragment.card_stack_view.getChildCount() <= 5) {

            reloadStack(MainActivity.context);
        }
    }


    public List<CardData> getData(){

        List<CardData> data = new ArrayList<>();

        String[] default_author_name = context.getResources().getStringArray(R.array.author_name);
        String[] default_time = context.getResources().getStringArray(R.array.time);
        String[] default_localisation = context.getResources().getStringArray(R.array.localisation);
        String[] default_main_content = context.getResources().getStringArray(R.array.main_content);
        int[] default_nb_upvotes = context.getResources().getIntArray(R.array.nb_upvotes);
        int[] default_nb_replies = context.getResources().getIntArray(R.array.nb_replies);
        String[] default_tags = context.getResources().getStringArray(R.array.tags);


        for(int i=0; i<default_author_name.length && i<default_time.length; i++){
            CardData current = new CardData();
            current.setAuthor_full_name(default_author_name[i]);
            current.setTimestamp(default_time[i]);
            current.setMain_content(default_main_content[i]);
            current.setNb_upvotes(default_nb_upvotes[i]);
            current.setNb_replies(default_nb_replies[i]);
            current.setTags_list(Tags.Spliter(default_tags[i]));
            data.add(current);
        }
        return data;
    }
}
