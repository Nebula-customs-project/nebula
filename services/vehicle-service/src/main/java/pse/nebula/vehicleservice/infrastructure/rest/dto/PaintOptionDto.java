package pse.nebula.vehicleservice.infrastructure.rest.dto;

import pse.nebula.vehicleservice.domain.model.CarType;
import pse.nebula.vehicleservice.domain.model.Paint;
import pse.nebula.vehicleservice.domain.model.PaintPrice;

import java.math.BigDecimal;

/**
 * DTO for paint option with resolved price.
 */
public record PaintOptionDto(
        Integer id,
        String name,
        String description,
        BigDecimal price
) {
    /**
     * Creates a PaintOptionDto from a Paint entity.
     * Filters prices by the specified car type.
     */
    public static PaintOptionDto fromEntity(Paint paint, CarType carType) {
        BigDecimal resolvedPrice = paint.getPrices().stream()
                .filter(p -> p.getCarType() == carType)
                .findFirst()
                .map(PaintPrice::getPrice)
                .orElse(BigDecimal.ZERO);

        return new PaintOptionDto(
                paint.getId(),
                paint.getName(),
                paint.getDescription(),
                resolvedPrice
        );
    }

    /**
     * Creates a PaintOptionDto from a Paint entity (uses first price found).
     * @deprecated Use fromEntity(Paint, CarType) instead
     */
    public static PaintOptionDto fromEntity(Paint paint) {
        BigDecimal resolvedPrice = paint.getPrices().stream()
                .findFirst()
                .map(PaintPrice::getPrice)
                .orElse(BigDecimal.ZERO);

        return new PaintOptionDto(
                paint.getId(),
                paint.getName(),
                paint.getDescription(),
                resolvedPrice
        );
    }
}

