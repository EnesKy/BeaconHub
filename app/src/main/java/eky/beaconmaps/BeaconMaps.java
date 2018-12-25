package eky.beaconmaps;

import android.app.Application;

import com.estimote.coresdk.common.config.EstimoteSDK;
import com.estimote.proximity_sdk.api.EstimoteCloudCredentials;

import eky.beaconmaps.notification.NotificationsManager;

public class BeaconMaps extends Application {

    private String appID = "";
    private String appToken = "";

    public EstimoteCloudCredentials cloudCredentials = new EstimoteCloudCredentials(appID, appToken);
    private NotificationsManager notificationsManager;

    @Override
    public void onCreate() {
        super.onCreate();

        EstimoteSDK.initialize(getApplicationContext(), appID, appToken);
        EstimoteSDK.enableDebugLogging(false);
    }

    public void enableBeaconNotifications(String helloTitle, String helledesc, String byeTitle, String byeDesc) {
        notificationsManager = new NotificationsManager(this, helloTitle, helledesc, byeTitle, byeDesc);
        notificationsManager.startMonitoring();
    }

}
