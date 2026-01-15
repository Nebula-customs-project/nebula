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
                new Vehicle("Furari", CarType.SPORTS, 670, new BigDecimal("245000.00"), "furarri-hero", "/models/furarri.glb"),
                new Vehicle("GTR", CarType.SPORTS, 565, new BigDecimal("280000.00"), "gtr-hero", "/models/GTR.glb"),
                new Vehicle("Infernus", CarType.SPORTS, 580, new BigDecimal("320000.00"), "infernus-hero", "/models/Infernus.glb"),

                // SEDAN
                new Vehicle("Dacia", CarType.SEDAN, 150, new BigDecimal("22000.00"), "dacia-hero", "/models/Dacia.glb"),
                new Vehicle("Gauntlet", CarType.SEDAN, 425, new BigDecimal("75000.00"), "gauntlet-hero", "/models/Gauntlet.glb"),

                // SUV
                new Vehicle("Deviant", CarType.SUV, 380, new BigDecimal("85000.00"), "deviant-hero", "/models/Deviant.glb"),

                // LUXURY_COUPE
                new Vehicle("Deluxo", CarType.LUXURY_COUPE, 320, new BigDecimal("150000.00"), "deluxo-hero", "/models/Deluxo.glb"),
                new Vehicle("P-911", CarType.LUXURY_COUPE, 443, new BigDecimal("175000.00"), "p911-hero", "/models/P-911.glb"),
                new Vehicle("Roadstar", CarType.LUXURY_COUPE, 400, new BigDecimal("165000.00"), "roadstar-hero", "/models/Roadstar.glb"),

                // SUPERCAR
                new Vehicle("Impaler", CarType.SUPERCAR, 750, new BigDecimal("450000.00"), "impaler-hero", "/models/Impaler.glb"),
                new Vehicle("NFS Car", CarType.SUPERCAR, 820, new BigDecimal("520000.00"), "nfs-hero", "/models/NFS_car.glb"),
                new Vehicle("Tarzan Wonder", CarType.SUPERCAR, 900, new BigDecimal("680000.00"), "tarzan-hero", "/models/TarzanWonderCar.glb")
        );

        vehicles.forEach(repository::save);
        logger.info("Seeded {} vehicles", vehicles.size());
    }

    private void seedPaints(PaintRepository repository) {
        // Fixed paint list as specified with visualKey and hex for frontend 3D rendering
        List<PaintData> paintsData = List.of(
                new PaintData("Black", "Timeless deep black metallic finish", "black", "#000000"),
                new PaintData("Alpine White", "Pure brilliant white solid finish", "alpine-white", "#FFFFFF"),
                new PaintData("Jamaica Blue", "Vibrant tropical blue metallic", "jamaica-blue", "#1E40AF"),
                new PaintData("Rallye Red", "Bold racing-inspired red", "rallye-red", "#DC2626"),
                new PaintData("Midnight Blue", "Deep elegant dark blue metallic", "midnight-blue", "#1E3A8A"),
                new PaintData("Lime Met Green", "Eye-catching lime green metallic", "lime-met-green", "#84CC16"),
                new PaintData("Scorch Red", "Intense fiery red metallic", "scorch-red", "#B91C1C"),
                new PaintData("Panther Pink", "Distinctive bold pink finish", "panther-pink", "#EC4899"),
                new PaintData("Orange", "Vibrant pure orange solid", "orange", "#F97316"),
                new PaintData("Green Go / Sassy Green", "Classic heritage green", "green-go", "#16A34A")
        );

        for (PaintData data : paintsData) {
            Paint paint = new Paint(data.name(), data.description(), data.visualKey(), data.hex());

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
        // Rim options with visualKey for frontend 3D rendering
        List<RimData> rimsData = List.of(
                new RimData("Sport 19\"", "Standard 19-inch sport alloy wheels", "rim-19-base", "sport"),
                new RimData("Performance 20\"", "High-performance 20-inch lightweight alloy", "rim-20-sport", "performance"),
                new RimData("Premium 21\"", "Premium 21-inch turbo-style wheels", "rim-21-turbo", "premium"),
                new RimData("Racing Pro", "Racing-inspired RS Spyder design", "rim-20-rs-spyder", "racing-pro"),
                new RimData("Exclusive 21\"", "Exclusive luxury finish wheels", "rim-21-exclusive", "exclusive")
        );

        for (RimData data : rimsData) {
            Rim rim = new Rim(data.name(), data.description(), data.image(), data.visualKey());

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
        // Interior options with visualKey for frontend 3D rendering
        List<InteriorData> interiorsData = List.of(
                new InteriorData("Black Leather", "Classic black leather upholstery", "interior-black-leather", "black"),
                new InteriorData("Beige Leather", "Luxury beige leather upholstery", "interior-beige", "beige"),
                new InteriorData("Red Sport", "Sporty red leather with carbon accents", "interior-red-sport", "red"),
                new InteriorData("Carbon Fiber", "Carbon fiber racing seats", "interior-carbon", "carbon"),
                new InteriorData("Two-Tone Black/Red", "Sporty two-tone combination", "interior-two-tone", "two-tone")
        );

        for (InteriorData data : interiorsData) {
            Interior interior = new Interior(data.name(), data.description(), data.image(), data.visualKey());

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
    private record PaintData(String name, String description, String visualKey, String hex) {
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

    private record RimData(String name, String description, String image, String visualKey) {
        BigDecimal priceForType(CarType carType) {
            // Sport 19" rims are included (base option)
            if (visualKey.equals("sport")) {
                return BigDecimal.ZERO;
            }
            // Performance 20" rims - mid tier
            if (visualKey.equals("performance")) {
                return switch (carType) {
                    case SEDAN -> new BigDecimal("1800.00");
                    case SUV -> new BigDecimal("2100.00");
                    case SPORTS -> new BigDecimal("2500.00");
                    case LUXURY_COUPE -> new BigDecimal("3000.00");
                    case SUPERCAR -> new BigDecimal("4500.00");
                };
            }
            // Premium/Racing/Exclusive rims - high tier
            return switch (carType) {
                case SEDAN -> new BigDecimal("3200.00");
                case SUV -> new BigDecimal("3800.00");
                case SPORTS -> new BigDecimal("4500.00");
                case LUXURY_COUPE -> new BigDecimal("5500.00");
                case SUPERCAR -> new BigDecimal("8000.00");
            };
        }
    }

    private record InteriorData(String name, String description, String image, String visualKey) {
        BigDecimal priceForType(CarType carType) {
            // Black leather is base/included
            if (visualKey.equals("black")) {
                return BigDecimal.ZERO;
            }
            // Beige leather - standard upgrade
            if (visualKey.equals("beige")) {
                return switch (carType) {
                    case SEDAN -> new BigDecimal("2000.00");
                    case SUV -> new BigDecimal("2300.00");
                    case SPORTS -> new BigDecimal("2500.00");
                    case LUXURY_COUPE -> new BigDecimal("3000.00");
                    case SUPERCAR -> new BigDecimal("4500.00");
                };
            }
            // Red Sport - sport upgrade
            if (visualKey.equals("red")) {
                return switch (carType) {
                    case SEDAN -> new BigDecimal("2500.00");
                    case SUV -> new BigDecimal("2800.00");
                    case SPORTS -> new BigDecimal("3200.00");
                    case LUXURY_COUPE -> new BigDecimal("3800.00");
                    case SUPERCAR -> new BigDecimal("5500.00");
                };
            }
            // Carbon Fiber / Two-Tone - premium options
            return switch (carType) {
                case SEDAN -> new BigDecimal("4000.00");
                case SUV -> new BigDecimal("4500.00");
                case SPORTS -> new BigDecimal("5000.00");
                case LUXURY_COUPE -> new BigDecimal("6000.00");
                case SUPERCAR -> new BigDecimal("9000.00");
            };
        }
    }
}

