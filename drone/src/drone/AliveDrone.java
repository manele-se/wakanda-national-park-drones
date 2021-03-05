package drone;


import org.eclipse.paho.client.mqttv3.MqttException;

import java.util.Random;

public class AliveDrone {

    // haven't used bash script
    // thought like this
    public static void main(String[] args) throws MqttException {

        /*
        System.out.println(args[0]);
        System.out.println(args[1]);
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
        */
        AirDrone drone = new AirDrone("alice");
        Random random = new Random();
        if(drone.timeToTravel())
        drone.setPosition(random.nextDouble() * 0.06 -1.9638,random.nextDouble() * 0.08 + 34.699);
        drone.moveTo();
    }
}
