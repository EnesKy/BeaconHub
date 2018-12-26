package eky.beaconmaps.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.estimote.sdk.SystemRequirementsChecker;
import com.estimote.sdk.connection.scanner.ConfigurableDevicesScanner;

import java.util.List;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import eky.beaconmaps.R;

public class ConnectBeaconActivity extends AppCompatActivity {

    public static final String EXTRA_SCAN_RESULT_ITEM_DEVICE = "com.estimote.configuration.SCAN_RESULT_ITEM_DEVICE";
    public static final Integer RSSI_THRESHOLD = -50;
    private ConfigurableDevicesScanner devicesScanner;
    private TextView devicesCount;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        Intent i = getIntent();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_beacon);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.title_activity_connect_beacon);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
        }

        devicesCount = findViewById(R.id.tv_beacon_count);

        //Configuration
        devicesScanner = new ConfigurableDevicesScanner(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (SystemRequirementsChecker.checkWithDefaultDialogs(this)) {
            devicesScanner.scanForDevices(new ConfigurableDevicesScanner.ScannerCallback() {
                @Override
                public void onDevicesFound(List<ConfigurableDevicesScanner.ScanResultItem> list) {
                    devicesCount.setText(String.valueOf(list.size()) + " Beacon found nearby.");
                    if (!list.isEmpty()) {
                        ConfigurableDevicesScanner.ScanResultItem item = list.get(0);
                        if (item.rssi > RSSI_THRESHOLD) {
                            devicesScanner.stopScanning();
                            Intent intent = new Intent(ConnectBeaconActivity.this, ConfigureBeaconActivity.class);
                            intent.putExtra(EXTRA_SCAN_RESULT_ITEM_DEVICE, item.device);
                            startActivity(intent);
                        }
                    }
                }
            });
        }
    }

}
