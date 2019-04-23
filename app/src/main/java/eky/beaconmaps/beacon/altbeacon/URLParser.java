package eky.beaconmaps.beacon.altbeacon;

import android.bluetooth.le.AdvertiseCallback;
import android.bluetooth.le.AdvertiseSettings;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.BeaconTransmitter;
import org.altbeacon.beacon.Identifier;
import org.altbeacon.beacon.utils.UrlBeaconUrlCompressor;

import java.net.MalformedURLException;
import java.util.ArrayList;

/**
 * Created by Enes Kamil YILMAZ on 19.04.2019.
 */

public class URLParser {

    public static void urlParse(String url, Context context, Beacon beacon) {
        try {
            byte[] urlBytes = UrlBeaconUrlCompressor.compress(url);
            Identifier encodedUrlIdentifier = Identifier.fromBytes(urlBytes, 0, urlBytes.length, false);
            ArrayList<Identifier> identifiers = new ArrayList<>();
            identifiers.add(encodedUrlIdentifier);
            Beacon beacon1 = new Beacon.Builder()
                    .setIdentifiers(identifiers)
                    .setManufacturer(beacon.getManufacturer())
                    .setTxPower(beacon.getTxPower())
                    .build();

            BeaconParser beaconParser = new BeaconParser().setBeaconLayout(BeaconParser.EDDYSTONE_URL_LAYOUT);
            BeaconTransmitter beaconTransmitter = new BeaconTransmitter(context, beaconParser);
            beaconTransmitter.setAdvertiseTxPowerLevel(AdvertiseSettings.ADVERTISE_TX_POWER_HIGH);
            beaconTransmitter.setAdvertiseMode(AdvertiseSettings.ADVERTISE_MODE_LOW_LATENCY);
            beaconTransmitter.startAdvertising(beacon1, new AdvertiseCallback() {
                @Override
                public void onStartFailure(int errorCode) {
                    Log.e("", "Advertisement start failed with code: " + errorCode);
                    Toast.makeText(context, "Error - " + String.valueOf(errorCode), Toast.LENGTH_LONG).show();

                }

                @Override
                public void onStartSuccess(AdvertiseSettings settingsInEffect) {
                    Log.i("", "Advertisement start succeeded.");
                    Toast.makeText(context, "URL is Transmitting", Toast.LENGTH_LONG).show();
                }
            });
        } catch (MalformedURLException e) {
            Log.d("", "That URL cannot be parsed");
        }
    }

    public static void urlParsing(Context context, Beacon beacon) {
        try {
            byte[] urlBytes = UrlBeaconUrlCompressor.compress("http://www.davidgyoungtech.com");
            Identifier encodedUrlIdentifier = Identifier.fromBytes(urlBytes, 0, urlBytes.length, false);
            ArrayList<Identifier> identifiers = new ArrayList<>();
            identifiers.add(encodedUrlIdentifier);
            beacon = new Beacon.Builder()
                    .setIdentifiers(identifiers)
                    .setManufacturer(0x0118)
                    .setTxPower(-59)
                    .build();
            BeaconParser beaconParser = new BeaconParser()
                    .setBeaconLayout(BeaconParser.EDDYSTONE_URL_LAYOUT);
            BeaconTransmitter beaconTransmitter = new BeaconTransmitter(context, beaconParser);
            beaconTransmitter.startAdvertising(beacon);
        } catch (MalformedURLException e) {
            Log.d("", "That URL cannot be parsed");
        }
    }


}
