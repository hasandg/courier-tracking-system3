# First stage: Build custom JRE
FROM eclipse-temurin:21-jdk-alpine AS jre-builder

# Set working directory
WORKDIR /app

# Copy pre-built JAR (this should be built locally with parent POM resolution)
COPY target/*.jar /app/app.jar

# Extract the JAR to analyze dependencies
RUN jar xvf app.jar
RUN jdeps --ignore-missing-deps -q \
    --recursive \
    --multi-release 21 \
    --print-module-deps \
    --class-path 'BOOT-INF/lib/*' \
    app.jar > modules.txt

# Build small JRE image with only required modules
RUN $JAVA_HOME/bin/jlink \
        --verbose \
        --add-modules $(cat modules.txt) \
        --strip-debug \
        --no-man-pages \
        --no-header-files \
        --compress=2 \
        --output /optimized-jdk-21

# Second stage: Create minimal runtime image
FROM alpine:latest
ENV JAVA_HOME=/opt/jdk/jdk-21
ENV PATH="${JAVA_HOME}/bin:${PATH}"

# Copy JRE from the base image
COPY --from=jre-builder /optimized-jdk-21 $JAVA_HOME

# Add app user
ARG APPLICATION_USER=spring

# Create a user to run the application, don't run as root
RUN addgroup --system $APPLICATION_USER && adduser --system $APPLICATION_USER --ingroup $APPLICATION_USER

# Create the application directory
RUN mkdir /app && chown -R $APPLICATION_USER /app

# Copy the final JAR file
COPY --from=jre-builder --chown=$APPLICATION_USER:$APPLICATION_USER /app/app.jar /app/app.jar

WORKDIR /app

USER $APPLICATION_USER

ENTRYPOINT [ "java", "-jar", "/app/app.jar" ]