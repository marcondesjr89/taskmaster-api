# ==============================================================================
# Multi-Stage Build: TaskMaster Core API (Java 21 + Spring Boot 3.3.3)
# ==============================================================================

# ------------------------------------------------------------------------------
# 1. Build Stage: Compilação e Empacotamento com Maven e JDK 21
# ------------------------------------------------------------------------------
FROM maven:3.9.9-eclipse-temurin-21-alpine AS builder

WORKDIR /app

# Copia apenas o descritor de dependências para aproveitar cache de camadas
COPY pom.xml .

# Baixa as dependências offline (otimização de cache)
RUN mvn dependency:go-offline -B

# Copia o código-fonte
COPY src ./src

# Executa o build empacotando o fat JAR executável do Spring Boot
RUN mvn clean package -DskipTests

# ------------------------------------------------------------------------------
# 2. Runtime Stage: Imagem de execução leve e segura com JRE 21 Alpine
# ------------------------------------------------------------------------------
FROM eclipse-temurin:21-jre-alpine AS runner

# Criação de usuário não-root por boas práticas de segurança
RUN addgroup -S appgroup && adduser -S appuser -G appgroup

WORKDIR /app

# Copia o artefato compilado do estágio builder
COPY --from=builder /app/target/*.jar app.jar

# Garante a posse dos arquivos para o usuário não-root
RUN chown -R appuser:appgroup /app

# Alterna para o usuário não privilegiado
USER appuser

# Documentação da porta exposta pela aplicação
EXPOSE 8080

# Configurações de JVM padrão e inicialização
ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0"

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
