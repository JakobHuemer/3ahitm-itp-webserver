FROM eclipse-temurin:21-jre-alpine-3.21
RUN mkdir -p /opt/app
WORKDIR /opt/app

COPY ./target/web-server-*.jar ./web-server.jar

CMD ["java", "-jar", "web-server.jar"]
