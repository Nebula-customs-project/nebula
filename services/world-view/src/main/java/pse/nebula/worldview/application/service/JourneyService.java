package pse.nebula.worldview.application.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pse.nebula.worldview.domain.exception.JourneyAlreadyExistsException;
import pse.nebula.worldview.domain.exception.JourneyNotFoundException;
import pse.nebula.worldview.domain.model.Coordinate;
import pse.nebula.worldview.domain.model.DrivingRoute;
import pse.nebula.worldview.domain.model.JourneyState;
import pse.nebula.worldview.domain.port.inbound.JourneyUseCase;
import pse.nebula.worldview.domain.port.inbound.RouteUseCase;
import pse.nebula.worldview.domain.port.outbound.CoordinatePublisher;
import pse.nebula.worldview.domain.port.outbound.JourneyStateRepository;

/**
 * Application service that implements journey-related use cases.
 * Orchestrates the journey lifecycle and coordinates between domain and infrastructure.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class JourneyService implements JourneyUseCase {

    private final RouteUseCase routeUseCase;
    private final JourneyStateRepository journeyStateRepository;
    private final CoordinatePublisher coordinatePublisher;

    @Override
    public JourneyState startNewJourney(String journeyId, double speedMetersPerSecond) {
        log.info("Starting new journey with ID: {} at speed: {} m/s", journeyId, speedMetersPerSecond);

        // Get a random route
        DrivingRoute route = routeUseCase.getRandomRoute();

        return createAndStartJourney(journeyId, route, speedMetersPerSecond);
    }

    @Override
    public JourneyState startJourneyOnRoute(String journeyId, String routeId, double speedMetersPerSecond) {
        log.info("Starting journey with ID: {} on route: {} at speed: {} m/s",
            journeyId, routeId, speedMetersPerSecond);

        DrivingRoute route = routeUseCase.getRouteById(routeId);

        return createAndStartJourney(journeyId, route, speedMetersPerSecond);
    }

    private JourneyState createAndStartJourney(String journeyId, DrivingRoute route, double speedMetersPerSecond) {
        // Check if journey already exists
        if (journeyStateRepository.exists(journeyId)) {
            throw new JourneyAlreadyExistsException(journeyId);
        }

        // Create new journey state
        JourneyState journeyState = new JourneyState(journeyId, route, speedMetersPerSecond);
        journeyState.start();

        // Persist the journey state
        journeyStateRepository.save(journeyState);

        // Publish journey started event
        coordinatePublisher.publishJourneyStarted(journeyState);

        log.info("Journey started successfully: {} on route: {}", journeyId, route.name());
        return journeyState;
    }

    @Override
    public JourneyState getJourneyState(String journeyId) {
        return journeyStateRepository.findById(journeyId)
            .orElseThrow(() -> new JourneyNotFoundException(journeyId));
    }

    @Override
    public Coordinate advanceJourney(String journeyId, double elapsedSeconds) {
        JourneyState journeyState = getJourneyState(journeyId);

        int totalWaypoints = journeyState.getRoute().waypoints().size();

        boolean completed = journeyState.advance(elapsedSeconds);

        // Save updated state
        journeyStateRepository.save(journeyState);

        // Publish coordinate update
        Coordinate currentPosition = journeyState.getCurrentPosition();
        int currentWaypoint = journeyState.getCurrentWaypointIndex();
        double progress = journeyState.getProgressPercentage();
        
        // Log every 10th waypoint or milestones (10%, 25%, 50%, 75%, 90%)
        if (currentWaypoint % 10 == 0 || currentWaypoint == 1 || 
            (progress > 10 && progress < 11) || (progress > 25 && progress < 26) ||
            (progress > 50 && progress < 51) || (progress > 75 && progress < 76) ||
            (progress > 90 && progress < 91)) {
            log.info("Journey {} - Waypoint {}/{} ({}%) - Position: [{}, {}]",
                    journeyId, 
                    currentWaypoint + 1, 
                    totalWaypoints,
                    String.format("%.1f", progress),
                    String.format("%.6f", currentPosition.latitude()),
                    String.format("%.6f", currentPosition.longitude()));
        }
        
        coordinatePublisher.publishCoordinateUpdate(journeyId, currentPosition, journeyState);

        if (completed) {
            log.info("=== JOURNEY COMPLETED: {} - Successfully reached destination! ===", journeyId);
            coordinatePublisher.publishJourneyCompleted(journeyState);
        }

        return currentPosition;
    }

    @Override
    public void pauseJourney(String journeyId) {
        log.info("Pausing journey: {}", journeyId);
        JourneyState journeyState = getJourneyState(journeyId);
        journeyState.pause();
        journeyStateRepository.save(journeyState);
    }

    @Override
    public void resumeJourney(String journeyId) {
        log.info("Resuming journey: {}", journeyId);
        JourneyState journeyState = getJourneyState(journeyId);
        journeyState.resume();
        journeyStateRepository.save(journeyState);
    }

    @Override
    public void stopJourney(String journeyId) {
        log.info("Stopping journey: {}", journeyId);
        journeyStateRepository.delete(journeyId);
    }

    @Override
    public boolean journeyExists(String journeyId) {
        return journeyStateRepository.exists(journeyId);
    }
}