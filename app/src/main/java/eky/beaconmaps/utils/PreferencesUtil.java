package eky.beaconmaps.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class PreferencesUtil {
    private static final String KEY_SHARED_FILE = "APP_FILE";
    private final SharedPreferences sharedPreferences;

    public PreferencesUtil(Context context) {
        sharedPreferences = context.getSharedPreferences(KEY_SHARED_FILE, Context.MODE_PRIVATE);
    }

    public void clearAllData() {
        sharedPreferences.edit().clear().apply();
    }

    public String getData(String key, String defaultValue) {
        return sharedPreferences.getString(key, defaultValue);
    }

    public void saveData(String key, String value) {
        sharedPreferences.edit().putString(key, value).apply();
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

    public void saveList(String key, List<Object> list) {
        sharedPreferences.edit().putString(key, new Gson().toJson(list)).apply();
    }

    public List<Object> getList(String key) {
        String allUserData = getData(key, null);
        if (allUserData == null)
            return null;

        Type listType = new TypeToken<ArrayList<Object>>() {
        }.getType();
        return new Gson().fromJson(allUserData, listType);
    }

}