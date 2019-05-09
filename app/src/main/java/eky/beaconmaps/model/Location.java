package eky.beaconmaps.model;

/**
 * Created by Enes Kamil YILMAZ on 9.05.2019.
 */
public class Location {

    Double lat, lng;

    public Location() {}

    public Location(Double lat, Double lng) {
        this.lat = lat;
        this.lng = lng;
    }

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Double getLng() {
        return lng;
    }

    public void setLng(Double lng) {
        this.lng = lng;
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

        Location other = (Location) o;

        return lat == other.lat && lng == other.lng;
    }
}
