package eky.beaconmaps.activities;

import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;
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
import com.google.android.material.button.MaterialButton;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import eky.beaconmaps.R;
import eky.beaconmaps.model.BeaconData;
import eky.beaconmaps.utils.FirebaseUtil;
import eky.beaconmaps.utils.PreferencesUtil;

public class LocationActivity extends BaseActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private boolean mLocationPermissionGranted;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private final LatLng mDefaultLocation = new LatLng(41.0463356, 28.9432943);
    private Location currentLocation;
    private static final int LOCATION_REQUEST_CODE =101;
    private float zoom_level = 14;

    private TextView tvUUID, tvMajor, tvMinor;
    private EditText etTitle, etDescription;
    private TextView tvLocation;
    private MaterialButton btnApply;

    private BeaconData beaconData;
    private LatLng beaconLoc;
    private PreferencesUtil preferencesUtil;

    private String title = "Company Name", desc = "Company Description";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);
        setSupportActionBar(findViewById(R.id.toolbar));

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map_location);
        mapFragment.getMapAsync(this);

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        preferencesUtil = new PreferencesUtil(this);

        if (preferencesUtil.getObject("claimed", BeaconData.class) != null)
            beaconData = preferencesUtil.getObject("claimed", BeaconData.class);

        if (beaconData != null) {
            if (beaconData.getCompanyName() != null)
                title = beaconData.getCompanyName();

            if (beaconData.getCompanyDesc() != null)
                desc = beaconData.getCompanyDesc();

            if (beaconData.getLocation() != null)
                beaconLoc = beaconData.getLatLng();
        }

        etTitle = findViewById(R.id.et_title_marker);
        etTitle.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (mMap != null) {

                    mMap.clear();
                    title = s.toString();

                    LatLng temp;
                    if (beaconLoc != null)
                        temp = beaconLoc;
                    else
                        temp = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());

                    mMap.addMarker(new MarkerOptions()
                            .position(temp)
                            .draggable(true)
                            .title(title)
                            .snippet(desc))
                            .showInfoWindow();
                }

            }

            @Override
            public void afterTextChanged(Editable s) { }
        });

        etDescription = findViewById(R.id.et_title_marker_desc);
        etDescription.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (mMap != null) {
                    mMap.clear();
                    desc = s.toString();

                    LatLng temp;
                    if (beaconLoc != null)
                        temp = beaconLoc;
                    else
                        temp = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());

                    mMap.addMarker(new MarkerOptions()
                            .position(temp)
                            .draggable(true)
                            .title(title)
                            .snippet(desc))
                            .showInfoWindow();
                }

            }

            @Override
            public void afterTextChanged(Editable s) { }
        });

        tvLocation = findViewById(R.id.tv_location_info);

        btnApply = findViewById(R.id.btn_apply);
        btnApply.setOnClickListener(v -> {

            beaconData.setLocation(new eky.beaconmaps.model.Location(beaconLoc.latitude, beaconLoc.longitude));
            beaconData.setCompanyName(etTitle.getText().toString());
            beaconData.setCompanyDesc(etDescription.getText().toString());

            preferencesUtil.saveObject("claimed", beaconData);

            FirebaseUtil.updateBeaconData(beaconData, "location");
            preferencesUtil.updateLists();

            finish();
        });

        if (beaconData != null) {
            if (beaconData.getLocation() != null)
                tvLocation.setText(beaconData.getLocation().getLat()+ " , " + beaconData.getLocation().getLng());
            if (beaconData.getCompanyName() != null)
                etTitle.setText(beaconData.getCompanyName());
            if (beaconData.getCompanyDesc() != null)
                etDescription.setText(beaconData.getCompanyDesc());

            tvUUID = findViewById(R.id.tv_uuid);
            tvMajor = findViewById(R.id.tv_major);
            tvMinor = findViewById(R.id.tv_minor);

            if (beaconData.getUuid() != null && !beaconData.getUuid().isEmpty()) {
                tvUUID.setText("UUID : " + beaconData.getUuid());
                tvMajor.setText("Major : " + beaconData.getMajor());
                tvMinor.setText("Minor : " + beaconData.getMinor());
            }
            else if (beaconData.getBeacon() != null) {
                tvUUID.setText("UUID : " + beaconData.getBeacon().getId1().toString());
                tvMajor.setText("Major : " + beaconData.getBeacon().getId2().toString());
                tvMinor.setText("Minor : " + beaconData.getBeacon().getId3().toString());
            }
        }

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        fetchLastLocation();

        if (beaconLoc != null) {
            mMap.animateCamera(CameraUpdateFactory.newLatLng(beaconLoc));
            mMap.addMarker(new MarkerOptions()
                    .draggable(true)
                    .position(beaconLoc)
                    .title(title)
                    .snippet(desc)).showInfoWindow();
        }


        mMap.setOnMapClickListener(latLng -> {

            beaconLoc = latLng;

            mMap.clear();

            mMap.addMarker(new MarkerOptions()
                    .draggable(true)
                    .position(beaconLoc)
                    .title(title)
                    .snippet(desc)).showInfoWindow();

            mMap.animateCamera(CameraUpdateFactory.newLatLng(beaconLoc));
            tvLocation.setText(beaconLoc.latitude + " , " + beaconLoc.longitude);

        });

        mMap.setOnMyLocationButtonClickListener(() -> {

            mMap.clear();

            fetchLastLocation();

            mMap.addMarker(new MarkerOptions()
                    .position(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()))
                    .draggable(true)
                    .position(beaconLoc)
                    .title(title)
                    .snippet(desc)).showInfoWindow();

            return true;
        });

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
                beaconLoc = new LatLng(location.getLatitude(), location.getLongitude());
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(beaconLoc, 17));
                tvLocation.setText(location.getLatitude() + " , " + location.getLongitude());
            } else {
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(mDefaultLocation, 18));
                tvLocation.setText(mDefaultLocation.latitude + " , " + mDefaultLocation.longitude);
            }
            updateLocationUI();
        });
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

    /**
     * Prompts the user for permission to use the device location.
     */
    private void getLocationPermission() {
        /*
         * The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
            fetchLastLocation();
        } else {
            ActivityCompat.requestPermissions(this,
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
                    Toast.makeText(this,"Location permission missing",Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }
}
