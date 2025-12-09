@echo off
echo ========================================
echo   RevHub Application Starter
echo ========================================
echo.
echo Starting Frontend on Port 4200...
echo.
cd frontend
start cmd /k "npm start"
echo.
echo ========================================
echo   Application Starting!
echo ========================================
echo.
echo Wait for compilation, then open:
echo http://localhost:4200
echo.
echo Make sure all backend services are running:
echo - user-service (8081)
echo - post-service (8082)
echo - feed-service (8083)
echo - follow-service (8084)
echo - notification-service (8085)
echo - chat-service (8086)
echo - search-service (8087)
echo.
pause
