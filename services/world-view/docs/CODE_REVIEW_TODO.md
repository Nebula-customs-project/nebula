# World-View Service - Code Review & TODO List

> **Review Date:** December 29, 2025  
> **Reviewer:** AI Code Review  
> **Files Reviewed:** 38 source files, 8 test files  
> **Branch:** `chores/testing_world-view`

---

## 📋 Executive Summary

This document contains a comprehensive code review of the `world-view` microservice, identifying improvements based on software engineering best practices including:

- **DRY** (Don't Repeat Yourself)
- **SOLID** Principles
- **Clean Code** Principles
- **Separation of Concerns**
- **Low Coupling, High Cohesion**
- **Defensive Programming**

---

## 📊 Priority Matrix

| Priority | Count | Action Required |
|----------|-------|-----------------|
| 🔴 **High** | 8 | Fix before production |
| 🟡 **Medium** | 15 | Fix in next sprint |
| 🟢 **Low** | 12 | Tech debt backlog |

---

## 🔴 High Priority Issues

### 1. Move Domain Exceptions to Domain Layer

**Files:** `JourneyService.java`, `RouteService.java`

**Current State:**
```java
// Inside JourneyService.java
public static class JourneyNotFoundException extends RuntimeException {
    public JourneyNotFoundException(String message) {
        super(message);
    }
}
```

**Problem:** Exceptions are nested inside application services, violating Domain-Driven Design. The domain layer should own its exceptions.

**Principle Violated:** Separation of Concerns, DDD

**Solution:** Create dedicated exception classes:
```
domain/
  exception/
    JourneyNotFoundException.java
    JourneyAlreadyExistsException.java
    RouteNotFoundException.java
    DomainException.java (base class)
```

---

### 2. PostgresRouteRepository Contains 800+ Lines of Hardcoded Data

**File:** `PostgresRouteRepository.java` (893 lines!)

**Problem:** The repository contains ~800 lines of hardcoded OSRM waypoint coordinates. This violates:
- Single Responsibility Principle (repository should only handle data access)
- Separation of Concerns (data should be separate from code)
- Maintainability (hard to update routes)

**Solution:**
1. Extract route data to JSON files:
```
resources/
  routes/
    route-1-ludwigsburg.json
    route-2-favoritepark.json
    ...
```

2. Create a `RouteDataLoader` service:
```java
@Service
public class RouteDataLoader {
    public List<DrivingRoute> loadRoutes() {
        // Load from JSON files
    }
}
```

---

### 3. Thread Creation in MqttCoordinatePublisher

**File:** `MqttCoordinatePublisher.java`

**Current State:**
```java
private void publishMessage(String topic, Object payload, String messageType) {
    new Thread(() -> {
        // publish logic
    }, "mqtt-publisher").start();
}
```

**Problem:** 
- Creates unbounded threads (can exhaust system resources)
- No thread reuse (inefficient)
- Thread naming doesn't distinguish instances

**Solution:** Use `ExecutorService`:
```java
private final ExecutorService mqttExecutor = Executors.newFixedThreadPool(4);

private void publishMessage(String topic, Object payload, String messageType) {
    mqttExecutor.submit(() -> {
        // publish logic
    });
}

@PreDestroy
public void shutdown() {
    mqttExecutor.shutdown();
}
```

---

### 4. Configuration Not Injected in JourneySchedulerService

**File:** `JourneySchedulerService.java`

**Current State:**
```java
private static final double UPDATE_INTERVAL_SECONDS = 0.5;
private static final long UPDATE_INTERVAL_MS = 500;
```

**Problem:** Hardcoded values when `application.yaml` has configurable properties:
```yaml
journey:
  scheduler:
    update-interval-ms: ${JOURNEY_UPDATE_INTERVAL:2000}
```

**Solution:**
```java
@Value("${journey.scheduler.update-interval-ms:500}")
private long updateIntervalMs;

private double getUpdateIntervalSeconds() {
    return updateIntervalMs / 1000.0;
}
```

---

### 5. GlobalExceptionHandler Couples to Service Classes

**File:** `GlobalExceptionHandler.java`

**Current State:**
```java
import pse.nebula.worldview.application.service.JourneyService;
import pse.nebula.worldview.application.service.RouteService;

@ExceptionHandler(JourneyService.JourneyNotFoundException.class)
public ResponseEntity<Map<String, Object>> handleJourneyNotFound(
    JourneyService.JourneyNotFoundException ex) {
    // ...
}
```

**Problem:** Infrastructure layer (exception handler) directly imports application layer classes.

**Solution:** After implementing TODO #1, use domain exceptions:
```java
import pse.nebula.worldview.domain.exception.JourneyNotFoundException;

@ExceptionHandler(JourneyNotFoundException.class)
public ResponseEntity<ErrorResponse> handleJourneyNotFound(JourneyNotFoundException ex) {
    // ...
}
```

---

### 6. Missing Unit Tests for Critical Components

**Components without tests:**
- `JourneySchedulerService` - Core scheduling logic
- `SseEmitterManager` - SSE connection management
- `CompositeCoordinatePublisher` - Publisher delegation
- `MqttCoordinatePublisher` - MQTT publishing
- `DtoMapper` - Object mapping
- `JourneyController` - REST endpoints

**Solution:** Add comprehensive unit tests:
```java
@ExtendWith(MockitoExtension.class)
class JourneySchedulerServiceTest {
    @Mock JourneyUseCase journeyUseCase;
    @InjectMocks JourneySchedulerService scheduler;
    
    @Test
    void shouldRegisterJourney() { ... }
    
    @Test
    void shouldUnregisterCompletedJourney() { ... }
    
    @Test
    void shouldAdvanceActiveJourneys() { ... }
}
```

---

### 7. CORS Allows All Origins

**Files:** `JourneyController.java`, `RouteController.java`

**Current State:**
```java
@CrossOrigin(origins = "*")
public class JourneyController { }
```

**Problem:** Allows any website to make requests - security vulnerability in production.

**Solution:** Create a central CORS configuration:
```java
@Configuration
public class CorsConfig implements WebMvcConfigurer {
    
    @Value("${cors.allowed-origins:http://localhost:3000}")
    private String[] allowedOrigins;
    
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
            .allowedOrigins(allowedOrigins)
            .allowedMethods("GET", "POST", "PUT", "DELETE")
            .allowCredentials(true);
    }
}
```

---

### 8. Credentials in application.yaml

**File:** `application.yaml`

**Current State:**
```yaml
mqtt:
  password: ${MQTT_PASSWORD:nebula@2025}
```

**Problem:** Default password is visible in version control.

**Solution:**
```yaml
mqtt:
  password: ${MQTT_PASSWORD}  # Required - no default
```

Add to `application-local.yaml` (gitignored):
```yaml
mqtt:
  password: nebula@2025
```

---

## 🟡 Medium Priority Issues

### 9. Duplicate Coordinate Publishing Code

**Files:** `SseCoordinatePublisher.java`, `MqttCoordinatePublisher.java`

**Problem:** Both publishers repeat:
```java
CoordinateUpdateDto update = dtoMapper.toCoordinateUpdate(journeyState);
```

**Solution:** Move conversion to `CompositeCoordinatePublisher`:
```java
@Override
public void publishCoordinateUpdate(String journeyId, Coordinate coordinate, JourneyState journeyState) {
    CoordinateUpdateDto update = dtoMapper.toCoordinateUpdate(journeyState);
    
    for (CoordinatePublisher publisher : publishers) {
        publisher.publishUpdate(journeyId, update);  // Pass DTO directly
    }
}
```

---

### 10. Duplicate Route Creation Logic

**File:** `PostgresRouteRepository.java`

**Problem:** 8 identical `createRouteX()` methods with copy-paste structure.

**Solution:** Extract common logic:
```java
private DrivingRoute createRoute(
        String id, 
        String name, 
        String description, 
        List<Coordinate> waypoints,
        int distanceMeters,
        int durationSeconds) {
    
    Coordinate start = waypoints.get(0);
    Coordinate end = waypoints.get(waypoints.size() - 1);
    
    return new DrivingRoute(id, name, description, start, end, 
            waypoints, distanceMeters, durationSeconds);
}
```

---

### 11. Repeated Error Response Building

**File:** `GlobalExceptionHandler.java`

**Problem:** Raw `Map<String, Object>` used for error responses.

**Solution:** Create `ErrorResponse` record:
```java
public record ErrorResponse(
    Instant timestamp,
    int status,
    String error,
    String message,
    Map<String, String> fieldErrors
) {
    public ErrorResponse(HttpStatus status, String error, String message) {
        this(Instant.now(), status.value(), error, message, null);
    }
}
```

---

### 12. Magic Numbers Without Constants

**Various Files:**

| File | Magic Number | Should Be |
|------|--------------|-----------|
| `SseEmitterManager.java` | `30 * 60 * 1000L` | `SSE_TIMEOUT_MINUTES` |
| `OsrmRouteService.java` | `100` | `MAX_WAYPOINTS` |
| `OsrmRouteService.java` | `10` (seconds) | `HTTP_TIMEOUT_SECONDS` |
| `JourneyController.java` | `15.0` (speed) | `DEFAULT_SPEED_MPS` |

---

### 13. InMemoryRouteRepository is Unused

**File:** `InMemoryRouteRepository.java` (321 lines)

**Problem:** Duplicates route definitions, maintained separately from PostgresRouteRepository.

**Solution Options:**
1. **Remove entirely** if not needed
2. **Move to test sources** as test fixture
3. **Convert to fallback** that's clearly disabled in production

---

### 14. Inconsistent Logging Levels

**Problem:** Similar operations logged at different levels:

```java
// JourneyService.java
log.info("Starting journey...");

// SseCoordinatePublisher.java  
log.debug("SSE: Sent coordinate update...");

// MqttCoordinatePublisher.java
log.info("Published MQTT journey started...");
```

**Solution:** Define logging policy:
| Level | When to Use |
|-------|-------------|
| `ERROR` | Exceptions, failures |
| `WARN` | Recoverable issues |
| `INFO` | Lifecycle events (start/stop/complete) |
| `DEBUG` | Progress updates, routine operations |
| `TRACE` | Detailed message payloads |

---

### 15. No Retry Logic for MQTT Connection

**File:** `MqttConfig.java`

**Problem:** Connection failure only logs warning:
```java
.whenComplete((connAck, throwable) -> {
    if (throwable != null) {
        log.error("Failed to connect to MQTT broker: {}", throwable.getMessage());
    }
});
```

**Solution:** Add health indicator and startup validation:
```java
@Bean
public HealthIndicator mqttHealthIndicator(Mqtt5AsyncClient client) {
    return () -> client.getState().isConnected() 
        ? Health.up().build() 
        : Health.down().withDetail("reason", "disconnected").build();
}
```

---

### 16. Silent Exception Swallowing

**File:** `SseEmitterManager.java`

**Current State:**
```java
emitters.values().forEach(emitter -> {
    try {
        emitter.complete();
    } catch (Exception e) {
        // Ignore
    }
});
```

**Solution:**
```java
} catch (Exception e) {
    log.debug("Error completing SSE emitter: {}", e.getMessage());
}
```

---

### 17. ObjectMapper Not Centrally Configured

**File:** `OsrmRouteService.java`

**Problem:** Creates its own `ObjectMapper`:
```java
this.objectMapper = new ObjectMapper();
```

**Solution:** Inject Spring's auto-configured bean:
```java
public OsrmRouteService(ObjectMapper objectMapper) {
    this.objectMapper = objectMapper;
    this.httpClient = HttpClient.newBuilder()
        .connectTimeout(Duration.ofSeconds(10))
        .build();
}
```

---

### 18. HttpClient Not Injectable

**File:** `OsrmRouteService.java`

**Problem:** Hard to mock for testing.

**Solution:** Make injectable:
```java
@Configuration
public class HttpClientConfig {
    @Bean
    public HttpClient httpClient() {
        return HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();
    }
}
```

---

### 19. Routes Package in Wrong Location

**Path:** `infrastructure/adapter/persistence/routes/`

**Problem:** `OsrmRouteService` is not a persistence adapter - it's an external API client.

**Solution:** Move to:
```
infrastructure/
  adapter/
    external/
      osrm/
        OsrmRouteService.java
        OsrmResponse.java
    routing/
      RouteDefinition.java
      RouteDefinitions.java
```

---

### 20. Test Using Deprecated MockBean

**File:** `RouteControllerTest.java`

**Current State:**
```java
@MockBean  // Deprecated in Spring Boot 3.4+
private RouteUseCase routeUseCase;
```

**Solution:** Use `@MockitoBean`:
```java
@MockitoBean
private RouteUseCase routeUseCase;
```

---

### 21. No Caching of Routes

**Problem:** Routes are fetched from DB on every request, but they never change.

**Solution:**
```java
@Service
@RequiredArgsConstructor
public class RouteService implements RouteUseCase {

    private final RouteRepository routeRepository;
    
    @Cacheable("routes")
    @Override
    public List<DrivingRoute> getAllRoutes() {
        return routeRepository.findAll();
    }
}
```

Add to config:
```java
@EnableCaching
@Configuration
public class CacheConfig {
    @Bean
    public CacheManager cacheManager() {
        return new ConcurrentMapCacheManager("routes");
    }
}
```

---

### 22. Progress Calculation Inefficiency

**File:** `JourneyState.java`

**Problem:** `updateProgress()` iterates all waypoints on every update:
```java
private void updateProgress() {
    double completedDistance = 0;
    for (int i = 0; i < currentWaypointIndex; i++) {
        completedDistance += route.getWaypointAt(i).distanceTo(route.getWaypointAt(i + 1));
    }
    // ...
}
```

**Solution:** Pre-calculate cumulative distances:
```java
private final double[] cumulativeDistances;

public JourneyState(...) {
    // In constructor
    this.cumulativeDistances = calculateCumulativeDistances();
}

private void updateProgress() {
    double completedDistance = cumulativeDistances[currentWaypointIndex];
    // Add partial segment distance
    progressPercentage = (completedDistance / route.totalDistanceMeters()) * 100.0;
}
```

---

### 23. Missing Validation Annotations

**File:** `StartJourneyRequest.java`

**Current State:**
```java
@Positive(message = "Speed must be positive")
private double speedMetersPerSecond;
```

**Missing:** Maximum speed validation, null handling for primitive.

**Solution:**
```java
@NotNull(message = "Speed is required")
@Positive(message = "Speed must be positive")
@Max(value = 100, message = "Speed cannot exceed 100 m/s")
private Double speedMetersPerSecond;  // Use wrapper type
```

---

## 🟢 Low Priority Issues

### 24. JourneyController Multiple Responsibilities

**Observation:** Controller handles lifecycle, streaming, and quick-start.

**Optional Refactor:**
```
JourneyController.java        - CRUD operations
JourneyStreamController.java  - SSE streaming
```

---

### 25. SseEmitterManager Tightly Coupled to DTO

**Observation:** Infrastructure component knows about `CoordinateUpdateDto`.

**Optional Refactor:** Make generic or accept `Object`.

---

### 26. Two Mapper Classes

**Files:** `DtoMapper.java`, `RouteEntityMapper.java`

**Observation:** Could use MapStruct for consistency.

---

### 27. No SSE Integration Tests

**Observation:** Critical feature untested end-to-end.

---

### 28. Inconsistent DTO Naming

**Current:**
- `StartJourneyRequest` (suffix: Request)
- `JourneyStateDto` (suffix: Dto)

**Standard:** 
- Inputs: `*Request`
- Outputs: `*Response` or `*Dto`

---

### 29. Missing Javadoc on Getters

**File:** `JourneyState.java`

**Observation:** Public getters lack documentation.

---

### 30. README Documentation Gaps

**Check if documented:**
- [ ] MQTT integration
- [ ] SSE streaming API
- [ ] Route definitions
- [ ] API endpoint list
- [ ] Environment variables

---

## ✅ Checklist by Sprint

### Sprint 1 (High Priority)
- [ ] Create domain exception classes
- [ ] Extract route data to JSON
- [ ] Replace Thread with ExecutorService
- [ ] Inject scheduler configuration
- [ ] Add missing unit tests
- [ ] Fix CORS configuration
- [ ] Remove default passwords

### Sprint 2 (Medium Priority)
- [ ] Create ErrorResponse DTO
- [ ] Extract route creation helper
- [ ] Add route caching
- [ ] Fix deprecated MockBean
- [ ] Reorganize package structure
- [ ] Add logging policy

### Backlog (Low Priority)
- [ ] Consider controller split
- [ ] Optimize progress calculation
- [ ] Standardize naming
- [ ] Add integration tests
- [ ] Complete Javadoc

---

## 📈 Metrics After Fixes

| Metric | Before | After (Expected) |
|--------|--------|------------------|
| PostgresRouteRepository LOC | 893 | ~150 |
| Test Coverage | ~40% | ~80% |
| Cyclomatic Complexity | Medium | Low |
| Coupling | High | Low |

---

*Document generated during code review session. Last updated: December 29, 2025*
