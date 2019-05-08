package eky.beaconmaps.fragments;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.browser.customtabs.CustomTabsIntent;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import eky.beaconmaps.R;
import eky.beaconmaps.adapter.MarkerItemAdapter;
import eky.beaconmaps.model.BeaconData;
import eky.beaconmaps.model.MarkerData;

public class BeaconMapFragment extends Fragment implements OnMapReadyCallback, MarkerItemAdapter.ItemClickListener, SearchView.OnQueryTextListener {

    private GoogleMap mMap;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private boolean mLocationPermissionGranted;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private final LatLng mDefaultLocation = new LatLng(41.0463356, 28.9432943);
    private Location currentLocation;
    private static final int LOCATION_REQUEST_CODE = 101;
    private float zoom_level = 18;
    public LatLng tempLocation;
    private View view;

    private SearchView searchView;
    private RecyclerView recyclerView;
    private LinearLayout llMarkerList;
    private MarkerItemAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private ImageButton ibMarkerList;
    private List<MarkerData> markerDataList = new ArrayList<>();

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
            ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST_CODE);
            return;
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_beacon_map, container, false);

        recyclerView = view.findViewById(R.id.rv_marker);
        llMarkerList = view.findViewById(R.id.ll_rv_list);
        layoutManager = new LinearLayoutManager(inflater.getContext());
        recyclerView.setLayoutManager(layoutManager);

        ibMarkerList = view.findViewById(R.id.ib_open_marker_list);
        ibMarkerList.setOnClickListener(v -> {

            refreshMarkerList();

            if (llMarkerList.getVisibility() == View.GONE)
                llMarkerList.setVisibility(View.VISIBLE);
            else
                llMarkerList.setVisibility(View.GONE);
        });

        searchView = view.findViewById(R.id.sv_marker);
        searchView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            public void onGlobalLayout() {
                searchView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });
        searchView.setOnQueryTextListener(this);

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(Objects.requireNonNull(getActivity()));
        fetchLastLocation();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        if (getArguments() != null && getArguments().get("KEY_LOC") != null) {
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom((LatLng) getArguments().get("KEY_LOC"), zoom_level));
            setArguments(null);
        }

        refreshMarkerList();

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setZoomControlsEnabled(true);

        //mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(getContext(), R.raw.gmap_style_night));

        /*View locationButton = ((View) this.getView().findViewById(Integer.parseInt("1")).getParent()).findViewById(Integer.parseInt("2"));
        RelativeLayout.LayoutParams rlp = (RelativeLayout.LayoutParams) locationButton.getLayoutParams();
        //rlp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
        rlp.setMargins(0, 200, 70, 0);*/

        if (tempLocation != null)
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(tempLocation, zoom_level));
        else
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(mDefaultLocation, zoom_level));

        //TODO: Delete this
        mMap.addMarker(new MarkerOptions().position(mDefaultLocation).title("Marker in Fsmvü"));

        Location loc = new Location("Fsm");
        loc.setLatitude(mDefaultLocation.latitude);
        loc.setLongitude(mDefaultLocation.longitude);

        mMap.setOnMapClickListener(latLng -> {

            /*mMap.clear();

            //Marker icon
            //BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.drawable.beacon_maps_no_background_icon);*/

            mMap.addMarker(new MarkerOptions()
                    .position(latLng)
                    //.icon(icon)
                    .title("Beacon")
                    .snippet("Description"))
                    .showInfoWindow();

            mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));

            loc.setLatitude(latLng.latitude);
            loc.setLongitude(latLng.longitude);
        });

        mMap.setOnMyLocationButtonClickListener(() -> {
            if (currentLocation != null)
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom
                        (new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()), zoom_level));

            Snackbar.make(Objects.requireNonNull(getActivity()).findViewById(R.id.cl_main),
                    "Distance between Current Location and last added marker : " + currentLocation.distanceTo(loc) + " m", Snackbar.LENGTH_LONG)
                    .setAction("Ok", view -> {
                    })
                    .setActionTextColor(getResources().getColor(R.color.rallyGreen))
                    .setAnimationMode(BaseTransientBottomBar.ANIMATION_MODE_SLIDE)
                    .show();

            return true;
        });

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
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                        new LatLng(currentLocation.getLatitude(),
                                currentLocation.getLongitude()), 18));
            } else {
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(mDefaultLocation, 18));
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
                    Snackbar.make(Objects.requireNonNull(getActivity()).findViewById(R.id.cl_main),
                            "Location permission missing", Snackbar.LENGTH_LONG)
                            .setAction("Ok", view -> {
                            })
                            .setActionTextColor(getResources().getColor(R.color.rallyGreen))
                            .show();
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
                mMap.getUiSettings().setTiltGesturesEnabled(true);
                mMap.setBuildingsEnabled(true);
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

    public void refreshMarkerList() {
        if (currentLocation != null) {
            //TODO: get registered beacondata list from database and refresh list
            markerDataList.clear();

            /*for(BeaconData beaconData : LIST) {
                Location loc = new Location("Temp");

                if(beaconData.getLocation() != null) {
                    loc.setLatitude(beaconData.getLocation().latitude);
                    loc.setLongitude(beaconData.getLocation().longitude);
                    markerDataList.add(new MarkerData(beaconData, currentLocation.distanceTo(loc)));
                }

            }*/

            BeaconData b1 = new BeaconData("Test 1 Title",
                    "Test 1 Descpription", "www.fsmvu.com", new LatLng(41.0446681, 28.9470421));

            BeaconData b2 = new BeaconData("Test 2 Title",
                    "Test 2 Descpription", "www.fsmvu.com", new LatLng(41.044943, 28.945840));

            BeaconData b3 = new BeaconData("Test 3 Title",
                    "Test 3 Descpription", "www.fsmvu.com", new LatLng(41.046869, 28.941817));

            BeaconData b4 = new BeaconData("Test 4 Title",
                    "Test 4 Descpription", "www.google.com", new LatLng(41.046100, 28.945239));

            markerDataList.add(new MarkerData(b1, currentLocation.distanceTo(latLng2Loc(b1.getLocation()))));

            markerDataList.add(new MarkerData(b2, currentLocation.distanceTo(latLng2Loc(b2.getLocation()))));

            markerDataList.add(new MarkerData(b3, currentLocation.distanceTo(latLng2Loc(b3.getLocation()))));

            markerDataList.add(new MarkerData(b4, currentLocation.distanceTo(latLng2Loc(b4.getLocation()))));

            for (MarkerData markerData : markerDataList)
                mMap.addMarker(new MarkerOptions().position(markerData.getBeaconData().getLocation()).title("Test"));

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                //Sorting the beacons by their distance to phone.
                Comparator<MarkerData> markerDistanceComparator = Comparator.comparing(MarkerData::getDistance);
                Collections.sort(markerDataList, markerDistanceComparator);
            }

            //TODO: 500m den uzakları listeden çıkartabilirsin???

            if (adapter == null) {
                adapter = new MarkerItemAdapter(markerDataList, this);
                recyclerView.setAdapter(adapter);
            } else {
                adapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    public void onItemClick(int position, View view) {
        //TODO: OnClickte websiteye ?????
        llMarkerList.setVisibility(View.GONE);
        openActionDialog(markerDataList.get(position).getBeaconData());
    }

    public Location latLng2Loc(LatLng loc) {
        Location temp = new Location("Temp");
        temp.setLatitude(loc.latitude);
        temp.setLongitude(loc.longitude);
        return temp;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        adapter.getFilter().filter(newText);
        return false;
    }

    public void openActionDialog(BeaconData beacon) {
        Dialog dialog;
        TextView tvLocation, tvWebsite;

        dialog = new Dialog(Objects.requireNonNull(getActivity()));
        dialog.setContentView(R.layout.dialog_marker);

        tvLocation = dialog.findViewById(R.id.tv_see_location);
        tvWebsite = dialog.findViewById(R.id.tv_visit_website);

        if (beacon.getWebUrl() == null || beacon.getWebUrl().isEmpty())
            tvWebsite.setVisibility(View.GONE);

        tvLocation.setOnClickListener(v -> {
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(beacon.getLocation(), zoom_level));
            dialog.dismiss();
        });

        tvWebsite.setOnClickListener(v -> {
            String url = "https://" + beacon.getWebUrl();
            CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
            CustomTabsIntent customTabsIntent = builder.build();
            builder.setToolbarColor(ContextCompat.getColor(getActivity(), R.color.rallyGreen));
            customTabsIntent.launchUrl(getActivity(), Uri.parse(url));

            dialog.dismiss();
        });

        dialog.show();
    }
}
