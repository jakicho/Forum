package co.forum.app.Data;

import android.location.Address;
import android.location.Geocoder;
import android.text.format.DateUtils;

import com.parse.ParseObject;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import co.forum.app.MainActivity;

public class NotifData {

    public static final int NOTIF_UPVOTE = 0;
    public static final int NOTIF_REPLY = 1;

    public static final int NOTIF_FAVORITE = 2;
    public static final int NOTIF_DISCUSSION = 3;



    public int notif_type;
    public String notifID;
    public String senderID;
    public String senderProfilURL;
    public String sender_name;
    public String cardID;
    public String card_content;

    public String localisation;

    public String timestamp;
    public boolean is_opened;

    public NotifData(){}

    public NotifData(int notif_type, String notifID,
                     String senderID, String sender_name, String pictURL,
                     String cardID, String card_content,
                     float latitude, float longitude,
                     Date timestamp, boolean is_opened) {

        this.notif_type = notif_type;

        this.notifID = notifID;

        this.senderID = senderID;

        this.sender_name = sender_name;

        this.senderProfilURL = pictURL;

        this.cardID = cardID;

        this.card_content = card_content;

        Geocoder gcd = new Geocoder(MainActivity.context, Locale.getDefault());

        try {

            BigDecimal bd = new BigDecimal(latitude).setScale(4, RoundingMode.HALF_EVEN);
            float lat = (float) bd.doubleValue();

            List<Address> addresses = gcd.getFromLocation(lat, longitude, 1);

            if (addresses.size() > 0) localisation = addresses.get(0).getLocality() + ", " + addresses.get(0).getCountryName();
            else localisation ="";

        } catch (IOException e) {
            localisation ="";
            e.printStackTrace();
        }


        String timeAgo = String.valueOf(DateUtils.getRelativeTimeSpanString(

                timestamp.getTime(),
                System.currentTimeMillis(),
                DateUtils.MINUTE_IN_MILLIS));


        this.timestamp = timeAgo;

        this.is_opened = is_opened;
    }





    public static ArrayList<NotifData> getNotifList(List<ParseNotification> parseNotificationList) {

        ArrayList<NotifData> notifList = new ArrayList<>();

        for (ParseNotification parseNotif : parseNotificationList) {

            NotifData notifData = new NotifData(
                    parseNotif.getType(),
                    parseNotif.getObjectId(),

                    ((ParseObject)parseNotif.get(ParseNotification.JOIN_SENDER)).getObjectId(),
                    parseNotif.getSender_FullName(),
                    parseNotif.getSenderProfilpictUrl(),

                    ((ParseObject)parseNotif.get(ParseNotification.JOIN_CARD)).getObjectId(),
                    parseNotif.getCard_Content(),

                    parseNotif.getLatitude(),
                    parseNotif.getLongitude(),

                    parseNotif.getCreatedAt(),
                    parseNotif.getIs_opened()
                    );

            notifList.add(notifData);
        }


        return notifList;
    }
}
