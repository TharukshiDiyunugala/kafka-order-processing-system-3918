@echo off
REM Windows Batch Script to run Kafka Order Processing System

echo ===================================
echo Kafka Order Processing System
echo ===================================
echo.

REM Check if Maven is available
where mvn >nul 2>nul
if %ERRORLEVEL% NEQ 0 (
    echo Maven not found in PATH!
    echo Please install Maven or use run-with-ide.bat
    pause
    exit /b 1
)

REM Check if Docker is running
docker ps >nul 2>nul
if %ERRORLEVEL% NEQ 0 (
    echo Docker is not running!
    echo Please start Docker Desktop
    pause
    exit /b 1
)

echo [1/4] Starting Kafka infrastructure...
docker-compose up -d
if %ERRORLEVEL% NEQ 0 (
    echo Failed to start Docker services!
    pause
    exit /b 1
)

echo [2/4] Waiting for services to initialize (30 seconds)...
timeout /t 30 /nobreak >nul

echo [3/4] Building project...
call mvn clean compile
if %ERRORLEVEL% NEQ 0 (
    echo Build failed!
    pause
    exit /b 1
)

echo [4/4] Project ready!
echo.
echo ===================================
echo Next Steps:
echo ===================================
echo 1. Open a NEW terminal and run:
echo    mvn exec:java -Dexec.mainClass="com.kafka.orders.consumer.OrderConsumer"
echo.
echo 2. Open ANOTHER terminal and run:
echo    mvn exec:java -Dexec.mainClass="com.kafka.orders.producer.OrderProducer"
echo.
echo 3. Open browser: http://localhost:8080 (Kafka UI)
echo.
echo To stop: docker-compose down
echo ===================================
pause
