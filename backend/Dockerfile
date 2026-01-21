# 1. Fase de construcción (Maven)
FROM maven:3.9.6-eclipse-temurin-21-alpine AS build
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

# 2. Fase de ejecución
FROM eclipse-temurin:21-jdk-alpine
WORKDIR /app
# Copiamos el jar (verifica que el nombre coincida con tu pom.xml)
COPY --from=build /app/target/backend-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080

# IMPORTANTE: Usamos sh -c para que el sistema reconozca la variable $JAVA_OPTS
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -Xmx512M -jar app.jar"]
