# 🚀 Quick Start Guide

## ⚡ Fastest Way to Get Started

### Prerequisites
- Java 21 installed ([Download here](https://adoptium.net/temurin/releases/))
- Docker Desktop running ([Download here](https://www.docker.com/products/docker-desktop/))
- Maven installed OR use an IDE

### Option 1: Automated Script (Windows)

```powershell
# Run this script
.\run.ps1
```

The script will:
1. ✅ Check prerequisites
2. ✅ Start Kafka infrastructure
3. ✅ Build the project
4. ✅ Show you next steps

### Option 2: Manual Steps

```powershell
# 1. Start Kafka
docker-compose up -d
Start-Sleep -Seconds 30

# 2. Build project
mvn clean compile

# 3. Run Consumer (Terminal 1)
mvn exec:java -Dexec.mainClass="com.kafka.orders.consumer.OrderConsumer"

# 4. Run Producer (Terminal 2) - open NEW terminal
mvn exec:java -Dexec.mainClass="com.kafka.orders.producer.OrderProducer"

# 5. Open browser
start http://localhost:8080
```

### Option 3: Use IntelliJ IDEA

1. Open IntelliJ IDEA
2. **File → Open** → Select this folder
3. Wait for Maven import
4. Right-click `OrderConsumer.java` → **Run**
5. Right-click `OrderProducer.java` → **Run**

### Option 4: Use VS Code

1. Install **Extension Pack for Java**
2. Open this folder in VS Code
3. Open `OrderConsumer.java` → Press **F5**
4. Open `OrderProducer.java` → Press **F5**

## 🆘 Need Help?

### Maven not installed?
```powershell
choco install maven -y
# Restart PowerShell after installation
```

### Java not installed?
```powershell
choco install openjdk21 -y
```

### Docker not running?
1. Start Docker Desktop
2. Wait for it to fully start (green icon)

## 📚 Full Documentation

- **SETUP.md** - Complete setup instructions
- **RUNNING.md** - Detailed running guide  
- **README.md** - Full project documentation

## 🎯 What You'll See

### Producer Output:
```
Sent order: orderId=ORDER-123, product=Item3, price=456.78
```

### Consumer Output:
```
Successfully processed order: orderId=ORDER-123, product=Item3, price=456.78

=== Price Statistics ===
Orders processed: 10
Running average price: $345.67
=======================
```

### Kafka UI:
Open `http://localhost:8080` to see messages in real-time!

---

**Ready to go! 🎉**
