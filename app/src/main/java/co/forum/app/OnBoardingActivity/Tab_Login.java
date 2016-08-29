package co.forum.app.OnBoardingActivity;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;

import butterknife.Bind;
import butterknife.ButterKnife;
import co.forum.app.Data.ParseRequest;
import co.forum.app.OnBoardingActivity.Form.FormAdapter;
import co.forum.app.OnBoardingActivity.Form.form_Login;
import co.forum.app.OnBoardingActivity.Form.form_Reset;
import co.forum.app.R;
import co.forum.app.tools.Message;
import co.forum.app.tools.NonSwipeableViewPager;


public class Tab_Login extends Fragment {

    View layout;

    @Bind(R.id.loader) public LinearLayout loader;
    @Bind(R.id.loginFormPager) public NonSwipeableViewPager loginFormPager;
    @Bind(R.id.logo_forum1) LinearLayout logo_forum1;

    public form_Login form_Login;
    public form_Reset form_Reset;

    public Tab_Login() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        layout = inflater.inflate(R.layout.ob_pager_form_login, container, false);

        ButterKnife.bind(this, layout);

        FormAdapter adapter = new FormAdapter(getFragmentManager());

        form_Login = new form_Login();
        form_Reset = new form_Reset();

        adapter.addFragment(form_Login);
        adapter.addFragment(form_Reset);

        loginFormPager.setAdapter(adapter);

        loginFormPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

                if (position == 0) ((OnBoardingActivity) OnBoardingActivity.context).form_title.setText("Login");

                if (position == 1) ((OnBoardingActivity) OnBoardingActivity.context).form_title.setText("Reset Password");

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        logo_forum1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((OnBoardingActivity) OnBoardingActivity.context).hide_Form();
            }
        });

        return layout;
    }

    public void login_User(String email, String password) {

        ((OnBoardingActivity)OnBoardingActivity.context)

                .show_Loader(OnBoardingActivity.LOGIN_FORM, true);

        InputMethodManager imm = (InputMethodManager) OnBoardingActivity.context.getSystemService(Context.INPUT_METHOD_SERVICE);

        imm.hideSoftInputFromWindow(form_Login.et_login_user_email.getWindowToken(), 0);

        int currentapiVersion = android.os.Build.VERSION.SDK_INT;

        ParseRequest.login(getActivity(), email, password, currentapiVersion);
    }

}
