-- Schema creation script for H2 Database
-- This ensures tables are created before data.sql runs

-- Note: This file is optional since we're using JPA with ddl-auto=create-drop
-- But it can help ensure proper initialization order

-- Categories table (will be created by JPA, this is just for reference)
-- CREATE TABLE IF NOT EXISTS categories (
--     id BIGINT AUTO_INCREMENT PRIMARY KEY,
--     name VARCHAR(255) NOT NULL UNIQUE,
--     description VARCHAR(500),
--     parent_id BIGINT,
--     is_active BOOLEAN DEFAULT TRUE,
--     created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
--     updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
--     FOREIGN KEY (parent_id) REFERENCES categories(id)
-- );
