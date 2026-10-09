-- Flyway PostgreSQL Schema Migration: V1__init_schema.sql

CREATE TABLE IF NOT EXISTS users (
    id BIGSERIAL PRIMARY KEY,
    full_name VARCHAR(255) NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    phone VARCHAR(50),
    role VARCHAR(50) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP
);

CREATE TABLE IF NOT EXISTS companies (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    registration_number VARCHAR(100) UNIQUE,
    contact_email VARCHAR(255),
    contact_phone VARCHAR(50),
    billing_address VARCHAR(500),
    status VARCHAR(50) NOT NULL DEFAULT 'ACTIVE',
    owner_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS company_locations (
    id BIGSERIAL PRIMARY KEY,
    company_id BIGINT NOT NULL REFERENCES companies(id) ON DELETE CASCADE,
    location_name VARCHAR(255) NOT NULL,
    address VARCHAR(500) NOT NULL,
    city VARCHAR(100),
    state VARCHAR(100),
    zip_code VARCHAR(20),
    latitude DOUBLE PRECISION,
    longitude DOUBLE PRECISION,
    location_type VARCHAR(50),
    is_indoor BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE IF NOT EXISTS plant_categories (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) UNIQUE NOT NULL,
    description VARCHAR(1000)
);

CREATE TABLE IF NOT EXISTS products (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    scientific_name VARCHAR(255),
    description VARCHAR(2000),
    price NUMERIC(10,2) NOT NULL,
    rental_price NUMERIC(10,2),
    installation_charge NUMERIC(10,2) NOT NULL DEFAULT 0.0,
    stock_quantity INTEGER NOT NULL DEFAULT 0,
    sku VARCHAR(100) UNIQUE NOT NULL,
    image_url VARCHAR(500),
    plant_size VARCHAR(50),
    is_indoor BOOLEAN NOT NULL DEFAULT TRUE,
    sunlight_requirement VARCHAR(100),
    water_requirement VARCHAR(100),
    temperature_requirement VARCHAR(100),
    humidity_requirement VARCHAR(100),
    maintenance_level VARCHAR(50),
    maintenance_frequency VARCHAR(50),
    suitable_locations VARCHAR(500),
    care_instructions VARCHAR(2000),
    for_sale BOOLEAN NOT NULL DEFAULT TRUE,
    for_rent BOOLEAN NOT NULL DEFAULT TRUE,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    category_id BIGINT REFERENCES plant_categories(id) ON DELETE SET NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP
);

CREATE TABLE IF NOT EXISTS inventory (
    id BIGSERIAL PRIMARY KEY,
    product_id BIGINT UNIQUE NOT NULL REFERENCES products(id) ON DELETE CASCADE,
    total_quantity INTEGER NOT NULL DEFAULT 0,
    available_quantity INTEGER NOT NULL DEFAULT 0,
    reserved_quantity INTEGER NOT NULL DEFAULT 0,
    installed_quantity INTEGER NOT NULL DEFAULT 0,
    rental_quantity INTEGER NOT NULL DEFAULT 0,
    damaged_quantity INTEGER NOT NULL DEFAULT 0,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    version BIGINT DEFAULT 0
);

CREATE TABLE IF NOT EXISTS maintenance_plans (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description VARCHAR(1000),
    monthly_price NUMERIC(10,2) NOT NULL,
    frequency VARCHAR(50) NOT NULL,
    included_services VARCHAR(1000),
    replacement_support BOOLEAN NOT NULL DEFAULT FALSE,
    emergency_support BOOLEAN NOT NULL DEFAULT FALSE,
    consultation_support BOOLEAN NOT NULL DEFAULT FALSE,
    is_active BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE IF NOT EXISTS carts (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT UNIQUE NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP
);

CREATE TABLE IF NOT EXISTS cart_items (
    id BIGSERIAL PRIMARY KEY,
    cart_id BIGINT NOT NULL REFERENCES carts(id) ON DELETE CASCADE,
    product_id BIGINT NOT NULL REFERENCES products(id) ON DELETE CASCADE,
    quantity INTEGER NOT NULL DEFAULT 1,
    is_rental BOOLEAN NOT NULL DEFAULT FALSE,
    include_installation BOOLEAN NOT NULL DEFAULT TRUE,
    maintenance_plan_id BIGINT REFERENCES maintenance_plans(id) ON DELETE SET NULL
);

CREATE TABLE IF NOT EXISTS bookings (
    id BIGSERIAL PRIMARY KEY,
    booking_number VARCHAR(100) UNIQUE NOT NULL,
    user_id BIGINT NOT NULL REFERENCES users(id),
    company_id BIGINT NOT NULL REFERENCES companies(id),
    company_location_id BIGINT REFERENCES company_locations(id),
    status VARCHAR(50) NOT NULL DEFAULT 'CREATED',
    payment_status VARCHAR(50) NOT NULL DEFAULT 'PENDING',
    plant_charges NUMERIC(10,2) NOT NULL DEFAULT 0.0,
    installation_charges NUMERIC(10,2) NOT NULL DEFAULT 0.0,
    maintenance_charges NUMERIC(10,2) NOT NULL DEFAULT 0.0,
    tax_amount NUMERIC(10,2) NOT NULL DEFAULT 0.0,
    discount_amount NUMERIC(10,2) NOT NULL DEFAULT 0.0,
    grand_total NUMERIC(10,2) NOT NULL DEFAULT 0.0,
    installation_date DATE,
    maintenance_plan_id BIGINT REFERENCES maintenance_plans(id),
    notes VARCHAR(1000),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP
);

CREATE TABLE IF NOT EXISTS booking_items (
    id BIGSERIAL PRIMARY KEY,
    booking_id BIGINT NOT NULL REFERENCES bookings(id) ON DELETE CASCADE,
    product_id BIGINT NOT NULL REFERENCES products(id),
    quantity INTEGER NOT NULL,
    unit_price NUMERIC(10,2) NOT NULL,
    installation_charge NUMERIC(10,2) NOT NULL DEFAULT 0.0,
    maintenance_charge NUMERIC(10,2) NOT NULL DEFAULT 0.0,
    total_price NUMERIC(10,2) NOT NULL,
    is_rental BOOLEAN NOT NULL DEFAULT FALSE,
    item_type VARCHAR(20) NOT NULL DEFAULT 'PLANT'
);

CREATE TABLE IF NOT EXISTS workers (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT UNIQUE NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    employee_code VARCHAR(100) UNIQUE NOT NULL,
    specialization VARCHAR(255),
    phone VARCHAR(50),
    status VARCHAR(50) NOT NULL DEFAULT 'ACTIVE',
    is_available BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE IF NOT EXISTS worker_assignments (
    id BIGSERIAL PRIMARY KEY,
    booking_id BIGINT NOT NULL REFERENCES bookings(id) ON DELETE CASCADE,
    worker_id BIGINT NOT NULL REFERENCES workers(id) ON DELETE CASCADE,
    status VARCHAR(50) NOT NULL DEFAULT 'ASSIGNED',
    notes VARCHAR(500),
    assigned_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS site_inspections (
    id BIGSERIAL PRIMARY KEY,
    booking_id BIGINT UNIQUE NOT NULL REFERENCES bookings(id) ON DELETE CASCADE,
    assigned_worker_id BIGINT REFERENCES workers(id) ON DELETE SET NULL,
    scheduled_date TIMESTAMP,
    completed_date TIMESTAMP,
    status VARCHAR(50) NOT NULL DEFAULT 'REQUESTED',
    space_assessment VARCHAR(1000),
    sunlight_assessment VARCHAR(1000),
    temperature_assessment VARCHAR(1000),
    humidity_assessment VARCHAR(1000),
    indoor_feasible BOOLEAN NOT NULL DEFAULT TRUE,
    plant_recommendations VARCHAR(1000),
    inspection_notes VARCHAR(1000)
);

CREATE TABLE IF NOT EXISTS installations (
    id BIGSERIAL PRIMARY KEY,
    booking_id BIGINT UNIQUE NOT NULL REFERENCES bookings(id) ON DELETE CASCADE,
    assigned_worker_id BIGINT REFERENCES workers(id) ON DELETE SET NULL,
    scheduled_date TIMESTAMP,
    started_at TIMESTAMP,
    completed_at TIMESTAMP,
    status VARCHAR(50) NOT NULL DEFAULT 'SCHEDULED',
    before_photos_json VARCHAR(2000),
    after_photos_json VARCHAR(2000),
    installation_notes VARCHAR(1000)
);

CREATE TABLE IF NOT EXISTS subscriptions (
    id BIGSERIAL PRIMARY KEY,
    company_id BIGINT NOT NULL REFERENCES companies(id),
    maintenance_plan_id BIGINT NOT NULL REFERENCES maintenance_plans(id),
    booking_id BIGINT REFERENCES bookings(id),
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    next_service_date DATE NOT NULL,
    status VARCHAR(50) NOT NULL DEFAULT 'ACTIVE',
    auto_renew BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE IF NOT EXISTS service_visits (
    id BIGSERIAL PRIMARY KEY,
    subscription_id BIGINT REFERENCES subscriptions(id) ON DELETE CASCADE,
    company_id BIGINT NOT NULL REFERENCES companies(id),
    assigned_worker_id BIGINT REFERENCES workers(id) ON DELETE SET NULL,
    scheduled_date DATE NOT NULL,
    arrival_time TIMESTAMP,
    completed_time TIMESTAMP,
    status VARCHAR(50) NOT NULL DEFAULT 'SCHEDULED',
    service_notes VARCHAR(1000),
    photos_json VARCHAR(2000)
);

CREATE TABLE IF NOT EXISTS plant_instances (
    id BIGSERIAL PRIMARY KEY,
    plant_identifier VARCHAR(100) UNIQUE NOT NULL,
    qr_code VARCHAR(255) UNIQUE NOT NULL,
    product_id BIGINT NOT NULL REFERENCES products(id),
    company_id BIGINT NOT NULL REFERENCES companies(id),
    company_location_id BIGINT REFERENCES company_locations(id),
    booking_id BIGINT REFERENCES bookings(id),
    installation_date DATE NOT NULL DEFAULT CURRENT_DATE,
    health_status VARCHAR(50) NOT NULL DEFAULT 'HEALTHY',
    last_service_date TIMESTAMP,
    notes VARCHAR(1000)
);

CREATE TABLE IF NOT EXISTS plant_health_logs (
    id BIGSERIAL PRIMARY KEY,
    plant_instance_id BIGINT NOT NULL REFERENCES plant_instances(id) ON DELETE CASCADE,
    health_status VARCHAR(50) NOT NULL,
    disease_notes VARCHAR(500),
    pest_notes VARCHAR(500),
    general_notes VARCHAR(1000),
    photo_url VARCHAR(500),
    recorded_by_user_id BIGINT REFERENCES users(id),
    log_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS watering_records (
    id BIGSERIAL PRIMARY KEY,
    plant_instance_id BIGINT NOT NULL REFERENCES plant_instances(id) ON DELETE CASCADE,
    watered_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    amount_liters DOUBLE PRECISION,
    notes VARCHAR(500),
    recorded_by_user_id BIGINT REFERENCES users(id)
);

CREATE TABLE IF NOT EXISTS fertilizer_records (
    id BIGSERIAL PRIMARY KEY,
    plant_instance_id BIGINT NOT NULL REFERENCES plant_instances(id) ON DELETE CASCADE,
    fertilizer_type VARCHAR(255) NOT NULL,
    quantity_grams DOUBLE PRECISION,
    applied_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    notes VARCHAR(500),
    recorded_by_user_id BIGINT REFERENCES users(id)
);

CREATE TABLE IF NOT EXISTS plant_replacements (
    id BIGSERIAL PRIMARY KEY,
    old_plant_instance_id BIGINT NOT NULL REFERENCES plant_instances(id),
    new_plant_instance_id BIGINT NOT NULL REFERENCES plant_instances(id),
    reason VARCHAR(500),
    replacement_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    replacement_cost NUMERIC(10,2) NOT NULL DEFAULT 0.0,
    covered_by_plan BOOLEAN NOT NULL DEFAULT TRUE,
    replaced_by_user_id BIGINT REFERENCES users(id)
);

CREATE TABLE IF NOT EXISTS payments (
    id BIGSERIAL PRIMARY KEY,
    transaction_id VARCHAR(100) UNIQUE NOT NULL,
    booking_id BIGINT NOT NULL REFERENCES bookings(id),
    amount NUMERIC(10,2) NOT NULL,
    currency VARCHAR(10) NOT NULL DEFAULT 'INR',
    payment_method VARCHAR(50) NOT NULL,
    payment_status VARCHAR(50) NOT NULL DEFAULT 'INITIATED',
    gateway_reference VARCHAR(255),
    payment_date TIMESTAMP
);

CREATE TABLE IF NOT EXISTS invoices (
    id BIGSERIAL PRIMARY KEY,
    invoice_number VARCHAR(100) UNIQUE NOT NULL,
    booking_id BIGINT UNIQUE NOT NULL REFERENCES bookings(id),
    company_id BIGINT NOT NULL REFERENCES companies(id),
    plant_charges NUMERIC(10,2) NOT NULL DEFAULT 0.0,
    installation_charges NUMERIC(10,2) NOT NULL DEFAULT 0.0,
    maintenance_charges NUMERIC(10,2) NOT NULL DEFAULT 0.0,
    tax_amount NUMERIC(10,2) NOT NULL DEFAULT 0.0,
    discount_amount NUMERIC(10,2) NOT NULL DEFAULT 0.0,
    total_amount NUMERIC(10,2) NOT NULL DEFAULT 0.0,
    invoice_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    payment_status VARCHAR(50) NOT NULL DEFAULT 'PENDING'
);

CREATE TABLE IF NOT EXISTS emergency_requests (
    id BIGSERIAL PRIMARY KEY,
    company_id BIGINT NOT NULL REFERENCES companies(id),
    plant_instance_id BIGINT REFERENCES plant_instances(id),
    problem_description VARCHAR(1000) NOT NULL,
    photo_url VARCHAR(500),
    preferred_visit_date TIMESTAMP,
    priority VARCHAR(50) NOT NULL DEFAULT 'MEDIUM',
    status VARCHAR(50) NOT NULL DEFAULT 'OPEN',
    worker_id BIGINT REFERENCES workers(id),
    resolution_notes VARCHAR(1000),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    resolved_at TIMESTAMP
);

CREATE TABLE IF NOT EXISTS complaints (
    id BIGSERIAL PRIMARY KEY,
    company_id BIGINT NOT NULL REFERENCES companies(id),
    category VARCHAR(100) NOT NULL,
    description VARCHAR(1000) NOT NULL,
    booking_id BIGINT REFERENCES bookings(id),
    service_visit_id BIGINT REFERENCES service_visits(id),
    photo_url VARCHAR(500),
    priority VARCHAR(50) NOT NULL DEFAULT 'MEDIUM',
    status VARCHAR(50) NOT NULL DEFAULT 'OPEN',
    assigned_admin_id BIGINT REFERENCES users(id),
    investigation_notes VARCHAR(1000),
    resolution_notes VARCHAR(1000),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    resolved_at TIMESTAMP
);

CREATE TABLE IF NOT EXISTS notifications (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id),
    title VARCHAR(255) NOT NULL,
    message VARCHAR(1000) NOT NULL,
    event_type VARCHAR(100) NOT NULL,
    reference_id BIGINT,
    is_read BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS green_office_scores (
    id BIGSERIAL PRIMARY KEY,
    company_id BIGINT NOT NULL REFERENCES companies(id),
    score INTEGER NOT NULL,
    total_plants INTEGER DEFAULT 0,
    healthy_plants INTEGER DEFAULT 0,
    green_coverage_sq_ft DOUBLE PRECISION DEFAULT 0.0,
    species_diversity_count INTEGER DEFAULT 0,
    maintenance_compliance_rate DOUBLE PRECISION DEFAULT 0.0,
    breakdown_json VARCHAR(2000),
    recommendations VARCHAR(2000),
    calculated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
