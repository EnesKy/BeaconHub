package eky.beaconmaps.activities;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import eky.beaconmaps.BeaconMaps;
import eky.beaconmaps.R;
import eky.beaconmaps.fragments.BeaconMapFragment;
import eky.beaconmaps.fragments.BeaconsNearbyFragment;
import eky.beaconmaps.fragments.ProfileFragment;
import eky.beaconmaps.model.BeaconData;
import eky.beaconmaps.utils.PreferencesUtil;

public class MainActivity extends BaseActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    private final String TAG = "MainActivity";

    private Fragment fragment1 = new BeaconMapFragment();
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
    private Bundle bundle = new Bundle();
    private PreferencesUtil preferencesUtil;

    private LatLng locFromNotification;
    private String notificationIdentity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getIntent();
        setContentView(R.layout.activity_main);

        preferencesUtil = new PreferencesUtil(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbarTitle = toolbar.findViewById(R.id.tv_toolbar_title);
        toolbarTitle.setText(R.string.title_main_activity);
        setSupportActionBar(toolbar);

        buttonSettings = findViewById(R.id.ib_settings);
        buttonSettings.setOnClickListener(v -> openActivity(null, SettingsActivity.class));

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
    protected void onResume() {
        super.onResume();
        isNetworkAvailable();

        BeaconMaps app = (BeaconMaps) getApplication();
        if (!app.isBeaconNotificationsEnabled()) {
            Log.d("BaseActivity", "Enabling beacon notifications");
            app.enableBeaconNotifications();
        }

    }

    @Override
    public void onBackPressed() {

        if (BeaconMapFragment.isMarkerListOpen) {
            BeaconMapFragment.llMarkerList.setVisibility(View.GONE);
            BeaconMapFragment.isMarkerListOpen = false;
        } else if (!lastOpenedFragments.isEmpty()) {
            changeFragments();
        } else {
            lastOpened = null;
            openAlertDialog();
            //super.onBackPressed();
        }
    }

    /**
     * Notification click event route method.
     * @param intent
     */
    @Override
    protected void onNewIntent(Intent intent) {
        isNetworkAvailable();

        if (intent.getExtras() != null) {
            if (intent.getExtras().get("NOTIFICATION_CLICK") != null) {
                String identity = (String) intent.getExtras().get("NOTIFICATION_CLICK");
                if (preferencesUtil.getRegisteredBeaconList() != null) {
                    for (BeaconData beaconData : preferencesUtil.getRegisteredBeaconList()) {
                        if (beaconData.getIdentity().equals(identity)) {
                            if (beaconData.getLocation() != null) {
                                locFromNotification = beaconData.getLatLng();
                                bundle.putParcelable("KEY_LOC", locFromNotification);
                            }
                        }
                    }
                }
            } else if (intent.getExtras().get("KEY_LOC") != null) {

                locFromNotification = (LatLng) intent.getExtras().get("KEY_LOC");
                bundle.putParcelable("KEY_LOC", locFromNotification);

            }
        }

        if (locFromNotification != null){
            fragment1.setArguments(bundle);
            // Todo crashes -> java.lang.IllegalStateException: Fragment already added and state has been saved

            // Eğer uygulama açıksa ve görünür fragment beaconMap değilse.
            if (active != fragment1)
                navigation.setSelectedItemId(R.id.navigation_beacon_map);
            else
                fragment1.onResume();
        }

        super.onNewIntent(intent);
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
                    addTolastOpenedFragments(active);
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
                    addTolastOpenedFragments(active);
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
                    addTolastOpenedFragments(active);
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
        isNetworkAvailable();
        lastOpened = lastOpenedFragments.get(lastOpenedFragments.size() - 1);
        lastOpenedFragments.remove(lastOpenedFragments.size() - 1);

        fm.beginTransaction().hide(active).show(lastOpened).commit();

        active = lastOpened;

        backPressed = true;
        navigation.setSelectedItemId(fragmentsBar.get(active));
    }

    public boolean addTolastOpenedFragments(Fragment fragment) {
        if (lastOpenedFragments.size() < 5) {
            lastOpenedFragments.add(fragment);
            return true;
        } else {
            return false;
        }
    }

    public void openAlertDialog() {
        AlertDialog.Builder alertBuild = new AlertDialog.Builder(this)
                .setTitle("Warning")
                .setMessage("Are you sure to exit?")
                .setPositiveButton("OK", (dialog, which) -> finish())
                .setNegativeButton("CANCEL", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = alertBuild.create();
        //dialog.show();

        Snackbar.make(this.findViewById(R.id.cl_main),
                "Do you want to exit?" , Snackbar.LENGTH_LONG)
                .setAction(" Yes ", view -> { finish(); })
                .setActionTextColor(getResources().getColor(R.color.rallyGreen))
                .setAnimationMode(BaseTransientBottomBar.ANIMATION_MODE_SLIDE)
                .show();

    }

}
