# Hospital Management System

A microservices-based hospital management system built with Spring Boot and Spring Cloud. This system provides comprehensive management of hospital operations using a **Server Orchestration Pattern**, where the Appointment Service acts as the central orchestrator coordinating all other services.

## 🏥 Project Overview

This is a distributed microservice architecture designed to handle various hospital management operations. The system follows a **Server Orchestration Pattern** where the **Appointment Service** serves as the orchestrator that coordinates workflows across multiple services.

### Architecture Pattern: Server Orchestration

In this pattern, the Appointment Service acts as the central coordinator:
- It receives requests and orchestrates calls to other services
- It manages the workflow of creating appointments (validates user, doctor, schedule, payment)
- It handles transaction coordination across services

### Technology Stack

| Component | Technology |
|-----------|------------|
| Framework | Spring Boot 4.1.0 |
| Cloud | Spring Cloud 2025.1.2 |
| Java Version | Java 21 |
| Service Discovery | Netflix Eureka |
| API Gateway | Spring Cloud Gateway |
| Communication | REST + OpenFeign |
| Database (Primary) | PostgreSQL |
| Database (Medical Records) | MongoDB |
| Build Tool | Maven |
| Containerization | Docker & Docker Compose |

## 🏗️ Architecture

### Server Orchestration Pattern

```
                                    ┌─────────────────┐
                                    │  hospital-postgres│
                                    │   (Port 5432)   │
                                    └────────┬────────┘
                                             │
┌──────────┐     ┌──────────┐     ┌─────────┴─────────┐
│  Client  │────▶│  Gateway  │────▶│  Discovery Server │
│          │     │ (8080)   │     │     (8761)       │
└──────────┘     └──────────┘     └─────────┬─────────┘
                                             │
                    ┌────────────────────────┴────────────────────────┐
                    │                                                   │
         ┌──────────▼──────────┐  ┌──────────▼──────────┐  ┌──────────▼──────────┐
         │    User Service     │  │   Doctor Service    │  │ Appointment Service  │
         │     (8081)         │  │      (8082)        │  │    (8083) 🏆        │
         │  [hospital-postgres]│  │  [hospital-postgres]│  │  [hospital-postgres]│◀─────┐
         └──────────┬──────────┘  └──────────┬──────────┘  └──────────┬──────────┘      │
                    │                           │                     │                  │
                    │◀──────────────────────────┼─────────────────────┼──────────────────│
                    │                           │                     │                  │
         ┌──────────▼──────────┐  ┌──────────▼──────────┐  ┌──────────▼──────────┐      │
         │   Schedule Service  │  │Medical Record Svc   │  │   Payment Service   │      │
         │      (8084)         │  │      (8085)         │  │      (8086)         │      │
         │  [hospital-postgres]│  │   [hospital-mongo]  │  │  [hospital-postgres]│◀─────┤
         └────────────────────┘  └─────────────────────┘  └──────────────────────┘      │
                                                                                           │
                    ┌───────────────────────────────────────────────────────────────────────┘
                    │
                    ▼
         ┌──────────────────────┐
         │     APPOINTMENT     │
         │     SERVICE IS THE   │
         │      ORCHESTRATOR    │
         │                      │
         │  • Validates User    │
         │  • Checks Doctor     │
         │  • Manages Schedule  │
         │  • Processes Payment │
         │  • Coordinates All   │
         └──────────────────────┘
```

### Flow Example: Create Appointment

```
1. Client → Gateway (8080)
2. Gateway → Appointment Service (8083) [Orchestrator]
3. Appointment Service:
   ├── Calls User Service (8081) → Validate patient
   ├── Calls Doctor Service (8082) → Check doctor availability
   ├── Calls Schedule Service (8084) → Book time slot
   ├── Calls Payment Service (8086) → Process payment
   └── Returns response to Client
```

## 📁 Project Structure

```
hospital-management/
├── docker-compose.yml        # Docker orchestration
├── Makefile                  # Make commands for Docker
├── .env                      # Environment variables
├── README.md                 # This documentation
│
├── discovery-service/        # Eureka Service Registry (8761)
│   └── src/main/resources/
│       └── application.yml   # Discovery server config
│
├── gateway/                  # API Gateway (8080)
│   └── src/main/resources/
│       └── application.yml   # Routes config
│
├── user-service/             # User/Patient Management (8081)
│   └── src/main/resources/
│       └── application.yml   # PostgreSQL + Eureka
│
├── doctor-service/           # Doctor Management (8082)
│   └── src/main/resources/
│       └── application.yml   # PostgreSQL + Eureka
│
├── appointment-service/      # 🏆 ORCHESTRATOR - Appointment Management (8083)
│   └── src/main/resources/
│       └── application.yml   # PostgreSQL + Eureka
│
├── schedule-service/         # Schedule Management (8084)
│   └── src/main/resources/
│       └── application.yml   # PostgreSQL + Eureka
│
├── medical-record-service/   # Medical Records - MongoDB (8085)
│   └── src/main/resources/
│       └── application.yml   # MongoDB + Eureka
│
└── payment-service/         # Payment Processing (8086)
    └── src/main/resources/
        └── application.yml   # PostgreSQL + Eureka
```

## 📋 Services Description

| Service | Port | Database | Role |
|---------|------|----------|------|
| **Discovery Service** | 8761 | - | Netflix Eureka server for service registration and discovery |
| **Gateway** | 8080 | - | API Gateway for routing requests to appropriate services |
| **User Service** | 8081 | PostgreSQL | Manages patient/user accounts and authentication |
| **Doctor Service** | 8082 | PostgreSQL | Handles doctor profiles and information |
| **Appointment Service** | 8083 | PostgreSQL | 🏆 **ORCHESTRATOR** - Coordinates appointment workflow |
| **Schedule Service** | 8084 | PostgreSQL | Handles doctor scheduling and availability |
| **Medical Record Service** | 8085 | MongoDB | Stores and manages medical records |
| **Payment Service** | 8086 | PostgreSQL | Processes appointment payments |

## 🚀 Quick Start

### Prerequisites

- **Java 21** or higher
- **Maven 3.9+**
- **Docker** and **Docker Compose**
- **Bun** (for git hooks)

---

## 🐳 Run with Docker (Recommended)

### 1. Build and Start All Services

```bash
# Navigate to project directory
cd hospital-management

# Build and start all containers
make up

# Or use docker-compose directly
docker compose up --build -d
```

### 2. Check Service Status

```bash
# View running containers
make ps

# Check health status
make status

# View logs
make logs
```

### 3. Stop Services

```bash
# Stop and remove containers (keeps volumes)
make down

# Full clean (removes containers, volumes, and images)
make clean
```

### 4. Rebuild from Scratch

```bash
make rebuild
```

---

## 💻 Run Manually (Development)

### 1. Start Databases

#### PostgreSQL
```bash
docker run -d \
  --name hospital-postgres \
  -e POSTGRES_USER=postgres \
  -e POSTGRES_PASSWORD=hosppass123 \
  -e POSTGRES_DB=hospital_db \
  -p 5432:5432 \
  postgres:16-alpine
```

#### MongoDB
```bash
docker run -d \
  --name hospital-mongo \
  -e MONGO_INITDB_ROOT_USERNAME=mongo \
  -e MONGO_INITDB_ROOT_PASSWORD=mongopass123 \
  -e MONGO_INITDB_DATABASE=medical_record_db \
  -p 27017:27017 \
  mongo:7
```

### 2. Run Services (Each service already has application.yml configured)

**Terminal 1 - Discovery Service:**
```bash
cd hospital-management/discovery-service
mvn spring-boot:run
```

**Terminal 2 - User Service:**
```bash
cd hospital-management/user-service
mvn spring-boot:run
```

**Terminal 3 - Doctor Service:**
```bash
cd hospital-management/doctor-service
mvn spring-boot:run
```

**Terminal 4 - Payment Service:**
```bash
cd hospital-management/payment-service
mvn spring-boot:run
```

**Terminal 5 - Schedule Service:**
```bash
cd hospital-management/schedule-service
mvn spring-boot:run
```

**Terminal 6 - Medical Record Service:**
```bash
cd hospital-management/medical-record-service
mvn spring-boot:run
```

**Terminal 7 - Appointment Service (Orchestrator):**
```bash
cd hospital-management/appointment-service
mvn spring-boot:run
```

**Terminal 8 - Gateway:**
```bash
cd hospital-management/gateway
mvn spring-boot:run
```

### 3. Build JAR Files

```bash
cd hospital-management

for service in discovery-service user-service doctor-service payment-service \
               schedule-service medical-record-service appointment-service gateway; do
    cd $service
    mvn clean package -DskipTests
    cd ..
done
```

---

## ⚙️ Configuration Details

### Environment Variables (.env)

```env
# PostgreSQL
POSTGRES_USER=postgres
POSTGRES_PASSWORD=hosppass123
POSTGRES_DB=hospital_db

# MongoDB
MONGO_INITDB_ROOT_USERNAME=mongo
MONGO_INITDB_ROOT_PASSWORD=mongopass123
MONGO_INITDB_DATABASE=medical_record_db

# Admin Panels
PGADMIN_DEFAULT_EMAIL=admin@admin.com
PGADMIN_DEFAULT_PASSWORD=pgadmin123

MONGO_EXPRESS_USER=mongoadmin
MONGO_EXPRESS_PASSWORD=mongopass123
```

### Service Configurations (application.yml)

Each service has `application.yml` pre-configured for Docker environment:

**PostgreSQL Services** (user, doctor, appointment, schedule, payment):
```yaml
spring:
  datasource:
    url: jdbc:postgresql://hospital-postgres:5432/hospital_db
    username: postgres
    password: hosppass123
```

**MongoDB Service** (medical-record-service):
```yaml
spring:
  data:
    mongodb:
      host: hospital-mongo
      port: 27017
      database: medical_record_db
      username: mongo
      password: mongopass123
```

**Eureka Configuration** (all services):
```yaml
eureka:
  client:
    service-url:
      defaultZone: http://discovery-service:8761/eureka
```

---

## 🌐 Access Points

### Services

| Service | URL |
|---------|-----|
| Gateway | http://localhost:8080 |
| Discovery (Eureka) | http://localhost:8761 |
| User Service | http://localhost:8081 |
| Doctor Service | http://localhost:8082 |
| **Appointment Service** | http://localhost:8083 (**Orchestrator**) |
| Schedule Service | http://localhost:8084 |
| Medical Record Service | http://localhost:8085 |
| Payment Service | http://localhost:8086 |

### Admin Panels

| Service | URL | Credentials |
|---------|-----|-------------|
| pgAdmin | http://localhost:8087 | admin@admin.com / pgadmin123 |
| MongoDB Express | http://localhost:8088 | mongoadmin / mongopass123 |
| Eureka Dashboard | http://localhost:8761 | (no auth) |

### Database Connections

| Database | Host | Port | Credentials |
|----------|------|------|-------------|
| PostgreSQL | localhost | 5432 | postgres / hosppass123 |
| MongoDB | localhost | 27017 | mongo / mongopass123 |

---

## 🛠️ Make Commands

```bash
make help     # Show all available commands
make up       # Build and start all containers
make down     # Stop all containers
make restart  # Restart all containers
make build    # Build Docker images
make rebuild  # Rebuild without cache and restart
make logs     # View container logs
make ps       # Show running containers
make status   # Show service health status
make clean    # Remove containers, volumes, images
make prune    # Clean up dangling images/volumes
```

---

## 📡 API Documentation

### Gateway Routes

All services are accessible through the gateway at `http://localhost:8080` with clean REST URLs:

```
GET/POST/PUT/DELETE /users/**          → User Service
GET/POST/PUT/DELETE /doctors/**        → Doctor Service
GET/POST/PUT/DELETE /appointments/**  → Appointment Service (Orchestrator)
GET/POST/PUT/DELETE /schedules/**      → Schedule Service
GET/POST/PUT/DELETE /records/**        → Medical Record Service
GET/POST/PUT/DELETE /payments/**       → Payment Service
```

### Example API Calls

```bash
# ========== Appointments (Orchestrator) ==========
GET    http://localhost:8080/appointments
POST   http://localhost:8080/appointments
GET    http://localhost:8080/appointments/1
PUT    http://localhost:8080/appointments/1
DELETE http://localhost:8080/appointments/1

# ========== Doctors ==========
GET    http://localhost:8080/doctors
POST   http://localhost:8080/doctors
GET    http://localhost:8080/doctors/1
PUT    http://localhost:8080/doctors/1
DELETE http://localhost:8080/doctors/1

# ========== Users ==========
GET    http://localhost:8080/users
POST   http://localhost:8080/users
GET    http://localhost:8080/users/1
PUT    http://localhost:8080/users/1
DELETE http://localhost:8080/users/1

# ========== Schedules ==========
GET    http://localhost:8080/schedules
POST   http://localhost:8080/schedules
GET    http://localhost:8080/schedules/1

# ========== Payments ==========
GET    http://localhost:8080/payments
POST   http://localhost:8080/payments
GET    http://localhost:8080/payments/1

# ========== Medical Records ==========
GET    http://localhost:8080/records
POST   http://localhost:8080/records
GET    http://localhost:8080/records/1
```

### Health Endpoints

```bash
curl http://localhost:8080/actuator/health      # Gateway
curl http://localhost:8761/actuator/health      # Discovery
curl http://localhost:8081/actuator/health      # User Service
curl http://localhost:8082/actuator/health     # Doctor Service
curl http://localhost:8083/actuator/health      # Appointment Service
curl http://localhost:8084/actuator/health      # Schedule Service
curl http://localhost:8085/actuator/health      # Medical Record Service
curl http://localhost:8086/actuator/health      # Payment Service
```

---

## 🔄 Orchestration Pattern Details

### Why Server Orchestration?

The **Appointment Service** acts as the central orchestrator because:

1. **Centralized Workflow Logic** - All appointment-related business logic is in one place
2. **Transaction Management** - Can coordinate and rollback if any step fails
3. **Clear API Contract** - Clients only need to know the orchestrator
4. **Service Independence** - Other services don't need to know about each other

### Orchestrator Business Logic

```
┌─────────────────────────────────────────────────────────────┐
│              APPOINTMENT SERVICE (Orchestrator)              │
│                                                             │
│   POST /api/appointments                                    │
│                                                             │
│   1. Validate User                                          │
│      └─ Call User Service → IF not found → throw error     │
│                                                             │
│   2. Validate Doctor                                        │
│      └─ Call Doctor Service → IF not available → throw error │
│                                                             │
│   3. Check Schedule                                         │
│      └─ Call Schedule Service → IF slot taken → throw error │
│                                                             │
│   4. Process Payment                                        │
│      └─ Call Payment Service → IF failed → throw error     │
│                                                             │
│   5. Create Appointment                                     │
│      └─ Save to local database                             │
│      └─ Return success response                            │
└─────────────────────────────────────────────────────────────┘
```

### Responsibilities of Appointment Service (Orchestrator)

- Receives and validates appointment requests
- Orchestrates calls to User, Doctor, Schedule, and Payment services
- Manages the appointment lifecycle
- Handles errors and rollbacks across services
- Returns consolidated responses to clients

---

## 🔒 Security Notes

- User Service includes Spring Security for authentication
- All service-to-service communication uses Eureka for discovery
- Use Feign clients for declarative REST calls between services
- Change default passwords in production!
- Use environment-specific configurations for production deployment

---

## 📜 Git & Conventional Commits

This project uses **Conventional Commits** with **Husky** for commit message enforcement.

### Setup (One Time)

```bash
# 1. Initialize git
git init
git add .
git commit -m "initial commit"

# 2. Install husky hooks
bunx husky install

# 3. Verify hooks are installed
cat .husky/commit-msg
```

### Commit Message Format

```
<type>: <description>

[optional body]

[optional footer]
```

### Commit Types

| Type | Description |
|------|-------------|
| `feat` | New feature |
| `fix` | Bug fix |
| `docs` | Documentation changes |
| `style` | Code style changes (formatting, no logic change) |
| `refactor` | Code refactoring (no feature/fix) |
| `perf` | Performance improvements |
| `test` | Adding or updating tests |
| `build` | Build system or dependency changes |
| `ci` | CI configuration changes |
| `chore` | Other changes (maintenance, tooling) |
| `revert` | Revert previous commit |
| `docker` | Docker related changes |
| `db` | Database related changes |

### Examples

```bash
# Good commits
git commit -m "feat: add appointment booking endpoint"
git commit -m "fix: resolve payment processing error"
git commit -m "docs: update API documentation"
git commit -m "docker: add postgres health check"
git commit -m "feat(appointment): implement scheduling validation"

# Bad commits (will be rejected)
git commit -m "fixed stuff"           # ❌ missing type
git commit -m "WIP"                    # ❌ too short
git commit -m "feat: Added new feature for users" # ❌ wrong case
```

### Generate Changelog

```bash
bun run changelog
```

---

## 📝 License

This project is for educational/demonstration purposes.
