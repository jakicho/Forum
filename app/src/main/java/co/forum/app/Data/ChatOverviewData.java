package co.forum.app.Data;

import android.text.format.DateUtils;

import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ChatOverviewData {

    private String chatID;

    private String other_user_id;
    private String other_user_pict;
    private String other_username;

    private boolean is_story;
    private String story_title;
    private String first_pict;

    private int nb_messages;
    private String firstTag;

    private String message;
    private String last_commenterID;
    private String last_timestamp;

    private boolean uptodate;
    private boolean subscribing;
    private String role;

    public static final String STARTER = "starter";
    public static final String REPLIER = "replier";


    private String starter_id;
    private String starter_pict;
    private String starter_name;

    private String replier_id;
    private String replier_pict;
    private String replier_name;


    public ChatOverviewData(){}

    // single chat
    public ChatOverviewData(String chatID,
                            String other_user_pict,
                            String other_user_id,
                            String other_username,
                            int nb_messages,
                            String firstTag,
                            String last_commenterID,
                            String last_message,
                            Date timestamp,
                            String role,
                            boolean subscribing,
                            boolean uptodate) {

        this.chatID = chatID;
        this.other_user_pict = other_user_pict;
        this.other_user_id = other_user_id;
        this.other_username = other_username;
        this.nb_messages = nb_messages;
        this.last_commenterID = last_commenterID;
        this.message = last_message;
        this.firstTag = firstTag;
        this.role = role;
        this.subscribing = subscribing;
        this.uptodate = uptodate;

        String timeAgo = String.valueOf(DateUtils.getRelativeTimeSpanString(

                timestamp.getTime(),
                System.currentTimeMillis(),
                DateUtils.MINUTE_IN_MILLIS));


        this.last_timestamp = timeAgo;

    }

    // double chat

    public ChatOverviewData(String chatID,
                            String starter_pict,
                            String starter_id,
                            String starter_name,

                            String replier_pict,
                            String replier_id,
                            String replier_name,

                            int nb_messages,
                            String firstTag,
                            String last_commenterID,

                            String last_message,
                            Date timestamp) {

        this.chatID = chatID;

        this.starter_pict = starter_pict;
        this.starter_id = starter_id;
        this.starter_name = starter_name;

        this.replier_pict = replier_pict;
        this.replier_id = replier_id;
        this.replier_name = replier_name;

        this.nb_messages = nb_messages;
        this.firstTag = firstTag;
        this.last_commenterID = last_commenterID;
        this.message = last_message;

        String timeAgo = String.valueOf(DateUtils.getRelativeTimeSpanString(

                timestamp.getTime(),
                System.currentTimeMillis(),
                DateUtils.MINUTE_IN_MILLIS));


        this.last_timestamp = timeAgo;

    }

    // story
    public ChatOverviewData(String storyID,
                            String author_pict,
                            String author_id,
                            String author_username,
                            int nb_parts,
                            String firstTag,
                            String first_post,
                            String first_pict,
                            Date timestamp,
                            String story_title) {

        this.chatID = storyID;
        this.other_user_pict = author_pict;
        this.other_user_id = author_id;
        this.other_username = author_username;
        this.nb_messages = nb_parts;
        this.firstTag = firstTag;
        this.message = first_post;
        this.first_pict = first_pict;
        this.story_title = story_title;

        String timeAgo = String.valueOf(DateUtils.getRelativeTimeSpanString(

                timestamp.getTime(),
                System.currentTimeMillis(),
                DateUtils.MINUTE_IN_MILLIS));


        this.last_timestamp = timeAgo;

    }


    public String getFirst_pict() {
        return first_pict;
    }

    public boolean isSubscribing() {
        return subscribing;
    }

    public void setSubscribing(boolean subscribing) {
        this.subscribing = subscribing;
    }

    public String getRole() {
        return role;
    }

    public boolean isUptodate() {
        return uptodate;
    }

    public String getChatID() {
        return chatID;
    }

    public String getOther_user_id() {
        return other_user_id;
    }

    public String getOther_user_pict() {
        return other_user_pict;
    }

    public String getStarter_name() {
        return starter_name;
    }

    public String getReplier_name() {
        return replier_name;
    }

    public String getStarter_pict() {
        return starter_pict;
    }

    public String getReplier_pict() {
        return replier_pict;
    }

    public String getOther_username() {
        return other_username;
    }

    public int getNb_messages() {
        return nb_messages;
    }

    public String getFirstTag() { return firstTag; }

    public String getLastCommenterID() {
        return last_commenterID;
    }

    public String getComment() {
        return message;
    }

    public String getLast_timestamp() {
        return last_timestamp;
    }


    public String getStory_title() {
        return story_title;
    }

    public void setStory_title(String story_title) {
        this.story_title = story_title;
    }

    public static ArrayList<ChatOverviewData> getChatsList(List<ParseChat> parseChatsList, String userID) {

        ArrayList<ChatOverviewData> list = new ArrayList<>();

        for (ParseChat parseChat : parseChatsList) {

            final ParseUser starterUser = parseChat.getParseUser(ParseChat.JOIN_STARTER_USER);
            final ParseUser replierUser = parseChat.getParseUser(ParseChat.JOIN_REPLIER_USER);

            String other_user_pict = "";
            String other_user_name = "";
            String other_user_id = "";
            boolean uptodate = true;
            String role = "";
            boolean subscribing = false;

            if(userID.equals(starterUser.getObjectId())) {

                if(replierUser.getParseFile("profil_pict") != null)
                    other_user_pict = replierUser.getParseFile("profil_pict").getUrl();
                else other_user_pict = "NULL";

                role = STARTER;
                subscribing = parseChat.getBoolean(ParseChat.STARTER_IS_LISTENING);
                uptodate = parseChat.getBoolean(ParseChat.STARTER_CHECKED);

                other_user_name = replierUser.getString("name") + " " + replierUser.getString("surname");
                other_user_id = replierUser.getObjectId();

            } else if(userID.equals(replierUser.getObjectId())) {

                if(starterUser.getParseFile("profil_pict") != null)
                    other_user_pict = starterUser.getParseFile("profil_pict").getUrl();
                else other_user_pict = "NULL";

                role = REPLIER;
                subscribing = parseChat.getBoolean(ParseChat.REPLIER_IS_LISTENING);
                uptodate = parseChat.getBoolean(ParseChat.REPLIER_CHECKED);

                other_user_name = starterUser.getString("name") + " " + starterUser.getString("surname");
                other_user_id = starterUser.getObjectId();

            }

            int nb_messages = parseChat.getCountComments();

            String firstTag = "";
            if(parseChat.getFirtsTag() != null) firstTag = parseChat.getFirtsTag();


            String last_commenterID = parseChat.getLastCommentID();
            if(last_commenterID == null) last_commenterID = "";

            String last_message = parseChat.getLastComment();

            Date timestamp = parseChat.getUpdatedAt();//Date timestamp = parseChat.getDate("updateAt");

            ChatOverviewData item = new ChatOverviewData(
                    parseChat.getObjectId(),
                    other_user_pict,
                    other_user_id,
                    other_user_name,
                    nb_messages,
                    firstTag,
                    last_commenterID,
                    last_message,
                    timestamp,
                    role,
                    subscribing,
                    uptodate);

            list.add(item);

        }
        return list;
    }


    public static ArrayList<ChatOverviewData> getCommunityChatsList(List<ParseChat> parseChatsList, String userID) {

        ArrayList<ChatOverviewData> list = new ArrayList<>();

        for (ParseChat parseChat : parseChatsList) {

            final ParseUser starterUser = parseChat.getParseUser(ParseChat.JOIN_STARTER_USER);
            final ParseUser replierUser = parseChat.getParseUser(ParseChat.JOIN_REPLIER_USER);

            String starter_pict;

            if(starterUser.getParseFile("profil_pict") != null)
                starter_pict = starterUser.getParseFile("profil_pict").getUrl();
            else starter_pict = "NULL";

            String starter_name = starterUser.getString("name") + " " + starterUser.getString("surname");
            String starter_id = starterUser.getObjectId();

            String replier_pict;

            if(replierUser.getParseFile("profil_pict") != null)
                replier_pict = replierUser.getParseFile("profil_pict").getUrl();
            else replier_pict = "NULL";

            String replier_name = replierUser.getString("name") + " " + replierUser.getString("surname");
            String replier_id = replierUser.getObjectId();


            int nb_messages = parseChat.getCountComments();

            String firstTag = "";
            if(parseChat.getFirtsTag() != null) firstTag = parseChat.getFirtsTag();

            String last_commenterID = parseChat.getLastCommentID();
            if(last_commenterID == null) last_commenterID = "";

            String last_message = parseChat.getLastComment();

            Date timestamp = parseChat.getUpdatedAt();//Date timestamp = parseChat.getDate("updateAt");

            ChatOverviewData item = new ChatOverviewData(
                    parseChat.getObjectId(),

                    starter_pict, starter_id, starter_name,
                    replier_pict, replier_id, replier_name,

                    nb_messages,
                    firstTag,
                    last_commenterID,
                    last_message,
                    timestamp);

            list.add(item);

        }
        return list;
    }


    public static ArrayList<ChatOverviewData> getStoriesList(List<ParseChat> parseChatsList) {

        ArrayList<ChatOverviewData> list = new ArrayList<>();

        for (ParseChat parseChat : parseChatsList) {

            final ParseUser author = parseChat.getParseUser(ParseChat.JOIN_STARTER_USER);
            final ParseCard firstCard = (ParseCard) parseChat.getParseObject(ParseChat.JOIN_FIRST_CARD);

            String author_pict = "";

            if(author.getParseFile("profil_pict") != null)
                author_pict = author.getParseFile("profil_pict").getUrl();
            String author_name = author.getString("name") + " " + author.getString("surname");
            String author_id = author.getObjectId();

            String story_title = parseChat.getStoryTitle();

            int nb_parts = parseChat.getCountComments();

            String firstTag = "";
            if(parseChat.getFirtsTag() != null) firstTag = parseChat.getFirtsTag();


            String first_post = firstCard.getContent();

            String first_pict ="";

            if(firstCard.getParseFile("pict")!= null) first_pict = firstCard.getParseFile("pict").getUrl();


            Date timestamp = parseChat.getCreatedAt();

            ChatOverviewData item = new ChatOverviewData(
                    parseChat.getObjectId(),
                    author_pict,
                    author_id,
                    author_name,
                    nb_parts,
                    firstTag,
                    first_post,
                    first_pict,
                    timestamp,
                    story_title);

            list.add(item);

        }
        return list;
    }
}
