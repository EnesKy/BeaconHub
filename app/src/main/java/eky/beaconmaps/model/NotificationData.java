package eky.beaconmaps.model;

import eky.beaconmaps.beacon.estimote.BeaconID;

public class NotificationData {

    private BeaconID beaconId;
    private String enterTitle;
    private String enterDesc;
    private String exitTitle;
    private String exitDesc;

    public NotificationData() {}

    public NotificationData(BeaconID beaconID, String enterTitle, String enterDesc, String exitTitle, String exitDesc) {
        this.beaconId = beaconID;
        this.enterTitle = enterTitle;
        this.enterDesc = enterDesc;
        this.exitTitle = exitTitle;
        this.exitDesc = exitDesc;
    }

    public BeaconID getBeaconId() {
        return beaconId;
    }

    public void setBeaconId(BeaconID beaconID) {
        this.beaconId = beaconID;
    }

    public String getEnterTitle() {
        return enterTitle;
    }

    public void setEnterTitle(String enterTitle) {
        this.enterTitle = enterTitle;
    }

    public String getEnterDesc() {
        return enterDesc;
    }

    public void setEnterDesc(String enterDesc) {
        this.enterDesc = enterDesc;
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
