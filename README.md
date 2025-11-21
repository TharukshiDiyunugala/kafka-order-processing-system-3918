# Kafka Order Processing System

A complete Kafka-based order processing system implemented in **Java 21** with **Avro serialization**, featuring real-time price aggregation, retry logic, and Dead Letter Queue (DLQ) support.

##  Features

-  **Avro Serialization**: Schema-based serialization for order messages
-  **Real-time Aggregation**: Running average calculation of order prices
-  **Retry Logic**: Automatic retry with exponential backoff for temporary failures
-  **Dead Letter Queue (DLQ)**: Failed messages are sent to DLQ after max retries
-  **Docker Support**: Complete Docker Compose setup for Kafka ecosystem
-  **Java 20**: Built with modern Java (compatible with Java 17+)

##  Architecture

```
┌─────────────┐       ┌──────────────┐       ┌─────────────┐
│   Producer  │──────▶│ Kafka Broker │──────▶│  Consumer   │
│  (OrderProd)│       │   (orders)   │       │ (OrderCons) │
└─────────────┘       └──────────────┘       └─────────────┘
                              │                      │
                              │                      ▼
                              │              ┌──────────────┐
                              │              │    Price     │
                              │              │  Aggregator  │
                              │              └──────────────┘
                              │                      │
                              ▼                      ▼
                      ┌──────────────┐       (Retry Logic)
                      │     DLQ      │              │
                      │ (orders-dlq) │◀─────────────┘
                      └──────────────┘     (Max retries)
```

##  Order Message Schema

Each order message contains:

| Field | Type | Description |
|-------|------|-------------|
| `orderId` | string | Unique identifier (e.g., "ORDER-1001") |
| `product` | string | Product name (e.g., "Item1", "Item2") |
| `price` | float | Product price (randomized between 10-1000) |
| `timestamp` | long | Order creation timestamp (epoch milliseconds) |

##  Quick Start

### Prerequisites

- **Java 17+** (Java 20 recommended, project uses Java 20)
- **Maven 3.8+** (or use IntelliJ IDEA which has Maven built-in)
- **Docker & Docker Compose**
- **Git**

### 1. Start Kafka Infrastructure

```powershell
# Start Kafka, Zookeeper, Schema Registry, and Kafka UI
docker-compose up -d

# Verify all services are running
docker-compose ps
```

Services will be available at:
- **Kafka Broker**: `localhost:9092`
- **Schema Registry**: `http://localhost:8081`
- **Kafka UI**: `http://localhost:8080` (Web interface)

### 2. Build the Project

```powershell
# Generate Avro classes and build
mvn clean install
```

This will:
- Generate Java classes from Avro schema (`order.avsc`)
- Compile the project with Java 21
- Create executable JAR

### 3. Run the Producer

```powershell
# Produce 50 orders with 1 second delay (default)
mvn exec:java -Dexec.mainClass="com.kafka.orders.producer.OrderProducer"

# Or specify custom parameters: <number_of_orders> <delay_ms>
mvn exec:java -Dexec.mainClass="com.kafka.orders.producer.OrderProducer" -Dexec.args="100 500"
```

### 4. Run the Consumer

Open a new terminal:

```powershell
# Start consumer with retry logic and DLQ
mvn exec:java -Dexec.mainClass="com.kafka.orders.consumer.OrderConsumer"
```

The consumer will:
- Process orders with simulated 10% failure rate
- Retry failed messages up to 3 times
- Send permanently failed messages to DLQ
- Display real-time price statistics every 10 orders

##  Real-time Price Aggregation

The consumer calculates and displays:
- **Running Average**: Continuously updated average price
- **Min Price**: Lowest price seen
- **Max Price**: Highest price seen
- **Order Count**: Total orders processed

Example output:
```
=== Price Statistics ===
Orders processed: 50
Running average price: $512.34
Min price: $15.67
Max price: $987.23
=======================
```

##  Retry Logic & DLQ

### Retry Mechanism
- **Max Retries**: 3 attempts per message
- **Backoff**: 2000ms delay between retries
- **Automatic**: Failed messages are retried transparently

### Dead Letter Queue
- Messages exceeding max retries → DLQ topic (`orders-dlq`)
- Preserves original message for later analysis
- Prevents blocking of message processing

##  Monitoring

### Kafka UI (Web Interface)
Visit `http://localhost:8080` to:
- View topics and messages
- Monitor consumer groups
- Inspect schemas in Schema Registry
- Browse DLQ messages

### Logs
Application logs are written to:
- **Console**: Real-time output
- **File**: `logs/kafka-orders.log`

##  Configuration

Edit `src/main/java/com/kafka/orders/config/KafkaConfig.java`:

```java
// Kafka Settings
BOOTSTRAP_SERVERS = "localhost:9092"
SCHEMA_REGISTRY_URL = "http://localhost:8081"

// Topics
ORDERS_TOPIC = "orders"
DLQ_TOPIC = "orders-dlq"

// Retry Configuration
MAX_RETRIES = 3
RETRY_BACKOFF_MS = 2000
```

##  Project Structure

```
kafka-order-processing/
├── src/
│   ├── main/
│   │   ├── avro/
│   │   │   └── order.avsc              # Avro schema definition
│   │   ├── java/com/kafka/orders/
│   │   │   ├── avro/                   # Generated Avro classes
│   │   │   ├── config/
│   │   │   │   └── KafkaConfig.java    # Kafka configuration
│   │   │   ├── producer/
│   │   │   │   └── OrderProducer.java  # Message producer
│   │   │   └── consumer/
│   │   │       ├── OrderConsumer.java  # Consumer with retry/DLQ
│   │   │       └── PriceAggregator.java # Real-time aggregation
│   │   └── resources/
│   │       └── logback.xml             # Logging configuration
├── docker-compose.yml                   # Kafka infrastructure
├── pom.xml                              # Maven configuration
└── README.md                            # This file
```

##  Testing

### Manual Testing

1. **Start infrastructure**: `docker-compose up -d`
2. **Run consumer** in terminal 1
3. **Run producer** in terminal 2
4. **Observe**:
   - Producer logs showing sent messages
   - Consumer logs showing processing and statistics
   - DLQ messages in Kafka UI

### Verify DLQ
```powershell
# Check DLQ topic messages in Kafka UI
# Navigate to: http://localhost:8080 → Topics → orders-dlq
```

##  Troubleshooting

### Connection Refused
```
Error: Connection refused to localhost:9092
Solution: Ensure Docker containers are running: docker-compose ps
```

### Schema Registry Error
```
Error: Failed to register schema
Solution: Verify Schema Registry is up: curl http://localhost:8081
```

### Build Errors
```
Error: Source option 5 is no longer supported
Solution: Ensure Java 21 is installed: java -version
```

##  Technologies Used

- **Java 21** (LTS)
- **Apache Kafka 3.6.1**
- **Apache Avro 1.11.3**
- **Confluent Schema Registry 7.5.3**
- **Maven 3.x**
- **Docker Compose**
- **SLF4J + Logback** (Logging)

##  Assignment Requirements Met

 Real-time order message production  
 Avro serialization for all messages  
 Real-time aggregation (running average of prices)  
 Retry logic for temporary failures  
 Dead Letter Queue for permanent failures  
 Live system demonstration  
 Git repository with complete code  
 Independent research and implementation  

##  License

This project is created for educational purposes as part of a Kafka systems assignment.

##  Diyunugala D.T.N - EG/2020/3918

Created using Java 21 and modern Kafka practices.

---

