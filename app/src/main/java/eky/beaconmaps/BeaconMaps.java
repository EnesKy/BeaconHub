package eky.beaconmaps;

import android.app.Application;

import com.estimote.proximity_sdk.api.EstimoteCloudCredentials;
import com.estimote.sdk.EstimoteSDK;

import eky.beaconmaps.activity.ConfigureBeaconActivity;
import eky.beaconmaps.notification.NotificationsManager;

public class BeaconMaps extends Application {

    private String appID = "beaconmap-7na";
    private String appToken = "406b37d7508496b9349808f7634c2b58";

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
        notificationsManager.startMonitoring(ConfigureBeaconActivity.getTag());
    }

}
