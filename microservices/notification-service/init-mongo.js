// Create database and user
db = db.getSiblingDB('notification_service_db');

// Create user
db.createUser({
  user: 'notif_user',
  pwd: 'notif123',
  roles: [
    {
      role: 'readWrite',
      db: 'notification_service_db'
    }
  ]
});

// Create indexes for performance
db.notifications.createIndex({ "userId": 1, "createdAt": -1 });
db.notifications.createIndex({ "userId": 1, "read": 1, "createdAt": -1 });
db.notifications.createIndex({ "type": 1, "createdAt": -1 });
db.notifications.createIndex({ "priority": 1, "read": 1 });
db.notifications.createIndex({ "pushed": 1, "createdAt": 1 });

db.user_preferences.createIndex({ "userId": 1 }, { unique: true });

// Sample data
db.notifications.insertMany([
  {
    userId: NumberLong(1),
    title: "Welcome to RevHub!",
    message: "Thanks for joining our platform",
    type: "SYSTEM",
    priority: "MEDIUM",
    read: false,
    pushed: false,
    createdAt: new Date()
  },
  {
    userId: NumberLong(1),
    title: "New Follower",
    message: "User 2 started following you",
    type: "FOLLOW",
    priority: "LOW",
    read: false,
    pushed: false,
    relatedEntityId: NumberLong(2),
    relatedEntityType: "USER",
    createdAt: new Date()
  }
]);

db.user_preferences.insertMany([
  {
    userId: NumberLong(1),
    pushEnabled: true,
    emailEnabled: true,
    typePreferences: {
      "FOLLOW": true,
      "LIKE": true,
      "COMMENT": true,
      "POST": true,
      "SYSTEM": true
    },
    timezone: "UTC"
  }
]);

print("Database initialized successfully");