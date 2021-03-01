package drone;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.Random;

public class LandDrone extends Drone {

    // keep it in the land drone and air drone
    // in case sometime we need to distinguish the air drone and land drone.
    private static final String PUBLISHER_ID = "chalmers-dat220-group1-drone";

    public LandDrone(String n){
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
            if (x + 10.0 < positionX)
                x = x + 10.0;
            else if (x < positionX && x + 10.0 > positionX) x = positionX;
            else if (x - 10.0 > positionX) x = x - 10.0;
            else x = positionX;

            //longitude travel
            if (y + 10.0 < positionY)
                y = y + 10.0;
            else if (y < positionY && y + 10.0 > positionY) y = positionY;
            else if (y - 10.0 > positionY) y = y - 10.0;
            else y = positionY;

            battery -= 1;
            try {
                sendSensorSignal();
                sendLoInfo();
            } catch (Exception e) {
                e.printStackTrace();
            }

            //interval time (maybe not needed)
            /*
            try {
                TimeUnit.SECONDS.sleep(3);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }*/
        }
    }

    @Override
    public boolean outOfBounds() {
        return false;
    }

    // temperature sensor
    // cant find any information of smoke sensor in the task1 file.
    // maybe need the information of location
    @Override
    public void sendSensorSignal() {
            Random r = new Random();

            // random int [0, 10]
            int i1 = r.nextInt(10);

            // random double [0, 1.0]
            double d1 = r.nextDouble();

            // final random temperature
            double heat = i1 * (10 + d1);

            // high temperature send signal
            if (i1 > 8) {
                JSONObject sensorToSend = new JSONObject();
                sensorToSend.put("temp", heat);
                String sendThisJson = sensorToSend.toString();
                byte[] sendTheseBytes = StandardCharsets.UTF_8.encode(sendThisJson).array();
                String thisDroneIdentity = this.name;
                String thisDroneSensorTopic = "chalmers/dat220/group1/drone/" + thisDroneIdentity + "/temp";
                try {
                    client.publish(thisDroneSensorTopic, sendTheseBytes, 0, false);
                } catch (MqttException e) {
                    e.printStackTrace();
                }
            }

    }

    @Override
    public void getMissions() {

    }
}
