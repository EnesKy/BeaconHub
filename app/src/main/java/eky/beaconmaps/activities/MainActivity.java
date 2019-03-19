package eky.beaconmaps.activities;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import eky.beaconmaps.R;
import eky.beaconmaps.fragments.BeaconMapFragment;
import eky.beaconmaps.fragments.BeaconsNearbyFragment;
import eky.beaconmaps.fragments.ProfileFragment;
import eky.beaconmaps.fragments.SettingsFragment;

public class MainActivity extends BaseActivity {

    final Fragment fragment1 = new BeaconMapFragment();
    final Fragment fragment2 = new BeaconsNearbyFragment();
    final Fragment fragment3 = new ProfileFragment();
    final FragmentManager fm = getSupportFragmentManager();
    Fragment active = fragment1;

    private TextView toolbarTitle;
    private ImageButton buttonSettings;
    public static BottomNavigationView navigation;
    private String openFragmentTag = "1";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getIntent();
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbarTitle = toolbar.findViewById(R.id.tv_toolbar_title);
        toolbarTitle.setText(R.string.title_main_activity);
        setSupportActionBar(toolbar);

        buttonSettings = findViewById(R.id.ib_settings);
        buttonSettings.setOnClickListener(v -> {
            navigation.setVisibility(View.GONE);
            fm.beginTransaction().add(R.id.main_container, new SettingsFragment(), "4").commit();
            Toast.makeText(this,"OnClickSettings",Toast.LENGTH_LONG).show();
        });

        navigation = findViewById(R.id.navigation);
        navigation.setSelectedItemId(R.id.navigation_beacon_map);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        fm.beginTransaction().addToBackStack(null);
        fm.beginTransaction().add(R.id.main_container, fragment1, "1").commit();
        fm.beginTransaction().add(R.id.main_container, fragment2, "2").hide(fragment2).commit();
        fm.beginTransaction().add(R.id.main_container, fragment3, "3").hide(fragment3).commit();

    }

    @Override
    public void onBackPressed() {
        if (!TextUtils.isEmpty(openFragmentTag)) {
            //ToDo: add alert dialog that asks "are you sure to exit? ".
        } else {
            super.onBackPressed();
        }
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_beacons_nearby:
                    fm.beginTransaction().hide(active).show(fragment2).commit();
                    active = fragment2;
                    toolbarTitle.setText(R.string.title_beacons_nearby);
                    buttonSettings.setVisibility(View.GONE);
                    return true;

                case R.id.navigation_beacon_map:
                    fm.beginTransaction().hide(active).show(fragment1).commit();
                    active = fragment1;
                    toolbarTitle.setText(R.string.title_beacon_map);
                    buttonSettings.setVisibility(View.GONE);
                    return true;

                case R.id.navigation_profile:
                    fm.beginTransaction().hide(active).show(fragment3).commit();
                    active = fragment3;
                    toolbarTitle.setText(R.string.title_profile);
                    buttonSettings.setVisibility(View.VISIBLE);
                    return true;
            }
            return false;
        }
    };

}
