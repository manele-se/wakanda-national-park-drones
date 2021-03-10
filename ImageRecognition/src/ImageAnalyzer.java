import communication.Communication;
import org.eclipse.paho.client.mqttv3.IMqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONObject;

import java.util.Random;

public class ImageAnalyzer {

    // The MQTT identity of the ImageRecognition component

    private static final String PUBLISHER_ID = "chalmers-dat220-group1-ImageRecognizer";
    private static IMqttClient mqttClient;

    // Drone sends a message related to captured Image on this topic
    private static final String IMAGE_TOPIC = "chalmers/dat220/group1/+/+/photo";
    private static final String IMAGE_REC_TOPIC = "chalmers/dat220/group1/imagerec"; // Copied from the dashboard!

    public static void main(String[] args) throws MqttException {

        String[] arr;   									// array declaration
        arr = new String[] { "Animal", "Plant", "Poacher" }; 	//  assuming will contain fixed elements

        //connect with the public broker
        try {
            mqttClient = Communication.connect(PUBLISHER_ID );
        } catch (MqttException e) {
            e.printStackTrace();
        }
        mqttClient.subscribe (IMAGE_TOPIC, 0, (topic, msg) -> {

            int rnd = new Random().nextInt(arr.length);

            // Get the drone's identity and type
                String[] topicParts = topic.split("/");
                String DroneType = topicParts[3];
                String DroneIdentity = topicParts[4];

                JSONObject imageDetection = new JSONObject();
                imageDetection.put(DroneType, DroneIdentity);
                imageDetection.put("ObjectDetected", arr[rnd] );

            Thread t1 = new Thread(new RunnableImpl(imageDetection));
            t1.start();
    });

    }
    private static class RunnableImpl implements Runnable {

        JSONObject Images ;

        public RunnableImpl(JSONObject json ) {
            this.Images = json;
        }

        public void run()
        {
            try {
                Communication.send(mqttClient,IMAGE_REC_TOPIC, Images);
            } catch (MqttException e) {
                e.printStackTrace();
            }
        }
}
}