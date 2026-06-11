# ==========================================
# STAGE 1: BUILD THE APPLICATION
# ==========================================
FROM eclipse-temurin:21-jdk-alpine AS build
WORKDIR /app

# 1. COPY DEPENDENCY FILES ONLY
# Kita copy pom.xml dan Maven wrapper dulu. 
# Layer ini akan di-CACHE oleh Docker selama pom.xml tidak berubah!
COPY pom.xml mvnw ./
COPY .mvn .mvn

# 2. DOWNLOAD DEPENDENCIES (OFFLINE MODE)
# Ini cuma jalan kalau ada perubahan di pom.xml. 
# Kalau lu cuma ubah code Java, step ini di-SKIP (super cepat!).
RUN ./mvnw dependency:go-offline

# 3. COPY SOURCE CODE
# Baru kita copy folder src yang isinya code Java lu.
COPY src ./src

# 4. BUILD THE JAR
# Compile code jadi .jar. Step ini yang jalan tiap kali lu update code.
RUN ./mvnw clean package -DskipTests

# ==========================================
# STAGE 2: CREATE THE RUNTIME IMAGE
# ==========================================
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Copy HANYA file .jar dari stage 'build'
COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]