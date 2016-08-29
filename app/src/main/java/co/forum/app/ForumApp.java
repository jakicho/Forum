package co.forum.app;

import android.app.ActivityManager;
import android.app.Application;
import android.content.ComponentName;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.parse.Parse;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.http.ParseHttpRequest;
import com.parse.http.ParseHttpResponse;
import com.parse.http.ParseNetworkInterceptor;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

import co.forum.app.Data.ParseCard;
import co.forum.app.Data.ParseChat;
import co.forum.app.Data.ParseDownSwipe;
import co.forum.app.Data.ParseNotification;
import co.forum.app.Data.ParseTag;
import co.forum.app.Data.ParseTagsNbFollowers;
import co.forum.app.Data.ParseTagsSubscription;
import co.forum.app.Data.ParseUpvote;
import co.forum.app.Data.ParseUserTagCount;


public class ForumApp extends Application {

    public static final String PARSE_PROD_APPLICATION_ID = "W88UWhbd6vaAusSBTWNtEGSxFkhmCNQs7YAlyOgh";
    public static final String PARSE_PROD_CLIENT_KEY = "RoECrtC0JCccVkn2RxTuditV5wXJq2ZjFfumRK8Y";

    public static final String PARSE_TEST_APPLICATION_ID = "6gvaaz5CbGtSHYJi0UeYB0GrSVyz1vGhnhcjgMEe";
    public static final String PARSE_TEST_CLIENT_KEY = "fsXef8eCD8qp9aRGKHdSruxOUhL47FliRuHQ4Eip";


    public static final String A_NEW_USER = "new user";
    public static final String A_COME_BACK = "come back";

    public static final String A_NEW_CARD = "new card";
    public static final String A_NEW_COMMENT = "new comment";

    public static final String A_UPVOTE = "swipe upvote";
    public static final String A_PASS = "swipe pass";

    public static final String P_MAIN = "main activity";

    private Tracker mTracker;

    @Override
    public void onCreate() {

        super.onCreate();

        init_parse();

    }




    private void init_parse() {

        Parse.enableLocalDatastore(this); // Enable Local Datastore

        ParseObject.registerSubclass(ParseCard.class);
        ParseObject.registerSubclass(ParseChat.class);
        ParseObject.registerSubclass(ParseTag.class);
        ParseObject.registerSubclass(ParseUserTagCount.class);
        ParseObject.registerSubclass(ParseUpvote.class);
        ParseObject.registerSubclass(ParseDownSwipe.class);
        ParseObject.registerSubclass(ParseNotification.class);
        ParseObject.registerSubclass(ParseTagsSubscription.class);
        ParseObject.registerSubclass(ParseTagsNbFollowers.class);



        //Parse.initialize(this, PARSE_TEST_APPLICATION_ID, PARSE_TEST_CLIENT_KEY);
        //Message.message(context, "TEST DATA BASE");

        //int currentapiVersion = android.os.Build.VERSION.SDK_INT;

        //if(currentapiVersion == android.os.Build.VERSION_CODES.JELLY_BEAN)
        Parse.addParseNetworkInterceptor(new ParseLogInterceptor());

        //Parse.initialize(this, PARSE_TEST_APPLICATION_ID, PARSE_TEST_CLIENT_KEY);
        Parse.initialize(this, PARSE_PROD_APPLICATION_ID, PARSE_PROD_CLIENT_KEY);

        ParseInstallation.getCurrentInstallation().saveInBackground();

        ParseInstallation installation = ParseInstallation.getCurrentInstallation();
        installation.saveInBackground();

    }




    synchronized public Tracker getDefaultTracker() {
        if (mTracker == null) {
            GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
            // To enable debug logging use: adb shell setprop log.tag.GAv4 DEBUG
            mTracker = analytics.newTracker(R.xml.global_tracker);
        }
        return mTracker;
    }


    public static final String DISABLE = "disable";
    public static final String ENABLE = "enable";
    public static String track = DISABLE; // TODO DISABLE FOR DEV

    public static void trackAction(Tracker mTracker, String action) {

        if (track.equals(ENABLE) && !((MainActivity) MainActivity.context).localData.getUserData(SharedPref.KEY_USER_ID).equals(MainActivity.ADMIN_ID)) {

            mTracker.send(new HitBuilders.EventBuilder()
                    .setCategory("Action")
                    .setAction(action)
                    .build());
        }
    }

    public static void trackPage(Tracker mTracker, String pageName) {

        if (track.equals(ENABLE) && !((MainActivity) MainActivity.context).localData.getUserData(SharedPref.KEY_USER_ID).equals(MainActivity.ADMIN_ID)) {

            mTracker.setScreenName(pageName);
            mTracker.send(new HitBuilders.ScreenViewBuilder().build());
        }
    }


    public class ParseLogInterceptor implements ParseNetworkInterceptor {
        @Override
        public ParseHttpResponse intercept(Chain chain) throws IOException {
            ParseHttpRequest request = chain.getRequest();

            ParseHttpResponse response = chain.proceed(request);

            // Consume the response body
            ByteArrayOutputStream responseBodyByteStream = new ByteArrayOutputStream();
            int n;
            byte[] buffer = new byte[1024];
            while ((n = response.getContent().read(buffer, 0, buffer.length)) != -1) {
                responseBodyByteStream.write(buffer, 0, n);
            }
            final byte[] responseBodyBytes = responseBodyByteStream.toByteArray();
            //Log.i("Response_Body", new String(responseBodyBytes));

            // Make a new response before return the response
            response = new ParseHttpResponse.Builder(response)
                    .setContent(new ByteArrayInputStream(responseBodyBytes))
                    .build();

            return response;
        }
    }



    public boolean checkApp(){
        ActivityManager am = (ActivityManager) this.getSystemService(ACTIVITY_SERVICE);

        // get the info from the currently running task
        List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(1);

        ComponentName componentInfo = taskInfo.get(0).topActivity;
        if (componentInfo.getPackageName().equalsIgnoreCase("YourPackage")) {
            return true;
        } else {
            return false;
        }
    }

}
