package eky.beaconmaps.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import eky.beaconmaps.R;

public class BeaconsNearbyFragment extends Fragment {

    public BeaconsNearbyFragment() {
        // Required empty public constructor
    }

    /*
    public static BeaconsNearbyFragment newInstance(String param1, String param2) {
        BeaconsNearbyFragment fragment = new BeaconsNearbyFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }*/

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_beacons_nearby, container, false);
    }

}
