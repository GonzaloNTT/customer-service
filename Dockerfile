# Etapa 1: Build
FROM maven:3.9.4-eclipse-temurin-17 AS builder
WORKDIR /app

# Copiar pom.xml y src
COPY pom.xml .
COPY src ./src
COPY checkstyle.xml .

# Copiar el JAR de schemas-avro al contenedor
COPY libs/schemas-avro-1.0.0.jar /tmp/schemas-avro-1.0.0.jar

# Instalarlo en el repositorio Maven local
RUN mvn install:install-file \
    -Dfile=/tmp/schemas-avro-1.0.0.jar \
    -DgroupId=com.bootcamp \
    -DartifactId=schemas-avro \
    -Dversion=1.0.0 \
    -Dpackaging=jar

# Verifica que esté donde esperas
RUN ls -l /tmp

# Construir microservicio sin tests
RUN mvn clean package -DskipTests

# Etapa 2: Runtime
FROM eclipse-temurin:17-jdk
WORKDIR /app
COPY --from=builder /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
