package eky.beaconmaps.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

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

}
