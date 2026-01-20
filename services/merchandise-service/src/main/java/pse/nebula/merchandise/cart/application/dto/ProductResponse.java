package pse.nebula.merchandise.cart.application.dto;

import java.math.BigDecimal;

public record ProductResponse(
                Long id,
                String name,
                String description,
                BigDecimal price,
                Integer stock,
                String imageUrl,
                String category,
                String badge,
                BigDecimal rating,
                Integer reviews) {
}
