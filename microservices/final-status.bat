@echo off
echo RevHub Microservices Final Status
echo =================================
echo.

echo Checking service ports...
netstat -an | findstr "808[1-9]" | findstr LISTENING

echo.
echo Updated Port Configuration:
echo - User Service: 8081
echo - Post Service: 8082  
echo - Feed Service: 8083
echo - Chat Service: 8088 (changed from 8084)
echo - Follow Service: 8085
echo - Notification Service: 8086
echo - Search Service: 8087

echo.
echo Testing health endpoints...
curl -s http://localhost:8081/health 2>nul && echo "User Service: OK" || echo "User Service: NOT READY"
curl -s http://localhost:8088/health 2>nul && echo "Chat Service: OK" || echo "Chat Service: NOT READY"

pause