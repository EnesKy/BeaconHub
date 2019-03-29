package eky.beaconmaps.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.RemoteException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import eky.beaconmaps.R;
import eky.beaconmaps.adapter.BeaconAdapter;
import eky.beaconmaps.recyclerview.DividerItemDecoration;

public class BeaconsNearbyFragment extends Fragment implements RangeNotifier, BeaconConsumer {

    public static final String TAG = "BeaconsNearbyFragment";
    private BeaconManager beaconManager;
    private List<Beacon> beaconList;

    private RecyclerView recyclerView;
    public RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;

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

        recyclerView = rootView.findViewById(R.id.rv_beacons);
        layoutManager = new LinearLayoutManager(inflater.getContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(new DividerItemDecoration(inflater.getContext(),
                                           DividerItemDecoration.VERTICAL));

        return rootView;
    }

    @Override
    public void didRangeBeaconsInRegion(Collection<Beacon> beacons, Region region) {

        beaconList.clear();
        beaconList.addAll(beacons);

        if (beacons.size() != 0) {
            if (adapter == null) {
                adapter = new BeaconAdapter(beaconList);
                recyclerView.setAdapter(adapter);
            } else {
                adapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    public void onBeaconServiceConnect() {
        try {
            // id1 ve id2 null olduğu sürece her beaconı görür.
            beaconManager.startRangingBeaconsInRegion(
                    new Region("myRangingUniqueId", null, null, null));
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

}
