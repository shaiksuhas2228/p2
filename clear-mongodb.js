// Connect to MongoDB and drop chat database
db = db.getSiblingDB('chat_service_db');
db.dropDatabase();
print('MongoDB chat_service_db dropped');
