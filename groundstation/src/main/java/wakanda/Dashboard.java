package wakanda;

// Using Paho MQTT library: https://www.baeldung.com/java-mqtt-client
import communication.Communication;
import org.eclipse.paho.client.mqttv3.*;
import org.json.JSONObject;
import org.springframework.boot.SpringApplication;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;

// Using JSON-Java: https://www.baeldung.com/java-org-json

public class Dashboard {

    // The MQTT client
    public static IMqttClient mqttClient;

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
    private static final String TANDEM_TOPICS = "chalmers/dat220/group1/+/+/partner";
    private static final String IMAGE_REC_TOPIC = "chalmers/dat220/group1/imagerec"; // Not sure what topic this will be yet!

    // Marker images
    private static final String LANDDRONE_MARKER_URL = "//maps.google.com/mapfiles/kml/pal4/icon45.png";
    private static final String AIRDRONE_MARKER_URL = "//maps.google.com/mapfiles/ms/icons/helicopter.png";
    private static final String RANGER_MARKER_URL = "//maps.google.com/mapfiles/ms/micons/police.png";
    private static final String UNKNOWN_MARKER_URL = "//maps.google.com/mapfiles/ms/micons/question.png";
    private static final String FIRE_MARKER_URL = "//maps.google.com/mapfiles/ms/micons/firedept.png";
    private static final String POACHER_MARKER_URL = "/poacher.svg";
    private static final String ANIMAL_MARKER_URL = "/cat.svg";
    private static final String PLANT_MARKER_URL = "/tree.svg"; // "//maps.google.com/mapfiles/ms/icons/tree.png";

    // Data storage
    public static final Map<String, MapObject> mappedObjects = new TreeMap<>();
    public static final Map<String, String> tandemMap = new HashMap<>();
    public static int nextImageRecognitionId = 1;

    private static final Map<String, String> objectTypeMarkers = new HashMap<>();

    public static void main(String[] args) throws MqttException {
        // Create a map of marker urls per object type
        objectTypeMarkers.put("airdrone", AIRDRONE_MARKER_URL);
        objectTypeMarkers.put("landdrone", LANDDRONE_MARKER_URL);
        objectTypeMarkers.put("ranger", RANGER_MARKER_URL);
        objectTypeMarkers.put("fire", FIRE_MARKER_URL);
        objectTypeMarkers.put("poacher", POACHER_MARKER_URL);
        objectTypeMarkers.put("animal", ANIMAL_MARKER_URL);
        objectTypeMarkers.put("plant", PLANT_MARKER_URL);

	    // Next 6 lines copied from https://www.baeldung.com/java-mqtt-client
        mqttClient = new MqttClient("tcp://broker.emqx.io:1883", PUBLISHER_ID);

        MqttConnectOptions options = new MqttConnectOptions();
        options.setAutomaticReconnect(true);
        options.setCleanSession(true);
        options.setConnectionTimeout(10);
        mqttClient.connect(options);

        // When there is a message from a drone about location:
        mqttClient.subscribe(LOCATION_TOPICS, 0, (topic, msg) -> {
            // Get the object identity
            // First split the topic into parts
            String[] topicParts = topic.split("/");
            String objectType = topicParts[3];
            String objectId = topicParts[4];

            JSONObject payload = Communication.getJson(msg);

            // Get latitude and longitude from the payload
            double latitude = payload.getDouble("latitude");
            double longitude = payload.getDouble("longitude");

            locationChanged(objectType, objectId, latitude, longitude);
        });

        // Subscribe to information about drones working in tandem
        mqttClient.subscribe(TANDEM_TOPICS, 0, (topic, msg) -> {
            // Get the drone's identity
            // First split the topic into parts
            String[] topicParts = topic.split("/");
            String objectType = topicParts[3];
            String objectId = topicParts[4];
            String sender = objectType + "_" + objectId;

            // Get the partner's identity
            JSONObject json = Communication.getJson(msg);
            String partner = json.getString("partner");

            tandemChanged(sender, partner);
        });

        mqttClient.subscribe(IMAGE_REC_TOPIC, 0, (topic, msg) -> {
            // Assuming the Image Recognition process sends JSON:
            JSONObject payload = Communication.getJson(msg);

            // What was discovered? Animal, Plant or Poacher.
            String objectDetected = payload.getString("ObjectDetected");

            // Who discovered it?
            String detectorType, detectorId, detectorKey;
            if (payload.has("airdrone")) {
                detectorType = "airdrone";
            }
            else if (payload.has("landdrone")) {
                detectorType = "landdrone";
            }
            else if (payload.has("ranger")) {
                detectorType = "ranger";
            }
            else {
                System.out.println("Error: Received an image recognition object from unknown object type");
                return;
            }
            detectorId = payload.getString(detectorType);

            System.out.println("Photo of " + objectDetected + " was taken by " + detectorType + " " + detectorId);

            // Get the drone object that took the picture
            detectorKey = detectorType + "_" + detectorId;
            MapObject discoveredBy = mappedObjects.get(detectorKey);

            // Get that drone's current location
            double latitude = discoveredBy.getLatitude();
            double longitude = discoveredBy.getLongitude();

            // Get the image icon URL
            String markerUrl = objectTypeMarkers.get(objectDetected.toLowerCase());

            // Create a new key for the photo on the map
            String newKey = String.format("imagerec-%05d", nextImageRecognitionId);
            nextImageRecognitionId++;

            // Create the map object on the drone's location, with the correct marker image
            MapObject image = new MapObject(latitude, longitude, markerUrl, 48, 1,null);

            mappedObjects.put(newKey, image);
        });

        // Start the Spring Boot Web application
        SpringApplication.run(WebServer.class, args);
    }

    private static void locationChanged(String objectType, String objectId, double latitude, double longitude) {
        String mapObjectKey = objectType + "_" + objectId;
        String markerUrl = objectTypeMarkers.getOrDefault(objectType, UNKNOWN_MARKER_URL);
        String currentPartner = tandemMap.getOrDefault(mapObjectKey, null);
        mappedObjects.put(mapObjectKey, new MapObject(latitude, longitude, markerUrl, 32, 2, currentPartner));
    }

    private static void tandemChanged(String sender, String partner) {
        tandemMap.put(sender, partner);
        if (mappedObjects.containsKey(sender)) {
            MapObject current = (MapObject)mappedObjects.get(sender);
            current.setPartner(partner);
        }
    }
}
