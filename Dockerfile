FROM maven:3.8.4-openjdk-17 AS MAVEN_BUILD

COPY pom.xml /build/
COPY src/main /build/src/main

WORKDIR /build/
RUN mvn -e package

FROM openjdk:17-alpine

WORKDIR /app

COPY --from=MAVEN_BUILD /build/target/build.jar /app/

ENTRYPOINT ["java", "-jar", "build.jar"]