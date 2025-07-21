# K-Store Build Script for Windows PowerShell
# This script builds and optionally runs the K-Store microservices application

param(
    [switch]$Clean,
    [switch]$Run,
    [switch]$Docker,
    [switch]$Test,
    [switch]$SkipTests,
    [switch]$Help
)

# Configuration
$Services = @(
    "k-store-common",
    "k-store-config-server", 
    "k-store-discovery-server",
    "k-store-api-gateway",
    "k-store-user-service",
    "k-store-product-service",
    "k-store-order-service",
    "k-store-payment-service",
    "k-store-notification-service"
)

function Show-Usage {
    Write-Host "Usage: .\build.ps1 [OPTIONS]" -ForegroundColor Yellow
    Write-Host "Options:" -ForegroundColor Yellow
    Write-Host "  -Help           Show this help message"
    Write-Host "  -Clean          Clean build (mvn clean install)"
    Write-Host "  -Run            Run services after build"
    Write-Host "  -Docker         Build Docker images"
    Write-Host "  -Test           Run tests only"
    Write-Host "  -SkipTests      Skip tests during build"
    Write-Host ""
    Write-Host "Examples:" -ForegroundColor Yellow
    Write-Host "  .\build.ps1                  # Build all services"
    Write-Host "  .\build.ps1 -Clean           # Clean build all services"
    Write-Host "  .\build.ps1 -Run             # Build and run services"
    Write-Host "  .\build.ps1 -Docker          # Build Docker images"
}

function Write-Header {
    Write-Host "=================================" -ForegroundColor Blue
    Write-Host "   K-Store Microservices Build   " -ForegroundColor Blue
    Write-Host "=================================" -ForegroundColor Blue
    Write-Host ""
}

function Write-Step {
    param([string]$Message)
    Write-Host "[STEP] $Message" -ForegroundColor Yellow
}

function Write-Success {
    param([string]$Message)
    Write-Host "[SUCCESS] $Message" -ForegroundColor Green
}

function Write-Error {
    param([string]$Message)
    Write-Host "[ERROR] $Message" -ForegroundColor Red
}

function Test-Prerequisites {
    Write-Step "Checking prerequisites..."
    
    # Check Java
    try {
        $null = Get-Command java -ErrorAction Stop
    } catch {
        Write-Error "Java is not installed or not in PATH"
        exit 1
    }
    
    # Check Maven
    try {
        $null = Get-Command mvn -ErrorAction Stop
    } catch {
        Write-Error "Maven is not installed or not in PATH"
        exit 1
    }
    
    # Check Docker if needed
    if ($Docker) {
        try {
            $null = Get-Command docker -ErrorAction Stop
        } catch {
            Write-Error "Docker is not installed or not in PATH"
            exit 1
        }
    }
    
    Write-Success "Prerequisites check passed"
    Write-Host ""
}

function Build-Service {
    param([string]$ServiceName)
    
    Write-Step "Building $ServiceName..."
    
    Push-Location $ServiceName
    
    try {
        if ($Clean) {
            & mvn clean install -DskipTests
        } elseif ($Test) {
            & mvn test
        } elseif ($SkipTests) {
            & mvn install -DskipTests
        } else {
            & mvn install
        }
        
        if ($LASTEXITCODE -eq 0) {
            Write-Success "$ServiceName built successfully"
        } else {
            Write-Error "Failed to build $ServiceName"
            exit 1
        }
    } finally {
        Pop-Location
    }
    
    Write-Host ""
}

function Build-DockerImage {
    param([string]$ServiceName)
    
    Write-Step "Building Docker image for $ServiceName..."
    
    Push-Location $ServiceName
    
    try {
        & docker build -t "kstore/${ServiceName}:latest" .
        
        if ($LASTEXITCODE -eq 0) {
            Write-Success "Docker image for $ServiceName built successfully"
        } else {
            Write-Error "Failed to build Docker image for $ServiceName"
            exit 1
        }
    } finally {
        Pop-Location
    }
    
    Write-Host ""
}

function Start-Infrastructure {
    Write-Step "Starting infrastructure services..."
    & docker-compose up -d postgres-users postgres-products postgres-orders postgres-payments redis
    
    # Wait for databases to be ready
    Write-Host "Waiting for databases to be ready..."
    Start-Sleep -Seconds 10
    
    Write-Success "Infrastructure services started"
    Write-Host ""
}

function Start-Services {
    Write-Step "Starting application services..."
    
    # Create logs directory
    if (!(Test-Path "logs")) {
        New-Item -ItemType Directory -Path "logs"
    }
    
    # Start Config Server
    Write-Step "Starting Config Server..."
    Push-Location "k-store-config-server"
    Start-Process -FilePath "mvn" -ArgumentList "spring-boot:run" -RedirectStandardOutput "..\logs\config-server.log" -RedirectStandardError "..\logs\config-server-error.log" -NoNewWindow
    Pop-Location
    Start-Sleep -Seconds 15
    
    # Start Discovery Server
    Write-Step "Starting Discovery Server..."
    Push-Location "k-store-discovery-server"
    Start-Process -FilePath "mvn" -ArgumentList "spring-boot:run" -RedirectStandardOutput "..\logs\discovery-server.log" -RedirectStandardError "..\logs\discovery-server-error.log" -NoNewWindow
    Pop-Location
    Start-Sleep -Seconds 15
    
    # Start business services
    $BusinessServices = @("k-store-user-service", "k-store-product-service", "k-store-order-service", "k-store-payment-service", "k-store-notification-service")
    foreach ($service in $BusinessServices) {
        Write-Step "Starting $service..."
        Push-Location $service
        Start-Process -FilePath "mvn" -ArgumentList "spring-boot:run" -RedirectStandardOutput "..\logs\$service.log" -RedirectStandardError "..\logs\$service-error.log" -NoNewWindow
        Pop-Location
        Start-Sleep -Seconds 5
    }
    
    # Start API Gateway last
    Write-Step "Starting API Gateway..."
    Push-Location "k-store-api-gateway"
    Start-Process -FilePath "mvn" -ArgumentList "spring-boot:run" -RedirectStandardOutput "..\logs\api-gateway.log" -RedirectStandardError "..\logs\api-gateway-error.log" -NoNewWindow
    Pop-Location
    
    Write-Success "All services started. Check logs in .\logs\ directory"
    Write-Host ""
    Write-Host "Service URLs:" -ForegroundColor Cyan
    Write-Host "  - API Gateway: http://localhost:8080"
    Write-Host "  - Eureka Dashboard: http://localhost:8761"
    Write-Host "  - Config Server: http://localhost:8888"
}

# Show help if requested
if ($Help) {
    Show-Usage
    exit 0
}

# Main execution
Write-Header
Test-Prerequisites

# Build root project first
Write-Step "Building root project..."
if ($Clean) {
    & mvn clean install -DskipTests
} else {
    & mvn install -DskipTests
}

if ($LASTEXITCODE -eq 0) {
    Write-Success "Root project built successfully"
} else {
    Write-Error "Failed to build root project"
    exit 1
}
Write-Host ""

# Build each service
foreach ($service in $Services) {
    if (Test-Path $service) {
        Build-Service $service
        
        # Build Docker image if requested
        if ($Docker -and $service -ne "k-store-common") {
            Build-DockerImage $service
        }
    } else {
        Write-Error "Service directory $service not found"
    }
}

Write-Success "All services built successfully!"
Write-Host ""

# Run services if requested
if ($Run) {
    Start-Infrastructure
    Start-Services
    
    Write-Host "========================================" -ForegroundColor Green
    Write-Host "   K-Store Application Started!       " -ForegroundColor Green
    Write-Host "========================================" -ForegroundColor Green
    Write-Host ""
    Write-Host "To stop all services, run:" -ForegroundColor Yellow
    Write-Host "  Get-Process | Where-Object {$_.ProcessName -eq 'java'} | Stop-Process"
    Write-Host "  docker-compose down"
}

Write-Success "Build completed successfully!"
