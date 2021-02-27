package drone;


import org.eclipse.paho.client.mqttv3.IMqttClient;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;

import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class Main {

    private static final String PUBLISHER_ID = UUID.randomUUID().toString();

    public static void main(String[] args) throws MqttException {


        AirDrone a1 = new AirDrone();
        a1.setName("alice");
        //a1.setAlive();
        a1.setPosition(300, 300);
        a1.moveTo();

        IMqttClient mqttClient = new MqttClient("tcp://localhost:1883", PUBLISHER_ID);

        MqttConnectOptions options = new MqttConnectOptions();
        options.setAutomaticReconnect(true);
        options.setCleanSession(true);
        options.setConnectionTimeout(10);
        try {
            mqttClient.connect(options);
        } catch (MqttException e) {
            e.printStackTrace();
        }

        CountDownLatch receivedSignal = new CountDownLatch(10);
        mqttClient.subscribe(AirDrone.DRONES_LOCATION_TOPICS, (topic, msg) -> {
            // Get the raw payload as a stream of bytes
            byte[] rawPayload = msg.getPayload();

            /*
            // Convert the stream of bytes to a string, containing JSON
            String jsonPayload = new String(rawPayload, StandardCharsets.UTF_8);

            // Parse the JSON string into a JsonObject
            JSONObject payload = new JSONObject(jsonPayload);

            // Get latitude and longitude from the payload
            double latitude = payload.getDouble("latitude");
            double longitude = payload.getDouble("longitude");
            */

            receivedSignal.countDown();
            System.out.println("final");
        });
        try {
            receivedSignal.await(1, TimeUnit.MINUTES);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
