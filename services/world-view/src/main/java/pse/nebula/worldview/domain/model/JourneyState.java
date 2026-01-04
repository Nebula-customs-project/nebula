package pse.nebula.worldview.domain.model;

import lombok.Getter;

/**
 * Represents the current state of a journey on a route.
 * This is a mutable entity that tracks the car's position along the route.
 */
@Getter
public class JourneyState {

    private final String journeyId;
    private final DrivingRoute route;
    private int currentWaypointIndex;
    private Coordinate currentPosition;
    private JourneyStatus status;
    private double speedMetersPerSecond;
    private double progressPercentage;

    public JourneyState(String journeyId, DrivingRoute route, double speedMetersPerSecond) {
        if (journeyId == null || journeyId.isBlank()) {
            throw new IllegalArgumentException("Journey ID cannot be null or empty");
        }
        if (route == null) {
            throw new IllegalArgumentException("Route cannot be null");
        }
        if (!Double.isFinite(speedMetersPerSecond) || speedMetersPerSecond <= 0) {
            throw new IllegalArgumentException(
                "Speed must be a positive finite number, got: " + speedMetersPerSecond);
        }

        this.journeyId = journeyId;
        this.route = route;
        this.speedMetersPerSecond = speedMetersPerSecond;
        this.currentWaypointIndex = 0;
        this.currentPosition = route.startPoint();
        this.status = JourneyStatus.NOT_STARTED;
        this.progressPercentage = 0.0;
    }

    /**
     * Start the journey.
     *
     * @throws IllegalStateException if journey is already completed or in progress
     */
    public void start() {
        if (status == JourneyStatus.COMPLETED) {
            throw new IllegalStateException("Cannot start a completed journey");
        }
        if (status == JourneyStatus.IN_PROGRESS) {
            throw new IllegalStateException("Journey is already in progress");
        }
        this.status = JourneyStatus.IN_PROGRESS;
    }

    /**
     * Pause the journey.
     *
     * @throws IllegalStateException if journey is not in progress
     */
    public void pause() {
        if (status != JourneyStatus.IN_PROGRESS) {
            throw new IllegalStateException(
                "Cannot pause journey in state: " + status + ". Journey must be in progress.");
        }
        this.status = JourneyStatus.PAUSED;
    }

    /**
     * Resume a paused journey.
     *
     * @throws IllegalStateException if journey is not paused
     */
    public void resume() {
        if (status != JourneyStatus.PAUSED) {
            throw new IllegalStateException(
                "Cannot resume journey in state: " + status + ". Journey must be paused.");
        }
        this.status = JourneyStatus.IN_PROGRESS;
    }

    /**
     * Move the car forward based on elapsed time.
     * Returns true if the car has reached the destination.
     *
     * @param elapsedSeconds The time elapsed since the last update (must be positive)
     * @return true if journey is completed
     * @throws IllegalArgumentException if elapsedSeconds is not positive and finite
     */
    public boolean advance(double elapsedSeconds) {
        if (!Double.isFinite(elapsedSeconds) || elapsedSeconds <= 0) {
            throw new IllegalArgumentException(
                "Elapsed time must be a positive finite number, got: " + elapsedSeconds);
        }

        if (status != JourneyStatus.IN_PROGRESS) {
            return status == JourneyStatus.COMPLETED;
        }

        double distanceToTravel = speedMetersPerSecond * elapsedSeconds;

        while (distanceToTravel > 0 && currentWaypointIndex < route.getTotalWaypoints() - 1) {
            Coordinate nextWaypoint = route.getWaypointAt(currentWaypointIndex + 1);
            double distanceToNextWaypoint = currentPosition.distanceTo(nextWaypoint);

            if (distanceToTravel >= distanceToNextWaypoint) {
                // Move to the next waypoint
                currentPosition = nextWaypoint;
                distanceToTravel -= distanceToNextWaypoint;
                currentWaypointIndex++;
            } else {
                // Move partially towards the next waypoint
                double fraction = distanceToTravel / distanceToNextWaypoint;
                currentPosition = currentPosition.interpolateTo(nextWaypoint, fraction);
                distanceToTravel = 0;
            }
        }

        // Update progress percentage
        updateProgress();

        // Check if we've reached the destination
        if (currentWaypointIndex >= route.getTotalWaypoints() - 1) {
            status = JourneyStatus.COMPLETED;
            progressPercentage = 100.0;
            return true;
        }

        return false;
    }

    private void updateProgress() {
        if (route.getTotalWaypoints() <= 1) {
            progressPercentage = 100.0;
            return;
        }

        double completedDistance = 0;
        for (int i = 0; i < currentWaypointIndex; i++) {
            completedDistance += route.getWaypointAt(i).distanceTo(route.getWaypointAt(i + 1));
        }

        // Add partial distance in current segment
        if (currentWaypointIndex < route.getTotalWaypoints() - 1) {
            Coordinate segmentStart = route.getWaypointAt(currentWaypointIndex);
            completedDistance += segmentStart.distanceTo(currentPosition);
        }

        progressPercentage = Math.min(100.0, (completedDistance / route.totalDistanceMeters()) * 100.0);
    }

    /**
     * Update the speed of the journey.
     *
     * @param speed The new speed in meters per second
     * @throws IllegalArgumentException if speed is not positive and finite
     * @throws IllegalStateException if journey is already completed
     */
    public void setSpeedMetersPerSecond(double speed) {
        if (!Double.isFinite(speed) || speed <= 0) {
            throw new IllegalArgumentException(
                "Speed must be a positive finite number, got: " + speed);
        }
        if (status == JourneyStatus.COMPLETED) {
            throw new IllegalStateException("Cannot change speed of a completed journey");
        }
        this.speedMetersPerSecond = speed;
    }
}