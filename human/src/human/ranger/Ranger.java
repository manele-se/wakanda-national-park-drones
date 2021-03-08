package human.ranger;

import communication.Communication;
import org.eclipse.paho.client.mqttv3.IMqttClient;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.util.Random;

//A component (implemented as a separate process) that ‘simulates’ one (or more) person(s):
// this/these person(s) ‘walks’ around in a virtual park.
// This process should generate data for movement, heart-beat signals (if the person is a ranger).
public class Ranger {

    // Limits to location
    private final static double MIN_LATITUDE = -1.9638;
    private final static double MAX_LATITUDE = -1.9068;
    private final static double MIN_LONGITUDE = 34.699;
    private final static double MAX_LONGITUDE = 34.787;

    // Helper functions to get random location
    private final static Random random = new Random();
    private static double randomDouble(double min, double max) {
        return min + (max - min) * random.nextDouble();
    }
    private static double randomLatitude() {
        return randomDouble(MIN_LATITUDE, MAX_LATITUDE);
    }
    private static double randomLongitude() {
        return randomDouble(MIN_LONGITUDE, MAX_LONGITUDE);
    }

    // Ranger information
    protected String name;
    protected IMqttClient client = null;
    // Current location
    protected double latitude;
    protected double longitude;
    // Destination
    protected double targetLatitude;
    protected double targetLongitude;
    // Number of steps left to "walk"
    protected int stepsLeft;

    // Publisher ID root
    private static final String PUBLISHER_ID = "chalmers-dat220-group1-human";

    public Ranger(String n) throws MqttException {
        // Store name
        this.name = n;
        // Connect to MQTT hub
        this.client = Communication.connect(PUBLISHER_ID + "-" + name.toLowerCase());
        // Start at a random location
        this.latitude = randomLatitude();
        this.longitude = randomLongitude();
    }

    // Function that measures distance from current location to the target location
    // SQRT((x1-x2)² + (y1-y2)²)
    private double distanceToTarget() {
        return Math.sqrt((targetLatitude - latitude) * (targetLatitude - latitude) + (targetLongitude - longitude) * (targetLongitude - longitude));
    }

    // Function that picks a new target location randomly and calculates how many steps it takes to "walk" there
    private void pickNewDestination() {
        this.targetLatitude = randomLatitude();
        this.targetLongitude = randomLongitude();
        // Count how many steps left to "walk"
        // Multiplying by 10000 to make it look "real"
        this.stepsLeft = (int)(distanceToTarget() * 10000);
    }

    // This person "walks" around in the virtual park.
    public void travel() throws InterruptedException, MqttException {
        // Loop forever!
        while (true) {
            // Pick a new destination
            pickNewDestination();

            // Walk there one step at a time until arriving at the destination
            while (stepsLeft > 0) {
                // Move one step forward
                latitude += (targetLatitude - latitude) / stepsLeft;
                longitude += (targetLongitude - longitude) / stepsLeft;

                // Decrease number of steps left
                --stepsLeft;

                // Send the location over MQTT
                sendLocation();

                // Only used for testing...
                //if (random.nextDouble() > 0.95) {
                //    sendPhoto();
                //}

                // Sleep for a while
                Thread.sleep(500);
            }
        }
    }

    // Send location signal
    public void sendLocation() throws MqttException {
        JSONObject positionToSend = new JSONObject();
        positionToSend.put("latitude", this.latitude);
        positionToSend.put("longitude", this.longitude);

        String thisRangerLocationTopic = "chalmers/dat220/group1/ranger/" + this.name + "/location";

        Communication.send(client, thisRangerLocationTopic, positionToSend);
    }

    // Only used for testing
    /*public void sendPhoto() throws MqttException {
        JSONObject payload = new JSONObject();
        payload.put("photo", true);

        String thisRangerPhotoTopic = "chalmers/dat220/group1/ranger/" + this.name + "/photo";

        Communication.send(client, thisRangerPhotoTopic, payload);
    }*/
}
