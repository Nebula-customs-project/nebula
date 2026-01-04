# World View Service -- Need to be updated.

A Spring Boot microservice that provides driving route simulation to **Dealership** dealership. This service follows Hexagonal Architecture (Ports and Adapters pattern).

## Features

- **8 Predefined Routes**: Real coordinates from various Stuttgart-area locations to Dealership
- **Random Route Selection**: Each UI reload gets a randomly selected route
- **Real-time Coordinate Streaming**: SSE (Server-Sent Events) for smooth car animation on the frontend
- **Journey Management**: Start, pause, resume, and stop journeys

## Architecture

```
┌─────────────────────────────────────────────────────────────────┐
│                        INFRASTRUCTURE                            │
│  ┌─────────────────┐  ┌─────────────────┐  ┌─────────────────┐ │
│  │  Web Adapter    │  │  SSE Publisher  │  │  Persistence    │ │
│  │  (Controllers)  │  │  (Messaging)    │  │  (In-Memory)    │ │
│  └────────┬────────┘  └────────┬────────┘  └────────┬────────┘ │
└───────────┼─────────────────────┼─────────────────────┼─────────┘
            │                     │                     │
┌───────────┼─────────────────────┼─────────────────────┼─────────┐
│           ▼                     ▼                     ▼         │
│  ┌─────────────────────────────────────────────────────────┐   │
│  │                    APPLICATION LAYER                      │   │
│  │  ┌─────────────┐  ┌─────────────────┐  ┌──────────────┐ │   │
│  │  │RouteService │  │ JourneyService  │  │ Scheduler    │ │   │
│  │  └─────────────┘  └─────────────────┘  └──────────────┘ │   │
│  └─────────────────────────────────────────────────────────┘   │
│                              │                                   │
│  ┌───────────────────────────┼───────────────────────────────┐ │
│  │                    DOMAIN LAYER                            │ │
│  │  ┌────────────┐  ┌──────────────┐  ┌────────────────────┐ │ │
│  │  │ Coordinate │  │ DrivingRoute │  │   JourneyState     │ │ │
│  │  └────────────┘  └──────────────┘  └────────────────────┘ │ │
│  │                                                            │ │
│  │  ┌─────────────────────┐  ┌────────────────────────────┐  │ │
│  │  │ Inbound Ports       │  │ Outbound Ports             │  │ │
│  │  │ - RouteUseCase      │  │ - RouteRepository          │  │ │
│  │  │ - JourneyUseCase    │  │ - JourneyStateRepository   │  │ │
│  │  │                     │  │ - CoordinatePublisher      │  │ │
│  │  └─────────────────────┘  └────────────────────────────┘  │ │
│  └───────────────────────────────────────────────────────────┘ │
└─────────────────────────────────────────────────────────────────┘
```

## Routes to Dealership

All routes end at: **Dealership** (Porschestraße 1, 70435 Stuttgart)
Coordinates: `48.8354, 9.1520`

| Route | Start Location | Distance | Duration |
|-------|---------------|----------|----------|
| 1 | Ludwigsburg Schloss | ~15 km | ~20 min |
| 2 | Favoritepark (Ludwigsburg) | ~12 km | ~18 min |
| 3 | Reutlingen | ~20 km | ~25 min |
| 4 | Böblingen | ~22 km | ~28 min |
| 5 | Sindelfingen | ~18 km | ~24 min |
| 6 | Waiblingen | ~16 km | ~22 min |
| 7 | Fellbach | ~10 km | ~15 min |
| 8 | Kornwestheim | ~5 km | ~10 min |

## API Endpoints

### Routes

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/v1/routes` | Get all available routes |
| GET | `/api/v1/routes/{routeId}` | Get a specific route |
| GET | `/api/v1/routes/random` | Get a random route |
| GET | `/api/v1/routes/count` | Get total route count |

### Journeys

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/v1/journeys` | Start a new journey |
| POST | `/api/v1/journeys/quick-start` | Quick start with random route |
| GET | `/api/v1/journeys/{journeyId}` | Get journey state |
| POST | `/api/v1/journeys/{journeyId}/pause` | Pause a journey |
| POST | `/api/v1/journeys/{journeyId}/resume` | Resume a journey |
| DELETE | `/api/v1/journeys/{journeyId}` | Stop a journey |
| GET | `/api/v1/journeys/{journeyId}/stream` | SSE stream of coordinates |

## Usage Examples

### Start a Journey with Random Route

```bash
curl -X POST http://localhost:8082/api/v1/journeys/quick-start \
  -H "Content-Type: application/json"
```

### Start a Journey on Specific Route

```bash
curl -X POST http://localhost:8082/api/v1/journeys \
  -H "Content-Type: application/json" \
  -d '{
    "journey_id": "my-journey-1",
    "route_id": "route-1",
    "speed_meters_per_second": 15.0
  }'
```

### Subscribe to Coordinate Updates (SSE)

```javascript
const eventSource = new EventSource('http://localhost:8082/api/v1/journeys/my-journey-1/stream');

eventSource.addEventListener('coordinate-update', (event) => {
  const data = JSON.parse(event.data);
  console.log('New position:', data.coordinate);
  // Update car position on map
});

eventSource.addEventListener('journey-completed', (event) => {
  console.log('Journey completed!');
  eventSource.close();
});
```

### Get All Routes

```bash
curl http://localhost:8082/api/v1/routes
```

## Configuration

The service runs on port `8082` by default. Configure via environment variables:

```properties
SERVER_PORT=8082
```

## Building and Running

```bash
# Build
./mvnw clean package -DskipTests

# Run
java -jar target/world-view.jar

# Or with Maven
./mvnw spring-boot:run
```