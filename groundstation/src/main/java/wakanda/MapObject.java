package wakanda;

public class MapObject {
    private final double latitude;
    private final double longitude;
    private final String markerUrl;
    private final int markerSize;
    private final int zIndex;
    private String partner;

    public MapObject(double latitude, double longitude, String markerUrl, int markerSize, int zIndex, String partner) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.markerUrl = markerUrl;
        this.markerSize = markerSize;
        this.zIndex = zIndex;
        this.partner = partner;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public String getMarkerUrl() {
        return markerUrl;
    }

    public String getPartner() {
        return partner;
    }

    public int getMarkerSize() {
        return markerSize;
    }

    public int getZIndex() {
        return zIndex;
    }

    public void setPartner(String partner) {
        this.partner = partner;
    }
}
