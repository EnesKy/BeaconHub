package eky.beaconmaps.activities;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

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

        // Google sign out
        // mGoogleSignInClient.signOut().addOnCompleteListener(this,
        //       task -> updateUI(null));
    }

}
