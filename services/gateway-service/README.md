# Gateway Service

API Gateway built with Spring Cloud Gateway for the Nebula platform.

## Features

- **Spring Cloud Gateway**: Reactive API gateway
- **Service Discovery**: Eureka client for dynamic routing
- **Config Client**: Fetches configuration from Config Server
- **Load Balancing**: Built-in load balancing via Eureka

## Architecture

```
src/main/java/pse/nebula/gateway/
├── GatewayServiceApplication.java   # Application entry point
├── controller/
│   └── HealthController.java        # Health check endpoint
├── exception/
│   └── GlobalExceptionHandler.java  # Exception handling
└── config/                          # Configuration classes
```

## Configuration

The gateway runs on port **8080** by default and connects to:
- **Eureka Server**: `http://localhost:8081/eureka/`
- **Config Server**: `http://localhost:8081`

### Environment Variables

| Variable | Default   | Description |
|----------|-----------|-------------|
| `SERVER_PORT` | 8080      | Gateway port |
| `EUREKA_HOST` | localhost | Eureka server host |
| `EUREKA_PORT` | 8761      | Eureka server port |
| `CONFIG_SERVER_HOST` | localhost | Config server host |
| `CONFIG_SERVER_PORT` | 8761      | Config server port |

## Routes

Routes are configured in `application.yaml`:

| Route ID | Path | Target Service |
|----------|------|----------------|
| world-view-service | /api/world-view/** | world-view |

## Running

```bash
# From project root
./mvnw spring-boot:run -pl services/gateway-service

# Or with custom port
SERVER_PORT=9000 ./mvnw spring-boot:run -pl services/gateway-service
```

## API Endpoints

- `GET /api/v1/health` - Gateway health check
- `GET /actuator/health` - Actuator health endpoint
- `GET /actuator/gateway/routes` - List all routes

