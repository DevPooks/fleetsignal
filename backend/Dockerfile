FROM maven:3.9.11-eclipse-temurin-21 AS build
WORKDIR /workspace
COPY pom.xml .
RUN mvn -B dependency:go-offline
COPY src src
RUN mvn -B -DskipTests package

FROM eclipse-temurin:21-jre
RUN apt-get update \
    && apt-get install --no-install-recommends -y curl \
    && rm -rf /var/lib/apt/lists/* \
    && useradd --system --uid 10001 fleetsignal
WORKDIR /app
COPY --from=build /workspace/target/fleetsignal-backend-*.jar app.jar
USER fleetsignal
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
