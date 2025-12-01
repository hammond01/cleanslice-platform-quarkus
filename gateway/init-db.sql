-- Create databases
CREATE DATABASE product_db;
CREATE DATABASE category_db;
CREATE DATABASE audit_db;
CREATE DATABASE kong;

-- Create users (if needed)
-- Products, categories and audit use default postgres user
-- Kong uses kong user
CREATE USER kong WITH PASSWORD 'kong';
GRANT ALL PRIVILEGES ON DATABASE kong TO kong;
