-- Create database if not exists
CREATE DATABASE IF NOT EXISTS feed_service_db;

-- Use the database
USE feed_service_db;

-- Create user if not exists
CREATE USER IF NOT EXISTS 'feed_service'@'%' IDENTIFIED BY 'feedservice123';

-- Grant privileges
GRANT ALL PRIVILEGES ON feed_service_db.* TO 'feed_service'@'%';
FLUSH PRIVILEGES;

-- Sample data
INSERT IGNORE INTO user_following (id, follower_id, following_id, created_at) VALUES
(1, 1, 2, NOW()),
(2, 1, 3, NOW()),
(3, 2, 1, NOW());

INSERT IGNORE INTO feed_items (id, user_id, post_id, post_user_id, score, created_at) VALUES
(1, 1, 1, 2, 0.95, NOW()),
(2, 1, 2, 3, 0.87, NOW()),
(3, 2, 1, 1, 0.92, NOW());