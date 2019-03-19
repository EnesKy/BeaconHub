package eky.beaconmaps.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import androidx.fragment.app.Fragment;
import eky.beaconmaps.R;

public class BeaconMapFragment extends Fragment implements OnMapReadyCallback {

    private GoogleMap mMap;

    public BeaconMapFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_beacon_map, container, false);

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        return v;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        //mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(getContext(), R.raw.gmap_style_night));

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(41.0463356, 28.9432943);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Fsmvü"));
        float zoom_level = 10;
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, zoom_level));
    }
}
