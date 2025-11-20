# Running the Kafka Order Processing System

This guide provides step-by-step instructions to run and demonstrate the system.

## Step 1: Start Kafka Infrastructure

Open PowerShell and navigate to the project directory:

```powershell
cd "d:\kafka order processing system"

# Start all services
docker-compose up -d

# Wait 30-60 seconds for services to initialize

# Verify services are running
docker-compose ps
```

Expected output:
```
NAME              STATUS    PORTS
kafka             Up        0.0.0.0:9092->9092/tcp, ...
schema-registry   Up        0.0.0.0:8081->8081/tcp
zookeeper         Up        0.0.0.0:2181->2181/tcp
kafka-ui          Up        0.0.0.0:8080->8080/tcp
```

## Step 2: Build the Project

```powershell
# Generate Avro classes and compile
mvn clean install
```

This will:
1. Generate `Order.java` from `order.avsc`
2. Compile all Java classes
3. Run tests (if any)
4. Create JAR file

## Step 3: Run the Consumer (Terminal 1)

Open a PowerShell terminal:

```powershell
cd "d:\kafka order processing system"

# Start the consumer
mvn exec:java -Dexec.mainClass="com.kafka.orders.consumer.OrderConsumer"
```

You should see:
```
OrderConsumer initialized and subscribed to: orders
Starting to consume orders...
```

## Step 4: Run the Producer (Terminal 2)

Open a NEW PowerShell terminal:

```powershell
cd "d:\kafka order processing system"

# Produce 50 orders with 1 second delay
mvn exec:java -Dexec.mainClass="com.kafka.orders.producer.OrderProducer"

# Or customize: 100 orders with 500ms delay
mvn exec:java -Dexec.mainClass="com.kafka.orders.producer.OrderProducer" -Dexec.args="100 500"
```

## Step 5: Observe the System

### In Producer Terminal
You'll see messages like:
```
Sent order: orderId=ORDER-1732109876543, product=Item3, price=456.78 -> partition=0, offset=0
Sent order: orderId=ORDER-1732109877543, product=Item1, price=123.45 -> partition=0, offset=1
```

### In Consumer Terminal
You'll see:
```
Successfully processed order: orderId=ORDER-1732109876543, product=Item3, price=456.78
Simulated temporary failure for order: ORDER-1732109877544
Retry 1/3 for order: ORDER-1732109877544
Successfully processed order: orderId=ORDER-1732109877544, product=Item2, price=234.56

=== Price Statistics ===
Orders processed: 10
Running average price: $345.67
Min price: $23.45
Max price: $987.23
=======================
```

### Dead Letter Queue (DLQ)
When a message fails 3 times:
```
Max retries exceeded for order: ORDER-1732109878888. Sending to DLQ
Sent order to DLQ: orderId=ORDER-1732109878888
```

## Step 6: Monitor via Kafka UI

1. Open browser: `http://localhost:8080`
2. Click on **Topics**
3. View messages in:
   - `orders` - Main topic
   - `orders-dlq` - Failed messages

## Step 7: Stop the System

### Stop Consumer and Producer
Press `Ctrl+C` in both terminals

### Stop Docker Services
```powershell
docker-compose down
```

Or keep running for next demo:
```powershell
# Just stop (keeps data)
docker-compose stop

# Restart later
docker-compose start
```

## Demonstration Checklist

✅ **Avro Serialization**: Check Schema Registry at `http://localhost:8081/subjects`  
✅ **Producer**: Watch orders being sent with different products and prices  
✅ **Consumer**: See orders being processed  
✅ **Real-time Aggregation**: Statistics printed every 10 orders  
✅ **Retry Logic**: Observe retry attempts for failed messages  
✅ **DLQ**: Check `orders-dlq` topic in Kafka UI for failed messages  

## Quick Test Commands

### Check Kafka Topics
```powershell
docker exec -it kafka kafka-topics --list --bootstrap-server localhost:9092
```

### Check Schema Registry
```powershell
curl http://localhost:8081/subjects
```

### View Logs
```powershell
# View application logs
cat logs/kafka-orders.log

# View Docker logs
docker-compose logs kafka
docker-compose logs schema-registry
```

## Troubleshooting

### Issue: "Connection refused to localhost:9092"
**Solution**: Wait for Kafka to fully start (30-60 seconds after `docker-compose up`)

### Issue: "Schema Registry not available"
**Solution**: 
```powershell
docker-compose restart schema-registry
# Wait 10 seconds
curl http://localhost:8081/subjects
```

### Issue: Build fails with "Source option 5 is no longer supported"
**Solution**: Verify Java 21:
```powershell
java -version  # Should show "21.x.x"
```

### Issue: Port 9092 already in use
**Solution**: 
```powershell
# Stop any existing Kafka
docker-compose down
# Or change port in docker-compose.yml
```

## Performance Tips

- **Fast Demo**: Use 100-200ms delay for rapid message flow
- **Clear View**: Use 1000-2000ms delay to see each message
- **Load Test**: Produce 1000+ messages with 100ms delay

## Example Full Demo Flow

```powershell
# 1. Start infrastructure
docker-compose up -d
Start-Sleep -Seconds 60

# 2. Build project
mvn clean install

# 3. In Terminal 1: Start consumer
mvn exec:java -Dexec.mainClass="com.kafka.orders.consumer.OrderConsumer"

# 4. In Terminal 2: Start producer (100 orders, 500ms delay)
mvn exec:java -Dexec.mainClass="com.kafka.orders.producer.OrderProducer" -Dexec.args="100 500"

# 5. Open browser to http://localhost:8080

# 6. Watch the magic happen! ✨
```

---

**Ready to demonstrate!** 🚀
