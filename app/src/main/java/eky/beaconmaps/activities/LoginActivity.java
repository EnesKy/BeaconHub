package eky.beaconmaps.activities;

import android.os.Bundle;
import android.widget.TextView;

import com.google.android.material.button.MaterialButton;

import eky.beaconmaps.R;

public class LoginActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        setSupportActionBar(findViewById(R.id.toolbar));

        MaterialButton buttonLogin = findViewById(R.id.button_login);
        buttonLogin.setOnClickListener(v -> {
            openActivity(null, MainActivity.class);
        });

        TextView textViewsignUp = findViewById(R.id.tv_sign_up);
        textViewsignUp.setOnClickListener(v -> {
            openActivity(null, SignUpActivity.class);
        });

    }

}
