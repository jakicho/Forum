package co.forum.app.OnBoardingActivity.Form;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import butterknife.Bind;
import butterknife.ButterKnife;
import co.forum.app.OnBoardingActivity.OnBoardingActivity;
import co.forum.app.R;
import co.forum.app.tools.Message;

public class form_Signup2 extends Fragment {

    @Bind(R.id.et_first_name) public EditText et_first_name;
    @Bind(R.id.block_name) LinearLayout block_name;
    @Bind(R.id.et_family_surname) EditText et_family_name;
    @Bind(R.id.btn_sign_up) Button btn_sign_up;

    boolean first_name_valid = false;
    boolean family_name_valid = false;

    public form_Signup2() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View layout = inflater.inflate(R.layout.ob_form_signup2, container, false);

        ButterKnife.bind(this, layout);

        //et_first_name.setTypeface(MainActivity.tf_serif);
        //et_family_name.setTypeface(MainActivity.tf_serif);

        et_first_name.setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS | InputType.TYPE_TEXT_FLAG_CAP_WORDS);
        et_family_name.setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS | InputType.TYPE_TEXT_FLAG_CAP_WORDS);


        btn_sign_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (first_name_valid && family_name_valid) {

                    ((OnBoardingActivity) OnBoardingActivity.context).tab_signup
                            .user_name = et_first_name.getText().toString();

                    ((OnBoardingActivity) OnBoardingActivity.context).tab_signup
                            .user_surname = et_family_name.getText().toString();

                    ((OnBoardingActivity) OnBoardingActivity.context).tab_signup
                            .register_User();

                } else if (!first_name_valid)
                    Message.message(getActivity(), "Enter your first name");

                else if (!family_name_valid)
                    Message.message(getActivity(), "Enter your family name");
            }
        });

        et_first_name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                if (et_first_name.getText().toString().length() >= 3) first_name_valid = true;

                else first_name_valid = false;

                set_btn_go_color();

            }
        });

        et_family_name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                if (et_family_name.getText().toString().length() >= 3) family_name_valid = true;

                else family_name_valid = false;

                set_btn_go_color();

            }
        });


        et_first_name.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus)
                    block_name.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.form_double_edit_text_focus));

                else if (!et_family_name.hasFocus())
                    block_name.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.form_double_edit_text));
            }
        });

        et_family_name.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus)
                    block_name.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.form_double_edit_text_focus));

                else if (!et_first_name.hasFocus())
                    block_name.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.form_double_edit_text));
            }
        });



        return layout;
    }


    private void set_btn_go_color() {

        if(first_name_valid && family_name_valid) {

            btn_sign_up.setTextColor(ContextCompat.getColor(getContext(), R.color.white));

            btn_sign_up.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.button_sl_activated));

        } else {

            btn_sign_up.setTextColor(ContextCompat.getColor(getContext(), R.color.btn_form_text_off));

            btn_sign_up.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.button_sl_deactivated));
        }


    }

}
