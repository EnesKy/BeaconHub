package eky.beaconmaps.utils;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


import eky.beaconmaps.model.BeaconData;
import eky.beaconmaps.model.NotificationData;

public class FirebaseUtil {

    public static String userIdToken;

    public static void saveBeacon(){
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        DatabaseReference databaseBook = database.child("beacon");
        databaseBook.push().setValue("deneme");
    }

    public static void saveBeaconData(BeaconData beacon){
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        DatabaseReference databaseBook = database.child("beaconData");
        database.child("beacon-"+beacon.getBeaconID().getProximityUUID().toString().
                substring(1,beacon.getBeaconID().getProximityUUID().toString().length()-1)).setValue(beacon);
    }

    public static void saveNotificationData(NotificationData notification) {
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        DatabaseReference databaseBook = database.child("beaconNotification");
        //database.child("notification-" + notification.getBeaconId().substring(1,notification.getBeaconId().length()-1)).setValue(notification);
    }

    public static String getUserIdToken() {
        return userIdToken;
    }

    public static void setUserIdToken(String userIdToken) {
        FirebaseUtil.userIdToken = userIdToken;
    }

}
