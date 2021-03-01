package ranger;

import org.eclipse.paho.client.mqttv3.IMqttClient;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Random;

//A component (implemented as a separate process) that ‘simulates’ one (or more) person(s):
// this/these person(s) ‘walks’ around in a virtual park.
// This process should generate data for movement, heart-beat signals (if the person is a ranger).
public class Ranger {
    // ranger position
    protected double x;
    protected double y;
    protected String name;
    IMqttClient client = null;

    private static final String PUBLISHER_ID = "chalmers-dat220-group1-ranger";

    public Ranger(String n){
        this.name = n;

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

    public void travel(){
        Random r = new Random();

        // random generate latitude
        int i1 = r.nextInt(10);
        double d1 = r.nextDouble();
        double x1 = i1*(10 - d1);
        System.out.println(x1);
        BigDecimal temp   =   new   BigDecimal(x1);
        double   x   =  temp.setScale(2, RoundingMode.HALF_UP).doubleValue();
        this. x = x;

        // random generate longitude
        int i2 = r.nextInt(10);
        double d2 = r.nextDouble();
        double y1 = i2*(10 + d2);
        System.out.println(y1);
        temp   =   new   BigDecimal(x1);
        double   y   =  temp.setScale(2, RoundingMode.HALF_UP).doubleValue();
        this.y = y;



        System.out.println(x);
    }
    // send signal
    public void sendInformation() {

    }
}
