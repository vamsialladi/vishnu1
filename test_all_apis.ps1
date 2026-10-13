# Comprehensive API Test Suite for Plant Care Service Monolithic Backend

$baseUrl = "http://localhost:8080"
$totalTests = 0
$passedTests = 0
$failedTests = 0
$failedNames = @()

function Test-Api {
    param(
        [string]$Name,
        [string]$Method,
        [string]$Path,
        [object]$Body = $null,
        [int[]]$ExpectedStatus = @(200, 201, 204)
    )
    $script:totalTests++
    $uri = "$baseUrl$Path"
    $headers = @{
        "Content-Type" = "application/json"
        "X-Correlation-ID" = "test-corr-$script:totalTests"
    }

    try {
        $params = @{
            Uri = $uri
            Method = $Method
            Headers = $headers
            ErrorAction = "Stop"
        }
        if ($null -ne $Body) {
            if ($Body -is [string]) {
                $params.Body = $Body
            } else {
                $params.Body = ($Body | ConvertTo-Json -Depth 10)
            }
        }

        $response = Invoke-RestMethod @params
        Write-Host " [PASS] $Name -> $Method $Path" -ForegroundColor Green
        $script:passedTests++
        return ,$response
    }
    catch {
        $statusCode = $_.Exception.Response.StatusCode.value__
        if ($ExpectedStatus -contains $statusCode) {
            Write-Host " [PASS] (Expected Status $statusCode) $Name -> $Method $Path" -ForegroundColor Green
            $script:passedTests++
            return $null
        } else {
            Write-Host " [FAIL] $Name -> $Method $Path [Status: $statusCode] Error: $($_.Exception.Message)" -ForegroundColor Red
            if ($_.ErrorDetails) {
                Write-Host "        Details: $($_.ErrorDetails.Message)" -ForegroundColor Yellow
            }
            $script:failedTests++
            $script:failedNames += "$Name -> $Method $Path [Status: $statusCode]"
            return $null
        }
    }
}

$uid = Get-Random -Minimum 10000 -Maximum 99999
$adminU = "adm_$uid"
$adminE = "adm_$uid@example.com"
$custU = "cust_$uid"
$custE = "cust_$uid@example.com"
$workerU = "wrk_$uid"
$workerE = "wrk_$uid@example.com"
$workerCode = "EMP-$uid"
$tempU = "tmp_$uid"
$tempE = "tmp_$uid@example.com"
$compName = "GreenWorks Tech $uid"
$compReg = "REG-$uid"
$sku1 = "PLANT-SNAKE-$uid"
$sku2 = "PLANT-MONS-$uid"
$tempSku = "PLANT-TEMP-$uid"

Write-Host "==========================================================" -ForegroundColor Cyan
Write-Host "STARTING COMPREHENSIVE REST API SUITE (Run ID: $uid)" -ForegroundColor Cyan
Write-Host "Target: $baseUrl" -ForegroundColor Cyan
Write-Host "==========================================================" -ForegroundColor Cyan

# 1. ACTUATOR ENDPOINTS
Write-Host "`n--- 1. ACTUATOR & HEALTH ---" -ForegroundColor Yellow
Test-Api -Name "Actuator Health" -Method "GET" -Path "/actuator/health" | Out-Null
Test-Api -Name "Actuator Info" -Method "GET" -Path "/actuator/info" | Out-Null

# 2. USER MANAGEMENT
Write-Host "`n--- 2. USER CONTROLLER ---" -ForegroundColor Yellow
$adminUser = Test-Api -Name "Register Admin User" -Method "POST" -Path "/api/v1/users/register" -Body @{
    username = $adminU
    email = $adminE
    password = "Password123!"
    fullName = "Master Administrator $uid"
    phoneNumber = "9876543210"
    role = "ROLE_ADMIN"
}

$custUser = Test-Api -Name "Register Customer User" -Method "POST" -Path "/api/v1/users/register" -Body @{
    username = $custU
    email = $custE
    password = "Password123!"
    fullName = "Corporate Client $uid"
    phoneNumber = "9876543211"
    role = "ROLE_CUSTOMER"
}

$workerUser = Test-Api -Name "Register Worker User" -Method "POST" -Path "/api/v1/users/register" -Body @{
    username = $workerU
    email = $workerE
    password = "Password123!"
    fullName = "Field Specialist $uid"
    phoneNumber = "9876543212"
    role = "ROLE_WORKER"
}

$tempUser = Test-Api -Name "Register Temp User for Delete" -Method "POST" -Path "/api/v1/users/register" -Body @{
    username = $tempU
    email = $tempE
    password = "Password123!"
    fullName = "Temp User To Delete"
    role = "ROLE_CUSTOMER"
}

Test-Api -Name "User Login" -Method "POST" -Path "/api/v1/users/login" -Body @{
    username = $custU
    password = "Password123!"
} | Out-Null

Test-Api -Name "Get Current User Me" -Method "GET" -Path "/api/v1/users/me?username=$custU" | Out-Null
Test-Api -Name "Get User by Username" -Method "GET" -Path "/api/v1/users/username/$custU" | Out-Null
Test-Api -Name "Get User by ID" -Method "GET" -Path "/api/v1/users/$($custUser.id)" | Out-Null
Test-Api -Name "Get All Users" -Method "GET" -Path "/api/v1/users" | Out-Null

Test-Api -Name "Update User Profile" -Method "PUT" -Path "/api/v1/users/$($custUser.id)" -Body @{
    username = $custU
    email = $custE
    password = "Password123!"
    fullName = "Corporate Client Updated $uid"
    phoneNumber = "9876543299"
    role = "ROLE_CUSTOMER"
} | Out-Null

Test-Api -Name "Forgot Password" -Method "POST" -Path "/api/v1/users/forgot-password" -Body @{
    email = $custE
} | Out-Null

Test-Api -Name "Reset Password" -Method "POST" -Path "/api/v1/users/reset-password" -Body @{
    tokenOrEmail = $custE
    newPassword = "NewPassword123!"
} | Out-Null

Test-Api -Name "User Logout" -Method "POST" -Path "/api/v1/users/logout?username=$custU" | Out-Null
Test-Api -Name "Delete Temp User" -Method "DELETE" -Path "/api/v1/users/$($tempUser.id)" | Out-Null

# 3. PLANT CATEGORY
Write-Host "`n--- 3. PLANT CATEGORY CONTROLLER ---" -ForegroundColor Yellow
$category = Test-Api -Name "Create Plant Category" -Method "POST" -Path "/api/categories" -Body @{
    name = "Indoor Air Purifiers $uid"
    description = "Plants selected for workspaces"
}
$tempCat = Test-Api -Name "Create Temp Category" -Method "POST" -Path "/api/categories" -Body @{
    name = "Temp Category $uid"
    description = "To be deleted"
}
Test-Api -Name "Get Category by ID" -Method "GET" -Path "/api/categories/$($category.id)" | Out-Null
Test-Api -Name "Get All Categories" -Method "GET" -Path "/api/categories" | Out-Null
Test-Api -Name "Update Category" -Method "PUT" -Path "/api/categories/$($category.id)" -Body @{
    name = "Indoor Foliage $uid"
    description = "Updated description"
} | Out-Null
Test-Api -Name "Delete Temp Category" -Method "DELETE" -Path "/api/categories/$($tempCat.id)" | Out-Null

# 4. PRODUCT CATALOG
Write-Host "`n--- 4. PRODUCT CONTROLLER ---" -ForegroundColor Yellow
$product1 = Test-Api -Name "Create Product 1 (Snake Plant)" -Method "POST" -Path "/api/v1/products" -Body @{
    name = "Sansevieria Snake Plant $uid"
    scientificName = "Dracaena trifasciata"
    description = "Hardy indoor succulent"
    price = 799.00
    rentalPrice = 149.00
    installationCharge = 99.00
    quantity = 50
    category = "Indoor Foliage $uid"
    sku = $sku1
    sunlightRequirement = "LOW"
    waterRequirement = "LOW"
    maintenanceLevel = "LOW"
    indoor = $true
    active = $true
}

$product2 = Test-Api -Name "Create Product 2 (Monstera)" -Method "POST" -Path "/api/v1/products" -Body @{
    name = "Monstera Deliciosa $uid"
    scientificName = "Monstera deliciosa"
    description = "Vibrant Swiss cheese plant"
    price = 1299.00
    rentalPrice = 249.00
    installationCharge = 149.00
    quantity = 30
    category = "Indoor Foliage $uid"
    sku = $sku2
    sunlightRequirement = "MEDIUM"
    waterRequirement = "MEDIUM"
    maintenanceLevel = "MEDIUM"
    indoor = $true
    active = $true
}

$tempProduct = Test-Api -Name "Create Temp Product" -Method "POST" -Path "/api/v1/products" -Body @{
    name = "Temp Plant $uid"
    price = 100.00
    quantity = 5
    sku = $tempSku
}

Test-Api -Name "Get Product by ID" -Method "GET" -Path "/api/v1/products/$($product1.id)" | Out-Null
Test-Api -Name "Get Product by SKU" -Method "GET" -Path "/api/v1/products/sku/$sku1" | Out-Null
Test-Api -Name "Get All Products" -Method "GET" -Path "/api/v1/products" | Out-Null
Test-Api -Name "Search Products" -Method "GET" -Path "/api/v1/products/search?query=Sansevieria" | Out-Null
Test-Api -Name "Get Products by Category" -Method "GET" -Path "/api/v1/products/category/$([uri]::EscapeDataString("Indoor Foliage $uid"))" | Out-Null
Test-Api -Name "Update Product" -Method "PUT" -Path "/api/v1/products/$($product1.id)" -Body @{
    name = "Sansevieria Snake Plant (Updated) $uid"
    scientificName = "Dracaena trifasciata"
    description = "Updated description"
    price = 849.00
    rentalPrice = 159.00
    installationCharge = 99.00
    quantity = 55
    category = "Indoor Foliage $uid"
    sku = $sku1
    indoor = $true
    active = $true
} | Out-Null
Test-Api -Name "Delete Temp Product" -Method "DELETE" -Path "/api/v1/products/$($tempProduct.id)" | Out-Null

# 5. INVENTORY
Write-Host "`n--- 5. INVENTORY CONTROLLER ---" -ForegroundColor Yellow
Test-Api -Name "Get All Inventory" -Method "GET" -Path "/api/inventory" | Out-Null
Test-Api -Name "Update Stock Product 1 (+50)" -Method "POST" -Path "/api/inventory/update-stock" -Body @{
    productId = $product1.id
    quantityChange = 50
    notes = "Initial shipment Product 1"
} | Out-Null
Test-Api -Name "Update Stock Product 2 (+50)" -Method "POST" -Path "/api/inventory/update-stock" -Body @{
    productId = $product2.id
    quantityChange = 50
    notes = "Initial shipment Product 2"
} | Out-Null
Test-Api -Name "Get Inventory by Product ID" -Method "GET" -Path "/api/inventory/product/$($product1.id)" | Out-Null
Test-Api -Name "Check Stock Availability" -Method "GET" -Path "/api/inventory/check-availability?productId=$($product1.id)&quantity=5" | Out-Null

# 6. MAINTENANCE PLANS
Write-Host "`n--- 6. MAINTENANCE PLANS ---" -ForegroundColor Yellow
$mPlan = Test-Api -Name "Create Maintenance Plan" -Method "POST" -Path "/api/maintenance/plans" -Body @{
    name = "Corporate Weekly Care $uid"
    description = "Weekly watering and maintenance"
    monthlyPrice = 1200.00
    frequency = "WEEKLY"
    includedServices = "Watering, Pruning, Soil Nourishing, Pest Spray"
    replacementSupport = $true
    emergencySupport = $true
    consultationSupport = $true
}

$tempPlan = Test-Api -Name "Create Temp Plan" -Method "POST" -Path "/api/maintenance/plans" -Body @{
    name = "Temp Plan To Delete $uid"
    monthlyPrice = 500.00
}

Test-Api -Name "Get All Maintenance Plans" -Method "GET" -Path "/api/maintenance/plans" | Out-Null
Test-Api -Name "Get Plan by ID" -Method "GET" -Path "/api/maintenance/plans/$($mPlan.id)" | Out-Null
Test-Api -Name "Update Maintenance Plan" -Method "PUT" -Path "/api/maintenance/plans/$($mPlan.id)" -Body @{
    name = "Corporate Weekly Premium Care $uid"
    description = "Updated maintenance details"
    monthlyPrice = 1350.00
    frequency = "WEEKLY"
    includedServices = "Watering, Pruning, Soil Nourishing, Pest Spray, Organic Fertilizers"
    replacementSupport = $true
    emergencySupport = $true
    consultationSupport = $true
} | Out-Null
Test-Api -Name "Delete Temp Plan" -Method "DELETE" -Path "/api/maintenance/plans/$($tempPlan.id)" | Out-Null

# 7. COMPANY & LOCATIONS
Write-Host "`n--- 7. COMPANY CONTROLLER ---" -ForegroundColor Yellow
$company = Test-Api -Name "Register Company" -Method "POST" -Path "/api/companies?username=$custU" -Body @{
    name = $compName
    registrationNumber = $compReg
    contactEmail = $custE
    contactPhone = "080-41234567"
    billingAddress = "Level 5, Tower A, Bengaluru 560055"
}

Test-Api -Name "Get Company by ID" -Method "GET" -Path "/api/companies/$($company.id)?username=$custU" | Out-Null
Test-Api -Name "Get User Companies" -Method "GET" -Path "/api/companies?username=$custU" | Out-Null
Test-Api -Name "Update Company" -Method "PUT" -Path "/api/companies/$($company.id)?username=$custU" -Body @{
    name = "$compName (HQ)"
    registrationNumber = $compReg
    contactEmail = $custE
    contactPhone = "080-41234567"
    billingAddress = "Level 5 & 6, Tower A, Bengaluru 560055"
} | Out-Null
Test-Api -Name "Update Company Status" -Method "PATCH" -Path "/api/companies/$($company.id)/status?status=ACTIVE" | Out-Null

$location = Test-Api -Name "Add Company Location" -Method "POST" -Path "/api/companies/locations?username=$custU" -Body @{
    companyId = $company.id
    locationName = "Main Campus - 5th Floor"
    address = "Brigade Gateway, Malleshwaram"
    city = "Bengaluru"
    state = "Karnataka"
    zipCode = "560055"
    latitude = 13.0118
    longitude = 77.5552
    locationType = "OFFICE"
    isIndoor = $true
}

$tempLocation = Test-Api -Name "Add Temp Location" -Method "POST" -Path "/api/companies/locations?username=$custU" -Body @{
    companyId = $company.id
    locationName = "Temp Storage Room"
    address = "Basement"
}

Test-Api -Name "Get Locations for Company" -Method "GET" -Path "/api/companies/$($company.id)/locations?username=$custU" | Out-Null
Test-Api -Name "Update Location" -Method "PUT" -Path "/api/companies/locations/$($location.id)?username=$custU" -Body @{
    companyId = $company.id
    locationName = "Main Campus - Executive Floor"
    address = "Brigade Gateway, Malleshwaram"
    city = "Bengaluru"
    state = "Karnataka"
    zipCode = "560055"
    locationType = "OFFICE"
    isIndoor = $true
} | Out-Null
Test-Api -Name "Delete Temp Location" -Method "DELETE" -Path "/api/companies/locations/$($tempLocation.id)?username=$custU" | Out-Null

# 8. CART CONTROLLER
Write-Host "`n--- 8. CART CONTROLLER ---" -ForegroundColor Yellow
$cartItem1 = Test-Api -Name "Add Item to Cart (Product 1)" -Method "POST" -Path "/api/cart/items?username=$custU" -Body @{
    productId = $product1.id
    quantity = 3
    isRental = $true
    includeInstallation = $true
    maintenancePlanId = $mPlan.id
}

$cart = Test-Api -Name "Get Cart" -Method "GET" -Path "/api/cart?username=$custU"
$firstCartItemId = $cart.items[0].id

Test-Api -Name "Update Cart Item Quantity" -Method "PUT" -Path "/api/cart/items/$($firstCartItemId)?username=$custU" -Body @{
    quantity = 4
} | Out-Null
Test-Api -Name "Remove Item from Cart" -Method "DELETE" -Path "/api/cart/items/$($firstCartItemId)?username=$custU" | Out-Null
Test-Api -Name "Clear Cart" -Method "DELETE" -Path "/api/cart/clear?username=$custU" | Out-Null

# Add items for booking
Test-Api -Name "Add Product 1 to Cart" -Method "POST" -Path "/api/cart/items?username=$custU" -Body @{
    productId = $product1.id
    quantity = 2
    isRental = $false
    includeInstallation = $true
    maintenancePlanId = $mPlan.id
} | Out-Null
Test-Api -Name "Add Product 2 to Cart" -Method "POST" -Path "/api/cart/items?username=$custU" -Body @{
    productId = $product2.id
    quantity = 1
    isRental = $true
    includeInstallation = $true
    maintenancePlanId = $mPlan.id
} | Out-Null
$cartReady = Test-Api -Name "View Ready Cart" -Method "GET" -Path "/api/cart?username=$custU"

# 9. BOOKING CONTROLLER
Write-Host "`n--- 9. BOOKING CONTROLLER ---" -ForegroundColor Yellow
$booking = Test-Api -Name "Create Booking from Cart" -Method "POST" -Path "/api/bookings?username=$custU" -Body @{
    companyId = $company.id
    companyLocationId = $location.id
    installationDate = (Get-Date).AddDays(3).ToString("yyyy-MM-dd")
    requireInspection = $true
    notes = "Delivery before 11 AM"
}

Test-Api -Name "Get Booking by ID" -Method "GET" -Path "/api/bookings/$($booking.id)?username=$custU" | Out-Null
Test-Api -Name "Get Bookings List" -Method "GET" -Path "/api/bookings?username=$custU" | Out-Null
Test-Api -Name "Update Booking Status to CONFIRMED (Admin)" -Method "PATCH" -Path "/api/bookings/$($booking.id)/status?username=$adminU" -Body @{
    status = "CONFIRMED"
} | Out-Null

# 10. WORKER CONTROLLER
Write-Host "`n--- 10. WORKER CONTROLLER ---" -ForegroundColor Yellow
$worker = Test-Api -Name "Register Worker Profile" -Method "POST" -Path "/api/workers" -Body @{
    userId = $workerUser.id
    employeeCode = $workerCode
    specialization = "Indoor Horticultural Specialist"
    phone = "9876543212"
}

Test-Api -Name "Get Worker by ID" -Method "GET" -Path "/api/workers/$($worker.id)" | Out-Null
Test-Api -Name "Get Current Worker Me" -Method "GET" -Path "/api/workers/me?username=$workerU" | Out-Null
Test-Api -Name "Get All Workers" -Method "GET" -Path "/api/workers" | Out-Null
Test-Api -Name "Update Worker Status" -Method "PATCH" -Path "/api/workers/$($worker.id)/status" -Body @{
    status = "ACTIVE"
    isAvailable = $true
} | Out-Null
Test-Api -Name "Assign Worker to Booking" -Method "POST" -Path "/api/workers/assign?bookingId=$($booking.id)&workerId=$($worker.id)&notes=Lead+Installer" | Out-Null

# 11. SITE INSPECTIONS
Write-Host "`n--- 11. SITE INSPECTION CONTROLLER ---" -ForegroundColor Yellow
$inspectionDate = (Get-Date).AddDays(1).ToString("yyyy-MM-ddTHH:mm:ss")
$inspection = Test-Api -Name "Schedule Site Inspection" -Method "POST" -Path "/api/inspections/schedule" -Body @{
    bookingId = $booking.id
    scheduledDate = $inspectionDate
    assignedWorkerId = $worker.id
}

Test-Api -Name "Get Inspection by ID" -Method "GET" -Path "/api/inspections/$($inspection.id)" | Out-Null
Test-Api -Name "Get Inspection by Booking ID" -Method "GET" -Path "/api/inspections/booking/$($booking.id)" | Out-Null
Test-Api -Name "Get Inspections for Worker" -Method "GET" -Path "/api/inspections/worker/$($worker.id)" | Out-Null
Test-Api -Name "Record Inspection Assessment" -Method "POST" -Path "/api/inspections/$($inspection.id)/assessment?username=$workerU" -Body @{
    spaceAssessment = "Spacious open floor plan, 3500 sq ft"
    sunlightAssessment = "Diffused natural light from East windows"
    temperatureAssessment = "Regulated HVAC at 22-24 degrees C"
    humidityAssessment = "55% relative humidity"
    indoorFeasible = $true
    plantRecommendations = "Sansevieria in corridors, Monstera near reception"
    inspectionNotes = "Ready for plant placement"
} | Out-Null

# 12. INSTALLATION CONTROLLER
Write-Host "`n--- 12. INSTALLATION CONTROLLER ---" -ForegroundColor Yellow
$installDate = (Get-Date).AddDays(2).ToString("yyyy-MM-ddTHH:mm:ss")
$installation = Test-Api -Name "Schedule Installation" -Method "POST" -Path "/api/installations/schedule" -Body @{
    bookingId = $booking.id
    scheduledDate = $installDate
    assignedWorkerId = $worker.id
}

Test-Api -Name "Get Installation by ID" -Method "GET" -Path "/api/installations/$($installation.id)" | Out-Null
Test-Api -Name "Get Installation by Booking ID" -Method "GET" -Path "/api/installations/booking/$($booking.id)" | Out-Null
Test-Api -Name "Get Installations for Worker" -Method "GET" -Path "/api/installations/worker/$($worker.id)" | Out-Null
Test-Api -Name "Start Installation" -Method "POST" -Path "/api/installations/$($installation.id)/start?username=$workerU" | Out-Null
Test-Api -Name "Complete Installation" -Method "POST" -Path "/api/installations/$($installation.id)/complete?username=$workerU" -Body @{
    beforePhotosJson = "['https://photos.example.com/site-empty.jpg']"
    afterPhotosJson = "['https://photos.example.com/plants-installed.jpg']"
    installationNotes = "All planters positioned and tagged with QR codes."
} | Out-Null

# 13. PAYMENT CONTROLLER
Write-Host "`n--- 13. PAYMENT CONTROLLER ---" -ForegroundColor Yellow
Test-Api -Name "Get Razorpay Config" -Method "GET" -Path "/api/payments/razorpay/config" | Out-Null

$rzpOrder = Test-Api -Name "Create Razorpay Order" -Method "POST" -Path "/api/payments/razorpay/create-order?username=$custU" -Body @{
    bookingId = $booking.id
}

$payment = Test-Api -Name "Process Payment" -Method "POST" -Path "/api/payments/process?username=$custU" -Body @{
    bookingId = $booking.id
    paymentMethod = "CARD"
}

Test-Api -Name "Handle Razorpay Webhook (Test Ping)" -Method "POST" -Path "/api/payments/razorpay/webhook" -Body "{`"event`": `"payment.captured`", `"payload`": {}}" | Out-Null

Test-Api -Name "Get Payment by ID" -Method "GET" -Path "/api/payments/$($payment.id)?username=$custU" | Out-Null
Test-Api -Name "Get Payments for Booking" -Method "GET" -Path "/api/payments/booking/$($booking.id)?username=$custU" | Out-Null
$invoice = Test-Api -Name "Get Invoice by Booking ID" -Method "GET" -Path "/api/payments/invoice/booking/$($booking.id)?username=$custU"
Test-Api -Name "Get Invoice by ID" -Method "GET" -Path "/api/payments/invoice/$($invoice.id)?username=$custU" | Out-Null

# 14. PLANT TRACKING (INSTANCES)
Write-Host "`n--- 14. PLANT TRACKING CONTROLLER ---" -ForegroundColor Yellow
$plants = Test-Api -Name "Get Plants for Company" -Method "GET" -Path "/api/plants/instances/company/$($company.id)?username=$custU"

if ($plants -and $plants.Count -gt 0) {
    $firstPlant = $plants[0]
    Test-Api -Name "Get Plant Instance by ID" -Method "GET" -Path "/api/plants/instances/$($firstPlant.id)?username=$custU" | Out-Null
    Test-Api -Name "Get Plant Instance by Identifier" -Method "GET" -Path "/api/plants/instances/identifier/$($firstPlant.plantIdentifier)" | Out-Null
    Test-Api -Name "Get Plant Instance by QR Code" -Method "GET" -Path "/api/plants/instances/qr/$($firstPlant.qrCode)" | Out-Null
    Test-Api -Name "Get Plants for Location" -Method "GET" -Path "/api/plants/instances/location/$($location.id)?username=$custU" | Out-Null

    Test-Api -Name "Record Plant Health Log" -Method "POST" -Path "/api/plants/instances/$($firstPlant.id)/health?username=$custU" -Body @{
        healthStatus = "HEALTHY"
        diseaseNotes = "None detected"
        pestNotes = "Clean"
        generalNotes = "Lush green foliage observed"
    } | Out-Null

    Test-Api -Name "Record Watering Event" -Method "POST" -Path "/api/plants/instances/$($firstPlant.id)/watering?username=$custU" -Body @{
        amountLiters = 1.5
        notes = "Weekly deep root watering"
    } | Out-Null

    Test-Api -Name "Record Fertilizer Event" -Method "POST" -Path "/api/plants/instances/$($firstPlant.id)/fertilizer?username=$custU" -Body @{
        fertilizerType = "Organic NPK Liquid Concentrate"
        quantityGrams = 20.0
        notes = "Bi-monthly nutrient booster applied"
    } | Out-Null

    Test-Api -Name "Replace Plant" -Method "POST" -Path "/api/plants/instances/$($firstPlant.id)/replace?username=$custU" -Body @{
        newProductId = $product2.id
        reason = "Aesthetic refresh requested by tenant"
        replacementCost = 0.00
        coveredByPlan = $true
    } | Out-Null
} else {
    Write-Host " [INFO] No plant instances auto-generated yet." -ForegroundColor DarkGray
}

# 15. MAINTENANCE SUBSCRIPTIONS & VISITS
Write-Host "`n--- 15. MAINTENANCE SUBSCRIPTIONS & VISITS ---" -ForegroundColor Yellow
$subs = Test-Api -Name "Get Subscriptions for Company" -Method "GET" -Path "/api/maintenance/subscriptions/company/$($company.id)?username=$custU"

if ($subs -and $subs.Count -gt 0) {
    $sub = $subs[0]
    Test-Api -Name "Get Subscription by ID" -Method "GET" -Path "/api/maintenance/subscriptions/$($sub.id)?username=$custU" | Out-Null
    Test-Api -Name "Pause Subscription" -Method "POST" -Path "/api/maintenance/subscriptions/$($sub.id)/pause?username=$custU" | Out-Null
    Test-Api -Name "Activate Subscription" -Method "POST" -Path "/api/maintenance/subscriptions/$($sub.id)/activate?username=$custU" | Out-Null

    $visitDate = (Get-Date).AddDays(4).ToString("yyyy-MM-dd")
    $visit = Test-Api -Name "Schedule Service Visit" -Method "POST" -Path "/api/maintenance/visits/schedule?subscriptionId=$($sub.id)&visitDate=$visitDate&workerId=$($worker.id)"

    Test-Api -Name "Get Visits for Subscription" -Method "GET" -Path "/api/maintenance/visits/subscription/$($sub.id)?username=$custU" | Out-Null
    Test-Api -Name "Get Visits for Worker" -Method "GET" -Path "/api/maintenance/visits/worker/$($worker.id)" | Out-Null
    Test-Api -Name "Record Worker Arrival" -Method "POST" -Path "/api/maintenance/visits/$($visit.id)/arrival?username=$workerU" | Out-Null
    Test-Api -Name "Complete Service Visit" -Method "POST" -Path "/api/maintenance/visits/$($visit.id)/complete?notes=Cleaned+and+fertilized&photosJson=[]&username=$workerU" | Out-Null
    Test-Api -Name "Cancel Subscription" -Method "POST" -Path "/api/maintenance/subscriptions/$($sub.id)/cancel?username=$custU" | Out-Null
} else {
    Write-Host " [INFO] No active subscriptions for company yet." -ForegroundColor DarkGray
}

# 16. EMERGENCY CONTROLLER
Write-Host "`n--- 16. EMERGENCY CONTROLLER ---" -ForegroundColor Yellow
$emergency = Test-Api -Name "Create Emergency Request" -Method "POST" -Path "/api/emergencies?username=$custU" -Body @{
    companyId = $company.id
    problemDescription = "Yellowing leaves with wilting on corridor planter"
    priority = "HIGH"
}

Test-Api -Name "Get Emergency by ID" -Method "GET" -Path "/api/emergencies/$($emergency.id)?username=$custU" | Out-Null
Test-Api -Name "Get Emergencies for Company" -Method "GET" -Path "/api/emergencies/company/$($company.id)?username=$custU" | Out-Null
Test-Api -Name "Get All Emergencies" -Method "GET" -Path "/api/emergencies" | Out-Null
Test-Api -Name "Update Emergency Status" -Method "PATCH" -Path "/api/emergencies/$($emergency.id)/status" -Body @{
    status = "ASSIGNED"
    assignedWorkerId = $worker.id
    resolutionNotes = "Dispatched worker for immediate soil moisture diagnostic"
} | Out-Null
Test-Api -Name "Get Emergencies for Worker" -Method "GET" -Path "/api/emergencies/worker/$($worker.id)" | Out-Null

# 17. COMPLAINT CONTROLLER
Write-Host "`n--- 17. COMPLAINT CONTROLLER ---" -ForegroundColor Yellow
$complaint = Test-Api -Name "Create Complaint" -Method "POST" -Path "/api/complaints?username=$custU" -Body @{
    companyId = $company.id
    category = "MISSED_VISIT"
    description = "Service technician delayed by 1 hour on Friday"
    priority = "MEDIUM"
}

Test-Api -Name "Get Complaint by ID" -Method "GET" -Path "/api/complaints/$($complaint.id)?username=$custU" | Out-Null
Test-Api -Name "Get Complaints for Company" -Method "GET" -Path "/api/complaints/company/$($company.id)?username=$custU" | Out-Null
Test-Api -Name "Get All Complaints" -Method "GET" -Path "/api/complaints" | Out-Null
Test-Api -Name "Resolve Complaint" -Method "PATCH" -Path "/api/complaints/$($complaint.id)/resolve?username=$adminU" -Body @{
    status = "RESOLVED"
    investigationNotes = "Traffic congestion caused 35m delay"
    resolutionNotes = "Apologized to client and credited bonus maintenance visit"
} | Out-Null

# 18. RECOMMENDATION CONTROLLER
Write-Host "`n--- 18. RECOMMENDATION CONTROLLER ---" -ForegroundColor Yellow
Test-Api -Name "Get Plant Recommendations" -Method "POST" -Path "/api/recommendations/plants" -Body @{
    officeAreaSqFt = 2500.0
    sunlightLevel = "MEDIUM"
    indoor = $true
    budget = 10000.0
    maintenancePreference = "LOW"
    locationType = "DESK"
    desiredPlantCount = 8
} | Out-Null

Test-Api -Name "Calculate Air Quality Score" -Method "POST" -Path "/api/recommendations/scores/calculate/$($company.id)" | Out-Null
Test-Api -Name "Get Latest Score for Company" -Method "GET" -Path "/api/recommendations/scores/company/$($company.id)?username=$custU" | Out-Null

# 19. NOTIFICATION CONTROLLER
Write-Host "`n--- 19. NOTIFICATION CONTROLLER ---" -ForegroundColor Yellow
$notifs = Test-Api -Name "Get Notifications for User" -Method "GET" -Path "/api/notifications?username=$custU"
Test-Api -Name "Get Unread Notifications" -Method "GET" -Path "/api/notifications/unread?username=$custU" | Out-Null

if ($notifs -and $notifs.Count -gt 0) {
    Test-Api -Name "Mark Notification As Read" -Method "PATCH" -Path "/api/notifications/$($notifs[0].id)/read?username=$custU" | Out-Null
}
Test-Api -Name "Mark All Notifications As Read" -Method "POST" -Path "/api/notifications/read-all?username=$custU" | Out-Null

# 20. ADMIN REPORT CONTROLLER
Write-Host "`n--- 20. ADMIN REPORT CONTROLLER ---" -ForegroundColor Yellow
Test-Api -Name "Get Admin Dashboard Stats (Admin)" -Method "GET" -Path "/api/admin/reports/dashboard?username=$adminU" | Out-Null
Test-Api -Name "IDOR/Security Test: Customer Accessing Admin (Expected 403)" -Method "GET" -Path "/api/admin/reports/dashboard?username=$custU" -ExpectedStatus @(403) | Out-Null

# 21. BOOKING CANCELLATION
Write-Host "`n--- 21. BOOKING CANCELLATION ---" -ForegroundColor Yellow
Test-Api -Name "Add Item for Cancellation Test" -Method "POST" -Path "/api/cart/items?username=$custU" -Body @{
    productId = $product1.id
    quantity = 1
} | Out-Null
$cancelBooking = Test-Api -Name "Create Booking to Cancel" -Method "POST" -Path "/api/bookings?username=$custU" -Body @{
    companyId = $company.id
}
Test-Api -Name "Cancel Booking" -Method "POST" -Path "/api/bookings/$($cancelBooking.id)/cancel?username=$custU" | Out-Null

Write-Host "`n==========================================================" -ForegroundColor Cyan
Write-Host "API TEST SUITE SUMMARY" -ForegroundColor Cyan
Write-Host "Total Tests Run : $script:totalTests" -ForegroundColor Cyan
Write-Host "Passed Tests    : $script:passedTests" -ForegroundColor Green
Write-Host "Failed Tests    : $script:failedTests" -ForegroundColor $(if ($script:failedTests -eq 0) { "Green" } else { "Red" })
Write-Host "==========================================================" -ForegroundColor Cyan

if ($script:failedTests -gt 0) {
    Write-Host "`nFAILED TEST DETAILS:" -ForegroundColor Red
    foreach ($fn in $script:failedNames) {
        Write-Host "  - $fn" -ForegroundColor Red
    }
    exit 1
} else {
    Write-Host "`nALL REST APIS TESTED AND PASSED PERFECTLY!" -ForegroundColor Green
    exit 0
}
