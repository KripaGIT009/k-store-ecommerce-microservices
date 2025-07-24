#!/bin/bash
# Service Status Check Script

echo "🔍 CHECKING SERVICE REGISTRATION STATUS"
echo "=========================================="

echo ""
echo "📊 Eureka Discovery Server:"
curl -s http://localhost:8761/actuator/health | jq -r '.status // "NOT_ACCESSIBLE"' 2>/dev/null || echo "Eureka Server not accessible"

echo ""
echo "🌐 API Gateway:"
curl -s http://localhost:8080/actuator/health | jq -r '.status // "NOT_ACCESSIBLE"' 2>/dev/null || echo "API Gateway not accessible"

echo ""
echo "📦 Product Service:"
curl -s http://localhost:8083/actuator/health | jq -r '.status // "NOT_ACCESSIBLE"' 2>/dev/null || echo "Product Service not accessible"

echo ""
echo "🔗 Service Discovery Test (via Gateway):"
curl -s http://localhost:8080/products/api/categories | head -c 100 2>/dev/null || echo "Gateway routing not working"

echo ""
echo "🏷️ Registered Services in Eureka:"
curl -s -H "Accept: application/json" http://localhost:8761/eureka/apps 2>/dev/null | jq -r '.applications.application[]?.name // "No services registered"' 2>/dev/null || echo "Could not fetch Eureka registry"
