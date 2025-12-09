// Create database and user
db = db.getSiblingDB('chat_service_db');

// Create user
db.createUser({
  user: 'chat_user',
  pwd: 'chat123',
  roles: [
    {
      role: 'readWrite',
      db: 'chat_service_db'
    }
  ]
});

// Create indexes for performance
db.chat_messages.createIndex({ "conversationId": 1, "timestamp": -1 });
db.chat_messages.createIndex({ "senderId": 1, "timestamp": -1 });
db.chat_messages.createIndex({ "receiverId": 1, "read": 1 });
db.chat_messages.createIndex({ "conversationId": 1, "receiverId": 1, "read": 1 });

db.conversations.createIndex({ "participants": 1 });
db.conversations.createIndex({ "lastMessageTime": -1 });

// Sample data
db.conversations.insertMany([
  {
    participants: [NumberLong(1), NumberLong(2)],
    participantUsernames: ["user1", "user2"],
    lastMessage: "Hello there!",
    lastMessageTime: new Date(),
    createdAt: new Date()
  }
]);

db.chat_messages.insertMany([
  {
    conversationId: "conv1",
    senderId: NumberLong(1),
    senderUsername: "user1",
    receiverId: NumberLong(2),
    receiverUsername: "user2",
    content: "Hello there!",
    messageType: "TEXT",
    read: false,
    timestamp: new Date()
  },
  {
    conversationId: "conv1",
    senderId: NumberLong(2),
    senderUsername: "user2",
    receiverId: NumberLong(1),
    receiverUsername: "user1",
    content: "Hi! How are you?",
    messageType: "TEXT",
    read: false,
    timestamp: new Date()
  }
]);

print("Chat database initialized successfully");