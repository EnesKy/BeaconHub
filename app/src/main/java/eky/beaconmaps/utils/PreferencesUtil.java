package eky.beaconmaps.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import eky.beaconmaps.model.BeaconData;

public class PreferencesUtil {
    private static final String KEY_SHARED_FILE = "APP_FILE";
    private static final String KEY_MY_BEACONS_LIST = "MY_BEACONS_LIST";
    private static final String KEY_BLOCKED_BEACONS_LIST = "BLOCKED_BEACONS_LIST";
    private final SharedPreferences sharedPreferences;

    public PreferencesUtil(Context context) {
        sharedPreferences = context.getSharedPreferences(KEY_SHARED_FILE, Context.MODE_PRIVATE);
    }

    public void saveData(String key, String value) {
        sharedPreferences.edit().putString(key, value).apply();
    }

    public String getData(String key, String defaultValue) {
        return sharedPreferences.getString(key, defaultValue);
    }

    public void clearAllData() {
        sharedPreferences.edit().clear().apply();
    }

    public void removeData(String key) {
        sharedPreferences.edit().remove(key).apply();
    }

    public <T> T getObject(String key, Class<T> classType) {
        String settingsData = getData(key, null);

        if (settingsData == null)
            return null;
        else
            return new Gson().fromJson(settingsData, classType);
    }

    public  <T> void saveObject(String key, T object) {
        sharedPreferences.edit().putString(key, new Gson().toJson(object)).apply();
    }

    public void saveMyBeaconsList(List<BeaconData> list) {
        sharedPreferences.edit().putString(KEY_MY_BEACONS_LIST, new Gson().toJson(list)).apply();
    }

    public List<BeaconData> getMyBeaconsList() {
        String allUserData = getData(KEY_MY_BEACONS_LIST, null);
        if (allUserData == null)
            return null;

        Type listType = new TypeToken<ArrayList<BeaconData>>() {
        }.getType();
        return new Gson().fromJson(allUserData, listType);
    }

    public void saveBlockedBeaconsList(List<BeaconData> list) {
        sharedPreferences.edit().putString(KEY_BLOCKED_BEACONS_LIST, new Gson().toJson(list)).apply();
    }

    public List<BeaconData> getBlockedBeaconsList() {
        String allUserData = getData(KEY_BLOCKED_BEACONS_LIST, null);
        if (allUserData == null)
            return null;

        Type listType = new TypeToken<ArrayList<BeaconData>>() {
        }.getType();
        return new Gson().fromJson(allUserData, listType);
    }

}