package drone;



import communication.Communication;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;

public class AirDrone extends Drone {

    // mqtt basic setting
    private static final String PUBLISHER_ID = "chalmers-dat220-group1-drone";

    public AirDrone(String n) {
        super(n);

        //connect with the public broker
        try {
            this.client = Communication.connect(PUBLISHER_ID);
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
