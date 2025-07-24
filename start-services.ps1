# Start User Service and Product Service with proper error handling
param(
    [switch]$UserService,
    [switch]$ProductService,
    [switch]$All
)

# Function to start a service
function Start-Service {
    param(
        [string]$ServiceName,
        [string]$JarPath,
        [int]$Port
    )
    
    Write-Host "Starting $ServiceName..."
    
    # Check if port is already in use
    $portCheck = Get-NetTCPConnection -LocalPort $Port -ErrorAction SilentlyContinue
    if ($portCheck) {
        Write-Host "$ServiceName port $Port is already in use"
        return
    }
    
    # Start the service
    try {
        $process = Start-Process -FilePath "java" -ArgumentList "-jar", $JarPath -WorkingDirectory "c:\Users\Kripa\my-projects" -PassThru -WindowStyle Hidden
        Write-Host "$ServiceName started with PID: $($process.Id)"
        
        # Wait a bit and check if service is responding
        Start-Sleep -Seconds 10
        try {
            $health = Invoke-RestMethod -Uri "http://localhost:$Port/actuator/health" -Method GET -TimeoutSec 5
            Write-Host "$ServiceName is healthy: $($health.status)"
        } catch {
            Write-Host "$ServiceName health check failed: $_"
        }
    } catch {
        Write-Host "Failed to start $ServiceName: $_"
    }
}

# Start services based on parameters
if ($UserService -or $All) {
    Start-Service -ServiceName "User Service" -JarPath "k-store-user-service\target\k-store-user-service-1.0.0.jar" -Port 8081
}

if ($ProductService -or $All) {
    Start-Service -ServiceName "Product Service" -JarPath "k-store-product-service\target\k-store-product-service-1.0.0.jar" -Port 8083
}

# Check Eureka registration after 30 seconds
if ($UserService -or $ProductService -or $All) {
    Write-Host "Waiting 30 seconds for services to register with Eureka..."
    Start-Sleep -Seconds 30
    
    try {
        $response = Invoke-RestMethod -Uri "http://localhost:8761/eureka/apps" -Method GET
        Write-Host "Registered services in Eureka:"
        $response.applications.application | ForEach-Object {
            Write-Host "  - $($_.name)"
        }
    } catch {
        Write-Host "Failed to check Eureka registry: $_"
    }
}
