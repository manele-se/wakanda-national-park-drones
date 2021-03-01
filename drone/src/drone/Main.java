package drone;


import org.eclipse.paho.client.mqttv3.MqttException;

public class Main {

    public static void main(String[] args) throws MqttException {

        AirDrone a1 = new AirDrone("alice");
        //a1.setAlive();
        a1.setPosition(138.2, 124.6);
        a1.moveTo();

        LandDrone l1 = new LandDrone("bob");
        l1.setPosition(133.6,-90.6);
        l1.moveTo();

    }
}
