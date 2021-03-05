package DecisionMaker;

import drone.Drone;

public class Mission {

    Drone drone;
    public double positionX;
    public double positionY;
    public MissionType missionID;

    public Mission (Drone drone, double x, double y, MissionType type ){
        this.drone= drone;
        this.positionX = x;
        this.positionY = y;
        missionID=type;
    }

    public String getDrone () {
        return this.drone.name;
    }

    public enum MissionType {

        SURVEILLANCE,
        OBJECTDETECTION;

    }

}
