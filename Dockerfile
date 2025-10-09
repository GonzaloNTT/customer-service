# ===============================
# 🏗️ Etapa 1: Build
# ===============================
FROM maven:3.9.4-eclipse-temurin-21 AS builder
WORKDIR /app

# Copiar POM y dependencias primero (para aprovechar la cache de Docker)
COPY pom.xml .
COPY checkstyle.xml .
COPY libs/schemas-avro-1.0.0.jar /tmp/schemas-avro-1.0.0.jar

# Instalar el JAR local en el repo Maven
RUN mvn install:install-file \
    -Dfile=/tmp/schemas-avro-1.0.0.jar \
    -DgroupId=com.bootcamp \
    -DartifactId=schemas-avro \
    -Dversion=1.0.0 \
    -Dpackaging=jar

# Descargar dependencias antes de copiar src (mejor cacheo)
RUN mvn dependency:go-offline

# Copiar el código fuente
COPY src ./src

# Compilar sin tests
RUN mvn clean package -DskipTests


# ===============================
# 🚀 Etapa 2: Runtime
# ===============================
FROM eclipse-temurin:21-jre
WORKDIR /app

# Copiar el JAR compilado
COPY --from=builder /app/target/*.jar app.jar

# Exponer el puerto del microservicio
EXPOSE 8080

# Ejecutar la aplicación
ENTRYPOINT ["java", "-jar", "app.jar"]
