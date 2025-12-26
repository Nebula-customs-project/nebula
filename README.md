# Nebula - Telemetry Management Platform

A microservices-based platform for vehicle telemetry simulation and management, built with Spring Boot and hexagonal architecture principles.

## 🏗️ Architecture

This project follows **Hexagonal Architecture (Ports and Adapters)** with clean separation of concerns:

- **Domain Layer**: Pure business logic, framework-agnostic
- **Application Layer**: Use cases and orchestration
- **Infrastructure Layer**: External integrations (Database, MQTT, REST APIs)

## 📦 Project Structure

```
nebula/
├── docker/                          # Docker infrastructure
│   ├── docker-compose.yml          # PostgreSQL & Mosquitto MQTT
│   └── mosquitto/                  # MQTT broker configuration
├── services/                        # Microservices
│   └── telemetry-simulator/        # Vehicle telemetry simulation service
│       ├── src/
│       │   └── main/
│       │       ├── java/
│       │       │   └── pse/nebula/telemetry/
│       │       │       ├── domain/          # Core business logic
│       │       │       ├── application/     # Use cases
│       │       │       └── infrastructure/  # External adapters
│       │       └── resources/
│       └── pom.xml
├── frontend/                        # Frontend application
│   └── neon/                       # Next.js web application
└── pom.xml                         # Parent POM
```

## 🚀 Quick Start

### Prerequisites

- **Java 17+**
- **Maven 3.8+**
- **Docker & Docker Compose**
- **Node.js 18+** (for frontend)

### 1. Environment Setup

⚠️ **IMPORTANT**: Before running the project, you must configure environment variables.

```bash
# Copy environment templates
cp .env.example .env
cp services/telemetry-simulator/.env.example services/telemetry-simulator/.env

# Edit .env files with your credentials
# See ENV_SETUP.md for detailed instructions
```

📖 **Read the [Environment Setup Guide](ENV_SETUP.md)** for detailed configuration instructions.

### 2. Start Infrastructure Services

```bash
cd docker
docker-compose --env-file ../.env up -d
```

This starts:
- **PostgreSQL** on port `5434`
- **Eclipse Mosquitto** (MQTT) on port `1883`

Verify services are running:
```bash
docker ps
```

### 3. Build and Run Telemetry Simulator

```bash
# Build the entire project
mvn clean install

# Run the telemetry-simulator service
cd services/telemetry-simulator
mvn spring-boot:run
```

The service will be available at `http://localhost:8080`

### 4. Run Frontend (Optional)

```bash
cd frontend/neon
npm install
npm run dev
```

The frontend will be available at `http://localhost:3000`

## 🛠️ Technology Stack

### Backend
- **Java 17**
- **Spring Boot 3.4.2**
- **Spring Data JPA** - Database persistence
- **PostgreSQL 15** - Relational database
- **HiveMQ MQTT Client 1.3.12** - MQTT messaging
- **Lombok** - Reduce boilerplate code
- **Maven** - Build and dependency management

### Infrastructure
- **Docker & Docker Compose** - Containerization
- **PostgreSQL 15 Alpine** - Database
- **Eclipse Mosquitto 2** - MQTT broker

### Frontend
- **Next.js 14** - React framework
- **TypeScript** - Type safety
- **Tailwind CSS** - Styling

## 🔒 Security

### Environment Variables

- **Never commit `.env` files** - They are already in `.gitignore`
- Use `.env.example` templates for reference
- Store secrets in environment variables, not in code
- Use strong, unique passwords for production

### Dependency Management

This project follows security best practices:
- Regular dependency updates
- CVE scanning and remediation
- Latest stable versions of critical dependencies

### Production Deployment

For production:
1. Use secret management tools (HashiCorp Vault, AWS Secrets Manager)
2. Enable SSL/TLS for all connections
3. Use strong, randomly generated passwords
4. Implement proper network segmentation
5. Follow principle of least privilege

## 📚 Documentation

- [Environment Setup Guide](ENV_SETUP.md) - Detailed environment configuration
- [GTA5 Extraction Guide](frontend/neon/GTA5_EXTRACTION_GUIDE.md) - Vehicle model extraction

## 🧪 Testing

```bash
# Run all tests
mvn test

# Run tests for specific service
cd services/telemetry-simulator
mvn test
```

## 🔧 Development

### Code Style

- Follow Java code conventions
- Use Lombok to reduce boilerplate
- Keep domain logic framework-agnostic
- Write clean, maintainable code

### Adding New Services

1. Create service directory under `services/`
2. Add module to parent `pom.xml`
3. Follow hexagonal architecture pattern
4. Create service-specific `.env.example`

### Building for Production

```bash
# Build with production profile
mvn clean package -Pprod

# Build Docker images
docker build -t nebula/telemetry-simulator:latest services/telemetry-simulator/
```

## 🐛 Troubleshooting

### Database Connection Issues

```bash
# Check PostgreSQL is running
docker logs telemetry_postgres

# Test connection
psql -h localhost -p 5434 -U nebula_user -d telemetry_db
```

### MQTT Connection Issues

```bash
# Check Mosquitto is running
docker logs telemetry_mosquitto

# Test MQTT connection
mosquitto_sub -h localhost -p 1883 -t telemetry/data
```

### Build Issues

```bash
# Clean and rebuild
mvn clean install -U

# Skip tests if needed
mvn clean install -DskipTests
```

## 📝 License

This project is proprietary and confidential.

## 👥 Contributors

- **PSE Team** - Initial work

## 📞 Support

For issues and questions, please contact the development team.

---

**Built with ❤️ by PSE Team**

