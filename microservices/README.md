# RevHub Microservices

This folder contains all microservices for the RevHub platform.

## Current Microservices

### 🔐 User Service
- **Path**: `user-service/`
- **Port**: 8081
- **Database**: MySQL (port 3307)
- **Purpose**: Authentication and user profile management
- **Endpoints**: `/api/auth/register`, `/api/auth/login`, `/health`

### 📝 Post Service
- **Path**: `post-service/`
- **Port**: 8082
- **Database**: MySQL (port 3308)
- **Purpose**: Posts, media uploads, and hashtags
- **Endpoints**: `/api/posts`, `/api/posts/{postId}/media`, `/health`

### 🔄 Feed Service
- **Path**: `feed-service/`
- **Port**: 8083
- **Database**: MySQL (port 3309) + Redis + Kafka
- **Purpose**: Personalized feeds with real-time updates
- **Endpoints**: `/api/feed/{userId}`, `/api/feed/follow`, `/health`

### 👥 Follow Service
- **Path**: `follow-service/`
- **Port**: 8084
- **Database**: MySQL (port 3310)
- **Purpose**: Followers graph management and relationships
- **Endpoints**: `/api/follow/follow`, `/api/follow/{userId}/graph`, `/health`

### 🔔 Notification Service
- **Path**: `notification-service/`
- **Port**: 8085
- **Database**: MongoDB (port 27017)
- **Purpose**: Push notifications and alerts with WebSocket
- **Endpoints**: `/api/notifications`, `/ws/notifications`, `/health`

### 💬 Chat Service
- **Path**: `chat-service/`
- **Port**: 8086
- **Database**: MongoDB (port 27018)
- **Purpose**: Real-time chat with WebSocket messaging
- **Endpoints**: `/api/chat/conversations`, `/ws/chat`, `/health`

### 🔍 Search Service
- **Path**: `search-service/`
- **Port**: 8087
- **Search Engine**: Elasticsearch (port 9200)
- **Purpose**: Post and user search with full-text capabilities
- **Endpoints**: `/api/search`, `/api/search/posts`, `/health`

## Future Microservices

Additional microservices will be added here as the platform grows:
- Review Service
- Analytics Service
- etc.

## Structure
```
microservices/
├── user-service/          # Authentication & profiles
├── post-service/          # Posts, media & hashtags
├── feed-service/          # Personalized feeds & caching
├── follow-service/        # Followers graph management
├── notification-service/  # Push notifications & alerts
├── chat-service/          # Real-time chat messaging
├── search-service/        # Post & user search with Elasticsearch
├── review-service/        # (Future) Review management
└── README.md             # This file
```

## Development
Each microservice is independent with its own:
- Database
- Docker configuration
- Jenkins pipeline
- Port allocation