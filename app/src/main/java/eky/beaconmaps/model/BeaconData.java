package eky.beaconmaps.model;

import com.google.android.gms.maps.model.LatLng;

import org.altbeacon.beacon.Beacon;

import eky.beaconmaps.beacon.estimote.BeaconID;

/**
 * Created by Enes Kamil YILMAZ on 26.04.2019.
 */

public class BeaconData {

    private Beacon beacon;
    private BeaconID beaconID;
    private NotificationData notificationData;
    private String companyName; //Google map marker title and notification subtitle
    private String companyDesc; //Google map marker description
    private String webUrl;
    private String webServiceUrl;
    private LatLng location;
    private boolean isBlocked = false;

    public BeaconData() {}

    public BeaconData(BeaconID beaconID) {
        this.beaconID = beaconID;
    }

    public BeaconData(Beacon beacon) {
        this.beacon = beacon;
    }

    public BeaconData(BeaconID beaconID, String companyName, String companyDesc, NotificationData notificationData, String webUrl, String webServiceUrl) {
        this.beaconID = beaconID;
        this.companyName = companyName;
        this.companyDesc = companyDesc;
        this.notificationData = notificationData;
        this.webUrl = webUrl;
        this.webServiceUrl = webServiceUrl;
    }

    public Beacon getBeacon() {
        return beacon;
    }

    public void setBeacon(Beacon beacon) {
        this.beacon = beacon;
    }

    public BeaconID getBeaconID() {
        return beaconID;
    }

    public void setBeaconID(BeaconID beaconID) {
        this.beaconID = beaconID;
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

    public LatLng getLocation() {
        return location;
    }

    public void setLocation(LatLng location) {
        this.location = location;
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

        if (beaconID != null) {
            return  beaconID.getMajor() == other.beaconID.getMajor() &&
                    beaconID.getMinor() == other.beaconID.getMinor() &&
                    beaconID.getProximityUUID().equals(other.beaconID.getProximityUUID());
        } else {
            if (beacon != null && other.beacon != null)
                return beacon.getIdentifiers().equals(other.beacon.getIdentifiers());
            else
                return false;
        }


    }

}
