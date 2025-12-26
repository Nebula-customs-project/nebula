# Application Layer - Telemetry Simulator

## Overview

The Application Layer serves as the **orchestration layer** in our Hexagonal Architecture. It coordinates between the domain logic and infrastructure adapters, managing the lifecycle of vehicle simulations.

## Architecture

```
application/
├── command/
│   └── StartSimulationCommand.java    # Command DTOs
├── exception/
│   ├── RouteNotFoundException.java    # Business exceptions
│   └── SimulationNotFoundException.java
└── service/
    └── SimulationRunnerService.java   # Main orchestration service
```

## Components

### 1. SimulationRunnerService

The core service that drives the simulation engine. It:

- **Manages Simulation Lifecycle**: Starts, stops, and monitors simulations
- **Scheduled Execution**: Runs every 7 seconds to update all active simulations
- **Orchestrates Ports**: Coordinates between repository, route service, and telemetry publisher
- **Exception Handling**: Provides robust error handling for all operations

#### Key Methods

```java
// Start a new simulation
public void startSimulation(StartSimulationCommand command)

// Stop a running simulation
public void stopSimulation(String vehicleId)

// Get current simulation status
public Simulation getSimulation(String vehicleId)

// Scheduled task - runs every 7 seconds
@Scheduled(fixedRate = 7000)
public void updateActiveSimulations()
```

#### Simulation Update Loop

Every 7 seconds, the service:

1. **Iterates** through all active simulations
2. **Moves** the vehicle using `simulation.move(7.0)` - domain logic
3. **Publishes** telemetry data to MQTT broker
4. **Persists** the updated state to database
5. **Handles** completion or errors gracefully

### 2. Command Objects

#### StartSimulationCommand

Encapsulates all parameters needed to start a simulation:

```java
public record StartSimulationCommand(
    String vehicleId,        // Unique vehicle identifier
    GeoPoint startLocation,  // Starting coordinates
    GeoPoint endLocation,    // Destination coordinates
    double speedMps          // Speed in meters per second
)
```

**Validation**: Built-in validation ensures all required fields are present and valid.

### 3. Exceptions

#### RouteNotFoundException
- Thrown when route service cannot find a path between two points
- Wraps underlying errors with context

#### SimulationNotFoundException
- Thrown when attempting to access a non-existent simulation
- Provides clear error messages with vehicle ID

## Design Principles Applied

### 1. **DRY (Don't Repeat Yourself)**

- Extracted `processSimulation()` to handle individual simulation updates
- Reusable helper methods: `fetchRoute()`, `publishTelemetry()`, `handleCompletedSimulation()`
- Single responsibility for each method

### 2. **Exception Handling**

- Try-catch blocks at appropriate levels
- Graceful degradation: Publishing failures don't stop simulations
- Contextual error messages with vehicle IDs
- Proper exception wrapping and logging

### 3. **Separation of Concerns**

- Application layer doesn't contain business logic (that's in domain)
- Doesn't know about MQTT, JPA, or infrastructure details
- Only depends on domain ports (interfaces)

### 4. **Thread Safety**

- Uses `CopyOnWriteArrayList` for concurrent access
- Creates snapshots before iteration to avoid concurrent modification
- Transactional boundaries properly defined

## Scheduled Task Configuration

The `@Scheduled(fixedRate = 7000)` annotation ensures:

- **Consistent Updates**: Every 7 seconds, regardless of execution time
- **Non-Blocking**: Spring uses a thread pool for scheduled tasks
- **Enabled by**: `@EnableScheduling` on main application class

## Usage Example

```java
@Autowired
private SimulationRunnerService simulationRunner;

// Start a simulation
StartSimulationCommand cmd = new StartSimulationCommand(
    "VEHICLE-001",
    new GeoPoint(40.7128, -74.0060),  // New York
    new GeoPoint(34.0522, -118.2437),  // Los Angeles
    25.0  // 25 m/s (~90 km/h)
);

simulationRunner.startSimulation(cmd);

// The scheduled task will automatically update every 7 seconds

// Stop when needed
simulationRunner.stopSimulation("VEHICLE-001");
```

## Integration Points

### Dependencies (Ports)

1. **SimulationRepository**: Persists simulation state
2. **RouteServicePort**: Fetches routes between points
3. **TelemetryPublisherPort**: Publishes data to MQTT broker

These are interfaces defined in the domain layer. The infrastructure layer provides concrete implementations.

## Transaction Management

- **@Transactional**: Ensures atomicity of operations
- **readOnly = true**: Optimizes read-only queries
- Spring manages transaction boundaries automatically

## Logging

Uses SLF4J with Lombok's `@Slf4j`:

- **INFO**: Major lifecycle events (start, stop, complete)
- **DEBUG**: Detailed location updates
- **WARN**: Recoverable issues (simulation not found)
- **ERROR**: Unrecoverable errors with stack traces

## Future Enhancements

Potential improvements:

1. **Async Processing**: Use `@Async` for non-blocking telemetry publishing
2. **Batch Updates**: Group database updates for better performance
3. **Circuit Breaker**: Add resilience patterns for route service calls
4. **Metrics**: Add Micrometer metrics for monitoring
5. **Events**: Publish domain events for simulation lifecycle changes

## Testing Strategy

- **Unit Tests**: Mock all ports, test business logic
- **Integration Tests**: Test with real Spring context
- **Scheduled Task Tests**: Use `@Scheduled` test support
- **Exception Scenarios**: Verify all error paths

---

**Next Phase**: Implement the Infrastructure Layer (adapters for database, MQTT, and route service)

