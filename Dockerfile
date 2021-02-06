FROM openjdk:8-jdk-alpine

COPY target/*.jar /opt/app/

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/opt/app/event-handler-0.0.1.jar"]