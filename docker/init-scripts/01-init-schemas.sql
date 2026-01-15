-- =============================================================================
-- Nebula Database Schema Initialization
-- Creates schemas for all microservices in the shared PostgreSQL database
-- =============================================================================
-- This script runs on first PostgreSQL container startup.
-- Each service has its own schema for data isolation.
-- =============================================================================

-- World View Service Schema
-- Stores driving routes, waypoints, and journey data
CREATE SCHEMA IF NOT EXISTS world_view;

-- Vehicle Service Schema
-- Stores vehicle models, configurations, and inventory
CREATE SCHEMA IF NOT EXISTS vehicle_service;

-- User Service Schema
-- Stores user accounts, authentication, and profiles
CREATE SCHEMA IF NOT EXISTS user_service;

-- Merchandise Service Schema
-- Stores merchandise catalog, orders, and inventory
CREATE SCHEMA IF NOT EXISTS merchandise_service;

-- Gateway Service does not need a schema (no persistence)
-- Platform Core does not need a schema (config server only)

-- Grant permissions to the application user
-- Note: POSTGRES_USER is set via environment variable
DO $$
BEGIN
    -- Grant usage on all schemas
    EXECUTE format('GRANT USAGE ON SCHEMA world_view TO %I', current_user);
    EXECUTE format('GRANT USAGE ON SCHEMA vehicle_service TO %I', current_user);
    EXECUTE format('GRANT USAGE ON SCHEMA user_service TO %I', current_user);
    EXECUTE format('GRANT USAGE ON SCHEMA merchandise_service TO %I', current_user);
    
    -- Grant all privileges on all tables in schemas
    EXECUTE format('GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA world_view TO %I', current_user);
    EXECUTE format('GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA vehicle_service TO %I', current_user);
    EXECUTE format('GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA user_service TO %I', current_user);
    EXECUTE format('GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA merchandise_service TO %I', current_user);
    
    -- Grant all privileges on all sequences
    EXECUTE format('GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA world_view TO %I', current_user);
    EXECUTE format('GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA vehicle_service TO %I', current_user);
    EXECUTE format('GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA user_service TO %I', current_user);
    EXECUTE format('GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA merchandise_service TO %I', current_user);
    
    -- Set default privileges for future tables
    EXECUTE format('ALTER DEFAULT PRIVILEGES IN SCHEMA world_view GRANT ALL ON TABLES TO %I', current_user);
    EXECUTE format('ALTER DEFAULT PRIVILEGES IN SCHEMA vehicle_service GRANT ALL ON TABLES TO %I', current_user);
    EXECUTE format('ALTER DEFAULT PRIVILEGES IN SCHEMA user_service GRANT ALL ON TABLES TO %I', current_user);
    EXECUTE format('ALTER DEFAULT PRIVILEGES IN SCHEMA merchandise_service GRANT ALL ON TABLES TO %I', current_user);
END $$;

-- Log successful initialization
DO $$
BEGIN
    RAISE NOTICE 'Nebula database schemas initialized successfully:';
    RAISE NOTICE '  - world_view';
    RAISE NOTICE '  - vehicle_service';
    RAISE NOTICE '  - user_service';
    RAISE NOTICE '  - merchandise_service';
END $$;