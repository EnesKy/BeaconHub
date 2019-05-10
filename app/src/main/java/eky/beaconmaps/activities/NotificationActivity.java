package eky.beaconmaps.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import eky.beaconmaps.R;
import eky.beaconmaps.model.BeaconData;
import eky.beaconmaps.model.NotificationData;
import eky.beaconmaps.utils.FirebaseUtil;
import eky.beaconmaps.utils.PreferencesUtil;

public class NotificationActivity extends AppCompatActivity implements View.OnClickListener {

    EditText entranceTitle;
    EditText entranceDesc;
    EditText exitTitle;
    EditText exitDesc;
    ImageButton helloIcon;
    ImageButton exitIcon;
    Button done;
    TextView companyName, companyName2;
    PreferencesUtil preferencesUtil;
    BeaconData beaconData;
    private TextView tvUUID, tvMajor, tvMinor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getIntent();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        preferencesUtil = new PreferencesUtil(this);

        entranceTitle = findViewById(R.id.hello_title);
        entranceDesc = findViewById(R.id.hello_desc);
        exitTitle = findViewById(R.id.exit_title);
        exitDesc = findViewById(R.id.exit_desc);
        done = findViewById(R.id.btn_done);
        done.setOnClickListener(this);

        companyName = findViewById(R.id.tv_company_name1);
        companyName2 = findViewById(R.id.tv_company_name2);

        if (preferencesUtil.getObject("clicked", BeaconData.class) != null) {
            beaconData = preferencesUtil.getObject("clicked", BeaconData.class);
            if (beaconData.getCompanyName() != null){
                companyName.setHint(beaconData.getCompanyName());
                companyName2.setHint(beaconData.getCompanyName());
            }
            if (beaconData.getNotificationData() != null) {
                entranceTitle.setText(beaconData.getNotificationData().getEnterTitle());
                entranceDesc.setText(beaconData.getNotificationData().getEnterDesc());
                exitTitle.setText(beaconData.getNotificationData().getExitTitle());
                exitDesc.setText(beaconData.getNotificationData().getExitTitle());
            }

            tvUUID = findViewById(R.id.tv_uuid);
            tvMajor = findViewById(R.id.tv_major);
            tvMinor = findViewById(R.id.tv_minor);

            if (beaconData.getUuid() != null && !beaconData.getUuid().isEmpty()) {
                tvUUID.setText("UUID : " + beaconData.getUuid());
                tvMajor.setText("Major : " + beaconData.getMajor());
                tvMinor.setText("Minor : " + beaconData.getMinor());
            }
            else if (beaconData.getBeacon() != null) {
                tvUUID.setText("UUID : " + beaconData.getBeacon().getId1().toString());
                tvMajor.setText("Major : " + beaconData.getBeacon().getId2().toString());
                tvMinor.setText("Minor : " + beaconData.getBeacon().getId3().toString());
            }
        }
    }

    @Override
    public void onClick(View view) {
        int viewId = view.getId();
        switch (viewId) {
            case R.id.btn_done:
                if (!entranceTitle.getText().toString().isEmpty() && !entranceDesc.getText().toString().isEmpty() &&
                        !exitTitle.getText().toString().isEmpty() && !exitDesc.getText().toString().isEmpty()) {

                    NotificationData notificationData = new NotificationData(
                            entranceTitle.getText().toString(), entranceDesc.getText().toString(),
                            exitTitle.getText().toString(), exitDesc.getText().toString());
                    beaconData.setNotificationData(notificationData);
                    preferencesUtil.saveObject("clicked", beaconData);

                    FirebaseUtil.updateBeaconData(beaconData, "notification");
                    preferencesUtil.updateLists();

                    Snackbar.make(findViewById(R.id.cl_main),
                            "Successful.", Snackbar.LENGTH_LONG)
                            .show();

                    finish();
                } else {
                    Snackbar.make(findViewById(R.id.cl_main),
                            "Fill the blank to add notification info.", Snackbar.LENGTH_LONG)
                            .show();
                }
                break;
        }
    }

}
