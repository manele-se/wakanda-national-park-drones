package drone;

//abstract class Drone

import org.eclipse.paho.client.mqttv3.IMqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public abstract class Drone {
   // private static int k = 0;

    // the basic information of Drones
    protected double x;
    protected double y;
    protected String name;
    protected int battery;

    // the name of drones that working in tandem
    protected String partner;
    // the position of location missions
    protected double positionX = -1;
    protected double positionY = -1;
    // initial mqttclient
    IMqttClient client = null;

    public Drone(String n) {
        this.name = n;
        Random random = new Random();
        x = (1.9168 + (random.nextDouble() * (1.9538 - 1.9168))) * -1; //latitude
        y = 34.710+ (random.nextDouble() * (34.767 - 34.699)); // longitude
        battery = 100;
        // System.out.println("orginal x:" + x + "  orginal y:" + y);
    }

    // get position from the message
    // the message will be translated in the other function
    public void setPosition(double x, double y) {
        this.positionX = x;
        this.positionY = y;
    }

    public void sendBattery() {
        this.battery -= 1;
        JSONObject positionToSend = new JSONObject();
        positionToSend.put("battery", this.battery);

        String sendThisJson = positionToSend.toString();
        byte[] sendTheseBytes = StandardCharsets.UTF_8.encode(sendThisJson).array();

        String thisDroneType = this.getClass().getSimpleName().toLowerCase();
        String thisDroneIdentity = this.name;
        String thisDroneBatteryTopic = "chalmers/dat220/group1/drone/" + thisDroneType + "/" +  thisDroneIdentity + "/battery";
        try {
            client.publish(thisDroneBatteryTopic, sendTheseBytes, 0, false);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    public boolean timeToTravel() {
        if (this.positionY == -1 || this.y == this.positionY)
            return true;
        else return false;
    }

    // when having missions, moving function
    public void moveTo() {
        while (this.positionY != -1 && (x != positionX || y != positionY)) {

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

           // System.out.println("moving x:" + x + "  moving y: " + y);
            try {
                sendLocation();
                sendBattery();
                sendSensorSignal();
                sendPhoto();
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                TimeUnit.SECONDS.sleep(3);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        this.positionY = -1;
    }

    // travel function when no missions or mission is over
    public void travel() {
        Random r = new Random();
        double m = r.nextDouble() * 0.001;
        int k = r.nextInt(4);
        if (k == 0 && intheBounds(x+m,y)) {
            x = x + m;
        } else if (k == 1 && intheBounds(x, y+m)) {
            y = y + m;
        } else if (k == 2 && intheBounds(x-m,y)) {
            x = x - m;
        } else if(k == 3 && intheBounds(x, y-m)) {
            y = y - m;
        }
        //System.out.println( this.name +"traveling x:" + x + "  traveling y: " + y);
        try {
            sendLocation();
            sendBattery();
            sendSensorSignal();
            sendPhoto();
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            TimeUnit.SECONDS.sleep(3);
        } catch (InterruptedException e) {
            e.printStackTrace();
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
        Random r = new Random();
        int p = r.nextInt(4);// 0,1,23
        int m = r.nextInt(20);//[0-19]

        // 5% to sent a picture
        if (m > 18) {
            JSONObject photoToSend = new JSONObject();
            // four type of photo, animal/plants/human/others
            if (p == 0) {
                photoToSend.put("photo", "animal");
            } else if (p == 1) {
                photoToSend.put("photo", "plant");
            } else if (p == 2) {
                photoToSend.put("photo", "human");// not ranger
            } else {
                photoToSend.put("photo", "others");
            }

            String sendThisJson = photoToSend.toString();
            byte[] sendTheseBytes = StandardCharsets.UTF_8.encode(sendThisJson).array();

            String thisDroneIdentity = this.name;
            String thisDroneType = this.getClass().getSimpleName().toLowerCase();
            String thisDronePhotoTopic = "chalmers/dat220/group1/" + thisDroneType + "/" + thisDroneIdentity + "/photo";
            client.publish(thisDronePhotoTopic, sendTheseBytes, 0, false);
        }
    }

    public void workInTandem(String n) throws MqttException {
        this.partner = n;
        JSONObject tandemToSend = new JSONObject();
        tandemToSend.put("partner", this.partner);

        String sendThisJson = tandemToSend.toString();
        byte[] sendTheseBytes = StandardCharsets.UTF_8.encode(sendThisJson).array();

        String thisDroneIdentity = this.name;
        String thisDroneType = this.getClass().getSimpleName().toLowerCase();
        String thisDronePartnerTopic = "chalmers/dat220/group1/" + thisDroneType + "/" + thisDroneIdentity + "/partner";
        System.out.println(name + " is working in tandem with " + n);
        client.publish(thisDronePartnerTopic, sendTheseBytes, 0, false);
    }
    // maybe not needed
    // judge whether the drones are out of the map
    public boolean intheBounds(double x,double y){
        if(x < -1.9638 || x > -1.9068){
            return false;
        } else if(y < 34.699 || y > 34.787){
            return false;
        } else{
            return true;
        }
    }

    // translate the messages from the ground station
    public abstract void getMissions();

}
