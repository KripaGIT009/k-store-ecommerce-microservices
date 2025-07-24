# 🛍️ K-Store Product Service Integration Guide

## 🎯 Product Service Overview

The Product Service is a comprehensive microservice for managing product catalog, inventory, and categories in the K-Store e-commerce platform.

## 🏗️ Service Architecture

### ✅ Current Configuration
- **Service Name**: `k-store-product-service`
- **Port**: `8083`
- **Database**: PostgreSQL (`k_store_products`)
- **Service Discovery**: Eureka Client enabled
- **API Gateway Integration**: Ready for service discovery routing

### 📦 Core Features
1. **Product Management** - CRUD operations for products
2. **Category Management** - Product categorization
3. **Inventory Tracking** - Stock management
4. **Search & Filtering** - Product discovery
5. **Pagination** - Large catalog support

## 🌐 API Gateway Integration

### Service Discovery Routes (Automatic)
The Product Service automatically registers with Eureka and becomes available through the API Gateway at:

```
http://localhost:8080/k-store-product-service/api/products
```

## 📋 API Endpoints

### Product Endpoints

#### ✅ Get All Products
```bash
# Via API Gateway
curl http://localhost:8080/k-store-product-service/api/products

# Direct Service
curl http://localhost:8083/api/products
```

**Response:**
```json
{
  "success": true,
  "message": "Products retrieved successfully",
  "data": {
    "content": [
      {
        "id": 1,
        "name": "MacBook Pro 16-inch",
        "description": "Apple MacBook Pro with M2 Max chip",
        "price": 2499.99,
        "stockQuantity": 15,
        "categoryName": "Electronics",
        "createdAt": "2025-07-24T06:12:57.483Z"
      }
    ],
    "totalElements": 50,
    "totalPages": 5,
    "number": 0,
    "size": 10
  }
}
```

#### ✅ Get Product by ID
```bash
# Via API Gateway
curl http://localhost:8080/k-store-product-service/api/products/1

# Direct Service  
curl http://localhost:8083/api/products/1
```

#### ✅ Create Product
```bash
# Via API Gateway
curl -X POST http://localhost:8080/k-store-product-service/api/products \
  -H "Content-Type: application/json" \
  -d '{
    "name": "iPhone 15 Pro",
    "description": "Latest Apple iPhone with A17 Pro chip",
    "price": 999.99,
    "stockQuantity": 50,
    "categoryId": 1
  }'
```

#### ✅ Update Product
```bash
# Via API Gateway
curl -X PUT http://localhost:8080/k-store-product-service/api/products/1 \
  -H "Content-Type: application/json" \
  -d '{
    "name": "iPhone 15 Pro Max",
    "description": "Updated description",
    "price": 1099.99,
    "stockQuantity": 30,
    "categoryId": 1
  }'
```

#### ✅ Delete Product
```bash
# Via API Gateway
curl -X DELETE http://localhost:8080/k-store-product-service/api/products/1
```

### Category Endpoints

#### ✅ Get All Categories
```bash
curl http://localhost:8080/k-store-product-service/api/categories
```

#### ✅ Create Category
```bash
curl -X POST http://localhost:8080/k-store-product-service/api/categories \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Electronics",
    "description": "Electronic devices and gadgets"
  }'
```

## 🗄️ Database Schema

### Products Table
```sql
CREATE TABLE products (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    price DECIMAL(10,2) NOT NULL,
    stock_quantity INTEGER NOT NULL DEFAULT 0,
    category_id BIGINT REFERENCES categories(id),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

### Categories Table
```sql
CREATE TABLE categories (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE,
    description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

## 🚀 Service Startup

### Prerequisites
1. **PostgreSQL** running on port 5432
2. **Eureka Discovery Server** running on port 8761
3. **Config Server** running on port 8888 (optional)
4. **API Gateway** running on port 8080

### Database Setup
```sql
-- Create database
CREATE DATABASE k_store_products;

-- Grant permissions
GRANT ALL PRIVILEGES ON DATABASE k_store_products TO postgres;
```

### Startup Command
```bash
cd k-store-product-service
mvn spring-boot:run
```

### Health Check
```bash
curl http://localhost:8083/actuator/health
```

## 🧪 Testing Integration

### 1. Service Registration Test
```bash
# Check Eureka registration
curl http://localhost:8761/eureka/apps/K-STORE-PRODUCT-SERVICE
```

### 2. Gateway Routing Test
```bash
# Test via Gateway
curl http://localhost:8080/k-store-product-service/api/products
```

### 3. Create Sample Products
```bash
# Create Electronics category
curl -X POST http://localhost:8080/k-store-product-service/api/categories \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Electronics",
    "description": "Electronic devices and gadgets"
  }'

# Create sample product
curl -X POST http://localhost:8080/k-store-product-service/api/products \
  -H "Content-Type: application/json" \
  -d '{
    "name": "MacBook Pro 16-inch",
    "description": "Apple MacBook Pro with M2 Max chip, 32GB RAM, 1TB SSD",
    "price": 2499.99,
    "stockQuantity": 15,
    "categoryId": 1
  }'
```

## 🔧 Configuration

### application.yml
```yaml
server:
  port: 8083

spring:
  application:
    name: k-store-product-service
  datasource:
    url: jdbc:postgresql://localhost:5432/k_store_products
    username: postgres
    password: password
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: true

eureka:
  client:
    service-url:
      defaultZone: http://localhost:8761/eureka/
```

## 🎉 Integration Status

### ✅ Ready Features
- Product CRUD operations
- Category management
- Service discovery registration
- API Gateway routing
- Health monitoring
- Database persistence
- Pagination support
- Input validation

### 🔄 Next Steps
1. Start Product Service
2. Test API Gateway integration
3. Create sample data
4. Integrate with Order Service
5. Add inventory management
6. Implement search functionality

The Product Service is **ready for integration** with the API Gateway using service discovery routing! 🚀
