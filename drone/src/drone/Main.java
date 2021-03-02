package drone;


import org.eclipse.paho.client.mqttv3.MqttException;

public class Main {

    public static void main(String[] args) throws MqttException {

        AirDrone a1 = new AirDrone("alice");
        //a1.setAlive();
        a1.setPosition(-1.92, 34.71);
        a1.moveTo();

        LandDrone l1 = new LandDrone("bob");
        l1.setPosition(-1.93,34.73);
        l1.moveTo();

    }
}
