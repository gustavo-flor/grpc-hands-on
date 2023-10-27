FROM maven:3.8.5-openjdk-17-slim AS build

COPY pom.xml .
COPY src src

RUN mvn clean install --batch-mode -DskipTests

FROM openjdk:17-slim AS bundle

COPY --from=build /target/*jar-with-dependencies.jar /app.jar
COPY docker-entrypoint.sh /

RUN chmod +x /docker-entrypoint.sh

ENTRYPOINT ["/docker-entrypoint.sh"]