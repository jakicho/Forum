package co.forum.app.OnBoardingActivity.Form;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import butterknife.Bind;
import butterknife.ButterKnife;
import co.forum.app.Data.ParseRequest;
import co.forum.app.R;

public class form_Reset extends Fragment {

    @Bind(R.id.et_email_recovery) EditText et_email_recovery;
    @Bind(R.id.btn_reset_email) Button btn_reset_email;

    public form_Reset() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View layout = inflater.inflate(R.layout.ob_form_reset, container, false);

        ButterKnife.bind(this, layout);

        //et_email_recovery.setTypeface(MainActivity.tf_serif);

        btn_reset_email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String emailAdress = et_email_recovery.getText().toString();

                ParseRequest.resetPassword(emailAdress);

            }
        });

        return layout;
    }

}
