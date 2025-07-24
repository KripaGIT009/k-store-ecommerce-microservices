# API Gateway Startup Script
# This script ensures proper startup sequence and error handling

Write-Host "🔄 CLEAN API GATEWAY STARTUP 🔄" -ForegroundColor Green
Write-Host ""

# Step 1: Clean shutdown
Write-Host "1. Stopping all Java processes..." -ForegroundColor Blue
Stop-Process -Name "java" -Force -ErrorAction SilentlyContinue
Start-Sleep 5

# Step 2: Verify ports are free
Write-Host "2. Verifying ports are free..." -ForegroundColor Blue
$port8080 = netstat -ano | findstr ":8080"
if ($port8080) {
    Write-Host "   ⚠️ Port 8080 still in use" -ForegroundColor Yellow
} else {
    Write-Host "   ✅ Port 8080 is free" -ForegroundColor Green
}

# Step 3: Start Discovery Server
Write-Host "3. Starting Discovery Server..." -ForegroundColor Blue
cd c:\Users\Kripa\my-projects\k-store-discovery-server
Start-Process powershell -ArgumentList "-Command", "java -jar target\k-store-discovery-server-1.0.0.jar" -WindowStyle Minimized
Start-Sleep 20

# Step 4: Verify Discovery Server
Write-Host "4. Verifying Discovery Server..." -ForegroundColor Blue
try {
    $disc = Invoke-RestMethod -Uri "http://localhost:8761/actuator/health" -TimeoutSec 5
    Write-Host "   ✅ Discovery Server: $($disc.status)" -ForegroundColor Green
} catch {
    Write-Host "   ❌ Discovery Server not ready" -ForegroundColor Red
}

# Step 5: Start API Gateway
Write-Host "5. Starting API Gateway..." -ForegroundColor Blue
cd c:\Users\Kripa\my-projects\k-store-api-gateway
Start-Process powershell -ArgumentList "-Command", "java -jar target\k-store-api-gateway-1.0.0.jar" -WindowStyle Normal
Start-Sleep 25

# Step 6: Test API Gateway
Write-Host "6. Testing API Gateway..." -ForegroundColor Blue
try {
    $gw = Invoke-RestMethod -Uri "http://localhost:8080/actuator/health" -TimeoutSec 10
    Write-Host "   ✅ API Gateway: $($gw.status)" -ForegroundColor Green
    Write-Host ""
    Write-Host "🎉 SUCCESS! API Gateway is now accessible on http://localhost:8080" -ForegroundColor Green
} catch {
    Write-Host "   ❌ API Gateway failed to start: $($_.Exception.Message)" -ForegroundColor Red
    Write-Host ""
    Write-Host "🔍 Troubleshooting suggestions:" -ForegroundColor Yellow
    Write-Host "   - Check for port conflicts"
    Write-Host "   - Review application logs"
    Write-Host "   - Verify configuration files"
}

# Step 7: Final status
Write-Host ""
Write-Host "📊 Final Status:" -ForegroundColor Blue
Write-Host "   Java processes: $((Get-Process -Name 'java' -ErrorAction SilentlyContinue).Count)"
$ports = netstat -ano | findstr "8080\|8761"
if ($ports) {
    $ports | ForEach-Object { Write-Host "   $_" }
} else {
    Write-Host "   No services listening on expected ports"
}
