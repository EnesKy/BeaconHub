package eky.beaconmaps.fragments;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.utils.UrlBeaconUrlCompressor;

import java.util.Objects;

import androidx.browser.customtabs.CustomTabsIntent;
import androidx.fragment.app.Fragment;
import eky.beaconmaps.R;

public class ProfileFragment extends Fragment {

    private TextView placeholder, title;
    private FirebaseUser user;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        user = FirebaseAuth.getInstance().getCurrentUser();

    }

    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_profile, container, false);

        placeholder = rootView.findViewById(R.id.tv_placeholder);
        placeholder.setVisibility(View.VISIBLE);

        title = rootView.findViewById(R.id.tv_title);

        if (user != null) {

            title.setText(user.getDisplayName() + "'s Beacons");

            user.getIdToken(true).addOnSuccessListener(result -> {
                String idToken = result.getToken();
                //Do whatever
                Log.d("ProfileFragment", "GetTokenResult result = " + idToken);
            });
        }

        return rootView;
    }

    public void openActionDialog(Beacon beacon, boolean isEddystone) { // TODO: Beacon bilgisi ekle. ?? Kullanıcının ise farklı text göster.
        Dialog beacon_dialog;
        TextView tvNotification;
        TextView tvWebUrl;
        TextView tvLocation;

        beacon_dialog = new Dialog(Objects.requireNonNull(getActivity()));
        beacon_dialog.setTitle("Add action");
        beacon_dialog.setContentView(R.layout.dialog_nearby_beacons);

        tvWebUrl = beacon_dialog.findViewById(R.id.tv_visit_website);
        tvWebUrl.setOnClickListener(v -> {

            if (isEddystone) {

                String url = UrlBeaconUrlCompressor.uncompress(beacon.getId1().toByteArray());
                CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
                CustomTabsIntent customTabsIntent = builder.build();
                customTabsIntent.launchUrl(getActivity(), Uri.parse(url));

            }

            Toast.makeText(getActivity(),"Clicked to visit website.", Toast.LENGTH_SHORT).show();
        });
        tvLocation = beacon_dialog.findViewById(R.id.tv_go_location);
        tvLocation.setOnClickListener(v -> {
            Toast.makeText(getActivity(),"Clicked to go to location.", Toast.LENGTH_SHORT).show();
        });

        beacon_dialog.show();
    }

}
