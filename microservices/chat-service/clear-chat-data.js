// MongoDB script to clear all chat data
// Run this with: mongosh mongodb://localhost:27017/chatdb clear-chat-data.js

db = db.getSiblingDB('chatdb');

print('Deleting all conversations...');
const conversationsResult = db.conversations.deleteMany({});
print('Deleted ' + conversationsResult.deletedCount + ' conversations');

print('Deleting all chat messages...');
const messagesResult = db.chat_messages.deleteMany({});
print('Deleted ' + messagesResult.deletedCount + ' chat messages');

print('Chat data cleared successfully!');
