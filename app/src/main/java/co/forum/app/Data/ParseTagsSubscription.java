package co.forum.app.Data;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

@ParseClassName("ParseTagsSubscription")
public class ParseTagsSubscription extends ParseObject{

    public static String JOIN_USER = "follower";
    public static String JOIN_FOLLOWERS_COUNTER_OBJECT = "nb_followers";
    public static String NB_TAGS = "nb_tags";
    public static String TAGS = "tags";

    private int nb_followers;

    public ParseTagsSubscription(){}

    public ParseTagsSubscription(ArrayList<String> tags){

        put(JOIN_USER, ParseUser.getCurrentUser());

        put(NB_TAGS, tags.size());

        setTags(tags);

    }


    public List<String> getTags() {
        return getList("tags");
    }

    public void setTags(ArrayList<String> tags) {
        put("tags", tags);
    }



    public int getNb_followers() {
        return nb_followers;
    }

    public void setNb_followers(int nb_followers) {
        this.nb_followers = nb_followers;
    }


    // --------------

    private String description; // que pour les Forum Feeds

    public String get_description() {
        return description;
    }

    public void set_description(String description) {
        this.description = description;
    }

}
