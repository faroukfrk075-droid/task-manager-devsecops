FROM eclipse-temurin:21-jre-alpine

# Create a non-root system group and user for security (Section 5.2 requirement)
RUN addgroup -S appgroup && adduser -S appuser -G appgroup

WORKDIR /app

# Copy only the packaged JAR and assign ownership to the non-root user
COPY --chown=appuser:appgroup target/task-manager-1.0.0.jar app.jar

# Switch to non-root user before running the application
USER appuser

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
