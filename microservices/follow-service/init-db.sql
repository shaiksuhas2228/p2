-- Create database if not exists
CREATE DATABASE IF NOT EXISTS follow_service_db;

-- Use the database
USE follow_service_db;

-- Create user if not exists
CREATE USER IF NOT EXISTS 'follow_service'@'%' IDENTIFIED BY 'followservice123';

-- Grant privileges
GRANT ALL PRIVILEGES ON follow_service_db.* TO 'follow_service'@'%';
FLUSH PRIVILEGES;

-- Create indexes for performance
CREATE INDEX IF NOT EXISTS idx_follows_follower ON follows(follower_id);
CREATE INDEX IF NOT EXISTS idx_follows_following ON follows(following_id);
CREATE INDEX IF NOT EXISTS idx_follows_created_at ON follows(created_at);
CREATE INDEX IF NOT EXISTS idx_follows_composite ON follows(follower_id, following_id);

-- Sample data
INSERT IGNORE INTO user_stats (user_id, followers_count, following_count) VALUES
(1, 2, 1),
(2, 1, 2),
(3, 1, 1);

INSERT IGNORE INTO follows (id, follower_id, following_id, created_at) VALUES
(1, 1, 2, NOW()),
(2, 1, 3, NOW()),
(3, 2, 1, NOW()),
(4, 3, 2, NOW());