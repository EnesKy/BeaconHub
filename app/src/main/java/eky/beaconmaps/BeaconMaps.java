package eky.beaconmaps;

import android.app.Application;

import com.estimote.coresdk.common.config.EstimoteSDK;

import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.powersave.BackgroundPowerSaver;
import org.altbeacon.beacon.startup.RegionBootstrap;

import eky.beaconmaps.beacon.estimote.BeaconID;
import eky.beaconmaps.beacon.estimote.BeaconNotificationsManager;

/**
 * Created by Enes Kamil YILMAZ on 28.03.2019.
 */

public class BeaconMaps extends Application {

    private static final String TAG = "BeaconReferenceApp";
    private RegionBootstrap regionBootstrap;
    private BackgroundPowerSaver backgroundPowerSaver;
    private boolean haveDetectedBeaconsSinceBoot = false;
    BeaconManager beaconManager;
    private boolean beaconNotificationsEnabled = false;

    public void onCreate() {
        super.onCreate();

        EstimoteSDK.initialize(getApplicationContext(), "beaconmap-7na"
                , "406b37d7508496b9349808f7634c2b58");

        beaconManager = org.altbeacon.beacon.BeaconManager.getInstanceForApplication(this);

    }

    public void enableBeaconNotifications() {
        if (beaconNotificationsEnabled) { return; }

        BeaconNotificationsManager beaconNotificationsManager = new BeaconNotificationsManager(this);
        beaconNotificationsManager.addNotification(
                new BeaconID("E263C169-EB5D-76DA-F938-1BBA59293189", 81, 81),
                "Gray - Hello, world.",
                "Gray - Goodbye, world.");
        beaconNotificationsManager.addNotification(
                new BeaconID("B9407F30-F5F8-466E-AFF9-25556B57FE6D", 100, 58168),
                "Lemon - Hello, world.",
                "Lemon - Goodbye, world.");
        beaconNotificationsManager.addNotification(
                new BeaconID("B9407F30-F5F8-466E-AFF9-25556B57FE6D", 100, 21066),
                "Purple - Hello, world.",
                "Purple - Goodbye, world.");
        beaconNotificationsManager.addNotification(
                new BeaconID("B9407F30-F5F8-466E-AFF9-25556B57FE6D", 100, 19782),
                "Pink - Hello, world.",
                "Pink - Goodbye, world.");
        beaconNotificationsManager.startMonitoring();

        beaconNotificationsEnabled = true;
    }

    public boolean isBeaconNotificationsEnabled() {
        return beaconNotificationsEnabled;
    }

}
