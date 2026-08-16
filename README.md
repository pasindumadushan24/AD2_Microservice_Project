# Smart Parking Management System (SPMS)

A cloud-native, microservices-based backend application for managing users, vehicles, parking spaces, and payments.

##  Architecture

The system uses **Spring Boot + Spring Cloud** microservices architecture.

### Infrastructure Services

* **API Gateway** – Single entry point for client requests.
* **Eureka Server** – Service discovery and registration.
* **Config Server** – Centralized configuration management.

### Business Services

* **User Service** – Manages users and profiles.
* **Vehicle Service** – Manages vehicle registration.
* **Parking Space Service** – Manages parking spaces and availability.
* **Payment Service** – Manages parking payments and transactions.

```text
Client
   |
   v
API Gateway :8080
   |
   +-- User Service
   +-- Vehicle Service
   +-- Parking Service
   +-- Payment Service
          |
          v
       MySQL
```

##  Tech Stack

* Java 17+
* Spring Boot 3.x
* Spring Cloud
* Netflix Eureka
* Spring Cloud Gateway
* Spring Cloud Config
* MySQL
* Maven
* Git & GitHub
* Postman

##  Database Strategy

The project follows the **Database-per-Service** pattern.

```text
User Service       → spms_user_db
Vehicle Service    → spms_vehicle_db
Parking Service    → spms_parking_db
Payment Service    → spms_payment_db
```

Each microservice has its own independent database.

##  API Testing

A Postman collection containing the project APIs is included:

[SPMS Postman Collection](./SPMS_Postman_Collection.json)

Import the JSON file into Postman and send requests through the API Gateway.

**API Gateway:**

```text
http://localhost:8080
```

##  Eureka Dashboard

After starting the services, registered services can be viewed at:

```text
http://localhost:8761
```

![Eureka Dashboard](docs/screenshots/eureka_dashboard.png)

##  How to Run

### Prerequisites

* Java 17+
* Maven
* MySQL
* Postman

### 1. Clone Repository

```bash
git clone https://github.com/Sadeepa123L/Smart-Parking-Management-System.git
cd Smart-Parking-Management-System
```

### 2. Start MySQL

Create the required databases:

```sql
CREATE DATABASE spms_user_db;
CREATE DATABASE spms_vehicle_db;
CREATE DATABASE spms_parking_db;
CREATE DATABASE spms_payment_db;
```

### 3. Start Services

Run services in this order:

```text
Config Server
      ↓
Eureka Server
      ↓
User Service
Vehicle Service
Parking Space Service
Payment Service
      ↓
API Gateway
```

Or use:

```powershell
.\start-all.ps1
```

##  Project Structure

```text
Smart-Parking-Management-System/
├── api-gateway/
├── config-server/
├── eureka-server/
├── user-service/
├── vehicle-service/
├── parking-space-service/
├── payment-service/
├── docs/
│   └── screenshots/
│       └── eureka_dashboard.png
├── SPMS_Postman_Collection.json
├── start-all.ps1
└── README.md
```

##  Git Workflow

The project uses Git and GitHub with feature branches.

```text
main
├── feature/user-service
├── feature/vehicle-service
├── feature/parking-service
└── feature/payment-service
```

##  Key Features

* Microservices Architecture
* API Gateway
* Eureka Service Discovery
* Centralized Configuration
* Database-per-Service
* REST APIs
* MySQL Integration
* Postman API Testing
* Git & GitHub Feature Branching


