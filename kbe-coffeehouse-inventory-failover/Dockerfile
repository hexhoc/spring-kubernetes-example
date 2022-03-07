#Start with a base image containing Java runtime
FROM openjdk:17
# Add Maintainer Info
LABEL maintainer="Vladislav Zhuravsky <hexhoc@gmail.com>"

# The application's jar file
ARG JAR_FILE=target/kbe-coffeehouse-inventory-failover-1.0.0-SNAPSHOT.jar

# Add the application's jar to the container
COPY ${JAR_FILE} app.jar

WORKDIR /
#execute the application after container is run
ENTRYPOINT java -jar /app.jar