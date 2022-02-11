FROM maven:3.8.4-jdk-8-slim AS build
RUN mkdir -p /workspace
WORKDIR /workspace
COPY pom.xml /workspace
COPY src /workspace/src
RUN mvn -f pom.xml clean package

FROM openjdk:8-jdk-slim
COPY --from=build /workspace/target/*.jar service-1.0.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","service-1.0.jar"]