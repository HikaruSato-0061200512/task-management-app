FROM openjdk:17-jdk-slim

WORKDIR /app

# Maven Wrapperをコピー
COPY .mvn/ .mvn/
COPY mvnw pom.xml ./

# 依存関係をダウンロード（キャッシュ効率化）
RUN ./mvnw dependency:go-offline

# ソースコードをコピー
COPY src/ src/

# アプリケーションをビルド
RUN ./mvnw clean package -DskipTests

# 実行時のポート
EXPOSE 8080

# アプリケーション実行
CMD ["java", "-jar", "target/*.jar"]
