#maven
FROM maven:latest AS maven
LABEL MAINTAINER="LDC-devs"

WORKDIR /build
COPY . /build
# Compile and package the application to an executable JAR
RUN mvn package -DskipTests

#java
FROM ghcr.io/graalvm/jdk:ol9-java17 as backend
ENV JAVA_OPTS "-XX:MaxRAMPercentage=60 -Djava.security.egd=file:/dev/./urandom"
ARG JAR_FILE=backend.jar

ENV APP_HOME /opt/app
WORKDIR $APP_HOME

# Copy the backend jar from the maven stage to the /opt/app directory of the current stage.
COPY --from=maven /build/target/${JAR_FILE} $APP_HOME

EXPOSE 8080

ENTRYPOINT exec java $JAVA_OPTS -jar $APP_HOME/backend.jar
