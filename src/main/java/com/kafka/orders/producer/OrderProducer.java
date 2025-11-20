package com.kafka.orders.producer;

import com.kafka.orders.avro.Order;
import com.kafka.orders.config.KafkaConfig;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Random;
import java.util.concurrent.ExecutionException;

/**
 * Kafka Producer that sends Order messages with Avro serialization
 */
public class OrderProducer {
    
    private static final Logger logger = LoggerFactory.getLogger(OrderProducer.class);
    private static final Random random = new Random();
    
    private final KafkaProducer<String, Order> producer;
    private final String topic;
    
    public OrderProducer() {
        this.producer = new KafkaProducer<>(KafkaConfig.getProducerProperties());
        this.topic = KafkaConfig.ORDERS_TOPIC;
        logger.info("OrderProducer initialized for topic: {}", topic);
    }
    
    /**
     * Generate and send a random order message
     */
    public void sendRandomOrder() {
        String orderId = "ORDER-" + System.currentTimeMillis();
        String[] products = {"Item1", "Item2", "Item3", "Item4", "Item5"};
        String product = products[random.nextInt(products.length)];
        float price = 10.0f + random.nextFloat() * 990.0f; // Price between 10 and 1000
        long timestamp = System.currentTimeMillis();
        
        Order order = Order.newBuilder()
                .setOrderId(orderId)
                .setProduct(product)
                .setPrice(price)
                .setTimestamp(timestamp)
                .build();
        
        sendOrder(orderId, order);
    }
    
    /**
     * Send an order to Kafka
     */
    public void sendOrder(String key, Order order) {
        ProducerRecord<String, Order> record = new ProducerRecord<>(topic, key, order);
        
        try {
            RecordMetadata metadata = producer.send(record).get();
            logger.info("Sent order: orderId={}, product={}, price={:.2f} -> partition={}, offset={}",
                    order.getOrderId().toString(), order.getProduct().toString(), order.getPrice(),
                    metadata.partition(), metadata.offset());
        } catch (InterruptedException | ExecutionException e) {
            logger.error("Failed to send order: {}", order.getOrderId().toString(), e);
            Thread.currentThread().interrupt();
        }
    }
    
    /**
     * Close the producer
     */
    public void close() {
        logger.info("Closing OrderProducer...");
        producer.close();
    }
    
    /**
     * Main method to run the producer
     */
    public static void main(String[] args) {
        OrderProducer producer = new OrderProducer();
        
        // Add shutdown hook
        Runtime.getRuntime().addShutdownHook(new Thread(producer::close));
        
        try {
            int numOrders = args.length > 0 ? Integer.parseInt(args[0]) : 50;
            int delayMs = args.length > 1 ? Integer.parseInt(args[1]) : 1000;
            
            logger.info("Starting to produce {} orders with {}ms delay...", numOrders, delayMs);
            
            for (int i = 0; i < numOrders; i++) {
                producer.sendRandomOrder();
                Thread.sleep(delayMs);
            }
            
            logger.info("Finished producing {} orders", numOrders);
        } catch (InterruptedException e) {
            logger.error("Producer interrupted", e);
            Thread.currentThread().interrupt();
        } finally {
            producer.close();
        }
    }
}
