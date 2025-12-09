@echo off
echo ========================================
echo   Rebuilding Feed Service
echo ========================================
echo.
echo Cleaning and rebuilding...
call mvn clean install -DskipTests
echo.
echo ========================================
echo   Build Complete!
echo ========================================
echo.
echo Now restart the feed-service
pause
