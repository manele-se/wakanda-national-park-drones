package wakanda;

// Using Paho MQTT library: https://www.baeldung.com/java-mqtt-client
import org.eclipse.paho.client.mqttv3.IMqttClient;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.json.JSONObject;
import org.springframework.boot.SpringApplication;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

// Using JSON-Java: https://www.baeldung.com/java-org-json

public class Dashboard {

    // The MQTT identity of the ground station
    private static final String PUBLISHER_ID = "chalmers-dat220-group1-groundstation";

    // Drones could send their locations using this topic pattern, for example:
    // chalmers/dat220/group1/drone/alice/location
    // chalmers/dat220/group1/drone/bob/location
    // Rangers send their locations using this topic pattern:
    // chalmers/dat220/group1/ranger/siouxsie/location
    // chalmers/dat220/group1/ranger/robert/location
    // The third part of the topic is the type of object (drone, ranger, ...)
    // The fourth part of the topic is the identity (number) of the drone
    // How MQTT topics and wildcards work: https://subscription.packtpub.com/book/application_development/9781787287815/1/ch01lvl1sec18/understanding-wildcards
    private static final String LOCATION_TOPICS = "chalmers/dat220/group1/+/+/location";

    // Marker images
    private static final String DRONE_MARKER_URL = "//maps.google.com/mapfiles/kml/pal4/icon45.png";
    private static final String RANGER_MARKER_URL = "//maps.google.com/mapfiles/ms/micons/police.png";
    private static final String UNKNOWN_MARKER_URL = "//maps.google.com/mapfiles/ms/micons/question.png";
    private static final String FIRE_MARKER_URL = "//maps.google.com/mapfiles/ms/micons/firedept.png;";

    // Data storage
    public static final Map<String, MapObject> mappedObjects = new HashMap<>();

    public static void main(String[] args) throws MqttException {
        // Create a map of marker urls per object type
        Map<String, String> objectTypeMarkers = new HashMap<>();
        objectTypeMarkers.put("drone", DRONE_MARKER_URL);
        objectTypeMarkers.put("ranger", RANGER_MARKER_URL);

	    // Next 6 lines copied from https://www.baeldung.com/java-mqtt-client
        IMqttClient mqttClient = new MqttClient("tcp://broker.hivemq.com:1883", PUBLISHER_ID);

        MqttConnectOptions options = new MqttConnectOptions();
        options.setAutomaticReconnect(true);
        options.setCleanSession(true);
        options.setConnectionTimeout(10);
        mqttClient.connect(options);

        // When there is a message from a drone about location:
        mqttClient.subscribe(LOCATION_TOPICS, 0, (topic, msg) -> {
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
            String objectType = topicParts[3];
            String objectId = topicParts[4];

            System.out.println("Location of " + objectType + " '" + objectId + "': " + latitude + "," + longitude);

            String mapObjectKey = objectType + "_" + objectId;
            String markerUrl = objectTypeMarkers.getOrDefault(objectType, UNKNOWN_MARKER_URL);
            mappedObjects.put(mapObjectKey, new MapObject(latitude, longitude, markerUrl));
        });

        // We should close the MQTT connection properly when the program shuts down
        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                System.out.println("Shutting down");
                try {
                    mqttClient.unsubscribe(LOCATION_TOPICS);
                    mqttClient.disconnect();
                    mqttClient.close();
                }
                catch (MqttException e) {
                    e.printStackTrace();
                }
            }
        });

        // Test sending a location for drone '123', in the middle of Serengeti.
        // This is how sending locations for drone should work.
        // The JSONObject should contain the latitude and longitude for the drone, stored in variables.
       /* JSONObject positionToSend = new JSONObject();
        positionToSend.put("latitude", -1.948754);
        positionToSend.put("longitude", 34.700409);

        String sendThisJson = positionToSend.toString();
        byte[] sendTheseBytes = StandardCharsets.UTF_8.encode(sendThisJson).array();

        String thisDroneIdentity = "123";
        String thisDroneLocationTopic = "chalmers/dat220/group1/drone/" + thisDroneIdentity + "/location";
        mqttClient.publish(thisDroneLocationTopic, sendTheseBytes, 0, false);*/

        // Start the Spring Boot Web application
        SpringApplication.run(WebServer.class, args);
    }
}
