FROM openjdk:17.0.1-slim
ARG JAR_FILE=build/libs/squash-rest-app-1.0.0-SNAPSHOT.jar
VOLUME /tmp
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
EXPOSE 80
