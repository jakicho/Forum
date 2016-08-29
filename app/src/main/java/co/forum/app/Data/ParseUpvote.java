package co.forum.app.Data;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

@ParseClassName("ParseUpvote")
public class ParseUpvote extends ParseObject{

    public static final String JOIN_CARD = "card";
    public static final String JOIN_RECEIVER = "card_author";
    public static final String JOIN_UPVOTER = "upvoter";

    public static final String LATITUDE = "latitude";
    public static final String LONGITUDE = "longitude";

    public ParseUpvote(){}

    public ParseUpvote(String cardID, String upvoterID, String receiverID,
                       float latitude, float longitude){

        final ParseCard card = ParseCard.createWithoutData(ParseCard.class, cardID);
        final ParseUser upvoter = ParseUser.createWithoutData(ParseUser.class, upvoterID);
        final ParseUser receiver = ParseUser.createWithoutData(ParseUser.class, receiverID);

        put(JOIN_CARD, card);
        put(JOIN_UPVOTER, upvoter);
        put(JOIN_RECEIVER, receiver);

        put(LATITUDE, latitude);
        put(LONGITUDE, longitude);

    }
}
