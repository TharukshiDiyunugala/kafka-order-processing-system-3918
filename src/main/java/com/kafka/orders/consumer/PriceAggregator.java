package com.kafka.orders.consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Real-time price aggregator that calculates running average
 */
public class PriceAggregator {
    
    private static final Logger logger = LoggerFactory.getLogger(PriceAggregator.class);
    
    private final AtomicLong totalPrice = new AtomicLong(0);
    private final AtomicInteger count = new AtomicInteger(0);
    private volatile double runningAverage = 0.0;
    
    // Track min and max for additional statistics
    private volatile float minPrice = Float.MAX_VALUE;
    private volatile float maxPrice = Float.MIN_VALUE;
    
    /**
     * Add a price and update the running average
     */
    public synchronized void addPrice(float price) {
        // Update count
        int newCount = count.incrementAndGet();
        
        // Update total (convert float to long by multiplying by 100 for precision)
        long priceCents = (long) (price * 100);
        long newTotal = totalPrice.addAndGet(priceCents);
        
        // Calculate new running average
        runningAverage = (double) newTotal / (newCount * 100);
        
        // Update min/max
        if (price < minPrice) {
            minPrice = price;
        }
        if (price > maxPrice) {
            maxPrice = price;
        }
        
        // Log statistics every 10 orders
        if (newCount % 10 == 0) {
            logStatistics();
        }
    }
    
    /**
     * Get the current running average
     */
    public double getRunningAverage() {
        return runningAverage;
    }
    
    /**
     * Get total count of prices processed
     */
    public int getCount() {
        return count.get();
    }
    
    /**
     * Get minimum price
     */
    public float getMinPrice() {
        return minPrice;
    }
    
    /**
     * Get maximum price
     */
    public float getMaxPrice() {
        return maxPrice;
    }
    
    /**
     * Log current statistics
     */
    private void logStatistics() {
        logger.info("=== Price Statistics ===");
        logger.info("Orders processed: {}", count.get());
        logger.info("Running average price: ${:.2f}", runningAverage);
        logger.info("Min price: ${:.2f}", minPrice);
        logger.info("Max price: ${:.2f}", maxPrice);
        logger.info("=======================");
    }
    
    /**
     * Print final statistics
     */
    public void printFinalStats() {
        logger.info("\n=== FINAL PRICE STATISTICS ===");
        logger.info("Total orders processed: {}", count.get());
        logger.info("Final running average price: ${:.2f}", runningAverage);
        logger.info("Minimum price: ${:.2f}", minPrice);
        logger.info("Maximum price: ${:.2f}", maxPrice);
        logger.info("==============================\n");
    }
}
