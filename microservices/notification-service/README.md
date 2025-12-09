# 🔔 Notification Service

Notification Service microservice for RevHub platform providing push notifications and alerts using MongoDB and WebSocket.

## 🚀 Features

- **Push Notifications**: Real-time WebSocket notifications
- **Alert Management**: Comprehensive notification system
- **User Preferences**: Customizable notification settings
- **Priority System**: LOW, MEDIUM, HIGH, URGENT priorities
- **Read Status**: Track read/unread notifications
- **Cleanup**: Automatic old notification cleanup
- **MongoDB Storage**: Flexible document-based storage

## 🔧 Configuration

- **Port**: 8085
- **Database**: MongoDB (port 27017)
- **WebSocket**: Real-time push notifications
- **Types**: FOLLOW, LIKE, COMMENT, POST, SYSTEM

## 📋 API Endpoints

### Notifications
- `POST /api/notifications` - Create notification
- `GET /api/notifications/{userId}` - Get user notifications
- `GET /api/notifications/{userId}/unread` - Get unread notifications
- `GET /api/notifications/{userId}/count` - Get unread count
- `PUT /api/notifications/{id}/read` - Mark as read
- `PUT /api/notifications/{userId}/read-all` - Mark all as read

### Preferences
- `GET /api/notifications/{userId}/preferences` - Get user preferences
- `PUT /api/notifications/{userId}/preferences` - Update preferences

### Maintenance
- `DELETE /api/notifications/{userId}/cleanup` - Cleanup old notifications

### Health
- `GET /api/notifications/health` - Service health check

## 🌐 WebSocket Connection

```javascript
// Connect to WebSocket
const socket = new SockJS('/ws/notifications');
const stompClient = Stomp.over(socket);

// Subscribe to user notifications
stompClient.subscribe('/topic/notifications/{userId}', function(notification) {
    const data = JSON.parse(notification.body);
    // Handle real-time notification
});
```

## 📊 MongoDB Collections

### notifications
- Notification documents with flexible schema
- Indexed by userId, createdAt, read status
- Support for related entities and priorities

### user_preferences
- User notification preferences
- Type-specific settings
- Push/email preferences

## 🔍 Notification Types

- **FOLLOW**: User follow notifications
- **LIKE**: Post like notifications  
- **COMMENT**: Comment notifications
- **POST**: New post notifications
- **SYSTEM**: System alerts and announcements

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

- **MongoDB Indexes**: Optimized query performance
- **WebSocket**: Real-time push notifications
- **Batch Operations**: Efficient bulk updates
- **Cleanup Jobs**: Automatic old data removal