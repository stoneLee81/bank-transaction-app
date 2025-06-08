# 使用多阶段构建，减小最终镜像大小
FROM maven:3.9.6-eclipse-temurin-21-alpine AS build

# 设置工作目录
WORKDIR /app

# 复制 Maven 配置文件
COPY settings.xml /root/.m2/settings.xml

# 复制pom.xml文件，充分利用Docker层缓存
COPY pom.xml .

# 下载依赖（这一步会被缓存，除非pom.xml发生变化）
RUN mvn dependency:go-offline -B

# 复制源代码
COPY src ./src

# 构建应用（跳过测试以加快构建速度）
RUN mvn clean package -DskipTests

# 运行阶段：使用更小的JRE镜像
FROM eclipse-temurin:21-jre-alpine

# 安装curl用于健康检查
RUN apk add --no-cache curl

# 创建非root用户运行应用（安全考虑）
RUN addgroup -g 1001 appgroup && adduser -u 1001 -G appgroup -s /bin/sh -D appuser

# 设置工作目录
WORKDIR /app

# 从构建阶段复制jar文件
COPY --from=build /app/target/*.jar app.jar

# 修改文件所有者
RUN chown appuser:appgroup app.jar

# 切换到非root用户
USER appuser

# 暴露端口
EXPOSE 8080

# 设置JVM参数优化容器运行
ENV JAVA_OPTS="-Xms256m -Xmx512m -XX:+UseG1GC -XX:+UseContainerSupport"

# 健康检查
HEALTHCHECK --interval=30s --timeout=3s --start-period=40s --retries=3 \
    CMD curl -f http://localhost:8080/actuator/health || exit 1

# 启动应用
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"] 