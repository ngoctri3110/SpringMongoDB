# Stage 1: Build stage
# Tối ưu hóa để giảm kích thước image và thời gian build
FROM maven:3.9.6-eclipse-temurin-17-alpine AS builder

WORKDIR /build

# Copy pom.xml và các source files
COPY pom.xml .
COPY src ./src
COPY .mvn ./.mvn
COPY mvnw .

# Build project - skipping tests để tăng tốc độ build
RUN ./mvnw clean package -DskipTests -q

# Stage 2: Runtime stage
FROM eclipse-temurin:17-jdk-alpine

WORKDIR /app

# Copy JAR from builder stage
COPY --from=builder /build/target/springboot-mongodb-*.jar app.jar

# Expose port
EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=40s --retries=3 \
    CMD wget -q -O- http://localhost:8080/actuator/health || exit 1

# Run application
ENTRYPOINT ["java", "-jar", "app.jar"]

# Alternative: Run with environment variables
# ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
