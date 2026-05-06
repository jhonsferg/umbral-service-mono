# 🚀 Setup Guía de Instalación - Umbral Backend Spring Boot

Esta guía te ayudará a configurar el ambiente de desarrollo para el backend de Umbral.

## 📋 Tabla de Contenidos

1. [Prerrequisitos](#prerrequisitos)
2. [Instalación Rápida](#instalación-rápida)
3. [Configuración Detallada](#configuración-detallada)
4. [Verificación](#verificación)
5. [Troubleshooting](#troubleshooting)

## 📦 Prerrequisitos

### Windows

1. **Java 21 JDK**
   - Descargar desde [Oracle](https://www.oracle.com/java/technologies/downloads/#java21)
   - O usando Chocolatey: `choco install openjdk21`

2. **Maven 3.9+**
   - Descargar desde [Maven.org](https://maven.apache.org/download.cgi)
   - O usando Chocolatey: `choco install maven`

3. **PostgreSQL 12+**
   - Descargar desde [postgresql.org](https://www.postgresql.org/download/windows/)
   - O usando Chocolatey: `choco install postgresql`
   - Usuario default: `postgres`
   - Contraseña: [la que configuraste]

4. **Redis (Opcional)**
   - Usar Windows Subsystem for Linux (WSL2)
   - O usar Docker: `docker run -d -p 6379:6379 redis`

5. **Git**
   - Descargar desde [git-scm.com](https://git-scm.com/)

### macOS

```bash
# Usando Homebrew
brew install openjdk@21
brew install maven
brew install postgresql
brew install redis
```

### Linux (Ubuntu/Debian)

```bash
sudo apt update
sudo apt install openjdk-21-jdk maven postgresql postgresql-contrib redis-server
```

## ⚡ Instalación Rápida

Si ya tienes todos los requisitos, sigue estos pasos:

```bash
# 1. Clonar repositorio
git clone <repo-url>
cd umbral-backend-spring

# 2. Copiar configuración
cp .env.example .env

# 3. Compilar
mvn clean install

# 4. Ejecutar
mvn spring-boot:run

# 5. Acceder a Swagger
# http://localhost:3001/api/v1/swagger-ui.html
```

## 🔧 Configuración Detallada

### Paso 1: Verificar Versiones

```bash
java -version
# Debería mostrar: 21.x.x

mvn -version
# Debería mostrar: 3.9.x

psql --version
# Debería mostrar: psql (PostgreSQL) 12+
```

### Paso 2: Configurar PostgreSQL

#### Windows (con pgAdmin o psql)

```bash
# Abrir psql como administrador
psql -U postgres

# Dentro de psql, ejecutar:
CREATE DATABASE umbral_local;
CREATE DATABASE umbral_dev;
CREATE DATABASE umbral_test;
CREATE DATABASE umbral_prod;

# Verificar
\l

# Salir
\q
```

#### macOS/Linux (terminal)

```bash
# Crear bases de datos
createdb -U postgres umbral_local
createdb -U postgres umbral_dev
createdb -U postgres umbral_test
createdb -U postgres umbral_prod

# Verificar
psql -U postgres -l | grep umbral
```

### Paso 3: Configurar Redis (Opcional)

#### Windows (con Docker)

```bash
docker run -d -p 6379:6379 --name umbral-redis redis:latest
```

#### macOS

```bash
brew services start redis
```

#### Linux

```bash
sudo systemctl start redis-server
sudo systemctl enable redis-server
```

### Paso 4: Configurar Variables de Entorno

**Editar `.env`:**

```env
# Desarrollo local
SPRING_PROFILES_ACTIVE=local

# Base de Datos
DB_HOST=localhost
DB_PORT=5432
DB_NAME=umbral_local
DB_USER=postgres
DB_PASSWORD=tu_contraseña_postgres

# Redis
REDIS_HOST=localhost
REDIS_PORT=6379
REDIS_PASSWORD=

# JWT (Cambiar en producción!)
JWT_ACCESS_SECRET=tu-clave-super-secreta-acceso-123456789
JWT_REFRESH_SECRET=tu-clave-super-secreta-refresh-123456789
JWT_ISSUER=umbral

# Frontend
FRONTEND_URL=http://localhost:3000

# Correo (Gmail)
# 1. Activar 2FA en Google
# 2. Generar "App Password" en myaccount.google.com
MAIL_HOST=smtp.gmail.com
MAIL_PORT=587
MAIL_USERNAME=tu-email@gmail.com
MAIL_PASSWORD=tu-app-password-google

# Aplicación
APP_PORT=3001
UPLOAD_DIR=./uploads
```

### Paso 5: Compilar Proyecto

```bash
# Descargar dependencias y compilar
mvn clean install

# Si algo falla, limpiar caché
mvn clean

# Después, intentar de nuevo
mvn install
```

## ✅ Verificación

### Verificar Instalación

```bash
# 1. Verificar Java
java -version

# 2. Verificar Maven
mvn -version

# 3. Verificar PostgreSQL
psql -U postgres -c "SELECT version();"

# 4. Verificar Redis (opcional)
redis-cli ping
# Output: PONG
```

### Ejecutar Aplicación

```bash
# Modo desarrollo (hot reload)
mvn spring-boot:run

# O desde IDE IntelliJ:
# Click derecho en UmbralApplication.java > Run
```

### Verificar que funciona

Una vez ejecutado, en otra terminal:

```bash
# Health check
curl http://localhost:3001/api/v1/actuator/health

# Response:
# {"status":"UP"}
```

### Acceder a Swagger UI

```
http://localhost:3001/api/v1/swagger-ui.html
```

Deberías ver la documentación interactiva de todos los endpoints.

## 🧪 Prueba de Endpoints

### Registrar Usuario

```bash
curl -X POST http://localhost:3001/api/v1/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "email": "test@example.com",
    "password": "password123",
    "firstName": "John",
    "lastName": "Doe"
  }'
```

### Login

```bash
curl -X POST http://localhost:3001/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "test@example.com",
    "password": "password123"
  }'
```

Respuesta (guardar `accessToken`):

```json
{
  "accessToken": "eyJhbGciOiJIUzUxMiIsInR5cCI6IkpXVCJ9...",
  "refreshToken": "eyJhbGciOiJIUzUxMiIsInR5cCI6IkpXVCJ9...",
  "user": { ... }
}
```

### Usar Access Token

```bash
curl http://localhost:3001/api/v1/auth/profile \
  -H "Authorization: Bearer eyJhbGciOiJIUzUxMiIsInR5cCI6IkpXVCJ9..."
```

## 🐛 Troubleshooting

### Error: "java: command not found"

**Solución:**
```bash
# Verificar instalación de Java
java -version

# Si no está instalado, instalarlo
# Windows: Descargar de oracle.com
# macOS: brew install openjdk@21
# Linux: sudo apt install openjdk-21-jdk
```

### Error: "mvn: command not found"

**Solución:**
```bash
# Verificar instalación de Maven
mvn -version

# Si no está, instalarlo
# Windows: Descargar de maven.apache.org
# macOS: brew install maven
# Linux: sudo apt install maven
```

### Error: "Could not connect to database"

**Solución:**
```bash
# 1. Verificar que PostgreSQL esté corriendo
# Windows: Verificar en Services
# macOS: brew services list
# Linux: sudo systemctl status postgresql

# 2. Verificar credenciales en .env
DB_HOST=localhost
DB_PORT=5432
DB_USER=postgres
DB_PASSWORD=tu_contraseña

# 3. Verificar que la BD existe
psql -U postgres -l | grep umbral_local

# 4. Si la BD no existe, crearla
psql -U postgres -c "CREATE DATABASE umbral_local;"
```

### Error: "Connection refused" (Redis)

**Solución:**
```bash
# 1. Verificar que Redis está corriendo
redis-cli ping

# 2. Si no está, iniciar Redis
# macOS: brew services start redis
# Linux: sudo systemctl start redis-server
# Windows con Docker: docker start umbral-redis

# 3. Si sigue fallando, desabilitar Redis (opcional)
# En application-local.yaml:
# spring:
#   cache:
#     type: simple  # Cambiar de redis a simple
```

### Error: "Port 3001 already in use"

**Solución:**
```bash
# Opción 1: Cambiar puerto en .env
APP_PORT=3002

# Opción 2: Matar proceso que usa el puerto
# Windows: netstat -ano | findstr :3001
# macOS/Linux: lsof -i :3001
```

### Error: "JWT validation failed"

**Solución:**
```bash
# Asegurar que JWT_ACCESS_SECRET sea igual en .env
JWT_ACCESS_SECRET=tu-clave-secreta-acceso-123456789
JWT_REFRESH_SECRET=tu-clave-secreta-refresh-123456789

# Cambiar las claves si es necesario
# Importante: Usar claves diferentes en producción!
```

### Error: "CORS error" en el frontend

**Solución:**
```bash
# Verificar FRONTEND_URL en .env
FRONTEND_URL=http://localhost:3000

# Debe coincidir con donde está corriendo el frontend

# Si sigue fallando, verificar en config/CorsConfig.java
```

### Error: "Email send failed"

**Solución:**
```bash
# 1. Verificar credenciales de Gmail en .env
MAIL_USERNAME=tu-email@gmail.com
MAIL_PASSWORD=tu-app-password-google

# 2. Activar 2FA en Google
# myaccount.google.com > Security > 2-Step Verification

# 3. Generar "App Password"
# myaccount.google.com > App passwords > Select app

# 4. Usar el App Password, no tu contraseña normal

# 5. Verificar MAIL_HOST y MAIL_PORT
MAIL_HOST=smtp.gmail.com
MAIL_PORT=587
```

## 📚 Recursos Útiles

- [Spring Boot Docs](https://spring.io/projects/spring-boot)
- [Spring Data JPA](https://spring.io/projects/spring-data-jpa)
- [Spring Security](https://spring.io/projects/spring-security)
- [PostgreSQL Docs](https://www.postgresql.org/docs/)
- [Redis Docs](https://redis.io/documentation)

## 💡 Consejos

1. **Usar IDE**: IntelliJ IDEA (Community es suficiente)
2. **Plugins útiles**: Lombok, Maven Helper
3. **Hot reload**: Spring Dev Tools automáticamente recarga cambios
4. **Tests**: Ejecutar `mvn test` frecuentemente
5. **Logs**: Ver en `logs/` directory para debugging

## ❓ Preguntas Frecuentes

**P: ¿Necesito Redis para desarrollo?**
A: No es obligatorio. Spring Boot puede usar caché simple en memoria.

**P: ¿Puedo usar otro SMTP que no sea Gmail?**
A: Sí, configurar MAIL_HOST, MAIL_PORT, MAIL_USERNAME, MAIL_PASSWORD según el proveedor.

**P: ¿Cómo cambio el puerto de la aplicación?**
A: Editar `.env`: `APP_PORT=3002` o en `application-local.yaml`: `server: port: 3002`

**P: ¿Cómo ejecuto tests?**
A: `mvn test` o `mvn verify` para incluir tests de integración.

---

¡Listo! Deberías tener el backend corriendo localmente. Si hay problemas, revisa los logs en `logs/local.log`.
