# Maven Installation Guide

Since Maven requires administrator privileges to install via Chocolatey, here are your options:

## Option 1: Install Maven with Admin Rights (Recommended)

1. **Right-click** on PowerShell
2. Select **"Run as Administrator"**
3. Run:
```powershell
choco install maven -y
```
4. Restart your terminal
5. Verify: `mvn -version`

## Option 2: Manual Maven Installation

1. Download Maven from: https://maven.apache.org/download.cgi
   - Download the **Binary zip archive** (e.g., `apache-maven-3.9.5-bin.zip`)

2. Extract to a folder (e.g., `C:\Program Files\Apache\maven`)

3. Add Maven to PATH:
   ```powershell
   # Open System Properties > Environment Variables
   # Or run this in PowerShell:
   [Environment]::SetEnvironmentVariable("Path", $env:Path + ";C:\Program Files\Apache\maven\bin", "User")
   ```

4. Restart PowerShell

5. Verify:
   ```powershell
   mvn -version
   ```

## Option 3: Use IntelliJ IDEA (No Maven Install Needed)

1. **Download IntelliJ IDEA Community** (free): https://www.jetbrains.com/idea/download/
2. Open IntelliJ IDEA
3. **File → Open** → Select `d:\kafka order processing system`
4. IntelliJ will use its **bundled Maven** automatically
5. Right-click `OrderProducer.java` → **Run**
6. Right-click `OrderConsumer.java` → **Run**

## Option 4: Use VS Code with Maven Extension

1. Install the **Extension Pack for Java** in VS Code
2. VS Code will download Maven automatically
3. Open the project folder
4. Use the Command Palette (Ctrl+Shift+P) → **Java: Build Workspace**

## Option 5: Use Docker to Build (Advanced)

```powershell
# Build using Maven Docker image
docker run -it --rm -v "${PWD}:/app" -w /app maven:3.9-eclipse-temurin-20 mvn clean compile

# Run producer
docker run -it --rm -v "${PWD}:/app" -w /app maven:3.9-eclipse-temurin-20 mvn exec:java -Dexec.mainClass="com.kafka.orders.producer.OrderProducer"
```

## Verify Your Setup

After installing Maven, verify everything:

```powershell
# Check Java
java -version
# Should show: Java 20

# Check Maven
mvn -version
# Should show: Apache Maven 3.x.x

# Build the project
cd "d:\kafka order processing system"
mvn clean compile

# You should see: BUILD SUCCESS
```

## Quick Test Without Building

If you want to test Kafka infrastructure first:

```powershell
# Start Kafka
docker-compose up -d

# Access Kafka UI
start http://localhost:8080
```

---

**Recommended**: Use **IntelliJ IDEA Community** (Option 3) - it's the easiest way to run the project without manual Maven installation.
