# K-Store Microservices Startup Monitor
# This script monitors the startup sequence completion

Write-Host "🚀 K-Store Microservices Startup Monitor 🚀" -ForegroundColor Green
Write-Host ""

$services = @(
    @{Name="Discovery Server"; Port=8761; Url="http://localhost:8761/actuator/health"},
    @{Name="API Gateway"; Port=8080; Url="http://localhost:8080/actuator/health"},
    @{Name="Product Service"; Port=8083; Url="http://localhost:8083/actuator/health"}
)

$maxWaitTime = 180 # 3 minutes
$startTime = Get-Date
$allServicesUp = $false

while (-not $allServicesUp -and ((Get-Date) - $startTime).TotalSeconds -lt $maxWaitTime) {
    $allUp = $true
    Write-Host "⏰ $([int]((Get-Date) - $startTime).TotalSeconds)s - Checking services..." -ForegroundColor Cyan
    
    foreach($service in $services) {
        try {
            $result = Invoke-RestMethod -Uri $service.Url -TimeoutSec 3
            if($result.status -eq "UP") {
                Write-Host "   ✅ $($service.Name): UP" -ForegroundColor Green
            } else {
                Write-Host "   ⚠️ $($service.Name): $($result.status)" -ForegroundColor Yellow
                $allUp = $false
            }
        } catch {
            Write-Host "   ❌ $($service.Name): Starting..." -ForegroundColor Red
            $allUp = $false
        }
    }
    
    if($allUp) {
        $allServicesUp = $true
        Write-Host ""
        Write-Host "🎉 ALL SERVICES ARE UP! Testing gateway routing..." -ForegroundColor Green
        
        try {
            $response = Invoke-RestMethod -Uri "http://localhost:8080/k-store-product-service/api/products" -TimeoutSec 10
            Write-Host "✅ STARTUP SEQUENCE COMPLETED SUCCESSFULLY!" -ForegroundColor Green
            Write-Host "✅ Gateway routing is operational!" -ForegroundColor Green
            Write-Host "✅ Response: $($response.message)" -ForegroundColor Green
        } catch {
            Write-Host "⚠️ Services up but gateway routing still propagating..." -ForegroundColor Yellow
            Write-Host "   This is normal - wait 30-60 more seconds" -ForegroundColor White
        }
        break
    } else {
        Start-Sleep 10
    }
}

if(-not $allServicesUp) {
    Write-Host "⚠️ Startup taking longer than expected. Check logs for issues." -ForegroundColor Yellow
}

Write-Host ""
Write-Host "📊 Final Status Summary:" -ForegroundColor Blue
foreach($service in $services) {
    try {
        $result = Invoke-RestMethod -Uri $service.Url -TimeoutSec 3
        Write-Host "   $($service.Name): ✅ $($result.status)" -ForegroundColor Green
    } catch {
        Write-Host "   $($service.Name): ❌ Not Ready" -ForegroundColor Red
    }
}
