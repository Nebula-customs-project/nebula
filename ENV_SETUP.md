# Environment Configuration Guide

This project uses environment variables to manage sensitive configuration data such as database credentials, API keys, and other secrets.

## 🔒 Security Best Practices

**IMPORTANT:** Never commit `.env` files to version control. These files are already listed in `.gitignore`.

## 📋 Setup Instructions

### 1. Root Environment Variables (Docker Infrastructure)

Copy the example file and configure your Docker services:

```bash
cp .env.example .env
```

Edit `.env` and update the following values:
- `POSTGRES_PASSWORD`: Set a strong password for PostgreSQL
- `POSTGRES_USER`: Database user (default: nebula_user)
- `POSTGRES_DB`: Database name (default: telemetry_db)
- `MQTT_PORT`: MQTT broker port (default: 1883)
- `MQTT_UI_PORT`: MQTT UI port (default: 9001)

### 2. Service Environment Variables (Telemetry Simulator)

Copy the example file for the telemetry-simulator service:

```bash
cp services/telemetry-simulator/.env.example services/telemetry-simulator/.env
```

Edit `services/telemetry-simulator/.env` and update the following values:
- `SPRING_DATASOURCE_PASSWORD`: Must match the PostgreSQL password from root `.env`
- `SPRING_DATASOURCE_USERNAME`: Must match the PostgreSQL user from root `.env`
- `SPRING_DATASOURCE_URL`: Database connection string (adjust host/port if needed)
- `MQTT_BROKER_HOST`: MQTT broker host (localhost for local dev, mosquitto for Docker)
- `SERVER_PORT`: Port for the service (default: 8080)

## 🚀 Running the Project

### Local Development

1. Start Docker services:
```bash
cd docker
docker-compose --env-file ../.env up -d
```

2. Run the telemetry-simulator service:
```bash
cd services/telemetry-simulator
mvn spring-boot:run
```

### Docker Deployment

When running services in Docker, update the connection strings:
- Change `SPRING_DATASOURCE_URL` to use `postgres` instead of `localhost`
- Change `MQTT_BROKER_HOST` to `mosquitto` instead of `localhost`

## 📝 Environment Variables Reference

### Root `.env` (Docker Infrastructure)

| Variable | Description | Default | Required |
|----------|-------------|---------|----------|
| POSTGRES_USER | PostgreSQL username | nebula_user | Yes |
| POSTGRES_PASSWORD | PostgreSQL password | - | Yes |
| POSTGRES_DB | Database name | telemetry_db | Yes |
| MQTT_PORT | MQTT broker port | 1883 | Yes |
| MQTT_UI_PORT | MQTT UI port | 9001 | Yes |

### Service `.env` (Telemetry Simulator)

| Variable | Description | Default | Required |
|----------|-------------|---------|----------|
| SERVER_PORT | Service port | 8080 | Yes |
| SPRING_DATASOURCE_URL | Database connection URL | - | Yes |
| SPRING_DATASOURCE_USERNAME | Database username | nebula_user | Yes |
| SPRING_DATASOURCE_PASSWORD | Database password | - | Yes |
| MQTT_BROKER_HOST | MQTT broker hostname | localhost | Yes |
| MQTT_BROKER_PORT | MQTT broker port | 1883 | Yes |
| MQTT_CLIENT_ID | MQTT client identifier | auto-generated | No |
| MQTT_TOPIC_TELEMETRY | MQTT telemetry topic | telemetry/data | No |

## ⚠️ Troubleshooting

### Connection Refused Errors

If you get connection refused errors:
1. Verify Docker services are running: `docker ps`
2. Check that ports are not already in use
3. Ensure environment variables are correctly set

### Authentication Failures

If you get authentication errors:
1. Verify passwords match between root `.env` and service `.env`
2. Restart Docker services after changing credentials
3. Clear any cached connections

## 🔐 Production Deployment

For production environments:

1. **Use strong passwords**: Generate cryptographically secure passwords
2. **Use secret management**: Consider using HashiCorp Vault, AWS Secrets Manager, or similar
3. **Rotate credentials regularly**: Change passwords and keys on a regular schedule
4. **Limit access**: Use principle of least privilege for all credentials
5. **Enable SSL/TLS**: Use encrypted connections for all services
6. **Audit access**: Monitor and log all access to sensitive credentials

## 📚 Additional Resources

- [Spring Boot Externalized Configuration](https://docs.spring.io/spring-boot/docs/current/reference/html/features.html#features.external-config)
- [Docker Compose Environment Variables](https://docs.docker.com/compose/environment-variables/)
- [PostgreSQL Security](https://www.postgresql.org/docs/current/security.html)
- [Eclipse Mosquitto Configuration](https://mosquitto.org/man/mosquitto-conf-5.html)

