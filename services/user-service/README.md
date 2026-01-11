# User Service

This microservice manages user profiles and their associated vehicles for the Nebula application.

## Features
- User registration and management
- Vehicle configuration and live data tracking
- Integration with MQTT for real-time vehicle updates
- RESTful API for frontend integration

## Endpoints
- `GET /api/users` - Get all users
- `POST /api/users` - Create user
- `GET /api/users/{id}` - Get user by ID
- `PUT /api/users/{id}` - Update user
- `DELETE /api/users/{id}` - Delete user
- `GET /api/users/{userId}/vehicle` - Get user's vehicle
- `POST /api/users/{userId}/vehicle` - Create/update user's vehicle

## Database
Uses PostgreSQL with JPA/Hibernate. Tables: `users`, `user_vehicles`.

## MQTT
Subscribes to `vehicle/+/data` for live updates.