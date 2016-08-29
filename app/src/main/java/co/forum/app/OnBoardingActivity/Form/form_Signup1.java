package co.forum.app.OnBoardingActivity.Form;


import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import butterknife.Bind;
import butterknife.ButterKnife;
import co.forum.app.OnBoardingActivity.OnBoardingActivity;
import co.forum.app.R;
import co.forum.app.tools.Message;

public class form_Signup1 extends Fragment {

    @Bind(R.id.et_signup_user_email)
    EditText et_signup_user_email;
    @Bind(R.id.et_signup_pwd_1)
    EditText et_signup_pwd_1;
    @Bind(R.id.et_signup_pwd_2)
    EditText et_signup_pwd_2;

    @Bind(R.id.block_password)
    LinearLayout block_password;
    @Bind(R.id.img_pwd_valid_1)
    ImageView img_pwd_valid_1;
    @Bind(R.id.img_pwd_valid_2)
    ImageView img_pwd_valid_2;

    @Bind(R.id.btn_go)
    Button btn_go;

    private boolean email_IsValid = false;
    private boolean password1_IsValid = false;
    private boolean password2_IsValid = false;

    public form_Signup1() {
        // Required empty public constructor
    }


    @TargetApi(Build.VERSION_CODES.LOLLIPOP) //api 21
    private void set_letterspacing() {

        int currentapiVersion = android.os.Build.VERSION.SDK_INT;

        if (currentapiVersion >= android.os.Build.VERSION_CODES.LOLLIPOP) {

            et_signup_user_email.setLetterSpacing(0.1f);
            et_signup_pwd_1.setLetterSpacing(0.1f);
            et_signup_pwd_2.setLetterSpacing(0.1f);
        }
    }


    @TargetApi(Build.VERSION_CODES.JELLY_BEAN) //api 16
    private void set_background() {

        int currentapiVersion = android.os.Build.VERSION.SDK_INT;

        if (currentapiVersion >= android.os.Build.VERSION_CODES.JELLY_BEAN) {

            et_signup_user_email.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (hasFocus) et_signup_user_email
                            .setBackground(ContextCompat.getDrawable(getContext(), R.drawable.form_edit_text_focus));

                    else et_signup_user_email
                            .setBackground(ContextCompat.getDrawable(getContext(), R.drawable.form_edit_text));
                }
            });

            et_signup_pwd_1.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (hasFocus)
                        block_password.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.form_double_edit_text_focus));

                    else if (!et_signup_pwd_2.hasFocus())
                        block_password.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.form_double_edit_text));
                }
            });

            et_signup_pwd_2.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (hasFocus)
                        block_password.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.form_double_edit_text_focus));

                    else if (!et_signup_pwd_1.hasFocus())
                        block_password.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.form_double_edit_text));
                }
            });

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.ob_form_signup1, container, false);

        ButterKnife.bind(this, layout);

        //et_signup_user_email.setTypeface(MainActivity.tf_serif);
        //et_signup_pwd_1.setTypeface(MainActivity.tf_serif);
        //et_signup_pwd_2.setTypeface(MainActivity.tf_serif);

            img_pwd_valid_1.setVisibility(View.INVISIBLE);
        img_pwd_valid_2.setVisibility(View.INVISIBLE);


            set_letterspacing();

            set_background();


            btn_go.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {

                    if (email_IsValid && password1_IsValid && password2_IsValid) {

                        ((OnBoardingActivity) OnBoardingActivity.context).tab_signup
                                .user_email = et_signup_user_email.getText().toString().toLowerCase();

                        ((OnBoardingActivity) OnBoardingActivity.context).tab_signup
                                .user_pwd = et_signup_pwd_1.getText().toString();

                        ((OnBoardingActivity) OnBoardingActivity.context).tab_signup
                                .signupFormPager.setCurrentItem(1, true);

                        ((OnBoardingActivity) OnBoardingActivity.context)
                                .back_signup1.setVisibility(View.VISIBLE);

                    } else if (!email_IsValid)
                        Message.message(getActivity(), "Enter a valid email adress");

                    else if (!password1_IsValid)
                        Message.message(getActivity(), "Your password is too short (7min)");

                    else if (!password2_IsValid)
                    Message.message(getActivity(), "The passwords are not similars");

            }
        });

        et_signup_user_email.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                if (emailIsValid())
                    et_signup_user_email.setTextColor(ContextCompat.getColor(getContext(), R.color.colorAccent));

                else
                    et_signup_user_email.setTextColor(ContextCompat.getColor(getContext(), R.color.tag_count_text));

                set_btn_go_color();

            }
        });

        et_signup_pwd_1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                if (et_signup_pwd_1.getText().toString().length() >= 7) {

                    password1_IsValid = true;

                    et_signup_pwd_1.setTextColor(ContextCompat.getColor(getContext(), R.color.colorAccent));

                    img_pwd_valid_1.setVisibility(View.VISIBLE);

                } else {

                    password1_IsValid = false;

                    et_signup_pwd_1.setTextColor(ContextCompat.getColor(getContext(), R.color.tag_count_text));

                    img_pwd_valid_1.setVisibility(View.INVISIBLE);
                }

                set_btn_go_color();

            }
        });

        et_signup_pwd_2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                if (passwords_are_similar()) {

                    et_signup_pwd_2.setTextColor(ContextCompat.getColor(getContext(), R.color.colorAccent));

                    img_pwd_valid_2.setVisibility(View.VISIBLE);

                } else {

                    et_signup_pwd_2.setTextColor(ContextCompat.getColor(getContext(), R.color.tag_count_text));

                    img_pwd_valid_2.setVisibility(View.INVISIBLE);

                }

                set_btn_go_color();

            }
        });

        return layout;
    }

    private boolean emailIsValid() {

        email_IsValid = false;

        String current = et_signup_user_email.getText().toString();

        if (current.contains("@")) {

            String after_at = current.substring(current.indexOf("@") + 1, current.length());

            if (after_at.contains("@")) {

                Message.message(getContext(), "sub : " + after_at + "\ncontain 2 @");

                email_IsValid = false;

            } else if (after_at.contains(".")) {

                String after_dot = after_at.substring(after_at.indexOf(".") + 1, after_at.length());

                if (after_dot.length() < 2) {

                    email_IsValid = false;

                } else email_IsValid = true;

            } else {

                email_IsValid = false;

            }

        }

        return email_IsValid;
    }

    private boolean passwords_are_similar() {

        if (et_signup_pwd_1.getText().toString().equals(et_signup_pwd_2.getText().toString()))

            password2_IsValid = true;

        else password2_IsValid = false;

        return password2_IsValid;
    }

    private void set_btn_go_color() {

        if (email_IsValid && password1_IsValid && password2_IsValid) {

            btn_go.setTextColor(ContextCompat.getColor(getContext(), R.color.white));

            btn_go.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.button_sl_activated));

        } else {

            btn_go.setTextColor(ContextCompat.getColor(getContext(), R.color.btn_form_text_off));

            btn_go.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.button_sl_deactivated));
        }


    }
}
