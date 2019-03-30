package eky.beaconmaps.model;

public class NotificationData {

    String beaconID;
    String tag;
    String helloTitle;
    String helloDesc;
    String exitTitle;
    String exitDesc;

    public NotificationData() {}

    public NotificationData(String beaconID, String helloTitle, String helloDesc, String exitTitle, String exitDesc) {
        this.beaconID = beaconID;
        this.helloTitle = helloTitle;
        this.helloDesc = helloDesc;
        this.exitTitle = exitTitle;
        this.exitDesc = exitDesc;
    }

    public void setBeaconID(String beaconID) {
        this.beaconID = beaconID;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public void setHelloTitle(String helloTitle) {
        this.helloTitle = helloTitle;
    }

    public void setHelloDesc(String helloDesc) {
        this.helloDesc = helloDesc;
    }

    public void setExitTitle(String exitTitle) {
        this.exitTitle = exitTitle;
    }

    public void setExitDesc(String exitDesc) {
        this.exitDesc = exitDesc;
    }

    public String getBeaconID() {
        return beaconID;
    }

    public String getTag() {
        return tag;
    }

    public String getHelloTitle() {
        return helloTitle;
    }

    public String getHelloDesc() {
        return helloDesc;
    }

    public String getExitTitle() {
        return exitTitle;
    }

    public String getExitDesc() {
        return exitDesc;
    }

}
