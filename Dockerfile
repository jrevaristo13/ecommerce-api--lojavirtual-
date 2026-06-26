# ==========================================
# Stage 1: Build da aplicação
# ==========================================
FROM eclipse-temurin:21-jdk-alpine AS builder

# Instalar Maven
RUN apk add --no-cache maven

WORKDIR /app

# Copiar apenas o pom.xml primeiro (cache de dependências)
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copiar o código fonte
COPY src ./src

# Build da aplicação (pula testes para acelerar)
RUN mvn clean package -DskipTests -B

# ==========================================
# Stage 2: Imagem final (menor)
# ==========================================
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Instalar wget para health check
RUN apk add --no-cache wget

# Copiar o JAR da stage anterior
COPY --from=builder /app/target/*.jar app.jar

# Expor porta
EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=40s \
  CMD wget --no-verbose --tries=1 --spider http://localhost:8080/actuator/health || exit 1

# Executar a aplicação
ENTRYPOINT ["java", "-jar", "app.jar"]
