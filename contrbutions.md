# Nebula - Project Contributors

This document details the individual contributions of the team members towards the development of the **Nebula** vehicle buying and telemetry platform.

## 👥 Team Members & Roles

| Name | Primary Role | Key Focus Areas |
| :--- | :--- | :--- |
| **Syed** | Full Stack & Architect | UI (3D), Backend Services, Infrastructure |
| **Divyansh Sahu** | Full Stack & Architect | UI, Backend Services, Infrastructure |
| **Akshika Baghla** | Full stack & Architect | Backend Services, UI, Infrastructure |
| **Pankaj Sain** | Full Stack & Architect | Merchandise Service, UI |
| **Gayathri** | Frontend | UI |
| **Manik** | Backend | Backend Services |

---

## 🛠 Detailed Contributions

### 👨‍💻 Syed (Musaib Uzzama)
**Core Responsibility:** Architecture Design, 3D Frontend, Infrastructure and World-View Services.

* **Car-Configurator UI (Frontend):**
    * Developed a realistic 3D vehicle configurator using **Next.js** and **React Three Fiber (R3F)**.
    * Implemented a "Gamify-like" environment for immersive user interaction in car configurator.
    * Built logic for real-time customization of vehicle assets, which includes exterior paints, interior colors, and rim selection using `.glb` models.
* **World-View Service (Backend):**
    * Designed the **Telemetry Simulator Service** using **Hexagonal Architecture** to ensure domain logic isolation.
    * Implemented **REST APIs** for service control and **MQTT** integration for streaming live car location data.
* **Infrastructure & Architecture:**
    *   **Co-worked with Divyansh and Akshika on Infrastructure:**
    * Defined the overall microservices architecture & hexagonal architecture for world-view.
    * Set up and configured **Docker Compose** for container orchestration across the development environment.
* **Integrations:**
    * Did setup of user session management from the Frontend.
    * Collaborated with vehicle-service B.E for data handshake.
    
### 👨‍💻 Divyansh

**Core Responsibility:** Architecture Design, Frontend, Infrastructure, Gateway and Platform service

* **UI (Frontend):**
    *   End-to-end development of the World View interface, providing an immersive driving experience.
    *   Integrated real-time vehicle telemetry data into the 3D like world environment and animations.
	*   Implemented the Browser cookie session management. 

* **Backend Services:**
    *   **Platform Core Service:** Led the development of the core platform service, centralizing shared infrastructure configurations.
    *   **Gateway Service:** Implemented the API Gateway to authenticate, route traffic and integrate various backend microservices.
    *   **User Service:** * Improved security features, specifically responsible for **JWT Token signing,generation** and implemented **refresh token**.
                          * Implemented storing and expiration of Refresh Token Family and revocation.
* **Infrastructure and Architecture:**
    *   **Co-worked with Syed and Akshika on Infrastructure:**
    *   **Docker:** Orchestrated the complete containerization of the application stack using Docker.
    *   **Platform Stack:** Set up and configured essential infrastructure components including **PostgreSQL**, **RabbitMQ**, and **WebSockets** within the platform-core. 
    *   **Composition Architecture:** Implemented Composition Architecture for the Platform Core Service and Gateway Service to ensure domain logic isolation.

* **Integrations:**
    *   Orchestrated the integration of all microservices through the Gateway and Platform services.
    *   Managed the end-to-end integration between the Frontend applications and Backend APIs.

### 👩‍💻 Akshika

**Core Responsibility:** Architecture Design, Frontend, Infrastructure, Vehicle Service and User Vehicle Service

*  **Vehicle Service and User Vehicle Service(Frontend):**
	*   **Frontend API Integration**: Integrated the frontend application with the Vehicle Service API to enable dynamic retrieval and display of vehicle inventory, implementing robust error handling and loading states to ensure a reliable user experience.
	
	* **UI Implementation**: Developed the "Cars Collection" interface using React and Next.js, featuring a responsive grid layout that showcases high-quality vehicle imagery alongside formatted metadata (pricing and specifications) for optimal product presentation.
	
	*  **Navigation & Context Management**: Designed and implemented the navigation logic connecting the vehicle listing to the Car Configurator, utilizing query parameters to maintain vehicle context and streamline the user's customization workflow.

	* **Real-Time Telemetry Integration**: Orchestrated the seamless integration of the User Vehicle Service with the client-side application via WebSocket protocols, enabling the continuous streaming and dynamic visualization of real-time vehicle telemetry data.

    * **State Synchronization**: Implemented the frontend consumption logic for user-specific vehicle data, ensuring the precise synchronization and persistent display of the user's configured vehicle attributes and operational status across the interface.



*  **Vehicle Service and User Vehicle Service (Backend):**
	*  **Backend Architecture & API Design**: Engineered the backend architecture for the Vehicle and User Vehicle Service, implementing RESTful API endpoints and robust data seeding mechanisms to ensure the reliable delivery of complex vehicle specifications and media assets to front-end clients.

	*  **Service Logic & State Management**: Developed the core business logic for the User Vehicle Service to manage vehicle ownership and configuration persistence, ensuring data integrity and seamless synchronization across the distributed microservices environment.



*  **Infrastructure & Architecture:**
      *   **Co-worked with Syed and Divyansh on Infrastructure:**
	  * **Microservices Architecture Design**: Architected a decoupled microservices infrastructure for the Vehicle and User Vehicle domains, optimizing system modularity and allowing for independent scaling and maintenance of core service components.
	 * **Real-Time Telemetry Implementation**: Designed and integrated a WebSocket-based communication layer within the User Vehicle Service to facilitate low-latency, bi-directional data streaming for real-time vehicle position and fuel level.

*  **Integrations:**
    *   **Made Handshake between car configurator and vehicle service:** Integration of car configurator and vehicle service to enable real-time vehicle configuration and customization.
    *   **Integration between user-vehicle service and vehicle service:** Integration of user-vehicle service and vehicle service to enable real-time personal vehicle ownership and configuration persistence.
    *  **Integration of gateway service and user-vehicle service:** Integration of gateway service and user-vehicle service to enable routing of requests to user-vehicle service.


### 👨‍💻 Pankaj Sain
**Core Responsibility:** Merchandise service development, frontend integration, and commerce-related frontend implementation.

* **Backend:**
    * Implemented the Merchandise Service scaffold and core domain structure.
    * Implemented product and cart domains, including controllers, services, repositories, DTOs, and entities.
    * Implemented REST endpoints for product creation, retrieval, update, deletion, and cart operations.
    * Implemented data seeding for merchandise products.
    * Added unit, integration, and controller tests for merchandise and cart functionality.

* **Integration:**
    * Integrated the Merchandise Service with the API Gateway.
    * Implemented frontend-to-backend communication for merchandise and cart APIs.
    * Implemented Next.js proxy routing for merchandise-related backend requests.
    * Connected frontend product, cart, checkout, and wishlist flows with backend APIs.

* **Frontend:**
    * Updated the merchandise listing page with improved UI, wishlist filtering, and toast notifications.
    * Updated cart page UI and client-side handling logic for a better user experience.
    * Updated checkout and payment page UI, including the implementation of the credit card preview component.
    * Updated navigation bar UI, including logo positioning, cart indicator, and navigation behavior.
	* Implemented cart persistence.


### 👩‍💻 Gayathri
**Core Responsibility:** Frontend Development

* **Frontend:**
    * Created the Neon frontend application structure with React and Next.js as the base for the platform UI.
    * Implemented user interface components and page layouts for primary platform screens.
    * Set up and maintained basic project dependencies and lock files for consistent frontend builds.
    * Established baseline page layout and ensured the groundwork for responsive design and modular UI development.
    * Supported integration readiness with backend services and other frontend branches by providing a clean, scalable codebase.

### 👨‍💻 Manik
**Core Responsibility:** User Service Development, Authentication & Security

* **User Service (Backend):**
    * Created the **User Service** from scratch including core architecture, controllers, models, and repositories.
    * Implemented **JWT Token Generation** and **JwksController** for token verification.
    * Integrated with **Config Server** for centralized configuration.

* **Authentication & Security:**
    * Implemented **Role-Based Access Control (RBAC)** with Admin and User roles.
    * Created **AdminController** and **AdminUserInitializer** for admin management.
    * Implemented **SecurityConfig** and **JwtAuthenticationFilter** for secure request handling.

* **Token Blacklisting:**
    * Designed and implemented **Redis-based Token Blacklisting** for secure logout functionality.
    * Refactored to **JTI (JWT ID)** approach for improved performance and security.

* **Testing & Integration:**
    * Added comprehensive **unit tests** for UserService.
    * Configured Gateway routing for User Service endpoints.

---

## 📊 Project Statistics
* **Total Contributors:** 6
* **Tech Stack:** Java, Spring Boot, Next.js, React Three Fiber, MQTT, Docker, Websocket, PostgreSQL, RabbitMQ, Redis
