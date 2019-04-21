package eky.beaconmaps.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import eky.beaconmaps.R;

public class ProfileFragment extends Fragment {

    private TextView placeholder;

    public ProfileFragment() {
        // Required empty public constructor
    }

    /**
     private void signOut() {
        mAuth.signOut();
     }

     FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
     if (user != null) {
     // Name, email address, and profile photo Url
     String name = user.getDisplayName();
     String email = user.getEmail();
     Uri photoUrl = user.getPhotoUrl();

     // Check if user's email is verified
     boolean emailVerified = user.isEmailVerified();

     // The user's ID, unique to the Firebase project. Do NOT use this value to
     // authenticate with your backend server, if you have one. Use
     // FirebaseUser.getIdToken() instead.
     String uid = user.getUid();
     }
     */

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_profile, container, false);

        placeholder = rootView.findViewById(R.id.tv_placeholder);
        placeholder.setVisibility(View.VISIBLE);

        return rootView;
    }

}
