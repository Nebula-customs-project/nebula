package pse.nebula.worldview.infrastructure.adapter.outbound.persistence.repository;

import org.springframework.stereotype.Repository;
import pse.nebula.worldview.domain.model.JourneyState;
import pse.nebula.worldview.domain.port.outbound.JourneyStateRepository;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * In-memory implementation of JourneyStateRepository.
 * Uses a thread-safe ConcurrentHashMap for storage.
 *
 * This adapter implements the outbound port for journey state persistence,
 * following the Hexagonal Architecture pattern.
 */
@Repository
public class InMemoryJourneyStateRepositoryAdapter implements JourneyStateRepository {

    private final Map<String, JourneyState> journeyStates = new ConcurrentHashMap<>();

    @Override
    public void save(JourneyState journeyState) {
        journeyStates.put(journeyState.getJourneyId(), journeyState);
    }

    @Override
    public Optional<JourneyState> findById(String journeyId) {
        return Optional.ofNullable(journeyStates.get(journeyId));
    }

    @Override
    public void delete(String journeyId) {
        journeyStates.remove(journeyId);
    }

    @Override
    public boolean exists(String journeyId) {
        return journeyStates.containsKey(journeyId);
    }

    /**
     * Clear all journey states (useful for testing).
     */
    public void clear() {
        journeyStates.clear();
    }

    /**
     * Get the count of active journeys.
     *
     * @return The number of stored journey states
     */
    public int size() {
        return journeyStates.size();
    }
}