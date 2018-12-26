package eky.beaconmaps.activity;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import eky.beaconmaps.R;

public class BaseActivity extends AppCompatActivity {

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_search:
                    Toast.makeText(getApplicationContext(), "Home", Toast.LENGTH_SHORT);
                    return true;
                case R.id.navigation_beacon_map:
                    Toast.makeText(getApplicationContext(), "BeaconMap", Toast.LENGTH_SHORT);
                    return true;
                case R.id.navigation_profile:
                    Toast.makeText(getApplicationContext(), "Profile", Toast.LENGTH_SHORT);
                    //TODO: Intent to Profile Screen
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.title_activity_profile);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
        }

        BottomNavigationView navigation = findViewById(R.id.bottom_navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }

}
