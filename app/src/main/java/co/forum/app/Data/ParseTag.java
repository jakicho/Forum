package co.forum.app.Data;

import com.parse.ParseClassName;
import com.parse.ParseObject;

@ParseClassName("ParseTag")
public class ParseTag extends ParseObject {

    public ParseTag(){}

    public ParseTag(String tag) {

        setTag(tag);
        setCounter(1);

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