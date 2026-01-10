package pse.nebula.vehicleservice.infrastructure.rest.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import pse.nebula.vehicleservice.domain.model.*;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("DTO Mapping Tests")
class DtoMappingTest {

    @Test
    @DisplayName("VehicleDto should map from Vehicle entity")
    void vehicleDtoShouldMapFromEntity() {
        // Given
        Vehicle vehicle = new Vehicle(
                "911 Carrera",
                CarType.SPORTS,
                379,
                new BigDecimal("106100.00"),
                "911-carrera-hero"
        );

        // When
        VehicleDto dto = VehicleDto.fromEntity(vehicle);

        // Then
        assertThat(dto.carName()).isEqualTo("911 Carrera");
        assertThat(dto.carType()).isEqualTo(CarType.SPORTS);
        assertThat(dto.horsePower()).isEqualTo(379);
        assertThat(dto.basePrice()).isEqualByComparingTo(new BigDecimal("106100.00"));
        assertThat(dto.image()).isEqualTo("911-carrera-hero");
    }

    @Test
    @DisplayName("PaintOptionDto should map from Paint entity with resolved price")
    void paintOptionDtoShouldMapWithPrice() {
        // Given
        Paint paint = new Paint("Black", "Timeless black finish");
        paint.addPrice(new PaintPrice(CarType.SPORTS, new BigDecimal("500.00")));

        // When
        PaintOptionDto dto = PaintOptionDto.fromEntity(paint);

        // Then
        assertThat(dto.name()).isEqualTo("Black");
        assertThat(dto.description()).isEqualTo("Timeless black finish");
        assertThat(dto.price()).isEqualByComparingTo(new BigDecimal("500.00"));
    }

    @Test
    @DisplayName("PaintOptionDto should return zero price when no prices available")
    void paintOptionDtoShouldReturnZeroWhenNoPrices() {
        // Given
        Paint paint = new Paint("Empty", "No prices");

        // When
        PaintOptionDto dto = PaintOptionDto.fromEntity(paint);

        // Then
        assertThat(dto.price()).isEqualByComparingTo(BigDecimal.ZERO);
    }

    @Test
    @DisplayName("RimOptionDto should map from Rim entity with resolved price")
    void rimOptionDtoShouldMapWithPrice() {
        // Given
        Rim rim = new Rim("Sport Alloy", "Sporty wheels", "rim-sport");
        rim.addPrice(new RimPrice(CarType.SPORTS, new BigDecimal("2500.00")));

        // When
        RimOptionDto dto = RimOptionDto.fromEntity(rim);

        // Then
        assertThat(dto.name()).isEqualTo("Sport Alloy");
        assertThat(dto.description()).isEqualTo("Sporty wheels");
        assertThat(dto.image()).isEqualTo("rim-sport");
        assertThat(dto.price()).isEqualByComparingTo(new BigDecimal("2500.00"));
    }

    @Test
    @DisplayName("InteriorOptionDto should map from Interior entity with resolved price")
    void interiorOptionDtoShouldMapWithPrice() {
        // Given
        Interior interior = new Interior("Black Leather", "Premium leather", "interior-black");
        interior.addPrice(new InteriorPrice(CarType.SPORTS, new BigDecimal("3200.00")));

        // When
        InteriorOptionDto dto = InteriorOptionDto.fromEntity(interior);

        // Then
        assertThat(dto.name()).isEqualTo("Black Leather");
        assertThat(dto.description()).isEqualTo("Premium leather");
        assertThat(dto.image()).isEqualTo("interior-black");
        assertThat(dto.price()).isEqualByComparingTo(new BigDecimal("3200.00"));
    }

    @Test
    @DisplayName("ErrorResponse should create with all fields")
    void errorResponseShouldCreateWithAllFields() {
        // When
        ErrorResponse error = ErrorResponse.of(404, "Not Found", "Vehicle not found", "/api/vehicles/999");

        // Then
        assertThat(error.status()).isEqualTo(404);
        assertThat(error.error()).isEqualTo("Not Found");
        assertThat(error.message()).isEqualTo("Vehicle not found");
        assertThat(error.path()).isEqualTo("/api/vehicles/999");
        assertThat(error.timestamp()).isNotNull();
    }
}

