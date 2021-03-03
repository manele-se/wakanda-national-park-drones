package drone;


public class AliveDrone {

    // haven't used bash script
    // thought like this
    public static void main(String[] args)  {

        //System.out.println(args[0]);
        //System.out.println(args[1]);
        if (args[0] == "0"){
            AirDrone a1 = new AirDrone(args[1]);
            a1.setPosition(-1.92, 34.71);
            a1.moveTo();
        } else {
            LandDrone l1 = new LandDrone(args[1]);
            l1.setPosition(-1.93,34.73);
            l1.moveTo();
        }

    }
}
