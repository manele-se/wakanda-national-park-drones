package drone;

// A component (implemented as a separate process) that simulates one (or more) drone(s).
// This/these drone(s) can be air-borne and land-based drone(s) and can ‘travel’ around the virtual park.
// This process should generate data for movement, heart-beat signal, work-in-tandem signals, sensor signals
// (e.g. the smoke sensor randomly, but rarely indicates that it senses smoke).


import org.eclipse.paho.client.mqttv3.IMqttClient;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;

public class AirDrone extends Drone {

    // mqtt basic setting
    private static final String PUBLISHER_ID = "chalmers-dat220-group1-drone";
    public static final String DRONES_LOCATION_TOPICS = "chalmers/dat220/group1/drone/+/location";
    IMqttClient client = null;

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

    // 'travel' around the virtual park
    @Override
    public void moveTo() {

        while (x != positionX || y != positionY) {

            // latitude travel
            if(x + 10 < positionX)
                x = x + 10;
            else if (x < positionX && x + 10 >positionX) x = positionX;
            else if(x - 10> positionX) x = x - 10;
            else x = positionX;

            //longitude travel
            if(y + 10 < positionY)
                y = y + 10;
            else if (y < positionY && y + 10 >positionY) y = positionY;
            else if(y - 10> positionY) y = y - 10;
            else y = positionY;

            battery -= 1;
            try {
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

    @Override
    public void sendSensorSignal() {

    }

    @Override
    public void getMissions() {

    }


    @Override
    public void sendLoInfo() throws Exception {
        JSONObject positionToSend = new JSONObject();
        positionToSend.put("latitude", this.x);
        positionToSend.put("longitude", this.y);

        String sendThisJson = positionToSend.toString();
        byte[] sendTheseBytes = StandardCharsets.UTF_8.encode(sendThisJson).array();

        String thisDroneIdentity = this.name;
        String thisDroneLocationTopic = "chalmers/dat220/group1/drone/" + thisDroneIdentity + "/location";
        client.publish(thisDroneLocationTopic, sendTheseBytes, 0, false);
    }
}
