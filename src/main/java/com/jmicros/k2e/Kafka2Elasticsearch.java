package com.jmicros.k2e;

import org.apache.http.HttpHost;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.elasticsearch.client.RestClient;

import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

public class Kafka2Elasticsearch {

    private static Logger logger = LogManager.getLogger(Kafka2Elasticsearch.class);

    private Set<String> topics;
    private KafkaStreams streams;
    private final RestClient esRestClient;

    public Kafka2Elasticsearch(){
        this.topics = new HashSet<>();
        this.esRestClient = RestClient.builder(new HttpHost("localhost", 9200, "http")).build();
    }

    private void run() {
        final StreamsBuilder streamsBuilder = new StreamsBuilder();
        buildTopology(streamsBuilder);
        streams = new KafkaStreams(streamsBuilder.build(), getProperties());
        streams.setUncaughtExceptionHandler((t, e) -> {
            logger.error("Stream app K2e error");
        });
        streams.start();
    }

    private void buildTopology(StreamsBuilder streamsBuilder) {
        Serde<String> stringSerde = Serdes.String();
        registerTopics();
        KStream<String, String> message = streamsBuilder.stream(topics, Consumed.with(stringSerde, stringSerde));

        Kafka2ElasticsearchTranslator translator = new Kafka2ElasticsearchTranslator(esRestClient);
        message.transform(translator);
    }

    private void registerTopics() {
        topics.add("___topic___1");
        topics.add("___topic___2");
        topics.add("___topic___3");
    }

    private static Properties getProperties() {
        Properties settings = new Properties();
        // Set a few key parameters
        settings.put(StreamsConfig.APPLICATION_ID_CONFIG, "k2e");
        // Kafka bootstrap server (broker to talk to); ubuntu is the host name for my VM running Kafka, port 9092 is where the (single) broker listens
        settings.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        // default serdes for serialzing and deserializing key and value from and to streams in case no specific Serde is specified
        settings.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
        settings.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
        settings.put(StreamsConfig.STATE_DIR_CONFIG, "/temp");

        return settings;
    }

    private Runnable stop() {
        return streams::close;
    }

    public static void main(String [] args){
        Kafka2Elasticsearch app = new Kafka2Elasticsearch();
        app.run();

        Runtime.getRuntime().addShutdownHook(new Thread(app.stop()));
    }

}
