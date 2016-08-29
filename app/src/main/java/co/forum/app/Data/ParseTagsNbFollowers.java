package co.forum.app.Data;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;


@ParseClassName("ParseTagsNbFollowers")
public class ParseTagsNbFollowers extends ParseObject {

    public static String NB_FOLLOWERS = "nb_followers";
    public static String NB_TAGS = "nb_tags";
    public static String TAGS = "tags";

    public ParseTagsNbFollowers(){}

    public ParseTagsNbFollowers(ArrayList<String> tags){

        put(NB_TAGS, tags.size());

        put(NB_FOLLOWERS, 1);

        setTags(tags);

    }

    public int get_nb_followers() {
        return getInt(NB_FOLLOWERS);
    }


    public List<String> getTags() {
        return getList("tags");
    }

    public void setTags(ArrayList<String> tags) {
        put("tags", tags);
    }


}
