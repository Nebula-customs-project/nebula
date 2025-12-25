package pse.nebula.telemetry.domain;

import java.util.List;

public class Simulation {
    private final String vehicleId;
    private GeoPoint currentLocation;
    private double speed; // meters per second
    private SimulationStatus status;
    private Route route;
    private int nextRoutePointIndex;

    public Simulation(String vehicleId, GeoPoint currentLocation, double speed, Route route) {
        this.vehicleId = vehicleId;
        this.currentLocation = currentLocation;
        this.speed = speed;
        this.route = route;
        this.status = SimulationStatus.STOPPED;
        this.nextRoutePointIndex = 1; // Assuming start is index 0
    }

    public void start() {
        this.status = SimulationStatus.RUNNING;
    }

    public void stop() {
        this.status = SimulationStatus.STOPPED;
    }

    public void move(double secondsElapsed) {
        if (status != SimulationStatus.RUNNING || route == null || route.points().isEmpty()) {
            return;
        }

        if (nextRoutePointIndex >= route.points().size()) {
            status = SimulationStatus.COMPLETED;
            return;
        }

        double distanceToTravel = speed * secondsElapsed;

        while (distanceToTravel > 0 && nextRoutePointIndex < route.points().size()) {
            GeoPoint target = route.points().get(nextRoutePointIndex);
            double distToTarget = calculateDistance(currentLocation, target);

            if (distanceToTravel >= distToTarget) {
                // Reached the target point
                currentLocation = target;
                distanceToTravel -= distToTarget;
                nextRoutePointIndex++;
            } else {
                // Move towards target
                double fraction = distanceToTravel / distToTarget;
                double newLat = currentLocation.lat() + (target.lat() - currentLocation.lat()) * fraction;
                double newLng = currentLocation.lng() + (target.lng() - currentLocation.lng()) * fraction;
                currentLocation = new GeoPoint(newLat, newLng);
                distanceToTravel = 0;
            }
        }

        if (nextRoutePointIndex >= route.points().size()) {
            status = SimulationStatus.COMPLETED;
        }
    }

    // Simple Euclidean distance for now, can be improved to Haversine
    // Note: This treats lat/lng as flat coordinates, which is an approximation.
    // For more accuracy, we should project or use Haversine.
    // Given "linear interpolation logic", this simple approach is likely expected for the exercise.
    // However, to make speed meaningful (m/s), we need to convert degrees to meters.
    // 1 deg lat ~= 111km. 1 deg lng varies.
    // Let's stick to a very simple interpolation where speed is in "degrees per second" if we don't convert.
    // But the prompt says "speed" (usually m/s or km/h).
    // Let's assume the coordinates are close enough that we can just interpolate.
    // But wait, if speed is 20m/s, and we add 20 to lat, we jump across the globe.
    // We need a conversion factor.
    // Let's assume a constant conversion for simplicity or just use a small factor.
    // Or better, let's implement a helper to move in meters.

    private double calculateDistance(GeoPoint p1, GeoPoint p2) {
        // Haversine formula or similar would be better, but for "linear interpolation" on a plane:
        // We need to convert degrees to meters to compare with 'distanceToTravel' (which is in meters).
        // Approx: 1 degree = 111,139 meters.
        double latDiff = p2.lat() - p1.lat();
        double lngDiff = p2.lng() - p1.lng();

        // Simple conversion for distance calculation
        double latMeters = latDiff * 111139;
        double lngMeters = lngDiff * 111139 * Math.cos(Math.toRadians(p1.lat()));

        return Math.sqrt(latMeters * latMeters + lngMeters * lngMeters);
    }

    // We need to adjust the move logic to handle the coordinate update correctly based on meters.
    // The previous logic:
    // double fraction = distanceToTravel / distToTarget;
    // This fraction is correct regardless of units, as long as distanceToTravel and distToTarget are in the same units.
    // So if calculateDistance returns meters, and distanceToTravel is meters, fraction is correct.
    // Then we apply fraction to the lat/lng difference.

    public String getVehicleId() {
        return vehicleId;
    }

    public GeoPoint getCurrentLocation() {
        return currentLocation;
    }

    public double getSpeed() {
        return speed;
    }

    public SimulationStatus getStatus() {
        return status;
    }

    public Route getRoute() {
        return route;
    }
}

