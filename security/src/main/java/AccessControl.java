import communication.Communication;
import org.eclipse.paho.client.mqttv3.IMqttClient;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicReference;

public class AccessControl {
    // The MQTT identity of the security component
    private static final String PUBLISHER_ID = "chalmers-dat220-group1-security";

    // The dashboards sends a message on this topic when a user wants to login
    private static final String SIGNIN_TOPIC = "chalmers/dat220/group1/signin";

    // The security component sends a message on this topic to return the results of a login attempt
    private static final String ACCESS_TOPIC = "chalmers/dat220/group1/access";

    public static void main(String[] args) throws MqttException {
        IMqttClient mqttClient = Communication.connect(PUBLISHER_ID);

        // Thread-safe response handling. It is not possible to publish messages from the
        // same thread as the subscriber. So we use a semaphore and an atomic string
        // reference to communicate between the subscriber, and the main loop, who will
        // send the response back to the dashboard.
        final AtomicReference<String> response = new AtomicReference<>();
        final Semaphore responseSemaphore = new Semaphore(0);

        mqttClient.subscribe(SIGNIN_TOPIC, 0, (topic, msg) -> {
            System.out.println("Received message on topic " + SIGNIN_TOPIC);

            // Get a JSON object from the payload
            JSONObject payload = Communication.getJson(msg);

            String username = payload.getString("username");
            String password = payload.getString("password");

            // Send the response
            // Is the password literally "correct"?
            if ("correct".equals(password)) {
                response.set("signedIn");
                System.out.println("User " + username + " has signed in correctly");
            }
            else {
                response.set("denied");
                System.out.println("User " + username + " was denied access");
            }

            responseSemaphore.release();
        });

        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                System.out.println("Shutting down");
                try {
                    System.out.println("Unsubscribing");
                    mqttClient.unsubscribe(SIGNIN_TOPIC);
                    System.out.println("Disconnecting");
                    mqttClient.disconnect();
                    System.out.println("Closing");
                    mqttClient.close();
                }
                catch (MqttException e) {
                    e.printStackTrace();
                }
            }
        });

        while (true) {
            try {
                responseSemaphore.acquire();
                Communication.send(mqttClient, ACCESS_TOPIC, response.get());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
