package com.kafka.orders.config;

import java.util.Properties;

/**
 * Configuration class for Kafka settings
 */
public class KafkaConfig {
    
    // Kafka Broker Configuration
    public static final String BOOTSTRAP_SERVERS = "localhost:9092";
    public static final String SCHEMA_REGISTRY_URL = "http://localhost:8081";
    
    // Topics
    public static final String ORDERS_TOPIC = "orders";
    public static final String DLQ_TOPIC = "orders-dlq";
    
    // Consumer Group
    public static final String CONSUMER_GROUP_ID = "order-consumer-group";
    
    // Retry Configuration
    public static final int MAX_RETRIES = 3;
    public static final long RETRY_BACKOFF_MS = 2000;
    
    /**
     * Get common Kafka properties
     */
    public static Properties getCommonProperties() {
        Properties props = new Properties();
        props.put("bootstrap.servers", BOOTSTRAP_SERVERS);
        props.put("schema.registry.url", SCHEMA_REGISTRY_URL);
        return props;
    }
    
    /**
     * Get producer properties
     */
    public static Properties getProducerProperties() {
        Properties props = getCommonProperties();
        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer", "io.confluent.kafka.serializers.KafkaAvroSerializer");
        props.put("acks", "all");
        props.put("retries", 3);
        props.put("linger.ms", 1);
        return props;
    }
    
    /**
     * Get consumer properties
     */
    public static Properties getConsumerProperties() {
        Properties props = getCommonProperties();
        props.put("group.id", CONSUMER_GROUP_ID);
        props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        props.put("value.deserializer", "io.confluent.kafka.serializers.KafkaAvroDeserializer");
        props.put("specific.avro.reader", "true");
        props.put("auto.offset.reset", "earliest");
        props.put("enable.auto.commit", "false");
        props.put("max.poll.records", 100);
        return props;
    }
}
