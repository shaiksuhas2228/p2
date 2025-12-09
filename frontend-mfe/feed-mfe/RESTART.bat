@echo off
echo Restarting Feed MFE...
taskkill /F /PID 5756
timeout /t 2
start cmd /k "npm start"
echo Feed MFE restarted on http://localhost:4201
