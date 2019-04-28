package eky.beaconmaps.model;

/**
 * Created by Enes Kamil YILMAZ on 26.04.2019.
 */

public class CompanyData {

    private String companyName, title, enterTitle, enterMessage, exitTitle, exitMessage;

    public CompanyData() { }

    public CompanyData(String companyName, String title, String enterTitle, String enterMessage, String exitTitle, String exitMessage) {
        this.companyName = companyName;
        this.title = title;
        this.enterTitle = enterTitle;
        this.enterMessage = enterMessage;
        this.exitTitle = exitTitle;
        this.exitMessage = exitMessage;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getEnterTitle() {
        return enterTitle;
    }

    public void setEnterTitle(String enterTitle) {
        this.enterTitle = enterTitle;
    }

    public String getEnterMessage() {
        return enterMessage;
    }

    public void setEnterMessage(String enterMessage) {
        this.enterMessage = enterMessage;
    }

    public String getExitTitle() {
        return exitTitle;
    }

    public void setExitTitle(String exitTitle) {
        this.exitTitle = exitTitle;
    }

    public String getExitMessage() {
        return exitMessage;
    }

    public void setExitMessage(String exitMessage) {
        this.exitMessage = exitMessage;
    }
}
