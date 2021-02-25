package wakanda;

import org.eclipse.paho.client.mqttv3.*;

public class Main {

    // The MQTT identity of the ground station
    private static final String PUBLISHED_ID = "groundstation";

    public static void main(String[] args) throws MqttException {
	    // write your code here
        IMqttClient publisher = new MqttClient("tcp://localhost:1883", PUBLISHED_ID);
    }
}
