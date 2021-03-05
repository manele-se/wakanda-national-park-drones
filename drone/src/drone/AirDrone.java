package drone;



import communication.Communication;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;

import java.util.LinkedList;

public class AirDrone extends Drone {

    // mqtt basic setting
    private static final String PUBLISHER_ID = "chalmers-dat220-group1-drone";

    public AirDrone(String n) {
        super(n);

        //connect with the public broker
        try {
            // Each publisher id must be unique - added the drone's name to the end
            this.client = Communication.connect(PUBLISHER_ID + "-" + n);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }



    @Override
    public boolean outOfBounds() {
        return false;
    }

    // sensor
    // cant find other sensors in the air drone except the camera sensor
    @Override
    public void sendSensorSignal() {

    }

    @Override
    public void getMissions() {

    }

}
