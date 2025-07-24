-- Insert sample categories for testing
-- Simple INSERT statements that will work with H2
INSERT INTO categories (name, description, is_active, created_at, updated_at) VALUES 
('Electronics', 'Electronic devices and gadgets', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Clothing', 'Apparel and fashion items', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Books', 'Books and publications', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Home & Garden', 'Home improvement and garden supplies', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Sports', 'Sports and outdoor equipment', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
