package co.forum.app.OnBoardingActivity.Form;


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
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import co.forum.app.OnBoardingActivity.OnBoardingActivity;
import co.forum.app.R;
import co.forum.app.tools.Message;

public class form_Login extends Fragment {

    @Bind(R.id.et_login_user_email) public EditText et_login_user_email;
    @Bind(R.id.et_login_password) EditText et_login_password;
    @Bind(R.id.btn_login2) Button btn_login2;
    @Bind(R.id.show_reset_page) TextView show_reset_page;

    private boolean email_IsValid = false;
    private boolean password_IsValid = false;

    public form_Login() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View layout = inflater.inflate(R.layout.ob_form_login, container, false);

        ButterKnife.bind(this, layout);

        //et_login_user_email.setTypeface(MainActivity.tf_serif);
        //et_login_password.setTypeface(MainActivity.tf_serif);


        btn_login2.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if (email_IsValid && password_IsValid) {

                    ((OnBoardingActivity) OnBoardingActivity.context).tab_login.login_User(

                            et_login_user_email.getText().toString(),

                            et_login_password.getText().toString());

                } else if (!email_IsValid)
                    Message.message(getContext(), "Enter a valid email adress");

                else Message.message(getContext(), "The password is too short");

            }
        });

        show_reset_page.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                ((OnBoardingActivity)OnBoardingActivity.context).tab_login.loginFormPager.setCurrentItem(1, true);

                ((OnBoardingActivity)OnBoardingActivity.context).back_login.setVisibility(View.VISIBLE);

            }
        });

        et_login_user_email.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) et_login_user_email
                        .setBackground(ContextCompat.getDrawable(getContext(), R.drawable.form_edit_text_focus));

                else et_login_user_email
                        .setBackground(ContextCompat.getDrawable(getContext(), R.drawable.form_edit_text));
            }
        });

        et_login_password.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) et_login_password
                        .setBackground(ContextCompat.getDrawable(getContext(), R.drawable.form_edit_text_focus));

                else et_login_password
                        .setBackground(ContextCompat.getDrawable(getContext(), R.drawable.form_edit_text));
            }
        });


        et_login_user_email.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                emailIsValid();

                set_btn_go_color();
            }
        });

        et_login_password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {


                if(et_login_password.getText().length() >= 7) password_IsValid = true;

                else password_IsValid = false;

                set_btn_go_color();

            }
        });

        return layout;
    }



    private boolean emailIsValid() {

        email_IsValid = false;

        String current = et_login_user_email.getText().toString();

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

            } else email_IsValid = false;

        }

        return email_IsValid;
    }

    private void set_btn_go_color() {

        if(email_IsValid && password_IsValid) {

            btn_login2.setTextColor(ContextCompat.getColor(getContext(), R.color.white));

            btn_login2.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.button_sl_activated));

        } else {

            btn_login2.setTextColor(ContextCompat.getColor(getContext(), R.color.btn_form_text_off));

            btn_login2.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.button_sl_deactivated));
        }


    }

}
