package ranger;

import DecisionMaker.Mission;
import communication.Communication;
import org.eclipse.paho.client.mqttv3.IMqttClient;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.util.Random;


//A component (implemented as a separate process) that ‘simulates’ one (or more) person(s):
// this/these person(s) ‘walks’ around in a virtual park.
// This process should generate data for movement, heart-beat signals (if the person is a ranger).
public class Ranger {

    // ranger basic information
    protected double x;
    protected double y;
    protected String name;
    IMqttClient client = null;
    Mission droneInAction;

    // ranger publisher_id
    private static final String PUBLISHER_ID = "chalmers-dat220-group1-ranger";
    private static final String MISSION_TOPIC = "chalmers/dat220/group1/delegate/";


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

    /**
     * Two parameters constructed created to instantiate a ranger assiging a mission
     * to one of the drones.
     */
    public Ranger (String n, String droneMission ) throws MqttException {
        this.name = n;
        SendMissionRequest(droneMission);
    }


    // this/these person(s) ‘walks’ around in a virtual park.
    public void travel() {
        Random r = new Random();

        // random generate latitude
        int i1 = r.nextInt(10);
        double d1 = r.nextDouble();
        double x1 = i1 * (10 - d1);
        // double variable retains two decimal places
        BigDecimal temp = new BigDecimal(x1);
        x1 = temp.setScale(2, RoundingMode.HALF_UP).doubleValue();
        this.x = x1;

        // random generate longitude
        int i2 = r.nextInt(10);
        double d2 = r.nextDouble();
        double y1 = i2 * (10 + d2);
        temp = new BigDecimal(y1);
        y1 = temp.setScale(2, RoundingMode.HALF_UP).doubleValue();
        this.y = y1;

        // travel around the park
        int k = 0;
        while (true) {
            int t = r.nextInt(2);

            // around, maybe not needed
            if (k == 0) {
                x = x + 10.0;
                k += 1;
            } else if (k == 1) {
                y = y + 10.0;
                k += 1;
            } else if (k == 2) {
                x = x - 10.0;
                k += 1;
            } else {
                y = y - 10.0;
                k = 0;
            }

            //random send information, maybe don't need randomize
            if (t == 1) {
                try {
                    sendInformation();
                    //System.out.println("latitude:" + x + "longitude" + y);
                } catch (MqttException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    // send signal
    public void sendInformation() throws MqttException {

        JSONObject positionToSend = new JSONObject();
        positionToSend.put("latitude", this.x);
        positionToSend.put("longitude", this.y);

        String sendThisJson = positionToSend.toString();
        byte[] sendTheseBytes = StandardCharsets.UTF_8.encode(sendThisJson).array();

        String thisRangerIdentity = this.name;
        String thisRangerLocationTopic = "chalmers/dat220/group1/ranger/" + thisRangerIdentity + "/location";
        client.publish(thisRangerLocationTopic, sendTheseBytes, 0, false);
    }

    public void SendMissionRequest(String droneMission ) throws MqttException {

        switch(droneMission) {

            case "surveilArea":

                droneInAction.missionID= Mission.MissionType.SURVEILLANCE;
                Communication.send(this.client,"chalmers/dat220/group1/delegate",  droneInAction.missionID.toString() +
                                "?" +
                                droneInAction.getDrone()
                         );
                break;
            case  "DetectObject":
                droneInAction.missionID= Mission.MissionType.OBJECTDETECTION;
                Communication.send(this.client,"chalmers/dat220/group1/delegate", droneInAction.missionID.toString() + "?" +
                                droneInAction.getDrone()
                          );
                break;
            default:
                System.out.println( " Mission not accurate  " + this.name);
                break;
        }
    }

}
