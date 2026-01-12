package pse.nebula.vehicleservice.infrastructure.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.transaction.support.TransactionTemplate;
import pse.nebula.vehicleservice.domain.model.*;
import pse.nebula.vehicleservice.domain.port.*;

import java.math.BigDecimal;
import java.util.List;

/**
 * Seeds the database with initial test data.
 * Only runs when the database is empty.
 * Disabled for test profile.
 */
@Configuration
@Profile("!test")
public class DataSeeder {

    private static final Logger logger = LoggerFactory.getLogger(DataSeeder.class);

    @Bean
    CommandLineRunner seedDatabase(
            VehicleRepository vehicleRepository,
            PaintRepository paintRepository,
            RimRepository rimRepository,
            InteriorRepository interiorRepository,
            TransactionTemplate transactionTemplate) {

        return args -> {
            // Only seed if database is empty (SQL init script may have already populated data)
            boolean hasVehicles = !vehicleRepository.findAll().isEmpty();
            boolean hasPaints = !paintRepository.findAll().isEmpty();

            if (hasVehicles && hasPaints) {
                logger.info("Database already contains data (from SQL init script), skipping Java seeder.");
                return;
            }

            if (!hasVehicles && !hasPaints) {
                logger.info("Seeding database with initial data...");

                // Wrap all seeding operations in a single transaction
                transactionTemplate.executeWithoutResult(status -> {
                    seedVehicles(vehicleRepository);
                    seedPaints(paintRepository);
                    seedRims(rimRepository);
                    seedInteriors(interiorRepository);
                });

                logger.info("Database seeding completed successfully!");
            } else {
                logger.warn("Database in inconsistent state - partial data exists. Manual intervention may be needed.");
            }
        };
    }

    private void seedVehicles(VehicleRepository repository) {
        List<Vehicle> vehicles = List.of(
                // SPORTS
                new Vehicle("911 Carrera", CarType.SPORTS, 379, new BigDecimal("106100.00"), "911-carrera-hero"),
                new Vehicle("718 Cayman", CarType.SPORTS, 300, new BigDecimal("63400.00"), "718-cayman-hero"),

                // SEDAN
                new Vehicle("Panamera", CarType.SEDAN, 325, new BigDecimal("92400.00"), "panamera-hero"),
                new Vehicle("Taycan", CarType.SEDAN, 402, new BigDecimal("86700.00"), "taycan-hero"),

                // SUV
                new Vehicle("Cayenne", CarType.SUV, 348, new BigDecimal("76300.00"), "cayenne-hero"),
                new Vehicle("Macan", CarType.SUV, 261, new BigDecimal("60900.00"), "macan-hero"),

                // LUXURY_COUPE
                new Vehicle("911 Targa 4S", CarType.LUXURY_COUPE, 443, new BigDecimal("137400.00"), "911-targa-hero"),
                new Vehicle("Panamera GTS", CarType.LUXURY_COUPE, 473, new BigDecimal("132900.00"), "panamera-gts-hero"),

                // SUPERCAR
                new Vehicle("911 GT3 RS", CarType.SUPERCAR, 518, new BigDecimal("229900.00"), "911-gt3rs-hero"),
                new Vehicle("918 Spyder", CarType.SUPERCAR, 887, new BigDecimal("845000.00"), "918-spyder-hero")
        );

        vehicles.forEach(repository::save);
        logger.info("Seeded {} vehicles", vehicles.size());
    }

    private void seedPaints(PaintRepository repository) {
        // Fixed paint list as specified
        List<PaintData> paintsData = List.of(
                new PaintData("Black", "Timeless deep black metallic finish"),
                new PaintData("Alpine White", "Pure brilliant white solid finish"),
                new PaintData("Jamaica Blue", "Vibrant tropical blue metallic"),
                new PaintData("Rallye Red", "Bold racing-inspired red"),
                new PaintData("Midnight Blue", "Deep elegant dark blue metallic"),
                new PaintData("Lime Met Green", "Eye-catching lime green metallic"),
                new PaintData("Scorch Red", "Intense fiery red metallic"),
                new PaintData("Panther Pink", "Distinctive bold pink finish"),
                new PaintData("Orange", "Vibrant pure orange solid"),
                new PaintData("Green Go / Sassy Green", "Classic heritage green")
        );

        for (PaintData data : paintsData) {
            Paint paint = new Paint(data.name(), data.description());

            // Add prices for each car type
            paint.addPrice(new PaintPrice(CarType.SPORTS, data.priceForType(CarType.SPORTS)));
            paint.addPrice(new PaintPrice(CarType.SEDAN, data.priceForType(CarType.SEDAN)));
            paint.addPrice(new PaintPrice(CarType.SUV, data.priceForType(CarType.SUV)));
            paint.addPrice(new PaintPrice(CarType.LUXURY_COUPE, data.priceForType(CarType.LUXURY_COUPE)));
            paint.addPrice(new PaintPrice(CarType.SUPERCAR, data.priceForType(CarType.SUPERCAR)));

            repository.save(paint);
        }

        logger.info("Seeded {} paints with prices for all car types", paintsData.size());
    }

    private void seedRims(RimRepository repository) {
        // Placeholder rim options
        List<RimData> rimsData = List.of(
                new RimData("19\" Base Alloy", "Standard 19-inch alloy wheels", "rim-19-base"),
                new RimData("20\" Sport Alloy", "Sporty 20-inch lightweight alloy", "rim-20-sport"),
                new RimData("21\" Turbo Design", "Premium 21-inch turbo-style wheels", "rim-21-turbo"),
                new RimData("20\" RS Spyder", "Racing-inspired RS Spyder design", "rim-20-rs-spyder"),
                new RimData("21\" Exclusive Design", "Exclusive luxury finish wheels", "rim-21-exclusive")
        );

        for (RimData data : rimsData) {
            Rim rim = new Rim(data.name(), data.description(), data.image());

            // Add prices for each car type
            rim.addPrice(new RimPrice(CarType.SPORTS, data.priceForType(CarType.SPORTS)));
            rim.addPrice(new RimPrice(CarType.SEDAN, data.priceForType(CarType.SEDAN)));
            rim.addPrice(new RimPrice(CarType.SUV, data.priceForType(CarType.SUV)));
            rim.addPrice(new RimPrice(CarType.LUXURY_COUPE, data.priceForType(CarType.LUXURY_COUPE)));
            rim.addPrice(new RimPrice(CarType.SUPERCAR, data.priceForType(CarType.SUPERCAR)));

            repository.save(rim);
        }

        logger.info("Seeded {} rims with prices for all car types", rimsData.size());
    }

    private void seedInteriors(InteriorRepository repository) {
        // Placeholder interior options
        List<InteriorData> interiorsData = List.of(
                new InteriorData("Black Leather", "Classic black leather upholstery", "interior-black-leather"),
                new InteriorData("Espresso Brown", "Warm espresso brown leather", "interior-espresso"),
                new InteriorData("Bordeaux Red", "Luxurious bordeaux red leather", "interior-bordeaux"),
                new InteriorData("Chalk Beige", "Elegant chalk beige leather", "interior-chalk"),
                new InteriorData("Two-Tone Black/Red", "Sporty two-tone combination", "interior-two-tone")
        );

        for (InteriorData data : interiorsData) {
            Interior interior = new Interior(data.name(), data.description(), data.image());

            // Add prices for each car type
            interior.addPrice(new InteriorPrice(CarType.SPORTS, data.priceForType(CarType.SPORTS)));
            interior.addPrice(new InteriorPrice(CarType.SEDAN, data.priceForType(CarType.SEDAN)));
            interior.addPrice(new InteriorPrice(CarType.SUV, data.priceForType(CarType.SUV)));
            interior.addPrice(new InteriorPrice(CarType.LUXURY_COUPE, data.priceForType(CarType.LUXURY_COUPE)));
            interior.addPrice(new InteriorPrice(CarType.SUPERCAR, data.priceForType(CarType.SUPERCAR)));

            repository.save(interior);
        }

        logger.info("Seeded {} interiors with prices for all car types", interiorsData.size());
    }

    // Helper records for seed data
    private record PaintData(String name, String description) {
        BigDecimal priceForType(CarType carType) {
            // First paint (Black) is base/included for SEDAN, others have varied pricing
            if (name.equals("Black")) {
                return switch (carType) {
                    case SEDAN -> new BigDecimal("0.00");
                    case SUV -> new BigDecimal("250.00");
                    case SPORTS -> new BigDecimal("500.00");
                    case LUXURY_COUPE -> new BigDecimal("750.00");
                    case SUPERCAR -> new BigDecimal("1500.00");
                };
            }
            // Alpine White - low cost option
            if (name.equals("Alpine White")) {
                return switch (carType) {
                    case SEDAN -> new BigDecimal("0.00");
                    case SUV -> new BigDecimal("0.00");
                    case SPORTS -> new BigDecimal("750.00");
                    case LUXURY_COUPE -> new BigDecimal("1000.00");
                    case SUPERCAR -> new BigDecimal("2000.00");
                };
            }
            // Premium colors have higher prices
            return switch (carType) {
                case SEDAN -> new BigDecimal("1200.00");
                case SUV -> new BigDecimal("1500.00");
                case SPORTS -> new BigDecimal("2200.00");
                case LUXURY_COUPE -> new BigDecimal("2800.00");
                case SUPERCAR -> new BigDecimal("4500.00");
            };
        }
    }

    private record RimData(String name, String description, String image) {
        BigDecimal priceForType(CarType carType) {
            // Base rims are included
            if (name.contains("Base")) {
                return BigDecimal.ZERO;
            }
            // Sport rims - mid tier
            if (name.contains("Sport")) {
                return switch (carType) {
                    case SEDAN -> new BigDecimal("1800.00");
                    case SUV -> new BigDecimal("2100.00");
                    case SPORTS -> new BigDecimal("2500.00");
                    case LUXURY_COUPE -> new BigDecimal("3000.00");
                    case SUPERCAR -> new BigDecimal("4500.00");
                };
            }
            // Premium rims - high tier
            return switch (carType) {
                case SEDAN -> new BigDecimal("3200.00");
                case SUV -> new BigDecimal("3800.00");
                case SPORTS -> new BigDecimal("4500.00");
                case LUXURY_COUPE -> new BigDecimal("5500.00");
                case SUPERCAR -> new BigDecimal("8000.00");
            };
        }
    }

    private record InteriorData(String name, String description, String image) {
        BigDecimal priceForType(CarType carType) {
            // Black leather is base/included
            if (name.equals("Black Leather")) {
                return BigDecimal.ZERO;
            }
            // Standard leather upgrades
            if (name.contains("Brown") || name.contains("Beige")) {
                return switch (carType) {
                    case SEDAN -> new BigDecimal("2500.00");
                    case SUV -> new BigDecimal("2800.00");
                    case SPORTS -> new BigDecimal("3200.00");
                    case LUXURY_COUPE -> new BigDecimal("3800.00");
                    case SUPERCAR -> new BigDecimal("5500.00");
                };
            }
            // Premium options (Red, Two-Tone)
            return switch (carType) {
                case SEDAN -> new BigDecimal("4200.00");
                case SUV -> new BigDecimal("4800.00");
                case SPORTS -> new BigDecimal("5500.00");
                case LUXURY_COUPE -> new BigDecimal("6500.00");
                case SUPERCAR -> new BigDecimal("9500.00");
            };
        }
    }
}

