package eky.beaconmaps;

import android.app.Application;

import com.estimote.proximity_sdk.api.EstimoteCloudCredentials;
import eky.beaconmaps.estimote.NotificationsManager;

public class MyApplication extends Application {

    public EstimoteCloudCredentials cloudCredentials = new EstimoteCloudCredentials("AppID", "appToken");
    private NotificationsManager notificationsManager;

    public void enableBeaconNotifications() {
        notificationsManager = new NotificationsManager(this);
        notificationsManager.startMonitoring();
    }
}
