package eky.beaconmaps.beacon.estimote;

import com.estimote.coresdk.observation.region.beacon.BeaconRegion;
import com.google.android.gms.maps.model.LatLng;

import java.util.UUID;

public class BeaconID {

    private UUID proximityUUID;
    private int major;
    private int minor;
    private LatLng location;

    public BeaconID(UUID proximityUUID, int major, int minor) {
        this.proximityUUID = proximityUUID;
        this.major = major;
        this.minor = minor;
    }

    public BeaconID(UUID proximityUUID, int major, int minor, LatLng location) {
        this.proximityUUID = proximityUUID;
        this.major = major;
        this.minor = minor;
        this.location = location;
    }

    public BeaconID(String UUIDString, int major, int minor) {
        this(UUID.fromString(UUIDString), major, minor);
    }

    public BeaconID(String UUIDString, int major, int minor, LatLng location) {
        this(UUID.fromString(UUIDString), major, minor, location);
    }

    public UUID getProximityUUID() {
        return proximityUUID;
    }

    public int getMajor() {
        return major;
    }

    public int getMinor() {
        return minor;
    }

    public LatLng getLocation() {
        return location;
    }

    public void setLocation(LatLng location) {
        this.location = location;
    }

    public BeaconRegion toBeaconRegion() {
        return new BeaconRegion(toString(), getProximityUUID(), getMajor(), getMinor());
    }

    public String toString() {
        return getProximityUUID().toString() + ":" + getMajor() + ":" + getMinor();
    }

    @Override
    public int hashCode() {
        return toString().hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }

        if (o == this) {
            return true;
        }

        if (getClass() != o.getClass()) {
            return super.equals(o);
        }

        BeaconID other = (BeaconID) o;

        return getProximityUUID().equals(other.getProximityUUID())
                && getMajor() == other.getMajor()
                && getMinor() == other.getMinor();
    }
}
