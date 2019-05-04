package eky.beaconmaps.utils;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

import eky.beaconmaps.model.BeaconData;

public class FirebaseUtil {

    public static String userIdToken;

    public static void saveUsersBeacons(List<BeaconData> beaconDataList){
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        DatabaseReference userDatabase = database.child("users").child("user - " + userIdToken);

        for (BeaconData beaconData : beaconDataList)
            userDatabase.push().setValue(beaconData);
    }

    private static List<BeaconData> getUsersBeacons() {
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        //TODO
        return null;
    }

    public static void registerBeacon(BeaconData beacon){
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        DatabaseReference databaseBook = database.child("registeredBeacons");
        databaseBook.child("beacon - " + beacon.getBeacon().getId1().toString()
                            + " - " + beacon.getBeacon().getId2().toString()
                            + " - " + beacon.getBeacon().getId3().toString()).setValue(beacon);
    }

    public static void updateRegisteredBeacon(BeaconData beaconData) {
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        DatabaseReference databaseBook = database.child("registeredBeacons");
        //databaseBook.getDatabase().
    }

    private static List<BeaconData> getRegisteredBeacons() {
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        database.child("registeredBeacons");

        //TODO
        return null;
    }

    public static String getUserIdToken() {
        return userIdToken;
    }

    public static void setUserIdToken(String userIdToken) {
        FirebaseUtil.userIdToken = userIdToken;
    }

}
