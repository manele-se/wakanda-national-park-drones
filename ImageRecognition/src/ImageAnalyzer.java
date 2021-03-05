import org.eclipse.paho.client.mqttv3.IMqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONObject;

import java.util.Random;

public class ImageAnalyzer {

    // The MQTT identity of the ImageRecognition component

    private static final String PUBLISHER_ID = "chalmers-dat220-group1-ImageRecognizer";

    // Drone sends a message related to captured Image on this topic
    private static final String IMAGE_TOPIC = "chalmers/dat220/group1/+/+/photo";

    public static IMqttClient mqttClient;

    public static void main(String[] args) throws MqttException {

        String[] arr;   									// array declaration
        arr = new String[] { "Animal", "Plant", "Poacher" }; 	//  assuming will contain fixed elements
         int rnd = new Random().nextInt(arr.length);

        mqttClient.subscribe (IMAGE_TOPIC, 0, (topic, msg) -> {
            

        });
    }
}
