@echo off
echo Checking RevHub Microservices Status...
echo.

echo User Service (8081):
curl -s http://localhost:8081/api/health 2>nul && echo [RUNNING] || echo [NOT RUNNING]

echo Post Service (8082):
curl -s http://localhost:8082/api/posts 2>nul && echo [RUNNING] || echo [NOT RUNNING]

echo Feed Service (8083):
curl -s http://localhost:8083/api/feed 2>nul && echo [RUNNING] || echo [NOT RUNNING]

echo Chat Service (8084):
curl -s http://localhost:8084/api/chat/conversations 2>nul && echo [RUNNING] || echo [NOT RUNNING]

echo Follow Service (8085):
curl -s http://localhost:8085/api/follow/stats/1 2>nul && echo [RUNNING] || echo [NOT RUNNING]

echo Notification Service (8086):
curl -s http://localhost:8086/api/notifications 2>nul && echo [RUNNING] || echo [NOT RUNNING]

echo Search Service (8087):
curl -s http://localhost:8087/api/search 2>nul && echo [RUNNING] || echo [NOT RUNNING]

echo.
echo Port Status:
netstat -an | findstr "808[1-7]" | findstr LISTENING

pause