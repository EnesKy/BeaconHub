package eky.beaconmaps.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

/**
 * Created by Enes Kamil YILMAZ on 28.03.2019.
 */

public class BeaconItemAdapter extends RecyclerView.Adapter<BeaconItemAdapter.BaseViewHolder> {

    private List<Beacon> beaconList;
    private ItemClickListener itemClickListener;
    private static final int iBeacon = 1;
    private static final int eddystoneUrl = 2;

    DecimalFormat numberFormat = new DecimalFormat("#.00");

    public BeaconItemAdapter(List<Beacon> beaconList, ItemClickListener itemClickListener) {
        this.beaconList = beaconList;
        this.itemClickListener = itemClickListener;
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

        MaterialCardView item;
        TextView tvUuid;
        TextView tvMajor;
        TextView tvMinor;
        TextView tvDistance;
        TextView tvRssi;
        TextView tvTx;
        TextView tvLastSeen;

        Date date = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

        private iBeaconViewHolder(View itemView) {
            super(itemView);

            item = itemView.findViewById(R.id.ibeacon_item);
            tvUuid = itemView.findViewById(R.id.proximity_uuid);
            tvMajor = itemView.findViewById(R.id.major);
            tvMinor = itemView.findViewById(R.id.minor);
            tvDistance = itemView.findViewById(R.id.distance);
            tvRssi = itemView.findViewById(R.id.rssi);
            tvTx = itemView.findViewById(R.id.tx);
            tvLastSeen = itemView.findViewById(R.id.last_seen);

            item.setOnClickListener(v -> itemClickListener.onItemClick((getAdapterPosition()), itemView));
        }

        @Override
        public void bind(Beacon beacon) {
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

        MaterialCardView item;
        TextView tvUrl;
        TextView tvDistance;
        TextView tvRssi;
        TextView tvTx;
        TextView tvLastSeen;

        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

        private EddystoneUrlViewHolder(View itemView) {
            super(itemView);

            item = itemView.findViewById(R.id.item_eddystone);

            tvUrl = itemView.findViewById(R.id.tv_eddystone_url);
            tvDistance = itemView.findViewById(R.id.distance);
            tvRssi = itemView.findViewById(R.id.rssi);
            tvTx = itemView.findViewById(R.id.tx);
            tvLastSeen = itemView.findViewById(R.id.last_seen);

            item.setOnClickListener(v -> itemClickListener.onItemClick((getAdapterPosition()), itemView));
        }

        @Override
        public void bind(Beacon beacon) {

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
        return beaconList.size();
    }

    public interface ItemClickListener {
        void onItemClick(int position, View view);
    }

}
