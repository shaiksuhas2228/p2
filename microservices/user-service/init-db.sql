-- User Service Database Initialization
CREATE DATABASE IF NOT EXISTS user_service_db;
USE user_service_db;

-- Grant privileges to user_service user
GRANT ALL PRIVILEGES ON user_service_db.* TO 'user_service'@'%';
FLUSH PRIVILEGES;

-- Create users table if not exists
CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    first_name VARCHAR(50),
    last_name VARCHAR(50),
    profile_picture VARCHAR(255),
    bio TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Insert sample data
INSERT IGNORE INTO users (username, email, password, first_name, last_name) VALUES
('admin', 'admin@revhub.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBaLFQf6.zsZO6', 'Admin', 'User');

SELECT 'User Service Database Initialized Successfully' as message;