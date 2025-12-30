# World-View Service Architecture Guide

> **Understanding Hexagonal Architecture (Ports & Adapters)**  
> A comparison with Traditional Spring Boot Architecture

---

## 📚 Table of Contents

1. [Introduction](#introduction)
2. [Traditional Spring Boot Architecture](#traditional-spring-boot-architecture)
3. [Hexagonal Architecture Overview](#hexagonal-architecture-overview)
4. [Side-by-Side Comparison](#side-by-side-comparison)
5. [World-View Service Structure](#world-view-service-structure)
6. [Layer-by-Layer Explanation](#layer-by-layer-explanation)
7. [Data Flow Examples](#data-flow-examples)
8. [Why Hexagonal Architecture?](#why-hexagonal-architecture)
9. [Quick Reference](#quick-reference)

---

## Introduction

The `world-view` service uses **Hexagonal Architecture** (also called **Ports & Adapters** or **Clean Architecture**). This guide will help you understand it by comparing it to the traditional Spring Boot layered architecture you're familiar with.

### Key Insight

> In hexagonal architecture, the **domain/business logic is at the center** and doesn't depend on any external technology. Everything else (database, web, messaging) are just "plugins" that connect to the core.

---

## Traditional Spring Boot Architecture

This is what you're used to:

```
src/main/java/com/example/app/
├── controller/          # REST Controllers
│   └── UserController.java
├── service/             # Business Logic
│   └── UserService.java
├── repository/          # Data Access
│   └── UserRepository.java
├── entity/              # JPA Entities
│   └── User.java
├── dto/                 # Data Transfer Objects
│   └── UserDto.java
└── config/              # Configuration
    └── SecurityConfig.java
```

### Layer Flow (Traditional)

```
┌─────────────────────────────────────────────────────────────┐
│                      Controller Layer                        │
│                   (REST API Endpoints)                       │
└─────────────────────────┬───────────────────────────────────┘
                          │ calls
                          ▼
┌─────────────────────────────────────────────────────────────┐
│                      Service Layer                           │
│                   (Business Logic)                           │
└─────────────────────────┬───────────────────────────────────┘
                          │ calls
                          ▼
┌─────────────────────────────────────────────────────────────┐
│                    Repository Layer                          │
│                   (Database Access)                          │
└─────────────────────────┬───────────────────────────────────┘
                          │ queries
                          ▼
┌─────────────────────────────────────────────────────────────┐
│                       Database                               │
└─────────────────────────────────────────────────────────────┘
```

### Problems with Traditional Architecture

1. **Service depends on Repository** - Business logic knows about database
2. **Hard to test** - Need database to test service layer
3. **Technology lock-in** - Changing database affects service code
4. **Mixed concerns** - DTOs often contain validation, JPA annotations, JSON annotations

---

## Hexagonal Architecture Overview

### The Hexagon Concept

Imagine your application as a hexagon (or any shape with multiple sides):

```
                    ┌─────────────────┐
                    │   REST API      │
                    │   (Adapter)     │
                    └────────┬────────┘
                             │
       ┌─────────────┐       │       ┌─────────────┐
       │   Message   │       │       │   Database  │
       │   Queue     │◄──────┼──────►│   (Adapter) │
       │  (Adapter)  │       │       │             │
       └─────────────┘       │       └─────────────┘
                             │
                    ┌────────▼────────┐
                    │                 │
                    │   DOMAIN CORE   │
                    │  (Pure Business │
                    │     Logic)      │
                    │                 │
                    └────────┬────────┘
                             │
       ┌─────────────┐       │       ┌─────────────┐
       │   External  │       │       │    File     │
       │   Service   │◄──────┴──────►│   System    │
       │  (Adapter)  │               │  (Adapter)  │
       └─────────────┘               └─────────────┘
```

### Key Concepts

| Term | Definition | Example |
|------|------------|---------|
| **Domain** | Pure business logic with no external dependencies | `JourneyState`, `DrivingRoute` |
| **Port** | An interface that defines how to interact with the domain | `JourneyUseCase`, `RouteRepository` |
| **Adapter** | Implementation that connects external tech to a port | `PostgresRouteRepository`, `JourneyController` |
| **Inbound Port** | How the outside world talks TO the domain | `JourneyUseCase` (called by controller) |
| **Outbound Port** | How the domain talks TO the outside world | `RouteRepository` (called by service) |

---

## Side-by-Side Comparison

### Directory Structure Comparison

| Traditional | Hexagonal (World-View) | Purpose |
|-------------|------------------------|---------|
| `controller/` | `infrastructure/adapter/web/controller/` | REST endpoints |
| `service/` | `application/service/` | Orchestration |
| `repository/` | `domain/port/outbound/` (interface) | Data access contract |
| `repository/` | `infrastructure/adapter/persistence/` (impl) | Data access implementation |
| `entity/` | `infrastructure/adapter/persistence/entity/` | JPA entities |
| `domain/` or `model/` | `domain/model/` | Business objects |
| `dto/` | `infrastructure/adapter/web/dto/` | API data contracts |
| `config/` | `infrastructure/config/` | Spring configuration |

### Code Organization Comparison

**Traditional:**
```java
// UserService.java
@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;  // Direct dependency
    
    public User createUser(UserDto dto) {
        User user = new User(dto.getName());
        return userRepository.save(user);   // Knows about JPA
    }
}
```

**Hexagonal:**
```java
// JourneyService.java (Application Layer)
@Service
@RequiredArgsConstructor
public class JourneyService implements JourneyUseCase {
    
    private final RouteUseCase routeUseCase;              // Inbound port
    private final JourneyStateRepository repository;      // Outbound port (interface!)
    private final CoordinatePublisher publisher;          // Outbound port (interface!)
    
    @Override
    public JourneyState startNewJourney(String journeyId, double speed) {
        DrivingRoute route = routeUseCase.getRandomRoute();
        JourneyState journey = new JourneyState(journeyId, route, speed);
        journey.start();
        repository.save(journey);           // Doesn't know if it's Postgres, Redis, or InMemory!
        publisher.publishJourneyStarted(journey);  // Doesn't know if it's SSE, MQTT, or WebSocket!
        return journey;
    }
}
```

---

## World-View Service Structure

```
services/world-view/src/main/java/pse/nebula/worldview/
│
├── WorldViewApplication.java              # Spring Boot entry point
│
├── domain/                                 # 🎯 THE CORE (No Spring dependencies!)
│   ├── model/                             # Business entities
│   │   ├── Coordinate.java                # Value object (record)
│   │   ├── DrivingRoute.java              # Value object (record)
│   │   ├── JourneyState.java              # Entity (mutable state)
│   │   └── JourneyStatus.java             # Enum
│   │
│   └── port/                              # Interfaces (contracts)
│       ├── inbound/                       # How outside calls IN
│       │   ├── JourneyUseCase.java        # "Start journey", "Pause", etc.
│       │   └── RouteUseCase.java          # "Get routes", "Get random route"
│       │
│       └── outbound/                      # How domain calls OUT
│           ├── CoordinatePublisher.java   # "Publish coordinates to somewhere"
│           ├── JourneyStateRepository.java # "Store journey state somewhere"
│           └── RouteRepository.java       # "Fetch routes from somewhere"
│
├── application/                           # 🔧 ORCHESTRATION LAYER
│   └── service/                           
│       ├── JourneyService.java            # Implements JourneyUseCase
│       ├── JourneySchedulerService.java   # Background job
│       └── RouteService.java              # Implements RouteUseCase
│
└── infrastructure/                        # 🔌 EXTERNAL CONNECTIONS
    ├── adapter/
    │   ├── messaging/                     # Message publishing adapters
    │   │   ├── CompositeCoordinatePublisher.java
    │   │   ├── MqttCoordinatePublisher.java      # Implements CoordinatePublisher
    │   │   └── SseCoordinatePublisher.java       # Implements CoordinatePublisher
    │   │
    │   ├── persistence/                   # Database adapters
    │   │   ├── InMemoryJourneyStateRepository.java  # Implements JourneyStateRepository
    │   │   ├── PostgresRouteRepository.java          # Implements RouteRepository
    │   │   ├── entity/
    │   │   │   ├── RouteEntity.java       # JPA entity (NOT domain model!)
    │   │   │   └── WaypointEntity.java
    │   │   ├── jpa/
    │   │   │   └── JpaRouteRepository.java  # Spring Data interface
    │   │   └── mapper/
    │   │       └── RouteEntityMapper.java   # Entity ↔ Domain conversion
    │   │
    │   └── web/                           # Web adapters
    │       ├── controller/
    │       │   ├── JourneyController.java # REST API
    │       │   └── RouteController.java
    │       ├── dto/                       # API data contracts
    │       │   ├── CoordinateDto.java
    │       │   ├── JourneyStateDto.java
    │       │   ├── RouteDto.java
    │       │   └── StartJourneyRequest.java
    │       ├── exception/
    │       │   └── GlobalExceptionHandler.java
    │       ├── mapper/
    │       │   └── DtoMapper.java         # Domain ↔ DTO conversion
    │       └── sse/
    │           └── SseEmitterManager.java
    │
    └── config/                            # Spring configuration
        ├── MqttConfig.java
        └── WorldViewConfig.java
```

---

## Layer-by-Layer Explanation

### 1️⃣ Domain Layer (`domain/`)

**Purpose:** Contains pure business logic. No Spring, no JPA, no HTTP concerns.

**What's Inside:**

| Package | Contents | Your Traditional Equivalent |
|---------|----------|----------------------------|
| `model/` | Business objects | `domain/` or `model/` |
| `port/inbound/` | Use case interfaces | N/A (service methods directly) |
| `port/outbound/` | Repository interfaces | `repository/` interfaces |

**Key Files:**

```java
// domain/model/Coordinate.java - Pure Java record, no annotations
public record Coordinate(double latitude, double longitude) {
    public Coordinate {
        if (latitude < -90 || latitude > 90) {
            throw new IllegalArgumentException("Invalid latitude");
        }
    }
    
    public double distanceTo(Coordinate other) {
        // Haversine formula - pure business logic
    }
}
```

```java
// domain/port/inbound/JourneyUseCase.java - What can the app DO?
public interface JourneyUseCase {
    JourneyState startNewJourney(String journeyId, double speed);
    JourneyState startJourneyOnRoute(String journeyId, String routeId, double speed);
    void pauseJourney(String journeyId);
    void resumeJourney(String journeyId);
    Coordinate advanceJourney(String journeyId, double elapsedSeconds);
}
```

```java
// domain/port/outbound/RouteRepository.java - What data does domain NEED?
public interface RouteRepository {
    List<DrivingRoute> findAll();
    Optional<DrivingRoute> findById(String routeId);
    int count();
}
```

**💡 Key Insight:** The domain layer has ZERO knowledge of:
- How routes are stored (Postgres? MongoDB? File?)
- How coordinates are published (SSE? MQTT? WebSocket?)
- How users interact with the system (REST? GraphQL? CLI?)

---

### 2️⃣ Application Layer (`application/`)

**Purpose:** Orchestrates use cases. Implements inbound ports. Calls outbound ports.

**Your Traditional Equivalent:** This is like your `service/` layer, but:
- It implements interfaces (ports) instead of being called directly
- It doesn't know concrete implementations (works with interfaces)

**Key Files:**

```java
// application/service/JourneyService.java
@Service
@RequiredArgsConstructor
public class JourneyService implements JourneyUseCase {  // ← Implements inbound port
    
    // Dependencies are INTERFACES, not concrete classes
    private final RouteUseCase routeUseCase;           // Another inbound port
    private final JourneyStateRepository repository;    // Outbound port
    private final CoordinatePublisher publisher;        // Outbound port
    
    @Override
    public JourneyState startJourneyOnRoute(String journeyId, String routeId, double speed) {
        DrivingRoute route = routeUseCase.getRouteById(routeId);
        JourneyState journey = new JourneyState(journeyId, route, speed);
        journey.start();
        repository.save(journey);
        publisher.publishJourneyStarted(journey);
        return journey;
    }
}
```

**Notice:**
- `JourneyService` doesn't know if `repository` is PostgreSQL or in-memory
- `JourneyService` doesn't know if `publisher` sends SSE or MQTT
- This makes testing EASY - just mock the interfaces!

---

### 3️⃣ Infrastructure Layer (`infrastructure/`)

**Purpose:** All the "dirty" technical stuff - databases, messaging, web frameworks.

**Subfolders:**

#### `adapter/web/` - REST API Layer
```
Your Traditional: controller/ + dto/
```

```java
// infrastructure/adapter/web/controller/JourneyController.java
@RestController
@RequestMapping("/api/v1/journeys")
@RequiredArgsConstructor
public class JourneyController {
    
    private final JourneyUseCase journeyUseCase;  // ← Depends on PORT, not service!
    private final DtoMapper dtoMapper;
    
    @PostMapping
    public ResponseEntity<JourneyStateDto> startJourney(@RequestBody StartJourneyRequest request) {
        // 1. Call domain through PORT
        JourneyState journey = journeyUseCase.startJourneyOnRoute(
            request.getJourneyId(),
            request.getRouteId(),
            request.getSpeedMetersPerSecond()
        );
        
        // 2. Convert domain model to DTO
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(dtoMapper.toDto(journey));
    }
}
```

#### `adapter/persistence/` - Database Layer
```
Your Traditional: repository/ + entity/
```

```java
// infrastructure/adapter/persistence/PostgresRouteRepository.java
@Repository
@Primary
@RequiredArgsConstructor
public class PostgresRouteRepository implements RouteRepository {  // ← Implements outbound port
    
    private final JpaRouteRepository jpaRepo;           // Spring Data JPA
    private final RouteEntityMapper mapper;
    
    @Override
    public List<DrivingRoute> findAll() {
        return jpaRepo.findAll().stream()
            .map(mapper::toDomain)        // Entity → Domain
            .toList();
    }
    
    @Override
    public Optional<DrivingRoute> findById(String routeId) {
        return jpaRepo.findById(routeId)
            .map(mapper::toDomain);
    }
}
```

**Notice the separation:**
- `RouteEntity` - JPA entity with `@Entity`, `@Column` annotations
- `DrivingRoute` - Pure domain object, no JPA
- `RouteEntityMapper` - Converts between them

#### `adapter/messaging/` - Messaging Layer
```
Your Traditional: No direct equivalent (maybe event publishing)
```

```java
// infrastructure/adapter/messaging/SseCoordinatePublisher.java
@Component
@RequiredArgsConstructor
public class SseCoordinatePublisher implements CoordinatePublisher {  // ← Implements outbound port
    
    private final SseEmitterManager sseManager;
    private final DtoMapper dtoMapper;
    
    @Override
    public void publishCoordinateUpdate(String journeyId, Coordinate coord, JourneyState state) {
        CoordinateUpdateDto update = dtoMapper.toCoordinateUpdate(state);
        sseManager.sendUpdate(journeyId, update);
    }
}
```

---

## Data Flow Examples

### Example 1: Start a Journey

```
┌──────────────┐         ┌─────────────────┐         ┌────────────────┐
│   Frontend   │         │ JourneyController│         │ JourneyService │
│  (Browser)   │         │    (Adapter)     │         │  (Application) │
└──────┬───────┘         └────────┬─────────┘         └───────┬────────┘
       │                          │                           │
       │ POST /api/v1/journeys    │                           │
       │ {journey_id, route_id}   │                           │
       │─────────────────────────►│                           │
       │                          │                           │
       │                          │ startJourneyOnRoute()     │
       │                          │ (via JourneyUseCase port) │
       │                          │──────────────────────────►│
       │                          │                           │
       │                          │           ┌───────────────┴───────────────┐
       │                          │           │                               │
       │                          │           ▼                               ▼
       │                          │   ┌──────────────────┐           ┌───────────────────┐
       │                          │   │ RouteRepository  │           │CoordinatePublisher│
       │                          │   │ (Outbound Port)  │           │ (Outbound Port)   │
       │                          │   └────────┬─────────┘           └─────────┬─────────┘
       │                          │            │                               │
       │                          │            ▼                               ▼
       │                          │   ┌──────────────────┐           ┌───────────────────┐
       │                          │   │PostgresRouteRepo │           │SseCoordPublisher  │
       │                          │   │   (Adapter)      │           │   (Adapter)       │
       │                          │   └────────┬─────────┘           └─────────┬─────────┘
       │                          │            │                               │
       │                          │            ▼                               ▼
       │                          │   ┌──────────────────┐           ┌───────────────────┐
       │                          │   │   PostgreSQL     │           │  SSE Connection   │
       │                          │   └──────────────────┘           └───────────────────┘
       │                          │                           │
       │                          │◄──────────────────────────│
       │                          │      JourneyState         │
       │                          │                           │
       │◄─────────────────────────│                           │
       │     JourneyStateDto      │                           │
       │                          │                           │
```

### Example 2: Object Transformations

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                                                                             │
│   HTTP Request                    Domain                      Database     │
│                                                                             │
│   StartJourneyRequest  ──────►  JourneyState  ◄──────────  RouteEntity     │
│   {                              (Domain Model)              (JPA Entity)  │
│     journey_id: "j-1"                │                           │         │
│     route_id: "r-1"                  │                           │         │
│     speed: 13.89                     │                           │         │
│   }                                  │                           │         │
│         │                            │                           │         │
│         │                            │                           │         │
│         ▼                            ▼                           ▼         │
│   ┌─────────────────┐      ┌─────────────────┐      ┌──────────────────┐  │
│   │   Controller    │      │  Application    │      │   Repository     │  │
│   │   (Web Adapter) │      │    Service      │      │(Persistence      │  │
│   │                 │      │                 │      │    Adapter)      │  │
│   └────────┬────────┘      └────────┬────────┘      └─────────┬────────┘  │
│            │                        │                         │            │
│            │    DtoMapper           │    RouteEntityMapper    │            │
│            │    ──────────          │    ──────────────────   │            │
│            ▼                        ▼                         ▼            │
│   JourneyStateDto            DrivingRoute              DrivingRoute        │
│   (API Response)             (Domain Model)            (Domain Model)      │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘
```

---

## Why Hexagonal Architecture?

### Benefits

| Benefit | Explanation |
|---------|-------------|
| **Testability** | Domain logic can be tested without database, web server, or messaging |
| **Flexibility** | Swap PostgreSQL for MongoDB without touching domain code |
| **Independence** | Multiple teams can work on different adapters simultaneously |
| **Clean Domain** | Business logic is pure and focused |
| **Technology Freedom** | Framework/library changes don't ripple through codebase |

### Real Example from World-View

```java
// We can have MULTIPLE implementations of CoordinatePublisher
public interface CoordinatePublisher {
    void publishCoordinateUpdate(String journeyId, Coordinate coord, JourneyState state);
}

// Implementation 1: SSE for browser EventSource
@Component
public class SseCoordinatePublisher implements CoordinatePublisher { ... }

// Implementation 2: MQTT for IoT devices  
@Component
public class MqttCoordinatePublisher implements CoordinatePublisher { ... }

// Implementation 3: Composite that uses BOTH!
@Component
@Primary
public class CompositeCoordinatePublisher implements CoordinatePublisher {
    private final List<CoordinatePublisher> publishers;
    
    public void publishCoordinateUpdate(...) {
        for (CoordinatePublisher publisher : publishers) {
            publisher.publishCoordinateUpdate(...);  // Publishes to SSE AND MQTT
        }
    }
}
```

**The domain (`JourneyService`) doesn't know or care which publisher is used!**

---

## Quick Reference

### Traditional → Hexagonal Mapping

| Traditional Package | Hexagonal Package | Layer |
|---------------------|-------------------|-------|
| `model/` | `domain/model/` | Domain |
| `service/` | `application/service/` | Application |
| `repository/` (interface) | `domain/port/outbound/` | Domain |
| `repository/` (impl) | `infrastructure/adapter/persistence/` | Infrastructure |
| `controller/` | `infrastructure/adapter/web/controller/` | Infrastructure |
| `dto/` | `infrastructure/adapter/web/dto/` | Infrastructure |
| `entity/` | `infrastructure/adapter/persistence/entity/` | Infrastructure |
| `config/` | `infrastructure/config/` | Infrastructure |

### Dependency Rules

```
┌─────────────────────────────────────────────────────────┐
│                    DEPENDENCY DIRECTION                  │
│                                                         │
│   Infrastructure ──────► Application ──────► Domain     │
│                                                         │
│   (Outer layers depend on inner layers, never reverse)  │
└─────────────────────────────────────────────────────────┘
```

- ✅ Controller can import JourneyUseCase
- ✅ JourneyService can import DrivingRoute
- ❌ DrivingRoute cannot import JourneyService
- ❌ Coordinate cannot import JpaRouteRepository

### When to Use Each Layer

| I want to... | Put it in... |
|--------------|--------------|
| Define business rules | `domain/model/` |
| Define what the app CAN DO | `domain/port/inbound/` |
| Define what the app NEEDS | `domain/port/outbound/` |
| Implement business operations | `application/service/` |
| Handle HTTP requests | `infrastructure/adapter/web/` |
| Access the database | `infrastructure/adapter/persistence/` |
| Publish messages | `infrastructure/adapter/messaging/` |
| Configure Spring beans | `infrastructure/config/` |

---

## Summary

### Traditional Architecture
```
Controller → Service → Repository → Database
(Everything flows down, service knows about JPA)
```

### Hexagonal Architecture
```
         ┌─────────────────┐
Web ────►│                 │◄──── Database
         │  DOMAIN CORE    │
MQTT ───►│  (Pure Logic)   │◄──── External API
         │                 │
         └─────────────────┘
(Domain is protected, adapters plug in from outside)
```

### Key Takeaways

1. **Domain is king** - It doesn't depend on anything external
2. **Ports are contracts** - They define what the domain needs/offers
3. **Adapters are plugins** - They connect external tech to ports
4. **Dependencies point inward** - Outer layers depend on inner layers
5. **Easy testing** - Mock the ports, test the domain logic

---

*Architecture guide for the Nebula World-View Service*  
*Last updated: December 29, 2025*
