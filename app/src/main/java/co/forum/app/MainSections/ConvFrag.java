package co.forum.app.MainSections;


import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import co.forum.app.MainActivity;
import co.forum.app.MainSections.ProfilPages.ChatOFrag;
import co.forum.app.R;
import co.forum.app.SharedPref;

public class ConvFrag extends Fragment {

    public static final String KEY_USER_ID = "key_user_id";
    public static final String KEY_USER_NAME = "key_user_name";

    public String UserID;
    public String UserName;

    @Bind(R.id.chat_tablayout) public TabLayout chat_tablayout;
    @Bind(R.id.chat_viewpager) public ViewPager chat_viewpager;

    ViewPagerAdapter adapter;

    public ChatOFrag userChats;
    public ChatOFrag otherChats;
    public ChatOFrag otherStories;


    public ConvFrag() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_conv, container, false);

        ButterKnife.bind(this, view);

        init_viewPager();

        return view;
    }

    private void init_viewPager() {

        adapter = new ViewPagerAdapter(getFragmentManager());

        UserID = ((MainActivity)getContext()).localData.getUserData(SharedPref.KEY_USER_ID);
        UserName = ((MainActivity)getContext()).localData.getUserData(SharedPref.KEY_FULL_NAME);

        // user chat
        Bundle bundle3 = new Bundle();
        //bundle3.putString(KEY_USER_NAME, UserName);
        bundle3.putInt(ChatOFrag.KEY_TYPE, ChatOFrag.CONV_U_CHAT);
        userChats = new ChatOFrag();
        userChats.setArguments(bundle3);

        // other chat
        Bundle bundle4 = new Bundle();
        //bundle4.putString(KEY_USER_NAME, UserName);
        bundle4.putInt(ChatOFrag.KEY_TYPE, ChatOFrag.CONV_O_CHAT);
        otherChats = new ChatOFrag();
        otherChats.setArguments(bundle4);

        // other stories
        /*Bundle bundle5 = new Bundle();
        bundle5.putInt(ChatOFrag.KEY_TYPE, ChatOFrag.STORIES_O);
        otherStories = new ChatOFrag();
        otherStories.setArguments(bundle5);
        */


        adapter.addFragment(userChats, "Chats");
        adapter.addFragment(otherChats, "Others' Chats");
        //adapter.addFragment(otherStories, "Stories");

        chat_viewpager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }

        });
        chat_viewpager.setAdapter(adapter);
        chat_viewpager.setOffscreenPageLimit(1);

        chat_tablayout.setupWithViewPager(chat_viewpager);
    }


    class ViewPagerAdapter extends FragmentPagerAdapter {

        private final List<Fragment> mFragmentList = new ArrayList();
        private final List<String> mFragmentTitleList = new ArrayList();

        public ViewPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getCount()
        {
            return this.mFragmentList.size();
        }

        @Override
        public Fragment getItem(int position)
        {
            return mFragmentList.get(position);
        }

        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }
    }

}
