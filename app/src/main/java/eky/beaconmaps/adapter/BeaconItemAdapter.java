package eky.beaconmaps.adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.card.MaterialCardView;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.utils.UrlBeaconUrlCompressor;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import eky.beaconmaps.R;
import eky.beaconmaps.fragments.ProfileFragment;

/**
 * Created by Enes Kamil YILMAZ on 28.03.2019.
 */

public class BeaconItemAdapter extends RecyclerView.Adapter<BeaconItemAdapter.BaseViewHolder> {

    private List<Beacon> beaconList;
    private ItemClickListener itemClickListener;
    private Boolean isNearby, isSettings;
    private static final int iBeacon = 1;
    private static final int eddystoneUrl = 2;

    @SuppressLint("SimpleDateFormat")
    private SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
    private DecimalFormat numberFormat = new DecimalFormat("#.00");

    //View Components;
    private MaterialCardView item;
    private TextView tvUuid;
    private TextView tvMajor;
    private TextView tvMinor;
    private TextView tvDistance;
    private LinearLayout llDistance;
    private TextView tvRssi;
    private TextView tvTx;
    private TextView tvLastSeen;
    private ImageView ivBelongstoUser;
    private TextView tvUrl;

    public BeaconItemAdapter(List<Beacon> beaconList, boolean isNearby, boolean isSettings,
                             ItemClickListener itemClickListener) {
        this.beaconList = beaconList;
        this.itemClickListener = itemClickListener;
        this.isNearby = isNearby;
        this.isSettings = isSettings;
    }

    @Override
    public int getItemViewType(int position) {

        Beacon beacon = beaconList.get(position);

        if (beacon.getServiceUuid() == 0xfeaa && beacon.getBeaconTypeCode() == 0x10) {
            return eddystoneUrl;
        } else {
            return iBeacon;
        }
    }

    @NonNull
    @Override
    public BaseViewHolder<Beacon> onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = null;

        switch (viewType) {
            case iBeacon:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_ibeacon, parent, false);
                return new iBeaconViewHolder(view);
            case eddystoneUrl:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_eddystone_url, parent, false);
                return new EddystoneUrlViewHolder(view);
            default:
                throw new IllegalArgumentException("Invalid view type");
        }
    }

    @Override
    public void onBindViewHolder(@NonNull BaseViewHolder holder, int position) {
        Beacon beacon = beaconList.get(position);
        holder.bind(beacon);
    }

    public static abstract class BaseViewHolder<T> extends RecyclerView.ViewHolder {
        private BaseViewHolder(View itemView) {
            super(itemView);
        }

        public abstract void bind(T type);
    }

    public class iBeaconViewHolder extends BaseViewHolder<Beacon> {

        private iBeaconViewHolder(View itemView) {
            super(itemView);

            item = itemView.findViewById(R.id.ibeacon_item);
            tvUuid = itemView.findViewById(R.id.proximity_uuid);
            tvMajor = itemView.findViewById(R.id.major);
            tvMinor = itemView.findViewById(R.id.minor);
            tvDistance = itemView.findViewById(R.id.distance);
            llDistance = itemView.findViewById(R.id.ll_distance);
            tvRssi = itemView.findViewById(R.id.rssi);
            tvTx = itemView.findViewById(R.id.tx);
            tvLastSeen = itemView.findViewById(R.id.last_seen);
            ivBelongstoUser = itemView.findViewById(R.id.iv_belongs_to_user);

            item.setOnClickListener(v -> itemClickListener.onItemClick((getAdapterPosition()), false, itemView));
        }

        @SuppressLint("SetTextI18n")
        @Override
        public void bind(Beacon beacon) {

            if (!isNearby) {
                llDistance.setVisibility(View.GONE);
                tvLastSeen.setVisibility(View.GONE);
            }
            if (isNearby || isSettings)
                if (ProfileFragment.myBeaconsList != null && ProfileFragment.myBeaconsList.contains(beacon))
                    ivBelongstoUser.setVisibility(View.VISIBLE);

            tvUuid.setText("Uuid : " + beacon.getId1());
            tvMajor.setText("Major : " + beacon.getId2());
            tvMinor.setText("Minor : " + beacon.getId3());
            tvDistance.setText(Double.toString(beacon.getDistance()).substring(0,5));
            tvRssi.setText("Rssi : " + beacon.getRssi());
            tvTx.setText("Rx : " + beacon.getTxPower());
            tvLastSeen.setText(formatter.format(new Date()));
        }
    }

    public class EddystoneUrlViewHolder extends BaseViewHolder<Beacon> {

        private EddystoneUrlViewHolder(View itemView) {
            super(itemView);

            item = itemView.findViewById(R.id.item_eddystone);
            tvUrl = itemView.findViewById(R.id.tv_eddystone_url);
            tvDistance = itemView.findViewById(R.id.distance);
            llDistance = itemView.findViewById(R.id.ll_distance);
            tvRssi = itemView.findViewById(R.id.rssi);
            tvTx = itemView.findViewById(R.id.tx);
            tvLastSeen = itemView.findViewById(R.id.last_seen);
            ivBelongstoUser = itemView.findViewById(R.id.iv_belongs_to_user);

            item.setOnClickListener(v -> itemClickListener.onItemClick((getAdapterPosition()), true, itemView));
        }

        @SuppressLint("SetTextI18n")
        @Override
        public void bind(Beacon beacon) {

            if (!isNearby) {
                llDistance.setVisibility(View.GONE);
                tvLastSeen.setVisibility(View.GONE);
            }
            if (isNearby || isSettings)
                if (ProfileFragment.myBeaconsList != null && ProfileFragment.myBeaconsList.contains(beacon))
                    ivBelongstoUser.setVisibility(View.VISIBLE);


            String url = UrlBeaconUrlCompressor.uncompress(beacon.getId1().toByteArray());

            tvUrl.setText(url);
            tvDistance.setText(Double.toString(beacon.getDistance()).substring(0,5));
            tvRssi.setText("Rssi : " + beacon.getRssi());
            tvTx.setText("Rx : " + beacon.getTxPower());
            tvLastSeen.setText(formatter.format(new Date()));
        }
    }

    @Override
    public int getItemCount() {
        if (beaconList != null)
            return beaconList.size();
        else
            return 0;
    }

    public interface ItemClickListener {
        void onItemClick(int position, boolean isEddystone, View view);
    }

}
