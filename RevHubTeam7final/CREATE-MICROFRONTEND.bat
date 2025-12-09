@echo off
echo ========================================
echo   Creating Micro-Frontend Architecture
echo ========================================
echo.
echo This will take 1-2 hours to complete.
echo.
echo Step 1: Creating 4 Angular Applications...
echo.

cd c:\Users\ramee\Downloads\RevHubTeam7final\RevHub

echo Creating auth-app (Port 4200)...
call ng new auth-app --routing --style=css --skip-git
cd auth-app
call npm install
cd ..

echo Creating feed-app (Port 4201)...
call ng new feed-app --routing --style=css --skip-git
cd feed-app
call npm install
cd ..

echo Creating profile-app (Port 4202)...
call ng new profile-app --routing --style=css --skip-git
cd profile-app
call npm install
cd ..

echo Creating chat-app (Port 4203)...
call ng new chat-app --routing --style=css --skip-git
cd chat-app
call npm install
cd ..

echo.
echo ========================================
echo   Step 2: Copying Components...
echo ========================================
echo.

echo Copying auth components...
xcopy /E /I /Y frontend\src\app\modules\auth auth-app\src\app\auth
xcopy /E /I /Y frontend\src\app\core auth-app\src\app\core

echo Copying feed components...
xcopy /E /I /Y frontend\src\app\modules\feed feed-app\src\app\feed
xcopy /E /I /Y frontend\src\app\modules\post feed-app\src\app\post
xcopy /E /I /Y frontend\src\app\core feed-app\src\app\core

echo Copying profile components...
xcopy /E /I /Y frontend\src\app\modules\profile profile-app\src\app\profile
xcopy /E /I /Y frontend\src\app\user-profile profile-app\src\app\user-profile
xcopy /E /I /Y frontend\src\app\core profile-app\src\app\core

echo Copying chat components...
copy /Y frontend\src\app\dashboard.component.* chat-app\src\app\
xcopy /E /I /Y frontend\src\app\core chat-app\src\app\core

echo.
echo ========================================
echo   Micro-Frontend Created!
echo ========================================
echo.
echo Next Steps:
echo 1. Configure each app's angular.json for correct ports
echo 2. Update routing in each app
echo 3. Test each app individually
echo.
echo Run each app:
echo   cd auth-app && ng serve --port 4200
echo   cd feed-app && ng serve --port 4201
echo   cd profile-app && ng serve --port 4202
echo   cd chat-app && ng serve --port 4203
echo.
pause
