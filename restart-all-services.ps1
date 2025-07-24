# Stop all Java processes and restart services one by one
Write-Host "=== STOPPING ALL JAVA PROCESSES ==="
Get-Process -Name "java" -ErrorAction SilentlyContinue | Stop-Process -Force
Start-Sleep -Seconds 5

Write-Host "=== STARTING EUREKA SERVER ==="
$eurekaProcess = Start-Process -FilePath "java" -ArgumentList "-jar", "k-store-discovery-server\target\k-store-discovery-server-1.0.0.jar" -WorkingDirectory "c:\Users\Kripa\my-projects" -PassThru -WindowStyle Minimized
Write-Host "Eureka PID: $($eurekaProcess.Id)"
Start-Sleep -Seconds 25

Write-Host "=== CHECKING EUREKA HEALTH ==="
try {
    $eurekaHealth = Invoke-RestMethod -Uri "http://localhost:8761/actuator/health" -Method GET -TimeoutSec 5
    Write-Host "Eureka Status: $($eurekaHealth.status)"
} catch {
    Write-Host "Eureka not ready: $_"
}

Write-Host "=== STARTING USER SERVICE ==="
$userProcess = Start-Process -FilePath "java" -ArgumentList "-Dspring.profiles.active=local", "-Dlogging.level.com.netflix.eureka=DEBUG", "-jar", "k-store-user-service\target\k-store-user-service-1.0.0.jar" -WorkingDirectory "c:\Users\Kripa\my-projects" -PassThru -WindowStyle Minimized
Write-Host "User Service PID: $($userProcess.Id)"
Start-Sleep -Seconds 15

Write-Host "=== CHECKING USER SERVICE HEALTH ==="
try {
    $userHealth = Invoke-RestMethod -Uri "http://localhost:8081/actuator/health" -Method GET -TimeoutSec 5
    Write-Host "User Service Status: $($userHealth.status)"
} catch {
    Write-Host "User Service not ready: $_"
}

Write-Host "=== STARTING PRODUCT SERVICE ==="
$productProcess = Start-Process -FilePath "java" -ArgumentList "-Dspring.profiles.active=local", "-jar", "k-store-product-service\target\k-store-product-service-1.0.0.jar" -WorkingDirectory "c:\Users\Kripa\my-projects" -PassThru -WindowStyle Minimized
Write-Host "Product Service PID: $($productProcess.Id)"
Start-Sleep -Seconds 15

Write-Host "=== CHECKING PRODUCT SERVICE HEALTH ==="
try {
    $productHealth = Invoke-RestMethod -Uri "http://localhost:8083/actuator/health" -Method GET -TimeoutSec 5
    Write-Host "Product Service Status: $($productHealth.status)"
} catch {
    Write-Host "Product Service not ready: $_"
}

Write-Host "=== STARTING API GATEWAY ==="
$gatewayProcess = Start-Process -FilePath "java" -ArgumentList "-Dspring.profiles.active=local", "-jar", "k-store-api-gateway\target\k-store-api-gateway-1.0.0.jar" -WorkingDirectory "c:\Users\Kripa\my-projects" -PassThru -WindowStyle Minimized
Write-Host "API Gateway PID: $($gatewayProcess.Id)"
Start-Sleep -Seconds 15

Write-Host "=== FINAL HEALTH CHECK ==="
$services = @(
    @{Port=8761; Name="Eureka Server"},
    @{Port=8081; Name="User Service"}, 
    @{Port=8083; Name="Product Service"},
    @{Port=8080; Name="API Gateway"}
)

foreach ($service in $services) {
    try {
        $response = Invoke-RestMethod -Uri "http://localhost:$($service.Port)/actuator/health" -Method GET -TimeoutSec 3
        Write-Host "$($service.Name): ✓ UP"
    } catch {
        Write-Host "$($service.Name): ✗ DOWN"
    }
}

Write-Host "=== EUREKA REGISTRY CHECK ==="
Start-Sleep -Seconds 10
try {
    $response = Invoke-RestMethod -Uri "http://localhost:8761/eureka/apps" -Method GET
    if ($response.applications.application) {
        Write-Host "Registered services:"
        if ($response.applications.application -is [array]) {
            $response.applications.application | ForEach-Object {
                Write-Host "  - $($_.name)"
            }
        } else {
            Write-Host "  - $($response.applications.application.name)"
        }
    } else {
        Write-Host "No services registered in Eureka"
    }
} catch {
    Write-Host "Error checking Eureka registry: $_"
}

Write-Host "=== STARTUP COMPLETE ==="
