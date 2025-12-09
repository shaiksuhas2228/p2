@echo off
echo Testing Microservice Endpoints...
echo.

echo User Service (8081):
curl -s http://localhost:8081/health 2>nul || echo [NOT READY]

echo Feed Service (8083) - requires userId:
curl -s "http://localhost:8083/api/feed/1" 2>nul || echo [NOT READY]

echo Chat Service (8084) - requires userId:
curl -s "http://localhost:8084/api/chat/conversations/1" 2>nul || echo [NOT READY]

echo Follow Service (8085) - stats endpoint:
curl -s "http://localhost:8085/api/follow/1/stats" 2>nul || echo [NOT READY]

echo Notification Service (8086) - requires userId:
curl -s "http://localhost:8086/api/notifications/1" 2>nul || echo [NOT READY]

echo.
echo Correct Endpoint Paths:
echo - Feed: /api/feed/{userId}
echo - Chat: /api/chat/conversations/{userId}
echo - Follow: /api/follow/{userId}/stats
echo - Notifications: /api/notifications/{userId}

pause