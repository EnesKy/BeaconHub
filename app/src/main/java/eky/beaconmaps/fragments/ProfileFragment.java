package eky.beaconmaps.fragments;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import androidx.browser.customtabs.CustomTabsIntent;
import androidx.core.app.NotificationCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import eky.beaconmaps.R;
import eky.beaconmaps.activities.LocationActivity;
import eky.beaconmaps.activities.MainActivity;
import eky.beaconmaps.activities.NotificationActivity;
import eky.beaconmaps.adapter.BeaconAdapter;
import eky.beaconmaps.model.BeaconData;
import eky.beaconmaps.model.NotificationData;
import eky.beaconmaps.utils.FirebaseUtil;
import eky.beaconmaps.utils.PreferencesUtil;

public class ProfileFragment extends Fragment implements BeaconAdapter.ItemClickListener {

    private final String TAG = "Profile Fragment";
    private static final String KEY_NOTIFICATION_DATA = "NOTIFICATION_DATA";
    private String CHANNEL_ID = "beaconMapsID";
    private int notificationID = 0;

    private TextView placeholder, title;
    private FirebaseUser user;
    private PreferencesUtil preferencesUtil;
    public static List<BeaconData> myBeaconsList = new ArrayList<>();
    private List<BeaconData> blockedBeaconsList;

    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;

    private ProgressDialog pd;
    private static String url;
    private String serviceResult;
    private BeaconData tempBeacon;
    private TextView name, email;
    private ImageView profilepic;

    TextView serviceResults;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        preferencesUtil = new PreferencesUtil(Objects.requireNonNull(getActivity()));

        //myBeaconsList = FirebaseUtil.usersBeaconList;
        //blockedBeaconsList = FirebaseUtil.blocklist;

        myBeaconsList = preferencesUtil.getMyBeaconsList();
        blockedBeaconsList = preferencesUtil.getBlockedBeaconsList();

        if (blockedBeaconsList == null) {
            blockedBeaconsList = new ArrayList<>();
        }

        if (blockedBeaconsList.size() > 0 && myBeaconsList != null && myBeaconsList.size() > 0) {
            for (BeaconData beaconData : myBeaconsList)
                if (blockedBeaconsList.contains(beaconData))
                    beaconData.setBlocked(true);
        }

        user = FirebaseAuth.getInstance().getCurrentUser();

    }

    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_profile, container, false);

        placeholder = rootView.findViewById(R.id.tv_placeholder);

        recyclerView = rootView.findViewById(R.id.rv_my_beacons);
        layoutManager = new LinearLayoutManager(inflater.getContext());
        recyclerView.setLayoutManager(layoutManager);
        adapter = new BeaconAdapter(myBeaconsList, false, this);
        recyclerView.setAdapter(adapter);

        title = rootView.findViewById(R.id.tv_title);
        title.setText("My Beacons");

        name = rootView.findViewById(R.id.tv_user_name);
        email = rootView.findViewById(R.id.tv_user_email);
        profilepic = rootView.findViewById(R.id.iv_profile_pic);

        if (user != null) {
            if (user.getDisplayName() == null)
                name.setText(preferencesUtil.getData("KEY_USER_NAME", ""));
            else
                name.setText(user.getDisplayName());

            email.setText(user.getEmail());

            if (user.getPhotoUrl() != null)
                //Glide.with(this).load(user.getPhotoUrl()).into(profilepic);
                Picasso.get().load(user.getPhotoUrl()).into(profilepic, new Callback() {
                    @Override
                    public void onSuccess() {
                        //TODO: resim yüklenene kadar önceki stock resim görünsün.
                    }

                    @Override
                    public void onError(Exception e) {

                    }
                });
        }

        return rootView;
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);

        preferencesUtil.updateLists();

        if (!hidden) {

            //if (FirebaseUtil.usersBeaconList != null) {
            if (preferencesUtil.getMyBeaconsList() != null) {
                myBeaconsList.clear();
                //myBeaconsList.addAll(FirebaseUtil.usersBeaconList);
                myBeaconsList.addAll(preferencesUtil.getMyBeaconsList());

                for (BeaconData beaconData : myBeaconsList)
                    if (beaconData.getUuid() == null)
                        myBeaconsList.remove(beaconData);

                if (adapter == null) {
                    adapter = new BeaconAdapter(myBeaconsList, false, this);
                    recyclerView.setAdapter(adapter);
                } else {
                    adapter.notifyDataSetChanged();
                }
            }

            if (myBeaconsList == null || myBeaconsList.size() == 0)
                placeholder.setVisibility(View.VISIBLE);
            else
                placeholder.setVisibility(View.GONE);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        onHiddenChanged(false);
    }

    private void openActionDialog(BeaconData beacon) {
        Dialog beacon_dialog;
        TextView tvUUID, tvMajor, tvMinor;
        TextView tvSeeNotification, tvAddNotification, tvUpdateNotification;
        TextView tvVisitWebsite, tvAddWebsite, tvUpdateWebsite;
        TextView tvSeeLocation, tvAddLocation, tvUpdateLocation;
        TextView tvAddWebService, tvUpdateWebService;
        TextView tvAddBlocklist;

        beacon_dialog = new Dialog(Objects.requireNonNull(getActivity()));
        beacon_dialog.setContentView(R.layout.dialog_users_beacons);

        tvUUID = beacon_dialog.findViewById(R.id.tv_uuid);
        tvMajor = beacon_dialog.findViewById(R.id.tv_major);
        tvMinor = beacon_dialog.findViewById(R.id.tv_minor);

        if (beacon.getUuid() != null && !beacon.getUuid().isEmpty()) {
            tvUUID.setText("UUID : " + beacon.getUuid());
            tvMajor.setText("Major : " + beacon.getMajor());
            tvMinor.setText("Minor : " + beacon.getMinor());
        }
        else if (beacon.getBeacon() != null) {
            tvUUID.setText("UUID : " + beacon.getBeacon().getId1().toString());
            tvMajor.setText("Major : " + beacon.getBeacon().getId2().toString());
            tvMinor.setText("Minor : " + beacon.getBeacon().getId3().toString());
        }

        tvSeeNotification = beacon_dialog.findViewById(R.id.tv_see_notification);
        tvSeeNotification.setOnClickListener( v -> {

            if (beacon.getNotificationData() != null ) {

                showNotification(beacon.getNotificationData().getEnterTitle(),
                        beacon.getNotificationData().getEnterDesc(), beacon);

                showNotification(beacon.getNotificationData().getExitTitle(),
                        beacon.getNotificationData().getExitDesc(), beacon);
            }
            beacon_dialog.dismiss();

        });

        tvAddNotification = beacon_dialog.findViewById(R.id.tv_add_notification);
        tvAddNotification.setOnClickListener(v -> {

            Intent intent = new Intent(getActivity(), NotificationActivity.class);
            preferencesUtil.saveObject("clicked", beacon);
            startActivity(intent);
            beacon_dialog.dismiss();

        });

        tvUpdateNotification = beacon_dialog.findViewById(R.id.tv_update_notification);
        tvUpdateNotification.setOnClickListener(v -> {

            Intent intent = new Intent(getActivity(), NotificationActivity.class);
            preferencesUtil.saveObject("clicked", beacon);
            startActivity(intent);
            beacon_dialog.dismiss();

        });

        tvAddBlocklist = beacon_dialog.findViewById(R.id.tv_add_blocklist);
        tvAddBlocklist.setOnClickListener(v -> {

            beacon.setBlocked(true);
            blockedBeaconsList.add(beacon);
            preferencesUtil.saveBlockedBeaconsList(blockedBeaconsList);
            onHiddenChanged(false);
            FirebaseUtil.removeBlockedBeacon(beacon);
            FirebaseUtil.updateBeaconData(beacon, "block");
            preferencesUtil.updateLists();

            beacon_dialog.dismiss();
        });

        tvAddWebsite = beacon_dialog.findViewById(R.id.tv_add_website);
        tvAddWebsite.setOnClickListener(v -> {

            openUrlDialog(beacon, false);

        });

        tvVisitWebsite = beacon_dialog.findViewById(R.id.tv_visit_website);
        tvVisitWebsite.setOnClickListener(v -> {
            String url = "";
            if (beacon.getWebUrl() != null && !beacon.getWebUrl().isEmpty())
                url = beacon.getWebUrl();
            CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
            CustomTabsIntent customTabsIntent = builder.build();
            customTabsIntent.launchUrl(getActivity(), Uri.parse(url));
            beacon_dialog.dismiss();

        });

        tvUpdateWebsite = beacon_dialog.findViewById(R.id.tv_update_website);
        tvUpdateWebsite.setOnClickListener(v -> {

            openUrlDialog(beacon, false);

        });

        tvAddLocation = beacon_dialog.findViewById(R.id.tv_add_location);
        tvAddLocation.setOnClickListener(v -> {

            Intent intent = new Intent(getActivity(), LocationActivity.class);
            preferencesUtil.saveObject("claimed", beacon);
            startActivity(intent);
            beacon_dialog.dismiss();

        });

        tvSeeLocation = beacon_dialog.findViewById(R.id.tv_go_location);
        tvSeeLocation.setOnClickListener(v -> {

            Intent resultIntent = new Intent(getActivity(), MainActivity.class);
            resultIntent.setFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
            resultIntent.putExtra("KEY_LOC", beacon.getLatLng());
            startActivity(resultIntent);
            beacon_dialog.dismiss();

        });

        tvUpdateLocation = beacon_dialog.findViewById(R.id.tv_update_location);
        tvUpdateLocation.setOnClickListener(v -> {

            Intent intent = new Intent(getActivity(), LocationActivity.class);
            preferencesUtil.saveObject("claimed", beacon);
            startActivity(intent);
            beacon_dialog.dismiss();

        });

        tvAddWebService = beacon_dialog.findViewById(R.id.tv_add_webservice);
        tvAddWebService.setOnClickListener(v -> {

            openUrlDialog(beacon, true);

        });

        tvUpdateWebService = beacon_dialog.findViewById(R.id.tv_update_webservice);
        tvUpdateWebService.setOnClickListener(v -> {

            openUrlDialog(beacon, true);

        });

        if (beacon.getNotificationData() == null) {
            tvSeeNotification.setVisibility(View.GONE);
            tvUpdateNotification.setVisibility(View.GONE);
        } else {
            tvAddNotification.setVisibility(View.GONE);
        }

        if (beacon.getWebUrl() == null || beacon.getWebUrl().isEmpty()) {
            tvVisitWebsite.setVisibility(View.GONE);
            tvUpdateWebsite.setVisibility(View.GONE);
        } else {
            tvAddWebsite.setVisibility(View.GONE);
        }

        if (beacon.getWebServiceUrl() == null) {
            tvUpdateWebService.setVisibility(View.GONE);
        } else {
            tvAddWebService.setVisibility(View.GONE);
        }

        if (beacon.getLocation() == null) {
            tvAddLocation.setVisibility(View.GONE);
        } else {
            tvSeeLocation.setVisibility(View.GONE);
            tvUpdateLocation.setVisibility(View.GONE);
        }

        if (FirebaseUtil.blocklist != null)
            if (FirebaseUtil.blocklist.contains(beacon))
                tvAddBlocklist.setVisibility(View.GONE);

        beacon_dialog.show();
    }

    public void openUrlDialog(BeaconData beaconData, boolean forWebService) {
        Dialog beacon_website;
        MaterialButton mbApply, mbTest;
        EditText etURL;

        beacon_website = new Dialog(Objects.requireNonNull(getActivity()));
        beacon_website.setContentView(R.layout.dialog_add_url);

        etURL = beacon_website.findViewById(R.id.et_url);

        if (forWebService) {
            if (beaconData.getWebServiceUrl() != null && !beaconData.getWebServiceUrl().isEmpty())
                etURL.setText(beaconData.getWebServiceUrl());
                url = beaconData.getWebServiceUrl();
        } else {
            if (beaconData.getWebUrl() != null && !beaconData.getWebUrl().isEmpty())
                etURL.setText(beaconData.getWebUrl());
        }

        etURL.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try {
                    url = s.toString();
                    URL newURL = new URL(url);
                } catch (MalformedURLException e) {
                    etURL.setError("Malformed URL");
                    e.printStackTrace();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        mbApply = beacon_website.findViewById(R.id.btn_apply);
        mbApply.setOnClickListener(v1 -> {

            if (!etURL.getText().toString().isEmpty()) {
                if (forWebService) {
                    FirebaseUtil.updateBeaconData(tempBeacon, "webService");
                    preferencesUtil.updateLists();
                } else {
                    beaconData.setWebUrl(etURL.getText().toString());
                    FirebaseUtil.updateBeaconData(beaconData,"website");
                    preferencesUtil.updateLists();
                }
            }

            onHiddenChanged(false);

        });

        serviceResults = beacon_website.findViewById(R.id.tv_websservice_results);

        mbTest = beacon_website.findViewById(R.id.btn_test);
        mbTest.setOnClickListener(v1 -> {
            if (forWebService) {

                tempBeacon = beaconData;
                new JsonTask().execute(etURL.getText().toString());
                serviceResults.setVisibility(View.VISIBLE);
                serviceResults.setText(serviceResult);

            } else {
                String url = "";
                if (!etURL.getText().toString().isEmpty())
                    url = etURL.getText().toString();
                CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
                CustomTabsIntent customTabsIntent = builder.build();
                customTabsIntent.launchUrl(getActivity(), Uri.parse(url));
            }

        });

        beacon_website.show();
    }

    @Override
    public void onItemClick(int position, View view) {
        openActionDialog(myBeaconsList.get(position));
    }


    @SuppressLint("StaticFieldLeak")
    public class JsonTask extends AsyncTask<String, String, String> {

        protected void onPreExecute() {
            super.onPreExecute();

            pd = new ProgressDialog(getActivity());
            pd.setMessage("Please wait");
            pd.setCancelable(false);
            pd.show();
        }

        protected String doInBackground(String... params) {

            HttpURLConnection connection = null;
            BufferedReader reader = null;

            try {
                URL url = new URL(params[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                InputStream stream = connection.getInputStream();
                reader = new BufferedReader(new InputStreamReader(stream));
                StringBuffer buffer = new StringBuffer();
                String line;

                while ((line = reader.readLine()) != null) {
                    buffer.append(line + "\n");
                }

                return buffer.toString();

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
                try {
                    if (reader != null) {
                        reader.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (pd.isShowing()) {
                pd.dismiss();
            }

            if (result == null || result.isEmpty()) {
                Log.d(TAG, "onPostExecute Error.");
            } else {
                Log.d(TAG, "onPostExecute result : " + result);

                try {
                    serviceResult = result;

                    JSONObject jsonObject = new JSONObject(result);

                    String companyName = jsonObject.getString("markerTitle");
                    String companyDesc = jsonObject.getString("markerDesc");
                    String website = jsonObject.getString("website");

                    NotificationData notificationData = new NotificationData(jsonObject.getString("enterTitle"),
                            jsonObject.getString("enterMessage"),
                            jsonObject.getString("exitTitle"),
                            jsonObject.getString("exitMessage"));

                    tempBeacon.setCompanyName(companyName);
                    tempBeacon.setCompanyDesc(companyDesc);
                    tempBeacon.setWebUrl(website);
                    tempBeacon.setWebServiceUrl(url);
                    tempBeacon.setNotificationData(notificationData);

                    serviceResults.setText("Success ! \n" + serviceResult);

                } catch (JSONException e) {

                    serviceResults.setText("Failure !");
                    e.printStackTrace();
                }
            }
        }
    }

    private void showNotification(String title, String message, BeaconData beaconData) {
        Intent resultIntent = new Intent(getActivity(), MainActivity.class);
        if (beaconData.getLocation() != null)
            resultIntent.putExtra("KEY_LOC", beaconData.getLatLng());
        PendingIntent resultPendingIntent = PendingIntent.getActivity(
                getActivity(), 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification notification;

        String subtext = "";

        if (beaconData.getCompanyName() != null) {
            subtext = beaconData.getCompanyName();
        }

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            notification = new Notification.Builder(getActivity(), CHANNEL_ID)
                    .setSmallIcon(R.mipmap.beacon_maps_no_background_icon)
                    .setContentTitle(title)
                    //.setContentText(message)
                    .setStyle(new Notification.BigTextStyle().bigText(message))
                    .setContentIntent(resultPendingIntent)
                    .setSubText(subtext)
                    .setVisibility(Notification.VISIBILITY_PUBLIC)
                    .setAutoCancel(true)
                    .build();
        } else {
            notification = new NotificationCompat.Builder(getActivity(), CHANNEL_ID)
                    .setSmallIcon(R.mipmap.beacon_maps_no_background_icon)
                    .setContentTitle(title)
                    //.setContentText(message)
                    .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                    .setSubText(subtext)
                    .setDefaults(NotificationCompat.DEFAULT_ALL)
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                    .setContentIntent(resultPendingIntent)
                    .setAutoCancel(true)
                    .build();
        }

        NotificationManager notificationManager =
                (NotificationManager) getActivity().getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(notificationID++, notification);

    }

}
