@echo off
echo Starting RevHub Micro-Frontend Architecture...
echo.
echo Opening 4 terminals for micro-frontends...
echo.

start cmd /k "cd /d %~dp0frontend && echo Starting Shell App (Port 4200)... && npm start"
timeout /t 3 /nobreak >nul

start cmd /k "cd /d %~dp0frontend-mfe\feed-mfe && echo Starting Feed MFE (Port 4201)... && npm start"
timeout /t 3 /nobreak >nul

start cmd /k "cd /d %~dp0frontend-mfe\profile-mfe && echo Starting Profile MFE (Port 4202)... && npm start"
timeout /t 3 /nobreak >nul

start cmd /k "cd /d %~dp0frontend-mfe\chat-mfe && echo Starting Chat MFE (Port 4203)... && npm start"

echo.
echo All micro-frontends are starting...
echo Wait for compilation to complete, then open: http://localhost:4200
echo.
pause
