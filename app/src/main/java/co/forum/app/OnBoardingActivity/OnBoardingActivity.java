package co.forum.app.OnBoardingActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;

import butterknife.Bind;
import butterknife.ButterKnife;
import co.forum.app.MainActivity;
import co.forum.app.R;
import co.forum.app.SharedPref;
import co.forum.app.tools.Message;
import co.forum.app.tools.NonSwipeableViewPager;

public class OnBoardingActivity extends AppCompatActivity {

    public static Context context;

    public static final String KEY_BACK_FROM_ONBOARDING = "onBoardingActivity";

    @Bind(R.id.form_toolbar) public FrameLayout form_toolbar;
    @Bind(R.id.form_title) public TextView form_title;

    @Bind(R.id.back_signup1) public FrameLayout back_signup1;
    @Bind(R.id.back_login) public FrameLayout back_login;


    // pitch deck
    @Bind(R.id.presentation) RelativeLayout presentation;
    @Bind(R.id.presentationPager) ViewPager presentationPager;
    @Bind(R.id.shadow) FrameLayout shadow;
    @Bind(R.id.dot_0) FrameLayout dot_0;
    @Bind(R.id.dot_1) FrameLayout dot_1;
    @Bind(R.id.dot_2) FrameLayout dot_2;
    @Bind(R.id.dot_3) FrameLayout dot_3;
    @Bind(R.id.dot_4) FrameLayout dot_4;
    @Bind(R.id.dot_5) FrameLayout dot_5;

    ArrayList<FrameLayout> dots;

    int last_page = 0;

    // forms panel
    @Bind(R.id.login_panel) LinearLayout login_panel;
    @Bind(R.id.login_btns) LinearLayout login_btns;
    @Bind(R.id.empty_space) FrameLayout empty_space;
    @Bind(R.id.btn_sign_in) Button btn_sign_in;
    @Bind(R.id.btn_login1) LinearLayout btn_login1;

    @Bind(R.id.invit_container) LinearLayout invit_container;
    @Bind(R.id.signup_container) LinearLayout signup_container;
    @Bind(R.id.login_container) LinearLayout login_container;

    public static final int SIGNUP_FORM = 0;
    public static final int LOGIN_FORM = 1;

    public Tab_Login tab_login;
    public Tab_Signup tab_signup;


    // positioning
    int login_panel_bottom_position;
    float toolbar_out_position = -300;



    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        context = this;

        setContentView(R.layout.activity_onboarding);

        ButterKnife.bind(this);


        back_signup1.setVisibility(View.GONE);

        back_login.setVisibility(View.GONE);

        form_toolbar.setTranslationY(toolbar_out_position);

        set_PitchDeck();

        set_Forms();

        set_position_layouts();

        set_ClickListeners();

    }

    @Override
    public void onBackPressed() {

        //setResult(LogActivity2.RESULT_CANCELED);


        if(login_container.getVisibility() == View.VISIBLE && tab_login.loginFormPager.getCurrentItem() == 1) {

            tab_login.loginFormPager.setCurrentItem(0, true);

            back_login.setVisibility(View.GONE);


        } else if(signup_container.getVisibility() == View.VISIBLE && tab_signup.signupFormPager.getCurrentItem() == 1) {

            tab_signup.signupFormPager.setCurrentItem(0, true);

            back_signup1.setVisibility(View.GONE);

        }

        else hide_Form();
    }

    @Override
    public void overridePendingTransition(int enterAnim, int exitAnim) {

        super.overridePendingTransition(enterAnim, exitAnim);
    }



    private void set_PitchDeck() {

        dots = new ArrayList<>(Arrays.asList(dot_0, dot_1, dot_2, dot_3, dot_4, dot_5));

        PresentationAdapter mAdapter = new PresentationAdapter(getSupportFragmentManager());

        presentationPager.setAdapter(mAdapter);

        presentationPager.setOffscreenPageLimit(1);

        set_PitchPagerListener();
    }

    private void set_Forms() {

        tab_signup = new Tab_Signup();

        tab_login = new Tab_Login();

        this.getSupportFragmentManager().beginTransaction()
                .add(R.id.signup_container, tab_signup, "signup_fragment")
                .commit();

        this.getSupportFragmentManager().beginTransaction()
                .add(R.id.login_container, tab_login, "login_fragment")
                .commit();

    }

    private void set_position_layouts() {

        ViewTreeObserver vto = empty_space.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {

                // presentation bottom margin
                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                        RelativeLayout.LayoutParams.WRAP_CONTENT,
                        RelativeLayout.LayoutParams.WRAP_CONTENT
                );
                params.setMargins(0, 0, 0, login_btns.getHeight());
                presentation.setLayoutParams(params);

                //login bottom position
                login_panel_bottom_position = empty_space.getHeight();

                login_panel.setTranslationY(login_panel_bottom_position);

                ViewTreeObserver obs = empty_space.getViewTreeObserver();

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {

                    obs.removeOnGlobalLayoutListener(this);

                } else obs.removeGlobalOnLayoutListener(this);
            }

        });


    }

    private void set_ClickListeners() {

        btn_sign_in.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                show_Form(SIGNUP_FORM);
            }
        });

        btn_login1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                show_Form(LOGIN_FORM);
            }
        });

        form_toolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (getCurrentFocus() != null) {
                    InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                    inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);

                    Handler handlerTimer = new Handler();
                    handlerTimer.postDelayed(new Runnable() {
                        public void run() {
                            hide_Form();
                        }}, 200);

                } else hide_Form();
            }
        });

        back_signup1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                tab_signup.signupFormPager.setCurrentItem(0, true);

                back_signup1.setVisibility(View.GONE);
            }
        });

        back_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                tab_login.loginFormPager.setCurrentItem(0, true);

                back_login.setVisibility(View.GONE);
            }
        });

    }

    private void set_PitchPagerListener() {

        presentationPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

                dots.get(last_page).setBackground(ContextCompat.getDrawable(MainActivity.context, R.drawable.pager_circle));

                dots.get(position).setBackground(ContextCompat.getDrawable(MainActivity.context, R.drawable.pager_dot));

                last_page = position;

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }



    private void show_Form(int form_type) {

        presentationPager.setVisibility(View.INVISIBLE);

        if(form_type == SIGNUP_FORM) {

            form_toolbar.setBackgroundColor(ContextCompat.getColor(context, R.color.colorAccent));

            back_login.setVisibility(View.GONE);

            form_title.setText("Create Account");

            if(tab_signup.signupFormPager.getCurrentItem() == 0)

                back_signup1.setVisibility(View.GONE);

            else back_signup1.setVisibility(View.VISIBLE);

            signup_container.setVisibility(View.VISIBLE);
            login_container.setVisibility(View.GONE);

            /*((OnBoardingActivity)context)
                    .getSupportFragmentManager()
                    .beginTransaction()
                    .show(tab_signup)
                    .commit();*/

        } else if(form_type == LOGIN_FORM) {

            form_toolbar.setBackgroundColor(ContextCompat.getColor(context, R.color.colorPrimary));

            back_signup1.setVisibility(View.GONE);

            if(tab_login.loginFormPager.getCurrentItem() == 0) {

                form_title.setText("Login");
                back_login.setVisibility(View.GONE);

            } else {

                form_title.setText("Reset Password");
                back_login.setVisibility(View.VISIBLE);

            }


            login_container.setVisibility(View.VISIBLE);
            signup_container.setVisibility(View.GONE);

            /*((OnBoardingActivity) context)
                    .getSupportFragmentManager()
                    .beginTransaction()
                    .show(tab_login)
                    .commit();*/
        }

        form_toolbar.animate()
                .translationY(0)
                .setInterpolator(new AccelerateInterpolator(20f))
                .setInterpolator(new DecelerateInterpolator(1f))
                .start();

        login_panel.animate()
                .translationY(0)
                .setInterpolator(new AccelerateInterpolator(20f))
                .setInterpolator(new DecelerateInterpolator(1f))
                .start();

        shadow.animate()
                .alpha(1f)
                .setInterpolator(new AccelerateInterpolator(20f))
                .setInterpolator(new DecelerateInterpolator(1f))
                .start();

        presentationPager.animate()
                .translationY(-150)
                .scaleX(0.95f)
                .scaleY(0.95f)
                .setInterpolator(new AccelerateInterpolator(1f))
                .setInterpolator(new DecelerateInterpolator(1f))
                .start();

        invit_container.setVisibility(View.GONE);
        //login_btns.setVisibility(View.GONE);
        //empty_space.setVisibility(View.GONE);
    }

    public void hide_Form() {

        presentationPager.setVisibility(View.VISIBLE);

        /*((OnBoardingActivity)context)
                .getSupportFragmentManager()
                .beginTransaction()
                .hide(tab_signup)
                .hide(tab_login)
                .commit();*/
        login_container.setVisibility(View.GONE);
        signup_container.setVisibility(View.GONE);

        form_toolbar.animate()
                .translationY(toolbar_out_position)
                .setInterpolator(new AccelerateInterpolator(20f))
                .setInterpolator(new DecelerateInterpolator(1f))
                .start();


        login_panel.animate()
                .translationY(login_panel_bottom_position)
                .setInterpolator(new AccelerateInterpolator(20f))
                .setInterpolator(new DecelerateInterpolator(1f))
                .start();

        shadow.animate()
                .alpha(0)
                .setInterpolator(new AccelerateInterpolator(20f))
                .setInterpolator(new DecelerateInterpolator(1f))
                .start();

        presentationPager.animate()
                .translationY(0)
                .scaleX(1)
                .scaleY(1)
                .setInterpolator(new AccelerateInterpolator(1f))
                .setInterpolator(new DecelerateInterpolator(1f))
                .start();

        invit_container.setVisibility(View.VISIBLE);

    }

    public void show_Loader(int form_type, boolean show) {

        NonSwipeableViewPager formPager;
        LinearLayout loader;

        if(form_type == LOGIN_FORM) {

            formPager = tab_login.loginFormPager;
            loader = tab_login.loader;

        } else { //form_type == SIGNUP_FORM

            formPager = tab_signup.signupFormPager;
            loader = tab_signup.loader;
        }

        if(show) {

            formPager.setVisibility(View.GONE);
            loader.setVisibility(View.VISIBLE);

        } else {

            formPager.setVisibility(View.VISIBLE);
            loader.setVisibility(View.GONE);
        }

    }


    public static void launch_fromMain(Context context) {

        Intent intent = new Intent(context, OnBoardingActivity.class);

        final int result = 1;

        ((MainActivity)MainActivity.context).startActivityForResult(intent, result);

    }

    public static void goBack_toMain(int form_type) {

        String welcomeText = "";

        Intent intent_BackToMain = new Intent();

        if(form_type == LOGIN_FORM) {

            intent_BackToMain.putExtra(OnBoardingActivity.KEY_BACK_FROM_ONBOARDING, OnBoardingActivity.LOGIN_FORM);

            welcomeText = "Welcome back in the Forum " + ((MainActivity) MainActivity.context).localData.getUserData(SharedPref.KEY_FULL_NAME);


        } else if(form_type == SIGNUP_FORM) {

            intent_BackToMain.putExtra(OnBoardingActivity.KEY_BACK_FROM_ONBOARDING, OnBoardingActivity.SIGNUP_FORM);

            welcomeText = "Welcome in the Forum " + ((MainActivity) MainActivity.context).localData.getUserData(SharedPref.KEY_FULL_NAME);

        }

        ((OnBoardingActivity) context).setResult(OnBoardingActivity.RESULT_OK, intent_BackToMain);

        ((OnBoardingActivity) context).finish();

        ((OnBoardingActivity) context).overridePendingTransition(0, R.anim.exit_right);

        Message.message(context, welcomeText);

    }
}
