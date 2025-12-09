@echo off
echo Adding Spring Cloud Consul to all microservices...
echo.

REM This script adds Consul dependencies to all backend services
REM You need to manually add these to each pom.xml:

echo Services to update:
echo - post-service
echo - chat-service  
echo - feed-service
echo - follow-service
echo - notification-service
echo - search-service
echo.

echo Add to each pom.xml PROPERTIES section:
echo ^<spring-cloud.version^>2023.0.3^</spring-cloud.version^>
echo.

echo Add to each pom.xml DEPENDENCIES section:
echo ^<dependency^>
echo     ^<groupId^>org.springframework.cloud^</groupId^>
echo     ^<artifactId^>spring-cloud-starter-consul-discovery^</artifactId^>
echo ^</dependency^>
echo.

echo Add BEFORE ^</dependencies^> closing tag:
echo ^<dependencyManagement^>
echo     ^<dependencies^>
echo         ^<dependency^>
echo             ^<groupId^>org.springframework.cloud^</groupId^>
echo             ^<artifactId^>spring-cloud-dependencies^</artifactId^>
echo             ^<version^>${spring-cloud.version}^</version^>
echo             ^<type^>pom^</type^>
echo             ^<scope^>import^</scope^>
echo         ^</dependency^>
echo     ^</dependencies^>
echo ^</dependencyManagement^>
echo.

echo Add to each application.properties:
echo spring.cloud.consul.host=localhost
echo spring.cloud.consul.port=8500
echo spring.cloud.consul.discovery.enabled=true
echo spring.cloud.consul.discovery.instance-id=${spring.application.name}:${random.value}
echo spring.cloud.consul.discovery.health-check-path=/actuator/health
echo spring.cloud.consul.discovery.health-check-interval=10s
echo management.endpoints.web.exposure.include=health,info
echo.

echo Add @EnableDiscoveryClient to each main application class
echo.

pause
