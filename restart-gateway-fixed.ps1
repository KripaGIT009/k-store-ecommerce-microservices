# API Gateway Startup Script - Fixed Version
Write-Host "🔄 CLEAN API GATEWAY STARTUP 🔄" -ForegroundColor Green
Write-Host ""

# Step 1: Clean shutdown
Write-Host "1. Stopping all Java processes..." -ForegroundColor Blue
Stop-Process -Name "java" -Force -ErrorAction SilentlyContinue
Start-Sleep 5

# Step 2: Start Discovery Server
Write-Host "2. Starting Discovery Server..." -ForegroundColor Blue
cd c:\Users\Kripa\my-projects\k-store-discovery-server
Start-Process powershell -ArgumentList "-Command", "java -jar target\k-store-discovery-server-1.0.0.jar" -WindowStyle Minimized
Start-Sleep 20

# Step 3: Start API Gateway
Write-Host "3. Starting API Gateway..." -ForegroundColor Blue
cd c:\Users\Kripa\my-projects\k-store-api-gateway
Start-Process powershell -ArgumentList "-Command", "java -jar target\k-store-api-gateway-1.0.0.jar" -WindowStyle Normal
Start-Sleep 25

# Step 4: Test API Gateway
Write-Host "4. Testing API Gateway..." -ForegroundColor Blue
try {
    $gw = Invoke-RestMethod -Uri "http://localhost:8080/actuator/health" -TimeoutSec 10
    Write-Host "   ✅ API Gateway: $($gw.status)" -ForegroundColor Green
    Write-Host "🎉 SUCCESS! API Gateway is accessible!" -ForegroundColor Green
} catch {
    Write-Host "   ❌ Still not accessible: $($_.Exception.Message)" -ForegroundColor Red
}

Write-Host ""
Write-Host "📊 Current Status:" -ForegroundColor Blue
Write-Host "Java processes: $((Get-Process -Name 'java' -ErrorAction SilentlyContinue).Count)"
