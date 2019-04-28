package eky.beaconmaps.model;

import com.google.android.gms.maps.model.LatLng;

import org.altbeacon.beacon.Beacon;

/**
 * Created by Enes Kamil YILMAZ on 26.04.2019.
 */

public class BeaconData {

    private Beacon beacon;
    private LatLng location;
    private NotificationData notificationData;
    private CompanyData companyData;
    private String webUrl;
    private String webServiceUrl;

    public BeaconData() {}

    public BeaconData(Beacon beacon, LatLng location, NotificationData notificationData, CompanyData companyData, String webUrl, String webServiceUrl) {
        this.beacon = beacon;
        this.location = location;
        this.notificationData = notificationData;
        this.companyData = companyData;
        this.webUrl = webUrl;
        this.webServiceUrl = webServiceUrl;
    }

    public Beacon getBeacon() {
        return beacon;
    }

    public void setBeacon(Beacon beacon) {
        this.beacon = beacon;
    }

    public LatLng getLocation() {
        return location;
    }

    public void setLocation(LatLng location) {
        this.location = location;
    }

    public NotificationData getNotificationData() {
        return notificationData;
    }

    public void setNotificationData(NotificationData notificationData) {
        this.notificationData = notificationData;
    }

    public CompanyData getCompanyData() {
        return companyData;
    }

    public void setCompanyData(CompanyData companyData) {
        this.companyData = companyData;
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
}
