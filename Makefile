# Hospital Management Microservices Stack - Docker Environment
# Server Orchestration Pattern with Appointment Service as Orchestrator

.PHONY: build up down restart logs clean ps status rebuild prune help

# Default target
help:
	@echo "Hospital Management Microservices Stack"
	@echo "Server Orchestration Pattern - Appointment Service as Orchestrator"
	@echo ""
	@echo "Available targets:"
	@echo "  build    - Build all Docker images"
	@echo "  up       - Start all containers"
	@echo "  down     - Stop and remove all containers"
	@echo "  restart  - Restart all containers"
	@echo "  logs     - Tail logs from all containers"
	@echo "  ps       - Show running containers"
	@echo "  status   - Show health status of all services"
	@echo "  rebuild  - Rebuild images without cache and restart"
	@echo "  clean    - Remove containers, volumes, and images"
	@echo "  prune    - Clean up dangling images and unused volumes"

# Build all Docker images
build:
	@echo "Building all Docker images..."
	docker compose build

# Start all containers
up: build
	@echo "Starting all services..."
	docker compose up -d
	@echo ""
	@echo "Services starting up. Check status with: make status"
	@echo ""
	@echo "Access points:"
	@echo "  Gateway:          http://localhost:8080"
	@echo "  Discovery:        http://localhost:8761"
	@echo "  User Service:     http://localhost:8081"
	@echo "  Doctor Service:   http://localhost:8082"
	@echo "  Appointment:      http://localhost:8083 (Orchestrator)"
	@echo "  Schedule:         http://localhost:8084"
	@echo "  Medical Record:   http://localhost:8085"
	@echo "  Payment Service:  http://localhost:8086"
	@echo "  PostgreSQL:       localhost:5432"
	@echo "  MongoDB:          localhost:27017"
	@echo "  pgAdmin:          http://localhost:8087"
	@echo "  MongoDB Express:  http://localhost:8088"

# Stop and remove all containers (keeps volumes)
down:
	@echo "Stopping all containers..."
	docker compose down

# Restart all containers
restart: down up

# Tail logs from all containers
logs:
	docker compose logs -f

# Show running containers
ps:
	docker compose ps

# Show health status of all services
status:
	@echo "=== Container Status ==="
	@docker compose ps
	@echo ""
	@echo "=== Health Checks ==="
	@docker compose ps --format "table {{.Name}}\t{{.Status}}"

# Rebuild images without cache and restart
rebuild:
	@echo "Rebuilding all images (no cache)..."
	docker compose build --no-cache
	@echo "Restarting services..."
	docker compose up -d --force-recreate

# Remove containers, volumes, and images
clean: down
	@echo "Removing volumes and images..."
	docker compose down -v --rmi local
	@echo "Clean complete."

# Clean up dangling images and unused volumes
prune:
	@echo "Pruning dangling images and unused volumes..."
	docker image prune -f
	docker volume prune -f
	@echo "Prune complete."
