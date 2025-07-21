#!/bin/bash

# K-Store Build Script
# This script builds and optionally runs the K-Store microservices application

set -e

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Configuration
SERVICES=("k-store-common" "k-store-config-server" "k-store-discovery-server" "k-store-api-gateway" "k-store-user-service" "k-store-product-service" "k-store-order-service" "k-store-payment-service" "k-store-notification-service")
BUILD_MODE="install"
RUN_SERVICES=false
DOCKER_BUILD=false
CLEAN_BUILD=false

# Functions
print_usage() {
    echo "Usage: $0 [OPTIONS]"
    echo "Options:"
    echo "  -h, --help          Show this help message"
    echo "  -c, --clean         Clean build (mvn clean install)"
    echo "  -r, --run           Run services after build"
    echo "  -d, --docker        Build Docker images"
    echo "  -t, --test          Run tests only"
    echo "  --skip-tests        Skip tests during build"
    echo ""
    echo "Examples:"
    echo "  $0                  # Build all services"
    echo "  $0 -c               # Clean build all services"
    echo "  $0 -r               # Build and run services"
    echo "  $0 -d               # Build Docker images"
}

print_header() {
    echo -e "${BLUE}=================================${NC}"
    echo -e "${BLUE}   K-Store Microservices Build   ${NC}"
    echo -e "${BLUE}=================================${NC}"
    echo ""
}

print_step() {
    echo -e "${YELLOW}[STEP]${NC} $1"
}

print_success() {
    echo -e "${GREEN}[SUCCESS]${NC} $1"
}

print_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

check_prerequisites() {
    print_step "Checking prerequisites..."
    
    # Check Java
    if ! command -v java &> /dev/null; then
        print_error "Java is not installed or not in PATH"
        exit 1
    fi
    
    # Check Maven
    if ! command -v mvn &> /dev/null; then
        print_error "Maven is not installed or not in PATH"
        exit 1
    fi
    
    # Check Docker if needed
    if [ "$DOCKER_BUILD" = true ]; then
        if ! command -v docker &> /dev/null; then
            print_error "Docker is not installed or not in PATH"
            exit 1
        fi
    fi
    
    print_success "Prerequisites check passed"
    echo ""
}

build_service() {
    local service=$1
    print_step "Building $service..."
    
    cd "$service"
    
    if [ "$CLEAN_BUILD" = true ]; then
        mvn clean install -DskipTests
    elif [ "$BUILD_MODE" = "test" ]; then
        mvn test
    elif [ "$BUILD_MODE" = "install-skip-tests" ]; then
        mvn install -DskipTests
    else
        mvn install
    fi
    
    if [ $? -eq 0 ]; then
        print_success "$service built successfully"
    else
        print_error "Failed to build $service"
        exit 1
    fi
    
    cd ..
    echo ""
}

build_docker_image() {
    local service=$1
    print_step "Building Docker image for $service..."
    
    cd "$service"
    docker build -t "kstore/$service:latest" .
    
    if [ $? -eq 0 ]; then
        print_success "Docker image for $service built successfully"
    else
        print_error "Failed to build Docker image for $service"
        exit 1
    fi
    
    cd ..
    echo ""
}

start_infrastructure() {
    print_step "Starting infrastructure services..."
    docker-compose up -d postgres-users postgres-products postgres-orders postgres-payments redis
    
    # Wait for databases to be ready
    echo "Waiting for databases to be ready..."
    sleep 10
    
    print_success "Infrastructure services started"
    echo ""
}

run_services() {
    print_step "Starting application services..."
    
    # Start Config Server
    print_step "Starting Config Server..."
    cd k-store-config-server
    nohup mvn spring-boot:run > ../logs/config-server.log 2>&1 &
    cd ..
    sleep 15
    
    # Start Discovery Server
    print_step "Starting Discovery Server..."
    cd k-store-discovery-server
    nohup mvn spring-boot:run > ../logs/discovery-server.log 2>&1 &
    cd ..
    sleep 15
    
    # Start business services
    for service in "k-store-user-service" "k-store-product-service" "k-store-order-service" "k-store-payment-service" "k-store-notification-service"; do
        print_step "Starting $service..."
        cd "$service"
        nohup mvn spring-boot:run > "../logs/${service}.log" 2>&1 &
        cd ..
        sleep 5
    done
    
    # Start API Gateway last
    print_step "Starting API Gateway..."
    cd k-store-api-gateway
    nohup mvn spring-boot:run > ../logs/api-gateway.log 2>&1 &
    cd ..
    
    print_success "All services started. Check logs in ./logs/ directory"
    echo ""
    echo "Service URLs:"
    echo "  - API Gateway: http://localhost:8080"
    echo "  - Eureka Dashboard: http://localhost:8761"
    echo "  - Config Server: http://localhost:8888"
}

# Parse command line arguments
while [[ $# -gt 0 ]]; do
    case $1 in
        -h|--help)
            print_usage
            exit 0
            ;;
        -c|--clean)
            CLEAN_BUILD=true
            shift
            ;;
        -r|--run)
            RUN_SERVICES=true
            shift
            ;;
        -d|--docker)
            DOCKER_BUILD=true
            shift
            ;;
        -t|--test)
            BUILD_MODE="test"
            shift
            ;;
        --skip-tests)
            BUILD_MODE="install-skip-tests"
            shift
            ;;
        *)
            echo "Unknown option $1"
            print_usage
            exit 1
            ;;
    esac
done

# Main execution
print_header
check_prerequisites

# Create logs directory if running services
if [ "$RUN_SERVICES" = true ]; then
    mkdir -p logs
fi

# Build root project first
print_step "Building root project..."
if [ "$CLEAN_BUILD" = true ]; then
    mvn clean install -DskipTests
else
    mvn install -DskipTests
fi

if [ $? -eq 0 ]; then
    print_success "Root project built successfully"
else
    print_error "Failed to build root project"
    exit 1
fi
echo ""

# Build each service
for service in "${SERVICES[@]}"; do
    if [ -d "$service" ]; then
        build_service "$service"
        
        # Build Docker image if requested
        if [ "$DOCKER_BUILD" = true ] && [ "$service" != "k-store-common" ]; then
            build_docker_image "$service"
        fi
    else
        print_error "Service directory $service not found"
    fi
done

print_success "All services built successfully!"
echo ""

# Run services if requested
if [ "$RUN_SERVICES" = true ]; then
    start_infrastructure
    run_services
    
    echo -e "${GREEN}========================================${NC}"
    echo -e "${GREEN}   K-Store Application Started!       ${NC}"
    echo -e "${GREEN}========================================${NC}"
    echo ""
    echo "To stop all services, run:"
    echo "  pkill -f 'spring-boot:run'"
    echo "  docker-compose down"
fi

print_success "Build completed successfully!"
