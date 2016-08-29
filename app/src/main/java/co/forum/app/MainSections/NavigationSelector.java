package co.forum.app.MainSections;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import co.forum.app.MainActivity;
import co.forum.app.R;
import co.forum.app.SharedPref;
import co.forum.app.SubActivity.SubActivity;
import co.forum.app.tools.NonSwipeableViewPager;
import co.forum.app.tools.SquareRelativeLayout;

public class NavigationSelector extends Fragment {

    public ActionBarDrawerToggle drawerToggle;
    public DrawerLayout mDrawerLayout;

    private boolean mUserLearnedDrawer;


    public NavigationFeed page00;
    public NavigationFeed page01;
    public NavigationFeed page02;

    @Bind(R.id.drawerTabLayout) TabLayout drawerTabLayout;
    @Bind(R.id.drawerViewPager) NonSwipeableViewPager drawerViewPager;

    @Bind(R.id.nav_title) TextView nav_title;
    @Bind(R.id.btn_add_subscription_global) SquareRelativeLayout btn_add_subscription_global;

    private View fragment;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.panel_navigation, container, false);

        ButterKnife.bind(this, view);

        setup_header_clicks();

        init_viewPager();

        init_tabLayout();

        return view;

    }


    private void setup_header_clicks() {

        nav_title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ((MainActivity) MainActivity.context).navigationSelector.mDrawerLayout.closeDrawer(Gravity.LEFT);

            }
        });


        btn_add_subscription_global.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SubActivity.openSubscriptionPage(getContext());
            }
        });
    }


    public void setup_drawer(int fragmentID, DrawerLayout drawerLayout, Toolbar toolbar) {

        fragment = getActivity().findViewById(fragmentID);

        mDrawerLayout = drawerLayout;
        drawerToggle = new ActionBarDrawerToggle(getActivity(), mDrawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close){

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);

                if(!mUserLearnedDrawer) {

                    mUserLearnedDrawer= true;

                    ((MainActivity)MainActivity.context).localData.setUserData(SharedPref.KEY_DRAWER, mUserLearnedDrawer);

                }

                mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_OPEN);

                //getActivity().invalidateOptionsMenu();
            }

            @Override
            public void onDrawerClosed(View drawerView) {

                super.onDrawerClosed(drawerView);

                mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);

                //getActivity().invalidateOptionsMenu();

            }
        };

        //if(!mUserLearnedDrawer) mDrawerLayout.openDrawer(fragment);

        mDrawerLayout.setDrawerListener(drawerToggle);
        drawerToggle.syncState();

    }


    private void init_viewPager() {

        drawerViewPager.setOffscreenPageLimit(2);

        drawerViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

                if(position == 0) {

                    btn_add_subscription_global.setVisibility(View.VISIBLE);

                } else btn_add_subscription_global.setVisibility(View.GONE);

                /*
                setTitle(tabTitle[position]);

                tabLayout.getTabAt(0).getIcon().setAlpha(80);
                tabLayout.getTabAt(2).getIcon().setAlpha(80);
                tabLayout.getTabAt(position).getIcon().setAlpha(255);

                if (position == 0) {
                    // click sur DECK

                    if (deckFragment.isSearchResult) action_rankResult.setVisible(true);
                    action_searchButton.setVisible(true);
                    action_logout.setVisible(false);

                    init_toolbar_click(0);



                } else if (position == 1) {
                    // click sur NOTIFICATION
                    if(push_page.equals("")) {
                        action_rankResult.setVisible(false);
                        action_searchButton.setVisible(false);
                        action_logout.setVisible(false);
                    }

                    new_notification_has_been_seen = true;

                    init_toolbar_click(1);



                } else if (position == 2) {
                    // click sur PROFIL
                    if(push_page.equals("")) {
                        action_rankResult.setVisible(false);
                        action_searchButton.setVisible(false);
                        action_logout.setVisible(true);
                    }


                    if( (((MainActivity)MainActivity.context).profilFragment.profil_viewpager != null) &&

                            ((MainActivity)MainActivity.context).profilFragment.profil_viewpager.getCurrentItem() == 2)

                        new_notification_chat_has_been_seen = true;

                    init_toolbar_click(2);


                } else toolbar.setOnClickListener(null);

                change_tab_profil_icon(new_notification_has_been_seen);
                change_tab_chat_icon(new_notification_chat_has_been_seen);

                */

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }

        });

        ViewPagerAdapter adapter = new ViewPagerAdapter(getFragmentManager());


        Bundle bundle0 = new Bundle();
        bundle0.putInt(NavigationFeed.PAGE, 0);

        Bundle bundle1 = new Bundle();
        bundle1.putInt(NavigationFeed.PAGE, 1);

        Bundle bundle2 = new Bundle();
        bundle2.putInt(NavigationFeed.PAGE, 2);

        page00 = new NavigationFeed();
        page01 = new NavigationFeed();
        page02 = new NavigationFeed();

        page00.setArguments(bundle0);
        page01.setArguments(bundle1);
        page02.setArguments(bundle2);

        adapter.addFragment(page00, "Following");
        adapter.addFragment(page01, "Forum");
        adapter.addFragment(page02, "Quotes");

        drawerViewPager.setAdapter(adapter);

    }


    private void init_tabLayout() {

        drawerTabLayout.setupWithViewPager(drawerViewPager);

        drawerTabLayout.setOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(drawerViewPager) {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                super.onTabSelected(tab);

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {



            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

                if (tab.getPosition() == 0)

                    ((MainActivity)MainActivity.context).navigationSelector.page00.subscription_recycler.smoothScrollToPosition(0);

                else if (tab.getPosition() == 1)

                    ((MainActivity)MainActivity.context).navigationSelector.page01.subscription_recycler.smoothScrollToPosition(0);

                else if (tab.getPosition() == 2)

                    ((MainActivity)MainActivity.context).navigationSelector.page02.subscription_recycler.smoothScrollToPosition(0);

            }
        });

        /*
        drawerTabLayout.getTabAt(0).setIcon(tabIcons[0]);
        drawerTabLayout.getTabAt(1).setIcon(tabIcons[1]);
        drawerTabLayout.getTabAt(2).setIcon(tabIcons[2]);

        drawerTabLayout.getTabAt(1).getIcon().setAlpha(80);
        drawerTabLayout.getTabAt(2).getIcon().setAlpha(80);
        */

    }


    class ViewPagerAdapter extends FragmentPagerAdapter {

        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            //return null;
            return mFragmentTitleList.get(position);
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

    }
}
