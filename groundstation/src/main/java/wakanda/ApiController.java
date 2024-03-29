package wakanda;

import communication.Communication;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.json.JSONObject;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

@RestController
@RequestMapping("/api")
public class ApiController {
    // The dashboards sends a message on this topic when a user wants to login
    private static final String SIGNIN_TOPIC = "chalmers/dat220/group1/signin";

    // The security component sends a message on this topic to return the results of a login attempt
    private static final String ACCESS_TOPIC = "chalmers/dat220/group1/access";

    // API method for getting all map objects
    // GET /api/mapstate
    // No parameters
    // Returns a JSON object with all map objects:
    // { "drone_bob" : { "latitude": 1.2, "longitude": 2.3 } }
    @GetMapping("/mapstate")
    public Map<String, MapObject> getAllMapObjects() {
        return Dashboard.mappedObjects;
    }

    // API method for signing in
    // Publishes a message to the Access Control component, and waits for a response.
    // See the SecurityProgram for more details.
    // POST /api/signin
    // Returns "signedIn", "denied", "timeout" or "error"
    @PostMapping("/signin")
    public String signin(String username, String password) throws MqttException {

        Semaphore receivedResponse = new Semaphore(0);
        AtomicReference<String> response = new AtomicReference<>();

        Dashboard.mqttClient.subscribe(ACCESS_TOPIC, 0, (topic, msg) -> {
            Dashboard.mqttClient.unsubscribe(ACCESS_TOPIC);
            String payload = Communication.getString(msg);
            response.set(payload);
            receivedResponse.release();
        });

        // Send username and password through MQTT to the Access Control
        JSONObject signinRequest = new JSONObject();
        //password in a "real world scenario" should be encrypted
        signinRequest.put("username", username);
        signinRequest.put("password", password);

        Communication.send(Dashboard.mqttClient, SIGNIN_TOPIC, signinRequest);

        try {
            if (receivedResponse.tryAcquire(1, 10, TimeUnit.SECONDS)) {
                String result = response.get();
                return response.get();
            }
            else {
                return "timeout";
            }
        } catch (InterruptedException e) {
            return "error";
        }
    }
}

//TODO: assign mission