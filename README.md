
# JMICROS EVENT PROCESSING


## Build Prerequisites

1. corretto 1.8
2. gradle 3.4.1
3. jdk 1.8

## Build

gradle clean assemble fatjar && docker build -t jmicros .

## Deployment Instructions

- Configure .env file with your IP address
- Start containers using docker-compose
```
docker-compose up -d kafka elasticsearch scope
```
You can review check the status of te containers using "weavescope" going to http://localhost:4040 

- Create topics (download a kafka client see instructions on https://kafka.apache.org/quickstart)

```
bin/kafka-topics.sh --create --topic ___topic___1 --zookeeper localhost:2181 --partitions 1 --replication-factor 1
bin/kafka-topics.sh --create --topic ___topic___2 --zookeeper localhost:2181 --partitions 1 --replication-factor 1
bin/kafka-topics.sh --create --topic ___topic___3 --zookeeper localhost:2181 --partitions 1 --replication-factor 1
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

How to get data from Elasticsearch

- Get indexes
```
curl --request GET --url 'http://localhost:9200/_cat/indices?v=' --header 'cache-control: no-cache' --header 'postman-token: 718e1576-5ce1-88c3-3e44-075f6d982ce9'
```
- Get Documents
```
curl --request GET --url 'http://localhost:9200/jmicros-streams/_search?scroll=10m&size=50' --header 'cache-control: no-cache' --header 'postman-token: d7dbffe1-f9b8-13a8-2116-02e3b9ca2228'
```
- Get Route
```
curl --request POST \
  --url http://localhost:9200/jmicros-streams/_search \
  --header 'cache-control: no-cache' \
  --header 'content-type: application/json' \
  --header 'postman-token: b5e624f4-754a-5a89-dba7-6189c9bc5f81' \
  --data '{"query":{"match":{"message.code":"678923AB"}},"_source":["topic"]}'
```
- Delete Index
```
curl --request DELETE --url http://localhost:9200/jmicros-streams --header 'cache-control: no-cache' --header 'postman-token: 4a6c0821-802b-547b-26f3-d15187e15413'
```
