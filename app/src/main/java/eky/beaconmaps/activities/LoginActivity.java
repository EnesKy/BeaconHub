package eky.beaconmaps.activities;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.TextView;

import com.google.android.material.button.MaterialButton;

import androidx.appcompat.widget.Toolbar;
import eky.beaconmaps.R;
import eky.beaconmaps.fragments.SignUpFragment;

public class LoginActivity extends BaseActivity {

    private TextView toolbarTitle;
    private String openFragmentTag = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbarTitle = toolbar.findViewById(R.id.tv_toolbar_title);
        toolbarTitle.setText(R.string.title_login_activity);
        setSupportActionBar(toolbar);

        MaterialButton buttonLogin = findViewById(R.id.button_login);
        buttonLogin.setOnClickListener(v -> {
            openActivity(null, MainActivity.class);
        });

        TextView textViewsignUp = findViewById(R.id.tv_sign_up);
        textViewsignUp.setOnClickListener(v -> {
            openFragmentTag = "SignUp";
            openFragment(R.id.login_container, new SignUpFragment(), openFragmentTag,
                    R.anim.enter_from_bottom, R.anim.hold);
        });

    }

    @Override
    public void onBackPressed() {
        if (!TextUtils.isEmpty(openFragmentTag)) {
            closeFragment(openFragmentTag, R.anim.hold, R.anim.exit_to_bottom);
        } else {
            super.onBackPressed();
        }
    }

}
