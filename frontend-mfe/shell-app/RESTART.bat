@echo off
echo Restarting Shell App...
taskkill /F /PID 16848
timeout /t 2
start cmd /k "npm start"
echo Shell App restarted on http://localhost:4200
