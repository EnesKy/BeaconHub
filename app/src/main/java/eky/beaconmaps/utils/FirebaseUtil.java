package eky.beaconmaps.utils;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import eky.beaconmaps.model.BeaconData;

public class FirebaseUtil {
    public static final String TAG = "FirebaseUtil";

    public static String userIdToken;
    private static boolean failed = false;

    public static List<BeaconData> usersBeacons = new ArrayList<>();
    public static List<BeaconData> blocklist = new ArrayList<>();
    public static List<BeaconData> registeredBeaconList = new ArrayList<>();
    public static Map<String, BeaconData> registeredBeaconMap = new HashMap<>();

    public static void saveUsersBeacons(List<BeaconData> beaconDataList){
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        DatabaseReference userDatabase = database.child("userBeacons").child("user - " + getUserIdToken().substring(0,20));
        if (beaconDataList != null || beaconDataList.size() != 0)
            userDatabase.setValue(beaconDataList);
    }

    public static void refreshUsersBeacons() {
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        DatabaseReference userDatabase = database.child("userBeacons").child("user - " + getUserIdToken().substring(0,20));

        userDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    usersBeacons.add(postSnapshot.getValue(BeaconData.class));
                }
                setFailed(false);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
                setFailed(true);
            }
        });

    }

    public static void registerBeacon(BeaconData beacon) {
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        DatabaseReference registeredBeacons = database.child("registeredBeacons");
        DatabaseReference registerBeacon = registeredBeacons.child(beacon.getIdentity());
        registerBeacon.setValue(beacon);
    }

    public static void refreshRegisteredBeaconList() {

        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        DatabaseReference registeredBeacons = database.child("registeredBeacons");
        DatabaseReference beacon = registeredBeacons.child("beaconData");

        beacon.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                registeredBeaconList.clear();
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    registeredBeaconList.add(postSnapshot.getValue(BeaconData.class));
                }
                setFailed(false);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
                setFailed(true);
            }
        });

    }

    public static Map<String, BeaconData> refreshRegisteredBeaconMap() {

        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        DatabaseReference registeredBeacons = database.child("registeredBeacons");
        DatabaseReference beacon = registeredBeacons.child("beaconData");

        beacon.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    BeaconData temp = postSnapshot.getValue(BeaconData.class);
                    if (temp != null && temp.getIdentity() != null)
                        registeredBeaconMap.put(temp.getIdentity(), temp);
                }
                setFailed(false);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
                setFailed(true);
            }
        });

        if (isFailed())
            return null;

        return registeredBeaconMap;
    }

    public static void add2Blocklist(BeaconData beaconData) {
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        DatabaseReference userDatabase = database.child("usersBlocklist").child("user - " + getUserIdToken().substring(0,20));
        if (!beaconData.isBlocked())
            userDatabase.setValue(beaconData);
    }

    public static void refreshBlocklist() {

        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        DatabaseReference userDatabase = database.child("usersBlocklist").child("user - " + getUserIdToken().substring(0,20));

        userDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    blocklist.add(postSnapshot.getValue(BeaconData.class));
                }
                setFailed(false);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
                setFailed(true);
            }
        });

    }

    public static String getUserIdToken() {
        return userIdToken;
    }

    public static void setUserIdToken(String userIdToken) {
        FirebaseUtil.userIdToken = userIdToken;
    }

    public static boolean isFailed() {
        return failed;
    }

    public static void setFailed(boolean failed) {
        FirebaseUtil.failed = failed;
    }

}
