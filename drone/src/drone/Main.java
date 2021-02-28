package drone;


import org.eclipse.paho.client.mqttv3.MqttException;

public class Main {

    public static void main(String[] args) throws MqttException {


        AirDrone a1 = new AirDrone("alice");
        //a1.setAlive();
        a1.setPosition(300, 300);
        a1.moveTo();

    }
}
