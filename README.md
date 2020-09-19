
# JMICROS EVENT PROCESSING


## Build Prerequisites

1. corretto 1.8
2. gradle 3.4.1

## Build

gradle clean assemble fatjar && docker build -t jmicros .

## Deployment Instructions

- Configure .env with your IP address
- Start containers using docker-compose
```
docker-compose up -d kafka elasticsearch
```
- Create topics (download a kafka client see instructions on https://kafka.apache.org/quickstart)

```
bin/kafka-topics.sh --create --topic ___topic___1 --bootstrap-server localhost:9092
bin/kafka-topics.sh --create --topic ___topic___2 --bootstrap-server localhost:9092
bin/kafka-topics.sh --create --topic ___topic___3 --bootstrap-server localhost:9092
```
- Start StreamApps App1.java and App2.java
- Start Kafka2Elasticsearch.java app

## Test

Sensor Message:
```
{
  "code":"678923AB",
  "time":1600291504,
  "temperature":10.5,
  "humidity":20
}
```

How to publish a message:
- Start a producer
```
bin/kafka-console-producer.sh --broker-list localhost:9092 --topic ___topic___1 --property=parse.key=true --property=key.separator=,
```
- Publish a raw message <key,value>
```
sensor_read_001,{"code":"678923AB","time":1600291504,"temperature":10.5,"humidity":20}
``` 

How to monitor messages in Kafka
- Subscribe to topic: ___topic___1
```
bin/kafka-console-consumer.sh --bootstrap-server localhost:9092 --property print.key=true --property key.separator="," --topic ___topic___1 
``` 
- Subscribe to topic: ___topic___2
```
bin/kafka-console-consumer.sh --bootstrap-server localhost:9092 --property print.key=true --property key.separator="," --topic ___topic___2
```
- Subscribe to topic: ___topic___3
```
bin/kafka-console-consumer.sh --bootstrap-server localhost:9092 --property print.key=true --property key.separator="," --topic ___topic___3
```
