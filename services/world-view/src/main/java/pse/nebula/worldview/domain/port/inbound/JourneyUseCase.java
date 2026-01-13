package pse.nebula.worldview.domain.port.inbound;

import pse.nebula.worldview.domain.model.Coordinate;
import pse.nebula.worldview.domain.model.JourneyState;

/**
 * Inbound port for journey-related use cases.
 * Handles starting, controlling, and querying journeys.
 */
public interface JourneyUseCase {

    /**
     * Start a new journey on a randomly selected route.
     *
     * @param journeyId Unique identifier for the journey
     * @param speedMetersPerSecond The speed of the car in m/s
     * @return The initial journey state
     */
    JourneyState startNewJourney(String journeyId, double speedMetersPerSecond);

    /**
     * Start a new journey on a specific route.
     *
     * @param journeyId Unique identifier for the journey
     * @param routeId The route to use
     * @param speedMetersPerSecond The speed of the car in m/s
     * @return The initial journey state
     */
    JourneyState startJourneyOnRoute(String journeyId, String routeId, double speedMetersPerSecond);

    /**
     * Get the current state of a journey.
     *
     * @param journeyId The journey identifier
     * @return The current journey state
     */
    JourneyState getJourneyState(String journeyId);

    /**
     * Get the next coordinate in the journey.
     * This advances the journey state and returns the new position.
     *
     * @param journeyId The journey identifier
     * @param elapsedSeconds Time elapsed since last update
     * @return The current coordinate after advancement
     */
    Coordinate advanceJourney(String journeyId, double elapsedSeconds);

    /**
     * Pause an active journey.
     *
     * @param journeyId The journey identifier
     */
    void pauseJourney(String journeyId);

    /**
     * Resume a paused journey.
     *
     * @param journeyId The journey identifier
     */
    void resumeJourney(String journeyId);

    /**
     * Stop and remove a journey.
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