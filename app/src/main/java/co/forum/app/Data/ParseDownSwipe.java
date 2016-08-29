package co.forum.app.Data;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

@ParseClassName("ParseDownSwipe")
public class ParseDownSwipe extends ParseObject{

    public static final String JOIN_CARD = "card";
    public static final String JOIN_CARD_AUTHOR = "card_author";
    public static final String JOIN_DOWN_SWIPER = "user";

    public static final String LATITUDE = "latitude";
    public static final String LONGITUDE = "longitude";

    public ParseDownSwipe(){}

    public ParseDownSwipe(String cardID, String downswiperID, String cardAuthorID, float latitude, float longitude){

        final ParseCard card = ParseCard.createWithoutData(ParseCard.class, cardID);
        final ParseUser downswiper = ParseUser.createWithoutData(ParseUser.class, downswiperID);
        final ParseUser author = ParseUser.createWithoutData(ParseUser.class, cardAuthorID);

        put(JOIN_CARD, card);
        put(JOIN_CARD_AUTHOR, author);
        put(JOIN_DOWN_SWIPER, downswiper);

        put(LATITUDE, latitude);
        put(LONGITUDE, longitude);
    }
}
