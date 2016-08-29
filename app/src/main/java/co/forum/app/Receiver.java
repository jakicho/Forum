package co.forum.app;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.parse.ParsePushBroadcastReceiver;

import org.json.JSONException;
import org.json.JSONObject;

public class Receiver extends ParsePushBroadcastReceiver {

    @Override
    public void onPushOpen(Context context, Intent intent) {
        //Log.e("Push", "Clicked");

        Intent i = new Intent(context, MainActivity.class);
        i.putExtras(intent.getExtras());

        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(i);
    }

    @Override
    protected Class<? extends Activity> getActivity(Context context, Intent intent) {
        return super.getActivity(context, intent);
    }

    @Override
    protected int getSmallIconId(Context context, Intent intent) {
        return super.getSmallIconId(context, intent);
    }

    @Override
    protected void onPushReceive(Context context, Intent intent) {

        super.onPushReceive(context, intent);
    }
}