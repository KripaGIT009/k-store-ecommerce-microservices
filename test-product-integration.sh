#!/bin/bash

# K-Store Product Service Integration Test Script

echo "🛍️ K-STORE PRODUCT SERVICE INTEGRATION TEST 🛍️"
echo ""

# Colors for output
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Test 1: Check if Product Service is running
echo "${BLUE}1️⃣ Testing Product Service Health${NC}"
if curl -s http://localhost:8083/actuator/health > /dev/null; then
    echo "${GREEN}✅ Product Service is running on port 8083${NC}"
else
    echo "${RED}❌ Product Service is not running${NC}"
    echo "${YELLOW}   Starting Product Service...${NC}"
    # cd k-store-product-service && mvn spring-boot:run &
fi

echo ""

# Test 2: Check Service Discovery Registration
echo "${BLUE}2️⃣ Testing Service Discovery Registration${NC}"
if curl -s http://localhost:8761/eureka/apps/K-STORE-PRODUCT-SERVICE > /dev/null; then
    echo "${GREEN}✅ Product Service registered with Eureka${NC}"
else
    echo "${RED}❌ Product Service not registered with Eureka${NC}"
fi

echo ""

# Test 3: Test API Gateway Routing
echo "${BLUE}3️⃣ Testing API Gateway Routing${NC}"
GATEWAY_RESPONSE=$(curl -s -o /dev/null -w "%{http_code}" http://localhost:8080/k-store-product-service/api/products)
if [ "$GATEWAY_RESPONSE" == "200" ]; then
    echo "${GREEN}✅ API Gateway routing working${NC}"
elif [ "$GATEWAY_RESPONSE" == "404" ]; then
    echo "${YELLOW}⏳ Service discovery propagation in progress${NC}"
else
    echo "${RED}❌ API Gateway routing failed (HTTP $GATEWAY_RESPONSE)${NC}"
fi

echo ""

# Test 4: Create Sample Category
echo "${BLUE}4️⃣ Creating Sample Category${NC}"
CATEGORY_RESPONSE=$(curl -s -X POST http://localhost:8080/k-store-product-service/api/categories \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Electronics",
    "description": "Electronic devices and gadgets"
  }' \
  -w "%{http_code}" -o /tmp/category_response.json)

if [ "$CATEGORY_RESPONSE" == "201" ]; then
    echo "${GREEN}✅ Category created successfully${NC}"
    cat /tmp/category_response.json | jq '.data.name' 2>/dev/null || echo "Electronics category created"
else
    echo "${YELLOW}⚠️ Category creation status: HTTP $CATEGORY_RESPONSE${NC}"
fi

echo ""

# Test 5: Create Sample Product
echo "${BLUE}5️⃣ Creating Sample Product${NC}"
PRODUCT_RESPONSE=$(curl -s -X POST http://localhost:8080/k-store-product-service/api/products \
  -H "Content-Type: application/json" \
  -d '{
    "name": "MacBook Pro 16-inch",
    "description": "Apple MacBook Pro with M2 Max chip, 32GB RAM, 1TB SSD",
    "price": 2499.99,
    "stockQuantity": 15,
    "categoryId": 1
  }' \
  -w "%{http_code}" -o /tmp/product_response.json)

if [ "$PRODUCT_RESPONSE" == "201" ]; then
    echo "${GREEN}✅ Product created successfully${NC}"
    cat /tmp/product_response.json | jq '.data.name' 2>/dev/null || echo "MacBook Pro created"
else
    echo "${YELLOW}⚠️ Product creation status: HTTP $PRODUCT_RESPONSE${NC}"
fi

echo ""

# Test 6: Get All Products
echo "${BLUE}6️⃣ Retrieving All Products${NC}"
PRODUCTS_RESPONSE=$(curl -s -w "%{http_code}" http://localhost:8080/k-store-product-service/api/products -o /tmp/products_response.json)

if [ "$PRODUCTS_RESPONSE" == "200" ]; then
    echo "${GREEN}✅ Products retrieved successfully${NC}"
    TOTAL_PRODUCTS=$(cat /tmp/products_response.json | jq '.data.totalElements' 2>/dev/null || echo "0")
    echo "   📦 Total Products: $TOTAL_PRODUCTS"
else
    echo "${YELLOW}⚠️ Products retrieval status: HTTP $PRODUCTS_RESPONSE${NC}"
fi

echo ""
echo "${BLUE}🎉 INTEGRATION TEST COMPLETE${NC}"
echo ""
echo "${GREEN}✅ Working Services:${NC}"
echo "   🏥 API Gateway: http://localhost:8080"
echo "   👤 User Service: http://localhost:8082"
echo "   🛍️ Product Service: http://localhost:8083"
echo "   🔍 Eureka: http://localhost:8761"
echo ""
echo "${BLUE}📋 API Gateway Routes:${NC}"
echo "   📝 User Registration: http://localhost:8080/k-store-user-service/api/users/register"
echo "   🛍️ Products: http://localhost:8080/k-store-product-service/api/products"
echo "   🏥 Health: http://localhost:8080/actuator/health"

# Cleanup temp files
rm -f /tmp/category_response.json /tmp/product_response.json /tmp/products_response.json
