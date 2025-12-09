# 👥 Follow Service

Follow Service microservice for RevHub platform managing followers graph and user relationships using MySQL.

## 🚀 Features

- **Follow/Unfollow**: User relationship management
- **Followers Graph**: Complete user connection mapping
- **Statistics Tracking**: Real-time follower/following counts
- **Mutual Connections**: Find common connections between users
- **Pagination Support**: Efficient large dataset handling
- **Performance Optimized**: Indexed queries and batch operations

## 🔧 Configuration

- **Port**: 8084
- **Database**: MySQL (port 3310)
- **Optimizations**: Connection pooling, batch processing, strategic indexing

## 📋 API Endpoints

### Follow Operations
- `POST /api/follow/follow` - Follow a user
- `POST /api/follow/unfollow` - Unfollow a user
- `GET /api/follow/is-following` - Check follow status

### Graph Queries
- `GET /api/follow/{userId}/followers` - Get user's followers
- `GET /api/follow/{userId}/following` - Get users being followed
- `GET /api/follow/{userId}/graph` - Complete follow graph
- `GET /api/follow/{userId}/mutual` - Mutual connections

### Statistics
- `GET /api/follow/{userId}/stats` - Follower/following counts

### Health
- `GET /api/follow/health` - Service health check

## 🏗️ Database Schema

### Tables
- **follows**: Core relationship table with unique constraints
- **user_stats**: Denormalized counters for performance

### Indexes
- Composite index on (follower_id, following_id)
- Individual indexes on follower_id and following_id
- Timestamp index for chronological queries

## 🔍 Graph Operations

### Follow Graph Structure
```json
{
  "userId": 1,
  "followers": [2, 3, 4],
  "following": [5, 6],
  "mutualConnections": [7, 8],
  "stats": {
    "followersCount": 3,
    "followingCount": 2
  }
}
```

## 🐳 Docker Commands

```bash
# Build and run
docker-compose up --build

# Run in background
docker-compose up -d

# Stop services
docker-compose down
```

## ⚡ Performance Features

- **Batch Operations**: Optimized bulk inserts/updates
- **Strategic Indexing**: Fast graph traversal queries
- **Connection Pooling**: Efficient database connections
- **Denormalized Counters**: O(1) statistics retrieval