package eky.beaconmaps.activities;

import android.app.Dialog;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.utils.UrlBeaconUrlCompressor;

import androidx.browser.customtabs.CustomTabsIntent;
import eky.beaconmaps.R;

public class SettingsActivity extends BaseActivity {

    public static final String TAG = "SettingsActivity";

    private MaterialButton btnSignOut;

    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;
    private FirebaseUser user;
    private TextView name, email;
    private ImageView profilepic;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getIntent();
        setContentView(R.layout.activity_settings);

        setSupportActionBar(findViewById(R.id.toolbar));

        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        btnSignOut = findViewById(R.id.btn_sign_out);
        btnSignOut.setOnClickListener(v -> signOut());

        if (user != null) {
            name = findViewById(R.id.tv_user_name);
            name.setText(user.getDisplayName());
            email = findViewById(R.id.tv_user_email);
            email.setText(user.getEmail());
            profilepic = findViewById(R.id.iv_profile_pic);
            Picasso.get().load(user.getPhotoUrl()).into(profilepic);
        }

    }

    private void signOut() {
        // Firebase sign out
        mAuth.signOut();
        mGoogleSignInClient.signOut();

        openActivity(null, LoginActivity.class);
    }

    public void openActionDialog(Beacon beacon, boolean isEddystone) { // TODO: Beacon bilgisi ekle. ?? Kullanıcının ise farklı text göster.
        Dialog beacon_dialog;
        TextView tvNotification;
        TextView tvWebUrl;
        TextView tvLocation;

        beacon_dialog = new Dialog(this);
        beacon_dialog.setTitle("Add action");
        beacon_dialog.setContentView(R.layout.dialog_nearby_beacons);

        tvWebUrl = beacon_dialog.findViewById(R.id.tv_visit_website);
        tvWebUrl.setOnClickListener(v -> {

            if (isEddystone) {

                String url = UrlBeaconUrlCompressor.uncompress(beacon.getId1().toByteArray());
                CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
                CustomTabsIntent customTabsIntent = builder.build();
                customTabsIntent.launchUrl(this, Uri.parse(url));

            }

            Toast.makeText(this,"Clicked to visit website.", Toast.LENGTH_SHORT).show();
        });
        tvLocation = beacon_dialog.findViewById(R.id.tv_go_location);
        tvLocation.setOnClickListener(v -> {
            Toast.makeText(this,"Clicked to go to location.", Toast.LENGTH_SHORT).show();
        });

        beacon_dialog.show();
    }

}
