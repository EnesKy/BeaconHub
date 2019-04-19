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
import org.altbeacon.beacon.Identifier;
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
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import eky.beaconmaps.R;
import eky.beaconmaps.adapter.BeaconAdapter;

public class BeaconsNearbyFragment extends Fragment implements RangeNotifier, BeaconConsumer,
                                            SearchView.OnQueryTextListener, BeaconAdapter.ItemClickListener, View.OnClickListener {

    public static final String TAG = "BeaconsNearbyFragment";

    private BeaconManager beaconManager;
    private List<Beacon> beaconList;
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

    public BeaconsNearbyFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        beaconManager = BeaconManager.getInstanceForApplication(Objects.requireNonNull(getActivity()));
        setBeaconsLayout();
        beaconManager.bind(this);

        beaconList = new ArrayList<>();

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

        all = new Region("rangingId", null, null, null);

        return rootView;
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

        if (isScanEnabled) {
            isScanEnabled = false;
            fabScanControl.setImageDrawable(getResources().getDrawable(R.drawable.ic_play_arrow_black_24dp));
            try {
                beaconManager.stopMonitoringBeaconsInRegion(all);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            progressBarLoading.setVisibility(View.GONE);
        } else {
            isScanEnabled = true;
            fabScanControl.setImageDrawable(getResources().getDrawable(R.drawable.ic_pause_black_24dp));
            try {
                beaconManager.startRangingBeaconsInRegion(all);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            progressBarLoading.setVisibility(View.VISIBLE);
        }

    }

    @Override
    public void didRangeBeaconsInRegion(Collection<Beacon> beacons, Region region) {

        beaconList.clear();
        beaconList.addAll(beacons);

        if (beaconList.size() != 0) {

            placeholder.setVisibility(View.GONE);

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                //Sorting the beacons by their distance to phone.
                Comparator<Beacon> beaconDistanceComparator = null;
                beaconDistanceComparator = Comparator.comparing(Beacon::getDistance);
                Collections.sort(beaconList, beaconDistanceComparator);
            }

            if (adapter == null) {
                adapter = new BeaconAdapter(beaconList, this);
                recyclerView.setAdapter(adapter);
            } else {
                if (isScanEnabled) {
                    adapter.notifyDataSetChanged();
                }
            }
        } else {
            placeholder.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onBeaconServiceConnect() {
        try {
            beaconManager.startRangingBeaconsInRegion(all);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        isScanEnabled = true;
        beaconManager.setForegroundScanPeriod(3000);
        beaconManager.setBackgroundScanPeriod(5000);
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
        //beaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout(BeaconParser.EDDYSTONE_URL_LAYOUT));
        // TODO: Bu satırı açarsan Adapterda getId2 de patlıyor oraya güncelleme yapmadan bunu açma
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
        for (Beacon beacon : beaconList) {
            for (Identifier identifier : beacon.getIdentifiers()) {
                if (identifier.toString().contains(query)) {
                    temp.add(beacon);
                }
            }
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
        Bundle bundle = new Bundle();
        bundle.putParcelable(TAG, beaconList.get(position));
        Toast.makeText(getActivity(),"Clicked to beacon with uuid: " + beaconList.get(position).getId1(),Toast.LENGTH_LONG).show();
        openActionDialog(beaconList.get(position));
        //Intent intent = new Intent(getActivity(), NotificationActivity.class);
        //startActivity(intent);
    }

    public void openActionDialog(Beacon beacon) { // TODO: Beacon bilgisi ekle. ?? Kullanıcının ise farklı text göster.
        Dialog beacon_dialog;
        TextView tvNotification;
        TextView tvWebUrl;
        TextView tvLocation;

        beacon_dialog = new Dialog(Objects.requireNonNull(getActivity()));
        beacon_dialog.setTitle("Add action");
        beacon_dialog.setContentView(R.layout.dialog_beacon_action);

        tvNotification = beacon_dialog.findViewById(R.id.tv_notification);
        tvNotification.setOnClickListener(v -> {
            scanControl();
            Toast.makeText(getActivity(),"Clicked to notification.", Toast.LENGTH_SHORT).show();
        });
        tvWebUrl = beacon_dialog.findViewById(R.id.tv_web);
        tvWebUrl.setOnClickListener(v -> {
            scanControl();
            Toast.makeText(getActivity(),"Clicked to web url.", Toast.LENGTH_SHORT).show();
        });
        tvLocation = beacon_dialog.findViewById(R.id.tv_location);
        tvLocation.setOnClickListener(v -> {
            scanControl();
            Toast.makeText(getActivity(),"Clicked to location.", Toast.LENGTH_SHORT).show();
        });

        beacon_dialog.show();
    }

}
