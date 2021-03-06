package eky.beaconmaps.activities;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import eky.beaconmaps.R;
import eky.beaconmaps.adapter.BeaconAdapter;
import eky.beaconmaps.model.BeaconData;
import eky.beaconmaps.utils.FirebaseUtil;
import eky.beaconmaps.utils.PreferencesUtil;

public class SettingsActivity extends BaseActivity implements BeaconAdapter.ItemClickListener {

    public static final String TAG = "SettingsActivity";

    private MaterialButton btnSignOut;
    private TextView name, email, placeholder;
    private ImageView profilepic;

    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;
    private FirebaseUser user;
    private PreferencesUtil preferencesUtil;
    private List<BeaconData> blockedBeaconsList;

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
        //blockedBeaconsList = preferencesUtil.getBlockedBeaconsList();
        blockedBeaconsList = FirebaseUtil.blocklist;
        if (blockedBeaconsList == null) {
            blockedBeaconsList = new ArrayList<>();
        }

        isNetworkAvailable();

        placeholder = findViewById(R.id.tv_placeholder);
        if (blockedBeaconsList.size() == 0 && FirebaseUtil.blocklist.size() == 0)
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
        btnSignOut.setOnClickListener(v -> {
            Snackbar.make(this.findViewById(R.id.cl_main),
                    "Are you sure?", Snackbar.LENGTH_LONG)
                    .setAction("Ok", view -> { signOut(); })
                    .setActionTextColor(getResources().getColor(R.color.rallyGreen))
                    .show();
        });

        recyclerView = findViewById(R.id.rv_blocked_beacons);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new BeaconAdapter(blockedBeaconsList, false, this);
        recyclerView.setAdapter(adapter);

        name = findViewById(R.id.tv_user_name);
        email = findViewById(R.id.tv_user_email);
        profilepic = findViewById(R.id.iv_profile_pic);

        if (user != null) {
            if (user.getDisplayName() == null)
                name.setText(preferencesUtil.getData("KEY_USER_NAME", ""));
            else
                name.setText(user.getDisplayName());

            email.setText(user.getEmail());

            if (user.getPhotoUrl() != null)
                //Glide.with(this).load(user.getPhotoUrl()).into(profilepic);
                Picasso.get().load(user.getPhotoUrl()).into(profilepic, new Callback() {
                    @Override
                    public void onSuccess() {
                        //TODO: resim yüklenene kadar önceki stock resim görünsün.
                    }

                    @Override
                    public void onError(Exception e) {

                    }
                });
        }

    }

    private void signOut() {
        // Firebase sign out
        mAuth.signOut();
        mGoogleSignInClient.signOut();

        preferencesUtil.clearAllData();

        Intent i = new Intent(this, LoginActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);
    }

    @Override
    public void onItemClick(int position, View view) {
        openActionDialog(blockedBeaconsList.get(position));
    }

    public void openActionDialog(BeaconData beacon) {
        Dialog beacon_dialog;
        TextView tvUUID, tvMajor, tvMinor;
        TextView tvUnblock;

        beacon_dialog = new Dialog(this);
        beacon_dialog.setTitle("Add action");
        beacon_dialog.setContentView(R.layout.dialog_blocked_beacons);

        tvUUID = beacon_dialog.findViewById(R.id.tv_uuid);
        tvMajor = beacon_dialog.findViewById(R.id.tv_major);
        tvMinor = beacon_dialog.findViewById(R.id.tv_minor);

        if (beacon.getUuid() != null && !beacon.getUuid().isEmpty()) {
            tvUUID.setText("UUID : " + beacon.getUuid());
            tvMajor.setText("Major : " + beacon.getMajor());
            tvMinor.setText("Minor : " + beacon.getMinor());
        }
        else if (beacon.getBeacon() != null) {
            tvUUID.setText("UUID : " + beacon.getBeacon().getId1().toString());
            tvMajor.setText("Major : " + beacon.getBeacon().getId2().toString());
            tvMinor.setText("Minor : " + beacon.getBeacon().getId3().toString());
        }

        tvUnblock = beacon_dialog.findViewById(R.id.tv_unblock);
        tvUnblock.setOnClickListener(v -> {
            beacon.setBlocked(false);

            if (preferencesUtil.getMyBeaconsList() != null && preferencesUtil.getMyBeaconsList().contains(beacon))
                FirebaseUtil.updateBeaconData(beacon, "block");

            blockedBeaconsList.remove(beacon);
            FirebaseUtil.removeBlockedBeacon(beacon);

            preferencesUtil.updateLists();

            adapter = new BeaconAdapter(FirebaseUtil.blocklist, false, this);
            recyclerView.setAdapter(adapter);

            if (blockedBeaconsList.size() == 0 && FirebaseUtil.blocklist.size() == 0)
                placeholder.setVisibility(View.VISIBLE);

            beacon_dialog.dismiss();
        });

        beacon_dialog.show();
    }

}
