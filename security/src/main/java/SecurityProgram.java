import org.eclipse.paho.client.mqttv3.IMqttClient;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;

public class SecurityProgram {
    // The MQTT identity of the security component
    private static final String PUBLISHER_ID = "chalmers-dat220-group1-security";

    // The dashboards sends a message on this topic when a user wants to login
    private static final String SIGNIN_TOPIC = "chalmers/dat220/group1/signin";

    // This component sends a message on this topic to return the results of a login attempt
    private static final String ACCESS_TOPIC = "chalmers/dat220/group1/access";

    public static void main(String[] args) throws MqttException {
        // Next 6 lines copied from https://www.baeldung.com/java-mqtt-client
        IMqttClient mqttClient = new MqttClient("tcp://broker.hivemq.com:1883", PUBLISHER_ID);
        MqttConnectOptions options = new MqttConnectOptions();
        options.setAutomaticReconnect(true);
        options.setCleanSession(true);
        options.setConnectionTimeout(10);
        mqttClient.connect(options);

        mqttClient.subscribe(SIGNIN_TOPIC, 0, (topic, msg) -> {
            // Get a JSON object from the payload
            byte[] rawPayload = msg.getPayload();
            String jsonPayload = new String(rawPayload, StandardCharsets.UTF_8);
            JSONObject payload = new JSONObject(jsonPayload);

            String username = payload.getString("username");
            String password = payload.getString("password");
            String sessionId = payload.getString("sessionId");

            // Send the response
            JSONObject response = new JSONObject();
            response.put("sessionId", sessionId);
            response.put("username", username);

            // Is the password literally "correct"?
            if ("correct".equals(password)) {
                response.put("result", "signedIn");
                System.out.println("User " + username + " has signed in correctly");
            }
            else {
                response.put("result", "denied");
                System.out.println("User " + username + " was denied access");
            }

            String sendThisJson = response.toString();
            byte[] sendTheseBytes = StandardCharsets.UTF_8.encode(sendThisJson).array();

            mqttClient.publish(ACCESS_TOPIC, sendTheseBytes, 0, false);
        });

        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                System.out.println("Shutting down");
                try {
                    mqttClient.unsubscribe(SIGNIN_TOPIC);
                    mqttClient.disconnect();
                    mqttClient.close();
                }
                catch (MqttException e) {
                    e.printStackTrace();
                }
            }
        });

        while (true) {
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
            }
        }
    }
}
