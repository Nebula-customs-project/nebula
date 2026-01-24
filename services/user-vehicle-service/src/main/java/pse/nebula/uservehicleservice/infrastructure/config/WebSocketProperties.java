package pse.nebula.uservehicleservice.infrastructure.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * WebSocket Configuration Properties
 * 
 * Loads WebSocket settings from application.yaml or Config Server.
 * Follows the externalized configuration pattern used for database settings.
 * 
 * Properties can be sourced from:
 * - Local application.yaml
 * - Config Server (shared-websocket profile)
 * - Environment variables
 */
@Configuration
@ConfigurationProperties(prefix = "websocket.vehicle-telemetry")
public class WebSocketProperties {

    /**
     * WebSocket endpoint path for vehicle telemetry.
     * Default: /ws/vehicle-telemetry
     */
    private String endpoint = "/ws/vehicle-telemetry";

    /**
     * Interval in minutes between telemetry updates.
     * Default: 5 minutes
     */
    private int intervalMinutes = 5;

    /**
     * Maximum WebSocket connections allowed per user.
     * New connections will close existing ones for the same user.
     * Default: 1
     */
    private int maxConnectionsPerUser = 1;

    // Getters and Setters

    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public int getIntervalMinutes() {
        return intervalMinutes;
    }

    public void setIntervalMinutes(int intervalMinutes) {
        this.intervalMinutes = intervalMinutes;
    }

    public int getMaxConnectionsPerUser() {
        return maxConnectionsPerUser;
    }

    public void setMaxConnectionsPerUser(int maxConnectionsPerUser) {
        this.maxConnectionsPerUser = maxConnectionsPerUser;
    }

    @Override
    public String toString() {
        return "WebSocketProperties{" +
                "endpoint='" + endpoint + '\'' +
                ", intervalMinutes=" + intervalMinutes +
                ", maxConnectionsPerUser=" + maxConnectionsPerUser +
                '}';
    }
}
