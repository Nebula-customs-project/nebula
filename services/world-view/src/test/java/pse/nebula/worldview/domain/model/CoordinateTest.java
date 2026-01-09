package pse.nebula.worldview.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Coordinate Tests")
class CoordinateTest {

    @Nested
    @DisplayName("Constructor Validation Tests")
    class ConstructorValidationTests {

        @Test
        @DisplayName("Should create valid coordinate")
        void shouldCreateValidCoordinate() {
            Coordinate coord = new Coordinate(48.8354, 9.1520);

            assertEquals(48.8354, coord.latitude());
            assertEquals(9.1520, coord.longitude());
        }

        @Test
        @DisplayName("Should accept boundary latitude values")
        void shouldAcceptBoundaryLatitudeValues() {
            assertDoesNotThrow(() -> new Coordinate(90, 0));
            assertDoesNotThrow(() -> new Coordinate(-90, 0));
            assertDoesNotThrow(() -> new Coordinate(0, 0));
        }

        @Test
        @DisplayName("Should accept boundary longitude values")
        void shouldAcceptBoundaryLongitudeValues() {
            assertDoesNotThrow(() -> new Coordinate(0, 180));
            assertDoesNotThrow(() -> new Coordinate(0, -180));
        }

        @Test
        @DisplayName("Should reject latitude above 90")
        void shouldRejectLatitudeAbove90() {
            assertThrows(IllegalArgumentException.class, () ->
                new Coordinate(91, 0));
        }

        @Test
        @DisplayName("Should reject latitude below -90")
        void shouldRejectLatitudeBelow90() {
            assertThrows(IllegalArgumentException.class, () ->
                new Coordinate(-91, 0));
        }

        @Test
        @DisplayName("Should reject longitude above 180")
        void shouldRejectLongitudeAbove180() {
            assertThrows(IllegalArgumentException.class, () ->
                new Coordinate(0, 181));
        }

        @Test
        @DisplayName("Should reject longitude below -180")
        void shouldRejectLongitudeBelowMinus180() {
            assertThrows(IllegalArgumentException.class, () ->
                new Coordinate(0, -181));
        }

        @Test
        @DisplayName("Should reject NaN latitude")
        void shouldRejectNaNLatitude() {
            assertThrows(IllegalArgumentException.class, () ->
                new Coordinate(Double.NaN, 0));
        }

        @Test
        @DisplayName("Should reject NaN longitude")
        void shouldRejectNaNLongitude() {
            assertThrows(IllegalArgumentException.class, () ->
                new Coordinate(0, Double.NaN));
        }

        @Test
        @DisplayName("Should reject infinite latitude")
        void shouldRejectInfiniteLatitude() {
            assertThrows(IllegalArgumentException.class, () ->
                new Coordinate(Double.POSITIVE_INFINITY, 0));
            assertThrows(IllegalArgumentException.class, () ->
                new Coordinate(Double.NEGATIVE_INFINITY, 0));
        }

        @Test
        @DisplayName("Should reject infinite longitude")
        void shouldRejectInfiniteLongitude() {
            assertThrows(IllegalArgumentException.class, () ->
                new Coordinate(0, Double.POSITIVE_INFINITY));
            assertThrows(IllegalArgumentException.class, () ->
                new Coordinate(0, Double.NEGATIVE_INFINITY));
        }
    }

    @Nested
    @DisplayName("distanceTo() Tests")
    class DistanceToTests {

        @Test
        @DisplayName("Should calculate distance correctly")
        void shouldCalculateDistance() {
            // Ludwigsburg Schloss to Dealership
            Coordinate start = new Coordinate(48.8973, 9.1920);
            Coordinate end = new Coordinate(48.8354, 9.1520);

            double distance = start.distanceTo(end);

            // Approximately 7-8 km straight line distance
            assertTrue(distance > 5000 && distance < 10000,
                "Distance should be roughly 7km, was: " + distance);
        }

        @Test
        @DisplayName("Should return zero for same coordinate")
        void shouldReturnZeroForSameCoordinate() {
            Coordinate coord = new Coordinate(48.8354, 9.1520);

            double distance = coord.distanceTo(coord);

            assertEquals(0, distance, 0.001);
        }

        @Test
        @DisplayName("Should throw exception for null target")
        void shouldThrowExceptionForNullTarget() {
            Coordinate coord = new Coordinate(48.8354, 9.1520);

            assertThrows(IllegalArgumentException.class, () ->
                coord.distanceTo(null));
        }

        @Test
        @DisplayName("Distance should be symmetric")
        void distanceShouldBeSymmetric() {
            Coordinate a = new Coordinate(48.8973, 9.1920);
            Coordinate b = new Coordinate(48.8354, 9.1520);

            assertEquals(a.distanceTo(b), b.distanceTo(a), 0.001);
        }
    }

    @Nested
    @DisplayName("interpolateTo() Tests")
    class InterpolateToTests {

        @Test
        @DisplayName("Should interpolate between coordinates")
        void shouldInterpolateBetweenCoordinates() {
            Coordinate start = new Coordinate(48.8, 9.0);
            Coordinate end = new Coordinate(49.0, 10.0);

            Coordinate midpoint = start.interpolateTo(end, 0.5);

            assertEquals(48.9, midpoint.latitude(), 0.0001);
            assertEquals(9.5, midpoint.longitude(), 0.0001);
        }

        @Test
        @DisplayName("Interpolate with zero fraction returns start")
        void interpolateWithZeroFractionReturnsStart() {
            Coordinate start = new Coordinate(48.8, 9.0);
            Coordinate end = new Coordinate(49.0, 10.0);

            Coordinate result = start.interpolateTo(end, 0);

            assertEquals(start.latitude(), result.latitude(), 0.0001);
            assertEquals(start.longitude(), result.longitude(), 0.0001);
        }

        @Test
        @DisplayName("Interpolate with one fraction returns end")
        void interpolateWithOneFractionReturnsEnd() {
            Coordinate start = new Coordinate(48.8, 9.0);
            Coordinate end = new Coordinate(49.0, 10.0);

            Coordinate result = start.interpolateTo(end, 1);

            assertEquals(end.latitude(), result.latitude(), 0.0001);
            assertEquals(end.longitude(), result.longitude(), 0.0001);
        }

        @Test
        @DisplayName("Should throw exception for null target")
        void shouldThrowExceptionForNullTarget() {
            Coordinate coord = new Coordinate(48.8354, 9.1520);

            assertThrows(IllegalArgumentException.class, () ->
                coord.interpolateTo(null, 0.5));
        }

        @Test
        @DisplayName("Should throw exception for negative fraction")
        void shouldThrowExceptionForNegativeFraction() {
            Coordinate start = new Coordinate(48.8, 9.0);
            Coordinate end = new Coordinate(49.0, 10.0);

            assertThrows(IllegalArgumentException.class, () ->
                start.interpolateTo(end, -0.1));
        }

        @Test
        @DisplayName("Should throw exception for fraction greater than 1")
        void shouldThrowExceptionForFractionGreaterThan1() {
            Coordinate start = new Coordinate(48.8, 9.0);
            Coordinate end = new Coordinate(49.0, 10.0);

            assertThrows(IllegalArgumentException.class, () ->
                start.interpolateTo(end, 1.1));
        }

        @Test
        @DisplayName("Should throw exception for NaN fraction")
        void shouldThrowExceptionForNaNFraction() {
            Coordinate start = new Coordinate(48.8, 9.0);
            Coordinate end = new Coordinate(49.0, 10.0);

            assertThrows(IllegalArgumentException.class, () ->
                start.interpolateTo(end, Double.NaN));
        }

        @Test
        @DisplayName("Should interpolate at 25%")
        void shouldInterpolateAt25Percent() {
            Coordinate start = new Coordinate(0, 0);
            Coordinate end = new Coordinate(40, 80);

            Coordinate result = start.interpolateTo(end, 0.25);

            assertEquals(10, result.latitude(), 0.0001);
            assertEquals(20, result.longitude(), 0.0001);
        }
    }

    @Nested
    @DisplayName("toString() Tests")
    class ToStringTests {

        @Test
        @DisplayName("Should format toString correctly")
        void shouldFormatToStringCorrectly() {
            Coordinate coord = new Coordinate(48.8354, 9.1520);

            String result = coord.toString();

            assertNotNull(result);
            assertTrue(result.contains("48.8354") || result.contains("48,8354"));
            assertTrue(result.contains("9.152") || result.contains("9,152"));
        }
    }

    @Nested
    @DisplayName("Equals and HashCode Tests")
    class EqualsAndHashCodeTests {

        @Test
        @DisplayName("Equal coordinates should be equal")
        void equalCoordinatesShouldBeEqual() {
            Coordinate a = new Coordinate(48.8354, 9.1520);
            Coordinate b = new Coordinate(48.8354, 9.1520);

            assertEquals(a, b);
            assertEquals(a.hashCode(), b.hashCode());
        }

        @Test
        @DisplayName("Different coordinates should not be equal")
        void differentCoordinatesShouldNotBeEqual() {
            Coordinate a = new Coordinate(48.8354, 9.1520);
            Coordinate b = new Coordinate(48.8355, 9.1520);

            assertNotEquals(a, b);
        }
    }
}