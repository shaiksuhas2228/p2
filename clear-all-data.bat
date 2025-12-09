@echo off
echo Clearing all database data...
echo.

echo Dropping MySQL databases...
"C:\Program Files\MySQL\MySQL Server 8.0\bin\mysql.exe" -u root -p10532 -e "DROP DATABASE IF EXISTS user_service_db; DROP DATABASE IF EXISTS post_service_db; DROP DATABASE IF EXISTS feed_service_db; DROP DATABASE IF EXISTS follow_service_db; DROP DATABASE IF EXISTS notification_service_db; DROP DATABASE IF EXISTS search_service_db;"

echo.
echo Dropping MongoDB database...
"C:\Program Files\MongoDB\Server\7.0\bin\mongosh.exe" --eval "use chat_service_db; db.dropDatabase();"

echo.
echo All databases cleared!
pause
