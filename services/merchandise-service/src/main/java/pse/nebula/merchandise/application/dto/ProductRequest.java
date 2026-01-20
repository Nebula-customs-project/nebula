package pse.nebula.merchandise.application.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record ProductRequest(
        @NotBlank String name,

        String description,

        @NotNull @Positive BigDecimal price,

        @NotNull @Min(0) Integer stock,

        String imageUrl,

        String category,

        String badge,

        @DecimalMin(value = "1.0") @DecimalMax(value = "5.0") BigDecimal rating,

        @Min(0) Integer reviews) {
}
