package co.forum.app;

import android.content.Context;
import android.content.SharedPreferences;

import com.parse.ParseUser;

import java.util.Date;

public class SharedPref {

    public static final String SP_FILE = "sPCardData";

    private String KEY_LOG_STATUS = "log_status";

    SharedPreferences localData;

    public SharedPref(Context context) {

        localData = context.getSharedPreferences(SP_FILE, Context.MODE_PRIVATE);

    }

    public boolean userIsConnected() {

        return localData.getBoolean(KEY_LOG_STATUS, false);
    }

    public void setUserConnectedStatus(boolean logStatus) {

        SharedPreferences.Editor spEditor = localData.edit();

        spEditor.putBoolean(KEY_LOG_STATUS, logStatus);

        spEditor.commit();

    }

    public boolean isUserCard(String authorID) {
        return authorID.equals(localData.getString(KEY_USER_ID, "null"));
    }


    public static String KEY_DRAWER = "drawer_learned";

    public static String KEY_USER_ID = "user_id";
    public static String KEY_NAME = "user_name";
    public static String KEY_LATITUDE = "latitude";
    public static String KEY_LONGITUDE = "longitude";
    public static String KEY_SURNAME = "user_surname";
    public static String KEY_FULL_NAME = "full_name";
    public static String KEY_BIO = "user_bio";
    public static String KEY_BIO_URL = "user_bio_link";
    public static String KEY_EMAIL = "user_email";
    public static String KEY_CARDS_COUNT = "cards_count";
    public static String KEY_UPVOTES_COUNT = "upbotes_count";
    public static String KEY_REPLIES_COUNT = "replies_count";
    public static String KEY_PICT_URL = "profil_pict_url";

    public static String KEY_CURATED_ACCOUNT = "curated";
    public static String KEY_CURATED_TAGLINE = "curated_tagline";

    public static String KEY_MOST_RECENT_TIMESTAMP = "most_recent_timestamp";


    public static String KEY_GPS_AUTHORISATION = "user_allows_gps";

    public void store_newProfilPict(String imageURL) {

        SharedPreferences.Editor spEditor = localData.edit();
        spEditor.putString(KEY_PICT_URL, imageURL);
        spEditor.commit();
    }

    public void storeUserData(ParseUser user) {

        SharedPreferences.Editor spEditor = localData.edit();

        if(user.getParseFile("profil_pict") != null)
            spEditor.putString(KEY_PICT_URL, user.getParseFile("profil_pict").getUrl());

        else spEditor.putString(KEY_PICT_URL, "NULL");

        spEditor.putString(KEY_USER_ID, user.getObjectId());
        spEditor.putString(KEY_NAME, user.getString("name"));
        spEditor.putString(KEY_SURNAME, user.getString("surname"));
        spEditor.putString(KEY_BIO, user.getString("bio"));

        if(user.getString("bio_link") == null )

            spEditor.putString(KEY_BIO_URL, user.getString(""));

        else spEditor.putString(KEY_BIO_URL, user.getString("bio_link"));

        spEditor.putString(KEY_EMAIL, user.getEmail());

        spEditor.putInt(KEY_CARDS_COUNT, user.getInt("cards_count") + user.getInt("cards_reply_count"));
        spEditor.putInt(KEY_UPVOTES_COUNT, user.getInt("upvotes_count"));
        spEditor.putInt(KEY_REPLIES_COUNT, user.getInt("replies_count"));

        spEditor.putBoolean(KEY_CURATED_ACCOUNT, user.getBoolean("curated"));

        if(user.getBoolean("curated")) spEditor.putString(KEY_CURATED_TAGLINE, user.getString("curated_tagline"));

        else spEditor.putString(KEY_CURATED_TAGLINE, "");

        spEditor.commit();

    }


    public void setUserData(String key, boolean value) {

        SharedPreferences.Editor spEditor = localData.edit();

        spEditor.putBoolean(key, value);

        spEditor.commit();
    }


    public void setUserData(String key, float value) {

        SharedPreferences.Editor spEditor = localData.edit();

        spEditor.putFloat(key, value);

        spEditor.commit();
    }

    public void setUserData(String key, int value) {

        SharedPreferences.Editor spEditor = localData.edit();

        spEditor.putInt(key, value);

        spEditor.commit();
    }

    public void setUserData(String key, String value) {

        SharedPreferences.Editor spEditor = localData.edit();

        spEditor.putString(key, value);

        spEditor.commit();
    }

    public float getGPS(String key) {

        if(key.equals(KEY_LATITUDE)) return localData.getFloat(KEY_LATITUDE, 0);
        if(key.equals(KEY_LONGITUDE)) return localData.getFloat(KEY_LONGITUDE, 0);

        return 0;

    }

    public boolean getGPS_authorisation() {

        return localData.getBoolean(KEY_GPS_AUTHORISATION, true);
    }


    public boolean getCurated() {

        return localData.getBoolean(KEY_CURATED_ACCOUNT, false);
    }

    public String getUserData(String key) {

        if(key.equals(KEY_PICT_URL)) return localData.getString(KEY_PICT_URL, "NULL");
        if(key.equals(KEY_USER_ID)) return localData.getString(KEY_USER_ID, "null");
        if(key.equals(KEY_NAME)) return localData.getString(KEY_NAME, "null");
        if(key.equals(KEY_SURNAME)) return localData.getString(KEY_SURNAME, "null");
        if(key.equals(KEY_FULL_NAME)) return localData.getString(KEY_NAME, "null") + " " + localData.getString(KEY_SURNAME, "null");
        if(key.equals(KEY_BIO)) return localData.getString(KEY_BIO, "");
        if(key.equals(KEY_BIO_URL)) return localData.getString(KEY_BIO_URL, "");
        if(key.equals(KEY_CURATED_TAGLINE)) return localData.getString(KEY_CURATED_TAGLINE, "null");

        return "empty";
    }


    public int getUserCount(String key) {

        if(key.equals(KEY_CARDS_COUNT)) return localData.getInt(KEY_CARDS_COUNT, -1);
        if(key.equals(KEY_UPVOTES_COUNT)) return localData.getInt(KEY_UPVOTES_COUNT, -1);
        if(key.equals(KEY_REPLIES_COUNT)) return localData.getInt(KEY_REPLIES_COUNT, -1);

        return -10;
    }

    public void incrementUserCount(String key) {

        if(key.equals(KEY_CARDS_COUNT)) {

            SharedPreferences.Editor spEditor = localData.edit();

            spEditor.putInt(KEY_CARDS_COUNT, localData.getInt(KEY_CARDS_COUNT, 0) + 1);

            spEditor.commit();


        }
    }



    public Date get_most_recent_timestamp_parse() {

        Date date = new Date(localData.getLong(KEY_MOST_RECENT_TIMESTAMP, 0));

        return date;
    }


    public void store_most_recent_timestamp_parse(Date timestamp) {

        SharedPreferences.Editor spEditor = localData.edit();

        long millis = timestamp.getTime();
        spEditor.putLong(KEY_MOST_RECENT_TIMESTAMP, millis);
        spEditor.commit();

    }
}
