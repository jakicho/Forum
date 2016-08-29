package co.forum.app.Data;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

@ParseClassName("ParseNotification")
public class ParseNotification extends ParseObject {

    public static final String JOIN_RECEIVER = "receiver";
    public static final String JOIN_SENDER = "sender";
    public static final String JOIN_CARD = "card";

    public static final String IS_OPENED = "is_opened";
    public static final String SENDER_ID = "sender_id";
    public static final String SENDER_PROFILPICT_URL = "sender_pict_url";
    public static final String SENDER_FULLNAME = "sender_fullname";
    public static final String CARD_CONTENT = "card_content";
    public static final String TYPE = "type";
    public static final String LATITUDE = "latitude";
    public static final String LONGITUDE = "longitude";


    public static final int TYPE_UPVOTE = 0;
    public static final int TYPE_COMMENT = 1;


    public ParseNotification(){}


    public ParseNotification(String receiverID, String senderID, String cardID,
                             int type, String senderFullName, String cardContent,
                             float latitude, float longitude) {

        ParseUser receiver = ParseUser.createWithoutData(ParseUser.class, receiverID);
        ParseUser sender = ParseUser.createWithoutData(ParseUser.class, senderID);
        ParseCard card = ParseCard.createWithoutData(ParseCard.class, cardID);

        put(JOIN_RECEIVER, receiver);
        put(JOIN_SENDER, sender);
        put(JOIN_CARD, card);

        put(LATITUDE, latitude);
        put(LONGITUDE, longitude);

        setIs_opened(false);

        setSender_ID(senderID);

        setSender_FullName(senderFullName);

        setCard_Content(cardContent);

        setType(type);
    }


    public float getLatitude() { return (float)getDouble(LATITUDE); }

    public float getLongitude() { return (float)getDouble(LONGITUDE); }

    public void setSenderProfilpictUrl(String value) {
        put(SENDER_PROFILPICT_URL, value);
    }

    public String getSenderProfilpictUrl() {
        return getString(SENDER_PROFILPICT_URL);
    }


    public String getSender_ID() {
        return getString(SENDER_ID);
    }

    public void setSender_ID(String value) {
        put(SENDER_ID, value);
    }



    public String getSender_FullName() {
        return getString(SENDER_FULLNAME);
    }

    public void setSender_FullName(String value) {
        put(SENDER_FULLNAME, value);
    }




    public String getCard_Content() {
        return getString(CARD_CONTENT);
    }

    public void setCard_Content(String value) {
        put(CARD_CONTENT, value);
    }



    public int getType() {
        return getInt(TYPE);
    }

    public void setType(int value) {
        put(TYPE, value);
    }



    public boolean getIs_opened() {
        return getBoolean(IS_OPENED);
    }

    public void setIs_opened(boolean value) {
        put(IS_OPENED, value);
    }

}
