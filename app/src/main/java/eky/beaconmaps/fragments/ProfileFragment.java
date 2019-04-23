package eky.beaconmaps.fragments;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.utils.UrlBeaconUrlCompressor;

import java.util.List;
import java.util.Objects;

import androidx.browser.customtabs.CustomTabsIntent;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import eky.beaconmaps.R;
import eky.beaconmaps.adapter.BeaconItemAdapter;
import eky.beaconmaps.utils.PreferencesUtil;

public class ProfileFragment extends Fragment implements BeaconItemAdapter.ItemClickListener {

    private TextView placeholder, title;
    private FirebaseUser user;
    private PreferencesUtil preferencesUtil;
    public static List<Beacon> myBeaconsList;

    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        preferencesUtil = new PreferencesUtil(Objects.requireNonNull(getActivity()));
        myBeaconsList = preferencesUtil.getMyBeaconsList();
        user = FirebaseAuth.getInstance().getCurrentUser();
    }

    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_profile, container, false);

        placeholder = rootView.findViewById(R.id.tv_placeholder);

        recyclerView = rootView.findViewById(R.id.rv_my_beacons);
        layoutManager = new LinearLayoutManager(inflater.getContext());
        recyclerView.setLayoutManager(layoutManager);
        adapter = new BeaconItemAdapter(myBeaconsList,false, false, this);
        recyclerView.setAdapter(adapter);

        title = rootView.findViewById(R.id.tv_title);

        if (user != null)
            title.setText(user.getDisplayName() + "'s Beacons");

        return rootView;
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);

        if (myBeaconsList.size() != preferencesUtil.getMyBeaconsList().size()) {
            myBeaconsList.clear();
            myBeaconsList = preferencesUtil.getMyBeaconsList();
            if (adapter == null) {
                adapter = new BeaconItemAdapter(myBeaconsList, false, false,this);
                recyclerView.setAdapter(adapter);
            } else {
                adapter.notifyDataSetChanged();
            }
        }

        if (myBeaconsList.size() == 0)
            placeholder.setVisibility(View.VISIBLE);
        else
            placeholder.setVisibility(View.GONE);
    }

    public void openActionDialog(Beacon beacon, boolean isEddystone) {
        Dialog beacon_dialog;
        TextView tvSeeNotification, tvAddNotification, tvUpdateNotification;
        TextView tvVisitWebsite, tvAddWebsite, tvUpdateWebsite;
        TextView tvSeeLocation, tvAddLocation, tvUpdateLocation;
        TextView tvAddBlocklist;

        beacon_dialog = new Dialog(Objects.requireNonNull(getActivity()));
        beacon_dialog.setContentView(R.layout.dialog_users_beacons);

        tvAddNotification = beacon_dialog.findViewById(R.id.tv_add_notification);
        tvAddNotification.setOnClickListener(v -> {

            //eğer notificationı varsa visibility==gone

        });

        tvAddWebsite = beacon_dialog.findViewById(R.id.tv_add_website);
        tvAddWebsite.setOnClickListener(v -> {

            //eğer websitesi varsa visibility==gone

        });

        tvAddLocation = beacon_dialog.findViewById(R.id.tv_add_location);
        tvAddLocation.setOnClickListener(v -> {

            //eğer locationı varsa visibility==gone

        });

        tvAddBlocklist = beacon_dialog.findViewById(R.id.tv_add_blocklist);
        tvAddBlocklist.setOnClickListener(v -> {

            //eğer blocked ise visibility==gone

            preferencesUtil.getBlockedBeaconsList().add(beacon);

            beacon_dialog.dismiss();
        });

        tvVisitWebsite = beacon_dialog.findViewById(R.id.tv_visit_website);
        tvVisitWebsite.setOnClickListener(v -> {

            if (isEddystone) {
                String url = UrlBeaconUrlCompressor.uncompress(beacon.getId1().toByteArray());
                CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
                CustomTabsIntent customTabsIntent = builder.build();
                customTabsIntent.launchUrl(getActivity(), Uri.parse(url));
            }

        });

        tvSeeLocation = beacon_dialog.findViewById(R.id.tv_go_location);
        tvSeeLocation.setOnClickListener(v -> {
            //eğer locationı yoksa visibility==gone
        });

        tvUpdateLocation = beacon_dialog.findViewById(R.id.tv_update_location);
        tvUpdateLocation.setOnClickListener(v -> {

            //eğer locationı yoksa visibility==gone

        });

        tvUpdateNotification = beacon_dialog.findViewById(R.id.tv_update_notification);
        tvUpdateNotification.setOnClickListener(v -> {

            //eğer notificationı yoksa visibility==gone

        });

        tvUpdateWebsite = beacon_dialog.findViewById(R.id.tv_update_website);
        tvUpdateWebsite.setOnClickListener(v -> {

            //eğer websitesi yoksa visibility==gone

        });

        beacon_dialog.show();
    }

    @Override
    public void onItemClick(int position, boolean isEddystone, View view) {
        openActionDialog(myBeaconsList.get(position), isEddystone);
    }

}
