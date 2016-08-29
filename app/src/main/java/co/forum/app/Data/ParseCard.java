package co.forum.app.Data;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;


@ParseClassName("ParseCard")
public class ParseCard extends ParseObject {

    public static String JOIN_CONTENT_USER = "content_to_user";
    public static String JOIN_REPLY_PARENT_USER = "reply_to_parent_user";
    public static String JOIN_REPLY_PARENT_CARD = "reply_to_parent_card";
    public static String JOIN_CHAT = "chat";
    public static String JOIN_PICT = "pict";

    public static String PROFILPICT_URL = "author_profil_pict";
    public static String LINK_URL = "link_url";
    public static String CURATED = "curated";
    public static String CURATED_TAGLINE = "curated_tagline";

    public static String AUTHOR_LISTENING = "author_is_listening";

    public static String IS_STORY = "is_story"; // get from JOIN_CHAT

    public ParseCard(){}


    public ParseCard(CardData card, boolean isReply) {

        setAuthorFullName(card.getAuthor_full_name());
        set_Curated(card.get_curated());

        if(isReply) set_Curated_tagline(""); //chelou
        else set_Curated_tagline(card.getCurated_tagline()); //chelou

        setContent(card.getMain_content());
        setLinkUrl(card.getLink_url());
        setTags(card.getTags_list());

        setUpvoteCount(card.getNb_upvotes());
        setReplyCount(card.getNb_replies());
        setPassCount(card.getNb_pass());

        setLatitude(card.getLatitude());
        setLongitude(card.getLongitude());

        put(AUTHOR_LISTENING, card.isAuthor_listening());
        put(JOIN_CONTENT_USER, ParseUser.getCurrentUser());

        if(isReply) {

            ParseCard parentCard = ParseCard.createWithoutData(ParseCard.class, card.getParent_card_id());

            setParentAuthorFullName(card.getParent_author_full_name());

            put(JOIN_REPLY_PARENT_CARD, parentCard);

            put(JOIN_REPLY_PARENT_USER, ParseUser.createWithoutData(ParseUser.class, card.getParent_author_id()));

        }

    }


    private int chatCount = 0;

    public void setChatCount(int chatCount) {
        this.chatCount = chatCount;
    }

    public int getChatCount() {
        return chatCount;
    }

    public void setChat(Object value) { put(JOIN_CHAT, value); }

    public void set_isStory(boolean value) { put(IS_STORY, value); }

    public boolean get_isStory() { return getBoolean(IS_STORY); }

    public void setLatitude(float value) { put("latitude", value); }

    public void setLongitude(float value) { put("longitude", value); }

    public float getLatitude() { return (float)getDouble("latitude"); }

    public float getLongitude() { return (float)getDouble("longitude"); }


    public String getParentAuthorFullName() { return getString("parent_author_full_name"); }

    public void setParentAuthorFullName(String value) { put("parent_author_full_name", value); }


    public void set_ProfilpictUrl(String value) {
        put(PROFILPICT_URL, value);
    }

    public String get_ProfilpictUrl() {
        return getString(PROFILPICT_URL);
    }


    public void set_Pict(Object value) {
        put(JOIN_PICT, value);
    }

    public Object get_Pict() {
        return get(JOIN_PICT);
    }


    public void set_listening_state(boolean value) { put(AUTHOR_LISTENING, value); }

    public boolean get_listening_state() {
        return getBoolean(AUTHOR_LISTENING);
    }


    public void set_Curated(boolean value) { put(CURATED, value); }

    public boolean get_Curated() {
        return getBoolean(CURATED);
    }

    public void set_Curated_tagline(String value) {
        put(CURATED_TAGLINE, value);
    }

    public String get_Curated_tagline() {
        return getString(CURATED_TAGLINE);
    }

    public String getAuthorFullName() { return getString("author_full_name"); }

    public void setAuthorFullName(String value) { put("author_full_name", value); }

    public String getContent() { return getString("content"); }

    public void setContent(String value) { put("content", value); }

    public String getLinkUrl() { return getString(LINK_URL); }

    public void setLinkUrl(String value) {

        if(value == null) put(LINK_URL, "");

        else put(LINK_URL, value);

    }




    public List<String> getTags() {
        return getList("tags");
    }

    public void setTags(ArrayList<String> tags) {
        put("tags", tags);
    }


    public int getReplyCount() {
        return getInt("reply_count");
    }

    public void setReplyCount(int value) {
        put("reply_count", value);
    }


    public int getUpvoteCount() {
        return getInt("upvote_count");
    }

    public void setUpvoteCount(int value) {
        put("upvote_count", value);
    }


    public int getPassCount() {
        return getInt("pass_count");
    }

    public void setPassCount(int value) {
        put("pass_count", value);
    }


}