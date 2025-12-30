package pse.nebula.worldview.application.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import pse.nebula.worldview.domain.model.JourneyState;
import pse.nebula.worldview.domain.model.JourneyStatus;
import pse.nebula.worldview.domain.port.inbound.JourneyUseCase;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Service responsible for running active journeys and publishing coordinate updates.
 * Uses a scheduled task to periodically advance journeys and stream coordinates to the frontend.
 */
@Slf4j
@Service
public class JourneySchedulerService {

    private final JourneyUseCase journeyUseCase;
    private final long updateIntervalMs;
    private final double updateIntervalSeconds;

    // Track active journey IDs
    private final Set<String> activeJourneyIds = ConcurrentHashMap.newKeySet();

    public JourneySchedulerService(
            JourneyUseCase journeyUseCase,
            @Value("${journey.scheduler.update-interval-ms:500}") long updateIntervalMs) {
        this.journeyUseCase = journeyUseCase;
        this.updateIntervalMs = updateIntervalMs;
        this.updateIntervalSeconds = updateIntervalMs / 1000.0;
        log.info("JourneySchedulerService initialized with update interval: {}ms ({}s)", 
                updateIntervalMs, updateIntervalSeconds);
    }

    /**
     * Register a journey for scheduled updates.
     *
     * @param journeyId The journey ID to track
     */
    public void registerJourney(String journeyId) {
        activeJourneyIds.add(journeyId);
        log.info("=== JOURNEY REGISTERED for scheduled updates: {} ===", journeyId);
        log.info("Active journeys count: {}", activeJourneyIds.size());
    }

    /**
     * Unregister a journey from scheduled updates.
     *
     * @param journeyId The journey ID to untrack
     */
    public void unregisterJourney(String journeyId) {
        activeJourneyIds.remove(journeyId);
        log.info("Unregistered journey from updates: {}", journeyId);
    }

    /**
     * Check if a journey is being tracked.
     *
     * @param journeyId The journey ID
     * @return true if the journey is active
     */
    public boolean isJourneyActive(String journeyId) {
        return activeJourneyIds.contains(journeyId);
    }

    /**
     * Get the number of active journeys.
     *
     * @return The count of active journeys
     */
    public int getActiveJourneyCount() {
        return activeJourneyIds.size();
    }

    /**
     * Get the configured update interval in milliseconds.
     * @return update interval in ms
     */
    public long getUpdateIntervalMs() {
        return updateIntervalMs;
    }

    /**
     * Scheduled task that runs at configured interval to update all active journeys.
     * This advances each journey and publishes coordinate updates to the frontend.
     */
    @Scheduled(fixedRateString = "${journey.scheduler.update-interval-ms:500}")
    public void updateActiveJourneys() {
        if (activeJourneyIds.isEmpty()) {
            return;
        }

        log.debug("=== SCHEDULER: Updating {} active journey(s) ===", activeJourneyIds.size());

        // Process each active journey
        for (String journeyId : Set.copyOf(activeJourneyIds)) {
            try {
                processJourney(journeyId);
            } catch (Exception e) {
                log.error("Error processing journey: {}", journeyId, e);
                // Continue with other journeys
            }
        }
    }

    private void processJourney(String journeyId) {
        if (!journeyUseCase.journeyExists(journeyId)) {
            log.warn("Journey no longer exists, unregistering: {}", journeyId);
            activeJourneyIds.remove(journeyId);
            return;
        }

        JourneyState state = journeyUseCase.getJourneyState(journeyId);

        // Skip if not in progress
        if (state.getStatus() != JourneyStatus.IN_PROGRESS) {
            if (state.getStatus() == JourneyStatus.COMPLETED) {
                log.info("Journey completed, unregistering: {}", journeyId);
                activeJourneyIds.remove(journeyId);
            }
            return;
        }

        // Advance the journey - this publishes the coordinate update
        journeyUseCase.advanceJourney(journeyId, updateIntervalSeconds);

        // Check if completed after advancement
        JourneyState updatedState = journeyUseCase.getJourneyState(journeyId);
        if (updatedState.getStatus() == JourneyStatus.COMPLETED) {
            log.info("Journey completed after update, unregistering: {}", journeyId);
            activeJourneyIds.remove(journeyId);
        }
    }
}

