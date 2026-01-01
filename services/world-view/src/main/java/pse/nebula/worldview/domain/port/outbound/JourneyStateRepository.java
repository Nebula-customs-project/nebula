package pse.nebula.worldview.domain.port.outbound;

import pse.nebula.worldview.domain.model.JourneyState;

import java.util.Optional;

/**
 * Outbound port for journey state persistence.
 * This is a secondary port implemented by infrastructure adapters.
 */
public interface JourneyStateRepository {

    /**
     * Save or update a journey state.
     *
     * @param journeyState The journey state to save
     */
    void save(JourneyState journeyState);

    /**
     * Find a journey state by its ID.
     *
     * @param journeyId The journey identifier
     * @return Optional containing the journey state if found
     */
    Optional<JourneyState> findById(String journeyId);

    /**
     * Delete a journey state.
     *
     * @param journeyId The journey identifier
     */
    void delete(String journeyId);

    /**
     * Check if a journey exists.
     *
     * @param journeyId The journey identifier
     * @return true if the journey exists
     */
    boolean exists(String journeyId);
}