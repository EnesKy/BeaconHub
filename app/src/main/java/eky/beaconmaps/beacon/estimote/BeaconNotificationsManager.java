package eky.beaconmaps.beacon.estimote;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import com.estimote.coresdk.observation.region.beacon.BeaconRegion;
import com.estimote.coresdk.recognition.packets.Beacon;
import com.estimote.coresdk.service.BeaconManager;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import androidx.core.app.NotificationCompat;
import eky.beaconmaps.R;
import eky.beaconmaps.activities.MainActivity;
import eky.beaconmaps.model.NotificationData;

import static androidx.core.content.ContextCompat.getSystemService;

public class BeaconNotificationsManager implements BeaconManager.BeaconMonitoringListener, BeaconManager.BeaconRangingListener {

    private static final String TAG = "BeaconNotifications";

    private Context context;
    private BeaconManager beaconManager;
    private Boolean isManagerConnected = false;
    private String CHANNEL_ID = "beaconMapsID";

    private List<BeaconRegion> regionsToMonitor = new ArrayList<>();
    private HashMap<String, NotificationData> notificationDatas = new HashMap<>();
    private HashMap<String, BeaconID> beaconIDs = new HashMap<>();

    private int notificationID = 0;

    public BeaconNotificationsManager(Context context) {

        this.context = context;
        beaconManager = new BeaconManager(context);
        beaconManager.setMonitoringListener(this);

        createNotificationChannel();
    }

    @Override
    public void onEnteredRegion(BeaconRegion region, List<Beacon> list) {
        Log.d(TAG, "onEnteredRegion: " + region.getIdentifier());
        NotificationData notificationData = notificationDatas.get(region.getIdentifier());
        BeaconID beaconID = beaconIDs.get(region.getIdentifier());

        if (notificationData != null && beaconID.toBeaconRegion().equals(region)) {
            showNotification(notificationData.getEnterTitle(), notificationData.getEnterDesc(), beaconID);
        }
    }

    @Override
    public void onExitedRegion(BeaconRegion region) {
        Log.d(TAG, "onExitedRegion: " + region.getIdentifier());
        NotificationData notificationData = notificationDatas.get(region.getIdentifier());
        BeaconID beaconID = beaconIDs.get(region.getIdentifier());

        if (notificationData != null && beaconID.toBeaconRegion().equals(region)) {
            showNotification(notificationData.getExitTitle(), notificationData.getExitDesc(), beaconID);
        }
    }

    @Override
    public void onBeaconsDiscovered(BeaconRegion beaconRegion, List<Beacon> list) {
        Log.d(TAG, "onBeaconsDiscovered: " + list.size());
        for (Beacon beacon : list) {
            Log.d(TAG, "onBeaconsDiscovered: " + beacon.toString());
        }
    }

    public void startMonitoring() {
        beaconManager.connect(() -> {
            isManagerConnected = true;
            for (BeaconRegion region : regionsToMonitor) {
                beaconManager.startMonitoring(region);
                beaconManager.setForegroundScanPeriod(5000, 10000); // Scan during 5s every 10s
                beaconManager.setBackgroundScanPeriod(4000, 20000); // Scan during 4s every 20s
            }
        });
    }

    public void addNotification(BeaconID beaconID, NotificationData notificationData) {
        //TODO: Add as BeaconData from Database. Don't use BeaconID.
        BeaconRegion region = beaconID.toBeaconRegion();
        notificationDatas.put(region.getIdentifier(), notificationData);
        beaconIDs.put(region.getIdentifier(), beaconID);
        regionsToMonitor.add(region);
    }

    private void showNotification(String title, String message, BeaconID beaconID) {
        //TODO: LatLng ekle. Farklı notificationlarda latlng nasıl ayırt edeceksin???
        // companyName i subtitle olarak ekleyebilirsin.
        Intent resultIntent = new Intent(context, MainActivity.class);
        resultIntent.putExtra("KEY_LOC", new LatLng(41.0463356, 28.9432943));
        PendingIntent resultPendingIntent = PendingIntent.getActivity(
                context, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification notification;

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            notification = new Notification.Builder(context, CHANNEL_ID)
                    .setSmallIcon(R.mipmap.beacon_maps_no_background_icon)
                    .setContentTitle(title)
                    //.setContentText(message)
                    .setStyle(new Notification.BigTextStyle().bigText(message))
                    .setContentIntent(resultPendingIntent)
                    .setSubText("Company Name")
                    .setVisibility(Notification.VISIBILITY_PUBLIC)
                    .setAutoCancel(true)
                    .build();
        } else {
            notification = new NotificationCompat.Builder(context, CHANNEL_ID)
                    .setSmallIcon(R.mipmap.beacon_maps_no_background_icon)
                    .setContentTitle(title)
                    //.setContentText(message)
                    .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                    .setSubText("Company Name")
                    .setDefaults(NotificationCompat.DEFAULT_ALL)
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                    .setContentIntent(resultPendingIntent)
                    .setAutoCancel(true)
                    .build();
        }

        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(notificationID++, notification);

    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String CHANNEL_NAME = "BeaconMaps Notification Channel";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance);
            channel.enableVibration(true);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(context, NotificationManager.class);
            assert notificationManager != null;
            notificationManager.createNotificationChannel(channel);
        }

    }

}
