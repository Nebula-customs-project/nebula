# World View Service

A Spring Boot microservice that simulates autonomous vehicle journeys to the **Dealership**. This service implements Hexagonal Architecture (Ports and Adapters pattern) for maximum flexibility and testability.

## Features

- **Automated Journey Management**: Journeys start automatically on random routes without user intervention
- **8 Predefined Routes**: Real GPS coordinates from various Stuttgart-area locations to the dealership
- **Real-time MQTT Streaming**: Live coordinate updates published to RabbitMQ/MQTT for frontend consumption
- **Journey Lifecycle Tracking**: Complete journey state management with progress monitoring
- **RESTful API**: Comprehensive endpoints for journey and route management
- **Hexagonal Architecture**: Clean separation of concerns for easy testing and extensibility

## Architecture

This service follows **Hexagonal Architecture** (Ports and Adapters), ensuring business logic independence from infrastructure concerns.

```
┌─────────────────────────────────────────────────────────────────────────┐
│                        INFRASTRUCTURE LAYER                              │
│  ┌───────────────────┐  ┌──────────────────┐  ┌────────────────────┐   │
│  │  Web Adapter      │  │ MQTT Publisher   │  │  Persistence       │   │
│  │  (Controllers)    │  │ (RabbitMQ)       │  │  (JPA/PostgreSQL)  │   │
│  └──────────┬────────┘  └────────┬─────────┘  └─────────┬──────────┘   │
└─────────────┼──────────────────────┼─────────────────────┼──────────────┘
              │                      │                     │
┌─────────────┼──────────────────────┼─────────────────────┼──────────────┐
│             ▼                      ▼                     ▼              │
│  ┌──────────────────────────────────────────────────────────────────┐  │
│  │                    APPLICATION LAYER                              │  │
│  │  ┌─────────────┐  ┌─────────────────┐  ┌───────────────────────┐ │  │
│  │  │RouteService │  │ JourneyService  │  │AutoJourneyScheduler   │ │  │
│  │  └─────────────┘  └─────────────────┘  └───────────────────────┘ │  │
│  │  ┌──────────────────────────────────────────────────────────────┐ │  │
│  │  │           JourneySchedulerService                            │ │  │
│  │  │  (Scheduled updates for active journeys)                     │ │  │
│  │  └──────────────────────────────────────────────────────────────┘ │  │
│  └──────────────────────────────────────────────────────────────────┘  │
│                                  │                                      │
│  ┌───────────────────────────────┼──────────────────────────────────┐  │
│  │                        DOMAIN LAYER                               │  │
│  │  ┌──────────────┐  ┌──────────────┐  ┌──────────────────────┐   │  │
│  │  │  Coordinate  │  │ DrivingRoute │  │   JourneyState       │   │  │
│  │  └──────────────┘  └──────────────┘  └──────────────────────┘   │  │
│  │                                                                   │  │
│  │  ┌───────────────────────┐  ┌──────────────────────────────────┐ │  │
│  │  │ Inbound Ports         │  │ Outbound Ports                   │ │  │
│  │  │ - RouteUseCase        │  │ - RouteRepository                │ │  │
│  │  │ - JourneyUseCase      │  │ - JourneyStateRepository         │ │  │
│  │  │                       │  │ - CoordinatePublisher (MQTT)     │ │  │
│  │  └───────────────────────┘  └──────────────────────────────────┘ │  │
│  └───────────────────────────────────────────────────────────────────┘  │
└─────────────────────────────────────────────────────────────────────────┘
```

### How It Works

1. **AutoJourneySchedulerService** automatically starts new journeys on random routes
2. **JourneySchedulerService** advances active journeys at configured intervals (default: 500ms)
3. Coordinate updates are published to **MQTT (RabbitMQ)** in real-time
4. Frontend subscribes to MQTT topic: `nebula/journey/{journeyId}/position`
5. When a journey completes, a new one starts automatically after a configurable delay

## 🗺️ Routes to Dealership

All routes end at: **Dealership** (Porschestraße 1, 70435 Stuttgart)  
Coordinates: `48.8354, 9.1520`

| Route ID | Start Location | Coordinates | Waypoints |
|----------|---------------|-------------|-----------|
| route-1 | Ludwigsburg Schloss | 48.8977, 9.1952 | 45 |
| route-2 | Favoritepark (Ludwigsburg) | 48.9111, 9.1866 | 38 |
| route-3 | Reutlingen | 48.4919, 9.2041 | 52 |
| route-4 | Böblingen | 48.6855, 9.0143 | 48 |
| route-5 | Sindelfingen | 48.7135, 9.0001 | 42 |
| route-6 | Waiblingen | 48.8297, 9.3178 | 40 |
| route-7 | Fellbach | 48.8108, 9.2785 | 35 |
| route-8 | Esslingen | 48.7408, 9.3050 | 46 |

**Note:** Routes are automatically selected at random when a new journey starts.

## API Endpoints

### Routes

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/v1/routes` | Get all available routes |
| GET | `/api/v1/routes/{routeId}` | Get a specific route by ID |
| GET | `/api/v1/routes/random` | Get a randomly selected route |
| GET | `/api/v1/routes/count` | Get total number of routes |

### Journeys

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/v1/journeys` | Start a new journey with specific route and speed |
| POST | `/api/v1/journeys/quick-start` | Quick start with auto-generated ID, random route, and default speed |
| GET | `/api/v1/journeys/{journeyId}` | Get current journey state and progress |
| DELETE | `/api/v1/journeys/{journeyId}` | Stop an active journey |

**Real-time Updates:** Subscribe to MQTT topic `nebula/journey/{journeyId}/position` for live coordinate streaming.

## Usage Examples

### Start a Journey with Random Route (Quick Start)

```bash
curl -X POST http://localhost:8082/api/v1/journeys/quick-start \
  -H "Content-Type: application/json"
```

**Response:**
```json
{
  "journey_id": "journey-a1b2c3d4",
  "route": {
    "id": "route-3",
    "name": "Reutlingen to Dealership",
    "start_location": "Reutlingen",
    "end_location": "Dealership"
  },
  "status": "IN_PROGRESS",
  "speed_meters_per_second": 15.0,
  "progress_percentage": 0.0
}
```

### Start a Journey on Specific Route

```bash
curl -X POST http://localhost:8082/api/v1/journeys \
  -H "Content-Type: application/json" \
  -d '{
    "journey_id": "my-journey-001",
    "route_id": "route-1",
    "speed_meters_per_second": 20.0
  }'
```

### Get Journey State

```bash
curl http://localhost:8082/api/v1/journeys/my-journey-001
```

**Response:**
```json
{
  "journey_id": "my-journey-001",
  "route": {
    "id": "route-1",
    "name": "Ludwigsburg Schloss to Dealership"
  },
  "status": "IN_PROGRESS",
  "current_position": {
    "latitude": 48.8756,
    "longitude": 9.1845
  },
  "progress_percentage": 45.2,
  "current_waypoint_index": 20,
  "total_waypoints": 45
}
```

### Subscribe to Real-time Coordinate Updates (MQTT)

The service publishes coordinate updates to RabbitMQ/MQTT. Frontend should subscribe to:

**Topic:** `nebula/journey/{journeyId}/position`

**Message Format:**
```json
{
  "type": "coordinate-update",
  "journeyId": "journey-a1b2c3d4",
  "coordinate": {
    "latitude": 48.8756,
    "longitude": 9.1845
  },
  "waypointIndex": 20,
  "totalWaypoints": 45,
  "progressPercentage": 45.2,
  "status": "IN_PROGRESS",
  "timestamp": "2026-01-09T10:30:45.123Z"
}
```

**Completion Message:**
```json
{
  "type": "journey-completed",
  "journeyId": "journey-a1b2c3d4",
  "route": {
    "id": "route-1",
    "name": "Ludwigsburg Schloss to Dealership"
  },
  "timestamp": "2026-01-09T10:35:22.456Z"
}
```

### Get All Routes

```bash
curl http://localhost:8082/api/v1/routes
```

### Stop a Journey

```bash
curl -X DELETE http://localhost:8082/api/v1/journeys/my-journey-001
```

## ⚙️ Configuration

The service runs on port `8082` by default. Key configuration options:

### Environment Variables

```properties
# Server Configuration
SERVER_PORT=8082

# Database Configuration
DB_HOST=localhost
DB_PORT=5432
DB_NAME=nebula
DB_USERNAME=your_username
DB_PASSWORD=your_password

# MQTT/RabbitMQ Configuration
MQTT_ENABLED=true
MQTT_HOST=localhost
MQTT_PORT=1883
MQTT_CLIENT_ID=world-view-service
MQTT_USERNAME=guest
MQTT_PASSWORD=guest
MQTT_TOPIC_PREFIX=nebula/journey

# Journey Scheduler Configuration
JOURNEY_UPDATE_INTERVAL=500              # Update interval in ms (default: 500ms = 2 updates/sec)
JOURNEY_DEFAULT_SPEED=13.89             # Default speed in m/s (≈ 50 km/h)
JOURNEY_DELAY_BETWEEN=5000              # Delay between journeys in ms (default: 5 seconds)
```

### Application Profiles

- **dev**: Development profile with detailed logging
- **prod**: Production profile with optimized settings

Run with a specific profile:
```bash
java -jar world-view.jar --spring.profiles.active=dev
```

### Key Configuration Files

- `application.yaml` - Base configuration
- `application-dev.yaml` - Development-specific settings (local credentials)
- `application-prod.yaml` - Production settings

## 🏗️ Building and Running

### Prerequisites

- Java 17 or higher
- Maven 3.8+
- PostgreSQL 14+
- RabbitMQ 3.x (for MQTT broker)

### Build

```bash
# Build without tests
./mvnw clean package -DskipTests

# Build with tests
./mvnw clean package
```

### Run

```bash
# Run the JAR
java -jar target/world-view-0.0.1-SNAPSHOT.jar

# Or with Maven
./mvnw spring-boot:run

# With development profile
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
```

### Docker Compose (Full Stack)

Run the entire Nebula platform including database and RabbitMQ:

```bash
cd ../../docker
docker-compose up -d
```

## 🧪 Testing

```bash
# Run all tests
./mvnw test

# Run specific test class
./mvnw test -Dtest=JourneyServiceTest

# Run with coverage
./mvnw clean verify

# SonarQube analysis
./mvnw clean verify sonar:sonar
```

## 🔧 Tech Stack of world-view service

- **Spring Boot 3.x** - Application framework
- **Spring Data JPA** - Database abstraction
- **PostgreSQL** - Persistent storage
- **RabbitMQ** - MQTT message broker for real-time streaming
- **Lombok** - Boilerplate reduction
- **SpringDoc OpenAPI** - API documentation (Swagger)
- **JUnit 5 & Mockito** - Testing framework
- **Maven** - Build tool

## 🎯 Design Patterns used

- **Hexagonal Architecture** (Ports and Adapters)
- **Domain-Driven Design** (DDD)
- **Repository Pattern**
- **Service Layer Pattern**
- **Dependency Injection**

## 📝 License

Part of the Nebula platform.

---

**Last Updated:** January 9, 2026  
**Version:** 1.0.0  
**Service Port:** 8082