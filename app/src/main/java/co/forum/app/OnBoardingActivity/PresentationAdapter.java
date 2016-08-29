package co.forum.app.OnBoardingActivity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.Arrays;

public class PresentationAdapter extends FragmentPagerAdapter {

    static final String PAGE_KEY = "page_key";

    PresentationPage info_0 = new PresentationPage();
    PresentationPage info_1 = new PresentationPage();
    PresentationPage info_2 = new PresentationPage();

    PresentationPage info_3 = new PresentationPage();
    PresentationPage info_4 = new PresentationPage();
    PresentationPage info_5 = new PresentationPage();

    Bundle bundle0 = new Bundle();
    Bundle bundle1 = new Bundle();
    Bundle bundle2 = new Bundle();
    Bundle bundle3 = new Bundle();
    Bundle bundle4 = new Bundle();
    Bundle bundle5 = new Bundle();


    ArrayList<PresentationPage> listFragments;// = new ArrayList<>();


    public PresentationAdapter(FragmentManager fm) {

        super(fm);

        bundle0.putInt(PAGE_KEY, 0);
        bundle1.putInt(PAGE_KEY, 1);
        bundle2.putInt(PAGE_KEY, 2);
        bundle3.putInt(PAGE_KEY, 3);
        bundle4.putInt(PAGE_KEY, 4);
        bundle5.putInt(PAGE_KEY, 5);


        info_0.setArguments(bundle0);
        info_1.setArguments(bundle1);
        info_2.setArguments(bundle2);
        info_3.setArguments(bundle3);
        info_4.setArguments(bundle4);
        info_5.setArguments(bundle5);


        listFragments = new ArrayList<>(
                Arrays.asList(info_0, info_1, info_2, info_3, info_4, info_5));
    }

    @Override
    public Fragment getItem(int i) {

        return listFragments.get(i);

    }

    @Override
    public int getCount() {
        return listFragments.size();
    }

}
