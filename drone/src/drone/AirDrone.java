package drone;

// A component (implemented as a separate process) that simulates one (or more) drone(s).
// This/these drone(s) can be air-borne and land-based drone(s) and can ‘travel’ around the virtual park.
// This process should generate data for movement, heart-beat signal, work-in-tandem signals, sensor signals
// (e.g. the smoke sensor randomly, but rarely indicates that it senses smoke).


import org.eclipse.paho.client.mqttv3.IMqttClient;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.util.concurrent.TimeUnit;

public class AirDrone extends Drone {

    public static final String DRONES_LOCATION_TOPICS = "drone/+/location";
    IMqttClient client;

    public AirDrone(String n) {
        super(n);
    }

    @Override
    public void moveTo() {
        // simple travel function
        //TODO: add the final position limitation
        while (x < positionX && y < positionY) {
            x = x + 10;
            y = y + 10;
            battery -= 1;
            sendInformation();
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

    @Override
    public void sendInformation() {
        String x = Double.toString(this.x);
        String y = Double.toString(this.y);
        // this format just for testing
        String s = "drone" + this.name + "x:" + x + "y:" + y + "battery" + this.battery;
        System.out.println(s);
    }

    @Override
    public void sendSensorSignal() {

    }

    @Override
    public void getMissions() {

    }

    public MqttMessage Information() {
        String x = Double.toString(this.x);
        String y = Double.toString(this.y);
        // this format just for testing
        String s = "drone" + this.name + "x:" + x + "y:" + y + "battery" + this.battery;
        byte[] payload = s.getBytes();
        return new MqttMessage(payload);
    }

    public Void call() throws Exception{
        if(!client.isConnected()){
            return null;
        }

        MqttMessage msg = Information();
        msg.setQos(0);
        msg.setRetained(true);
        client.publish(DRONES_LOCATION_TOPICS,msg);
        return null;
    }
}
