## Running an MQTT Broker
The MQTT protocol is an open standard, which means that anyone can create programs that implement the protocol. Programs that communicate over MQTT are called Clients. A program that acts as the communication hub is called a Broker.

There are a lot of different implementations of MQTT brokers. I have found one that appears pretty simple to install and run. It requires a working Python 3 environment and Pip to work.

- [HBMQTT documentation](https://hbmqtt.readthedocs.io/en/latest/)
- [Running a broker](https://hbmqtt.readthedocs.io/en/latest/quickstart.html#running-a-broker)

```
# To install
pip install hbmqtt

# To start the broker
hbmqtt
```

When the broker is running, any program that knows the address of the broker can communicate through it.

## Communicating
Every program that we need to write must act as an MQTT Client. There are packages that we can import that lets us do that. For a Java program, we can use Maven to add MQTT Client support.

- [MQTT Client in Java](https://www.baeldung.com/java-mqtt-client)

## Data format
One suggestion for communication is that we use JSON to package the data. This is a suggestion of different events that drones can send, for example:

```
{
  "event_type": "location",
  "latitude": 5.128124,
  "longitude": 14.915432
}
```

```
{
  "event_type": "fire",
  "latitude": 5.128124,
  "longitude": 14.915432
}
```

```
{
  "event_type": "heartbeat",
  "battery_level": 74,
  "latitude": 5.128124,
  "longitude": 14.915432
}
```

```
{
  "event_type": "help_request",
  "requested_drone_type": "air",
  "latitude": 5.128124,
  "longitude": 14.915432
}
```
