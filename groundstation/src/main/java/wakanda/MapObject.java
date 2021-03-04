package wakanda;

public class MapObject {
    private double latitude;
    private double longitude;
    private String markerUrl;
    private String partner;

    public MapObject(double latitude, double longitude, String markerUrl, String partner) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.markerUrl = markerUrl;
        this.partner = partner;
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

    public void setMarkerUrl(String markerUrl) {
        this.markerUrl = markerUrl;
    }

    public String getPartner() {
        return partner;
    }

    public void setPartner(String partner) {
        this.partner = partner;
    }
}
