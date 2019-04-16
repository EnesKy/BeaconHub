package eky.beaconmaps.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.material.card.MaterialCardView;

import org.altbeacon.beacon.Beacon;

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

public class BeaconAdapter extends RecyclerView.Adapter<BeaconAdapter.ViewHolder> {

    private List<Beacon> beaconList;
    private ItemClickListener itemClickListener;
    DecimalFormat numberFormat = new DecimalFormat("#.00");

    public BeaconAdapter(List<Beacon> beaconList, ItemClickListener itemClickListener) {
        this.beaconList = beaconList;
        this.itemClickListener = itemClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.beacon_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Beacon beacon = beaconList.get(position);

        holder.tvUuid.setText("Uuid : " + beacon.getId1());
        holder.tvMajor.setText("Major : " + beacon.getId2());
        holder.tvMinor.setText("Minor : " + beacon.getId3());
        holder.tvDistance.setText(Double.toString(beacon.getDistance()).substring(0,5));
        holder.tvRssi.setText("Rssi : " + beacon.getRssi());
        holder.tvTx.setText("Rx : " + beacon.getTxPower());

        Date date = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        holder.tvLastSeen.setText(formatter.format(date));

    }

    @Override
    public int getItemCount() {
        return beaconList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        MaterialCardView item;
        TextView tvUuid;
        TextView tvMajor;
        TextView tvMinor;
        TextView tvDistance;
        TextView tvRssi;
        TextView tvTx;
        TextView tvLastSeen;

        public ViewHolder(View itemView) {
            super(itemView);
            itemView.setClickable(false);

            item = itemView.findViewById(R.id.beacon_item);
            tvUuid = itemView.findViewById(R.id.proximity_uuid);
            tvMajor = itemView.findViewById(R.id.major);
            tvMinor = itemView.findViewById(R.id.minor);
            tvDistance = itemView.findViewById(R.id.distance);
            tvRssi = itemView.findViewById(R.id.rssi);
            tvTx = itemView.findViewById(R.id.tx);
            tvLastSeen = itemView.findViewById(R.id.last_seen);

            item.setOnClickListener(v -> itemClickListener.onItemClick((getAdapterPosition()), itemView));
        }
    }


    public interface ItemClickListener {
        void onItemClick(int position, View view);
    }

}
