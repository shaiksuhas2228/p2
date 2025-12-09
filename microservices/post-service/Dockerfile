FROM openjdk:17-jdk-slim

WORKDIR /app

COPY target/post-service-1.0.0.jar app.jar

RUN mkdir -p uploads/posts

EXPOSE 8082

ENTRYPOINT ["java", "-jar", "app.jar"]