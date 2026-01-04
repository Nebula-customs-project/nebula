package pse.nebula.worldview.application.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import pse.nebula.worldview.domain.model.DrivingRoute;
import pse.nebula.worldview.domain.model.JourneyState;
import pse.nebula.worldview.domain.model.JourneyStatus;
import pse.nebula.worldview.domain.port.inbound.JourneyUseCase;
import pse.nebula.worldview.domain.port.inbound.RouteUseCase;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Service responsible for automatically managing journeys.
 *
 * This service:
 * - Automatically starts a new journey on a random route when no journey is active
 * - Continuously advances the active journey
 * - When a journey completes, waits for a configurable delay before starting a new one
 *
 * No user intervention is required - journeys run automatically in a loop.
 */
@Slf4j
@Service
public class AutoJourneySchedulerService {

    private final JourneyUseCase journeyUseCase;
    private final RouteUseCase routeUseCase;
    private final double updateIntervalSeconds;
    private final double defaultSpeedMps;
    private final long delayBetweenJourneysMs;

    // Track the current active journey
    private final AtomicReference<String> activeJourneyId = new AtomicReference<>(null);

    // Track when the last journey completed (for delay between journeys)
    private volatile long lastJourneyCompletedTime = 0;

    public AutoJourneySchedulerService(
            JourneyUseCase journeyUseCase,
            RouteUseCase routeUseCase,
            @Value("${journey.scheduler.update-interval-ms:500}") long updateIntervalMs,
            @Value("${journey.scheduler.default-speed-mps:13.89}") double defaultSpeedMps,
            @Value("${journey.scheduler.delay-between-journeys-ms:5000}") long delayBetweenJourneysMs) {
        this.journeyUseCase = journeyUseCase;
        this.routeUseCase = routeUseCase;
        this.updateIntervalSeconds = updateIntervalMs / 1000.0;
        this.defaultSpeedMps = defaultSpeedMps;
        this.delayBetweenJourneysMs = delayBetweenJourneysMs;

        log.info("AutoJourneySchedulerService initialized:");
        log.info("  - Update interval: {}ms ({}s)", updateIntervalMs, updateIntervalSeconds);
        log.info("  - Default speed: {} m/s ({} km/h)", defaultSpeedMps, defaultSpeedMps * 3.6);
        log.info("  - Delay between journeys: {}ms", delayBetweenJourneysMs);
    }

    /**
     * Main scheduled task that manages the journey lifecycle automatically.
     * Runs at the configured update interval.
     */
    @Scheduled(fixedRateString = "${journey.scheduler.update-interval-ms:500}")
    public void manageJourneys() {
        String currentJourneyId = activeJourneyId.get();

        if (currentJourneyId == null) {
            // No active journey - check if we should start a new one
            handleNoActiveJourney();
        } else {
            // Active journey exists - advance it
            handleActiveJourney(currentJourneyId);
        }
    }

    /**
     * Handle the case when no journey is currently active.
     * Starts a new journey after the configured delay.
     */
    private void handleNoActiveJourney() {
        // Check if we need to wait before starting a new journey
        if (lastJourneyCompletedTime > 0) {
            long timeSinceCompletion = System.currentTimeMillis() - lastJourneyCompletedTime;
            if (timeSinceCompletion < delayBetweenJourneysMs) {
                log.debug("Waiting before starting new journey. Time remaining: {}ms",
                        delayBetweenJourneysMs - timeSinceCompletion);
                return;
            }
        }

        // Start a new journey on a random route
        startNewAutoJourney();
    }

    /**
     * Handle the active journey - advance it and check for completion.
     */
    private void handleActiveJourney(String journeyId) {
        try {
            // Check if journey still exists
            if (!journeyUseCase.journeyExists(journeyId)) {
                log.warn("Active journey no longer exists: {}", journeyId);
                activeJourneyId.set(null);
                return;
            }

            JourneyState state = journeyUseCase.getJourneyState(journeyId);

            // Check if journey is completed
            if (state.getStatus() == JourneyStatus.COMPLETED) {
                log.info("=== JOURNEY COMPLETED: {} ===", journeyId);
                onJourneyCompleted(journeyId);
                return;
            }

            // Only advance if in progress
            if (state.getStatus() == JourneyStatus.IN_PROGRESS) {
                journeyUseCase.advanceJourney(journeyId, updateIntervalSeconds);

                // Check if completed after advancement
                JourneyState updatedState = journeyUseCase.getJourneyState(journeyId);
                if (updatedState.getStatus() == JourneyStatus.COMPLETED) {
                    log.info("=== JOURNEY COMPLETED after advance: {} ===", journeyId);
                    onJourneyCompleted(journeyId);
                }
            }
        } catch (Exception e) {
            log.error("Error processing journey: {}", journeyId, e);
            // Clear the active journey to allow recovery
            activeJourneyId.set(null);
        }
    }

    /**
     * Start a new journey automatically on a random route.
     */
    private void startNewAutoJourney() {
        try {
            // Get a random route
            DrivingRoute route = routeUseCase.getRandomRoute();

            // Generate a unique journey ID
            String journeyId = "auto-journey-" + UUID.randomUUID().toString().substring(0, 8);

            log.info("=== STARTING NEW AUTO JOURNEY ===");
            log.info("Journey ID: {}", journeyId);
            log.info("Route: {} ({})", route.name(), route.id());
            log.info("Speed: {} m/s ({} km/h)", defaultSpeedMps, defaultSpeedMps * 3.6);

            // Start the journey
            JourneyState journeyState = journeyUseCase.startNewJourney(journeyId, defaultSpeedMps);

            // Register as active journey
            activeJourneyId.set(journeyId);

            log.info("Journey started successfully. Total waypoints: {}",
                    journeyState.getRoute().getTotalWaypoints());

        } catch (Exception e) {
            log.error("Failed to start new auto journey", e);
        }
    }

    /**
     * Handle journey completion - cleanup and prepare for next journey.
     */
    private void onJourneyCompleted(String journeyId) {
        // Clean up the completed journey
        try {
            journeyUseCase.stopJourney(journeyId);
        } catch (Exception e) {
            log.debug("Journey already cleaned up: {}", journeyId);
        }

        // Clear active journey
        activeJourneyId.set(null);

        // Record completion time for delay
        lastJourneyCompletedTime = System.currentTimeMillis();

        log.info("Journey {} cleaned up. Next journey will start in {}ms",
                journeyId, delayBetweenJourneysMs);
    }

    /**
     * Get the current active journey ID, if any.
     *
     * @return Optional containing the active journey ID
     */
    public Optional<String> getActiveJourneyId() {
        return Optional.ofNullable(activeJourneyId.get());
    }

    /**
     * Check if there is an active journey running.
     *
     * @return true if a journey is currently active
     */
    public boolean hasActiveJourney() {
        return activeJourneyId.get() != null;
    }

    /**
     * Get the current active journey state, if any.
     *
     * @return Optional containing the active journey state
     */
    public Optional<JourneyState> getActiveJourneyState() {
        String journeyId = activeJourneyId.get();
        if (journeyId == null) {
            return Optional.empty();
        }
        try {
            return Optional.of(journeyUseCase.getJourneyState(journeyId));
        } catch (Exception e) {
            return Optional.empty();
        }
    }
}

