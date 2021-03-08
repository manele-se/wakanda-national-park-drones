# Wakanda Autonomous System for Poacher Prevention (WASPP)

## How to start

In IntelliJ, in the Project explorer view, navigate to **security**, **src**, **main**, **java**, **AccessControl**.
Right-click on **AccessControl** and select **Run AccessControl.main**.

Then navigate to **groundstation**, **src**, **main**, **java**, **wakanda**, **Dashboard**.
Right-click on **Dashboard** and select **Run Dashboard.main**.

Then navigate to **human**, **src**, **human**, **HumanMain**.
Right-click on **HumanMain** and select **Run HumanMain.main**.

Then navigate to **ImageRecognition**, **src**, **ImageAnalyzer**.
Right-click on **ImageAnalyzer** and select **Run ImageAnalyzer.main**.

All these four can be reached from the project dropdown at the top right of the IntelliJ program window.
In that case, pick the class and click the green "play" button instead.

Finally go to the **Terminal** *(can be found in the bottom section of the program window)*.
Run `./run-all-drones.sh`

### About the drone scripts

The directory names and classpaths in the `run-drone.sh` and `run-all-drones.sh` scripts must be
changed, depending on your development environment, and the location of your Java interpreter, Java Virtual Machine,
and dependencies.

### Trouble-shooting

If there are a lot of exception messages when running the drones, you have probably started
multiple instances of the `run-all-drones.sh` script.

**Option 1:**

`killall java`

This will kill **all** Java processes, including Dashboard, AccessControl and HumanMain. So those three
will need to be started again before running the script.

**Option 2:**

`ps`

This lists all running processes. Look for java processes and `kill` all those processes.

## Connecting to a public MQTT broker
Running MQTT brokers locally is a really bad experience, and it is very difficult to find a broker that actually works.

There are a lot of public MQTT brokers that are free for development and testing purposes. One that is working fine is:

- [HiveMQ Public MQTT Broker](https://www.hivemq.com/public-mqtt-broker/)

We can use that one for the purposes of the assignment.

## Communicating
Every program that we need to write must act as an MQTT Client. There are packages that we can import that lets us do that. For a Java program, we can use Maven to add MQTT Client support.

- [MQTT Client in Java](https://www.baeldung.com/java-mqtt-client)

## Topics
Each drone should have a couple of different topics:

- `chalmers/dat220/group1/drone/ID/location`
- `chalmers/dat220/group1/drone/ID/heartbeat`
- `chalmers/dat220/group1/drone/ID/helprequest`
- ...

## Data format
One suggestion for communication is that we use JSON to package the data.

```
// For locations:
{
  "latitude": 5.128124,
  "longitude": 14.915432
}
```

```
// For drone heartbeats:
{
  "battery_level": 74,
  "latitude": 5.128124,
  "longitude": 14.915432
}
```

```
// For help requests:
{
  "requested_drone_type": "air",
  "latitude": 5.128124,
  "longitude": 14.915432
}
```
