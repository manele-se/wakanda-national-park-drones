package wakanda;

public class MapObject {
    private double latitude;
    private double longitude;
    private String markerUrl;

    public MapObject(double latitude, double longitude, String markerUrl) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.markerUrl = markerUrl;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getMarkerUrl() {
        return markerUrl;
    }

    public void setMarkerUrl(String color) {
        this.markerUrl = color;
    }
}
