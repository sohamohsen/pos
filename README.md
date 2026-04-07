# Advanced Point of Sale (POS) Microservices Ecosystem
A modern, enterprise-ready Point of Sale backend built using **Spring Boot 3**, **Spring Cloud**, **Netflix Eureka**, and **PostgreSQL**. The project is split into 7 isolated microservices communicating via **Feign** load-balanced through **Spring Cloud Gateway**.

## 🏗️ Architecture

The backend ecosystem comprises the following 7 microservices:

1. **`pos_registry` (Port 8761):** Netflix Eureka Service Registry. All services register here to enable dynamic service discovery.
2. **`pos_gateway` (Port 8000):** Spring Cloud API Gateway. Serves as the single unified entry point for all frontend traffic, abstracting internal architectures and handling CORS.
3. **`user` (Port 8080):** Manage Users, Roles, employees, and Auth authentication context (JWT).
4. **`pos_product` (Port 8081):** Product Catalog & Category management.
5. **`pos_inventory` (Port 8082):** Stock Tracking and Stock Movement histories.
6. **`pos_payment` (Port 8083):** Checkout, Payment processing, generating Receipts, and updating inventory dynamically upon checkout.
7. **`pos_customer` (Port 8084):** Customer CRM and automated Loyalty Point calculation.
8. **`pos_reporting` (Port 8085):** Aggregated analytics and dashboard metrics.

## 🚀 How to Run Locally

### Prerequisites
- Java JDK 17
- PostgreSQL running locally on default port `5432` with databases created (`pos_user`, `pos_product`, `pos_inventory`, `pos_payment`, `pos_customer`).
- Maven (included via `mvnw` wrapper in each service directory)

### Startup Sequence

> **IMPORTANT:** You must start the infrastructure services strictly in the following order:

1. **Start the Registry First**
   Wait for this to fully start up on `8761`.
   ```bash
   cd pos_registry
   ./mvnw spring-boot:run
   ```

2. **Start the Core Services**
   These can be started in any order. Open separate terminal windows for each:
   ```bash
   # User Service
   cd user && ./mvnw spring-boot:run
   
   # Product Service
   cd pos_product && ./mvnw spring-boot:run
   
   # Inventory Service
   cd pos_inventory && ./mvnw spring-boot:run
   
   # Payment Service
   cd pos_payment && ./mvnw spring-boot:run
   
   # Customer Service
   cd pos_customer && ./mvnw spring-boot:run
   
   # Reporting Service
   cd pos_reporting && ./mvnw spring-boot:run
   ```

3. **Start the API Gateway**
   Once all services are up and registered with Eureka, spin up the entry point:
   ```bash
   cd pos_gateway
   ./mvnw spring-boot:run
   ```

## 🌐 API Routing via Gateway

The UI should make all requests strictly to **`http://localhost:8000`**. The gateway proxies requests based on the prefix:

- `/api/auth/**` & `/api/user/**` 👉 `pos_user`
- `/api/product/**` 👉 `pos_product`
- `/api/inventory/**` 👉 `pos_inventory`
- `/api/payment/**` 👉 `pos_payment`
- `/api/customer/**` 👉 `pos_customer`
- `/api/reports/**` 👉 `pos_reporting`

## 🔒 Security
Microservices leverage stateless **JWT Authentication**. Ensure `jwt.secret` in `application.properties` matches uniformly across all downstream services that mandate role-based checks.

## ✍️ Authors
Built with an advanced Microservice architectural approach utilizing the latest industry best practices.
