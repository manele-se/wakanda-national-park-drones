package human;

import org.eclipse.paho.client.mqttv3.MqttException;
import human.ranger.Ranger;

public class HumanMain {
    public static void main(String[] args) throws InterruptedException {
        // If program is started with no arguments, start just one ranger
        if (args.length == 0) {
            runRanger("wenjie");
        }
        else {
            // For each argument, start one ranger with the argument as their name
            for (int i = 0; i < args.length; ++i) {
                runRanger(args[i]);
            }
        }
        System.out.println("All rangers started!");

        // Loop forever
        while (true) {
            Thread.sleep(1000);
        }
    }

    private static void runRanger(String name) {
        // Create a new thread that creates a Ranger object and calls its travel method
        Thread thread = new Thread(new Runnable() {
            public void run() {
                try {
                    Ranger r = new Ranger(name);
                    r.travel();
                } catch (InterruptedException | MqttException e) {
                    e.printStackTrace();
                }
            }
        });

        // Start the thread
        thread.start();
    }
}
