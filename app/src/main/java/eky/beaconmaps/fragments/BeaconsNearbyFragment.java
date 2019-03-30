package eky.beaconmaps.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
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

import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import eky.beaconmaps.R;
import eky.beaconmaps.adapter.BeaconAdapter;

public class BeaconsNearbyFragment extends Fragment implements RangeNotifier, BeaconConsumer,
                                            SearchView.OnQueryTextListener, BeaconAdapter.ItemClickListener {

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_beacons_nearby, container, false);

        placeholder = rootView.findViewById(R.id.tv_placeholder);

        progressBarLoading = rootView.findViewById(R.id.pb_loading);
        progressBarLoading.setVisibility(View.VISIBLE);

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

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void didRangeBeaconsInRegion(Collection<Beacon> beacons, Region region) {

        beaconList.clear();
        beaconList.addAll(beacons);

        if (beaconList.size() != 0) {

            placeholder.setVisibility(View.GONE);

            //Sorting the beacons by their distance to phone.
            Comparator<Beacon> beaconDistanceComparator = Comparator.comparing(Beacon::getDistance);
            Collections.sort(beaconList, beaconDistanceComparator);

            if (adapter == null) {
                adapter = new BeaconAdapter(beaconList, this);
                recyclerView.setAdapter(adapter);
            } else {
                adapter.notifyDataSetChanged();
            }
        } else {
            placeholder.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onBeaconServiceConnect() {
        try {
            Region all = new Region("rangingId", null, null, null);
            beaconManager.startRangingBeaconsInRegion(all);
            beaconManager.addRangeNotifier(this);
        } catch (RemoteException e) { }
    }

    @Override
    public Context getApplicationContext() {
        return null;
    }

    @Override
    public void unbindService(ServiceConnection serviceConnection) {

    }

    @Override
    public boolean bindService(Intent intent, ServiceConnection serviceConnection, int i) {
        return false;
    }

    private void setBeaconsLayout() {

        beaconManager.getBeaconParsers().clear();
        beaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout("m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24")); //iBeacon
        //beaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout("m:0-1=5900,i:2-2,i:3-4,p:5-5"));
        //beaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout(BeaconParser.ALTBEACON_LAYOUT));
        //beaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout(BeaconParser.EDDYSTONE_UID_LAYOUT));
        //beaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout(BeaconParser.EDDYSTONE_TLM_LAYOUT));
        //beaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout(BeaconParser.EDDYSTONE_URL_LAYOUT));
        //beaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout(BeaconParser.URI_BEACON_LAYOUT));
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
    }
}
