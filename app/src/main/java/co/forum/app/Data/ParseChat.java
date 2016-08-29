package co.forum.app.Data;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;


@ParseClassName("ParseChat")
public class ParseChat extends ParseObject {

    public static final String JOIN_FIRST_CARD = "first_card";
    public static final String JOIN_STARTER_USER = "user_1";
    public static final String JOIN_REPLIER_USER = "user_2";

    public static final String IS_STORY = "is_story";
    public static final String STORY_TITLE = "story_title";

    public static final String FIRTS_TAG = "first_tag";
    public static final String COUNT_COMMENTS = "count_comments";
    public static final String LAST_COMMENT = "lastComment";
    public static final String LAST_COMMENTER_ID = "lastCommenterID";

    public static final String STARTER_CHECKED = "starter_checked";
    public static final String REPLIER_CHECKED = "replier_checked";

    public static final String STARTER_IS_LISTENING = "starter_is_listening";
    public static final String REPLIER_IS_LISTENING = "replier_is_listening";

    public ParseChat(){}

    public ParseChat(Object firstCard_ID, Object user1_ID, Object user2_ID, String parent_card_first_tag, int count) {

        put(JOIN_FIRST_CARD, firstCard_ID);

        put(JOIN_STARTER_USER, user1_ID);

        put(JOIN_REPLIER_USER, user2_ID);

        put(FIRTS_TAG, parent_card_first_tag);

        put(COUNT_COMMENTS, count);

        put(STARTER_CHECKED, true);

        put(REPLIER_CHECKED, true);

        if(!user1_ID.equals(user2_ID)) {

            put(REPLIER_IS_LISTENING, true);

            put(IS_STORY, false);

        } else put(IS_STORY, true);


        ArrayList<String> talkersID = new ArrayList<>();
        talkersID.add(((ParseUser)user1_ID).getObjectId());
        talkersID.add(((ParseUser)user2_ID).getObjectId());

        setUsersID(talkersID);

    }


    public List<String> getUsersID() {
        return getList("userIDs");
    }

    public void setUsersID(ArrayList<String> usersID) {
        put("userIDs", usersID);
    }

    public void setCountComments(int value) {
        put(COUNT_COMMENTS, value);
    }

    public void subscribe_commenter(String commenter) {

        put(commenter, true);

    }

    public void unsubscribe_commenter(String commenter) {

        put(commenter, false);

    }

    public void setStarterChecked(boolean value) {
        put(STARTER_CHECKED, value);
    }

    public void setReplierChecked(boolean value) {
        put(REPLIER_CHECKED, value);
    }


    public void setLastCommenterID(String lastCommentID) { put(LAST_COMMENTER_ID, lastCommentID); }

    public String getLastCommentID() {
        return getString(LAST_COMMENTER_ID);
    }

    public String getFirtsTag() {
        return getString(FIRTS_TAG);
    }


    public void setLastComment(String lastComment) { put(LAST_COMMENT, lastComment); }

    public String getLastComment() {
        return getString(LAST_COMMENT);
    }

    public int getCountComments() {
        return getInt(COUNT_COMMENTS);
    }


    public boolean isStory() {return getBoolean(IS_STORY);}

    public void setStoryTitle(String storyTitle) { put(STORY_TITLE, storyTitle); }

    public String getStoryTitle() {
        return getString(STORY_TITLE);
    }


    /*

    - compteur int à 2
    - id de la carte parent
    - id de l'auteur starter
    - id de l'auteur replier



     */
}
