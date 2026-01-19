package pse.nebula.merchandise.infrastructure.config;

import java.math.BigDecimal;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.transaction.support.TransactionTemplate;

import pse.nebula.merchandise.domain.model.Product;
import pse.nebula.merchandise.domain.repository.ProductRepository;

/**
 * Seeds the merchandise database with initial product data when empty.
 */
@Configuration
@Profile("!test")
public class DataSeeder {

    private static final Logger logger = LoggerFactory.getLogger(DataSeeder.class);

    @Bean
    CommandLineRunner seedDatabase(ProductRepository productRepository, TransactionTemplate transactionTemplate) {
        return args -> {
            transactionTemplate.executeWithoutResult(status -> {
            boolean hasProducts = productRepository.count() > 0;

            if (hasProducts) {
                logger.info("Merchandise DB already contains products, skipping seeder.");
                return;
            }

            logger.info("Seeding merchandise products...");

                List<Product> products = List.of(
                Product.builder()
                    .name("Nebula Racing Cap")
                    .description("Premium cap")
                    .price(new BigDecimal("35.00"))
                    .stock(100)
                    .imageUrl("https://images.unsplash.com/photo-1588850561407-ed78c282e89b?w=400")
                    .category("Apparel")
                    .badge("Bestseller")
                    .rating(new BigDecimal("4.8"))
                    .reviews(124)
                    .build(),
                Product.builder()
                    .name("Team Jacket")
                    .description("Warm team jacket")
                    .price(new BigDecimal("129.00"))
                    .stock(80)
                    .imageUrl("https://images.unsplash.com/photo-1551028719-00167b16eac5?w=400")
                    .category("Apparel")
                    .badge("Premium")
                    .rating(new BigDecimal("4.9"))
                    .reviews(89)
                    .build(),
                Product.builder()
                    .name("Car Model 1:18 Apex")
                    .description("Detailed scale model")
                    .price(new BigDecimal("89.00"))
                    .stock(60)
                    .imageUrl("https://images.unsplash.com/photo-1581235720704-06d3acfcb36f?w=400")
                    .category("Models")
                    .badge("Limited")
                    .rating(new BigDecimal("5.0"))
                    .reviews(203)
                    .build(),
                Product.builder()
                    .name("Premium Keychain")
                    .description("Premium metal keychain")
                    .price(new BigDecimal("25.00"))
                    .stock(200)
                    .imageUrl("https://unsplash.com/photos/hbCqHtYeUZ8/download?force=true&w=400")
                    .category("Accessories")
                    .badge("Premium")
                    .rating(new BigDecimal("4.6"))
                    .reviews(312)
                    .build(),
                Product.builder()
                    .name("Carbon Fiber Wallet")
                    .description("Slim carbon fiber wallet")
                    .price(new BigDecimal("78.00"))
                    .stock(120)
                    .imageUrl("https://images.unsplash.com/photo-1627123424574-724758594e93?w=400")
                    .category("Accessories")
                    .badge("New")
                    .rating(new BigDecimal("4.7"))
                    .reviews(156)
                    .build(),
                Product.builder()
                    .name("Racing Gloves")
                    .description("High grip racing gloves")
                    .price(new BigDecimal("65.00"))
                    .stock(90)
                    .imageUrl("https://unsplash.com/photos/5fnmt6S7y4o/download?force=true&w=400")
                    .category("Apparel")
                    .badge(null)
                    .rating(new BigDecimal("4.8"))
                    .reviews(98)
                    .build(),
                Product.builder()
                    .name("Nebula Coffee Mug Set")
                    .description("Ceramic mug set")
                    .price(new BigDecimal("32.00"))
                    .stock(150)
                    .imageUrl("https://images.unsplash.com/photo-1514228742587-6b1558fcca3d?w=400")
                    .category("Lifestyle")
                    .badge(null)
                    .rating(new BigDecimal("4.5"))
                    .reviews(267)
                    .build(),
                Product.builder()
                    .name("Performance T-Shirt")
                    .description("Breathable performance tee")
                    .price(new BigDecimal("45.00"))
                    .stock(140)
                    .imageUrl("https://images.unsplash.com/photo-1521572163474-6864f9cf17ab?w=400")
                    .category("Apparel")
                    .badge("Bestseller")
                    .rating(new BigDecimal("4.7"))
                    .reviews(189)
                    .build(),
                Product.builder()
                    .name("Leather Driving Shoes")
                    .description("Comfort driving shoes")
                    .price(new BigDecimal("145.00"))
                    .stock(70)
                    .imageUrl("https://images.unsplash.com/photo-1549298916-b41d501d3772?w=400")
                    .category("Apparel")
                    .badge("Premium")
                    .rating(new BigDecimal("4.9"))
                    .reviews(76)
                    .build(),
                Product.builder()
                    .name("Backpack - Velocity Series")
                    .description("Durable commuter backpack")
                    .price(new BigDecimal("95.00"))
                    .stock(110)
                    .imageUrl("https://images.unsplash.com/photo-1553062407-98eeb64c6a62?w=400")
                    .category("Accessories")
                    .badge("New")
                    .rating(new BigDecimal("4.8"))
                    .reviews(142)
                    .build(),
                Product.builder()
                    .name("Watch - Limited Edition")
                    .description("Chronograph watch")
                    .price(new BigDecimal("299.00"))
                    .stock(40)
                    .imageUrl("https://images.unsplash.com/photo-1523275335684-37898b6baf30?w=400")
                    .category("Accessories")
                    .badge("Limited")
                    .rating(new BigDecimal("5.0"))
                    .reviews(54)
                    .build(),
                Product.builder()
                    .name("Sunglasses - Sport")
                    .description("Polarized sport sunglasses")
                    .price(new BigDecimal("89.00"))
                    .stock(130)
                    .imageUrl("https://images.unsplash.com/photo-1572635196237-14b3f281503f?w=400")
                    .category("Accessories")
                    .badge(null)
                    .rating(new BigDecimal("4.6"))
                    .reviews(223)
                    .build()
                );

                productRepository.saveAll(products);
                logger.info("Seeded {} merchandise products", products.size());
            });
        };
    }
}
