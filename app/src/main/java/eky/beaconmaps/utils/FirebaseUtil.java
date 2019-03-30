package eky.beaconmaps.utils;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import eky.beaconmaps.model.BeaconData;
import eky.beaconmaps.model.NotificationData;

public class FirebaseUtil {

    public static void saveBeacon(){
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        DatabaseReference databaseBook = database.child("beacon");
        databaseBook.push().setValue("deneme");
    }

    public static void saveBeaconData(BeaconData beacon){
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        DatabaseReference databaseBook = database.child("beaconData");
        database.child("beacon-"+beacon.getuuid().substring(1,beacon.getuuid().length()-1)).setValue(beacon);
    }

    public static void saveNotificationData(NotificationData notification) {
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        DatabaseReference databaseBook = database.child("beaconNotification");
        database.child("notification-" + notification.getBeaconID().
                    substring(1,notification.getBeaconID().length()-1)).setValue(notification);
    }

}
