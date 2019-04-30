package eky.beaconmaps.model;

public class NotificationData {

    private String enterTitle;
    private String enterDesc;
    private String exitTitle;
    private String exitDesc;

    public NotificationData() {}

    public NotificationData(String enterTitle, String enterDesc, String exitTitle, String exitDesc) {
        this.enterTitle = enterTitle;
        this.enterDesc = enterDesc;
        this.exitTitle = exitTitle;
        this.exitDesc = exitDesc;
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
