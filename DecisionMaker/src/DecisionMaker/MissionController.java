package DecisionMaker;


import org.eclipse.paho.client.mqttv3.*;
import org.json.JSONObject;
import communication.Communication;
import java.util.Random;


public class MissionController {

    // The MQTT identity of the Mission component
    private static final String PUBLISHER_ID = "chalmers-dat220-group1-mission" ;

    // dashboard sends a message on this topic when a ranger assign mission to drones
    public static IMqttClient mqttClient;

    private String DroneName;

    public MissionController(String DroneIdentity){
        this.DroneName = DroneIdentity;

        //connect with the public broker
        try {
            this.mqttClient = Communication.connect(PUBLISHER_ID );
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    public static void delegateMissionRequest ( String droneName) throws MqttException {

        Random random = new Random();
        // latitude coordinate
        double x = (19.068 + (random.nextDouble() * (1.9638 - 1.9068))) * -1;
        // longitude coordinates
        double y = 34.699 + (random.nextDouble() * (34.787 - 34.699));
        JSONObject sendCoordinates = new JSONObject();
        sendCoordinates.put("latitude", x);
        sendCoordinates.put("longitude", y);

        String sendJson = sendCoordinates.toString();
        String topic = "“chalmers/dat220/group1/" + droneName + "/mission";

        try {
            Communication.send(mqttClient, topic, sendJson );
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws MqttException{
        delegateMissionRequest("alice");
    }

}


