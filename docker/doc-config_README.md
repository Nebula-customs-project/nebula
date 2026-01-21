# Nebula Platform - Docker Configuration

This directory contains the complete Docker containerization setup for the Nebula Platform, enabling you to run the entire application stack with a single command.

---

## 📁 Directory Structure

```
docker/
├── .env                           # Environment variables (actual values - gitignored)
├── .env.example                   # Template for environment variables
├── docker-compose.yml             # Complete stack (infrastructure + services + frontend)
├── docker-compose.services.yml    # Services-only overlay (see note below)
├── init-scripts/                  # PostgreSQL initialization scripts
│   ├── 01-init-schemas.sql        # Creates database schemas
│   ├── 02-vehicle-service-schema.sql
│   ├── 03-user-service-schema.sql
│   ├── 04-merchandise-service-schema.sql
│   ├── 05-world-view-service-schema.sql
│   └── 06-grant-access.sql        # Grants permissions to app user
└── rabbitmq/                      # RabbitMQ configuration
    ├── enabled_plugins            # Enables MQTT and management plugins
    └── rabbitmq.conf              # MQTT and WebSocket configuration
```

---

## 🚀 Quick Start

```bash
# 1. Navigate to docker directory
cd docker

# 2. Copy and configure environment variables
cp .env.example .env
# Edit .env with your secure passwords

# 3. Start the entire stack
docker compose up -d --build
```

**Access Points:**
| Service | URL |
|---------|-----|
| Frontend | http://localhost:3000 |
| API Gateway | http://localhost:8080 |
| RabbitMQ Management | http://localhost:15672 |

---

## 📦 Services Overview

### Infrastructure Services

| Service | Image | Port | Purpose |
|---------|-------|------|---------|
| **PostgreSQL** | `postgres:15-alpine` | 5434 | Shared database for all services |
| **RabbitMQ** | `rabbitmq:4.0-management-alpine` | 5672, 1883, 15672, 15675 | Message broker with MQTT support |

### Application Services

| Service | Port | Purpose |
|---------|------|---------|
| **platform-core** | 8761 (internal) | Eureka server & Config server (legacy, disabled in Docker) |
| **gateway-service** | 8080 | API Gateway - single entry point for all API requests |
| **user-service** | 8083 (internal) | User authentication and management |
| **vehicle-service** | 8081 (internal) | Vehicle catalog and configurations |
| **world-view** | 8082 (internal) | Routes, journeys, and real-time MQTT updates |
| **merchandise-service** | 8084 (internal) | Merchandise catalog and cart |
| **frontend** | 3000 | Next.js web application |

---

## 🏗️ How Images Are Built

Each service uses a multi-stage Dockerfile located in its respective directory. The build process:

```yaml
build:
  context: ..              # Points to project root
  dockerfile: services/<service-name>/Dockerfile
```

**Build stages:**
1. **Maven Build** - Compiles Java code and creates JAR
2. **Runtime** - Alpine JRE with only the final JAR

**Example (gateway-service):**
```dockerfile
# Stage 1: Build
FROM maven:3-eclipse-temurin-21-alpine AS build
COPY services/gateway-service/pom.xml .
RUN mvn dependency:go-offline
COPY services/gateway-service/src ./src
RUN mvn package -DskipTests

# Stage 2: Run
FROM eclipse-temurin:21-jre-alpine
COPY --from=build /app/target/*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
```

**Frontend build:**
```yaml
build:
  context: ../frontend
  dockerfile: Dockerfile
  args:
    NEXT_PUBLIC_GATEWAY_URL: http://localhost:8080
    NEXT_PUBLIC_MQTT_URL: ws://localhost:15675/ws
```

---

## ⚙️ Spring Profile Configuration (application-docker.yaml)

Each service has an `application-docker.yaml` file in `src/main/resources/` that provides Docker-specific configuration.

**Purpose:**
- **Disable Eureka** - In Docker, services communicate via container DNS names (e.g., `http://vehicle-service:8081`) instead of Eureka service discovery
- **Disable Config Server** - Configuration is injected via environment variables
- **Direct routing** - Gateway uses hardcoded service URLs instead of `lb://` load-balanced routes

**Example from gateway-service:**
```yaml
spring:
  cloud:
    gateway:
      routes:
        - id: vehicle-service
          uri: http://vehicle-service:8081  # Direct URL, not lb://vehicle-service
          predicates:
            - Path=/api/v1/vehicles/**

eureka:
  client:
    enabled: false
```

**Activated via:**
```yaml
environment:
  SPRING_PROFILES_ACTIVE: docker
```

---

## 🗄️ Database Initialization Scripts

Located in `init-scripts/`, these SQL scripts run **automatically** on first PostgreSQL startup.

| Script | Purpose |
|--------|---------|
| `01-init-schemas.sql` | Creates schemas: `world_view`, `vehicle_service`, `user_service`, `merchandise_service` |
| `02-05-*-schema.sql` | Creates tables for each service |
| `06-grant-access.sql` | Grants permissions to the application user |

**Why schemas?** Each microservice gets its own PostgreSQL schema for data isolation while sharing a single database instance.

---

## 🐰 RabbitMQ Configuration

### Enabled Plugins (`enabled_plugins`)
```
[rabbitmq_management, rabbitmq_mqtt, rabbitmq_web_mqtt]
```
- **rabbitmq_management** - Web UI at port 15672
- **rabbitmq_mqtt** - MQTT protocol support (port 1883)
- **rabbitmq_web_mqtt** - WebSocket MQTT for browser clients (port 15675)

### Configuration (`rabbitmq.conf`)
- MQTT on port 1883 for backend services
- WebSocket MQTT on port 15675 at path `/ws` for frontend
- Authentication required (`allow_anonymous = false`)

---

## 📝 Environment Variables

Copy `.env.example` to `.env` and configure:

| Variable | Description |
|----------|-------------|
| `POSTGRES_DB` | Database name |
| `POSTGRES_USER` | Database user |
| `POSTGRES_PASSWORD` | Database password |
| `RABBITMQ_USER` | RabbitMQ username |
| `RABBITMQ_PASSWORD` | RabbitMQ password |
| `ADMIN_DEFAULT_PASSWORD` | Default admin user password |

---

## ❓ docker-compose.services.yml - Is It Redundant?

### Short Answer: **Yes, it's effectively redundant/obsolete.**

### Detailed Analysis:

| File | Purpose | Contains |
|------|---------|----------|
| `docker-compose.yml` | **Complete stack** | Infrastructure + All Services + Frontend |
| `docker-compose.services.yml` | **Services overlay** | Only application services (no infra, no frontend) |

### The Intended Use Case

`docker-compose.services.yml` was designed as a **compose override file** to be used with the base file:

```bash
docker compose -f docker-compose.yml -f docker-compose.services.yml up -d
```

However, looking at both files, there's **significant duplication**:

| Aspect | docker-compose.yml | docker-compose.services.yml |
|--------|--------------------|-----------------------------|
| platform-core | ✅ Defined | ✅ Duplicated |
| gateway-service | ✅ Defined | ✅ Duplicated |
| user-service | ✅ Defined | ✅ Duplicated |
| vehicle-service | ✅ Defined | ✅ Duplicated |
| world-view | ✅ Defined | ✅ Duplicated |
| merchandise-service | ✅ Defined | ✅ Duplicated |
| PostgreSQL | ✅ Defined | ❌ Missing |
| RabbitMQ | ✅ Defined | ❌ Missing |
| Frontend | ✅ Defined | ❌ Missing |

### Why Keep It (Arguments For)

1. **Separation of Concerns** - Infrastructure vs. application services
2. **Selective Rebuilds** - Rebuild only services without touching infra
3. **CI/CD Pipelines** - Different compose files for different stages

### Why Remove It (Arguments Against)

1. **Maintenance Burden** - Changes must be made in two places
2. **Drift Risk** - The two files can become out of sync
3. **Not Standalone** - It can't run without the base file's networks and depends_on references
4. **Single Command is Better** - `docker compose up -d --build` does everything

### Recommendation

> [!TIP]
> **Delete `docker-compose.services.yml`** unless you have a specific use case for compose overrides.

The main `docker-compose.yml` is self-contained and sufficient for:
- Local development
- Testing
- Simple production deployments

If you need selective rebuilds, use:
```bash
docker compose up -d --build gateway-service vehicle-service
```

---

## 🔧 Common Commands

```bash
# Start everything
docker compose up -d --build

# View logs
docker compose logs -f

# View specific service logs
docker compose logs -f gateway-service

# Rebuild single service
docker compose up -d --build vehicle-service

# Stop everything
docker compose down

# Stop and remove volumes (clean slate)
docker compose down -v

# Check service health
docker compose ps
```

---

## 🌐 Network Architecture

```
                    ┌─────────────────────────────────────────────────────────┐
                    │                   nebula-network                        │
                    │                   (Docker Bridge)                       │
                    │                                                         │
    ┌───────────────┼───────────────┐                                         │
    │ External      │               │                                         │
    │ Ports         │               ▼                                         │
    │               │    ┌──────────────────┐                                 │
    │  :3000 ───────┼───►│    frontend      │                                 │
    │               │    └────────┬─────────┘                                 │
    │               │             │                                           │
    │               │             ▼                                           │
    │  :8080 ───────┼───►┌──────────────────┐                                 │
    │               │    │  gateway-service │                                 │
    │               │    └────────┬─────────┘                                 │
    │               │             │                                           │
    │               │    ┌────────┴────────┬──────────────┬──────────────┐    │
    │               │    ▼                 ▼              ▼              ▼    │
    │               │ ┌─────────┐    ┌───────────┐  ┌──────────┐  ┌──────────┐│
    │               │ │vehicle- │    │world-view │  │user-     │  │merch-    ││
    │               │ │service  │    │           │  │service   │  │service   ││
    │               │ └────┬────┘    └─────┬─────┘  └────┬─────┘  └────┬─────┘│
    │               │      │               │             │              │     │
    │               │      │     ┌─────────┴─────────────┴──────────────┘     │
    │               │      │     │         │                                  │
    │               │      ▼     ▼         ▼                                  │
    │  :5434 ───────┼───►┌──────────────────┐  ┌──────────────────┐           │
    │  :15672 ──────┼───►│    PostgreSQL    │  │    RabbitMQ      │◄──:1883   │
    │  :15675 ──────┼───►│                  │  │                  │◄──:5672   │
    │               │    └──────────────────┘  └──────────────────┘           │
    └───────────────┴─────────────────────────────────────────────────────────┘
```

**Key Points:**
- All services communicate internally via container names
- Only gateway (:8080), frontend (:3000), and infrastructure ports are exposed
- Application services use `expose` (internal only) not `ports` (external)
