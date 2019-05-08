package eky.beaconmaps.adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.card.MaterialCardView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import eky.beaconmaps.R;
import eky.beaconmaps.fragments.ProfileFragment;
import eky.beaconmaps.model.BeaconData;

/**
 * Created by Enes Kamil YILMAZ on 30.04.2019.
 */

public class BeaconAdapter extends RecyclerView.Adapter<BeaconAdapter.ViewHolder> implements Filterable {

    private List<BeaconData> beaconList;
    private List<BeaconData> beaconListFull;
    private Boolean isNearby;
    private ItemClickListener itemClickListener;

    @SuppressLint("SimpleDateFormat")
    private SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

    public BeaconAdapter(List<BeaconData> beaconList, boolean isNearby, ItemClickListener itemClickListener) {
        this.beaconList = beaconList;
        if (beaconList != null)
            beaconListFull = new ArrayList<>(beaconList);
        this.isNearby = isNearby;
        this.itemClickListener = itemClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_ibeacon, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        BeaconData beacon = beaconList.get(position);

        if (beacon.getWebUrl() != null) {
            holder.tvUrl.setText("Website : " + beacon.getWebUrl());
        } else {
            holder.llUrl.setVisibility(View.GONE);
        }

        if (!isNearby) {

            if (beacon.getUuid() != null && !beacon.getUuid().isEmpty()) {
                holder.tvUuid.setText("UUID : " + beacon.getUuid());
                holder.tvMajor.setText("Major : " + beacon.getMajor());
                holder.tvMinor.setText("Minor : " + beacon.getMinor());
            }  else if (beacon.getBeacon() != null) {
                holder.tvUuid.setText("UUID : " + beacon.getBeacon().getId1());
                holder.tvMajor.setText("Major : " + beacon.getBeacon().getId2());
                holder.tvMinor.setText("Minor : " + beacon.getBeacon().getId3());
            }

            if (beacon.isBlocked())
                holder.ivBlocked.setVisibility(View.VISIBLE);
            else
                holder.ivBlocked.setVisibility(View.GONE);

            holder.llDistance.setVisibility(View.GONE);
            holder.llTx.setVisibility(View.GONE);
            holder.llRssi.setVisibility(View.GONE);
            holder.tvLastSeen.setVisibility(View.GONE);
        } else {

            holder.tvUuid.setText("UUID : " + beacon.getBeacon().getId1());
            holder.tvMajor.setText("Major : " + beacon.getBeacon().getId2());
            holder.tvMinor.setText("Minor : " + beacon.getBeacon().getId3());
            holder.tvDistance.setText(Double.toString(beacon.getBeacon().getDistance()).substring(0,5));
            holder.tvRssi.setText("Rssi : " + beacon.getBeacon().getRssi());
            holder.tvTx.setText("Rx : " + beacon.getBeacon().getTxPower());
            holder.tvLastSeen.setText(formatter.format(new Date()));


            //TODO: Registered beacon bilgilerini getir. Checkle kayıtlı mı değil mi???

            if (ProfileFragment.myBeaconsList != null && ProfileFragment.myBeaconsList.contains(beacon))
                holder.ivBelongstoUser.setVisibility(View.VISIBLE);
        }

    }

    @Override
    public int getItemCount() {
        if (beaconList != null)
            return beaconList.size();
        else
            return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        //View Components;
        private MaterialCardView item;
        private TextView tvUuid, tvMajor, tvMinor, tvUrl, tvLastSeen, tvDistance, tvRssi, tvTx;
        private ImageView ivBelongstoUser, ivBlocked;
        private LinearLayout llDistance, llRssi, llTx, llUrl;

        public ViewHolder(View itemView) {
            super(itemView);
            itemView.setClickable(false);

            item = itemView.findViewById(R.id.ibeacon_item);
            tvUuid = itemView.findViewById(R.id.proximity_uuid);
            tvMajor = itemView.findViewById(R.id.major);
            tvMinor = itemView.findViewById(R.id.minor);
            tvLastSeen = itemView.findViewById(R.id.last_seen);
            tvDistance = itemView.findViewById(R.id.distance);
            tvRssi = itemView.findViewById(R.id.rssi);
            tvTx = itemView.findViewById(R.id.tx);
            tvUrl = itemView.findViewById(R.id.tv_url);
            ivBelongstoUser = itemView.findViewById(R.id.iv_belongs_to_user);
            ivBlocked = itemView.findViewById(R.id.iv_blocked_icon);
            llDistance = itemView.findViewById(R.id.ll_distance);
            llRssi = itemView.findViewById(R.id.rssi_container);
            llTx = itemView.findViewById(R.id.tx_container);
            llUrl = itemView.findViewById(R.id.url_container);

            item.setOnClickListener(v -> itemClickListener.onItemClick((getAdapterPosition()), itemView));
        }
    }

    public interface ItemClickListener {
        void onItemClick(int position, View view);
    }

    @Override
    public Filter getFilter() {
        return beaconFilter;
    }

    private Filter beaconFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<BeaconData> filteredList = new ArrayList<>();

            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(beaconListFull);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();

                for (BeaconData item : beaconListFull) {
                    if (item.getBeacon().getId1().toString().toLowerCase().contains(filterPattern)
                            || item.getBeacon().getId2().toString().toLowerCase().contains(filterPattern)
                            || item.getBeacon().getId3().toString().toLowerCase().contains(filterPattern)) {
                        filteredList.add(item);
                    }
                }
            }

            FilterResults results = new FilterResults();
            results.values = filteredList;

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            beaconList.clear();
            beaconList.addAll((List) results.values);
            notifyDataSetChanged();
        }
    };

}
