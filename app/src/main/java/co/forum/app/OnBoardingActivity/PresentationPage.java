package co.forum.app.OnBoardingActivity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;

import butterknife.Bind;
import butterknife.ButterKnife;
import co.forum.app.Adapters.CardStackAdapter;
import co.forum.app.MainSections.DeckFrag;
import co.forum.app.R;


public class PresentationPage extends Fragment {

    int page;

    @Bind(R.id.layout_stub) ViewStub layout_stub;

    DeckFrag deckFrag;

    View view;

    public PresentationPage(){}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.ob_pager_pitch, container, false);

        ButterKnife.bind(this, view);

        page = getArguments().getInt(PresentationAdapter.PAGE_KEY);

        set_page_info(page);

        return view;
    }


    private void set_page_info(int page) {

        switch (page) {

            case 0:
                layout_stub.setLayoutResource(R.layout.ob_pres_page0);
                layout_stub.inflate();
                break;

            case 1:
                layout_stub.setLayoutResource(R.layout.ob_pres_page1b);
                layout_stub.inflate();
                break;

            case 2:

                layout_stub.setLayoutResource(R.layout.ob_pres_page2b);
                layout_stub.inflate();

                break;

            case 3:
                layout_stub.setLayoutResource(R.layout.ob_pres_page3b);
                layout_stub.inflate();
                break;

            case 4:
                layout_stub.setLayoutResource(R.layout.ob_pres_page4b);
                layout_stub.inflate();
                break;

            case 5:
                layout_stub.setLayoutResource(R.layout.ob_pres_page5b);
                layout_stub.inflate();
                break;
        }

        /*



        int currentapiVersion = android.os.Build.VERSION.SDK_INT;

        if(currentapiVersion == android.os.Build.VERSION_CODES.JELLY_BEAN) {

            switch (page) {

                case 0:
                    layout_stub.setLayoutResource(R.layout.ob_pres_page0);
                    layout_stub.inflate();
                    break;

                case 1:
                    layout_stub.setLayoutResource(R.layout.ob_pres_page1b);
                    layout_stub.inflate();
                    break;

                case 2:

                    layout_stub.setLayoutResource(R.layout.ob_pres_page2b);
                    layout_stub.inflate();

                    break;

                case 3:
                    layout_stub.setLayoutResource(R.layout.ob_pres_page3b);
                    layout_stub.inflate();
                    break;

                case 4:
                    layout_stub.setLayoutResource(R.layout.ob_pres_page4b);
                    layout_stub.inflate();
                    break;

                case 5:
                    layout_stub.setLayoutResource(R.layout.ob_pres_page5b);
                    layout_stub.inflate();
                    break;
            }

        } else {

            switch (page) {

                case 0:
                    layout_stub.setLayoutResource(R.layout.ob_pres_page0);
                    layout_stub.inflate();
                    break;

                case 1:
                    layout_stub.setLayoutResource(R.layout.ob_pres_page1);
                    layout_stub.inflate();
                    break;

                case 2:

                    layout_stub.setLayoutResource(R.layout.ob_pres_page2);
                    layout_stub.inflate();

                    deckFrag = new DeckFrag();

                    Bundle bundle = new Bundle();

                    bundle.putInt(DeckFrag.KEY_DECK_TYPE, CardStackAdapter.DEMO_DECK);

                    deckFrag.setArguments(bundle);

                    FragmentManager manager = getFragmentManager();

                    FragmentTransaction transaction = manager.beginTransaction();

                    transaction.add(R.id.demo_deck_container, deckFrag, "demo_deck");

                    transaction.commit();

                    break;

                case 3:
                    layout_stub.setLayoutResource(R.layout.ob_pres_page3);
                    layout_stub.inflate();
                    break;

                case 4:
                    layout_stub.setLayoutResource(R.layout.ob_pres_page4);
                    layout_stub.inflate();
                    break;

                case 5:
                    layout_stub.setLayoutResource(R.layout.ob_pres_page5);
                    layout_stub.inflate();
                    break;
            }
        }

        */
    }
}
