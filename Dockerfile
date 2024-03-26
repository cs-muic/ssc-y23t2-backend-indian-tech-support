FROM openjdk:latest
VOLUME /tmp
ARG JAR_FILE=target/securing-web-complete-0.0.1-SNAPSHOT-spring-boot.jar
COPY ${JAR_FILE} app.jar
ENV TZ=Asia/Bangkok
EXPOSE 8081
ENTRYPOINT ["java", "-jar","/app.jar"]