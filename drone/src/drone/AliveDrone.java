package drone;


import communication.Communication;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.json.JSONObject;

import java.util.Random;

public class AliveDrone {

    // haven't used bash script
    // thought like this
    private static final String MISSON_TOPICS = "chalmers/dat220/group1/+/+/mission";
    public static void main(String[] args) throws MqttException {


        System.out.println(args[0]);
        System.out.println(args[1]);
        Drone drone;


        if (args[0].equals("0")) {
            drone = new AirDrone(args[1]);
        } else {
            drone = new LandDrone(args[1]);
        }

        // Work in tandem is the third argument
        if (args.length >= 3) {
            drone.workInTandem(args[2]);
        }

        drone.client.subscribe(MISSON_TOPICS, 0, (topic, msg) -> {
            // Get the drone's identity
            // First split the topic into parts
            String[] topicParts = topic.split("/");
            String objectType = topicParts[3];
            String objectId = topicParts[4];

            // System.out.println(objectId + " " + objectType);
            if(objectId.equals(drone.name)) {
                JSONObject payload = Communication.getJson(msg);

                // Get latitude and longitude from the payload
                double latitude = payload.getDouble("latitude");
                double longitude = payload.getDouble("longitude");

                drone.setPosition(latitude, longitude);
            }
        });
        while(true) {
            if (drone.timeToTravel()) {
                drone.travel();
            }
            drone.moveTo();
        }
    }
}
