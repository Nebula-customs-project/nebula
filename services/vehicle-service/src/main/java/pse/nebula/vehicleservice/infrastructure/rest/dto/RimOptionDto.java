package pse.nebula.vehicleservice.infrastructure.rest.dto;

import pse.nebula.vehicleservice.domain.model.CarType;
import pse.nebula.vehicleservice.domain.model.Rim;
import pse.nebula.vehicleservice.domain.model.RimPrice;

import java.math.BigDecimal;

/**
 * DTO for rim option with resolved price.
 */
public record RimOptionDto(
        Integer id,
        String name,
        String description,
        String image,
        BigDecimal price
) {
    /**
     * Creates a RimOptionDto from a Rim entity.
     * Filters prices by the specified car type.
     */
    public static RimOptionDto fromEntity(Rim rim, CarType carType) {
        BigDecimal resolvedPrice = rim.getPrices().stream()
                .filter(p -> p.getCarType() == carType)
                .findFirst()
                .map(RimPrice::getPrice)
                .orElse(BigDecimal.ZERO);

        return new RimOptionDto(
                rim.getId(),
                rim.getName(),
                rim.getDescription(),
                rim.getImage(),
                resolvedPrice
        );
    }

    /**
     * Creates a RimOptionDto from a Rim entity (uses first price found).
     * @deprecated Use fromEntity(Rim, CarType) instead
     */
    public static RimOptionDto fromEntity(Rim rim) {
        BigDecimal resolvedPrice = rim.getPrices().stream()
                .findFirst()
                .map(RimPrice::getPrice)
                .orElse(BigDecimal.ZERO);

        return new RimOptionDto(
                rim.getId(),
                rim.getName(),
                rim.getDescription(),
                rim.getImage(),
                resolvedPrice
        );
    }
}

