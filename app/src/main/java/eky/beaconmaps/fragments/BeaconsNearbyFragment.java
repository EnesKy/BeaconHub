package eky.beaconmaps.fragments;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.RemoteException;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

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
import java.util.Locale;
import java.util.Objects;

import androidx.appcompat.widget.SearchView;
import androidx.browser.customtabs.CustomTabsIntent;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import eky.beaconmaps.R;
import eky.beaconmaps.activities.LocationActivity;
import eky.beaconmaps.adapter.BeaconAdapter;
import eky.beaconmaps.model.BeaconData;
import eky.beaconmaps.utils.PreferencesUtil;

public class BeaconsNearbyFragment extends Fragment implements RangeNotifier, BeaconConsumer,
        SearchView.OnQueryTextListener, View.OnClickListener, BeaconAdapter.ItemClickListener {

    public static final String TAG = "BeaconsNearbyFragment";

    private BeaconManager beaconManager;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private SearchView searchView;
    private CountDownTimer countDownTimer;
    private String query;
    private TextView placeholder;
    private ProgressBar progressBarLoading;
    private FloatingActionButton fabScanControl;

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

        searchView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            public void onGlobalLayout() {
                searchView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });

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
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fab_scan_stopper:
                scanControl();
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

        List<BeaconData> temp = new ArrayList<>();

        if (isScanEnabled) {

            for (Beacon blocked : beacons)
                if (blockedBeaconsList != null && blockedBeaconsList.contains(blocked))
                    beacons.remove(blocked);

        }

        if (beacons.size() != 0 && isScanEnabled) {

            placeholder.setVisibility(View.GONE);
            List<Beacon> sortedList = new ArrayList<>(beacons);

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                //Sorting the beacons by their distance to phone.
                Comparator<Beacon> beaconDistanceComparator = Comparator.comparing(Beacon::getDistance);
                Collections.sort(sortedList, beaconDistanceComparator);
            }

            for(Beacon beacon : sortedList)
                temp.add(new BeaconData(beacon));

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
            all = new Region("rangingId", null, null, null);
            beaconManager.startRangingBeaconsInRegion(all);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        isScanEnabled = true;
        beaconManager.setForegroundScanPeriod(1000);
        beaconManager.setForegroundBetweenScanPeriod(2000);
        beaconManager.setBackgroundScanPeriod(1000);
        beaconManager.setBackgroundBetweenScanPeriod(10000);
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
        beaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout("m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24")); //iBeacon

        /**
         * TODO: Eddystone URL parsing işlemi yapılamadığı için şimdilik rafa kaldırıldı.
         * beaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout(BeaconParser.EDDYSTONE_URL_LAYOUT));
         **/
    }

    public void initializeCountDownTimer() {
        countDownTimer = new CountDownTimer(750, 1000) {
            public void onTick(long millisUntilFinished) {
            }

            public void onFinish() {
                filterBeaconList(query);
                countDownTimer.cancel();
            }
        };
    }

    private void filterBeaconList(String query) {
        List<Beacon> temp = new ArrayList<>();
        for (BeaconData beacon : beaconList) {
            /*for (Identifier identifier : beacon.getIdentifiers()) {
                if (identifier.toString().contains(query)) {
                    temp.add(beacon);
                }
            }*/
        }
        //TODO: change list with temp. When query used stop scanning beacon until query cancelled.
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        newText = newText.toUpperCase(Locale.US);
        countDownTimer.start();
        if (!TextUtils.isEmpty(newText)) {
            if (countDownTimer != null) {
                countDownTimer.cancel();
            }
            countDownTimer.start();
        }
        query = newText;
        return false;
    }

    @Override
    public void onItemClick(int position, View view) {
        //Bundle bundle = new Bundle();
        //bundle.putParcelable(TAG, beaconList.get(position));
        //Intent intent = new Intent(getActivity(), NotificationActivity.class);
        //startActivity(intent);
        stopScanning();

        openActionDialog(beaconList.get(position));

        Toast.makeText(getActivity(), "Clicked to beacon with uuid: " +
                beaconList.get(position).getBeacon().getId1(), Toast.LENGTH_LONG).show();
    }

    public void openActionDialog(BeaconData beacon) {

        //TODO: Tıklanan beacon sistemde yer alıyorsa ve url'e sahipse listeden çek ve ekle.

        Dialog beacon_dialog;
        TextView tvNotification;
        TextView tvWebUrl;
        TextView tvLocation;
        TextView tvClaimBeacon;

        beacon_dialog = new Dialog(Objects.requireNonNull(getActivity()));
        beacon_dialog.setTitle("Add action");
        beacon_dialog.setContentView(R.layout.dialog_nearby_beacons);

        tvWebUrl = beacon_dialog.findViewById(R.id.tv_visit_website);
        tvWebUrl.setOnClickListener(v -> {
            //String url = UrlBeaconUrlCompressor.uncompress(beacon.getId1().toByteArray());
            CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
            CustomTabsIntent customTabsIntent = builder.build();
            //customTabsIntent.launchUrl(getActivity(), Uri.parse(url));

            Toast.makeText(getActivity(), "Clicked to visit website.", Toast.LENGTH_SHORT).show();
            beacon_dialog.dismiss();
        });
        tvLocation = beacon_dialog.findViewById(R.id.tv_go_location);
        tvLocation.setOnClickListener(v -> {

            Bundle bundle = new Bundle();
            //bundle.putParcelable(TAG, beacon);
            Intent intent = new Intent(getActivity(), LocationActivity.class);
            startActivity(intent);

            beacon_dialog.dismiss();
        });

        tvNotification = beacon_dialog.findViewById(R.id.tv_add_blocklist);
        tvNotification.setOnClickListener(v -> {
            //URLParser.urlParse("http://www.google.com", getActivity(), beacon);
            //URLParser.urlParsing(getActivity(),beacon);

            blockedBeaconsList.add(beacon);
            preferencesUtil.saveBlockedBeaconsList(blockedBeaconsList);

            beacon_dialog.dismiss();
        });

        tvClaimBeacon = beacon_dialog.findViewById(R.id.tv_claim_beacon);
        tvClaimBeacon.setOnClickListener(v -> {
            Toast.makeText(getActivity(), "Clicked to claim beacon.", Toast.LENGTH_SHORT).show();

            claimBeacon(beacon);

            beacon_dialog.dismiss();
        });

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
        Toast.makeText(getActivity(), "Beacon claim successful.", Toast.LENGTH_SHORT).show();
    }

}
