package pse.nebula.worldview.infrastructure.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Application configuration for the World View service.
 * Enables scheduling for the journey update scheduler.
 */
@Configuration
@EnableScheduling
public class WorldViewConfig {

    // Additional configuration beans can be added here as needed
}