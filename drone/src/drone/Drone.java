package drone;

//abstract class Drone

public abstract class Drone {
    // the position of Drones
    protected double x;
    protected double y;
    protected String name;
    protected int battery;

    // the position of missions
    protected double positionX;
    protected double positionY;

    public Drone(String n){
        this.name = n;
        x = 0.0;
        y = 0.0;
        battery = 100;
    }
    // get position from the message
    // the message will be translated in the other function
    public void setPosition(double x, double y) {
        this.positionX = x;
        this.positionY = y;
    }

    // give a drone name
    public void setName(String s) {
        this.name = s;
    }

    //maybe not needed
    // judge whether the drones are out of bound
    public abstract boolean outOfBounds();

    // maybe not needed
    // wake up drone
    public void setAlive() {

    }

    // send position + heartbeat + batterystate information
    public abstract void sendInformation();

    // travel function
    public abstract void moveTo();

    // send signal of sensors
    public abstract void sendSensorSignal();

    // translate the json object
    public abstract void getMissions();

}
