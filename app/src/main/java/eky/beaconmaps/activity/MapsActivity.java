package eky.beaconmaps.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.estimote.proximity_sdk.api.EstimoteCloudCredentials;
import com.estimote.sdk.SystemRequirementsChecker;
import com.estimote.sdk.connection.scanner.ConfigurableDevicesScanner;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import eky.beaconmaps.BeaconMaps;
import eky.beaconmaps.R;
import eky.beaconmaps.datamodel.BeaconData;

public class MapsActivity extends FragmentActivity implements View.OnClickListener, OnMapReadyCallback {

    private GoogleMap mMap;
    private static final String TAG = MapsActivity.class.getSimpleName();
    public static final String EXTRA_SCAN_RESULT_ITEM_DEVICE = "com.estimote.configuration.SCAN_RESULT_ITEM_DEVICE";
    private CameraPosition mCameraPosition;
    private FusedLocationProviderClient mFusedLocationProviderClient;

    // A default location (FSMVU Halic) and default zoom to use when location permission is not granted.
    private final LatLng mDefaultLocation = new LatLng(41.0463356, 28.9432943);
    private String defaultLocationText = "41.0463356, 28.9432943";
    private static final int ZOOM_LEVEL = 18;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private boolean mLocationPermissionGranted;
    private static Location mLastKnownLocation;

    // Keys for storing activity state.
    private static final String KEY_CAMERA_POSITION = "camera_position";
    private static final String KEY_LOCATION = "location";

    //Spinner item lists
    List<String> beaconSpnList = new ArrayList();
    List<String> functionList = new ArrayList();
    List<ConfigurableDevicesScanner.ScanResultItem> beaconList;
    private int funcPos = -1;
    private int spnBeaconPos = 0;
    ConfigurableDevicesScanner.ScanResultItem beacon = null;

    private Dialog beacon_dialog;
    TextView tvBeacon;
    Spinner spnBeacon;
    TextView tvFunc;
    Spinner spnFunc;
    Button btnCancel;
    Button btnDone;
    FloatingActionButton fButton;
    Animation animShake;

    private ConfigurableDevicesScanner devicesScanner;
    private BeaconData beacondata;
    private List<BeaconData> beaconDataList = new ArrayList();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Intent i = getIntent();
        super.onCreate(savedInstanceState);

        // Retrieve location and camera position from saved instance state.
        if (savedInstanceState != null) {
            mLastKnownLocation = savedInstanceState.getParcelable(KEY_LOCATION);
            mCameraPosition = savedInstanceState.getParcelable(KEY_CAMERA_POSITION);
        }

        setContentView(R.layout.activity_maps);

        // Construct a FusedLocationProviderClient.
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // if you want to access Estimote Cloud credentials from BeaconMaps:
        EstimoteCloudCredentials estimoteCloudCredentials = ((BeaconMaps) getApplication()).cloudCredentials;

        beacon_dialog = new Dialog(MapsActivity.this);
        beacon_dialog.setTitle("Select Beacon and It's Function");
        beacon_dialog.setContentView(R.layout.select_beacon_dialog);

        tvBeacon = beacon_dialog.findViewById(R.id.tv_beacon);
        spnBeacon = beacon_dialog.findViewById(R.id.spn_beacon);
        tvFunc = beacon_dialog.findViewById(R.id.tv_function);
        spnFunc = beacon_dialog.findViewById(R.id.spn_function);
        btnCancel = beacon_dialog.findViewById(R.id.btn_cancel);
        btnDone = beacon_dialog.findViewById(R.id.btn_done);

        beaconSpnList.add("Select a Beacon");
        beaconSpnList.add("Select Closest Beacon");

        functionList.add("Select a function");
        functionList.add("Notification");

        fButton = findViewById(R.id.floating_button);
        fButton.setOnClickListener(this);
        animShake = AnimationUtils.loadAnimation(this, R.anim.shake_animation);

        //Configuration
        devicesScanner = new ConfigurableDevicesScanner(this);

        //
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                    BeaconData temp = childSnapshot.getValue(BeaconData.class);
                    beaconDataList.add(temp);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("beaconData", "Yakalanamadı :/");
            }
        };
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        DatabaseReference databaseBook = database.child("beaconData");
        database.addValueEventListener(valueEventListener);

        Timer myTimer = new Timer();
        myTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                updatePins();
            }

        }, 0, 3000);

    }

    @Override
    public void onClick(View view) {
        int viewId = view.getId();
        switch (viewId) {
            case R.id.floating_button:
                beacon_dialog.show();

                //TODO: Spinnerları kaldır. Edittext koy. Dialog koy. Recycler viewlı tam ekranda görünür olsun
                //TODO: Beacon seçim cell tasarımında icon, ID, vs vs bilgiler olsun....

                spnBeacon.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        if (position > 1) {
                            position = 7;
                        }

                        spnBeaconPos = position;

                        switch (position) {
                            case 7:
                                devicesScanner.stopScanning();
                                beacon = getDeviceFromId(parent.getSelectedItem().toString());
                                break;
                        }
                        Log.d("spnBeacon", "Selected item = " + parent.getSelectedItem().toString());
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                        Log.d("spnBeacon", "---NothingSelected---");
                    }
                });

                spnFunc.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                        switch (parent.getSelectedItem().toString()) {
                            case "Notification":
                                funcPos = 1;
                                break;
                        }
                        Log.d("spnFunc", "Selected item = " + parent.getSelectedItem().toString());
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                        Log.d("spnBeacon", "---NothingSelected---");
                    }
                });

                ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getApplicationContext(),
                        android.R.layout.simple_spinner_item, beaconSpnList);
                dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spnBeacon.setAdapter(dataAdapter);

                ArrayAdapter<String> dataAdapter2 = new ArrayAdapter<String>(getApplicationContext(),
                        android.R.layout.simple_spinner_item, functionList);
                dataAdapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spnFunc.setAdapter(dataAdapter2);

                btnCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        beacon_dialog.cancel();
                    }
                });

                btnDone.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.d("beaconDialog", "Clicked Done Button");
                        /*if (funcPos == 1 && spnBeaconPos) {
                            Intent i = new Intent(MapsActivity.this, NotificationActivity.class);
                            startActivity(i);
                        }*/
                        if (funcPos != 1 && spnBeaconPos == 0) {
                            spnFunc.startAnimation(animShake);
                            spnBeacon.startAnimation(animShake);
                            Toast.makeText(getApplicationContext(), "Please make a selection.", Toast.LENGTH_SHORT).show();
                        } else if (funcPos != 1) {
                            spnFunc.startAnimation(animShake);
                            Toast.makeText(getApplicationContext(), "Please select a function.", Toast.LENGTH_SHORT).show();
                        } else if (spnBeaconPos == 0) {
                            spnBeacon.startAnimation(animShake);
                            Toast.makeText(getApplicationContext(), "Please select a beacon.", Toast.LENGTH_SHORT).show();
                        } else if (spnBeaconPos == 1 && funcPos == 1) {
                            Intent i = new Intent(MapsActivity.this, ConnectBeaconActivity.class);
                            startActivity(i);
                            beacon_dialog.dismiss();
                        } else if (spnBeaconPos == 7 && funcPos == 1) {
                            if (beacon != null) {
                                Intent intent = new Intent(MapsActivity.this, ConfigureBeaconActivity.class);
                                intent.putExtra(EXTRA_SCAN_RESULT_ITEM_DEVICE, beacon.device);
                                startActivity(intent);
                            }
                        }
                    }
                });
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (SystemRequirementsChecker.checkWithDefaultDialogs(this)) {
            devicesScanner.scanForDevices(new ConfigurableDevicesScanner.ScannerCallback() {
                @Override
                public void onDevicesFound(List<ConfigurableDevicesScanner.ScanResultItem> list) {
                    beaconList = list;
                    if (!beaconList.isEmpty() && beaconSpnList.size() == 2) {
                        fButton.startAnimation(animShake);
                        for (ConfigurableDevicesScanner.ScanResultItem item : beaconList) {
                            if (!beaconSpnList.contains(item.device.deviceId.toString())) {
                                beaconSpnList.add(item.device.deviceId.toString());
                            }
                        }
                    }
                    if (!beaconSpnList.isEmpty() && !list.isEmpty()) {
                        if (beaconSpnList.size() > 2 && list.get(0).device.deviceId.toString() != beaconSpnList.get(2)) {
                            beaconSpnList.clear();
                            beaconSpnList.add("Select a Beacon");
                            beaconSpnList.add("Select Closest Beacon");
                            fButton.startAnimation(animShake);
                            for (ConfigurableDevicesScanner.ScanResultItem beacon : list) {
                                beaconSpnList.add(beacon.device.deviceId.toString());
                            }
                        }
                    }
                }
            });
        }

    }

    /**
     * Saves the state of the map when the activity is paused.
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (mMap != null) {
            outState.putParcelable(KEY_CAMERA_POSITION, mMap.getCameraPosition());
            outState.putParcelable(KEY_LOCATION, mLastKnownLocation);
            super.onSaveInstanceState(outState);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        //mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.style_json));

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {

                // Clears the previously touched position
                mMap.clear();

                mMap.addMarker(new MarkerOptions()
                        .position(latLng)
                        //.icon(bitmapDescriptorFromVector(getApplicationContext(), R.drawable.ic_icons8_plus))
                        .title("Beacon"))
                        .showInfoWindow();

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
            }
        });

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                marker.showInfoWindow();
                return true;
            }
        });

        mMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
            @Override
            public boolean onMyLocationButtonClick() {
                Toast.makeText(getApplicationContext(), "Clicked to the button", Toast.LENGTH_SHORT).show();
                return true;
            }
        });

        // Prompt the user for permission.
        getLocationPermission();

        // Turn on the My Location layer and the related control on the map.
        updateLocationUI();

        // Get the current location of the device and set the position of the map.
        getDeviceLocation();
    }

    /**
     * Gets the current location of the device, and positions the map's camera.
     */
    private void getDeviceLocation() {
        try {
            if (mLocationPermissionGranted) {
                Task<Location> locationResult = mFusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener(this, new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful()) {
                            // Set the map's camera position to the current location of the device.
                            mLastKnownLocation = task.getResult();
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                    new LatLng(mLastKnownLocation.getLatitude(),
                                            mLastKnownLocation.getLongitude()), ZOOM_LEVEL));
                        } else {
                            Log.d(TAG, "Current location is null. Using defaults.");
                            Log.e(TAG, "Exception: %s", task.getException());
                            mMap.moveCamera(CameraUpdateFactory
                                    .newLatLngZoom(mDefaultLocation, ZOOM_LEVEL));
                            mMap.getUiSettings().setMyLocationButtonEnabled(false);
                            defaultLocationText = "Your Location : " + defaultLocationText;
                        }
                    }
                });
            }
        } catch (SecurityException e) {
            Log.e("Exception: %s", e.getMessage());
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
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mLocationPermissionGranted = true;
                }
            }
        }
        updateLocationUI();
    }

    /**
     * Prompts the user for permission to use the device location.
     */
    private void getLocationPermission() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
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
                mLastKnownLocation = null;
                getLocationPermission();
            }
        } catch (SecurityException e) {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    private BitmapDescriptor bitmapDescriptorFromVector(Context context, int vectorResId) {
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorResId);
        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    private ConfigurableDevicesScanner.ScanResultItem getDeviceFromId(String id) {
        for (String deviceId : beaconSpnList) {
            if (deviceId.equals(id)) {
                for (ConfigurableDevicesScanner.ScanResultItem item : beaconList) {
                    if (item.device.deviceId.toString().equals(id)) {
                        return item;
                    }
                }
            }
        }
        return null;
    }

    public static Location getmLastKnownLocation() {
        return mLastKnownLocation;
    }

    public void updatePins() {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                LatLng temp;
                if (!beaconDataList.isEmpty()) {
                    for (BeaconData beacon : beaconDataList) {
                        String beaconLoc = beacon.getLocation();
                        temp = new LatLng(Double.parseDouble((beaconLoc.split(",")[0])),
                                Double.parseDouble((beaconLoc.split(",")[1])));
                        mMap.addMarker(new MarkerOptions()
                                .position(temp)
                                //.icon(bitmapDescriptorFromVector(getApplicationContext(), R.drawable.ic_icons8_plus))
                                .title("Beacon - " + beacon.getuuid()))
                                .showInfoWindow();
                    }
                }
            }
        });

    }

}
