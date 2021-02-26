package drone;

//abstract class Drone

import java.util.concurrent.TimeUnit;

public abstract class Drone {
    // the position of Drones
    protected double x = 0;
    protected double y = 0;
    protected String name;
    protected int battery = 100;

    // the position of missions
    protected double positionX;
    protected double positionY;

    // get position from the message
    // the message will be translated in the main function
    public void setPosition(double x, double y) {
        this.positionX = x;
        this.positionY = y;
    }

    // give a drone name
    public void setName(String s) {
        this.name = s;
    }

    // judge whether the drones are out of bound
    public abstract boolean outOfBounds();

    // maybe not needed
    // wake up drone
    public void setAlive() {
        while (battery > 0) {
            battery -= 1;//if have time, add some time function.
            try {
                TimeUnit.SECONDS.sleep(2);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    // send position + heartbeat + batterystate information
    public abstract void sendInformation();

    // travel function
    public abstract void moveTo();

    // send signal of sensors
    public abstract void sendSensorSignal();

    public abstract void getMissions();

}
