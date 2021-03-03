package drone;

//abstract class Drone

import org.eclipse.paho.client.mqttv3.IMqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

public abstract class Drone {
    // the basic information of Drones
    protected double x;
    protected double y;
    protected String name;
    protected int battery;

    IMqttClient client = null;

    // the position of location missions
    protected double positionX;
    protected double positionY;

    public Drone(String n) {
        this.name = n;
        x = -1.95; //latitude
        y = 34.7; // longitude
        battery = 100;
    }

    // get position from the message
    // the message will be translated in the other function
    public void setPosition(double x, double y) {
        this.positionX = x;
        this.positionY = y;
    }

    public void sendBattery(){
        this.battery -= 1;
        JSONObject positionToSend = new JSONObject();
        positionToSend.put("battery", this.battery);

        String sendThisJson = positionToSend.toString();
        byte[] sendTheseBytes = StandardCharsets.UTF_8.encode(sendThisJson).array();

        String thisDroneIdentity = this.name;
        String thisDroneBatteryTopic = "chalmers/dat220/group1/drone/" + thisDroneIdentity + "/battery";
        try {
            client.publish(thisDroneBatteryTopic, sendTheseBytes, 0, false);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }
    // travel function
    public void moveTo(){
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

            try {
                sendLocation();
                sendBattery();
                sendSensorSignal();
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

    // send location information through broker
    public void sendLocation() throws Exception {
        JSONObject positionToSend = new JSONObject();
        positionToSend.put("latitude", this.x);
        positionToSend.put("longitude", this.y);

        String sendThisJson = positionToSend.toString();
        byte[] sendTheseBytes = StandardCharsets.UTF_8.encode(sendThisJson).array();

        String thisDroneIdentity = this.name;
        String thisDroneLocationTopic = "chalmers/dat220/group1/drone/" + thisDroneIdentity + "/location";
        client.publish(thisDroneLocationTopic, sendTheseBytes, 0, false);
    }

    public abstract void sendSensorSignal();

    //maybe not needed
    // judge whether the drones are out of the map
    public abstract boolean outOfBounds();

    // maybe not needed
    // wake up drone
    public void setAlive() {
    }

    // send signal of sensors

    // translate the messages from the ground station
    public abstract void getMissions();

}
