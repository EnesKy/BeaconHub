package eky.beaconmaps.notification;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;

import com.estimote.proximity_sdk.api.ProximityObserver;
import com.estimote.proximity_sdk.api.ProximityObserverBuilder;
import com.estimote.proximity_sdk.api.ProximityZone;
import com.estimote.proximity_sdk.api.ProximityZoneBuilder;
import com.estimote.proximity_sdk.api.ProximityZoneContext;

import androidx.core.app.NotificationCompat;
import eky.beaconmaps.BeaconMaps;
import eky.beaconmaps.activity.MapsActivity;
import eky.beaconmaps.R;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;

import static com.estimote.mgmtsdk.repackaged.dfu_v0_6_1.no.nordicsemi.android.dfu.DfuBaseService.NOTIFICATION_ID;

public class NotificationsManager  {

    private Context context;
    private NotificationManager notificationManager;
    private Notification helloNotification;
    private Notification goodbyeNotification;
    private NotificationCompat.Builder builder;
    private int notificationId = 1;

    long vibrateTimeArr[] = {1000, 2000, 1000, 2000, 1000};

    public NotificationsManager(Context context, String helloTitle, String helledesc, String byeTitle, String byeDesc) {
        this.context = context;
        this.notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        this.helloNotification = buildNotification(helloTitle, helledesc);
        this.goodbyeNotification = buildNotification(byeTitle, byeDesc);
        notificationManager.notify(1,helloNotification);
        notificationManager.notify(1,goodbyeNotification);
    }



    private Notification buildNotification(String title, String text) {

        builder = new NotificationCompat.Builder(context, "content_channel");

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel contentChannel = new NotificationChannel(
                    "content_channel", "Things near you", NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(contentChannel);
        }

        builder.setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setSmallIcon(R.mipmap.beacon_maps_launcher_icon)
                //.setLargeIcon(BitmapFactory.decodeResource( getResources(), R.mipmap.beacon_maps_launcher_icon)) //todo: getResources() ?????
                .setContentTitle(title)
                .setContentText(text)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(text))
                .setVibrate(vibrateTimeArr)
                .setLights(Color.RED, 1000, 1000)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                //.setDefaults(NotificationCompat.DEFAULT_SOUND | NotificationCompat.DEFAULT_VIBRATE)
                //.setContentIntent(PendingIntent.getActivity(context, 0, new Intent(context, MapsActivity.class), PendingIntent.FLAG_UPDATE_CURRENT))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .addAction(R.drawable.common_google_signin_btn_icon_dark,"Show Beacon on the map", null); //Buraya mapte beaconın lokasyonuna götürecek intent ekle

        Intent intent = new Intent(context, MapsActivity.class);
        //intent.putExtra("BeaconLocation",Location);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
        builder.addAction(android.R.drawable.ic_menu_view, "Show on Map", pendingIntent); //TODO: add map icon and location intent

        Intent buttonIntent = new Intent(context, NotificationReceiver.class);
        buttonIntent.putExtra("notificationId", NOTIFICATION_ID);
        PendingIntent dismissIntent = PendingIntent.getBroadcast(context, 0, buttonIntent, 0);
        builder.addAction(android.R.drawable.ic_menu_view, "Dismiss", dismissIntent);

        return builder.build();
    }

    public void startMonitoring() {
        ProximityObserver proximityObserver =
                new ProximityObserverBuilder(context, ((BeaconMaps) context).cloudCredentials)
                        .onError(new Function1<Throwable, Unit>() {
                            @Override
                            public Unit invoke(Throwable throwable) {
                                Log.e("app", "proximity observer error: " + throwable);
                                return null;
                            }
                        })
                        .withBalancedPowerMode()
                        .build();

        ProximityZone zone = new ProximityZoneBuilder()
                .forTag("TAG")
                .inCustomRange(1.0)
                .onEnter(new Function1<ProximityZoneContext, Unit>() {
                    @Override
                    public Unit invoke(ProximityZoneContext proximityContext) {
                        notificationManager.notify(notificationId, helloNotification);
                        return null;
                    }
                })
                .onExit(new Function1<ProximityZoneContext, Unit>() {
                    @Override
                    public Unit invoke(ProximityZoneContext proximityContext) {
                        notificationManager.notify(notificationId, goodbyeNotification);
                        return null;
                    }
                })
                .build();
        proximityObserver.startObserving(zone);
    }

}
