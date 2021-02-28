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
