package pse.nebula.worldview.application.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
@RequiredArgsConstructor
public class JourneySchedulerService {

    private static final double UPDATE_INTERVAL_SECONDS = 2.0; // 2 seconds between updates

    private final JourneyUseCase journeyUseCase;

    // Track active journey IDs
    private final Set<String> activeJourneyIds = ConcurrentHashMap.newKeySet();

    /**
     * Register a journey for scheduled updates.
     *
     * @param journeyId The journey ID to track
     */
    public void registerJourney(String journeyId) {
        activeJourneyIds.add(journeyId);
        log.info("Registered journey for updates: {}", journeyId);
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
     * Scheduled task that runs every 2 seconds to update all active journeys.
     * This advances each journey and publishes coordinate updates to the frontend.
     */
    @Scheduled(fixedRate = 2000) // 2 seconds
    public void updateActiveJourneys() {
        if (activeJourneyIds.isEmpty()) {
            return;
        }

        log.debug("Updating {} active journey(s)", activeJourneyIds.size());

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
        journeyUseCase.advanceJourney(journeyId, UPDATE_INTERVAL_SECONDS);

        // Check if completed after advancement
        JourneyState updatedState = journeyUseCase.getJourneyState(journeyId);
        if (updatedState.getStatus() == JourneyStatus.COMPLETED) {
            log.info("Journey completed after update, unregistering: {}", journeyId);
            activeJourneyIds.remove(journeyId);
        }
    }
}

