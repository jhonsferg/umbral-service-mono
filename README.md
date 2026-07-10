# Umbral Spring Boot Backend

![Build Status](https://img.shields.io/badge/build-passing-brightgreen)
![Version](https://img.shields.io/badge/version-1.0.0-blue)
![License](https://img.shields.io/badge/license-MIT-green)
![Java](https://img.shields.io/badge/java-21-red)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.0.5-green)
![Gradle](https://img.shields.io/badge/Gradle-8.10-blue)

Plataforma de gestion financiera empresarial construida con **Spring Boot 4.0.5**, **Spring Security**, **JWT**, **PostgreSQL** y **Redis**. Aplicacion backend completa con autenticacion multi-factor, gestion de transacciones, presupuestos, metas financieras y mas.

---

## Tabla de Contenidos

- [Caracteristicas](#caracteristicas)
- [Requisitos Previos](#requisitos-previos)
- [Instalacion Rapida](#instalacion-rapida)
- [Estructura del Proyecto](#estructura-del-proyecto)
- [Configuracion](#configuracion)
- [Uso de la API](#uso-de-la-api)
- [Documentacion OpenAPI](#documentacion-openapi)
- [Autenticacion](#autenticacion)
- [Perfiles de Entorno](#perfiles-de-entorno)
- [Base de Datos](#base-de-datos)
- [Desarrollo](#desarrollo)
- [Despliegue](#despliegue)
- [Solucion de Problemas](#solucion-de-problemas)
- [Seguridad](#seguridad)

---

## Caracteristicas

### Autenticacion y Seguridad

**JWT con tokens duales**
- Access tokens: 24 horas de validez
- Refresh tokens: 7 dias de validez
- Signing: HS512 algorithm

**Multi-Factor Authentication (MFA)**
- TOTP (Time-based One-Time Password)
- QR code generation para apps autenticadora
- Backup codes support

**Gestion de Sesiones**
- Device tracking y fingerprinting
- IP address validation
- Session revocation
- Logout multidevice

**Control de Acceso**
- Role-based access control (RBAC)
- Tenant isolation (multi-usuario)
- Metodo-level security

### Gestion Financiera

**Cuentas Bancarias**
- CRUD completo
- Roles de acceso (OWNER, EDITOR, VIEWER)
- Compartir cuentas entre usuarios

**Transacciones**
- Creacion y edicion de transacciones
- Filtrado avanzado (fecha, categoria, cuenta, monto)
- Busqueda por descripcion
- Paginacion automatica

**Categorias**
- Ingreso/Gasto
- Cache para performance
- Gestion por usuario

**Presupuestos**
- Crear presupuestos por categoria
- Alertas de limite
- Comparativa gasto vs presupuesto

**Metas Financieras**
- Crear metas con fecha objetivo
- Seguimiento de progreso
- Calculo automatico de faltante

**Deudas**
- Registro de deudas
- Seguimiento de pagos
- Calculo de intereses

**Activos**
- Inventario de activos
- Valuacion
- Depreciacion

**Transacciones Recurrentes**
- Plantillas reutilizables
- Generacion automatica
- Historial de transacciones

### Analisis y Reportes

**Analytics**
- Resumen de balances (ingreso/gasto)
- Analisis por categoria
- Tendencias mensuales
- Predicciones

**Activity Logging**
- Auditoria completa de acciones
- Timestamps automaticos
- Trazabilidad de cambios

**Notificaciones**
- Email con templates
- Push notifications ready
- Alertas de presupuesto

### Infraestructura

**Multi-Environment**
- Local (desarrollo con debug)
- Dev (servidor de desarrollo)
- Test (H2 en memoria)
- Prod (SSL, hardened)

**Caching**
- Redis para cache distribuida
- Local cache fallback
- Cache invalidation automatica

**Database**
- PostgreSQL 12+
- JPA/Hibernate ORM
- Soft delete pattern
- Audit fields automaticos

**API Documentation**
- OpenAPI 3.0 / Swagger
- Interactive Swagger UI
- Habilitacion por perfil
- Deshabilitado en produccion

---

## Requisitos Previos

### Minimos
- **Java 21+** ([Descargar](https://www.oracle.com/java/technologies/downloads/#java21))
- **Gradle 8.10+** ([Descargar](https://gradle.org/))
- **PostgreSQL 12+** ([Descargar](https://www.postgresql.org/))
- **Git**

### Opcionales (Recomendado)
- **Redis 6+** - Para cache distribuida
- **Docker** - Para containerizacion
- **Postman** - Para testing de APIs
- **pgAdmin** - Para administracion de BD

---

## Instalacion Rapida

### 1. Clonar el repositorio
```bash
git clone https://github.com/jhonsferg/umbral-service-mono.git
cd umbral-service-mono
```

### 2. Configurar variables de entorno
```bash
cp .env.example .env
```

**Editar `.env` con tu editor favorito:**
```bash
# Windows
notepad .env

# Linux/macOS
nano .env
```

**Configuracion minima:**
```env
# Base de datos
DB_HOST=localhost
DB_PORT=5432
DB_NAME=umbral
DB_USER=postgres
DB_PASSWORD=your_password

# JWT (cambiar en produccion)
JWT_ACCESS_SECRET=your-very-long-secret-key-change-in-production-min-64-chars
JWT_REFRESH_SECRET=your-very-long-refresh-secret-key-change-in-production-min-64-chars

# Email (opcional para desarrollo)
SMTP_HOST=localhost
SMTP_PORT=1025
SMTP_USER=test
SMTP_PASSWORD=test
```

### 3. Crear base de datos (PostgreSQL)

**Linux/macOS:**
```bash
sudo -u postgres createuser umbral_user
sudo -u postgres createdb umbral -O umbral_user
sudo -u postgres psql -c "ALTER USER umbral_user WITH PASSWORD 'your_password';"
```

**Windows (usando pgAdmin):**
1. Abrir pgAdmin
2. Crear nuevo user: `umbral_user`
3. Crear nueva base de datos: `umbral`
4. Asignar propietario: `umbral_user`

### 4. Compilar y ejecutar
```bash
# Compilar
./gradlew build

# Ejecutar en desarrollo
./gradlew bootRun

# Ejecutar con perfil especifico
./gradlew bootRun -Pprofile=local
```

### 5. Acceder a la aplicacion
```
API Swagger UI: http://localhost:8080/api/v1/docs
Health Check: http://localhost:8080/actuator/health
API Docs JSON: http://localhost:8080/api/v1/v3/api-docs
```

---

## Estructura del Proyecto

```
umbral-service-mono/
├── src/
│   ├── main/
│   │   ├── java/com/codesoftlabs/umbral/
│   │   │   ├── UmbralApplication.java         # Punto de entrada
│   │   │   ├── config/                         # Configuraciones Spring
│   │   │   ├── controller/                     # REST Controllers (17)
│   │   │   ├── service/                        # Business Logic (21)
│   │   │   ├── repository/                     # Data Access
│   │   │   ├── entity/                         # JPA Entities (23)
│   │   │   ├── dto/                            # Data Transfer Objects
│   │   │   ├── exception/                      # Custom Exceptions
│   │   │   ├── security/                       # JWT & Spring Security
│   │   │   ├── mapper/                         # Object Mappers
│   │   │   ├── beans/                          # Spring Beans
│   │   │   ├── common/                         # Shared Classes
│   │   │   └── util/                           # Utility Classes
│   │   └── resources/
│   │       ├── application.yaml                # Base config
│   │       ├── application-local.yaml          # Local dev
│   │       ├── application-dev.yaml            # Server dev
│   │       ├── application-test.yaml           # Testing
│   │       └── application-prod.yaml           # Production
│   └── test/java/...                           # Tests
├── build.gradle.kts                            # Gradle build script
├── settings.gradle.kts                         # Gradle settings
├── gradle.properties                           # Gradle properties
├── gradlew                                     # Gradle wrapper (Unix)
├── gradlew.bat                                 # Gradle wrapper (Windows)
├── Dockerfile                                  # Production Docker image
├── Dockerfile.dev                              # Development Docker image
├── .env.example                                # Env template
├── .gitignore                                  # Git ignore
└── README.md                                   # This file
```

---

## Configuracion

### Perfiles de Entorno

#### **local** (Desarrollo Local)
```bash
./gradlew bootRun -Pprofile=local
```

#### **dev** (Servidor de Desarrollo)
```bash
export SPRING_PROFILES_ACTIVE=dev
./gradlew bootRun
```

#### **test** (Testing/CI-CD)
```bash
./gradlew bootRun -Pprofile=test
```

#### **prod** (Produccion)
```bash
export SPRING_PROFILES_ACTIVE=prod
java -jar build/libs/umbral-1.0.0.jar
```

### OpenAPI / Swagger Habilitacion

Cada perfil controla OpenAPI independientemente:

```yaml
# application-local.yaml (habilitado)
springdoc:
  swagger-ui:
    enabled: true
  api-docs:
    enabled: true

# application-prod.yaml (deshabilitado)
springdoc:
  swagger-ui:
    enabled: false
  api-docs:
    enabled: false
```

---

## Uso de la API

### Autenticacion

#### 1. Registrarse
```bash
curl -X POST http://localhost:8080/api/v1/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "email": "user@example.com",
    "password": "SecurePass123!",
    "firstName": "John",
    "lastName": "Doe"
  }'
```

#### 2. Login
```bash
curl -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "user@example.com",
    "password": "SecurePass123!"
  }'
```

#### 3. Acceder a endpoints protegidos
```bash
curl -X GET http://localhost:8080/api/v1/transactions \
  -H "Authorization: Bearer YOUR_ACCESS_TOKEN"
```

---

## Documentacion OpenAPI

### Acceder a Swagger UI
```
http://localhost:8080/api/v1/docs
```

**Caracteristicas:**
- Explorar todos los endpoints
- Ver parametros y responses
- Ejecutar peticiones directamente
- Autenticacion integrada

### Habilitar/Deshabilitar Swagger

En cada perfil (`application-*.yaml`):
```yaml
springdoc:
  swagger-ui:
    enabled: true      # o false
  api-docs:
    enabled: true      # o false
```

O con variable de entorno:
```bash
export SWAGGER_UI_ENABLED=false
export API_DOCS_ENABLED=false
```

---

## Autenticacion

### JWT Tokens
- **Access Token**: 24 horas
- **Refresh Token**: 7 dias
- **Algorithm**: HS512
- **Secret**: Minimo 64 caracteres

### MFA (TOTP)
1. Habilitar: `POST /auth/mfa/setup`
2. Escanear QR code
3. Verificar: `POST /auth/mfa/verify`

---

## Desarrollo

### Crear un Nuevo Endpoint

1. Crear DTO
2. Crear metodo en Servicio
3. Crear metodo en Controlador con anotaciones OpenAPI
4. Agregar tests

### Formatear Codigo
```bash
# Con IDE (IntelliJ/VS Code)
# Code -> Reformat Code

# O manualmente revisar indentacion
```

### Testing
```bash
./gradlew test
./gradlew test --tests "com.codesoftlabs.umbral.service.AuthServiceTest"
```

### Spring Boot DevTools

El proyecto incluye Spring Boot DevTools para desarrollo con hot-reload:

- **Auto-reinicio**: La aplicacion se reinicia automaticamente al detectar cambios en el codigo fuente
- **LiveReload**: Compatible con extensiones de navegador para actualizaciones en tiempo real
- **Habilitado en**: perfiles `local` y `dev`
- **Deshabilitado automaticamente**: en perfil `prod`

Para configurar DevTools, editar `application-local.yaml`:
```yaml
spring:
  devtools:
    restart:
      enabled: true
      additional-paths: src/main/java
    livereload:
      enabled: true
```

---

## Despliegue

### Build JAR
```bash
./gradlew clean build -x test
```

### Despliegue Docker
```bash
# Produccion
docker build -t umbral:1.0 .
docker run -d -p 8080:8080 \
  -e SPRING_PROFILES_ACTIVE=prod \
  umbral:1.0

# Desarrollo
docker build -f Dockerfile.dev -t umbral:dev .
docker run -d -p 8080:8080 \
  -e SPRING_PROFILES_ACTIVE=dev \
  umbral:dev
```

---

## Solucion de Problemas

### Error: "Database connection refused"
```bash
# Verificar PostgreSQL
psql -U postgres -c "SELECT 1"
```

### Error: "Port 8080 already in use"
```bash
# Windows
netstat -ano | findstr :8080
taskkill /PID <PID> /F

# Linux/macOS
lsof -ti :8080 | xargs kill -9
```

### Error: "JWT signature does not match"
```bash
# Verificar JWT_SECRET >= 64 caracteres
echo ${JWT_ACCESS_SECRET} | wc -c
```

### Error: "Gradle build failed"
```bash
# Limpiar cache de Gradle
rm -rf .gradle
rm -rf build

# Reintentar build
./gradlew clean build
```

---

## Seguridad

### Checklist de Produccion
- [ ] JWT_SECRET cambiado
- [ ] SSL/TLS habilitado
- [ ] Database user/password seguro
- [ ] Swagger deshabilitado
- [ ] Logging no incluye datos sensibles
- [ ] CORS configurado
- [ ] Rate limiting implementado
- [ ] Backups automaticos

---

## Licencia

MIT License - Ver `LICENSE`

---

**Version:** 1.0.0
**Status:** Production Ready
**Ultima actualizacion:** Julio 2026
