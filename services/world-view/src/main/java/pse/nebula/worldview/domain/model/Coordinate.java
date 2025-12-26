package pse.nebula.worldview.domain.model;

/**
 * Represents a geographic coordinate with latitude and longitude.
 * Immutable value object following Domain-Driven Design principles.
 */
public record Coordinate(double latitude, double longitude) {

    public Coordinate {
        if (latitude < -90 || latitude > 90) {
            throw new IllegalArgumentException("Latitude must be between -90 and 90 degrees");
        }
        if (longitude < -180 || longitude > 180) {
            throw new IllegalArgumentException("Longitude must be between -180 and 180 degrees");
        }
    }

    /**
     * Calculate the distance to another coordinate using the Haversine formula.
     *
     * @param other The target coordinate
     * @return Distance in meters
     */
    public double distanceTo(Coordinate other) {
        final double EARTH_RADIUS_METERS = 6_371_000;

        double lat1Rad = Math.toRadians(this.latitude);
        double lat2Rad = Math.toRadians(other.latitude);
        double deltaLatRad = Math.toRadians(other.latitude - this.latitude);
        double deltaLngRad = Math.toRadians(other.longitude - this.longitude);

        double a = Math.sin(deltaLatRad / 2) * Math.sin(deltaLatRad / 2) +
                   Math.cos(lat1Rad) * Math.cos(lat2Rad) *
                   Math.sin(deltaLngRad / 2) * Math.sin(deltaLngRad / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return EARTH_RADIUS_METERS * c;
    }

    /**
     * Interpolate between this coordinate and another coordinate.
     *
     * @param target The target coordinate
     * @param fraction The fraction of the distance (0.0 to 1.0)
     * @return The interpolated coordinate
     */
    public Coordinate interpolateTo(Coordinate target, double fraction) {
        if (fraction < 0 || fraction > 1) {
            throw new IllegalArgumentException("Fraction must be between 0 and 1");
        }

        double newLat = this.latitude + (target.latitude - this.latitude) * fraction;
        double newLng = this.longitude + (target.longitude - this.longitude) * fraction;

        return new Coordinate(newLat, newLng);
    }

    @Override
    public String toString() {
        return String.format("Coordinate[lat=%.6f, lng=%.6f]", latitude, longitude);
    }
}

