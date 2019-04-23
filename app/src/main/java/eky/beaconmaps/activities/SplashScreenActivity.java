package eky.beaconmaps.activities;

import android.os.Bundle;

import eky.beaconmaps.R;

public class SplashScreenActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        openActivity(null, LoginActivity.class);
        finish();
    }
}
