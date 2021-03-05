package drone;


import org.eclipse.paho.client.mqttv3.MqttException;

import java.util.Random;

public class AliveDrone {

    // haven't used bash script
    // thought like this
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

        // Considering the logic of travel and moveTo, thought it would be like this
        // pseudo code
        // - in drone:
        // subscribe the mqtt from mission handler{
        //  use the translate function to set drones position
        // }
        // - in the main function/ Drone:
        // while(true){
        //  while(timeToTravel()){ // continuous to check whether the drone has a mission
        //      travel();
        //      }
        //  moveTo(); // have a mission
        // }
        // two while, a little complicated, but no idea about an easier way to implement it.

        Random random = new Random();
        drone.setPosition(random.nextDouble() * 0.06 - 1.9638, random.nextDouble() * 0.08 + 34.699);
        drone.moveTo();
    }
}
