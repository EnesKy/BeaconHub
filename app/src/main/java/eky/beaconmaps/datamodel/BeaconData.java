package eky.beaconmaps.datamodel;

public class BeaconData {

    String uuid;
    String type;
    String color;
    String major;
    String minor;
    String location;

    public BeaconData() {}

    public BeaconData(String uuid, String major, String minor, String location) {
        this.uuid = uuid;
        //this.type = type;
        this.major = major;
        this.minor = minor;
        //this.color = color;
        this.location = location;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public void setuuid(String uuid) {
        this.uuid = uuid;
    }

    public void setMajor(String major) {
        this.major = major;
    }

    public void setMinor(String minor) {
        this.minor = minor;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getuuid() {
        return uuid;
    }

    public String getType() {
        return type;
    }

    public String getColor() {
        return color;
    }

    public String getMajor() {
        return major;
    }

    public String getMinor() {
        return minor;
    }

    public String getLocation() {
        return location;
    }
}
