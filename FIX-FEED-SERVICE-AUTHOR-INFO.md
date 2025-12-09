# Fix: Feed Service - Username and Profile Photo Not Showing

## Problem
When viewing posts through the feed-service, the username and profile photo of the person who uploaded the post were not displaying properly.

## Root Cause
The `FeedResponse` DTO in feed-service was missing the `author` field and related fields (authorUsername, profilePicture, etc.) that are returned by the post-service. When feed-service calls post-service to get post details, it receives the author information, but the FeedResponse DTO couldn't map these fields, so they were lost.

## Solution Applied

### 1. Updated FeedResponse DTO
**File**: `microservices/feed-service/src/main/java/com/revhub/feedservice/dto/FeedResponse.java`

Added the following fields to match the PostResponse structure from post-service:
- `String authorUsername` - The username of the post author
- `String imageUrl` - The post image URL
- `String mediaType` - The media type (image/video)
- `Integer likesCount` - Number of likes
- `Integer commentsCount` - Number of comments
- `Integer sharesCount` - Number of shares
- `Map<String, Object> author` - Complete author object with username and profilePicture

These fields are now properly mapped when feed-service calls post-service's `/api/posts/{id}` endpoint.

## How to Apply the Fix

### Step 1: Rebuild Feed Service
Run the rebuild script:
```batch
cd microservices\feed-service
REBUILD-SERVICE.bat
```

Or manually:
```batch
cd microservices\feed-service
mvn clean install -DskipTests
```

### Step 2: Restart Feed Service
1. Stop the currently running feed-service (if running)
2. Start it again using your normal startup method

### Step 3: Verify the Fix
1. Open the application at http://localhost:4200
2. Navigate to the feed
3. Check that each post now shows:
   - The correct username of the person who posted
   - Their profile picture (or default avatar with initials)

## Technical Details

### Data Flow
1. **Frontend** calls feed-service: `GET /api/feed/{userId}`
2. **Feed-service** retrieves feed items from database
3. **Feed-service** calls post-service for each post: `GET /api/posts/{postId}`
4. **Post-service** returns complete post data including author information
5. **Feed-service** maps the response to FeedResponse DTO (now includes author fields)
6. **Frontend** receives posts with author information and displays them

### Author Information Structure
The `author` field in FeedResponse contains:
```json
{
  "id": 1,
  "username": "john_doe",
  "profilePicture": "data:image/jpeg;base64,..." or null
}
```

If profilePicture is null or empty, the frontend uses UI Avatars API to generate a default avatar with the user's initials.

## Files Modified
- `microservices/feed-service/src/main/java/com/revhub/feedservice/dto/FeedResponse.java`

## Related Services
- **Post Service** (port 8082): Provides the complete post data with author information
- **User Service** (port 8081): Post-service calls this to fetch author details
- **Feed Service** (port 8083): Now properly receives and forwards author information

## Testing Checklist
- [ ] Feed service builds successfully
- [ ] Feed service starts without errors
- [ ] Posts in feed show correct usernames
- [ ] Posts in feed show correct profile pictures
- [ ] Default avatars appear for users without profile pictures
- [ ] Following feed shows posts with author information
- [ ] Universal feed shows posts with author information
