# ==========================================
# STAGE 1: BUILD THE APPLICATION
# ==========================================
# Kita pakai JDK Alpine biar proses compile Maven cepat dan image-nya relatif kecil
FROM eclipse-temurin:21-jdk-alpine AS build

# Set working directory di dalam container
WORKDIR /app

# Copy semua file project ke dalam container
COPY . .

# Jalankan Maven untuk build .jar file. 
# -DskipTests biar build cepat (kita asumsikan test sudah dijalankan di CI/CD pipeline)
RUN ./mvnw clean package -DskipTests

# ==========================================
# STAGE 2: CREATE THE RUNTIME IMAGE
# ==========================================
# Kita cuma butuh JRE (Java Runtime Environment) untuk menjalankan .jar, nggak butuh JDK (yang ada compiler-nya)
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Copy HANYA file .jar dari stage 'build' ke stage ini.
# Ini yang bikin image final kita kecil! (Layer caching juga bikin rebuild cepat)
COPY --from=build /app/target/*.jar app.jar

# Expose port yang dipakai aplikasi
EXPOSE 8080

# Command untuk menjalankan aplikasi
ENTRYPOINT ["java", "-jar", "app.jar"]