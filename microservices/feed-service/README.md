# 🔄 Feed Service

Feed Service microservice for RevHub platform providing personalized feeds with real-time updates using Kafka and Redis caching.

## 🚀 Features

- **Personalized Feeds**: Algorithm-based feed generation with scoring
- **Real-time Updates**: Kafka integration for instant feed updates
- **Redis Caching**: High-performance feed caching with TTL
- **User Following**: Follow/unfollow functionality
- **Score-based Ranking**: Time decay and relevance scoring

## 🔧 Configuration

- **Port**: 8083
- **Database**: MySQL (port 3309)
- **Cache**: Redis (port 6379)
- **Message Queue**: Kafka (port 9092)

## 📋 API Endpoints

### Feed
- `GET /api/feed/{userId}` - Get personalized feed
- `POST /api/feed/follow` - Follow a user

### Health
- `GET /api/feed/health` - Service health check

## 🏗️ Architecture

### Components
- **FeedService**: Core feed generation and caching
- **PostEventConsumer**: Kafka consumer for real-time updates
- **PostServiceClient**: Communication with post-service
- **RedisConfig**: Caching configuration

### Scoring Algorithm
- Time decay factor
- Hashtag relevance boost
- User relationship weight

## 🐳 Docker Stack

```bash
# Start complete stack
docker-compose up --build

# Services included:
# - MySQL (feed database)
# - Redis (caching)
# - Kafka + Zookeeper (messaging)
# - Feed Service application
```

## 📊 Database Schema

- **feed_items**: Personalized feed entries with scores
- **user_following**: User relationship tracking