# Kafka Order Processing System - Setup Guide

## Prerequisites Installation

### 1. Install Java 20 (or Java 17+)

#### Option A: Using Chocolatey (Recommended for Windows)
```powershell
# Install Chocolatey if not already installed
Set-ExecutionPolicy Bypass -Scope Process -Force; [System.Net.ServicePointManager]::SecurityProtocol = [System.Net.ServicePointManager]::SecurityProtocol -bor 3072; iex ((New-Object System.Net.WebClient).DownloadString('https://community.chocolatey.org/install.ps1'))

# Install Java 20
choco install openjdk20 -y

# Verify installation
java -version
```

#### Option B: Manual Installation
1. Download Java 20 from: https://adoptium.net/temurin/releases/
2. Choose **JDK 20** (or 21) for Windows x64
3. Install and add to PATH
4. Verify: `java -version`

### 2. Install Maven

#### Option A: Using Chocolatey
```powershell
choco install maven -y

# Verify installation
mvn -version
```

#### Option B: Manual Installation
1. Download Maven from: https://maven.apache.org/download.cgi
2. Extract to `C:\Program Files\Apache\maven`
3. Add to PATH: `C:\Program Files\Apache\maven\bin`
4. Verify: `mvn -version`

### 3. Install Docker Desktop

1. Download from: https://www.docker.com/products/docker-desktop/
2. Install Docker Desktop for Windows
3. Start Docker Desktop
4. Verify: `docker --version` and `docker-compose --version`

### 4. Install Git (if not already installed)

```powershell
choco install git -y

# Or download from: https://git-scm.com/download/win
```

## Environment Variables Setup

### Set JAVA_HOME
```powershell
# Find Java installation path
$javaPath = (Get-Command java).Source
Write-Host "Java is at: $javaPath"

# Set JAVA_HOME (adjust path as needed)
[System.Environment]::SetEnvironmentVariable('JAVA_HOME', 'C:\Program Files\Eclipse Adoptium\jdk-21.0.1.12-hotspot', [System.EnvironmentVariableTarget]::Machine)
```

### Set MAVEN_HOME (if manually installed)
```powershell
[System.Environment]::SetEnvironmentVariable('MAVEN_HOME', 'C:\Program Files\Apache\maven', [System.EnvironmentVariableTarget]::Machine)
```

### Refresh PATH
```powershell
$env:Path = [System.Environment]::GetEnvironmentVariable("Path","Machine")
```

## Verify All Prerequisites

```powershell
# Check Java
java -version
# Should show: java version "20" or "21" (both work)

# Check Maven
mvn -version
# Should show: Apache Maven 3.x.x

# Check Docker
docker --version
docker-compose --version

# Check Git
git --version
```

## Quick Start Without Maven

If Maven is not available, you can use Maven Wrapper (included):

```powershell
cd "d:\kafka order processing system"

# Windows
.\mvnw.cmd clean compile

# Build and run producer
.\mvnw.cmd exec:java -Dexec.mainClass="com.kafka.orders.producer.OrderProducer"

# Build and run consumer
.\mvnw.cmd exec:java -Dexec.mainClass="com.kafka.orders.consumer.OrderConsumer"
```

## Alternative: Use IDE

### IntelliJ IDEA (Recommended)
1. Open IntelliJ IDEA
2. File → Open → Select `d:\kafka order processing system`
3. Wait for Maven to import dependencies
4. Right-click `OrderProducer.java` → Run
5. Right-click `OrderConsumer.java` → Run

### VS Code
1. Install extensions:
   - Extension Pack for Java
   - Spring Boot Extension Pack
2. Open folder: `d:\kafka order processing system`
3. Press F5 to run

## Troubleshooting

### "mvn not recognized"
**Solution**: 
- Install Maven using Chocolatey: `choco install maven -y`
- Or use Maven Wrapper: `.\mvnw.cmd` instead of `mvn`
- Restart PowerShell after installation

### "JAVA_HOME not set"
**Solution**:
```powershell
# Find Java path
where java

# Set JAVA_HOME (adjust path)
[System.Environment]::SetEnvironmentVariable('JAVA_HOME', 'C:\Program Files\Eclipse Adoptium\jdk-21.0.1.12-hotspot', [System.EnvironmentVariableTarget]::Machine)

# Restart PowerShell
```

### Docker not starting
**Solution**:
- Enable Virtualization in BIOS
- Enable Hyper-V in Windows Features
- Restart computer

### Port already in use
**Solution**:
```powershell
# Check what's using port 9092
netstat -ano | findstr :9092

# Kill process (replace PID)
taskkill /PID <PID> /F
```

## Next Steps

Once prerequisites are installed, follow **RUNNING.md** for system demonstration.

---

## Quick Installation Script

Save this as `setup.ps1` and run as Administrator:

```powershell
# Enable script execution
Set-ExecutionPolicy Bypass -Scope Process -Force

# Install Chocolatey
[System.Net.ServicePointManager]::SecurityProtocol = [System.Net.ServicePointManager]::SecurityProtocol -bor 3072
iex ((New-Object System.Net.WebClient).DownloadString('https://community.chocolatey.org/install.ps1'))

# Install prerequisites
choco install openjdk21 maven git docker-desktop -y

Write-Host "Installation complete! Please restart your computer."
Write-Host "After restart, run: docker-compose up -d"
```

Run:
```powershell
# As Administrator
.\setup.ps1
```

---

**Need Help?** Check the README.md for detailed documentation.
