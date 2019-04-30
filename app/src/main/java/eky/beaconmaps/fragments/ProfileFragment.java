package eky.beaconmaps.fragments;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

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
import java.util.List;
import java.util.Objects;

import androidx.browser.customtabs.CustomTabsIntent;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import eky.beaconmaps.R;
import eky.beaconmaps.adapter.BeaconAdapter;
import eky.beaconmaps.model.BeaconData;
import eky.beaconmaps.model.NotificationData;
import eky.beaconmaps.utils.PreferencesUtil;

public class ProfileFragment extends Fragment implements BeaconAdapter.ItemClickListener {

    private final String TAG = "Profile Fragment";
    private static final String KEY_NOTIFICATION_DATA = "NOTIFICATION_DATA";

    private TextView placeholder, title;
    private FirebaseUser user;
    private PreferencesUtil preferencesUtil;
    public static List<BeaconData> myBeaconsList;

    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;

    private ProgressDialog pd;
    private static String url;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        preferencesUtil = new PreferencesUtil(Objects.requireNonNull(getActivity()));
        myBeaconsList = preferencesUtil.getMyBeaconsList();
        user = FirebaseAuth.getInstance().getCurrentUser();

        //TODO: bu işlemi dialog eventine ekle. URl giriş dialogunu kullan bunda da
        url = "http://895b8c9f.ngrok.io/getCompanyData";
        new JsonTask().execute(url);
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

        //if (user != null)
        //    title.setText(user.getDisplayName() + "'s Beacons");
        title.setText("My Beacons");

        return rootView;
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);

        if (preferencesUtil.getMyBeaconsList() != null &&
                myBeaconsList.size() != preferencesUtil.getMyBeaconsList().size()) {

            myBeaconsList.clear();
            myBeaconsList = preferencesUtil.getMyBeaconsList();
            if (adapter == null && myBeaconsList != null) {
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

    public void openActionDialog(BeaconData beacon) {
        Dialog beacon_dialog;
        TextView tvSeeNotification, tvAddNotification, tvUpdateNotification;
        TextView tvVisitWebsite, tvAddWebsite, tvUpdateWebsite;
        TextView tvSeeLocation, tvAddLocation, tvUpdateLocation;
        TextView tvAddBlocklist;

        beacon_dialog = new Dialog(Objects.requireNonNull(getActivity()));
        beacon_dialog.setContentView(R.layout.dialog_users_beacons);

        tvAddNotification = beacon_dialog.findViewById(R.id.tv_add_notification);
        tvAddNotification.setOnClickListener(v -> {

            //eğer notificationı varsa visibility==gone

        });

        tvAddWebsite = beacon_dialog.findViewById(R.id.tv_add_website);
        tvAddWebsite.setOnClickListener(v -> {

            //eğer websitesi varsa visibility==gone

        });

        tvAddLocation = beacon_dialog.findViewById(R.id.tv_add_location);
        tvAddLocation.setOnClickListener(v -> {

            //eğer locationı varsa visibility==gone

        });

        tvAddBlocklist = beacon_dialog.findViewById(R.id.tv_add_blocklist);
        tvAddBlocklist.setOnClickListener(v -> {

            //eğer blocked ise visibility==gone

            preferencesUtil.getBlockedBeaconsList().add(beacon);

            beacon_dialog.dismiss();
        });

        tvVisitWebsite = beacon_dialog.findViewById(R.id.tv_visit_website);
        tvVisitWebsite.setOnClickListener(v -> {

            //TODO: add url info here
            //String url = UrlBeaconUrlCompressor.uncompress(beacon.getId1().toByteArray());
            CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
            CustomTabsIntent customTabsIntent = builder.build();
            customTabsIntent.launchUrl(getActivity(), Uri.parse(url));

        });

        tvSeeLocation = beacon_dialog.findViewById(R.id.tv_go_location);
        tvSeeLocation.setOnClickListener(v -> {
            //eğer locationı yoksa visibility==gone
        });

        tvUpdateLocation = beacon_dialog.findViewById(R.id.tv_update_location);
        tvUpdateLocation.setOnClickListener(v -> {

            //eğer locationı yoksa visibility==gone

        });

        tvUpdateNotification = beacon_dialog.findViewById(R.id.tv_update_notification);
        tvUpdateNotification.setOnClickListener(v -> {

            //eğer notificationı yoksa visibility==gone

        });

        tvUpdateWebsite = beacon_dialog.findViewById(R.id.tv_update_website);
        tvUpdateWebsite.setOnClickListener(v -> {

            //eğer websitesi yoksa visibility==gone

        });

        beacon_dialog.show();
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
                    JSONObject jsonObject = new JSONObject(result);

                    //jsonObject.getString("companyName")
                    //jsonObject.getString("title")

                    NotificationData notificationData = new NotificationData(jsonObject.getString("enterTitle"),
                            jsonObject.getString("enterMessage"),
                            jsonObject.getString("exitTitle"),
                            jsonObject.getString("exitMessage"));

                    preferencesUtil.saveObject(KEY_NOTIFICATION_DATA, notificationData);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
