package drone;

// A component (implemented as a separate process) that simulates one (or more) drone(s).
// This/these drone(s) can be air-borne and land-based drone(s) and can ‘travel’ around the virtual park.
// This process should generate data for movement, heart-beat signal, work-in-tandem signals, sensor signals
// (e.g. the smoke sensor randomly, but rarely indicates that it senses smoke).


import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;

public class AirDrone extends Drone {

    // mqtt basic setting
    private static final String PUBLISHER_ID = "chalmers-dat220-group1-drone";
    // public static final String DRONES_LOCATION_TOPICS = "chalmers/dat220/group1/drone/+/location";


    public AirDrone(String n) {
        super(n);

        //connect with the public broker
        try {
            this.client = new MqttClient("tcp://broker.hivemq.com:1883", PUBLISHER_ID);
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
