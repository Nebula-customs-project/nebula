package pse.nebula.worldview.domain.port.inbound;

import pse.nebula.worldview.domain.model.Coordinate;
import pse.nebula.worldview.domain.model.JourneyState;

/**
 * Inbound port for journey-related use cases.
 * Handles journey lifecycle and querying.
 *
 * Note: Journeys are automatically managed by the system.
 * Users cannot manually start, stop, or pause journeys.
 */
public interface JourneyUseCase {

    /**
     * Start a new journey on a randomly selected route.
     * This is called internally by the auto-scheduler.
     *
     * @param journeyId Unique identifier for the journey
     * @param speedMetersPerSecond The speed of the car in m/s
     * @return The initial journey state
     */
    JourneyState startNewJourney(String journeyId, double speedMetersPerSecond);

    /**
     * Get the current state of a journey.
     *
     * @param journeyId The journey identifier
     * @return The current journey state
     */
    JourneyState getJourneyState(String journeyId);

    /**
     * Advance the journey and return the new position.
     * This is called internally by the auto-scheduler.
     *
     * @param journeyId The journey identifier
     * @param elapsedSeconds Time elapsed since last update
     * @return The current coordinate after advancement
     */
    Coordinate advanceJourney(String journeyId, double elapsedSeconds);

    /**
     * Stop and remove a journey.
     * This is called internally when a journey completes.
     *
     * @param journeyId The journey identifier
     */
    void stopJourney(String journeyId);

    /**
     * Check if a journey exists.
     *
     * @param journeyId The journey identifier
     * @return true if the journey exists
     */
    boolean journeyExists(String journeyId);
}