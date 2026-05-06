# 🚀 Umbral Spring Boot Backend

![Build Status](https://img.shields.io/badge/build-passing-brightgreen)
![Version](https://img.shields.io/badge/version-1.0.0-blue)
![License](https://img.shields.io/badge/license-MIT-green)
![Java](https://img.shields.io/badge/java-21-red)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.0.5-green)

Plataforma de gestión financiera empresarial construida con **Spring Boot 4.0.5**, **Spring Security**, **JWT**, **PostgreSQL** y **Redis**. Aplicación backend completa con autenticación multi-factor, gestión de transacciones, presupuestos, metas financieras y más.

---

## 📋 Tabla de Contenidos

- [Características](#características)
- [Requisitos Previos](#requisitos-previos)
- [Instalación Rápida](#instalación-rápida)
- [Estructura del Proyecto](#estructura-del-proyecto)
- [Configuración](#configuración)
- [Uso de la API](#uso-de-la-api)
- [Documentación OpenAPI](#documentación-openapi)
- [Autenticación](#autenticación)
- [Perfiles de Entorno](#perfiles-de-entorno)
- [Base de Datos](#base-de-datos)
- [Desarrollo](#desarrollo)
- [Despliegue](#despliegue)
- [Solución de Problemas](#solución-de-problemas)
- [Seguridad](#seguridad)
- [Contribuir](#contribuir)

---

## ✨ Características

### Autenticación & Seguridad
✅ **JWT con tokens duales**
- Access tokens: 24 horas de validez
- Refresh tokens: 7 días de validez
- Signing: HS512 algorithm

✅ **Multi-Factor Authentication (MFA)**
- TOTP (Time-based One-Time Password)
- QR code generation para apps autenticadora
- Backup codes support

✅ **Gestión de Sesiones**
- Device tracking y fingerprinting
- IP address validation
- Session revocation
- Logout multidevice

✅ **Control de Acceso**
- Role-based access control (RBAC)
- Tenant isolation (multi-usuario)
- Método-level security

### Gestión Financiera
✅ **Cuentas Bancarias**
- CRUD completo
- Roles de acceso (OWNER, EDITOR, VIEWER)
- Compartir cuentas entre usuarios

✅ **Transacciones**
- Creación y edición de transacciones
- Filtrado avanzado (fecha, categoría, cuenta, monto)
- Búsqueda por descripción
- Paginación automática

✅ **Categorías**
- Ingreso/Gasto
- Cacheo para performance
- Gestión por usuario

✅ **Presupuestos**
- Crear presupuestos por categoría
- Alertas de límite
- Comparativa gasto vs presupuesto

✅ **Metas Financieras**
- Crear metas con fecha objetivo
- Seguimiento de progreso
- Cálculo automático de faltante

✅ **Deudas**
- Registro de deudas
- Seguimiento de pagos
- Cálculo de intereses

✅ **Activos**
- Inventario de activos
- Valuación
- Depreciación

✅ **Transacciones Recurrentes**
- Plantillas reutilizables
- Generación automática
- Historial de transacciones

### Análisis & Reportes
✅ **Analytics**
- Resumen de balances (ingreso/gasto)
- Análisis por categoría
- Tendencias mensuales
- Predicciones

✅ **Activity Logging**
- Auditoría completa de acciones
- Timestamps automáticos
- Trazabilidad de cambios

✅ **Notificaciones**
- Email con templates
- Push notifications ready
- Alertas de presupuesto

### Infraestructura
✅ **Multi-Environment**
- Local (desarrollo con debug)
- Dev (servidor de desarrollo)
- Test (H2 en memoria)
- Prod (SSL, hardened)

✅ **Caching**
- Redis para caché distribuida
- Local cache fallback
- Cache invalidation automática

✅ **Database**
- PostgreSQL 12+
- JPA/Hibernate ORM
- Soft delete pattern
- Audit fields automáticos

✅ **API Documentation**
- OpenAPI 3.0 / Swagger
- Interactive Swagger UI
- Habilitación por perfil
- Deshabilitado en producción

---

## 🔧 Requisitos Previos

### Mínimos
- **Java 21+** ([Descargar](https://www.oracle.com/java/technologies/downloads/#java21))
- **Maven 3.8+** ([Descargar](https://maven.apache.org/))
- **PostgreSQL 12+** ([Descargar](https://www.postgresql.org/))
- **Git**

### Opcionales (Recomendado)
- **Redis 6+** - Para caching distribuida
- **Docker** - Para containerización
- **Postman** - Para testing de APIs
- **pgAdmin** - Para administración de BD

---

## 🚀 Instalación Rápida

### 1. Clonar el repositorio
```bash
git clone https://github.com/jhonsferg/Umbral.git
cd Umbral/umbral-backend-spring
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

**Configuración mínima:**
```env
# Base de datos
DB_HOST=localhost
DB_PORT=5432
DB_NAME=umbral
DB_USER=postgres
DB_PASSWORD=your_password

# JWT (cambiar en producción)
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
mvn clean install

# Ejecutar en desarrollo
mvn spring-boot:run

# Ejecutar con perfil específico
mvn spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=local"
```

### 5. Acceder a la aplicación
```
API Swagger UI: http://localhost:3001/api/v1/docs
Health Check: http://localhost:3001/actuator/health
API Docs JSON: http://localhost:3001/api/v1/v3/api-docs
```

---

## 📁 Estructura del Proyecto

```
umbral-backend-spring/
├── src/
│   ├── main/
│   │   ├── java/com/codesoftlabs/umbral/
│   │   │   ├── UmbralApplication.java         # Punto de entrada
│   │   │   ├── config/                         # Configuraciones Spring
│   │   │   ├── controller/                     # REST Controllers (15)
│   │   │   ├── service/                        # Business Logic (19)
│   │   │   ├── repository/                     # Data Access (14)
│   │   │   ├── entity/                         # JPA Entities (18)
│   │   │   ├── dto/                            # Data Transfer Objects
│   │   │   ├── exception/                      # Custom Exceptions
│   │   │   ├── security/                       # JWT & Spring Security
│   │   │   ├── filter/                         # Request Filters
│   │   │   ├── common/                         # Shared Classes
│   │   │   ├── util/                           # Utility Classes
│   │   │   └── websocket/                      # WebSocket Config
│   │   └── resources/
│   │       ├── application.yaml                # Base config
│   │       ├── application-local.yaml          # Local dev
│   │       ├── application-dev.yaml            # Server dev
│   │       ├── application-test.yaml           # Testing
│   │       ├── application-prod.yaml           # Production
│   │       └── templates/                      # Email templates
│   └── test/java/...                           # Tests
├── pom.xml                                      # Maven config
├── .env.example                                 # Env template
├── .gitignore                                   # Git ignore
└── README.md                                    # This file
```

---

## ⚙️ Configuración

### Perfiles de Entorno

#### **local** (Desarrollo Local)
```bash
mvn spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=local"
```

#### **dev** (Servidor de Desarrollo)
```bash
export SPRING_PROFILES_ACTIVE=dev
mvn spring-boot:run
```

#### **test** (Testing/CI-CD)
```bash
mvn spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=test"
```

#### **prod** (Producción)
```bash
export SPRING_PROFILES_ACTIVE=prod
java -jar umbral-0.0.1-SNAPSHOT.jar
```

### OpenAPI / Swagger Habilitación

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

## 📚 Uso de la API

### Autenticación

#### 1. Registrarse
```bash
curl -X POST http://localhost:3001/api/v1/auth/register \
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
curl -X POST http://localhost:3001/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "user@example.com",
    "password": "SecurePass123!"
  }'
```

#### 3. Acceder a endpoints protegidos
```bash
curl -X GET http://localhost:3001/api/v1/transactions \
  -H "Authorization: Bearer YOUR_ACCESS_TOKEN"
```

---

## 📖 Documentación OpenAPI

### Acceder a Swagger UI
```
http://localhost:3001/api/v1/docs
```

**Características:**
- 🔍 Explorar todos los endpoints
- 📝 Ver parámetros y responses
- ✅ Ejecutar peticiones directamente
- 🔐 Autenticación integrada

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

## 🔐 Autenticación

### JWT Tokens
- **Access Token**: 24 horas
- **Refresh Token**: 7 días
- **Algorithm**: HS512
- **Secret**: Mínimo 64 caracteres

### MFA (TOTP)
1. Habilitar: `POST /auth/mfa/setup`
2. Escanear QR code
3. Verificar: `POST /auth/mfa/verify`

---

## 💻 Desarrollo

### Crear un Nuevo Endpoint

1. Crear DTO
2. Crear método en Servicio
3. Crear método en Controlador con anotaciones OpenAPI
4. Agregar tests

### Formattear Código
```bash
# Con IDE (IntelliJ/VS Code)
# Code → Reformat Code

# O manualmente revisar indentación
```

### Testing
```bash
mvn clean test
mvn test -Dtest=AuthServiceTest
```

---

## 🚀 Despliegue

### Build JAR
```bash
mvn clean package -DskipTests
```

### Despliegue Docker
```bash
docker build -t umbral:1.0 .
docker run -d -p 8080:8080 \
  -e SPRING_PROFILES_ACTIVE=prod \
  umbral:1.0
```

---

## 🔍 Solución de Problemas

### Error: "Database connection refused"
```bash
# Verificar PostgreSQL
psql -U postgres -c "SELECT 1"
```

### Error: "Port 3001 already in use"
```bash
# Cambiar puerto o matar proceso
lsof -ti :3001 | xargs kill -9
```

### Error: "JWT signature does not match"
```bash
# Verificar JWT_SECRET >= 64 caracteres
echo ${JWT_ACCESS_SECRET} | wc -c
```

---

## 🔐 Seguridad

### Checklist de Producción
- [ ] JWT_SECRET cambiado
- [ ] SSL/TLS habilitado
- [ ] Database user/password seguro
- [ ] Swagger deshabilitado
- [ ] Logging no incluye datos sensibles
- [ ] CORS configurado
- [ ] Rate limiting implementado
- [ ] Backups automáticos

---

## 📄 Licencia

MIT License - Ver `LICENSE`

---

**Versión:** 1.0.0  
**Status:** ✅ Production Ready  
**Última actualización:** Abril 2026
