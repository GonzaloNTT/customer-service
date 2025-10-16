# ===============================
# 🏗️ Etapa 1: Build
# ===============================
FROM maven:3.9.9-eclipse-temurin-21 AS builder
WORKDIR /app

# 🔹 Copiar POM y archivos de configuración (para usar caché de dependencias)
COPY pom.xml .
COPY checkstyle.xml .
COPY libs/schemas-avro-1.0.0.jar /tmp/schemas-avro-1.0.0.jar

# 🔹 Instalar el JAR local en el repo Maven (no tocar)
RUN mvn install:install-file \
    -Dfile=/tmp/schemas-avro-1.0.0.jar \
    -DgroupId=com.bootcamp \
    -DartifactId=schemas-avro \
    -Dversion=1.0.0 \
    -Dpackaging=jar

# ✅ Usar un settings.xml para mejorar estabilidad de red y reintentos
# (lo creamos inline en el contenedor)
RUN echo '<settings xmlns="http://maven.apache.org/SETTINGS/1.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://maven.apache.org/SETTINGS/1.0.0 https://maven.apache.org/xsd/settings-1.0.0.xsd"> \
  <mirrors> \
    <mirror> \
      <id>central-fast</id> \
      <mirrorOf>central</mirrorOf> \
      <url>https://repo1.maven.org/maven2</url> \
    </mirror> \
  </mirrors> \
  <profiles> \
    <profile> \
      <id>retries</id> \
      <properties> \
        <maven.wagon.http.retryHandler.count>3</maven.wagon.http.retryHandler.count> \
        <maven.wagon.http.retryHandler.requestSentEnabled>true</maven.wagon.http.retryHandler.requestSentEnabled> \
      </properties> \
    </profile> \
  </profiles> \
  <activeProfiles><activeProfile>retries</activeProfile></activeProfiles> \
</settings>' > /root/.m2/settings.xml

# ✅ Descargar dependencias con reintentos y sin saturar red
RUN mvn -B dependency:go-offline -s /root/.m2/settings.xml || true

# 🔹 Copiar el código fuente después (para usar cache)
COPY src ./src

# ✅ Compilar sin tests
RUN mvn clean package -DskipTests -s /root/.m2/settings.xml


# ===============================
# 🚀 Etapa 2: Runtime
# ===============================
FROM eclipse-temurin:21-jre
WORKDIR /app

# 🔹 Copiar el JAR final
COPY --from=builder /app/target/customer-service-0.0.1-SNAPSHOT.jar app.jar

# 🔹 Exponer el puerto del microservicio
EXPOSE 8082

# ✅ Ejecutar la app
ENTRYPOINT ["java", "-jar", "app.jar"]
