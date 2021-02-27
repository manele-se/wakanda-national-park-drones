package wakanda;

public class DroneInfo extends MapObject {
    public DroneInfo(double latitude, double longitude) {
        super(latitude, longitude);
    }

    @Override
    public String getColor() {
        return "blue";
    }
}
