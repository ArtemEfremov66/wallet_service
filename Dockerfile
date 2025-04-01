# Собираем приложение
FROM eclipse-temurin:17-jdk-jammy as builder
WORKDIR /app
COPY .mvn/ .mvn
COPY mvnw pom.xml ./
RUN ./mvnw dependency:go-offline
COPY src ./src
RUN ./mvnw package -DskipTests

# Финальный образ
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app
COPY --from=builder /app/target/wallet-service-0.0.1-SNAPSHOT.jar ./app.jar
COPY src/main/docker/entrypoint.sh /entrypoint.sh
RUN chmod +x /entrypoint.sh
RUN apt-get update && apt-get install -y postgresql-client

EXPOSE 8080
ENTRYPOINT ["/entrypoint.sh"]
