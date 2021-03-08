package DecisionMaker;


import drone.Drone;
import org.eclipse.paho.client.mqttv3.*;
import org.json.JSONObject;
import communication.Communication;
import java.util.Random;


public class MissionController {

    // The MQTT identity of the Mission component
    private static final String PUBLISHER_ID = "chalmers-dat220-group1-mission" ;

    // dashboard sends a message on this topic when a ranger assign mission to drones
    private static IMqttClient mqttClient;
    private static String DroneType;
    private static String DroneName;

    public static void delegateMissionRequest(String droneType, String droneName ) throws MqttException {

        Random random = new Random();
        // latitude coordinate
        double x = (19.068 + (random.nextDouble() * (1.9638 - 1.9068))) * -1;
        // longitude coordinates
        double y = 34.699 + (random.nextDouble() * (34.787 - 34.699));
        JSONObject sendCoordinates = new JSONObject();
        sendCoordinates.put("latitude", x);
        sendCoordinates.put("longitude", y);

        String sendJson = sendCoordinates.toString();
        String topic = "“chalmers/dat220/group1/" + droneType +  "/" +  droneName  +  "/mission";

        try {
            Communication.send(mqttClient, topic, sendJson );
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    private static void main(String[] args) throws MqttException {

        //connect with the public broker
        try {
            mqttClient = Communication.connect(PUBLISHER_ID );
        } catch (MqttException e) {
            e.printStackTrace();
        }
            // args[0] is the file name
            // args[1] is the drone type : airdrone or landdrone
             // args[2] is the drone name

        if (args.length > 1 &&  (args[1].equals("airdrone") |  args[1].equals("landdrone" ))) {
                DroneType = args[1];
                DroneName = args[2];

                delegateMissionRequest(DroneType, DroneName);

            } else {
                System.out.println(" Specify existing drone to assign mission to ");

    }

}
}


