# PowerShell Script to run Kafka Order Processing System

Write-Host "===================================" -ForegroundColor Cyan
Write-Host "Kafka Order Processing System" -ForegroundColor Cyan
Write-Host "===================================" -ForegroundColor Cyan
Write-Host ""

# Check if Maven is available
$mvnExists = Get-Command mvn -ErrorAction SilentlyContinue
if (-not $mvnExists) {
    Write-Host "Maven not found in PATH!" -ForegroundColor Red
    Write-Host "Please install Maven or use an IDE" -ForegroundColor Yellow
    Write-Host ""
    Write-Host "Quick fix: Install with Chocolatey" -ForegroundColor Yellow
    Write-Host "  choco install maven -y" -ForegroundColor White
    pause
    exit 1
}

# Check if Docker is running
try {
    docker ps | Out-Null
} catch {
    Write-Host "Docker is not running!" -ForegroundColor Red
    Write-Host "Please start Docker Desktop" -ForegroundColor Yellow
    pause
    exit 1
}

Write-Host "[1/4] Starting Kafka infrastructure..." -ForegroundColor Green
docker-compose up -d
if ($LASTEXITCODE -ne 0) {
    Write-Host "Failed to start Docker services!" -ForegroundColor Red
    pause
    exit 1
}

Write-Host "[2/4] Waiting for services to initialize (30 seconds)..." -ForegroundColor Green
Start-Sleep -Seconds 30

Write-Host "[3/4] Building project..." -ForegroundColor Green
mvn clean compile
if ($LASTEXITCODE -ne 0) {
    Write-Host "Build failed!" -ForegroundColor Red
    pause
    exit 1
}

Write-Host "[4/4] Project ready!" -ForegroundColor Green
Write-Host ""
Write-Host "===================================" -ForegroundColor Cyan
Write-Host "Next Steps:" -ForegroundColor Cyan
Write-Host "===================================" -ForegroundColor Cyan
Write-Host "1. Open a NEW terminal and run:" -ForegroundColor Yellow
Write-Host "   mvn exec:java -Dexec.mainClass=`"com.kafka.orders.consumer.OrderConsumer`"" -ForegroundColor White
Write-Host ""
Write-Host "2. Open ANOTHER terminal and run:" -ForegroundColor Yellow
Write-Host "   mvn exec:java -Dexec.mainClass=`"com.kafka.orders.producer.OrderProducer`"" -ForegroundColor White
Write-Host ""
Write-Host "3. Open browser: http://localhost:8080 (Kafka UI)" -ForegroundColor Yellow
Write-Host ""
Write-Host "To stop: docker-compose down" -ForegroundColor Yellow
Write-Host "===================================" -ForegroundColor Cyan
pause
