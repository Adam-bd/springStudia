# ETAP 1: Budowanie aplikacji (Maven)
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
# Budujemy aplikację pomijając testy (żeby było szybciej na Renderze)
RUN mvn clean package -DskipTests

# ETAP 2: Uruchamianie aplikacji (tylko środowisko Javy)
FROM eclipse-temurin:17-jre
WORKDIR /app
# Kopiujemy zbudowany plik .jar z pierwszego etapu
COPY --from=build /app/target/*.jar app.jar
# Informujemy Render, że aplikacja działa na porcie 8080
EXPOSE 8080
# Komenda startowa
ENTRYPOINT ["java", "-jar", "app.jar"]