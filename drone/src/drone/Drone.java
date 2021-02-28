package drone;

//abstract class Drone

public abstract class Drone {
    // the basic information of Drones
    protected double x;
    protected double y;
    protected String name;
    protected int battery;

    // the position of location missions
    protected double positionX;
    protected double positionY;

    public Drone(String n) {
        this.name = n;
        x = -1.95; //latitude
        y = 34.7; // longitude
        battery = 100;
    }

    // get position from the message
    // the message will be translated in the other function
    public void setPosition(double x, double y) {
        this.positionX = x;
        this.positionY = y;
    }

    // travel function
    public abstract void moveTo();

    // send location information through broker
    public abstract void sendLoInfo() throws Exception;

    //maybe not needed
    // judge whether the drones are out of the map
    public abstract boolean outOfBounds();

    // maybe not needed
    // wake up drone
    public void setAlive() {
    }

    // send signal of sensors
    public abstract void sendSensorSignal();

    // translate the messages from the ground station
    public abstract void getMissions();

}
