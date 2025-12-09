# 💬 Chat Service

Real-time chat microservice for RevHub platform using WebSocket and MongoDB.

## 🚀 Features

- **Real-time Messaging**: WebSocket-based instant messaging
- **Conversation Management**: Automatic conversation creation and tracking
- **Message Persistence**: MongoDB storage with indexing
- **Read Status**: Track read/unread messages
- **Typing Indicators**: Real-time typing notifications
- **User Presence**: Join/leave conversation tracking
- **Message History**: Paginated message retrieval

## 🔧 Configuration

- **Port**: 8086
- **Database**: MongoDB (port 27018)
- **WebSocket**: Real-time bidirectional communication
- **Message Types**: TEXT, IMAGE, FILE (extensible)

## 📋 API Endpoints

### REST API
- `GET /api/chat/conversations/{userId}` - Get user conversations
- `GET /api/chat/messages/{conversationId}` - Get conversation messages
- `PUT /api/chat/messages/{conversationId}/read` - Mark messages as read
- `GET /api/chat/messages/{conversationId}/unread-count` - Get unread count

### WebSocket Endpoints
- `/app/chat.send` - Send message
- `/app/chat.join` - Join conversation
- `/app/chat.typing` - Typing indicator

### WebSocket Subscriptions
- `/user/queue/messages` - Receive messages
- `/user/queue/typing` - Typing notifications

## 🌐 WebSocket Integration

### Client Connection
```javascript
const socket = new SockJS('/ws/chat');
const stompClient = Stomp.over(socket);

stompClient.connect({}, function() {
    // Subscribe to messages
    stompClient.subscribe('/user/queue/messages', function(message) {
        const chatMessage = JSON.parse(message.body);
        // Handle incoming message
    });
    
    // Subscribe to typing indicators
    stompClient.subscribe('/user/queue/typing', function(typing) {
        const typingEvent = JSON.parse(typing.body);
        // Handle typing indicator
    });
});
```

### Send Message
```javascript
stompClient.send('/app/chat.send', {}, JSON.stringify({
    senderId: 1,
    senderUsername: 'user1',
    receiverId: 2,
    receiverUsername: 'user2',
    content: 'Hello!',
    messageType: 'TEXT'
}));
```

### Typing Indicator
```javascript
stompClient.send('/app/chat.typing', {}, JSON.stringify({
    senderUsername: 'user1',
    receiverUsername: 'user2',
    typing: true
}));
```

## 📊 MongoDB Collections

### chat_messages
- Message documents with conversation tracking
- Indexed by conversationId, timestamp, read status
- Support for different message types

### conversations
- Conversation metadata and participants
- Last message tracking for UI optimization
- Participant management

## 🔍 Message Flow

1. **Send Message**: Client sends via WebSocket
2. **Save to MongoDB**: Message persisted with metadata
3. **Real-time Delivery**: WebSocket broadcast to participants
4. **Conversation Update**: Last message and timestamp updated
5. **Read Status**: Track when messages are read

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

- **MongoDB Indexes**: Optimized for conversation and message queries
- **WebSocket Persistence**: Maintain connections for real-time updates
- **Pagination**: Efficient message history loading
- **Conversation Caching**: Quick conversation list retrieval

Real-time chat service providing instant messaging with full conversation management!