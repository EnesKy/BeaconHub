package eky.beaconmaps.beacon.altbeacon;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;
import android.widget.EditText;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.Identifier;
import org.altbeacon.beacon.MonitorNotifier;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;
import org.altbeacon.beacon.utils.UrlBeaconUrlCompressor;

import java.text.DecimalFormat;
import java.util.Collection;

public class RangingActivity extends Activity implements BeaconConsumer, MonitorNotifier, RangeNotifier {

    protected static final String TAG = "RangingActivity";
    private BeaconManager beaconManager;

    private EditText rangingText;

    DecimalFormat numberFormat = new DecimalFormat("#.000");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        beaconManager = BeaconManager.getInstanceForApplication(this);
        setBeaconsLayout();
        beaconManager.bind(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
        beaconManager.unbind(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        beaconManager.bind(this);
    }

    @Override
    public void onBeaconServiceConnect() {
        try {
            // id1 ve id2 null olduğu sürece her beaconı görür.
            beaconManager.startRangingBeaconsInRegion(new Region("myRangingUniqueId", null, null, null));
            beaconManager.addRangeNotifier(this);
            Log.e(TAG, "onBeaonServiceConnect");
        } catch (RemoteException e) {
        }
    }

    public void showNotification(String title, String message) {
        Intent notifyIntent = new Intent(this, RangingActivity.class);
        notifyIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivities(this, 0,
                new Intent[]{notifyIntent}, PendingIntent.FLAG_UPDATE_CURRENT);
        Notification notification = new Notification.Builder(this)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle(title)
                .setContentText(message)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .build();
        notification.defaults |= Notification.DEFAULT_SOUND;
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(1, notification);
    }

    private void logToDisplay(final String line) {
        runOnUiThread(new Runnable() {
            public void run() {
                rangingText.append(line + "\n");
            }
        });
    }

    @Override
    public void didEnterRegion(Region region) {
        Log.e("didEnterRegion", TAG + " " + region.toString());
        showNotification("Beacon", "Entered Region");
    }

    @Override
    public void didExitRegion(Region region) {
        Log.e("didExitRegion", TAG + " " + region.toString());
        showNotification("Beacon", "Exited Region");
    }

    @Override
    public void didDetermineStateForRegion(int i, Region region) {

    }

    private void setBeaconsLayout() {
        beaconManager.getBeaconParsers().clear();
        beaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout("m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24")); //iBeacon
        beaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout("m:0-1=5900,i:2-2,i:3-4,p:5-5"));
        beaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout(BeaconParser.ALTBEACON_LAYOUT));
        beaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout(BeaconParser.EDDYSTONE_UID_LAYOUT));
        beaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout(BeaconParser.EDDYSTONE_TLM_LAYOUT));
        beaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout(BeaconParser.EDDYSTONE_URL_LAYOUT));
        beaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout(BeaconParser.URI_BEACON_LAYOUT));
    }

    @Override
    public void didRangeBeaconsInRegion(Collection<Beacon> beacons, Region region) {
        if (beacons.size() > 0) {
            Log.d(TAG, "didRangeBeaconsInRegion called with beacon count:  " + beacons.size());
            //Beacon firstBeacon = beacons.iterator().next();
            //logToDisplay("The first beacon " + firstBeacon.toString() + " is about " + firstBeacon.getDistance() + " meters away.");
            rangingText.setText("There is " + beacons.size() + " beacons in sight." + "\n"
                    + " - - - - - - - - - - - - - - - - - - - - - - - - " + "\n");
            for (Beacon beacon : beacons) {
                rangingText.append(beacon.getId1() + " - " + numberFormat.format(beacon.getDistance()) + " meters away" + "\n");


                if (beacon.getServiceUuid() == 0xfeaa && beacon.getBeaconTypeCode() == 0x00) {
                    // This is a Eddystone-UID frame
                    Identifier namespaceId = beacon.getId1();
                    Identifier instanceId = beacon.getId2();
                    Log.d(TAG, "I see a beacon transmitting namespace id: " + namespaceId +
                            " and instance id: " + instanceId +
                            " approximately " + beacon.getDistance() + " meters away.");

                    // Do we have telemetry data?
                    if (beacon.getExtraDataFields().size() > 0) {
                        long telemetryVersion = beacon.getExtraDataFields().get(0);
                        long batteryMilliVolts = beacon.getExtraDataFields().get(1);
                        long pduCount = beacon.getExtraDataFields().get(3);
                        long uptime = beacon.getExtraDataFields().get(4);

                        Log.d(TAG, "The above beacon is sending telemetry version " + telemetryVersion +
                                ", has been up for : " + uptime + " seconds" +
                                ", has a battery level of " + batteryMilliVolts + " mV" +
                                ", and has transmitted " + pduCount + " advertisements.");

                        if (beacon.getServiceUuid() == 0xfeaa && beacon.getBeaconTypeCode() == 0x10) {
                            // This is a Eddystone-URL frame
                            String url = UrlBeaconUrlCompressor.uncompress(beacon.getId1().toByteArray());
                            Log.d(TAG, "I see a beacon transmitting a url: " + url +
                                    " approximately " + beacon.getDistance() + " meters away.");
                        }

                    }
                } else {
                    Log.d(TAG, "didRangeBeaconsInRegion called but there is no beacons in sight.");
                }
            }
        }

    }
}
