-- Create database if not exists
CREATE DATABASE IF NOT EXISTS post_service_db;

-- Use the database
USE post_service_db;

-- Create user if not exists
CREATE USER IF NOT EXISTS 'post_service'@'%' IDENTIFIED BY 'postservice123';

-- Grant privileges
GRANT ALL PRIVILEGES ON post_service_db.* TO 'post_service'@'%';
FLUSH PRIVILEGES;

-- Sample data
INSERT IGNORE INTO posts (id, content, user_id, created_at, updated_at) VALUES
(1, 'Welcome to RevHub Post Service! #welcome #revhub', 1, NOW(), NOW()),
(2, 'Testing media upload functionality #test #media', 1, NOW(), NOW());

INSERT IGNORE INTO hashtags (id, tag_name, post_id) VALUES
(1, '#welcome', 1),
(2, '#revhub', 1),
(3, '#test', 2),
(4, '#media', 2);