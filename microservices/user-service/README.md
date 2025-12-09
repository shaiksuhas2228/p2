# User Service

Authentication and user profile management microservice for RevHub.

## Features
- User registration and login
- JWT token generation
- Password encryption
- Profile management

## Database
- **Type**: MySQL
- **Database**: `revhub_users`
- **Port**: 3307 (containerized)

## Endpoints
- `POST /api/auth/register` - User registration
- `POST /api/auth/login` - User login
- `GET /health` - Health check

## Development
```bash
mvn spring-boot:run
```

## Docker Deployment
```bash
# Build
./build.sh

# Deploy
./deploy.sh
```

## Environment Variables
- `SPRING_DATASOURCE_URL`
- `SPRING_DATASOURCE_USERNAME`
- `SPRING_DATASOURCE_PASSWORD`

## Port
- **Service**: 8081
- **Database**: 3307