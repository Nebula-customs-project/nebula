# Platform Core Service

Discovery Server (Eureka) + Config Server for the Nebula platform.

## Features

- **Eureka Server**: Service registry for microservice discovery
- **Config Server**: Centralized configuration management
- **Spring MVC**: Traditional web framework (not reactive)

## Architecture

```
src/main/java/pse/nebula/platformcore/
├── PlatformCoreApplication.java    # Application entry point (@EnableEurekaServer, @EnableConfigServer)
└── controller/
    └── HealthController.java       # Health check endpoint
```

## Configuration

The service runs on port **8081** by default.

### Environment Variables

| Variable | Default | Description |
|----------|---------|-------------|
| `SERVER_PORT` | 8081 | Server port |

## Running

```bash
# From project root
./mvnw spring-boot:run -pl services/platform-core
```

## API Endpoints

### Eureka Dashboard
- `GET /` - Eureka dashboard

### Config Server
- `GET /{application}/{profile}` - Get configuration for an application

### Health
- `GET /api/v1/health` - Service health check
- `GET /actuator/health` - Actuator health endpoint

## Integration with Gateway Service

The `gateway-service` connects to this service for:
1. **Service Discovery**: Registers itself and discovers other services via Eureka
2. **Configuration**: Fetches its configuration from Config Server
