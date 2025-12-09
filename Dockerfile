# ---------- BUILD ----------
FROM maven:3.9.4-eclipse-temurin-17 AS build
WORKDIR /app

# copy maven files (cache deps)
COPY pom.xml ./
COPY .mvn .mvn
COPY mvnw mvnw
RUN mvn -q -N -e -B dependency:go-offline

# copy source and build
COPY src src
RUN mvn -B -e -DskipTests package

# ---------- RUNTIME ----------
FROM eclipse-temurin:17-jre-alpine
ARG JAR_FILE=/app/target/*.jar
COPY --from=build /app/target/*.jar /app/app.jar

# optional unprivileged user
RUN addgroup -S jobconnect && adduser -S jobconnect -G jobconnect
USER jobconnect

EXPOSE 8080
ENTRYPOINT ["java","-jar","/app/app.jar"]
