# Simple service starter script
Write-Host "Starting User Service..."
$userServiceProcess = Start-Process -FilePath "java" -ArgumentList "-Dspring.profiles.active=local", "-jar", "k-store-user-service\target\k-store-user-service-1.0.0.jar" -WorkingDirectory "c:\Users\Kripa\my-projects" -PassThru -WindowStyle Minimized

Write-Host "Starting Product Service..."  
$productServiceProcess = Start-Process -FilePath "java" -ArgumentList "-Dspring.profiles.active=local", "-jar", "k-store-product-service\target\k-store-product-service-1.0.0.jar" -WorkingDirectory "c:\Users\Kripa\my-projects" -PassThru -WindowStyle Minimized

Write-Host "User Service PID: $($userServiceProcess.Id)"
Write-Host "Product Service PID: $($productServiceProcess.Id)"

Write-Host "Waiting 45 seconds for services to start and register..."
Start-Sleep -Seconds 45

# Check Eureka registry
Write-Host "Checking Eureka registry..."
try {
    $response = Invoke-RestMethod -Uri "http://localhost:8761/eureka/apps" -Method GET
    if ($response.applications.application) {
        Write-Host "Registered services:"
        $response.applications.application | ForEach-Object {
            Write-Host "  - $($_.name)"
        }
    } else {
        Write-Host "No services registered in Eureka"
    }
} catch {
    Write-Host "Failed to check Eureka registry: $_"
}

# Check individual service health
Write-Host "`nChecking service health..."
try {
    $userHealth = Invoke-RestMethod -Uri "http://localhost:8081/actuator/health" -Method GET -TimeoutSec 5
    Write-Host "User Service: $($userHealth.status)"
} catch {
    Write-Host "User Service: Not responding"
}

try {
    $productHealth = Invoke-RestMethod -Uri "http://localhost:8083/actuator/health" -Method GET -TimeoutSec 5
    Write-Host "Product Service: $($productHealth.status)"
} catch {
    Write-Host "Product Service: Not responding"
}
