package communication;

import org.eclipse.paho.client.mqttv3.*;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;

public class Communication {
    private static final String BROKER_URL = "tcp://test.mosquitto.org:1883";

    public static JSONObject getJson(MqttMessage message) {
        // Convert the stream of bytes to a string, containing JSON
        String jsonPayload = getString(message);

        // Parse the JSON string into a JsonObject
        JSONObject payload = new JSONObject(jsonPayload);

        return payload;
    }

    public static String getString(MqttMessage message) {
        // Get the raw payload as a stream of bytes
        byte[] rawPayload = message.getPayload();

        // Convert the stream of bytes to a string, containing JSON
        String stringPayload = new String(rawPayload, StandardCharsets.UTF_8);

        return stringPayload;
    }

    public static void send(IMqttClient mqttClient, String topic, String payload) throws MqttException {
        byte[] sendTheseBytes = StandardCharsets.UTF_8.encode(payload).array();
        mqttClient.publish(topic, sendTheseBytes, 0, false);
    }

    public static void send(IMqttClient mqttClient, String topic, JSONObject json) throws MqttException {
        String sendThisJson = json.toString();
        send(mqttClient, topic, sendThisJson);
    }

    public static IMqttClient connect(String publisherId) throws MqttException {
        // Next 6 lines copied from https://www.baeldung.com/java-mqtt-client
        IMqttClient mqttClient = new MqttClient(BROKER_URL, publisherId);
        MqttConnectOptions options = new MqttConnectOptions();
        options.setAutomaticReconnect(true);
        options.setCleanSession(true);
        options.setConnectionTimeout(10);
        mqttClient.connect(options);

        // We should close the MQTT connection properly when the program shuts down
        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                try {
                    mqttClient.disconnect();
                    mqttClient.close();
                }
                catch (MqttException e) {
                    e.printStackTrace();
                }
            }
        });

        return mqttClient;
    }
}
