package drone;


import org.eclipse.paho.client.mqttv3.MqttException;

public class AliveDrone {

    // haven't used bash script
    // thought like this
    public static void main(String[] args) throws MqttException {

        System.out.println(args[0]);
        System.out.println(args[1]);
        Drone drone;

        if (args[0].equals("0")){
            drone = new AirDrone(args[1]);
            drone.setPosition(-1.92, 34.71);
            drone.moveTo();
        } else {
            drone = new LandDrone(args[1]);
            drone.setPosition(-1.93,34.73);
            drone.moveTo();
        }

        // Work in tandem
        if (args.length >= 3) {
            drone.workInTandem(args[2]);
        }
    }
}
