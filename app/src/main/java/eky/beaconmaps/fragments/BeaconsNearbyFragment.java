package eky.beaconmaps.fragments;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.RemoteException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

import androidx.appcompat.widget.SearchView;
import androidx.browser.customtabs.CustomTabsIntent;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import eky.beaconmaps.R;
import eky.beaconmaps.activities.MainActivity;
import eky.beaconmaps.adapter.BeaconAdapter;
import eky.beaconmaps.model.BeaconData;
import eky.beaconmaps.utils.PreferencesUtil;

public class BeaconsNearbyFragment extends Fragment implements RangeNotifier, BeaconConsumer,
        SearchView.OnQueryTextListener, View.OnClickListener, BeaconAdapter.ItemClickListener {

    public static final String TAG = "BeaconsNearbyFragment";

    private BeaconManager beaconManager;
    private RecyclerView recyclerView;
    private BeaconAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private SearchView searchView;
    private TextView placeholder;
    private ProgressBar progressBarLoading;
    private FloatingActionButton fabScanControl;
    private ConstraintLayout clSearch, clFilterOptions;

    private Boolean isScanEnabled = false;
    private Region all;
    private PreferencesUtil preferencesUtil;

    private List<BeaconData> beaconList = new ArrayList<>();
    private List<BeaconData> blockedBeaconsList;
    private List<BeaconData> mBeaconDataList = new ArrayList<>();

    public BeaconsNearbyFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        preferencesUtil = new PreferencesUtil(Objects.requireNonNull(getActivity()));
        blockedBeaconsList = preferencesUtil.getBlockedBeaconsList();
        if (blockedBeaconsList == null) {
            blockedBeaconsList = new ArrayList<>();
        }

        beaconManager = BeaconManager.getInstanceForApplication(Objects.requireNonNull(getActivity()));
        setBeaconsLayout();
        beaconManager.bind(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_beacons_nearby, container, false);

        placeholder = rootView.findViewById(R.id.tv_placeholder);

        progressBarLoading = rootView.findViewById(R.id.pb_loading);
        progressBarLoading.setVisibility(View.VISIBLE);

        fabScanControl = rootView.findViewById(R.id.fab_scan_stopper);
        fabScanControl.setOnClickListener(this);

        recyclerView = rootView.findViewById(R.id.rv_beacons);
        layoutManager = new LinearLayoutManager(inflater.getContext());
        recyclerView.setLayoutManager(layoutManager);

        searchView = rootView.findViewById(R.id.beacon_search_view);
        clFilterOptions = rootView.findViewById(R.id.cl_search_options);
        clSearch = rootView.findViewById(R.id.cl_search);

        searchView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            public void onGlobalLayout() {
                searchView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });

        clSearch.setOnClickListener(this);

        searchView.setOnQueryTextListener(this);

        return rootView;
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        blockedBeaconsList = preferencesUtil.getBlockedBeaconsList();
        if (blockedBeaconsList == null) {
            blockedBeaconsList = new ArrayList<>();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        //Makes the keyboard open above the bottom nav view
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fab_scan_stopper:
                scanControl();
                break;
            case R.id.cl_search:
                if (clFilterOptions.getVisibility() == View.GONE)
                    clFilterOptions.setVisibility(View.VISIBLE);
                else
                    clFilterOptions.setVisibility(View.GONE);
                break;
        }
    }

    private void scanControl() {
        if (isScanEnabled)
            stopScanning();
        else
            startScanning();
    }

    private void startScanning() {
        isScanEnabled = true;
        fabScanControl.setImageDrawable(getResources().getDrawable(R.drawable.ic_pause_black_24dp));
        try {
            beaconManager.startRangingBeaconsInRegion(all);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        progressBarLoading.setVisibility(View.VISIBLE);
    }

    private void stopScanning() {
        isScanEnabled = false;
        fabScanControl.setImageDrawable(getResources().getDrawable(R.drawable.ic_play_arrow_black_24dp));
        try {
            beaconManager.stopMonitoringBeaconsInRegion(all);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        progressBarLoading.setVisibility(View.GONE);
    }

    @Override
    public void didRangeBeaconsInRegion(Collection<Beacon> beacons, Region region) {

        placeholder.setVisibility(View.GONE);

        List<BeaconData> temp = new ArrayList<>();
        for (Beacon beacon : beacons)
            temp.add(new BeaconData(beacon));

        List<BeaconData> unblockedBeacons = new ArrayList<>();
        blockedBeaconsList = preferencesUtil.getBlockedBeaconsList();

        if (blockedBeaconsList == null) {
            unblockedBeacons.addAll(temp);
        } else {
            for (BeaconData unblocked : temp)
                if (!blockedBeaconsList.contains(unblocked))
                    unblockedBeacons.add(unblocked);
        }

        if (unblockedBeacons.size() != 0 && isScanEnabled) {

            placeholder.setVisibility(View.GONE);

            List<Beacon> sortedList = new ArrayList<>();

            for (BeaconData beaconData : unblockedBeacons)
                sortedList.add(beaconData.getBeacon());

            temp = new ArrayList<>();

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                //Sorting the beacons by their distance to phone.
                Comparator<Beacon> beaconDistanceComparator = Comparator.comparing(Beacon::getDistance);
                Collections.sort(sortedList, beaconDistanceComparator);
            }

            for(Beacon beacon : sortedList) {
                //todo: databaseden çektiğin listede yer alıyorsa listeden aldığın beacondata bilgisini döndür.

                temp.add(new BeaconData(beacon));
                //FirebaseUtil.saveBeaconData(new BeaconData(beacon));
            }

            beaconList.clear();
            beaconList.addAll(temp);

            if (adapter == null) {
                adapter = new BeaconAdapter(beaconList, true, this);
                recyclerView.setAdapter(adapter);
            } else {
                if (isScanEnabled) {
                    adapter.notifyDataSetChanged();
                }
            }
        } else if (beaconList.size() == 0) {
            placeholder.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onBeaconServiceConnect() {
        try {
            all = new Region("allIBeacons", null, null, null);
            beaconManager.startRangingBeaconsInRegion(all);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        isScanEnabled = true;
        beaconManager.setForegroundScanPeriod(5000);
        beaconManager.setForegroundBetweenScanPeriod(10000);
        beaconManager.setBackgroundScanPeriod(5000);
        beaconManager.setBackgroundBetweenScanPeriod(20000);
        beaconManager.addRangeNotifier(this);
    }

    @Override
    public Context getApplicationContext() {
        return Objects.requireNonNull(getActivity()).getApplicationContext();
    }

    @Override
    public void unbindService(ServiceConnection serviceConnection) {
        Objects.requireNonNull(getActivity()).unbindService(serviceConnection);
    }

    @Override
    public boolean bindService(Intent intent, ServiceConnection serviceConnection, int i) {
        return Objects.requireNonNull(getActivity()).bindService(intent, serviceConnection, i);
    }

    private void setBeaconsLayout() {
        beaconManager.getBeaconParsers().clear();

        //iBeacon
        beaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout("m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24"));

        /**
         * TODO: Eddystone URL parsing işlemi yapılamadığı için şimdilik rafa kaldırıldı.
         * beaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout(BeaconParser.EDDYSTONE_URL_LAYOUT));
         **/
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        stopScanning();
        adapter.getFilter().filter(newText);
        return false;
    }

    @Override
    public void onItemClick(int position, View view) {
        stopScanning();

        openActionDialog(beaconList.get(position));

    }

    public void openActionDialog(BeaconData beacon) {

        //TODO: Tıklanan beacon sistemde yer alıyorsa ve url'e sahipse listeden çek ve ekle.

        Dialog beacon_dialog;
        TextView tvBlockBeacon, tvWebUrl, tvLocation, tvClaimBeacon;
        TextView tvUUID, tvMajor, tvMinor;

        beacon_dialog = new Dialog(Objects.requireNonNull(getActivity()));
        beacon_dialog.setTitle("Add action");
        beacon_dialog.setContentView(R.layout.dialog_nearby_beacons);

        tvUUID = beacon_dialog.findViewById(R.id.tv_uuid);
        tvMajor = beacon_dialog.findViewById(R.id.tv_major);
        tvMinor = beacon_dialog.findViewById(R.id.tv_minor);

        if (beacon.getBeaconID() != null) {
            tvUUID.setText("UUID : " + beacon.getBeaconID().getProximityUUID().toString());
            tvMajor.setText("Major : " + beacon.getBeaconID().getMajor());
            tvMinor.setText("Minor : " + beacon.getBeaconID().getMinor());
        }
        else if (beacon.getBeacon() != null) {
            tvUUID.setText("UUID : " + beacon.getBeacon().getId1().toString());
            tvMajor.setText("Major : " + beacon.getBeacon().getId2().toString());
            tvMinor.setText("Minor : " + beacon.getBeacon().getId3().toString());
        }

        tvWebUrl = beacon_dialog.findViewById(R.id.tv_visit_website);

        if (beacon.getWebUrl() == null || beacon.getWebUrl().isEmpty()) {
            tvWebUrl.setVisibility(View.GONE);
        } else {
            tvWebUrl.setOnClickListener(v -> {
                //String url = UrlBeaconUrlCompressor.uncompress(beacon.getId1().toByteArray());
                CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
                CustomTabsIntent customTabsIntent = builder.build();
                //customTabsIntent.launchUrl(getActivity(), Uri.parse(url));

                beacon_dialog.dismiss();
            });
        }

        tvLocation = beacon_dialog.findViewById(R.id.tv_go_location);
        if (beacon.getBeaconID() == null || beacon.getBeaconID().getLocation() == null) {
            tvLocation.setVisibility(View.GONE);
        } else {
            tvLocation.setOnClickListener(v -> {
                //TODO: Düzenle bu işlemi. Map fragmenta geçiş yapıp lokasyona git.
                Bundle bundle = new Bundle();
                //bundle.putParcelable(TAG, beacon);
                Intent intent = new Intent(getActivity(), MainActivity.class);
                startActivity(intent);

                beacon_dialog.dismiss();
            });
        }

        tvBlockBeacon = beacon_dialog.findViewById(R.id.tv_add_blocklist);
        tvBlockBeacon.setOnClickListener(v -> {

            beacon.setBlocked(true);
            blockedBeaconsList.add(beacon);
            preferencesUtil.saveBlockedBeaconsList(blockedBeaconsList);

            Snackbar snack = Snackbar.make(Objects.requireNonNull(getActivity()).findViewById(R.id.cl_main),
                    "Beacon added to Blocklist. \n" +
                          "You will no longer see this beacon while scanning.", Snackbar.LENGTH_LONG)
                    .setAction("Ok", view -> {})
                    .setActionTextColor(getResources().getColor(R.color.rallyGreen));
            ((TextView) snack.getView().findViewById(com.google.android.material.R.id.snackbar_text)).setSingleLine(false);
            snack.show();

            beacon_dialog.dismiss();
        });

        tvClaimBeacon = beacon_dialog.findViewById(R.id.tv_claim_beacon);
        tvClaimBeacon.setOnClickListener(v -> {

            claimBeacon(beacon);

            beacon_dialog.dismiss();
        });

        if (preferencesUtil.getMyBeaconsList() != null) {
            if (preferencesUtil.getMyBeaconsList().contains(beacon)) {
                tvClaimBeacon.setVisibility(View.GONE);
            }
        }

        beacon_dialog.show();
    }

    public void claimBeacon(BeaconData beacon) {

        // TODO: Sistemde yer alan bir beacon ise bu seçenek görünmemeli ve sahip olduğu özellikler gösterilmeli.

        mBeaconDataList = preferencesUtil.getMyBeaconsList();

        if (mBeaconDataList == null) {
            mBeaconDataList = new ArrayList<>();
        }

        mBeaconDataList.add(beacon);
        preferencesUtil.saveMyBeaconsList(mBeaconDataList);
        //TODO: add to users beacons and registered beacons
        Snackbar.make(Objects.requireNonNull(getActivity()).findViewById(R.id.cl_main),
                "Beacon claim successful.", Snackbar.LENGTH_LONG)
                .setAction("Ok", view -> { })
                .setActionTextColor(getResources().getColor(R.color.rallyGreen))
                .show();
    }

}
