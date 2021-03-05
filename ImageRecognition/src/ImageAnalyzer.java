import org.eclipse.paho.client.mqttv3.IMqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;

public class ImageAnalyzer {

    // The MQTT identity of the ImageRecognition component

    private static final String PUBLISHER_ID = "chalmers-dat220-group1-ImageRecognizer";

    // Drone sends a message related to captured Image on this topic
    private static final String IMAGE_TOPIC = "chalmers/dat220/group1/Images";

    public static IMqttClient mqttClient;

    public static void main(String[] args) throws MqttException {

        mqttClient.subscribe (IMAGE_TOPIC, 0, (topic, msg) -> {
            // Get the raw payload as a stream of bytes
            byte[] photo = msg.getPayload();


        });
    }
}
