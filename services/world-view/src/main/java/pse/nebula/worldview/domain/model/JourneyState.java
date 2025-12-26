package pse.nebula.worldview.domain.model;

/**
 * Represents the current state of a journey on a route.
 * This is a mutable entity that tracks the car's position along the route.
 */
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
        if (speedMetersPerSecond <= 0) {
            throw new IllegalArgumentException("Speed must be positive");
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
     */
    public void start() {
        if (status == JourneyStatus.COMPLETED) {
            throw new IllegalStateException("Cannot start a completed journey");
        }
        this.status = JourneyStatus.IN_PROGRESS;
    }

    /**
     * Pause the journey.
     */
    public void pause() {
        if (status == JourneyStatus.IN_PROGRESS) {
            this.status = JourneyStatus.PAUSED;
        }
    }

    /**
     * Resume a paused journey.
     */
    public void resume() {
        if (status == JourneyStatus.PAUSED) {
            this.status = JourneyStatus.IN_PROGRESS;
        }
    }

    /**
     * Move the car forward based on elapsed time.
     * Returns true if the car has reached the destination.
     *
     * @param elapsedSeconds The time elapsed since the last update
     * @return true if journey is completed
     */
    public boolean advance(double elapsedSeconds) {
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

        progressPercentage = (completedDistance / route.totalDistanceMeters()) * 100.0;
    }

    // Getters
    public String getJourneyId() {
        return journeyId;
    }

    public DrivingRoute getRoute() {
        return route;
    }

    public int getCurrentWaypointIndex() {
        return currentWaypointIndex;
    }

    public Coordinate getCurrentPosition() {
        return currentPosition;
    }

    public JourneyStatus getStatus() {
        return status;
    }

    public double getSpeedMetersPerSecond() {
        return speedMetersPerSecond;
    }

    public double getProgressPercentage() {
        return progressPercentage;
    }

    public void setSpeedMetersPerSecond(double speed) {
        if (speed <= 0) {
            throw new IllegalArgumentException("Speed must be positive");
        }
        this.speedMetersPerSecond = speed;
    }
}

