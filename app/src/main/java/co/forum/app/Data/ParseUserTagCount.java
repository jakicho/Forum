package co.forum.app.Data;

import com.parse.ParseClassName;
import com.parse.ParseObject;

@ParseClassName("ParseUserTagCount")
public class ParseUserTagCount  extends ParseObject {

    public static String JOIN_TAG_ID = "tag_id";

    public static String JOIN_USER_ID = "user_id";

    public ParseUserTagCount(){}

    public ParseUserTagCount(String tag, Object tagID, Object userID){

        setJoinUserId(userID);

        setJoinTagId(tagID);

        setTag(tag);

        setCounter(1);

    }


    public void setJoinTagId(Object tagId){
        put(JOIN_TAG_ID, tagId);

    }

    public void setJoinUserId(Object userId) {
        put(JOIN_USER_ID, userId);
    }

    public String getTag() {
        return getString("tag");
    }

    public void setTag(String value) {
        put("tag", value);
    }

    public int getCounter() {
        return getInt("counter");
    }

    public void setCounter(int value) {
        put("counter", value);
    }

}
