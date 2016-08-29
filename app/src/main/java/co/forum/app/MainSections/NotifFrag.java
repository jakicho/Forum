package co.forum.app.MainSections;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.parse.ParseObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import co.forum.app.Adapters.NotificationAdapter;
import co.forum.app.Data.NotifData;
import co.forum.app.Data.ParseNotification;
import co.forum.app.Data.ParseRequest;
import co.forum.app.MainActivity;
import co.forum.app.R;
import co.forum.app.SharedPref;
import co.forum.app.tools.Message;


public class NotifFrag extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    @Bind(R.id.generic_recycler_view) public RecyclerView notification_recycler;
    @Bind(R.id.empty_notif_indicator) LinearLayout empty_notif_indicator;
    @Bind(R.id.notification_loader) LinearLayout notification_loader;
    @Bind(R.id.notification_bottom_loader) LinearLayout notification_bottom_loader;

    @Bind(R.id.refreshNotificationRecycler) SwipeRefreshLayout refreshNotificationRecycler;

    private NotificationAdapter notificationAdapter;

    private String userID;

    private List<NotifData> notifDataList;

    public NotifFrag() {
        // Required empty public constructor
    }

    public static boolean just_created = true;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        notifDataList = new ArrayList<>(); //notifDataList = getNotifDataList();

        userID = ((MainActivity)MainActivity.context).localData.getUserData(SharedPref.KEY_USER_ID);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View recycler = inflater.inflate(R.layout.generic_recycler_view, container, false);

        ButterKnife.bind(this, recycler);

        refreshNotificationRecycler.setOnRefreshListener(this);

        refreshNotificationRecycler.setVisibility(View.GONE);

        ParseRequest.get_UserNotifications(getContext(), userID, 0);

        return recycler;
    }

    @Override
    public void onResume() {

        super.onResume();

        if(!just_created && notificationAdapter != null) {

            ParseRequest.getUserNotificationsRefresh(
                    getActivity(),
                    userID,
                    ((MainActivity) MainActivity.context).localData.get_most_recent_timestamp_parse(),
                    true);

        } else just_created = false;
    }


    List<ParseNotification> parseNotificationList;
    ArrayList<NotifData> notifList;
    int offset;


    public void display_notifications(List<ParseNotification> parseNotificationList, int offset) {

        this.parseNotificationList = parseNotificationList;

        this.offset = offset;

        if (offset == 0) {

            refreshNotificationRecycler.setVisibility(View.GONE);

            notification_loader.setVisibility(View.VISIBLE);

        }

        else notification_bottom_loader.setVisibility(View.VISIBLE);

        Thread myThread = new Thread(new transform_data_bg_thread());

        myThread.start();

    }

    private class transform_data_bg_thread implements Runnable {

        @Override
        public void run() {

            notifList = transform_data(parseNotificationList);

            ((MainActivity)MainActivity.context).runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    if(offset == 0) displayFirstCards(MainActivity.context, notifList);

                    else {

                        notification_bottom_loader.setVisibility(View.GONE);

                        notifDataList.addAll(notifList);

                        if(notificationAdapter == null) {

                            // bug...

                            //if(isComments) Message.message(context, "Comments tab (no adapter), offset : " + offset);

                            //else Message.message(context, "Cards tab (no adapter), offset : " + offset);

                        } else notificationAdapter.notifyDataSetChanged();
                    }
                }
            });
        }
    }


    private ArrayList<NotifData> transform_data(List<ParseNotification> parseNotificationList) {

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


    public void displayCards(Context context, ArrayList<NotifData> notifsList, int offset) {

        if (offset == 0) displayFirstCards(context, notifsList);

        else if(offset == -1) {

            // refresh

            if(notifDataList.size() != 0) {

                notifDataList.addAll(0, notifsList);

                notificationAdapter.notifyItemRangeInserted(0, notifsList.size()); //notificationAdapter.notifyDataSetChanged();

                notification_recycler.scrollToPosition(0);

            } else displayFirstCards(context, notifsList);


        }
    }


    private void hideOrShowEmptyIndicator() {

        if(notifDataList.size() == 0) empty_notif_indicator.setVisibility(View.VISIBLE);

        else empty_notif_indicator.setVisibility(View.GONE);
    }


    public int pageOffset = 0;
    public int previousTotal = 0;
    private int visibleThreshold = 1;
    private boolean loading = true;
    int firstVisibleItem, visibleItemCount, totalItemCount;

    private void displayFirstCards(final Context context, final ArrayList<NotifData> notifsList) {

        refreshNotificationRecycler.setVisibility(View.VISIBLE);

        notification_loader.setVisibility(View.GONE);

        this.notifDataList = notifsList;

        hideOrShowEmptyIndicator();

        notificationAdapter = new NotificationAdapter(getActivity(), notifDataList);

        notification_recycler.setAdapter(notificationAdapter);

        final LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());

        notification_recycler.setLayoutManager(mLayoutManager);

        notification_recycler.addOnScrollListener(new RecyclerView.OnScrollListener() {

            private void loadMore() {

                visibleItemCount = notification_recycler.getChildCount();
                totalItemCount = mLayoutManager.getItemCount();
                firstVisibleItem = mLayoutManager.findFirstVisibleItemPosition();

                if (loading) {
                    if (totalItemCount > previousTotal) {
                        loading = false;
                        previousTotal = totalItemCount;
                    }
                }
                if (!loading && (totalItemCount - visibleItemCount)
                        <= (firstVisibleItem + visibleThreshold)) {
                    // End has been reached
                    ++pageOffset;

                    //Message.message(context, "loading previous notifications\nplease wait...");

                    ParseRequest.get_UserNotifications(context, userID, pageOffset);

                    loading = true;
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {

                super.onScrolled(recyclerView, dx, dy);

                loadMore();

                ((MainActivity) getContext()).onScrollHideShowBottomMenu(dy);

            }
        });
    }


    public void empty(Context context) {

        displayCards(context, new ArrayList<NotifData>(), 0);

    }


    public void setupMainUserNotif(Context context) {

        userID = ((MainActivity) context).localData.getUserData(SharedPref.KEY_USER_ID);

        ParseRequest.get_UserNotifications(getContext(), userID, 0);

    }


    public List<NotifData> getNotifDataList(){

        List<NotifData> data = new ArrayList<>();

        int[] default_type = getActivity().getResources().getIntArray(R.array.notif_type);
        String[] default_issuer = getActivity().getResources().getStringArray(R.array.issuer_name);
        String[] default_time = getActivity().getResources().getStringArray(R.array.notif_time);
        String[] default_main_content = getActivity().getResources().getStringArray(R.array.notif_content);

        for(int i=0; i<default_issuer.length && i<default_time.length; i++){

            NotifData current = new NotifData();
            current.notif_type = default_type[i];
            current.sender_name = default_issuer[i];
            current.timestamp = default_time[i];
            current.card_content = default_main_content[i];
            data.add(current);
        }
        return data;
    }


    @Override
    public void onRefresh() {

        ParseRequest.getUserNotificationsRefresh(
                getActivity(),
                userID,
                ((MainActivity) MainActivity.context).localData.get_most_recent_timestamp_parse(),
                false);

        refreshNotificationRecycler.setRefreshing(false);
    }
}
