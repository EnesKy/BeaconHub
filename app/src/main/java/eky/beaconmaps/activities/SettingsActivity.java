package eky.beaconmaps.activities;

import android.os.Bundle;

import eky.beaconmaps.R;

public class SettingsActivity extends BaseActivity {


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getIntent();
        setContentView(R.layout.activity_settings);

        setSupportActionBar(findViewById(R.id.toolbar));

    }


}
