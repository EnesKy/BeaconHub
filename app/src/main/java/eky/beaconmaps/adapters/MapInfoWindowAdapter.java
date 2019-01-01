package eky.beaconmaps.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

import eky.beaconmaps.R;
import eky.beaconmaps.datamodel.InfoWindowData;

public class MapInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {

    private Context context;

    public MapInfoWindowAdapter(Context ctx){
        context = ctx;
    }

    @Override
    public View getInfoWindow(Marker marker) {
        return null;
    }

    @Override
    public View getInfoContents(Marker marker) {
        //TODO: burada iş var :/
        View view = ((Activity)context).getLayoutInflater().inflate(R.layout.map_info_windows, null);

        TextView name = view.findViewById(R.id.tv_name);
        TextView id = view.findViewById(R.id.tv_id);
        ImageView img = view.findViewById(R.id.iv_beacon);

        InfoWindowData infoWindowData = (InfoWindowData) marker.getTag();

        //int imageId = context.getResources().getIdentifier(infoWindowData.getImage().toLowerCase(), "drawable", context.getPackageName());
        //img.setImageResource(imageId);

        img.setImageDrawable(context.getDrawable(R.drawable.ic_icons8_plus));
        name.setText(infoWindowData.getName());
        id.setText(infoWindowData.getId());

        return view;
    }
}
