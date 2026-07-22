# Hospital Management System - Complete Documentation

## Table of Contents

1. [System Overview](#system-overview)
2. [Architecture](#architecture)
3. [Services Description](#services-description)
4. [Service Communication](#service-communication)
5. [API Endpoints](#api-endpoints)
6. [Appointment Workflow](#appointment-workflow)
7. [Testing Guide](#testing-guide)
8. [Swagger UI Setup](#swagger-ui-setup)
9. [Setup & Running](#setup--running)
10. [Environment Configuration](#environment-configuration)

---

## System Overview

A microservices-based hospital management system using:
- **Spring Boot 4.1.0** with **Java 21**
- **Spring Cloud 2025.1.2** for microservices infrastructure
- **Netflix Eureka** for service discovery
- **Spring Cloud Gateway** for API routing
- **OpenFeign** for inter-service communication
- **PostgreSQL** (via Supabase) for user, doctor, schedule, payment, appointment data
- **MongoDB** (via Atlas) for medical records

### Project Structure

```
hospital-management/
├── discovery-service/         # Netflix Eureka Server (8761)
├── gateway/                   # API Gateway (8080)
├── user-service/              # User management & auth (8081)
├── doctor-service/            # Doctor management (8082)
├── appointment-service/       # Orchestrator (8083)
├── schedule-service/          # Schedule management (8084)
├── medical-record-service/    # Medical records - MongoDB (8085)
└── payment-service/           # Payment processing (8086)
```

---

## Architecture

### Service Discovery Pattern (Eureka)

```
┌─────────────────────────────────────────────────────────────────┐
│                        Netflix Eureka                           │
│                    (http://discovery-service:8761)              │
├─────────────────────────────────────────────────────────────────┤
│                                                                 │
│   All services register themselves with Eureka:                │
│                                                                 │
│   ┌─────────────┐    ┌─────────────┐    ┌─────────────────┐   │
│   │ user-service│    │doctor-service│   │appointment-service│  │
│   │   :8081     │    │    :8082     │   │      :8083       │  │
│   └─────────────┘    └─────────────┘    └─────────────────┘   │
│                                                                 │
│   ┌─────────────┐    ┌─────────────┐    ┌─────────────────┐   │
│   │schedule-svc │    │medical-rec   │   │  payment-service │   │
│   │    :8084    │    │   :8085     │   │      :8086       │   │
│   └─────────────┘    └─────────────┘    └─────────────────┘   │
│                                                                 │
└─────────────────────────────────────────────────────────────────┘
```

### Feign Client Pattern (No Hardcoded URLs)

When appointment-service needs to call user-service:

```java
// BEFORE (hardcoded - BAD)
@FeignClient(name = "user-service", url = "http://user-service:8081")

// AFTER (Eureka discovery - GOOD)
@FeignClient(name = "user-service")
```

Eureka automatically resolves the service name to the actual URL.

---

## Services Description

| Service | Port | Database | Description |
|---------|------|----------|-------------|
| **discovery-service** | 8761 | - | Eureka Server - registers all services |
| **gateway** | 8080 | - | Routes requests to appropriate services |
| **user-service** | 8081 | PostgreSQL | User accounts, authentication (JWT), roles |
| **doctor-service** | 8082 | PostgreSQL | Doctor profiles, specialties |
| **appointment-service** | 8083 | PostgreSQL | **ORCHESTRATOR** - coordinates all workflows |
| **schedule-service** | 8084 | PostgreSQL | Doctor schedules, time slots |
| **medical-record-service** | 8085 | MongoDB | Medical records, diagnoses, medications |
| **payment-service** | 8086 | PostgreSQL | Payment processing |

### Service Responsibilities

| Service | Responsibilities |
|---------|-------------------|
| **user-service** | Register, login, JWT auth, user CRUD, role management (ADMIN, DOCTOR, NURSE, PATIENT) |
| **doctor-service** | Doctor CRUD, create doctor+user together (admin), link to user account |
| **schedule-service** | Create/read/update schedules, available slots, link to doctor |
| **appointment-service** | Orchestrator - validates all data, coordinates workflow, manages appointment lifecycle |
| **payment-service** | Create payments, update status (PENDING, COMPLETED, FAILED), calculate change |
| **medical-record-service** | Create/read/update medical records, diagnoses, medications |

---

## Service Communication

### Feign Clients Map

```
appointment-service (ORCHESTRATOR)
├── UserClient ──────────────► user-service (validate patient)
├── DoctorClient ────────────► doctor-service (validate doctor)
├── ScheduleClient ─────────► schedule-service (validate schedule)
├── PaymentClient ──────────► payment-service (get payment status)
└── MedicalRecordClient ────► medical-record-service (link medical record)

doctor-service
└── UserClient ──────────────► user-service (register user, get user)

schedule-service
├── DoctorClient ────────────► doctor-service (validate doctor)
└── UserClient ──────────────► user-service (get doctor info)
```

### OpenFeign Response Classes

Each Feign client uses `@Data` (Lombok) for response DTOs:

```java
@FeignClient(name = "user-service")
public interface UserClient {
    @GetMapping("/users/{id}")
    ResponseEntity<UserResponse> getUserById(@PathVariable("id") Integer id);

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    class UserResponse {
        private Integer id;
        private String name;
        private String email;
        private String role;
        private String phone;
    }
}
```

---

## API Endpoints

> **Note:** All API calls go through the **Gateway (port 8080)** unless testing individual services directly.

### Gateway Routes

All requests go through gateway at `http://localhost:8080`:

| Path | Service | Description |
|------|---------|-------------|
| `/users/**` | user-service | User management, auth |
| `/doctors/**` | doctor-service | Doctor management |
| `/appointments/**` | appointment-service | Appointments (orchestrator) |
| `/schedules/**` | schedule-service | Schedule management |
| `/records/**` | medical-record-service | Medical records |
| `/payments/**` | payment-service | Payments |

### User Service (8081)

```bash
# Authentication
POST   /api/auth/register     # Register new user
POST   /api/auth/login        # Login, returns JWT token

# User Management (requires auth)
GET    /users                # Get all users (ADMIN only)
GET    /users/{id}           # Get user by ID
GET    /users/email/{email}  # Get user by email
GET    /users/role/{role}    # Get users by role
PUT    /users/{id}           # Update user
DELETE /users/{id}           # Delete user (ADMIN only)
```

### Doctor Service (8082)

```bash
# Doctor Management
GET    /doctors                    # Get all doctors
GET    /doctors/{id}              # Get doctor by ID
GET    /doctors/user/{userId}     # Get doctor by user ID
GET    /doctors/active            # Get active doctors

# Create Doctor
POST   /doctors                   # Create with existing userId
POST   /doctors/admin/create-with-user  # Create user + doctor together (ADMIN)

# Update/Delete
PUT    /doctors/{id}              # Update doctor
DELETE /doctors/{id}              # Delete doctor
```

### Schedule Service (8084)

```bash
# Schedule Management
GET    /schedules                 # Get all schedules
GET    /schedules/{id}            # Get schedule by ID
GET    /schedules/doctor/{doctorId}  # Get schedules by doctor
GET    /schedules/available        # Get available slots

# Create/Update Schedule
POST   /schedules                 # Create schedule
PUT    /schedules/{id}            # Update schedule
PATCH  /schedules/{id}/status     # Update status (AVAILABLE, BOOKED, CANCELLED)
DELETE /schedules/{id}            # Delete schedule
```

### Payment Service (8086)

```bash
# Payment Management
GET    /payments                  # Get all payments
GET    /payments/{id}             # Get payment by ID

# Create/Update Payment
POST   /payments                  # Create payment
PUT    /payments/{id}             # Update payment
PATCH  /payments/{id}/status      # Update status (PENDING, COMPLETED, FAILED)
DELETE /payments/{id}             # Delete payment
```

### Medical Record Service (8085)

```bash
# Medical Records (MongoDB)
GET    /medical-records                    # Get all records
GET    /medical-records/{id}              # Get record by ID

# Create/Update Records
POST   /medical-records                    # Create record
PUT    /medical-records/{id}               # Update record
DELETE /medical-records/{id}              # Delete record
```

### Appointment Service (8083) - ORCHESTRATOR

```bash
# Appointment Management
GET    /appointments               # Get all appointments
GET    /appointments/{id}          # Get appointment by ID
GET    /appointments/pending       # Get pending appointments (for nurse review)

# Appointment Workflow
POST   /appointments               # Create appointment request
PATCH  /appointments/{id}/approve # Nurse approves appointment
PATCH  /appointments/{id}/reject   # Nurse rejects (requires reason)
```

---

## Appointment Workflow

### Complete Appointment Lifecycle

```
┌─────────────────────────────────────────────────────────────────────────┐
│                         APPOINTMENT WORKFLOW                             │
└─────────────────────────────────────────────────────────────────────────┘

   PATIENT                    NURSE                     SYSTEM
      │                         │                          │
      │                         │                          │
      ▼                         │                          │
┌─────────────────┐              │                          │
│ 1. REQUEST      │              │                          │
│ POST /appointments│────────────►                          │
│ {patientId,     │              │                          │
│  doctorId,       │              │                          │
│  scheduleId,     │              │                          │
│  complaint}      │              │                          │
└────────┬────────┘              │                          │
         │                       │                          │
         │   Validate patient exists ────────────────────────│
         │   Validate doctor exists ─────────────────────────│
         │   Validate schedule exists ───────────────────────│
         │                       │                          │
         │   Status: REQUESTED ◄─┼──────────────────────────┘
         │                       │                          │
         ▼                       │                          │
┌─────────────────┐              │                          │
│ 2. REVIEW       │              │                          │
│ GET /appointments│◄─────────────┤                          │
│        /pending │              │                          │
└────────┬────────┘              │                          │
         │                       │                          │
         │                       ▼                          │
         │              ┌─────────────────┐                 │
         │              │ 3. APPROVE/REJECT│                 │
         │              │ PATCH /appoint- │                 │
         │              │ ments/{id}/     │                 │
         │              │ approve|reject  │                 │
         │              └────────┬────────┘                 │
         │                       │                          │
         │            ┌──────────┴──────────┐               │
         │            │                     │               │
         │            ▼                     ▼               │
         │   ┌─────────────────┐  ┌─────────────────┐        │
         │   │ 4a. APPROVED    │  │ 4b. REJECTED   │        │
         │   │ Status: APPROVED│  │ Status: REJECTED│       │
         │   └────────┬────────┘  └─────────────────┘        │
         │            │                     │               │
         │            ▼                     │               │
         │   ┌─────────────────┐            │               │
         │   │ 5. PAYMENT      │            │               │
         │   │ Patient pays    │            │               │
         │   │ POST /payments  │            │               │
         │   └────────┬────────┘            │               │
         │            │                     │               │
         │            ▼                     │               │
         │   ┌─────────────────┐            │               │
         │   │ 6. PAID         │            │               │
         │   │ Status: PAID    │            │               │
         │   └────────┬────────┘            │               │
         │            │                     │               │
         │            ▼                     │               │
         │   ┌─────────────────┐            │               │
         │   │ 7. COMPLETE     │            │               │
         │   │ Doctor finishes │            │               │
         │   │ Medical record  │            │               │
         │   │ auto-created    │            │               │
         │   └─────────────────┘            │               │
         │            │                     │               │
         ▼            ▼                     ▼               │
┌─────────────────────────────────────────────────┐
│                  COMPLETED                       │
└─────────────────────────────────────────────────┘
```

### Appointment Status Enum

```java
public enum AppointmentStatus {
    REQUESTED,   // Initial state - waiting for nurse review
    APPROVED,    // Nurse approved - waiting for payment
    REJECTED,    // Nurse rejected - with reason
    PAID,        // Payment completed
    COMPLETED,   // Appointment done, medical record created
    CANCELLED    // Cancelled by patient or system
}
```

### Workflow Implementation in AppointmentService

```java
// 1. Create appointment - validates all references
public ResponseEntity<AppointmentResponseDTO> createAppointment(AppointmentRequestDTO request) {
    // Validate patient exists
    ResponseEntity<UserClient.UserResponse> patient = userClient.getUserById(request.getPatientId());
    
    // Validate doctor exists
    ResponseEntity<DoctorClient.DoctorResponse> doctor = doctorClient.getDoctorById(request.getDoctorId());
    
    // Validate schedule exists
    ResponseEntity<ScheduleClient.ScheduleResponse> schedule = scheduleClient.getScheduleById(request.getScheduleId());
    
    // Save with REQUESTED status
    appointment.setStatus(AppointmentStatus.REQUESTED);
    return appointmentRepository.save(appointment);
}

// 2. Approve - only if currently REQUESTED
public Optional<AppointmentResponseDTO> approveAppointment(Integer id) {
    return appointmentRepository.findById(id)
        .filter(a -> a.getStatus() == AppointmentStatus.REQUESTED)
        .map(appointment -> {
            appointment.setStatus(AppointmentStatus.APPROVED);
            return appointmentRepository.save(appointment);
        });
}

// 3. Reject - only if currently REQUESTED, requires reason
public Optional<AppointmentResponseDTO> rejectAppointment(Integer id, AppointmentRejectDTO request) {
    return appointmentRepository.findById(id)
        .filter(a -> a.getStatus() == AppointmentStatus.REJECTED)
        .map(appointment -> {
            appointment.setStatus(AppointmentStatus.REJECTED);
            appointment.setRejectionReason(request.getReason());
            return appointmentRepository.save(appointment);
        });
}
```

---

## Testing Guide

### Prerequisites

1. All services running (via Docker or manual)
2. Supabase PostgreSQL configured
3. MongoDB Atlas configured
4. Eureka running at http://localhost:8761

### Testing with cURL

#### 1. Register Users

```bash
# Register Admin
curl -X POST http://localhost:8080/users/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Admin User",
    "email": "admin@hospital.com",
    "password": "admin123",
    "phone": "081234567890",
    "role": "ADMIN"
  }'

# Register Doctor (will create user first)
curl -X POST http://localhost:8080/users/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Dr. Sarah",
    "email": "sarah@hospital.com",
    "password": "doctor123",
    "phone": "081234567891",
    "role": "DOCTOR"
  }'

# Register Patient
curl -X POST http://localhost:8080/users/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "name": "John Patient",
    "email": "john@email.com",
    "password": "patient123",
    "phone": "081234567892",
    "role": "PATIENT"
  }'
```

#### 2. Login (Get JWT Token)

```bash
curl -X POST http://localhost:8080/users/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "admin@hospital.com",
    "password": "admin123"
  }'
```

Response:
```json
{
  "token": "eyJhbGciOiJIUzI1NiIs...",
  "user": {
    "id": 1,
    "name": "Admin User",
    "email": "admin@hospital.com",
    "role": "ADMIN"
  }
}
```

#### 3. Create Doctor with User (Admin Feature)

```bash
curl -X POST http://localhost:8080/doctors/admin/create-with-user \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Dr. Ahmad",
    "email": "ahmad@hospital.com",
    "password": "doctor123",
    "phone": "081234567893",
    "specialization": "Cardiology",
    "licenseNumber": "SIP-12345"
  }'
```

#### 4. Create Schedule

```bash
curl -X POST http://localhost:8080/schedules \
  -H "Content-Type: application/json" \
  -d '{
    "doctorId": 1,
    "date": "2024-12-25",
    "startTime": "09:00",
    "endTime": "12:00",
    "room": "Room 101",
    "status": "AVAILABLE"
  }'
```

#### 5. Get Available Schedules

```bash
curl http://localhost:8080/schedules/available
```

#### 6. Create Appointment

```bash
curl -X POST http://localhost:8080/appointments \
  -H "Content-Type: application/json" \
  -d '{
    "patientId": 2,
    "doctorId": 1,
    "scheduleId": 1,
    "complaint": "Chest pain and shortness of breath"
  }'
```

Response:
```json
{
  "id": 1,
  "patientId": 2,
  "patientName": "John Patient",
  "doctorId": 1,
  "doctorName": "Dr. Sarah",
  "scheduleId": 1,
  "appointmentDateTime": "2024-12-25 09:00",
  "complaint": "Chest pain and shortness of breath",
  "status": "REQUESTED",
  "price": 150000.00,
  "createdAt": "2024-12-20T10:30:00"
}
```

#### 7. Nurse Reviews Pending Appointments

```bash
curl http://localhost:8080/appointments/pending
```

#### 8. Nurse Approves Appointment

```bash
curl -X PATCH http://localhost:8080/appointments/1/approve
```

#### 9. Process Payment

```bash
curl -X POST http://localhost:8080/payments \
  -H "Content-Type: application/json" \
  -d '{
    "appointmentId": 1,
    "totalAmount": 150000.00,
    "amountPaid": 200000.00,
    "paymentMethod": "CASH"
  }'
```

Response:
```json
{
  "id": 1,
  "appointmentId": 1,
  "totalAmount": 150000.00,
  "amountPaid": 200000.00,
  "changeAmount": 50000.00,
  "paymentMethod": "CASH",
  "status": "PENDING",
  "createdAt": "2024-12-20T10:35:00"
}
```

#### 10. Complete Payment

```bash
curl -X PATCH http://localhost:8080/payments/1/status \
  -H "Content-Type: application/json" \
  -d '{
    "status": "COMPLETED"
  }'
```

#### 11. Create Medical Record

```bash
curl -X POST http://localhost:8080/records \
  -H "Content-Type: application/json" \
  -d '{
    "appointmentId": "1",
    "doctorId": 1,
    "patientId": 2,
    "complaints": ["Chest pain", "Shortness of breath"],
    "diagnosis": "Mild anxiety with hyperventilation",
    "bloodPressure": "120/80",
    "temperature": 36.5,
    "medications": [
      {"name": "Anti-anxiety", "dose": "1x daily"},
      {"name": "Multivitamin", "dose": "1x daily"}
    ],
    "doctorNotes": "Patient advised to rest and follow up in 1 week"
  }'
```

### Testing with Swagger UI

## Swagger UI Setup

### Centralized Swagger (Recommended)

All services are aggregated into **ONE Swagger UI** at the gateway:

```
http://localhost:8080/swagger-ui.html
```

This provides:
- **Dropdown selector** to switch between services
- **Try it out** feature for all endpoints
- **All services in one place**

### Swagger UI URLs

| Service | Port | Swagger UI URL |
|---------|------|---------------|
| **Gateway (All Services)** | **8080** | **http://localhost:8080/swagger-ui.html** |
| User Service | 8081 | http://localhost:8081/swagger-ui.html |
| Doctor Service | 8082 | http://localhost:8082/swagger-ui.html |
| Appointment Service | 8083 | http://localhost:8083/swagger-ui.html |
| Schedule Service | 8084 | http://localhost:8084/swagger-ui.html |
| Medical Record Service | 8085 | http://localhost:8085/swagger-ui.html |
| Payment Service | 8086 | http://localhost:8086/swagger-ui.html |

### How Gateway Aggregates Swagger

The gateway routes `/api-docs/*` to each service's `/v3/api-docs`:

```
┌─────────────────────────────────────────────────────────────┐
│                    http://localhost:8080                     │
│                   (Gateway Swagger UI)                       │
├─────────────────────────────────────────────────────────────┤
│                                                             │
│  ┌─────────────────────────────────────────────────────┐   │
│  │ [Service Dropdown]                                    │   │
│  │  ├─ user-service                                     │   │
│  │  ├─ doctor-service                                   │   │
│  │  ├─ appointment-service                              │   │
│  │  ├─ schedule-service                                 │   │
│  │  ├─ medical-record-service                           │   │
│  │  └─ payment-service                                  │   │
│  └─────────────────────────────────────────────────────┘   │
│                                                             │
│  ┌─────────────────────────────────────────────────────┐   │
│  │ GET /users                                          │   │
│  │ POST /users                                         │   │
│  │ GET /users/{id}                                    │   │
│  │ ...                                                 │   │
│  └─────────────────────────────────────────────────────┘   │
│                                                             │
└─────────────────────────────────────────────────────────────┘
         │                    │              │
         ▼                    ▼              ▼
┌────────────────┐  ┌────────────────┐  ┌────────────────┐
│  user-service   │  │ doctor-service │  │ appointment-svc│
│   /v3/api-docs  │  │  /v3/api-docs  │  │ /v3/api-docs   │
│     (8081)      │  │    (8082)      │  │    (8083)      │
└────────────────┘  └────────────────┘  └────────────────┘
```

### Swagger API Docs URLs

| Service | API Docs URL |
|---------|-------------|
| Gateway | http://localhost:8080/v3/api-docs |
| User Service | http://localhost:8080/api-docs/user |
| Doctor Service | http://localhost:8080/api-docs/doctor |
| Appointment Service | http://localhost:8080/api-docs/appointment |
| Schedule Service | http://localhost:8080/api-docs/schedule |
| Medical Record Service | http://localhost:8080/api-docs/medical-record |
| Payment Service | http://localhost:8080/api-docs/payment |

### Testing with Swagger

1. Open **http://localhost:8080/swagger-ui.html**
2. Select service from dropdown (e.g., "user-service")
3. Expand an endpoint (e.g., POST /api/auth/register)
4. Click **"Try it out"**
5. Fill in request body
6. Click **"Execute"**
7. View response

### Testing Auth Endpoints

For endpoints requiring JWT token:

1. First, call **POST /api/auth/login** to get token
2. Copy the token from response
3. Click **"Authorize"** button (🔓) at top right
4. Paste token: `Bearer <your-token>`
5. Click **Authorize**
6. Now test authenticated endpoints

```
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

### Testing Service Discovery

Verify Eureka has all services registered:

```bash
curl http://localhost:8761/eureka/code/home
```

Or visit http://localhost:8761 in browser.

---

## Setup & Running

### Option 1: Docker (Recommended)

```bash
# 1. Copy environment file
cp .env.example .env

# 2. Edit .env with your credentials:
#    - SUPABASE_POSTGRES_URL
#    - SUPABASE_POSTGRES_USER
#    - SUPABASE_POSTGRES_PASSWORD
#    - MONGODB_ATLAS_URI
#    - JWT_SECRET

# 3. Start all services
make up

# Or directly with docker compose
docker compose up --build -d
```

### Option 2: Manual (Development)

```bash
# Start Eureka first
cd discovery-service && mvn spring-boot:run

# Then start services (can be parallel)
cd user-service && mvn spring-boot:run
cd doctor-service && mvn spring-boot:run
cd schedule-service && mvn spring-boot:run
cd payment-service && mvn spring-boot:run
cd medical-record-service && mvn spring-boot:run
cd appointment-service && mvn spring-boot:run
cd gateway && mvn spring-boot:run
```

---

## Environment Configuration

### .env File

```env
# Supabase PostgreSQL (Connection Pooling)
SUPABASE_POSTGRES_URL=jdbc:postgresql://aws-1-ap-southeast-1.pooler.supabase.com:5432/postgres
SUPABASE_POSTGRES_USER=postgres.xxxxx
SUPABASE_POSTGRES_PASSWORD=your_password

# MongoDB Atlas
MONGODB_ATLAS_URI=mongodb+srv://username:password@cluster.xxxxx.mongodb.net/medical_record_db?authSource=admin

# JWT (min 32 chars)
JWT_SECRET=your_secure_jwt_secret_at_least_32_characters_long
```

### Service Ports (Quick Reference)

| Service | Port | Base URL | Swagger UI |
|---------|------|----------|------------|
| **Gateway** | **8080** | **http://localhost:8080** | **http://localhost:8080/swagger-ui.html** |
| Discovery (Eureka) | 8761 | http://localhost:8761 | - |
| User Service | 8081 | http://localhost:8081 | http://localhost:8081/swagger-ui.html |
| Doctor Service | 8082 | http://localhost:8082 | http://localhost:8082/swagger-ui.html |
| Appointment Service | 8083 | http://localhost:8083 | http://localhost:8083/swagger-ui.html |
| Schedule Service | 8084 | http://localhost:8084 | http://localhost:8084/swagger-ui.html |
| Medical Record Service | 8085 | http://localhost:8085 | http://localhost:8085/swagger-ui.html |
| Payment Service | 8086 | http://localhost:8086 | http://localhost:8086/swagger-ui.html |

**Recommended:** Use Gateway (port 8080) for all API calls and Swagger UI.

### Health Checks

```bash
# Check all services
for port in 8080 8761 8081 8082 8083 8084 8085 8086; do
  curl -s http://localhost:$port/actuator/health | jq -r .status 2>/dev/null || echo "DOWN"
done
```

---

## DTOs (Data Transfer Objects)

Each service uses DTOs for API layer separation:

### Pattern

```java
// Request DTO - for creating/updating
@Data @NoArgsConstructor @AllArgsConstructor
public class ExampleRequestDTO {
    @NotNull private Integer field;
    private String optionalField;
}

// Response DTO - for responses (with @Builder)
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class ExampleResponseDTO {
    private Integer id;
    private String field;
    private LocalDateTime createdAt;
}

// Service conversion
public ExampleResponseDTO toDTO(ExampleModel model) {
    return ExampleResponseDTO.builder()
        .id(model.getId())
        .field(model.getField())
        .createdAt(model.getCreatedAt())
        .build();
}
```

### DTOs Per Service

| Service | Request DTOs | Response DTOs |
|---------|--------------|---------------|
| user-service | RegisterRequestDTO, UserUpdateDTO | UserResponseDTO |
| doctor-service | DoctorRequestDTO, DoctorWithUserRequestDTO | DoctorResponseDTO, DoctorWithUserResponseDTO |
| schedule-service | ScheduleRequestDTO, AvailableSlotRequestDTO | ScheduleResponseDTO |
| payment-service | PaymentRequestDTO, PaymentStatusUpdateRequestDTO | PaymentResponseDTO |
| medical-record-service | MedicalRecordRequestDTO, MedicationDTO | MedicalRecordResponseDTO |
| appointment-service | AppointmentRequestDTO, AppointmentRejectDTO | AppointmentResponseDTO |

---

## Troubleshooting

### Eureka Not Registering Services

1. Check if discovery-service is running
2. Verify network connectivity between containers
3. Check logs: `docker compose logs [service-name]`

### Feign Client Errors

1. Ensure service name matches exactly (case-sensitive)
2. Verify Eureka has the service registered
3. Check circuit breaker not open

### Database Connection Issues

1. Verify Supabase/MongoDB credentials
2. Check IP whitelist (0.0.0.0/0 for development)
3. Enable IPv6 in Docker Desktop settings

### JWT Authentication Errors

1. Ensure JWT_SECRET is set (min 32 characters)
2. Token expires - re-login
3. Check token format in Authorization header

---

## License

Educational/Demonstration Project
