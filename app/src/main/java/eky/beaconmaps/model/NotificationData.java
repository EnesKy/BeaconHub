package eky.beaconmaps.model;

public class NotificationData {

    String beaconUuid;
    String helloTitle;
    String helloDesc;
    String exitTitle;
    String exitDesc;

    public NotificationData(String beaconUuid, String helloTitle, String helloDesc, String exitTitle, String exitDesc) {
        this.beaconUuid = beaconUuid;
        this.helloTitle = helloTitle;
        this.helloDesc = helloDesc;
        this.exitTitle = exitTitle;
        this.exitDesc = exitDesc;
    }

    public String getBeaconUuid() {
        return beaconUuid;
    }

    public void setBeaconUuid(String beaconUuid) {
        this.beaconUuid = beaconUuid;
    }

    public String getHelloTitle() {
        return helloTitle;
    }

    public void setHelloTitle(String helloTitle) {
        this.helloTitle = helloTitle;
    }

    public String getHelloDesc() {
        return helloDesc;
    }

    public void setHelloDesc(String helloDesc) {
        this.helloDesc = helloDesc;
    }

    public String getExitTitle() {
        return exitTitle;
    }

    public void setExitTitle(String exitTitle) {
        this.exitTitle = exitTitle;
    }

    public String getExitDesc() {
        return exitDesc;
    }

    public void setExitDesc(String exitDesc) {
        this.exitDesc = exitDesc;
    }
}
