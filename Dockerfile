FROM openjdk:17-jdk-slim

WORKDIR /app

# Maven Wrapperをコピー
COPY .mvn/ .mvn/
COPY mvnw pom.xml ./
RUN chmod +x mvnw

# ソースコードをコピー
COPY src/ src/

# アプリケーションをビルド
RUN ./mvnw clean package -DskipTests

# 正確なJARファイル名でコピー
RUN cp target/tasks_app-0.0.1-SNAPSHOT.jar app.jar

# ポート設定
EXPOSE 8080

# アプリケーション実行
CMD ["java", "-jar", "app.jar"]
