package wakanda;

// Using Paho MQTT library: https://www.baeldung.com/java-mqtt-client
import org.eclipse.paho.client.mqttv3.*;

// Using JSON-Java: https://www.baeldung.com/java-org-json
import org.json.*;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import org.springframework.boot.SpringApplication;

public class Main {

    // The MQTT identity of the ground station
    private static final String PUBLISHER_ID = "chalmers-dat220-group1-groundstation";

    // Drones could send their locations using this topic pattern, for example:
    // drone/1/location
    // drone/72/location
    // drone/bob/location
    // drone/alice/location
    // The second part of the topic is the identity (name) of the drone
    // How MQTT topics and wildcards work: https://subscription.packtpub.com/book/application_development/9781787287815/1/ch01lvl1sec18/understanding-wildcards
    private static final String DRONES_LOCATION_TOPICS = "chalmers/dat220/group1/drone/+/location";

    // Data storage
    public static Map<String, MapObject> mappedObjects = new HashMap<String, MapObject>();

    public static void main(String[] args) throws MqttException {
	    // Next 6 lines copied from https://www.baeldung.com/java-mqtt-client
        IMqttClient mqttClient = new MqttClient("tcp://broker.hivemq.com:1883", PUBLISHER_ID);

        MqttConnectOptions options = new MqttConnectOptions();
        options.setAutomaticReconnect(true);
        options.setCleanSession(true);
        options.setConnectionTimeout(10);
        mqttClient.connect(options);

        String sendThisJson = "{\"latitude\":-1.948754,\"longitude\":34.700409}";
        byte[] sendTheseBytes = StandardCharsets.UTF_8.encode(sendThisJson).array();
        mqttClient.publish("chalmers/dat220/group1/drone/y/location", sendTheseBytes, 1, true);

        // When there is a message from a drone about location:
        mqttClient.subscribe(DRONES_LOCATION_TOPICS, 1, (topic, msg) -> {
            // Get the raw payload as a stream of bytes
            byte[] rawPayload = msg.getPayload();

            // Convert the stream of bytes to a string, containing JSON
            String jsonPayload = new String(rawPayload, StandardCharsets.UTF_8);

            // Parse the JSON string into a JsonObject
            JSONObject payload = new JSONObject(jsonPayload);

            // Get latitude and longitude from the payload
            double latitude = payload.getDouble("latitude");
            double longitude = payload.getDouble("longitude");

            // Get the drone identity
            // First split the topic into parts
            String[] topicParts = topic.split("/");
            String droneId = topicParts[4];

            System.out.println("Location of drone '" + droneId + "': " + latitude + "," + longitude);

            mappedObjects.put(droneId, new DroneInfo(latitude, longitude));
        });

        SpringApplication.run(WebServer.class, args);
    }
}
