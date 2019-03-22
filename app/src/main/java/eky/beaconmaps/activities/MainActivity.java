package eky.beaconmaps.activities;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import eky.beaconmaps.R;
import eky.beaconmaps.fragments.BeaconMapFragment;
import eky.beaconmaps.fragments.BeaconsNearbyFragment;
import eky.beaconmaps.fragments.ProfileFragment;

public class MainActivity extends BaseActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    private final Fragment fragment1 = new BeaconMapFragment();
    private final Fragment fragment2 = new BeaconsNearbyFragment();
    private final Fragment fragment3 = new ProfileFragment();
    private final FragmentManager fm = getSupportFragmentManager();
    private Fragment active = fragment1;
    private Fragment lastOpened;
    private List<Fragment> lastOpenedFragments = new ArrayList<>();
    private HashMap<Fragment, Integer> fragmentsBar = new HashMap<>();

    private TextView toolbarTitle;
    private ImageButton buttonSettings;
    public static BottomNavigationView navigation;
    private boolean backPressed = false;

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
            openActivity(null, SettingsActivity.class);
        });

        navigation = findViewById(R.id.navigation);
        navigation.setSelectedItemId(R.id.navigation_beacon_map);
        navigation.setOnNavigationItemSelectedListener(this);

        fm.beginTransaction().addToBackStack(null);
        fm.beginTransaction().add(R.id.main_container, fragment1, "1").commit();
        fm.beginTransaction().add(R.id.main_container, fragment2, "2").hide(fragment2).commit();
        fm.beginTransaction().add(R.id.main_container, fragment3, "3").hide(fragment3).commit();

        fragmentsBar.put(fragment1, R.id.navigation_beacon_map);
        fragmentsBar.put(fragment2, R.id.navigation_beacons_nearby);
        fragmentsBar.put(fragment3, R.id.navigation_profile);

    }

    @Override
    public void onBackPressed() {
        if (!lastOpenedFragments.isEmpty()) {
            changeFragments();
        } else {
            lastOpened = null;
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.navigation_beacons_nearby:
                if (!fragment2.isHidden()) {
                    return true;
                }
                if (backPressed) {
                    backPressed = false;
                } else {
                    lastOpenedFragments.add(active);
                    fm.beginTransaction().hide(active).show(fragment2).commit();
                    active = fragment2;
                }
                toolbarTitle.setText(R.string.title_beacons_nearby);
                buttonSettings.setVisibility(View.GONE);
                return true;

            case R.id.navigation_beacon_map:
                if (!fragment1.isHidden()) {
                    return true;
                }
                if (backPressed) {
                    backPressed = false;
                } else {
                    lastOpenedFragments.add(active);
                    fm.beginTransaction().hide(active).show(fragment1).commit();
                    active = fragment1;
                }
                toolbarTitle.setText(R.string.title_beacon_map);
                buttonSettings.setVisibility(View.GONE);
                return true;

            case R.id.navigation_profile:
                if (!fragment3.isHidden()) {
                    return true;
                }
                if (backPressed) {
                    backPressed = false;
                } else {
                    lastOpenedFragments.add(active);
                    fm.beginTransaction().hide(active).show(fragment3).commit();
                    active = fragment3;
                }
                toolbarTitle.setText(R.string.title_profile);
                buttonSettings.setVisibility(View.VISIBLE);
                return true;
        }
        return false;
    }

    public void changeFragments() {
        lastOpened = lastOpenedFragments.get(lastOpenedFragments.size() - 1);
        lastOpenedFragments.remove(lastOpenedFragments.size() - 1);

        fm.beginTransaction().hide(active).show(lastOpened).commit();

        active = lastOpened;

        backPressed = true;
        navigation.setSelectedItemId(fragmentsBar.get(active));
    }

    public void openAlertDialog() {
        AlertDialog.Builder alertBuild = new AlertDialog.Builder(this)
                .setTitle("Warning")
                .setMessage("Are you sure to exit?")
                .setPositiveButton("OK", (dialog, which) -> super.onBackPressed())
                .setNeutralButton("CANCEL", (dialog, which) -> {
                    dialog.dismiss();
                });

        AlertDialog dialog = alertBuild.create();
        dialog.show();
    }

}
