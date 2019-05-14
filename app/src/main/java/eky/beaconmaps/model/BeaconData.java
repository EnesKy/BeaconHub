package eky.beaconmaps.model;

import com.google.android.gms.maps.model.LatLng;

import org.altbeacon.beacon.Beacon;

/**
 * Created by Enes Kamil YILMAZ on 26.04.2019.
 */

public class BeaconData {

    private Beacon beacon;
    private String uuid;
    private int major;
    private int minor;
    private NotificationData notificationData;
    private String companyName; //Google map marker title and notification subtitle
    private String companyDesc; //Google map marker description
    private String webUrl;
    private String webServiceUrl;
    private Location location;
    private boolean isBlocked;
    private String update = "";

    public BeaconData() {}

    //Use this for database update
    public BeaconData(String update) {
        this.update = update;
    }

    public BeaconData(Beacon beacon) {
        this.beacon = beacon;
        this.uuid = beacon.getId1().toString();
        this.major = beacon.getId2().toInt();
        this.minor = beacon.getId3().toInt();
    }

    public BeaconData(String uuid, int major, int minor) {
        this.uuid = uuid;
        this.major = major;
        this.minor = minor;
    }

    public BeaconData(String uuid, int major, int minor, NotificationData notificationData) {
        this.uuid = uuid;
        this.major = major;
        this.minor = minor;
        this.notificationData = notificationData;
    }

    public BeaconData(String companyName, String companyDesc, String webUrl, Location location) {
        this.companyName = companyName;
        this.companyDesc = companyDesc;
        this.webUrl = webUrl;
        this.location = location;
    }

    public BeaconData(Beacon beacon, String uuid, int major, int minor, NotificationData notificationData,
                      String companyName, String companyDesc, String webUrl, String webServiceUrl,
                      Location location, boolean isBlocked) {
        this.beacon = beacon;
        this.uuid = uuid;
        this.major = major;
        this.minor = minor;
        this.notificationData = notificationData;
        this.companyName = companyName;
        this.companyDesc = companyDesc;
        this.webUrl = webUrl;
        this.webServiceUrl = webServiceUrl;
        this.location = location;
        this.isBlocked = isBlocked;
    }

    public Beacon getBeacon() {
        return beacon;
    }

    public void setBeacon(Beacon beacon) {
        this.beacon = beacon;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getCompanyDesc() {
        return companyDesc;
    }

    public void setCompanyDesc(String companyDesc) {
        this.companyDesc = companyDesc;
    }

    public NotificationData getNotificationData() {
        return notificationData;
    }

    public void setNotificationData(NotificationData notificationData) {
        this.notificationData = notificationData;
    }

    public String getWebUrl() {
        return webUrl;
    }

    public void setWebUrl(String webUrl) {
        this.webUrl = webUrl;
    }

    public String getWebServiceUrl() {
        return webServiceUrl;
    }

    public void setWebServiceUrl(String webServiceUrl) {
        this.webServiceUrl = webServiceUrl;
    }

    public boolean isBlocked() {
        return isBlocked;
    }

    public void setBlocked(boolean blocked) {
        isBlocked = blocked;
    }

    public Location getLocation() {
        return location;
    }

    public LatLng getLatLng() {
        if (location != null)
            return new LatLng(location.lat, location.lng);
        else
            return null;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public int getMajor() {
        return major;
    }

    public void setMajor(int major) {
        this.major = major;
    }

    public int getMinor() {
        return minor;
    }

    public void setMinor(int minor) {
        this.minor = minor;
    }

    public String getIdentity() {
        if (uuid != null)
            return uuid + " - " + major + " - " + minor;
        else if (beacon != null && beacon.getIdentifiers().size() != 0)
            return beacon.getId1().toString() + " - " + beacon.getId2().toInt() + " - " + beacon.getId3().toInt();
        else
            return update;
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

        BeaconData other = (BeaconData) o;

        return getIdentity().equals(other.getIdentity());
    }

}
