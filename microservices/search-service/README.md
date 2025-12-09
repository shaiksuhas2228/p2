# 🔍 Search Service

Search Service microservice for RevHub platform providing post and user search using Elasticsearch.

## 🚀 Features

- **Full-text Search**: Advanced text search across posts and users
- **Multi-field Search**: Search content, hashtags, usernames, and bios
- **Hashtag Search**: Dedicated hashtag-based post discovery
- **User Suggestions**: Auto-complete user search with prefix matching
- **Relevance Scoring**: Elasticsearch relevance-based result ranking
- **Sorting Options**: Sort by relevance, date, or popularity
- **Real-time Indexing**: Add/update/delete documents in real-time

## 🔧 Configuration

- **Port**: 8087
- **Search Engine**: Elasticsearch (port 9200)
- **Indexes**: `posts` and `users`
- **Search Types**: ALL, POSTS, USERS

## 📋 API Endpoints

### Search Operations
- `POST /api/search` - Universal search (posts + users)
- `GET /api/search/posts` - Search posts only
- `GET /api/search/users` - Search users only
- `GET /api/search/hashtag/{hashtag}` - Search by hashtag
- `GET /api/search/users/suggest` - User auto-complete

### Index Management
- `POST /api/search/index/post` - Index a post
- `POST /api/search/index/user` - Index a user
- `DELETE /api/search/index/post/{postId}` - Remove post from index
- `DELETE /api/search/index/user/{userId}` - Remove user from index

### Health
- `GET /api/search/health` - Service health check

## 🔍 Search Examples

### Universal Search
```json
POST /api/search
{
  "query": "spring boot tutorial",
  "type": "ALL",
  "sortBy": "relevance",
  "page": 0,
  "size": 20
}
```

### Post Search
```bash
GET /api/search/posts?query=javascript&page=0&size=10
```

### User Search
```bash
GET /api/search/users?query=developer&page=0&size=10
```

### Hashtag Search
```bash
GET /api/search/hashtag/programming?page=0&size=20
```

### User Suggestions
```bash
GET /api/search/users/suggest?prefix=john&size=5
```

## 📊 Elasticsearch Indexes

### Posts Index
- **Fields**: content, hashtags, username, userId, createdAt
- **Analyzers**: Standard for content, keyword for hashtags
- **Boosting**: hashtags^3, content^2 for relevance

### Users Index
- **Fields**: username, bio, email, followersCount, verified
- **Analyzers**: Standard for text fields
- **Boosting**: username^3, bio^1 for relevance

## 🔄 Search Flow

1. **Query Processing**: Parse and validate search parameters
2. **Elasticsearch Query**: Build multi-field search queries
3. **Result Processing**: Convert documents to response DTOs
4. **Relevance Scoring**: Elasticsearch scoring with field boosting
5. **Response**: Return paginated results with metadata

## 🚀 Indexing Strategy

### Real-time Indexing
- Index posts when created/updated
- Index users when profiles are updated
- Remove documents when deleted

### Bulk Operations
- Batch indexing for initial data load
- Bulk updates for performance optimization

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

- **Field Boosting**: Prioritize important fields (hashtags, usernames)
- **Pagination**: Efficient large result set handling
- **Caching**: Elasticsearch query result caching
- **Indexing**: Optimized document structure for fast search

Advanced search capabilities with Elasticsearch providing fast, relevant results for posts and users!