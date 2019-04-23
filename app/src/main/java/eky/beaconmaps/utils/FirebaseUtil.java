package eky.beaconmaps.utils;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.altbeacon.beacon.Beacon;

import eky.beaconmaps.model.NotificationData;

public class FirebaseUtil {

    public static String userIdToken;

    public static void saveBeacon(){
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        DatabaseReference databaseBook = database.child("beacon");
        databaseBook.push().setValue("deneme");
    }

    public static void saveBeaconData(Beacon beacon){
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        DatabaseReference databaseBook = database.child("beaconData");
        database.child("beacon-"+beacon.getId1().toString().
                substring(1,beacon.getId1().toString().length()-1)).setValue(beacon);
    }

    public static void saveNotificationData(NotificationData notification) {
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        DatabaseReference databaseBook = database.child("beaconNotification");
        database.child("notification-" + notification.getBeaconUuid().
                    substring(1,notification.getBeaconUuid().length()-1)).setValue(notification);
    }

    public static String getUserIdToken() {
        return userIdToken;
    }

    public static void setUserIdToken(String userIdToken) {
        FirebaseUtil.userIdToken = userIdToken;
    }

}
