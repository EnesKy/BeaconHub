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

    public static List<BeaconData> usersBeaconList = new ArrayList<>();
    public static List<BeaconData> blocklist = new ArrayList<>();
    public static List<BeaconData> registeredBeaconList = new ArrayList<>();
    public static Map<String, BeaconData> registeredBeaconMap = new HashMap<>();

    public static void claimBeacon(BeaconData beaconData){
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        DatabaseReference usersBeacons = database.child("userBeacons").child("user - " + getUserIdToken().substring(0,20));
        DatabaseReference beacon = usersBeacons.child(beaconData.getIdentity());
        beacon.setValue(beaconData);
    }

    public static void removeClaimedBeacon(BeaconData beaconData) {
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        DatabaseReference usersBeacons = database.child("userBeacons").child("user - " + getUserIdToken().substring(0,20));
        usersBeacons.child(beaconData.getIdentity()).removeValue(
                (databaseError, databaseReference) -> Log.d(TAG, "Beacon removed from blocklist."));
    }

    public static void registerBeacon(BeaconData beacon) {
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        DatabaseReference registeredBeacons = database.child("registeredBeacons");
        DatabaseReference registerBeacon = registeredBeacons.child(beacon.getIdentity());
        registerBeacon.setValue(beacon);
    }

    public static void removeRegisteredBeacon(BeaconData beaconData) {
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        DatabaseReference registeredBeacons = database.child("registeredBeacons");
        registeredBeacons.child(beaconData.getIdentity()).removeValue(
                (databaseError, databaseReference) -> Log.d(TAG, "Beacon removed from blocklist."));
    }

    public static void add2Blocklist(BeaconData beaconData) {
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        DatabaseReference userDatabase = database.child("usersBlocklist").child("user - " + getUserIdToken().substring(0,20));
        DatabaseReference blockList = userDatabase.child(beaconData.getIdentity());
        blockList.setValue(beaconData);
    }

    public static void removeBlockedBeacon(BeaconData beaconData) {
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        DatabaseReference userDatabase = database.child("usersBlocklist").child("user - " + getUserIdToken().substring(0,20));
        DatabaseReference blockList = userDatabase.child(beaconData.getIdentity());
        blockList.removeValue(
                (databaseError, databaseReference) -> Log.d(TAG, "Beacon removed from blocklist."));
    }

    public static void updateBeaconData(BeaconData beaconData, String type) {
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        DatabaseReference usersBeacons = database.child("userBeacons").child("user - " + getUserIdToken().substring(0,20));
        DatabaseReference beacon = usersBeacons.child(beaconData.getIdentity());

        HashMap updates = new HashMap();

        if (type.equals("location")) {
            updates.put("location", beaconData.getLocation());
            updates.put("companyName", beaconData.getCompanyName());
            updates.put("companyDesc", beaconData.getCompanyDesc());
            updateRegisteredBeacon(beaconData, "location", updates);
        } else if (type.equals("notification")) {
            updates.put("notificationData", beaconData.getNotificationData());
            updateRegisteredBeacon(beaconData, "notification", updates);
        } else if (type.equals("website")) {
            updates.put("webUrl", beaconData.getWebUrl());
            updateRegisteredBeacon(beaconData, "website", updates);
        } else if (type.equals("webService")) {
            updates.put("webServiceUrl", beaconData.getWebServiceUrl());
            updates.put("webUrl", beaconData.getWebUrl());
            updates.put("companyName", beaconData.getCompanyName());
            updates.put("companyDesc", beaconData.getCompanyDesc());
            updates.put("notificationData", beaconData.getNotificationData());
            updateRegisteredBeacon(beaconData, "webService", updates);
        } else if (type.equals("block")) {
            updates.put("isBlocked", beaconData.isBlocked());
            updateRegisteredBeacon(beaconData, "block", updates);
        }

        beacon.updateChildren(updates);

    }

    public static void updateRegisteredBeacon(BeaconData beaconData, String type, HashMap updates) {
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        DatabaseReference registeredBeacons = database.child("registeredBeacons");
        DatabaseReference registerBeacon = registeredBeacons.child(beaconData.getIdentity());

        if (type.equals("location")) {
            updates.put("location", beaconData.getLocation());
            updates.put("companyName", beaconData.getCompanyName());
            updates.put("companyDesc", beaconData.getCompanyDesc());
        } else if (type.equals("notification")) {
            updates.put("notificationData", beaconData.getNotificationData());
        } else if (type.equals("website")) {
            updates.put("webUrl", beaconData.getWebUrl());
        } else if (type.equals("webService")) {
            updates.put("webServiceUrl", beaconData.getWebServiceUrl());
            updates.put("webUrl", beaconData.getWebUrl());
            updates.put("companyName", beaconData.getCompanyName());
            updates.put("companyDesc", beaconData.getCompanyDesc());
            updates.put("notificationData", beaconData.getNotificationData());
        } else if (type.equals("block")) {
            updates.put("isBlocked", beaconData.isBlocked());
        }

        registerBeacon.updateChildren(updates);

    }

    public static void refreshUsersBeacons() {

        claimBeacon(new BeaconData("temp"));
        removeClaimedBeacon(new BeaconData("temp"));

        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        DatabaseReference userDatabase = database.child("userBeacons").child("user - " + getUserIdToken().substring(0,20));

        userDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                usersBeaconList.clear();
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    usersBeaconList.add(postSnapshot.getValue(BeaconData.class));
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

    public static void refreshRegisteredBeaconList() {

        registerBeacon(new BeaconData("temp"));
        removeRegisteredBeacon(new BeaconData("temp"));

        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        DatabaseReference registeredBeacons = database.child("registeredBeacons");

        registeredBeacons.addValueEventListener(new ValueEventListener() {
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

        registerBeacon(new BeaconData("temp2"));
        removeRegisteredBeacon(new BeaconData("temp2"));

        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        DatabaseReference registeredBeacons = database.child("registeredBeacons");

        registeredBeacons.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                registeredBeaconMap.clear();
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

    public static void refreshBlocklist() {

        add2Blocklist(new BeaconData("temp"));
        removeBlockedBeacon(new BeaconData("temp"));

        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        DatabaseReference userDatabase = database.child("usersBlocklist").child("user - " + getUserIdToken().substring(0,20));

        userDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                blocklist.clear();
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
