package eky.beaconmaps.activities;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import eky.beaconmaps.R;
import eky.beaconmaps.fragments.BeaconMapFragment;
import eky.beaconmaps.fragments.BeaconsNearbyFragment;
import eky.beaconmaps.fragments.ProfileFragment;
import me.anwarshahriar.calligrapher.Calligrapher;

public class MainActivity extends AppCompatActivity {

    final Fragment fragment1 = new BeaconMapFragment();
    final Fragment fragment2 = new BeaconsNearbyFragment();
    final Fragment fragment3 = new ProfileFragment();
    final FragmentManager fm = getSupportFragmentManager();
    Fragment active = fragment1;

    TextView toolbarTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getIntent();
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbarTitle = toolbar.findViewById(R.id.tv_toolbar_title);
        toolbarTitle.setText(R.string.title_main_activity);
        setSupportActionBar(toolbar);

        Calligrapher calligrapher = new Calligrapher(this);
        calligrapher.setFont(this, "font/MuseoSans.otf", true);

        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setSelectedItemId(R.id.navigation_beacon_map);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        fm.beginTransaction().add(R.id.main_container, fragment3, "3").hide(fragment3).commit();
        fm.beginTransaction().add(R.id.main_container, fragment2, "2").hide(fragment2).commit();
        fm.beginTransaction().add(R.id.main_container, fragment1, "1").commit();

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
                    return true;

                case R.id.navigation_beacon_map:
                    fm.beginTransaction().hide(active).show(fragment1).commit();
                    active = fragment1;
                    toolbarTitle.setText(R.string.title_beacon_map);
                    return true;

                case R.id.navigation_profile:
                    fm.beginTransaction().hide(active).show(fragment3).commit();
                    active = fragment3;
                    toolbarTitle.setText(R.string.title_profile);
                    return true;
            }
            return false;
        }
    };

}
