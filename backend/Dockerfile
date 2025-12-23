# 使用Maven构建应用
FROM maven:3-openjdk-17 AS builder
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# 使用OpenJDK运行应用
FROM openjdk:17-jdk-slim
WORKDIR /app
COPY --from=builder /app/target/cloud-manager-backend-1.0.0.jar app.jar

# 暴露端口
EXPOSE 8080

# 运行应用
ENTRYPOINT ["java", "-jar", "app.jar"]