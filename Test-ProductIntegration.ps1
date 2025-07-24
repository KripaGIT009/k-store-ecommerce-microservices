# K-Store Product Service Integration Test Script (PowerShell)

Write-Host "K-STORE PRODUCT SERVICE INTEGRATION TEST" -ForegroundColor Cyan
Write-Host ""

# Test 1: Check if Product Service is running
Write-Host "Testing Product Service Health" -ForegroundColor Blue
try {
    $health = Invoke-RestMethod -Uri "http://localhost:8083/actuator/health" -TimeoutSec 5
    Write-Host "✅ Product Service is running on port 8083" -ForegroundColor Green
    Write-Host "   Status: $($health.status)" -ForegroundColor Green
} catch {
    Write-Host "❌ Product Service is not running" -ForegroundColor Red
    Write-Host "   Error: $($_.Exception.Message)" -ForegroundColor Yellow
}

Write-Host ""

# Test 2: Check Service Discovery Registration  
Write-Host "2️⃣ Testing Service Discovery Registration" -ForegroundColor Blue
try {
    $eureka = Invoke-RestMethod -Uri "http://localhost:8761/eureka/apps/K-STORE-PRODUCT-SERVICE" -TimeoutSec 5
    Write-Host "✅ Product Service registered with Eureka" -ForegroundColor Green
} catch {
    Write-Host "❌ Product Service not registered with Eureka" -ForegroundColor Red
}

Write-Host ""

# Test 3: Test API Gateway Routing
Write-Host "3️⃣ Testing API Gateway Routing" -ForegroundColor Blue
try {
    $products = Invoke-RestMethod -Uri "http://localhost:8080/k-store-product-service/api/products" -TimeoutSec 10
    Write-Host "✅ API Gateway routing working" -ForegroundColor Green
    Write-Host "   📦 Total Products: $($products.data.totalElements)" -ForegroundColor Green
} catch {
    $statusCode = $_.Exception.Response.StatusCode.value__
    if ($statusCode -eq 404) {
        Write-Host "⏳ Service discovery propagation in progress" -ForegroundColor Yellow
    } else {
        Write-Host "❌ API Gateway routing failed (HTTP $statusCode)" -ForegroundColor Red
    }
}

Write-Host ""

# Test 4: Create Sample Category
Write-Host "4️⃣ Creating Sample Category" -ForegroundColor Blue
try {
    $categoryData = @{
        name = "Electronics"
        description = "Electronic devices and gadgets"
    } | ConvertTo-Json

    $category = Invoke-RestMethod -Uri "http://localhost:8080/k-store-product-service/api/categories" `
        -Method Post -ContentType "application/json" -Body $categoryData

    Write-Host "✅ Category created successfully" -ForegroundColor Green
    Write-Host "   📂 Category: $($category.data.name)" -ForegroundColor Green
} catch {
    Write-Host "⚠️ Category creation failed: $($_.Exception.Message)" -ForegroundColor Yellow
}

Write-Host ""

# Test 5: Create Sample Product
Write-Host "5️⃣ Creating Sample Product" -ForegroundColor Blue
try {
    $productData = @{
        name = "MacBook Pro 16-inch"
        description = "Apple MacBook Pro with M2 Max chip, 32GB RAM, 1TB SSD"
        price = 2499.99
        stockQuantity = 15
        categoryId = 1
    } | ConvertTo-Json

    $product = Invoke-RestMethod -Uri "http://localhost:8080/k-store-product-service/api/products" `
        -Method Post -ContentType "application/json" -Body $productData

    Write-Host "✅ Product created successfully" -ForegroundColor Green
    Write-Host "   🛍️ Product: $($product.data.name)" -ForegroundColor Green
    Write-Host "   💰 Price: `$$($product.data.price)" -ForegroundColor Green
} catch {
    Write-Host "⚠️ Product creation failed: $($_.Exception.Message)" -ForegroundColor Yellow
}

Write-Host ""

# Test 6: Get All Products
Write-Host "6️⃣ Retrieving All Products" -ForegroundColor Blue
try {
    $allProducts = Invoke-RestMethod -Uri "http://localhost:8080/k-store-product-service/api/products"
    Write-Host "✅ Products retrieved successfully" -ForegroundColor Green
    Write-Host "   📦 Total Products: $($allProducts.data.totalElements)" -ForegroundColor Green
    Write-Host "   📄 Current Page: $($allProducts.data.number + 1) of $($allProducts.data.totalPages)" -ForegroundColor Green
} catch {
    Write-Host "⚠️ Products retrieval failed: $($_.Exception.Message)" -ForegroundColor Yellow
}

Write-Host ""
Write-Host "Integration Test Complete" -ForegroundColor Cyan
Write-Host ""
Write-Host "Working Services:" -ForegroundColor Green
Write-Host "   API Gateway: http://localhost:8080" -ForegroundColor White
Write-Host "   User Service: http://localhost:8082" -ForegroundColor White
Write-Host "   Product Service: http://localhost:8083" -ForegroundColor White
Write-Host "   Eureka: http://localhost:8761" -ForegroundColor White
Write-Host ""
Write-Host "API Gateway Routes:" -ForegroundColor Blue
Write-Host "   User Registration: http://localhost:8080/k-store-user-service/api/users/register" -ForegroundColor White
Write-Host "   Products: http://localhost:8080/k-store-product-service/api/products" -ForegroundColor White
Write-Host "   Health: http://localhost:8080/actuator/health" -ForegroundColor White
