package co.forum.app.OnBoardingActivity;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;

import com.parse.ParseUser;

import butterknife.Bind;
import butterknife.ButterKnife;
import co.forum.app.Data.ParseRequest;
import co.forum.app.MainActivity;
import co.forum.app.OnBoardingActivity.Form.FormAdapter;
import co.forum.app.OnBoardingActivity.Form.form_Signup1;
import co.forum.app.OnBoardingActivity.Form.form_Signup2;
import co.forum.app.R;
import co.forum.app.tools.NonSwipeableViewPager;

public class Tab_Signup extends Fragment {

    View layout;

    @Bind(R.id.loader) public LinearLayout loader;
    @Bind(R.id.signupFormPager) public NonSwipeableViewPager signupFormPager;
    @Bind(R.id.logo_forum2) LinearLayout logo_forum2;

    public form_Signup1 form_Signup1;
    public form_Signup2 form_Signup2;

    public String user_email;
    public String user_pwd;

    public String user_name;
    public String user_surname;
    public ParseUser parseUser;

    public Tab_Signup() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        layout = inflater.inflate(R.layout.ob_pager_form_signup, container, false);

        ButterKnife.bind(this, layout);

        FormAdapter adapter = new FormAdapter(getFragmentManager());

        form_Signup1 = new form_Signup1();
        form_Signup2 = new form_Signup2();

        adapter.addFragment(form_Signup1);
        adapter.addFragment(form_Signup2);

        signupFormPager.setAdapter(adapter);

        logo_forum2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((OnBoardingActivity) OnBoardingActivity.context).hide_Form();
            }
        });

        return layout;
    }

    public void register_User() {

        ParseUser.logOut();

        parseUser = new ParseUser();

        parseUser.setUsername(user_email);
        parseUser.setEmail(user_email);
        parseUser.setPassword(user_pwd);

        parseUser.put("name", user_name);
        parseUser.put("surname", user_surname);
        parseUser.increment("RunCount");
        parseUser.put("bio", "");
        parseUser.put("cards_count", 0);
        parseUser.put("cards_reply_count", 0);
        parseUser.put("upvotes_count", 0);
        parseUser.put("replies_count", 0);
        parseUser.put("curated", false);

        parseUser.put("api", android.os.Build.VERSION.SDK_INT);
        parseUser.put("appVersion", getResources().getString(R.string.app_version)); //MainActivity.APP_VERSION

        ((OnBoardingActivity)OnBoardingActivity.context).show_Loader(OnBoardingActivity.SIGNUP_FORM, true);

        InputMethodManager imm = (InputMethodManager) OnBoardingActivity.context.getSystemService(Context.INPUT_METHOD_SERVICE);

        imm.hideSoftInputFromWindow(form_Signup2.et_first_name.getWindowToken(), 0);


        ParseRequest.signup(getContext(), parseUser);
    }

}
