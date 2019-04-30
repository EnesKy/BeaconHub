package eky.beaconmaps.activities;

import android.app.Dialog;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.utils.UrlBeaconUrlCompressor;

import java.util.ArrayList;
import java.util.List;

import androidx.browser.customtabs.CustomTabsIntent;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import eky.beaconmaps.R;
import eky.beaconmaps.adapter.BeaconItemAdapter;
import eky.beaconmaps.utils.PreferencesUtil;

public class SettingsActivity extends BaseActivity implements BeaconItemAdapter.ItemClickListener {

    public static final String TAG = "SettingsActivity";

    private MaterialButton btnSignOut;
    private TextView name, email, placeholder;
    private ImageView profilepic;

    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;
    private FirebaseUser user;
    private PreferencesUtil preferencesUtil;
    private List<Beacon> blockedBeaconsList;

    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getIntent();
        setContentView(R.layout.activity_settings);
        setSupportActionBar(findViewById(R.id.toolbar));

        preferencesUtil = new PreferencesUtil(this);
        blockedBeaconsList = preferencesUtil.getBlockedBeaconsList();
        if (blockedBeaconsList == null) {
            blockedBeaconsList = new ArrayList<>();
        }

        placeholder = findViewById(R.id.tv_placeholder);
        if (blockedBeaconsList.size() == 0)
            placeholder.setVisibility(View.VISIBLE);

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

        recyclerView = findViewById(R.id.rv_blocked_beacons);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new BeaconItemAdapter(blockedBeaconsList,false, true,this);
        recyclerView.setAdapter(adapter);

        name = findViewById(R.id.tv_user_name);
        email = findViewById(R.id.tv_user_email);
        profilepic = findViewById(R.id.iv_profile_pic);

        if (user != null) {
            if (user.getDisplayName() == null)
                name.setText(preferencesUtil.getData("KEY_USER_NAME",""));
            else
                name.setText(user.getDisplayName());

            email.setText(user.getEmail());

            if (user.getPhotoUrl() != null) //Todo: null ise default foto görünmüyor???
                Picasso.get().load(user.getPhotoUrl()).into(profilepic);
        }

    }

    private void signOut() {
        // Firebase sign out
        mAuth.signOut();
        mGoogleSignInClient.signOut();

        openActivity(null, LoginActivity.class);
    }

    public void openActionDialog(Beacon beacon, boolean isEddystone) { // TODO: Kullanıcının ise farklı text göster.
        Dialog beacon_dialog;
        TextView tvUnblock, tvWebUrl, tvLocation;

        beacon_dialog = new Dialog(this);
        beacon_dialog.setTitle("Add action");
        beacon_dialog.setContentView(R.layout.dialog_blocked_beacons);

        tvUnblock = beacon_dialog.findViewById(R.id.tv_unblock);
        tvUnblock.setOnClickListener(v -> {
            blockedBeaconsList.remove(beacon);
            preferencesUtil.saveBlockedBeaconsList(blockedBeaconsList);

            blockedBeaconsList.clear();
            blockedBeaconsList = preferencesUtil.getBlockedBeaconsList();
            adapter.notifyDataSetChanged();
            beacon_dialog.dismiss();
        });

        tvWebUrl = beacon_dialog.findViewById(R.id.tv_visit_website);
        tvWebUrl.setOnClickListener(v -> {

            if (isEddystone) {

                String url = UrlBeaconUrlCompressor.uncompress(beacon.getId1().toByteArray());
                CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
                CustomTabsIntent customTabsIntent = builder.build();
                customTabsIntent.launchUrl(this, Uri.parse(url));

            }

            beacon_dialog.dismiss();
        });
        tvLocation = beacon_dialog.findViewById(R.id.tv_go_location);
        tvLocation.setOnClickListener(v -> {

            beacon_dialog.dismiss();
        });

        beacon_dialog.show();
    }

    @Override
    public void onItemClick(int position, boolean isEddystone, View view) {
        openActionDialog(blockedBeaconsList.get(position), isEddystone);
    }
}
