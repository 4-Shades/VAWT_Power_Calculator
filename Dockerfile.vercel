# Resolve Maven dependencies in a separate layer so source-only changes can
# reuse the downloaded dependency cache during later builds.
FROM maven:3.9.9-eclipse-temurin-17 AS dependencies
WORKDIR /workspace
COPY pom.xml ./
RUN mvn --batch-mode dependency:go-offline

# Compile the application and create the executable Spring Boot JAR.
FROM dependencies AS build
COPY src ./src
RUN mvn --batch-mode package -DskipTests

# Keep the delivered image small and run the service without root privileges.
FROM eclipse-temurin:17-jre-alpine AS runtime
WORKDIR /app
RUN addgroup -S spring && adduser -S spring -G spring
COPY --from=build /workspace/target/*.jar /app/app.jar
USER spring
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
