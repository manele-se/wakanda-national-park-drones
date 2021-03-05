package DecisionMaker;


import org.eclipse.paho.client.mqttv3.*;
import org.json.JSONObject;
import communication.Communication;

import java.nio.charset.StandardCharsets;
import java.util.Random;


public class MissionHandler {

    // The MQTT identity of the Mission component
    private static final String PUBLISHER_ID = "chalmers-dat220-group1-mission" ;

    // dashboard sends a message on this topic when a ranger assign mission to drones
    private static final String MISSION_TOPIC = "chalmers/dat220/group1/delegate";
    private static  final String MISSION_ASSIGNMENT_TOPIC = "chalmers/dat220/group1/mission";

    public static IMqttClient mqttClient;

    public static void main(String[] args) throws MqttException {

    // When there is a message from a drone about what type of mission:
     mqttClient.subscribe (MISSION_TOPIC, 0, (topic, msg) -> {

        byte[] missionType = msg.getPayload();

         String[] topicParts = missionType.toString().split("");
         String Type = topicParts[0];
         String droneName = topicParts[1];

         if ( Type.equals("SURVEILLANCE") )  {
             delegateMissionRequest(droneName, Type, -1.5,34);

         } else if ( Type.equals("OBJECTDETECTION")) {
             delegateMissionRequest(droneName, Type, -1.5,34);
         }

    });
    }

    public static void delegateMissionRequest(String droneName, String typeMission, double x, double y) throws MqttException {

        MqttMessage message = new MqttMessage();
        String missionDetails = droneName + "/" + typeMission + "/" + x +"/" + y;
        message.setPayload(missionDetails.getBytes());
        mqttClient.publish(MISSION_ASSIGNMENT_TOPIC, message);
    }

  //  public void AssignMission(Drone device, String name) {

   //     if (this.positionX != device.getXmovement() && this.positionY != device.getYmovement()) {

   //     }

    //    Random r = new Random();
       // this.positionX= X;
     //   this.positionY= Y;

   // }

}


