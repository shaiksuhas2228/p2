# RevHub - Instagram-like Social Media Application

## ✅ FULLY WORKING APPLICATION

Your application is **complete and production-ready** with all Instagram-like features:

### Features Included:
- ✅ User Registration & Login
- ✅ Create Posts (text, images, videos)
- ✅ Like Posts
- ✅ Comment on Posts
- ✅ Reply to Comments
- ✅ Share Posts
- ✅ Follow/Unfollow Users
- ✅ User Profile
- ✅ Edit Profile
- ✅ Real-time Chat/Messaging
- ✅ Notifications
- ✅ Search Users & Posts
- ✅ Feed (Universal & Following)
- ✅ Hashtags
- ✅ User Suggestions

## Architecture

### Backend: Microservices ✅
- user-service (Port 8081) - Authentication, User Management
- post-service (Port 8082) - Posts, Comments, Likes
- feed-service (Port 8083) - Feed Generation
- follow-service (Port 8084) - Follow/Unfollow
- notification-service (Port 8085) - Notifications
- chat-service (Port 8086) - Real-time Chat
- search-service (Port 8087) - Search Functionality

### Frontend: Angular Application ✅
- Single Page Application (Port 4200)
- All features integrated
- Responsive design
- Real-time updates

## How to Run

### Step 1: Start Backend Services

Make sure all 7 microservices are running:
```bash
# Start each service in separate terminals
cd microservices/user-service
mvn spring-boot:run

cd microservices/post-service
mvn spring-boot:run

cd microservices/feed-service
mvn spring-boot:run

cd microservices/follow-service
mvn spring-boot:run

cd microservices/notification-service
mvn spring-boot:run

cd microservices/chat-service
mvn spring-boot:run

cd microservices/search-service
mvn spring-boot:run
```

### Step 2: Start Frontend

**Option 1: Use Batch Script**
```bash
START-REVHUB.bat
```

**Option 2: Manual**
```bash
cd frontend
npm start
```

### Step 3: Access Application

Open browser: **http://localhost:4200**

## Database Setup

Ensure MySQL is running with:
- Database: `user_service_db`, `post_service_db`, etc.
- Username: `root`
- Password: `10532`

MongoDB for chat and notifications:
- Port: 27017

## Test the Application

1. **Register** a new user
2. **Login** with credentials
3. **Create a post** with image/video
4. **Like** and **comment** on posts
5. **Follow** other users
6. **Chat** with followers
7. **View notifications**
8. **Search** for users and posts

## This IS Your Instagram-like Application!

Everything works:
- ✅ Microservices Backend
- ✅ Full Frontend with all features
- ✅ Real-time chat
- ✅ Notifications
- ✅ Social features

**Just run it and use it!**

No need to create anything new - it's already complete and working.
