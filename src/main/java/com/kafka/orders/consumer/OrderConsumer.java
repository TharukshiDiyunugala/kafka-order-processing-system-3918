package com.kafka.orders.consumer;

import com.kafka.orders.avro.Order;
import com.kafka.orders.config.KafkaConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.errors.SerializationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Kafka Consumer with retry logic, DLQ, and real-time price aggregation
 */
public class OrderConsumer {
    
    private static final Logger logger = LoggerFactory.getLogger(OrderConsumer.class);
    
    private final KafkaConsumer<String, Order> consumer;
    private final KafkaProducer<String, Order> dlqProducer;
    private final PriceAggregator priceAggregator;
    private final AtomicBoolean running = new AtomicBoolean(true);
    
    // Retry tracking: orderId -> retry count
    private final Map<String, Integer> retryMap = new HashMap<>();
    
    public OrderConsumer() {
        this.consumer = new KafkaConsumer<>(KafkaConfig.getConsumerProperties());
        this.dlqProducer = new KafkaProducer<>(KafkaConfig.getProducerProperties());
        this.priceAggregator = new PriceAggregator();
        
        consumer.subscribe(Collections.singletonList(KafkaConfig.ORDERS_TOPIC));
        logger.info("OrderConsumer initialized and subscribed to: {}", KafkaConfig.ORDERS_TOPIC);
    }
    
    /**
     * Process orders with retry logic and DLQ
     */
    public void consumeOrders() {
        logger.info("Starting to consume orders...");
        
        while (running.get()) {
            try {
                ConsumerRecords<String, Order> records = consumer.poll(Duration.ofMillis(1000));
                
                for (ConsumerRecord<String, Order> record : records) {
                    processRecord(record);
                }
                
                // Commit offsets after processing batch
                consumer.commitSync();
                
            } catch (Exception e) {
                logger.error("Error during consumption", e);
            }
        }
    }
    
    /**
     * Process a single record with retry logic
     */
    private void processRecord(ConsumerRecord<String, Order> record) {
        Order order = record.value();
        String orderId = order.getOrderId();
        
        try {
            // Simulate processing with potential failures
            boolean success = processOrder(order);
            
            if (success) {
                logger.info("Successfully processed order: orderId={}, product={}, price={:.2f}",
                        orderId, order.getProduct(), order.getPrice());
                
                // Update aggregation
                priceAggregator.addPrice(order.getPrice());
                
                // Remove from retry map if successful
                retryMap.remove(orderId);
            } else {
                handleFailure(record, orderId, order);
            }
            
        } catch (Exception e) {
            logger.error("Exception while processing order: {}", orderId, e);
            handleFailure(record, orderId, order);
        }
    }
    
    /**
     * Process an order (simulates business logic with potential failures)
     */
    private boolean processOrder(Order order) {
        // Simulate random failures (10% failure rate for demonstration)
        if (Math.random() < 0.1) {
            logger.warn("Simulated temporary failure for order: {}", order.getOrderId());
            return false;
        }
        
        // Simulate processing time
        try {
            Thread.sleep(50);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
        }
        
        return true;
    }
    
    /**
     * Handle processing failure with retry logic
     */
    private void handleFailure(ConsumerRecord<String, Order> record, String orderId, Order order) {
        int retryCount = retryMap.getOrDefault(orderId, 0);
        retryCount++;
        
        if (retryCount < KafkaConfig.MAX_RETRIES) {
            logger.warn("Retry {}/{} for order: {}", retryCount, KafkaConfig.MAX_RETRIES, orderId);
            retryMap.put(orderId, retryCount);
            
            // Wait before retry
            try {
                Thread.sleep(KafkaConfig.RETRY_BACKOFF_MS);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            
            // Reprocess
            processRecord(record);
        } else {
            logger.error("Max retries exceeded for order: {}. Sending to DLQ", orderId);
            sendToDLQ(order);
            retryMap.remove(orderId);
        }
    }
    
    /**
     * Send failed message to Dead Letter Queue
     */
    private void sendToDLQ(Order order) {
        ProducerRecord<String, Order> dlqRecord = 
            new ProducerRecord<>(KafkaConfig.DLQ_TOPIC, order.getOrderId(), order);
        
        try {
            dlqProducer.send(dlqRecord).get();
            logger.info("Sent order to DLQ: orderId={}", order.getOrderId());
        } catch (Exception e) {
            logger.error("Failed to send order to DLQ: {}", order.getOrderId(), e);
        }
    }
    
    /**
     * Shutdown the consumer gracefully
     */
    public void shutdown() {
        logger.info("Shutting down OrderConsumer...");
        running.set(false);
        consumer.wakeup();
    }
    
    /**
     * Close resources
     */
    public void close() {
        logger.info("Closing OrderConsumer resources...");
        consumer.close();
        dlqProducer.close();
        priceAggregator.printFinalStats();
    }
    
    /**
     * Main method to run the consumer
     */
    public static void main(String[] args) {
        OrderConsumer consumer = new OrderConsumer();
        
        // Add shutdown hook
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            consumer.shutdown();
            consumer.close();
        }));
        
        // Start consuming
        consumer.consumeOrders();
    }
}
