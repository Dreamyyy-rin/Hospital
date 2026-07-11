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

| Tool | Version | Required | Purpose |
|------|---------|----------|---------|
| **Java** | 21+ | ✅ | Runtime for Spring Boot |
| **Maven** | 3.9+ | ✅ | Build tool |
| **Docker** | Latest | ✅ | Containerization |
| **Bun** | Latest | ✅ | Git hooks (commitlint) |
| **Git** | Latest | ✅ | Version control |

---

### 🪝 Setup Git Hooks (All Platforms)

#### Mac / Linux

```bash
# Install dependencies (auto-installs husky)
bun install

# Make hooks executable
chmod +x .husky/commit-msg .husky/pre-commit

# Verify hooks are installed
ls -la .husky/
```

#### Windows (PowerShell)

```powershell
# Install dependencies
bun install

# Make hooks executable (PowerShell)
icacls .husky\commit-msg /grant Everyone:RX
icacls .husky\pre-commit /grant Everyone:RX

# Or using Git Bash (if installed)
chmod +x .husky/commit-msg .husky/pre-commit

# Verify hooks are installed
dir .husky
```

#### Windows (Git Bash / WSL)

```bash
# Same as Mac/Linux
bun install
chmod +x .husky/commit-msg .husky/pre-commit
```

---

### 🛠️ Platform-Specific Setup

#### 🍎 Mac

```bash
# Install Homebrew (if not installed)
/bin/bash -c "$(curl -fsSL https://raw.githubusercontent.com/Homebrew/install/HEAD/install.sh)"

# Install Java 21
brew install openjdk@21
echo 'export JAVA_HOME=/opt/homebrew/opt/openjdk@21/libexec/openjdk.jdk/Contents/Home' >> ~/.zshrc
source ~/.zshrc

# Install Maven
brew install maven

# Install Bun
curl -fsSL https://bun.sh/install | bash

# Install Docker Desktop
brew install --cask docker

# Start Docker
open -a Docker
```

#### 🐧 Linux (Ubuntu/Debian)

```bash
# Update packages
sudo apt update && sudo apt upgrade -y

# Install Java 21
sudo apt install openjdk-21-jdk -y
export JAVA_HOME=/usr/lib/jvm/java-21-openjdk-amd64

# Install Maven
sudo apt install maven -y

# Install Bun
curl -fsSL https://bun.sh/install | bash

# Install Docker
sudo apt install docker.io docker-compose -y
sudo usermod -aG docker $USER
newgrp docker

# Install Make (for Makefile commands)
sudo apt install make -y
```

#### 🪟 Windows

```powershell
# Install Chocolatey (Package Manager)
Set-ExecutionPolicy Bypass -Scope Process -Force
iex ((New-Object System.Net.WebClient).DownloadString('https://chocolatey.org/install.ps1'))

# Install Java 21
choco install openjdk21 -y

# Install Maven
choco install maven -y

# Install Bun
irm bun.sh/install.ps1 | iex

# Install Docker Desktop
choco install docker-desktop -y

# Install Git (if not installed)
choco install git -y

# Install Make (for Makefile commands)
choco install make -y

# Restart terminal after installations
```

**Alternative for Windows:** Use [WSL2](https://docs.microsoft.com/en-us/windows/wsl/install) (Windows Subsystem for Linux) for a Linux environment with full compatibility.

---

### ✅ Verify Installation

```bash
# Check all installed tools
java -version
mvn -version
docker --version
bun --version
git --version
```

---

## 🐳 Run with Docker (Recommended)

### Mac / Linux

```bash
# Navigate to project directory
cd hospital-management

# Build and start all containers
make up

# Or use docker-compose directly
docker compose up --build -d
```

### Windows

```powershell
# Navigate to project directory
cd hospital-management

# Build and start all containers
make up

# Or use docker-compose directly
docker compose up --build -d

# If Make doesn't work, use these commands:
docker compose build
docker compose up -d
```

### Docker Commands (All Platforms)

| Command | Description |
|---------|-------------|
| `make up` | Build and start all containers |
| `make down` | Stop all containers |
| `make ps` | View running containers |
| `make logs` | View container logs |
| `make status` | Check service health |
| `make clean` | Remove containers and volumes |
| `make rebuild` | Rebuild from scratch |

**Without Makefile (Windows/Alternative):**
```bash
# Start services
docker compose up --build -d

# Stop services
docker compose down

# View logs
docker compose logs -f

# Check status
docker compose ps

# Clean everything
docker compose down -v --rmi all

# Rebuild
docker compose down -v
docker compose up --build -d
```

## 💻 Run Manually (Development)

> **Note:** For most development, use Docker. Manual run is for debugging or specific setups.

### 1. Start Databases

#### Mac / Linux
```bash
docker run -d \
  --name hospital-postgres \
  -e POSTGRES_USER=postgres \
  -e POSTGRES_PASSWORD=hosppass123 \
  -e POSTGRES_DB=hospital_db \
  -p 5432:5432 \
  postgres:16-alpine

docker run -d \
  --name hospital-mongo \
  -e MONGO_INITDB_ROOT_USERNAME=mongo \
  -e MONGO_INITDB_ROOT_PASSWORD=mongopass123 \
  -e MONGO_INITDB_DATABASE=medical_record_db \
  -p 27017:27017 \
  mongo:7
```

#### Windows (PowerShell)
```powershell
docker run -d --name hospital-postgres -e POSTGRES_USER=postgres -e POSTGRES_PASSWORD=hosppass123 -e POSTGRES_DB=hospital_db -p 5432:5432 postgres:16-alpine

docker run -d --name hospital-mongo -e MONGO_INITDB_ROOT_USERNAME=mongo -e MONGO_INITDB_ROOT_PASSWORD=mongopass123 -e MONGO_INITDB_DATABASE=medical_record_db -p 27017:27017 mongo:7
```

### 2. Run Each Service

Start services in order (each in separate terminal/tab):

```bash
# Start Discovery Service first
cd discovery-service && mvn spring-boot:run

# Then start other services (can be in parallel)
cd user-service && mvn spring-boot:run
cd doctor-service && mvn spring-boot:run
cd payment-service && mvn spring-boot:run
cd schedule-service && mvn spring-boot:run
cd medical-record-service && mvn spring-boot:run
cd appointment-service && mvn spring-boot:run
cd gateway && mvn spring-boot:run
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

### Mac / Linux

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

### Windows (PowerShell / CMD)

```powershell
# If Make is installed (via choco install make)
make up

# Otherwise use docker-compose directly:
docker compose up --build -d
docker compose down
docker compose ps
docker compose logs -f
docker compose down -v
```

### Windows (Git Bash / WSL)

```bash
# Same as Mac/Linux
make up
make down
# etc.
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

### 📚 API Documentation (Swagger/OpenAPI)

All services have Swagger UI for interactive API documentation.

#### Main Gateway (Access All Services Here)

| URL | Description |
|-----|-------------|
| http://localhost:8080/swagger-ui.html | Gateway Swagger - Select service from dropdown |
| http://localhost:8080/v3/api-docs | OpenAPI JSON spec |

#### Individual Service Documentation

| Service | Swagger UI | Port |
|---------|------------|------|
| User Service | http://localhost:8080/swagger-ui.html?group=user-service | 8081 |
| Doctor Service | http://localhost:8080/swagger-ui.html?group=doctor-service | 8082 |
| Appointment Service | http://localhost:8080/swagger-ui.html?group=appointment-service | 8083 |
| Schedule Service | http://localhost:8080/swagger-ui.html?group=schedule-service | 8084 |
| Medical Record | http://localhost:8080/swagger-ui.html?group=medical-record-service | 8085 |
| Payment Service | http://localhost:8080/swagger-ui.html?group=payment-service | 8086 |

#### Using Swagger UI

1. Open http://localhost:8080/swagger-ui.html
2. Select a service from the **top dropdown** (e.g., user-service)
3. Use the **Server dropdown** to choose:
   - `http://localhost:8080` - Route through Gateway
   - `http://localhost:8081` (etc.) - Direct to service
4. Click any endpoint → **Try it out** to test

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

## ⚠️ DEVELOPMENT STANDARDS (MANDATORY)

All developers **MUST** follow these standards before contributing.

---

## 🪝 Git Hooks (Husky) - REQUIRED

This project uses **Husky** to enforce commit message conventions. Git hooks are **mandatory** - they ensure consistent commit history and are part of the CI/CD pipeline.

### One-Time Setup for New Developers

```bash
# 1. Install dependencies (auto-installs husky on first run)
bun install

# 2. Husky is automatically initialized via prepare script
# Just verify hooks are installed:
ls -la .husky/

# 3. Verify commit-msg hook is executable
chmod +x .husky/commit-msg .husky/pre-commit

# 4. Test the setup with a valid commit
git commit -m "chore: test husky setup"
```

### Why is this mandatory?

- ✅ Enforces consistent commit history
- ✅ Generates automatic changelog
- ✅ Required for CI/CD pipeline
- ✅ Makes code review easier
- ✅ Follows industry best practices

### Troubleshooting

```bash
# If hooks are not running:
git config core.hooksPath .husky

# Re-enable hooks if disabled:
git hooks --enable

# Skip hooks (NOT recommended, only for emergencies):
git commit --no-verify -m "emergency fix"

# Update husky:
bun add -D husky@latest
bun run prepare
```

---

## 📜 Conventional Commits - REQUIRED

### Commit Message Format

```
<type>: <short description>

[optional body]

[optional footer]
```

**Rules:**
- Type is **required** and must be lowercase
- Subject/description is **required**
- Max **100 characters** total
- No period at the end
- Use imperative mood (e.g., "add" not "added")

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
# ✅ Valid commits
git commit -m "feat: add appointment booking endpoint"
git commit -m "fix: resolve payment processing error"
git commit -m "docs: update API documentation"
git commit -m "refactor: simplify user service"
git commit -m "test: add unit tests for appointment"
git commit -m "docker: add health check for postgres"
git commit -m "ci: add github actions workflow"

# ❌ Invalid commits (will be REJECTED)
git commit -m "fixed stuff"                        # Missing type
git commit -m "WIP"                               # Too short & no type
git commit -m "Added new feature for users"        # Uppercase & wrong tense
git commit -m "feat: something here."              # Period at end
git commit -m "this is a very long message that exceeds the maximum allowed length which is 100 characters"  # Too long
```

### Generate Changelog

```bash
bun run changelog
```

---

## 📝 License

This project is for educational/demonstration purposes.
