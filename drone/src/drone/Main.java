package drone;

public class Main {
    public static void main(String[] args) {
        AirDrone a1 = new AirDrone();
        a1.setName("alice");
        //a1.setAlive();
        a1.setPosition(300, 300);
        a1.moveTo();
    }
}
