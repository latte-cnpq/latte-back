# Estágio 1: Construção da Aplicação com Maven
FROM maven:3.8.4-openjdk-17 AS MAVEN_BUILD

COPY pom.xml /build/
COPY src/main /build/src/main
COPY src/main/resources /build/src/main/resources

WORKDIR /build/
RUN mvn -e package

# Estágio 2: Execução da Aplicação com o JAR compilado
FROM openjdk:17-alpine

WORKDIR /app

COPY --from=MAVEN_BUILD /build/target/build.jar /app/
COPY --from=MAVEN_BUILD /build/src/main/resources /app/src/main/resources

ENTRYPOINT ["java", "-jar", "build.jar"]
