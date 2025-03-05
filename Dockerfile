# Usar una imagen base con Maven y Java instalados
FROM maven:3.8.4-openjdk-17 AS build

# Directorio de trabajo dentro del contenedor
WORKDIR /app

# Copiar el archivo pom.xml y el código fuente
COPY pom.xml .
COPY src ./src

# Construir el proyecto con Maven
RUN mvn clean package -DskipTests

# Segunda etapa: Crear una imagen ligera solo con el JAR
FROM openjdk:17-jdk-alpine

# Directorio de trabajo dentro del contenedor
WORKDIR /app

# Copiar el JAR generado en la etapa anterior
COPY --from=build /app/target/order-service.jar order-service.jar

# Exponer el puerto en el que corre la aplicación
EXPOSE 8080

# Comando para ejecutar la aplicación
ENTRYPOINT ["java", "-jar", "order-service.jar"]