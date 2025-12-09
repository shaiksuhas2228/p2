@echo off
echo Starting RevHub Micro-Frontend Applications...

echo.
echo Installing dependencies for all applications...

cd shell-app
echo Installing shell-app dependencies...
call npm install
cd ..

cd feed-mfe
echo Installing feed-mfe dependencies...
call npm install
cd ..

cd profile-mfe
echo Installing profile-mfe dependencies...
call npm install
cd ..

cd chat-mfe
echo Installing chat-mfe dependencies...
call npm install
cd ..

echo.
echo Starting all applications...
echo Please wait for all applications to start before accessing the main app.

start "Feed MFE" cmd /k "cd feed-mfe && npm start"
timeout /t 5 /nobreak > nul

start "Profile MFE" cmd /k "cd profile-mfe && npm start"
timeout /t 5 /nobreak > nul

start "Chat MFE" cmd /k "cd chat-mfe && npm start"
timeout /t 5 /nobreak > nul

start "Shell App" cmd /k "cd shell-app && npm start"

echo.
echo All applications are starting...
echo.
echo Access URLs:
echo - Main Application: http://localhost:4200
echo - Feed MFE: http://localhost:4201
echo - Profile MFE: http://localhost:4202
echo - Chat MFE: http://localhost:4203
echo.
echo Press any key to exit...
pause > nul