package ranger;

import org.eclipse.paho.client.mqttv3.MqttException;

public class Main {
    public static void main(String[] args) throws MqttException {
        Ranger r1 = new Ranger("wenjie");
        r1.travel();
    }
}
