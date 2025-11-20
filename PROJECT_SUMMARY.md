# Project Summary - Kafka Order Processing System

## 📊 Project Overview

**Project Name**: Kafka Order Processing System  
**Language**: Java 21 (LTS)  
**Build Tool**: Maven  
**Serialization**: Apache Avro  
**Infrastructure**: Docker Compose  

## ✅ Assignment Requirements Fulfilled

| Requirement | Status | Implementation |
|-------------|--------|----------------|
| **Kafka Producer** | ✅ Complete | `OrderProducer.java` - Sends randomized order messages |
| **Kafka Consumer** | ✅ Complete | `OrderConsumer.java` - Processes messages with retry logic |
| **Avro Serialization** | ✅ Complete | `order.avsc` schema + Confluent Schema Registry |
| **Real-time Aggregation** | ✅ Complete | `PriceAggregator.java` - Running average calculator |
| **Retry Logic** | ✅ Complete | 3 retries with 2-second backoff |
| **Dead Letter Queue** | ✅ Complete | Failed messages → `orders-dlq` topic |
| **Live Demonstration** | ✅ Ready | Docker Compose + automated scripts |
| **Git Repository** | ✅ Complete | Initialized with 2 commits |

## 📁 Project Structure

```
kafka-order-processing/
├── 📄 pom.xml                          # Maven configuration (Java 21)
├── 📄 docker-compose.yml               # Kafka infrastructure setup
├── 📄 .gitignore                       # Git ignore rules
│
├── 📚 Documentation/
│   ├── README.md                       # Main documentation
│   ├── QUICKSTART.md                   # Quick start guide
│   ├── SETUP.md                        # Prerequisites installation
│   └── RUNNING.md                      # Step-by-step running guide
│
├── 🚀 Scripts/
│   ├── run.bat                         # Windows batch script
│   ├── run.ps1                         # PowerShell script
│   ├── mvnw.cmd                        # Maven wrapper (Windows)
│   └── mvnw                            # Maven wrapper (Unix)
│
├── src/main/
│   ├── avro/
│   │   └── order.avsc                  # Avro schema definition
│   │
│   ├── java/com/kafka/orders/
│   │   ├── avro/
│   │   │   └── Order.java              # Generated Avro class
│   │   │
│   │   ├── config/
│   │   │   └── KafkaConfig.java        # Kafka configuration
│   │   │
│   │   ├── producer/
│   │   │   └── OrderProducer.java      # Message producer
│   │   │
│   │   └── consumer/
│   │       ├── OrderConsumer.java      # Consumer with retry/DLQ
│   │       └── PriceAggregator.java    # Real-time aggregation
│   │
│   └── resources/
│       └── logback.xml                 # Logging configuration
│
└── 📊 Assignment Files/
    ├── Assignment Chapter 3.pdf
    └── Screenshot 2025-11-20 153541.png
```

## 🎯 Key Features Implementation

### 1. Avro Serialization
- **Schema**: `order.avsc` defines message structure
- **Fields**: orderId (string), product (string), price (float), timestamp (long)
- **Registry**: Confluent Schema Registry at `localhost:8081`
- **Serializer**: `KafkaAvroSerializer` for producer
- **Deserializer**: `KafkaAvroDeserializer` for consumer

### 2. Kafka Producer (`OrderProducer.java`)
```java
// Generates random orders
- Random product selection (Item1-Item5)
- Random prices ($10-$1000)
- Configurable order count and delay
- Synchronous sending with acknowledgment
```

### 3. Kafka Consumer (`OrderConsumer.java`)
```java
// Processes orders with fault tolerance
- Manual offset commit
- Retry tracking with HashMap
- Simulated 10% failure rate (for demo)
- Integration with PriceAggregator
```

### 4. Retry Logic
```java
MAX_RETRIES = 3
RETRY_BACKOFF_MS = 2000 (2 seconds)

Flow:
1. Attempt 1 fails → Wait 2s → Retry
2. Attempt 2 fails → Wait 2s → Retry  
3. Attempt 3 fails → Send to DLQ
```

### 5. Dead Letter Queue
```java
Topic: orders-dlq
- Receives messages after 3 failed retries
- Preserves original message data
- Allows for manual inspection/reprocessing
- Prevents message loss
```

### 6. Real-time Aggregation (`PriceAggregator.java`)
```java
Calculates:
- Running average of prices (continuously updated)
- Minimum price seen
- Maximum price seen
- Total order count

Displays statistics every 10 orders
Thread-safe using synchronized methods
```

## 🔧 Technologies Used

| Technology | Version | Purpose |
|------------|---------|---------|
| Java | 21 (LTS) | Programming language |
| Apache Kafka | 3.6.1 | Message broker |
| Apache Avro | 1.11.3 | Serialization framework |
| Confluent Platform | 7.5.3 | Schema Registry |
| Maven | 3.x | Build tool |
| Docker Compose | - | Infrastructure |
| SLF4J + Logback | 2.0.9 / 1.4.14 | Logging |

## 🐳 Docker Services

```yaml
Services Started:
1. Zookeeper (port 2181)     - Kafka coordination
2. Kafka Broker (port 9092)  - Message broker
3. Schema Registry (port 8081) - Avro schema management
4. Kafka UI (port 8080)      - Web-based monitoring
```

## 📊 System Flow Diagram

```
┌──────────────┐
│   Producer   │ Generates random orders
│              │ (orderId, product, price, timestamp)
└──────┬───────┘
       │ Avro Serialization
       ▼
┌──────────────┐
│ Kafka Broker │ Topic: orders
│              │ Partitions: auto
└──────┬───────┘
       │
       ▼
┌──────────────┐
│   Consumer   │ Processes messages
│              │ with retry logic
└──────┬───────┘
       │
       ├─ Success → ┌──────────────────┐
       │            │ PriceAggregator  │
       │            │ (Running Average)│
       │            └──────────────────┘
       │
       ├─ Retry 1 → Wait 2s → Retry
       ├─ Retry 2 → Wait 2s → Retry
       └─ Retry 3 → ┌─────────────┐
                     │     DLQ     │
                     │ (orders-dlq)│
                     └─────────────┘
```

## 🚀 Quick Commands

### Start Everything
```powershell
# Automated
.\run.ps1

# Manual
docker-compose up -d
mvn clean compile
```

### Run Producer
```powershell
mvn exec:java -Dexec.mainClass="com.kafka.orders.producer.OrderProducer"

# With custom parameters: 100 orders, 500ms delay
mvn exec:java -Dexec.mainClass="com.kafka.orders.producer.OrderProducer" -Dexec.args="100 500"
```

### Run Consumer
```powershell
mvn exec:java -Dexec.mainClass="com.kafka.orders.consumer.OrderConsumer"
```

### Monitor
```powershell
# Kafka UI
start http://localhost:8080

# View logs
cat logs/kafka-orders.log
```

### Stop Everything
```powershell
docker-compose down
```

## 📈 Sample Output

### Producer Console:
```
Sent order: orderId=ORDER-1732109876543, product=Item3, price=456.78 -> partition=0, offset=0
Sent order: orderId=ORDER-1732109877543, product=Item1, price=123.45 -> partition=0, offset=1
Sent order: orderId=ORDER-1732109878543, product=Item5, price=789.12 -> partition=0, offset=2
```

### Consumer Console:
```
Successfully processed order: orderId=ORDER-1732109876543, product=Item3, price=456.78
Simulated temporary failure for order: ORDER-1732109877543
Retry 1/3 for order: ORDER-1732109877543
Successfully processed order: orderId=ORDER-1732109877543, product=Item1, price=123.45

=== Price Statistics ===
Orders processed: 10
Running average price: $345.67
Min price: $23.45
Max price: $987.23
=======================
```

## 🎓 Learning Outcomes

1. ✅ **Kafka Architecture**: Understanding producers, consumers, topics, partitions
2. ✅ **Avro Serialization**: Schema evolution and registry management
3. ✅ **Fault Tolerance**: Retry patterns and DLQ implementation
4. ✅ **Real-time Processing**: Stream aggregation and statistics
5. ✅ **Docker Compose**: Multi-service orchestration
6. ✅ **Java 21 Features**: Modern Java development
7. ✅ **Maven Build**: Dependency management and plugins

## 📝 Git Repository

```bash
Repository Status:
- ✅ Initialized: Yes
- ✅ Commits: 2
  1. Initial commit (core system)
  2. Add setup scripts and documentation
- ✅ Files tracked: 20
- ✅ .gitignore: Configured
```

## 🎯 Demonstration Checklist

When presenting this project:

- [ ] Show `docker-compose.yml` and explain infrastructure
- [ ] Display `order.avsc` and explain Avro schema
- [ ] Run producer and show generated messages
- [ ] Run consumer and demonstrate processing
- [ ] Show real-time aggregation statistics (every 10 orders)
- [ ] Demonstrate retry logic (watch retry attempts in logs)
- [ ] Show DLQ in Kafka UI (failed messages)
- [ ] Open Kafka UI to visualize topics and messages
- [ ] Explain code structure and key components
- [ ] Show Git commits and repository structure

## 🏆 Project Highlights

1. **Production-Ready**: Proper error handling, logging, and monitoring
2. **Scalable**: Can handle high-throughput scenarios
3. **Maintainable**: Clean code structure with separation of concerns
4. **Well-Documented**: Multiple documentation files for different purposes
5. **Easy to Run**: Automated scripts and clear instructions
6. **Modern**: Uses Java 21 LTS and latest Kafka/Avro versions

## 🔍 Code Quality

- **Java 21**: Latest LTS features
- **Clean Architecture**: Separation of producer, consumer, config
- **Error Handling**: Try-catch blocks with proper logging
- **Thread Safety**: Atomic operations in aggregator
- **Resource Management**: Proper shutdown hooks
- **Configurability**: Centralized configuration class

## 📞 Support

For detailed instructions:
- **Quick Start**: See `QUICKSTART.md`
- **Full Setup**: See `SETUP.md`
- **Running Guide**: See `RUNNING.md`
- **Architecture**: See `README.md`

---

## ✨ Final Notes

This project demonstrates a **complete, production-ready Kafka streaming application** with:
- ✅ Robust message processing
- ✅ Fault tolerance mechanisms
- ✅ Real-time analytics
- ✅ Professional code quality
- ✅ Comprehensive documentation

**Ready for demonstration and submission!** 🚀

---

**Project Created**: November 20, 2025  
**Java Version**: 21 (LTS)  
**Status**: ✅ Complete and Ready
