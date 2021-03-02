package drone;

// A component (implemented as a separate process) that simulates one (or more) drone(s).
// This/these drone(s) can be air-borne and land-based drone(s) and can ‘travel’ around the virtual park.
// This process should generate data for movement, heart-beat signal, work-in-tandem signals, sensor signals
// (e.g. the smoke sensor randomly, but rarely indicates that it senses smoke).


import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;

import java.util.concurrent.TimeUnit;

public class AirDrone extends Drone {

    // mqtt basic setting
    private static final String PUBLISHER_ID = "chalmers-dat220-group1-drone";

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
    public void moveTo() {
        while (x != positionX || y != positionY) {

            // latitude travel
            if (x + 0.001 < positionX)
                x = x + 0.001;
            else if (x < positionX && x + 0.001 > positionX) x = positionX;
            else if (x - 0.001 > positionX) x = x - 0.001;
            else x = positionX;

            //longitude travel
            if (y + 0.001 < positionY)
                y = y + 0.001;
            else if (y < positionY && y + 0.001 > positionY) y = positionY;
            else if (y - 0.001 > positionY) y = y - 0.001;
            else y = positionY;

            battery -= 1;
            try {
                sendLoInfo();
            } catch (Exception e) {
                e.printStackTrace();
            }

            //interval time (maybe not needed)

            try {
                TimeUnit.SECONDS.sleep(3);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
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
