package drone;

//abstract class Drone

import org.eclipse.paho.client.mqttv3.IMqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.LinkedList;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public abstract class Drone {
    // the basic information of Drones
    protected double x;
    protected double y;
    public String name;
    protected int battery;
    protected LinkedList<String> availableDrones;

    // the name of drones that working in tandem
    protected String partner;

    // initial mqttclient
    IMqttClient client = null;

    // the position of location missions
    protected double positionX;
    protected double positionY;

    public Drone(String n) {
        this.name = n;
        Random random = new Random();
        x = random.nextDouble() * 0.06 -1.9638; //latitude
        y = random.nextDouble() * 0.08 + 34.699; // longitude
        battery = 100;
        availableDrones.add(n);

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
        String thisDroneType = this.getClass().getSimpleName().toLowerCase();
        String thisDroneLocationTopic = "chalmers/dat220/group1/" + thisDroneType + "/" + thisDroneIdentity + "/location";
        client.publish(thisDroneLocationTopic, sendTheseBytes, 0, false);
    }

    // send signal of sensors
    public abstract void sendSensorSignal();

    // send photo information through broker
    public void sendPhoto() throws MqttException {
        JSONObject photoToSend = new JSONObject();

        // the second variable may need to be changed according to the image recognition function
        photoToSend.put("photo", true);

        String sendThisJson = photoToSend.toString();
        byte[] sendTheseBytes = StandardCharsets.UTF_8.encode(sendThisJson).array();

        String thisDroneIdentity = this.name;
        String thisDroneType = this.getClass().getSimpleName().toLowerCase();
        String thisDronePhotoTopic = "chalmers/dat220/group1/" + thisDroneType + "/" + thisDroneIdentity + "/photo";
        client.publish(thisDronePhotoTopic, sendTheseBytes, 0, false);
    }

    // no idea about how drones are assigned co-working partners
    // ground station gives information or other methods?
    // Or, we can use this function to simply fake it.
    public void workInTandem(String n) throws MqttException {
        this.partner = n;
        JSONObject tandemToSend = new JSONObject();
        tandemToSend.put("partner", this.partner);

        String sendThisJson = tandemToSend.toString();
        byte[] sendTheseBytes = StandardCharsets.UTF_8.encode(sendThisJson).array();

        String thisDroneIdentity = this.name;
        String thisDroneType = this.getClass().getSimpleName().toLowerCase();
        String thisDronePartnerTopic = "chalmers/dat220/group1/" + thisDroneType +"/" + thisDroneIdentity + "/partner";
        System.out.println(name + " is working in tandem with " + n);
        client.publish(thisDronePartnerTopic, sendTheseBytes, 0, false);
    }

    // no idea about how drones are assigned co-working partners
    // ground station gives information or other methods?
    // Or, we can use this function to simply fake it.

    public void capturesImages() throws MqttException {

        JSONObject photoToSend = new JSONObject();
        String target = getBufferImage();
        // Mocking upcoming image results.
        if ( this.positionX != x |  this.positionY != y) {

        switch(target) {
            case "Human":
                photoToSend.put(" Ranger ", "detected");
                break;
            case  "Plant":
                // code block
                photoToSend.put(" Plant  ", "detected");
                break;

            case  "Animal":
                // code bloc
                photoToSend.put(" Animal  ", "detected");
                break;
            default:
                System.out.println(  " No specific object detected by   " + this.name);
                // code block
        }
            String sendThisJson = photoToSend.toString();
            MqttMessage message = new MqttMessage(sendThisJson.getBytes());
            message.setQos(0);
            client.publish("chalmers/dat220/group1/drone/Images", message);

        }
    }

    // This suppose to return whatever images we want.
    public String  getBufferImage() {
        return null;
    }

    // maybe not needed
    // judge whether the drones are out of the map
    public abstract boolean outOfBounds();

    // maybe not needed
    // wake up drone
    public void setAlive() {
    }

    // translate the messages from the ground station
    public abstract void getMissions();

}
