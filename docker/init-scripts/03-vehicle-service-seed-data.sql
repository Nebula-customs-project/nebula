-- Vehicle Service Seed Data
-- Populates initial test data for the Vehicle Service

-- ============================================================================
-- VEHICLES
-- ============================================================================
INSERT INTO vehicle_service.vehicle (car_name, car_type, horse_power, base_price, image) VALUES
-- SPORTS
('911 Carrera', 'SPORTS', 379, 106100.00, '911-carrera-hero'),
('718 Cayman', 'SPORTS', 300, 63400.00, '718-cayman-hero'),
-- SEDAN
('Panamera', 'SEDAN', 325, 92400.00, 'panamera-hero'),
('Taycan', 'SEDAN', 402, 86700.00, 'taycan-hero'),
-- SUV
('Cayenne', 'SUV', 348, 76300.00, 'cayenne-hero'),
('Macan', 'SUV', 261, 60900.00, 'macan-hero'),
-- LUXURY_COUPE
('911 Targa 4S', 'LUXURY_COUPE', 443, 137400.00, '911-targa-hero'),
('Panamera GTS', 'LUXURY_COUPE', 473, 132900.00, 'panamera-gts-hero'),
-- SUPERCAR
('911 GT3 RS', 'SUPERCAR', 518, 229900.00, '911-gt3rs-hero'),
('918 Spyder', 'SUPERCAR', 887, 845000.00, '918-spyder-hero');

-- ============================================================================
-- PAINTS (Fixed list as specified)
-- ============================================================================
INSERT INTO vehicle_service.paint (name, description) VALUES
('Black', 'Timeless deep black metallic finish'),
('Alpine White', 'Pure brilliant white solid finish'),
('Jamaica Blue', 'Vibrant tropical blue metallic'),
('Rallye Red', 'Bold racing-inspired red'),
('Midnight Blue', 'Deep elegant dark blue metallic'),
('Lime Met Green', 'Eye-catching lime green metallic'),
('Scorch Red', 'Intense fiery red metallic'),
('Panther Pink', 'Distinctive bold pink finish'),
('Orange', 'Vibrant pure orange solid'),
('Green Go / Sassy Green', 'Classic heritage green');

-- ============================================================================
-- PAINT PRICES (per car type)
-- ============================================================================
-- Black (included for SEDAN)
INSERT INTO vehicle_service.paint_price (paint_id, car_type, price) VALUES
(1, 'SEDAN', 0.00), (1, 'SUV', 250.00), (1, 'SPORTS', 500.00), (1, 'LUXURY_COUPE', 750.00), (1, 'SUPERCAR', 1500.00);

-- Alpine White (included for SEDAN and SUV)
INSERT INTO vehicle_service.paint_price (paint_id, car_type, price) VALUES
(2, 'SEDAN', 0.00), (2, 'SUV', 0.00), (2, 'SPORTS', 750.00), (2, 'LUXURY_COUPE', 1000.00), (2, 'SUPERCAR', 2000.00);

-- Premium colors (Jamaica Blue, Rallye Red, Midnight Blue, Lime Met Green, Scorch Red, Panther Pink, Orange, Green Go)
INSERT INTO vehicle_service.paint_price (paint_id, car_type, price) VALUES
(3, 'SEDAN', 1200.00), (3, 'SUV', 1500.00), (3, 'SPORTS', 2200.00), (3, 'LUXURY_COUPE', 2800.00), (3, 'SUPERCAR', 4500.00),
(4, 'SEDAN', 1200.00), (4, 'SUV', 1500.00), (4, 'SPORTS', 2200.00), (4, 'LUXURY_COUPE', 2800.00), (4, 'SUPERCAR', 4500.00),
(5, 'SEDAN', 1200.00), (5, 'SUV', 1500.00), (5, 'SPORTS', 2200.00), (5, 'LUXURY_COUPE', 2800.00), (5, 'SUPERCAR', 4500.00),
(6, 'SEDAN', 1200.00), (6, 'SUV', 1500.00), (6, 'SPORTS', 2200.00), (6, 'LUXURY_COUPE', 2800.00), (6, 'SUPERCAR', 4500.00),
(7, 'SEDAN', 1200.00), (7, 'SUV', 1500.00), (7, 'SPORTS', 2200.00), (7, 'LUXURY_COUPE', 2800.00), (7, 'SUPERCAR', 4500.00),
(8, 'SEDAN', 1200.00), (8, 'SUV', 1500.00), (8, 'SPORTS', 2200.00), (8, 'LUXURY_COUPE', 2800.00), (8, 'SUPERCAR', 4500.00),
(9, 'SEDAN', 1200.00), (9, 'SUV', 1500.00), (9, 'SPORTS', 2200.00), (9, 'LUXURY_COUPE', 2800.00), (9, 'SUPERCAR', 4500.00),
(10, 'SEDAN', 1200.00), (10, 'SUV', 1500.00), (10, 'SPORTS', 2200.00), (10, 'LUXURY_COUPE', 2800.00), (10, 'SUPERCAR', 4500.00);

-- ============================================================================
-- RIMS
-- ============================================================================
INSERT INTO vehicle_service.rim (name, description, image) VALUES
('Sport A', 'Standard Sport rims', 'rim-19-base'),
('Sport B', 'Five-spoke design', 'rim-20-sport'),
('Racing Pro', 'Lightweight Racing Rims', 'rim-21-turbo'),
('Chrome Luxury', 'Premium chrome finish', 'rim-20-rs-spyder'),
('Carbon Fiber', 'Ultra-light carbon fiber', 'rim-21-exclusive');

-- ============================================================================
-- RIM PRICES (per car type)
-- ============================================================================
-- Base Alloy (included)
INSERT INTO vehicle_service.rim_price (rim_id, car_type, price) VALUES
(1, 'SEDAN', 0.00), (1, 'SUV', 0.00), (1, 'SPORTS', 0.00), (1, 'LUXURY_COUPE', 0.00), (1, 'SUPERCAR', 0.00);

-- Sport Alloy (mid tier)
INSERT INTO vehicle_service.rim_price (rim_id, car_type, price) VALUES
(2, 'SEDAN', 1800.00), (2, 'SUV', 2100.00), (2, 'SPORTS', 2500.00), (2, 'LUXURY_COUPE', 3000.00), (2, 'SUPERCAR', 4500.00);

-- Premium rims (high tier)
INSERT INTO vehicle_service.rim_price (rim_id, car_type, price) VALUES
(3, 'SEDAN', 3200.00), (3, 'SUV', 3800.00), (3, 'SPORTS', 4500.00), (3, 'LUXURY_COUPE', 5500.00), (3, 'SUPERCAR', 8000.00),
(4, 'SEDAN', 3200.00), (4, 'SUV', 3800.00), (4, 'SPORTS', 4500.00), (4, 'LUXURY_COUPE', 5500.00), (4, 'SUPERCAR', 8000.00),
(5, 'SEDAN', 3200.00), (5, 'SUV', 3800.00), (5, 'SPORTS', 4500.00), (5, 'LUXURY_COUPE', 5500.00), (5, 'SUPERCAR', 8000.00);

-- ============================================================================
-- INTERIORS
-- ============================================================================
INSERT INTO vehicle_service.interior (name, description, image) VALUES
('Black Leather', 'Classic black leather upholstery', 'interior-black-leather'),
('Espresso Brown', 'Warm espresso brown leather', 'interior-espresso'),
('Bordeaux Red', 'Luxurious bordeaux red leather', 'interior-bordeaux'),
('Chalk Beige', 'Elegant chalk beige leather', 'interior-chalk'),
('Two-Tone Black/Red', 'Sporty two-tone combination', 'interior-two-tone');

-- ============================================================================
-- INTERIOR PRICES (per car type)
-- ============================================================================
-- Black Leather (included)
INSERT INTO vehicle_service.interior_price (interior_id, car_type, price) VALUES
(1, 'SEDAN', 0.00), (1, 'SUV', 0.00), (1, 'SPORTS', 0.00), (1, 'LUXURY_COUPE', 0.00), (1, 'SUPERCAR', 0.00);

-- Standard leather upgrades (Brown, Beige)
INSERT INTO vehicle_service.interior_price (interior_id, car_type, price) VALUES
(2, 'SEDAN', 2500.00), (2, 'SUV', 2800.00), (2, 'SPORTS', 3200.00), (2, 'LUXURY_COUPE', 3800.00), (2, 'SUPERCAR', 5500.00),
(4, 'SEDAN', 2500.00), (4, 'SUV', 2800.00), (4, 'SPORTS', 3200.00), (4, 'LUXURY_COUPE', 3800.00), (4, 'SUPERCAR', 5500.00);

-- Premium options (Red, Two-Tone)
INSERT INTO vehicle_service.interior_price (interior_id, car_type, price) VALUES
(3, 'SEDAN', 4200.00), (3, 'SUV', 4800.00), (3, 'SPORTS', 5500.00), (3, 'LUXURY_COUPE', 6500.00), (3, 'SUPERCAR', 9500.00),
(5, 'SEDAN', 4200.00), (5, 'SUV', 4800.00), (5, 'SPORTS', 5500.00), (5, 'LUXURY_COUPE', 6500.00), (5, 'SUPERCAR', 9500.00);

