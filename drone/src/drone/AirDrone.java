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
            // Each publisher id must be unique - added the drone's name to the end
            this.client = new MqttClient("tcp://test.mosquitto.org:1883", PUBLISHER_ID + "-" + n);
        } catch (MqttException e) {
            e.printStackTrace();
        }

        MqttConnectOptions options = new MqttConnectOptions();
        options.setAutomaticReconnect(true);
        options.setCleanSession(true);
        options.setConnectionTimeout(10);
        try {
            client.connect(options);
        } catch (MqttException e) {
            e.printStackTrace();
        }
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
