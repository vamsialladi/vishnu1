# 🌱 Plant Care Service Platform Backend

Enterprise Monolithic Backend for the **Plant Care Service Platform**, developed with **Java 21**, **Spring Boot 3.3.4**, **PostgreSQL**, and **Spring Security**.

---

## 🏗️ Architecture & Package Layout

All source code is structured directly within the established package namespaces:
```text
src/main/java/com/example/project/
├── controller/        # REST Controllers (Auth, Product, Company, Cart, Booking, Maintenance, etc.)
├── service/           # Service Interfaces & Payment/Recommendation abstractions
├── serviceimpl/       # Concrete Transactional Business Logic & Engines
├── repository/        # Spring Data JPA Repositories (Pessimistic Locking & Filtering)
├── entity/            # Domain JPA Entities
├── dto/               # Strongly-typed Request & Response DTOs
├── config/            # Cross-cutting Configuration (Security, OpenAPI/CORS)
├── security/          # CustomUserDetailsService
├── exception/         # Global Exception Handler & Standard Error Responses
└── util/              # Common Utility Helpers (SKU, String utils)
```

---

## 🚀 Implemented Phases (Phase 1 – Phase 17)

1. **Phase 1: Project Foundation**
   - Java 21, Spring Boot 3.3.4, Spring Web, Spring Data JPA, Spring Security, Validation, Actuator.
   - Dual database profile (`dev`/`test` with in-memory H2, `prod` with PostgreSQL & Flyway migrations).
   - Global exception handling, standardized API responses, CORS configuration.

2. **Phase 2: Authentication & Authorization**
   - User entity with roles: `ROLE_CUSTOMER`, `ROLE_WORKER`, `ROLE_ADMIN`.
   - BCrypt password hashing, Custom UserDetailsService, Method-level security (`@PreAuthorize`).

3. **Phase 3: Company Management**
   - Company & CompanyLocation entities and repositories.
   - Multi-tenant data isolation and IDOR access validation.

4. **Phase 4: Plant Catalog**
   - Categories, botanical attributes (Scientific name, sunlight, water, humidity, maintenance level, care instructions).
   - Filtered querying and pagination.

5. **Phase 5: Inventory Management**
   - Total, Available, Reserved, Installed, Rental, Damaged tracking.
   - Pessimistic locking (`@Lock(LockModeType.PESSIMISTIC_WRITE)`) to eliminate concurrency races and prevent negative inventory.

6. **Phase 6: Cart & Pricing**
   - Persistent user shopping carts with Buy vs Rent items, installation charges, and maintenance plan selection.
   - Server-side tax (GST) and grand total calculation.

7. **Phase 7: Booking & Site Inspection**
   - Full booking state machine (`CREATED`, `PENDING_PAYMENT`, `PAYMENT_COMPLETED`, `CONFIRMED`, `SITE_INSPECTION_REQUIRED`, `SITE_INSPECTION_COMPLETED`, `WORKER_ASSIGNED`, `INSTALLATION_SCHEDULED`, `INSTALLATION_STARTED`, `INSTALLATION_COMPLETED`, `MAINTENANCE_ACTIVE`, `COMPLETED`, `CANCELLED`).
   - Space, sunlight, temperature, humidity assessments.

8. **Phase 8: Worker & Installation**
   - Worker profile, employee code, specialization, availability management.
   - Assignment and installation execution workflow with before/after photos.

9. **Phase 9: Maintenance & Subscriptions**
   - Maintenance plans (Basic, Standard, Premium; `EVERY_4_DAYS`, `WEEKLY`, `TWICE_MONTHLY`, `MONTHLY`, `CUSTOM`).
   - Subscriptions lifecycle management (Pause, Activate, Cancel, Expire).
   - Scheduled auto-generation for upcoming service visits.

10. **Phase 10: Plant Tracking & Health**
    - Individual `PlantInstance` lifecycle with unique identifiers and QR code lookup.
    - Health logging (`HEALTHY`, `NEEDS_ATTENTION`, `UNHEALTHY`, `CRITICAL`, `REPLACED`), watering records, fertilizer logs, and historical plant replacements.

11. **Phase 11: Payment & Invoice**
    - Extensible `PaymentGateway` abstraction with `MockPaymentGateway` (Razorpay/Stripe ready).
    - Invoice creation and status handling without storing sensitive credentials.

12. **Phase 12: Emergency Support & Complaints**
    - Priority-based emergency maintenance requests (`LOW`, `MEDIUM`, `HIGH`, `CRITICAL`).
    - Customer complaint filing, investigation notes, and admin resolution workflows.

13. **Phase 13: Notification System**
    - Event-driven notifications with unread query and mark-as-read endpoints.

14. **Phase 14: Recommendations & Green Office Score**
    - Rule-based biophilic plant recommendation engine (area, sunlight, indoor, budget).
    - Green Office Score calculation (0–100) based on health ratio, green coverage, species diversity, and compliance.

15. **Phase 15: Reports & Admin APIs**
    - Real-time database metrics for total customers, active subscriptions, revenue, booking distribution, and plant health distribution.

16. **Phase 16: Testing & Security Testing**
    - 100% test pass rate across controller, service, repository, and security layers.

17. **Phase 17: Docker & Production Deployment**
    - Multi-stage `Dockerfile`, `docker-compose.yml`, AWS RDS/ECS readiness.

---

## 🛠️ Build & Run

### 1. Running Locally (Dev Profile)
```bash
mvn spring-boot:run
```
Swagger / Endpoints accessible at `http://localhost:8080`.

### 2. Running Test Suite
```bash
mvn test
```

### 3. Docker Compose (Production Profile with PostgreSQL)
```bash
docker-compose up --build
```
