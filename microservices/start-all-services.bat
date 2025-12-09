@echo off
echo Starting all RevHub microservices...

echo Starting User Service (Port 8081)...
start "User Service" cmd /k "cd user-service && mvn spring-boot:run"
timeout /t 5 /nobreak > nul

echo Starting Post Service (Port 8082)...
start "Post Service" cmd /k "cd post-service && mvn spring-boot:run"
timeout /t 5 /nobreak > nul

echo Starting Feed Service (Port 8083)...
start "Feed Service" cmd /k "cd feed-service && mvn spring-boot:run"
timeout /t 5 /nobreak > nul

echo Starting Chat Service (Port 8084)...
start "Chat Service" cmd /k "cd chat-service && mvn spring-boot:run"
timeout /t 5 /nobreak > nul

echo Starting Follow Service (Port 8085)...
start "Follow Service" cmd /k "cd follow-service && mvn spring-boot:run"
timeout /t 5 /nobreak > nul

echo Starting Notification Service (Port 8086)...
start "Notification Service" cmd /k "cd notification-service && mvn spring-boot:run"
timeout /t 5 /nobreak > nul

echo Starting Search Service (Port 8087)...
start "Search Service" cmd /k "cd search-service && mvn spring-boot:run"

echo.
echo All services are starting...
echo Wait 30-60 seconds for all services to fully start.
echo.
echo Service URLs:
echo - User Service: http://localhost:8081
echo - Post Service: http://localhost:8082  
echo - Feed Service: http://localhost:8083
echo - Chat Service: http://localhost:8084
echo - Follow Service: http://localhost:8085
echo - Notification Service: http://localhost:8086
echo - Search Service: http://localhost:8087
echo.
pause