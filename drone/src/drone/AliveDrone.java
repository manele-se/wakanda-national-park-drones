package drone;


import org.eclipse.paho.client.mqttv3.IMqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.json.JSONObject;

import communication.Communication;

import java.nio.charset.StandardCharsets;
import java.util.LinkedList;
import java.util.Random;
import java.util.StringTokenizer;

public class AliveDrone {

    // haven't used bash script
    // thought like this

    public static void main(String[] args) throws MqttException {

        final String MISSION_ASSIGNMENT_TOPIC = "chalmers/dat220/group1/mission";
        final String IMAGE_TOPIC = "chalmers/dat220/group1/Images";

        System.out.println(args[0]);
        System.out.println(args[1]);
        IMqttClient mqttClient = null;

        Drone drone;

        if (args[0].equals("0")){
            drone = new AirDrone(args[1]);
        } else {
            drone = new LandDrone(args[1]);

        }

        // Work in tandem is the third argument
        if (args.length >= 3) {
            drone.workInTandem(args[2]);
        }

        Random random = new Random();
        drone.setPosition(random.nextDouble() * 0.06 -1.9638,random.nextDouble() * 0.08 + 34.699);
        drone.moveTo();

        mqttClient.subscribe(MISSION_ASSIGNMENT_TOPIC, 0, (topic, msg) -> {
            // Get the raw payload as a stream of bytes

            String duty = msg.getPayload().toString();
            String[] topicParts = duty.split("/");
            String droneName = topicParts[0];
            String Type = topicParts[1];

            for ( String droneInMission : drone.availableDrones)  {
                if (droneName  == droneInMission ) {
                    StringTokenizer coordinatex = new StringTokenizer(topicParts[2]);
                    StringTokenizer coordinatey = new StringTokenizer(topicParts[3]);
                    drone.positionX = Double.parseDouble(String.valueOf(coordinatex));
                    drone.positionY = Double.parseDouble(String.valueOf(coordinatey));
                }
            }
            if ( Type.equals("OBJECTDETECTION")) {
                drone.capturesImages();
            }
        });

    }
}
