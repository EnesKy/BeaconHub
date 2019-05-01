package eky.beaconmaps.fragments;

import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.Task;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import eky.beaconmaps.R;

public class BeaconMapFragment extends Fragment implements OnMapReadyCallback {

    private GoogleMap mMap;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private boolean mLocationPermissionGranted;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private final LatLng mDefaultLocation = new LatLng(41.0463356, 28.9432943);
    private Location currentLocation;
    private static final int LOCATION_REQUEST_CODE =101;
    private float zoom_level = 14;
    public LatLng tempLocation;
    private View view;

    public BeaconMapFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getActivity(),
                        android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[] {android.Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST_CODE);
            return;
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_beacon_map, container, false);

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());
        //fetchLastLocation();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        if (getArguments() != null && getArguments().get("KEY_LOC") != null) {
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom((LatLng) getArguments().get("KEY_LOC"), zoom_level));
            setArguments(null);
        }

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        //mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(getContext(), R.raw.gmap_style_night));

        if (tempLocation != null)
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(tempLocation, zoom_level));
        else
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mDefaultLocation, zoom_level));


        mMap.addMarker(new MarkerOptions().position(mDefaultLocation).title("Marker in Fsmvü"));


        mMap.setOnMapClickListener(latLng -> {

            //Clears the previously touched position
            //mMap.clear();

            /*mMap.addMarker(new MarkerOptions()
                    .position(latLng)
                    //.icon(bitmapDescriptorFromVector(getApplicationContext(), R.drawable.ic_icons8_plus))
                    .title("Beacon"))
                    .showInfoWindow();*/

            /*InfoWindowData info = new InfoWindowData();
            //info.setImage("snowqualmie");
            info.setId("Beacon ID");
            info.setName("Beacon Name");

            MapInfoWindowAdapter customInfoWindow = new MapInfoWindowAdapter(getApplicationContext());
            mMap.setInfoWindowAdapter(customInfoWindow);

            Marker m = mMap.addMarker(new MarkerOptions().position(latLng));
            m.setTag(info);
            m.showInfoWindow();*/

            mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));

            //BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.drawable.ic_icons8_plus);
            //markerOptions.icon(icon);
        });

        mMap.setOnMarkerClickListener(marker -> {
            marker.showInfoWindow();
            return true;
        });

        /*mMap.setOnMyLocationButtonClickListener(() -> {
            //Toast.makeText(getActivity(), "Clicked to the button", Toast.LENGTH_SHORT).show();
            return true;
        });*/

        getLocationPermission();

        //fetchLastLocation();

    }

    /**
     * Gets the current location of the device, and positions the map's camera.
     */
    private void fetchLastLocation() {
        @SuppressLint("MissingPermission")
        Task<Location> task = mFusedLocationProviderClient.getLastLocation();
        task.addOnSuccessListener(location -> {
            if (location != null) {
                currentLocation = location;
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                        new LatLng(currentLocation.getLatitude(),
                                currentLocation.getLongitude()), 17));
            }else{
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mDefaultLocation, 18));
            }
            updateLocationUI();
        });
    }

    /**
     * Prompts the user for permission to use the device location.
     */
    private void getLocationPermission() {
        /*
         * The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (ContextCompat.checkSelfPermission(getActivity(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
            fetchLastLocation();
        } else {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    /**
     * Handles the result of the request for location permissions.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        mLocationPermissionGranted = false;
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION:
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mLocationPermissionGranted = true;
                    fetchLastLocation();
                }
                break;

            case LOCATION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    fetchLastLocation();
                } else {
                    Toast.makeText(getActivity(),"Location permission missing",Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    /**
     * Updates the map's UI settings based on whether the user has granted location permission.
     */
    private void updateLocationUI() {

        if (mMap == null) {
            return;
        }
        try {
            if (mLocationPermissionGranted) {
                mMap.setMyLocationEnabled(true);
                mMap.getUiSettings().setMyLocationButtonEnabled(true);
            } else {
                mMap.setMyLocationEnabled(false);
                mMap.getUiSettings().setMyLocationButtonEnabled(false);
                currentLocation = null;
                getLocationPermission();
            }
        } catch (SecurityException e) {
            Log.e("Exception: %s", e.getMessage());
        }
    }

}
