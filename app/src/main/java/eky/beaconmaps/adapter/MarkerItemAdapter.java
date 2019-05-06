package eky.beaconmaps.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.google.android.material.card.MaterialCardView;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import eky.beaconmaps.R;
import eky.beaconmaps.model.MarkerData;

/**
 * Created by Enes Kamil YILMAZ on 6.05.2019.
 */

public class MarkerItemAdapter extends RecyclerView.Adapter<MarkerItemAdapter.ViewHolder> implements Filterable {

    private List<MarkerData> markerList;
    private List<MarkerData> markerListFull;
    private ItemClickListener itemClickListener;
    private DecimalFormat numberFormat = new DecimalFormat("#.000");

    public MarkerItemAdapter(List<MarkerData> markerList, ItemClickListener itemClickListener) {
        this.markerList = markerList;
        if (markerList != null)
            markerListFull = new ArrayList<>(markerList);
        this.itemClickListener = itemClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_marker, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        MarkerData marker = markerList.get(position);

        if (marker.getBeaconData().getCompanyName() != null)
            holder.tvCompanyName.setText(marker.getBeaconData().getCompanyName());

        if (marker.getBeaconData().getCompanyDesc() != null)
            holder.tvCompanyDesc.setText(marker.getBeaconData().getCompanyDesc());

        if (marker.getDistance() > 0)
            holder.tvDistance.setText(numberFormat.format(marker.getDistance()) + " m");

        if (marker.getBeaconData().getWebUrl() != null) {
            holder.tvCompanyWebsite.setText("Website : " + marker.getBeaconData().getWebUrl());
        } else {
            holder.viewWebsite.setVisibility(View.GONE);
            holder.tvCompanyWebsite.setVisibility(View.GONE);
        }

    }

    @Override
    public int getItemCount() {
        if (markerList != null)
            return markerList.size();
        else
            return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        //View Components;
        private MaterialCardView item;
        private TextView tvCompanyName, tvCompanyDesc, tvCompanyWebsite, tvDistance;
        private View viewWebsite;

        public ViewHolder(View itemView) {
            super(itemView);
            itemView.setClickable(false);

            item = itemView.findViewById(R.id.item_marker);
            tvCompanyName = itemView.findViewById(R.id.tv_company_name);
            tvCompanyDesc = itemView.findViewById(R.id.tv_company_description);
            tvCompanyWebsite = itemView.findViewById(R.id.tv_company_website);
            viewWebsite = itemView.findViewById(R.id.view_website);
            tvDistance = itemView.findViewById(R.id.tv_distance);

            item.setOnClickListener(v -> itemClickListener.onItemClick((getAdapterPosition()), itemView));
        }
    }

    public interface ItemClickListener {
        void onItemClick(int position, View view);
    }

    @Override
    public Filter getFilter() {
        return markerFilter;
    }

    private Filter markerFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<MarkerData> filteredList = new ArrayList<>();

            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(markerListFull);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();

                for (MarkerData item : markerListFull) {
                    if (item.getBeaconData().getCompanyName().toLowerCase().contains(filterPattern)) {
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
            markerList.clear();
            markerList.addAll((List) results.values);
            notifyDataSetChanged();
        }
    };

}

