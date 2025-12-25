package pse.nebula.telemetry.infrastructure.config;

import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.Configuration;

/**
 * Application Configuration
 *
 * Enables scanning for @ConfigurationProperties annotated classes
 * in the infrastructure.config package.
 */
@Configuration
@ConfigurationPropertiesScan("pse.nebula.telemetry.infrastructure.config")
public class ApplicationConfig {
}

