# 📝 Post Service

Post Service microservice for RevHub platform handling posts, media uploads, and hashtags.

## 🚀 Features

- **Post Management**: Create and retrieve posts
- **Media Upload**: Support for multiple file uploads per post
- **Hashtag System**: Automatic hashtag extraction and trending
- **User Posts**: Filter posts by user
- **Hashtag Search**: Find posts by hashtags

## 🔧 Configuration

- **Port**: 8082
- **Database**: MySQL (port 3308)
- **Upload Directory**: `uploads/posts/`
- **Max File Size**: 10MB per file, 50MB per request

## 📋 API Endpoints

### Posts
- `POST /api/posts` - Create new post
- `GET /api/posts` - Get all posts
- `GET /api/posts/user/{userId}` - Get posts by user
- `GET /api/posts/hashtag/{hashtag}` - Get posts by hashtag

### Media
- `POST /api/posts/{postId}/media` - Upload media files

### Health
- `GET /api/posts/health` - Service health check

## 🐳 Docker Commands

```bash
# Build and run
docker-compose up --build

# Run in background
docker-compose up -d

# Stop services
docker-compose down
```

## 📊 Database Schema

- **posts**: Post content and metadata
- **media**: File upload information
- **hashtags**: Hashtag associations