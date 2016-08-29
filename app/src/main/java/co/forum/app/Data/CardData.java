package co.forum.app.Data;

import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.format.DateUtils;

import com.parse.ParseUser;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import co.forum.app.MainActivity;
import co.forum.app.SharedPref;
import co.forum.app.tools.Message;
import co.forum.app.tools.Tags;

public class CardData implements Parcelable {

    public static final int MIN_TAGS = 1;
    public static final int MAX_TAGS = 7;
    public static final int MAX_THOUGHT_LENGTH = 400;

    public static final String PICT_NOT_UPLOADED_YET = "pict not uploaded yet";

    public static final String CARD_TYPE = "card_type"; // post or comment
    public static final boolean IS_POST = false;
    public static final boolean IS_COMMENT = true;

    private String card_id;
    private String author_id;
    private String author_full_name;
    private String user_pict_url;

    private String parent_card_id;
    private String parent_author_id;
    private String parent_author_full_name;

    private String chat_id;

    private String timestamp;


    private float latitude;
    private float longitude;

    private String localisation;

    private boolean isStory;
    private boolean curated;
    private String curated_tagline;

    private String pict_url;
    private String main_content;
    private String link_url;

    private int nb_upvotes;
    private int nb_pass;
    private int nb_replies;
    private ArrayList<String> tags_list;

    private boolean author_listening;

    private Uri repliedImage;

    public CardData() {}

    // title card
    public CardData(String title) {

        this.main_content = title;

    }

    // new
    public CardData(String user_id,
                    String author_full_name,
                    String user_pict_url,
                    boolean curated, String curated_tagline,
                    String parent_card_id,
                    String pict_url,
                    String main_content,
                    String link_url,
                    ArrayList<String> tags_list,
                    float latitude, float longitude,
                    int nb_upvotes, int nb_replies, int nb_pass) {

        this.author_id = user_id;
        this.author_full_name = author_full_name;
        this.user_pict_url = user_pict_url;
        this.curated = curated;
        this.curated_tagline = curated_tagline;
        this.parent_card_id = parent_card_id;

        this.pict_url = pict_url;
        this.main_content = main_content;
        this.link_url = link_url;
        this.tags_list = tags_list;

        this.latitude = latitude;
        this.longitude = longitude;

        this.nb_upvotes = nb_upvotes;
        this.nb_replies = nb_replies;
        this.nb_pass = nb_pass;

        author_listening = true;

    }

    // reply
    public CardData(String user_id,
                    String author_full_name,
                    String parent_card_id,
                    String parent_user_id,
                    String parent_author_full_name,
                    String chat_id,
                    String main_content,
                    Uri repliedImage,
                    String link_url,
                    ArrayList<String> tags_list,
                    float latitude, float longitude,
                    int nb_upvotes, int nb_replies, int nb_pass) {

        this.author_id = user_id;
        this.author_full_name = author_full_name;
        this.curated = false;
        this.parent_card_id = parent_card_id;
        this.parent_author_id = parent_user_id;
        this.parent_author_full_name = parent_author_full_name;
        this.chat_id = chat_id;

        this.main_content = main_content;
        this.repliedImage = repliedImage;
        this.link_url = link_url;
        this.tags_list = tags_list;

        this.latitude = latitude;
        this.longitude = longitude;

        this.nb_upvotes = nb_upvotes;
        this.nb_replies = nb_replies;
        this.nb_pass = nb_pass;

        author_listening = true;
    }

    private int chat_count;


    public boolean isAuthor_listening() {
        return author_listening;
    }

    public void setAuthor_listening(boolean author_listening) {
        this.author_listening = author_listening;
    }

    public Uri getRepliedImage() {
        return repliedImage;
    }

    public void setRepliedImage(Uri repliedImage) {
        this.repliedImage = repliedImage;
    }

    public int getChat_count() {
        return chat_count;
    }

    public void setChat_count(int chat_count) {
        this.chat_count = chat_count;
    }

    public String getChat_id() {
        return chat_id;
    }

    public void setChat_id(String chat_id) {
        this.chat_id = chat_id;
    }

    public String getLocalisation() {
        return localisation;
    }

    public void setLocalisation(String localisation) {
        this.localisation = localisation;
    }

    public String getCurated_tagline() {
        return curated_tagline;
    }

    public void setCurated_tagline(String curated_tagline) {
        this.curated_tagline = curated_tagline;
    }


    public void set_isStory(boolean isStory) {
        this.isStory = isStory;
    }

    public boolean isStory() { return isStory; }

    public void set_curated(boolean curated) {
        this.curated = curated;
    }

    public boolean get_curated() {
        return curated;
    }

    public void setLatitude(float latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(float longitude) {
        this.longitude = longitude;
    }

    public float getLatitude() {
        return latitude;
    }

    public float getLongitude() {
        return longitude;
    }

    public void setCard_id(String card_id) {
        this.card_id = card_id;
    }


    public void set_user_pict_url(String user_pict_url) {
        this.user_pict_url = user_pict_url;
    }

    public String get_user_pict_url() {

        if(user_pict_url == null) return "";

        else return user_pict_url;
    }


    public void setAuthor_id(String author_id) {
        this.author_id = author_id;
    }

    public void setParent_card_id(String parent_card_id) {
        this.parent_card_id = parent_card_id;
    }

    public void setParent_author_id(String parent_author_id) {
        this.parent_author_id = parent_author_id;
    }

    public void setAuthor_full_name(String author_full_name) {
        this.author_full_name = author_full_name;
    }



    public void set_pict_url(String pict_url) {
        this.pict_url = pict_url;
    }

    public String get_pict_url() {

        if(pict_url == null) return "";

        else return pict_url;
    }


    public void setMain_content(String main_content) {
        this.main_content = main_content;
    }

    public void setLink_url(String link_url) {

        if(link_url == null) this.link_url = "";

        else this.link_url = link_url;
    }

    public String getLink_url() {

        if(link_url == null) return "";
        else return link_url;
    }

    public void setNb_upvotes(int nb_upvotes) {
        this.nb_upvotes = nb_upvotes;
    }

    public void setNb_replies(int nb_replies) {
        this.nb_replies = nb_replies;
    }

    public void setTags_list(ArrayList<String> tags_list) {
        this.tags_list = tags_list;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getParent_author_full_name() {
        return parent_author_full_name;
    }

    public void setParent_author_full_name(String parent_author_full_name) {
        this.parent_author_full_name = parent_author_full_name;
    }

    public String getCard_id() {
        return card_id;
    }

    public String getAuthor_id() {
        return author_id;
    }

    public String getParent_card_id() {
        return parent_card_id;
    }

    public String getParent_author_id() {
        return parent_author_id;
    }

    public String getAuthor_full_name() {
        return author_full_name;
    }

    public String getMain_content() {
        return main_content;
    }

    public int getNb_upvotes() {
        return nb_upvotes;
    }

    public int getNb_replies() {
        return nb_replies;
    }

    public int getNb_pass() {
        return nb_pass;
    }

    public void setNb_pass(int nb_pass) {
        this.nb_pass = nb_pass;
    }

    public ArrayList<String> getTags_list() {
        return tags_list;
    }


    // ---------------

    public void setParentCard(ParseCard parseCard) {

        if ((parseCard.get(ParseCard.JOIN_REPLY_PARENT_CARD) == null) &&

                (parseCard.get(ParseCard.JOIN_REPLY_PARENT_USER) == null)) {

            setParent_card_id("");

            setParent_author_id("");

        } else {

            setParent_card_id(((ParseCard) parseCard.get(ParseCard.JOIN_REPLY_PARENT_CARD)).getObjectId());

            setParent_author_id(((ParseUser) parseCard.get(ParseCard.JOIN_REPLY_PARENT_USER)).getObjectId());

            setParent_author_full_name(parseCard.getParentAuthorFullName());

        }

    }


    public void setCard(ParseCard parseCard, String mainUserID) {

        if(!mainUserID.isEmpty()) {

            setAuthor_id(mainUserID);
            setAuthor_full_name(((MainActivity) MainActivity.context).localData.getUserData(SharedPref.KEY_FULL_NAME));

        } else {

            setAuthor_id(((ParseUser) parseCard.get(ParseCard.JOIN_CONTENT_USER)).getObjectId());
            setAuthor_full_name(parseCard.getAuthorFullName());
        }

        if(parseCard.get_ProfilpictUrl() == null) set_user_pict_url("NULL");

        else set_user_pict_url(parseCard.get_ProfilpictUrl());

        set_curated(parseCard.get_Curated());
        setCurated_tagline(parseCard.get_Curated_tagline());
        set_isStory(parseCard.get_isStory());


        String timeAgo = String.valueOf(DateUtils.getRelativeTimeSpanString(

                parseCard.getCreatedAt().getTime(),
                System.currentTimeMillis(),
                DateUtils.MINUTE_IN_MILLIS));

        setTimestamp(timeAgo);

        setCard_id(parseCard.getObjectId());

        if(parseCard.getParseFile("pict")!= null){
            set_pict_url(parseCard.getParseFile("pict").getUrl());
            //Message.message(MainActivity.context, "koaa : " + parseCard.getParseFile("pict").getUrl());
        }

        else set_pict_url("");





        if(parseCard.get(ParseCard.JOIN_CHAT) != null) {

            setChat_id(((ParseChat) parseCard.get(ParseCard.JOIN_CHAT)).getObjectId());
            setChat_count(parseCard.getChatCount());

        } else {

            setChat_id("");

            setChat_count(0);
        }

        setTags_list((ArrayList<String>) parseCard.getTags());

        setMain_content(parseCard.getContent());
        setLink_url(parseCard.getLinkUrl());

        setLatitude(parseCard.getLatitude());
        setLongitude(parseCard.getLongitude());

        /*

        if(parseCard.getLongitude() && parseCard.getLatitude() != null) {

            Number triangle =  parseCard.getLatitude();

            float truc = (float) triangle;

            //setLatitude( (float) parseCard.getLatitude());
            //setLongitude( (float) parseCard.getLongitude());

        } else {

            setLatitude(0.12f);
            setLongitude(0.12f);

        }
        */

        setNb_upvotes(parseCard.getUpvoteCount());
        setNb_replies(parseCard.getReplyCount());
        setNb_pass(parseCard.getPassCount());

        setAuthor_listening(parseCard.get_listening_state());
    }

    public static ArrayList<CardData> getCardsList(List<ParseCard> parseCardList, String mainUserID, boolean forReplies) {

        ArrayList<CardData> cardsList = new ArrayList<>();

        for (ParseCard parseCard : parseCardList) {

            CardData cardData = new CardData();

            cardData.setParentCard(parseCard);

            cardData.setCard(parseCard, mainUserID);

            if(forReplies) cardData.set_localisation_from_gps(parseCard);

            cardsList.add(cardData);
        }

        return cardsList;

    }


    public static String get_localisation_from_gps(float latitude, float longitude) {

        Geocoder gcd = new Geocoder(MainActivity.context, Locale.getDefault());

        try {

            BigDecimal bd = new BigDecimal(latitude).setScale(3, RoundingMode.HALF_EVEN);
            float lat = (float) bd.doubleValue();

            List<Address> addresses = null;
            addresses = gcd.getFromLocation(lat, longitude, 1);

            if (addresses.size() > 0) return (addresses.get(0).getLocality() + ", " + addresses.get(0).getCountryName());

            else return ("");

        } catch (IOException e) {

            return ("");
            //e.printStackTrace();
        }

    }


    public void set_localisation_from_gps(ParseCard parseCard) {

        Geocoder gcd = new Geocoder(MainActivity.context, Locale.getDefault());

        try {

            BigDecimal bd = new BigDecimal(parseCard.getLatitude()).setScale(3, RoundingMode.HALF_EVEN);
            float lat = (float) bd.doubleValue();

            List<Address> addresses = null;
            addresses = gcd.getFromLocation(lat, parseCard.getLongitude(), 1);

            if (addresses.size() > 0) setLocalisation(addresses.get(0).getLocality() + ", " + addresses.get(0).getCountryName());

            else setLocalisation("");

        } catch (IOException e) {

            setLocalisation("");
            e.printStackTrace();
        }

    }


    public static CardData getCard(ParseCard parseCard) {

        CardData cardData = new CardData();

        cardData.setParentCard(parseCard);

        cardData.setCard(parseCard, "");

        return cardData;
    }



    // PARCELABLE -------------------------------- //

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        dest.writeString(card_id);
        dest.writeString(author_id);
        dest.writeString(user_pict_url);
        dest.writeString(parent_card_id);
        dest.writeString(parent_author_id);
        dest.writeString(chat_id);

        dest.writeString(timestamp);
        dest.writeString(author_full_name);
        dest.writeString(parent_author_full_name);

        dest.writeString(pict_url);
        dest.writeString(main_content);
        dest.writeString(link_url);

        dest.writeFloat(latitude);
        dest.writeFloat(longitude);

        dest.writeValue(isStory);

        dest.writeValue(curated);
        dest.writeString(curated_tagline);

        dest.writeInt(nb_upvotes);
        dest.writeInt(nb_replies);
        dest.writeInt(nb_pass);

        dest.writeString(Tags.TagsString(tags_list));

        dest.writeValue(author_listening);

    }

    // this is used to regenerate your object. All Parcelables must have a CREATOR that implements these two methods
    public static final Parcelable.Creator<CardData> CREATOR = new Parcelable.Creator<CardData>() {

        public CardData createFromParcel(Parcel in) {
            return new CardData(in);
        }

        public CardData[] newArray(int size) {
            return new CardData[size];
        }

    };

    public CardData(Parcel in) {

        card_id = in.readString();
        author_id = in.readString();
        user_pict_url = in.readString();

        parent_card_id = in.readString();
        parent_author_id = in.readString();
        chat_id = in.readString();

        timestamp = in.readString();
        author_full_name = in.readString();
        parent_author_full_name = in.readString();

        pict_url = in.readString();
        main_content = in.readString();
        link_url = in.readString();

        latitude = in.readFloat();
        longitude = in.readFloat();

        isStory = (boolean) in.readValue(null);

        curated = (boolean) in.readValue(null);
        curated_tagline = in.readString();

        nb_upvotes = in.readInt();
        nb_replies = in.readInt();
        nb_pass = in.readInt();
        tags_list = Tags.Spliter(in.readString());

        author_listening = (boolean) in.readValue(null);

    }
}
