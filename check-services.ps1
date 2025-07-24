# Service Status Check Script for Windows PowerShell

Write-Host "🔍 CHECKING SERVICE REGISTRATION STATUS" -ForegroundColor Cyan
Write-Host "==========================================" -ForegroundColor Cyan

Write-Host ""
Write-Host "📊 Eureka Discovery Server:" -ForegroundColor Yellow
try { 
    $eureka = Invoke-RestMethod -Uri "http://localhost:8761/actuator/health" -TimeoutSec 5
    Write-Host "Status: $($eureka.status)" -ForegroundColor Green
} catch { 
    Write-Host "Eureka Server not accessible" -ForegroundColor Red 
}

Write-Host ""
Write-Host "🌐 API Gateway:" -ForegroundColor Yellow
try { 
    $gateway = Invoke-RestMethod -Uri "http://localhost:8080/actuator/health" -TimeoutSec 5
    Write-Host "Status: $($gateway.status)" -ForegroundColor Green
} catch { 
    Write-Host "API Gateway not accessible" -ForegroundColor Red 
}

Write-Host ""
Write-Host "📦 Product Service:" -ForegroundColor Yellow
try { 
    $product = Invoke-RestMethod -Uri "http://localhost:8083/actuator/health" -TimeoutSec 5
    Write-Host "Status: $($product.status)" -ForegroundColor Green
} catch { 
    Write-Host "Product Service not accessible" -ForegroundColor Red 
}

Write-Host ""
Write-Host "🔗 Service Discovery Test (via Gateway):" -ForegroundColor Yellow
try { 
    $categories = Invoke-RestMethod -Uri "http://localhost:8080/products/api/categories" -TimeoutSec 5
    Write-Host "Gateway routing working - Found $($categories.Count) categories" -ForegroundColor Green
} catch { 
    Write-Host "Gateway routing not working: $($_.Exception.Message)" -ForegroundColor Red 
}

Write-Host ""
Write-Host "🏷️ Registered Services in Eureka:" -ForegroundColor Yellow
try { 
    $registry = Invoke-RestMethod -Uri "http://localhost:8761/eureka/apps" -Headers @{"Accept"="application/json"} -TimeoutSec 5
    if ($registry.applications.application) {
        $registry.applications.application | ForEach-Object { 
            Write-Host "✅ $($_.name) - $($_.instance.Count) instance(s)" -ForegroundColor Green 
        }
    } else {
        Write-Host "No services registered" -ForegroundColor Yellow
    }
} catch { 
    Write-Host "Could not fetch Eureka registry: $($_.Exception.Message)" -ForegroundColor Red 
}
