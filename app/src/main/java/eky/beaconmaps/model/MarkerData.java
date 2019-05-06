package eky.beaconmaps.model;

/**
 * Created by Enes Kamil YILMAZ on 6.05.2019.
 */

public class MarkerData {

    private BeaconData mBeaconData;
    private float distance;

    public MarkerData(BeaconData beaconData, float distance) {
        mBeaconData = beaconData;
        this.distance = distance;
    }

    public BeaconData getBeaconData() {
        return mBeaconData;
    }

    public void setBeaconData(BeaconData beaconData) {
        mBeaconData = beaconData;
    }

    public float getDistance() {
        return distance;
    }

    public void setDistance(float distance) {
        this.distance = distance;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null)
            return false;

        if (o == this)
            return true;

        if (getClass() != o.getClass())
            return super.equals(o);

        MarkerData other = (MarkerData) o;

        if (mBeaconData.getLocation() != null && other.mBeaconData.getLocation() != null)
            if (mBeaconData.getCompanyName() != null && other.mBeaconData.getCompanyName() != null)
            return (mBeaconData.getLocation().equals(other.getBeaconData().getLocation()))
                    && (mBeaconData.getCompanyName().equals(other.mBeaconData.getCompanyName()));

        return false;
    }

}
