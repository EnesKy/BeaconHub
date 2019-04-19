package eky.beaconmaps.beacon.altbeacon;

import android.content.Context;
import android.util.Log;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.BeaconTransmitter;
import org.altbeacon.beacon.Identifier;
import org.altbeacon.beacon.utils.UrlBeaconUrlCompressor;

import java.net.MalformedURLException;

/**
 * Created by Enes Kamil YILMAZ on 19.04.2019.
 */
public class URLParser {

    public void urlParse(Context context, Beacon beacon) { // TODO: make this work
        try {
            byte[] urlBytes = UrlBeaconUrlCompressor.compress("http://www.fsmvu.com");
            Identifier encodedUrlIdentifier = Identifier.fromBytes(urlBytes, 0, urlBytes.length, false);
            /*
            ArrayList<Identifier> identifiers = new ArrayList<>();
            identifiers.add(encodedUrlIdentifier);
            Beacon beacon = new Beacon.Builder()
                    .setIdentifiers(identifiers)
                    .setManufacturer(0x0118)
                    .setTxPower(-59)
                    .build();
            */
            BeaconParser beaconParser = new BeaconParser().setBeaconLayout(BeaconParser.EDDYSTONE_URL_LAYOUT);
            BeaconTransmitter beaconTransmitter = new BeaconTransmitter(context, beaconParser);
            beaconTransmitter.startAdvertising(beacon);
        } catch (MalformedURLException e) {
            Log.d("URLParser", "That URL cannot be parsed");
        }
    }



}
