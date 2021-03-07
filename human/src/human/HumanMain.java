package human;

import org.eclipse.paho.client.mqttv3.MqttException;
import human.ranger.Ranger;

public class HumanMain {
    public static void main(String[] args) throws InterruptedException {
        if (args.length == 0) {
            runRanger("wenjie");
        }
        else {
            for (int i = 0; i < args.length; ++i) {
                runRanger(args[i]);
            }
        }
        System.out.println("All rangers started!");

        while (true) {
            Thread.sleep(1000);
        }
    }

    private static void runRanger(String name) {
        Thread thread = new Thread(new Runnable() {
            public void run() {
                Ranger r1 = null;
                try {
                    r1 = new Ranger(name);
                    r1.travel();
                } catch (InterruptedException | MqttException e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
    }
}
