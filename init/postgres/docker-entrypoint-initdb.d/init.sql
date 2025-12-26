-- ============================================
-- Order Service Database Initialization
-- ============================================

-- Check if database exists, create if not
SELECT 'CREATE DATABASE "order-service"'
    WHERE NOT EXISTS (SELECT FROM pg_database WHERE datname = 'order-service')\gexec

-- Connect to database
    \c order-service

-- Create extension for UUID support (optional)
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- Grant privileges
GRANT ALL PRIVILEGES ON DATABASE "order-service" TO admin;

-- Log successful initialization
DO $$
BEGIN
  RAISE NOTICE 'Database order-service initialized successfully';
END $$;