package pse.nebula.vehicleservice.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Domain Model Tests")
class DomainModelTest {

    @Test
    @DisplayName("Vehicle should store all properties correctly")
    void vehicleShouldStoreProperties() {
        // Given/When
        Vehicle vehicle = new Vehicle(
                "911 Carrera",
                CarType.SPORTS,
                379,
                new BigDecimal("106100.00"),
                "911-carrera-hero"
        );

        // Then
        assertThat(vehicle.getCarName()).isEqualTo("911 Carrera");
        assertThat(vehicle.getCarType()).isEqualTo(CarType.SPORTS);
        assertThat(vehicle.getHorsePower()).isEqualTo(379);
        assertThat(vehicle.getBasePrice()).isEqualByComparingTo(new BigDecimal("106100.00"));
        assertThat(vehicle.getImage()).isEqualTo("911-carrera-hero");
    }

    @Test
    @DisplayName("Paint should allow adding prices")
    void paintShouldAllowAddingPrices() {
        // Given
        Paint paint = new Paint("Black", "Timeless black finish");
        PaintPrice sportsPrice = new PaintPrice(CarType.SPORTS, new BigDecimal("500.00"));
        PaintPrice sedanPrice = new PaintPrice(CarType.SEDAN, BigDecimal.ZERO);

        // When
        paint.addPrice(sportsPrice);
        paint.addPrice(sedanPrice);

        // Then
        assertThat(paint.getPrices()).hasSize(2);
        assertThat(paint.getPrices()).contains(sportsPrice, sedanPrice);
    }

    @Test
    @DisplayName("Rim should allow adding prices")
    void rimShouldAllowAddingPrices() {
        // Given
        Rim rim = new Rim("Sport Alloy", "Sporty wheels", "rim-sport");
        RimPrice price = new RimPrice(CarType.SPORTS, new BigDecimal("2500.00"));

        // When
        rim.addPrice(price);

        // Then
        assertThat(rim.getPrices()).hasSize(1);
        assertThat(rim.getPrices().get(0).getPrice()).isEqualByComparingTo(new BigDecimal("2500.00"));
    }

    @Test
    @DisplayName("Interior should allow adding prices")
    void interiorShouldAllowAddingPrices() {
        // Given
        Interior interior = new Interior("Black Leather", "Premium leather", "interior-black");
        InteriorPrice price = new InteriorPrice(CarType.LUXURY_COUPE, new BigDecimal("3800.00"));

        // When
        interior.addPrice(price);

        // Then
        assertThat(interior.getPrices()).hasSize(1);
        assertThat(interior.getPrices().get(0).getCarType()).isEqualTo(CarType.LUXURY_COUPE);
    }

    @Test
    @DisplayName("CarType enum should have all expected values")
    void carTypeShouldHaveAllValues() {
        // When
        CarType[] values = CarType.values();

        // Then
        assertThat(values).containsExactlyInAnyOrder(
                CarType.SPORTS,
                CarType.SEDAN,
                CarType.SUV,
                CarType.LUXURY_COUPE,
                CarType.SUPERCAR
        );
    }
}

