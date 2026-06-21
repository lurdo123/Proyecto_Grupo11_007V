# Gl1tch_St0re — Arquitectura de Microservicios

Proyecto semestral desarrollado para la asignatura **Desarrollo FullStack 1 (DSY1103)**.  
Sistema de e-commerce especializado en componentes y hardware computacional, construido sobre una arquitectura distribuida de microservicios independientes con Spring Boot y persistencia real en MySQL.

---

## Integrantes

| Nombre | GitHub |
|--------|--------|
| Jonatan Parra | @jonatanparra |
| Williams Rivera | @WilliamsG22 |
| Vicente Azocar | @lurdo123 |

---

## Descripción del proyecto

**Gl1tch_St0re** es una plataforma de venta de hardware y componentes de computación que gestiona el ciclo completo de compra: desde la autenticación del usuario y el catálogo de productos, hasta las órdenes, pagos, envíos, garantías y reseñas. Cada dominio funcional está encapsulado en un microservicio independiente con su propia base de datos MySQL.

---

## Microservicios implementados

| # | Servicio | Puerto | Base de datos | Descripción |
|---|----------|--------|---------------|-------------|
| 1 | `autenticacion` | 8080 | `autenticacion` | Gestión de usuarios con login y generación de tokens JWT |
| 2 | `catalogo` | 8081 | `catalogo` | Administración del catálogo de productos disponibles |
| 3 | `clientes` | 8082 | `clientes` | Perfiles de clientes con nivel de fidelidad |
| 4 | `compatibilidad` | 8083 | `compatibilidad` | Verificación de compatibilidad entre componentes de hardware |
| 5 | `envios` | 8084 | `envios` | Seguimiento de envíos por orden, usuario y estado |
| 6 | `garantias` | 8085 | `garantias` | Gestión de garantías asociadas a productos y órdenes |
| 7 | `inventario` | 8086 | `inventario` | Control de stock, estado físico y ubicación en bodega |
| 8 | `ordenes` | 8087 | `ordenes` | Creación y gestión de órdenes de compra |
| 9 | `pagos` | 8088 | `pagos` | Registro de pagos con método, monto y estado de transacción |
| 10 | `preventas` | 8089 | `preventas` | Reservas anticipadas con fecha de lanzamiento |
| 11 | `promociones` | 8090 | `promociones` | Gestión de promociones y descuentos activos |
| 12 | `resenas` | 8091 | `resenas` | Reseñas verificadas de productos con calificación del 1 al 5 |

---

## Funcionalidades implementadas

### Persistencia real con JPA + Hibernate y Flyway

Todos los microservicios utilizan:
- Entidades JPA con `@Entity`, `@Id`, `@GeneratedValue`, `@Column`
- Repositorios que extienden `JpaRepository`
- Migraciones de base de datos gestionadas con **Flyway** (`db/migration/V0__...sql`)
- Configuración de datasource y dialecto MySQL en `application.properties`
- `spring.jpa.hibernate.ddl-auto=none` (esquema controlado exclusivamente por Flyway)

### Patrón CSR (Controller – Service – Repository/Model)

Cada microservicio está organizado en los siguientes paquetes con responsabilidades claramente separadas:

```
src/main/java/Gl1tch_st0re/<microservicio>/
├── controller/      → Manejo de solicitudes REST (solo orquestación)
├── service/         → Lógica de negocio y reglas del dominio
├── repository/      → Acceso a datos mediante JpaRepository
├── model/           → Entidades JPA
├── dto/
│   ├── request/     → DTOs de entrada con validaciones Bean Validation
│   └── response/    → DTOs de salida y DTOs para consumo remoto
├── client/          → WebClients para comunicación entre microservicios (solo en: envios, garantias, inventario, ordenes, pagos, resenas)
├── exceptions/      → Excepciones personalizadas y GlobalExceptionHandler
└── security/        → Filtro JWT, JwtService y SecurityConfig
```

### Endpoints REST (CRUD completo en todos los microservicios)

Todos los endpoints retornan JSON estructurado, utilizan `ResponseEntity` y siguen convenciones REST:

| Método | Ruta | Descripción |
|--------|------|-------------|
| `GET` | `/api/<recurso>` | Listar todos los registros |
| `GET` | `/api/<recurso>/{id}` | Obtener por ID |
| `POST` | `/api/<recurso>` | Crear nuevo registro |
| `PUT` | `/api/<recurso>/{id}` | Actualizar registro existente |
| `DELETE` | `/api/<recurso>/{id}` | Eliminar por ID |
| `DELETE` | `/api/<recurso>` | Eliminar todos los registros |

Endpoints adicionales específicos por dominio:

- `POST /api/autenticaciones/login` → Autenticación con JWT
- `GET /api/compatibilidades/verificar?componenteBase=X&componenteCompatible=Y` → Verificación de compatibilidad
- `GET /api/envios/usuario/{usuario}`, `/estado/{estado}`, `/orden/{ordenId}` → Filtros de envío
- `GET /api/ordenes/usuario/{usuario}`, `/estado/{estado}` → Filtros de órdenes
- `GET /api/preventas/usuario/{usuario}`, `/estado/{estado}` → Filtros de preventas

### Validaciones con Bean Validation (JSR 380)

Los DTOs de request utilizan anotaciones como `@NotNull`, `@NotBlank`, `@Min`, `@Max`, `@Positive`, con mensajes descriptivos en español. El controlador recibe `@Valid` para activar la validación antes de llegar a la capa de servicio.

### Manejo de excepciones centralizado con `@RestControllerAdvice`

Cada microservicio implementa un `GlobalExceptionHandler` que captura:
- Excepción personalizada `*NotFoundException` → HTTP 404
- `MethodArgumentNotValidException` → HTTP 400 con detalle de campos inválidos
- `Exception` genérica → HTTP 500

Las respuestas de error siguen un formato estructurado con `ErrorResponse` (timestamp, status, error, mensaje, ruta).

### Reglas de negocio en la capa de servicio

Ejemplos implementados:
- `clientes`: validación de unicidad de `usuario_id` al crear y actualizar; asignación automática de nivel `"Bronce"` si no se especifica
- `ordenes` y `preventas`: normalización del campo estado a `toUpperCase()` antes de persistir
- `resenas`: calificación restringida entre 1 y 5 mediante Bean Validation; unicidad de reseña por usuario y producto
- `compatibilidad`: lógica de verificación de compatibilidad entre componentes por nombre
- `pagos`: validación de unicidad de `id_transaccion_externa`
- `inventario`: validación de unicidad de `producto_id`

### Comunicación entre microservicios con WebClient

Se implementaron 6 flujos de comunicación entre microservicios utilizando **WebClient** de Spring WebFlux:

| Servicio origen | Consulta a | Cuándo | Puerto destino |
|----------------|-----------|--------|----------------|
| `ordenes` | `catalogo` | Al crear una orden, valida que el producto existe, está disponible y tiene stock suficiente | 8081 |
| `pagos` | `ordenes` | Al registrar un pago, valida que la orden referenciada existe | 8087 |
| `envios` | `ordenes` | Al crear un envío, valida que la orden referenciada existe | 8087 |
| `garantias` | `catalogo` | Al crear una garantía, valida que el producto existe | 8081 |
| `resenas` | `catalogo` | Al crear una reseña, valida que el producto existe | 8081 |
| `inventario` | `catalogo` | Al registrar inventario, valida que el producto existe | 8081 |

Cada cliente está implementado en el paquete `client/` de su respectivo microservicio con manejo de errores y timeout implícito.

### Logs estructurados con SLF4J

Todos los microservicios implementan `@Slf4j` de Lombok en la capa de servicio con mensajes en puntos clave del flujo:
- `log.info(...)` en operaciones exitosas (listado, creación, actualización, eliminación)
- `log.warn(...)` en validaciones de negocio y registros no encontrados
- `log.error(...)` en fallos de comunicación entre microservicios

Los mensajes incluyen el prefijo `[nombre_servicio]` para facilitar la trazabilidad en consola.

### Seguridad JWT

Los microservicios incluyen `JwtAuthenticationFilter`, `JwtService` y `SecurityConfig` para proteger endpoints con autenticación basada en tokens. El endpoint `/api/autenticaciones/login` es público; el resto requiere token válido en el header `Authorization: Bearer <token>`.

### Colección Postman

Se incluye el archivo `Gl1tch_St0re.postman_collection.json` en la raíz del repositorio con los endpoints de todos los microservicios configurados y listos para pruebas de integración.

---

## Requisitos previos

- Java 21
- Maven 3.8+
- MySQL 8 (o Docker)

---

## Pasos para ejecutar

### Opción 1 — Con Docker (recomendado para base de datos)

1. Levantar MySQL con Docker Compose desde la raíz del proyecto:

```bash
docker-compose up -d
```

Esto crea un contenedor MySQL en el puerto `3306` con contraseña `root123`.

2. Ejecutar cada microservicio desde su carpeta:

```bash
cd autenticacion
./mvnw spring-boot:run
```

Repetir para cada microservicio (`catalogo`, `clientes`, `compatibilidad`, `envios`, `garantias`, `inventario`, `ordenes`, `pagos`, `preventas`, `promociones`, `resenas`).

### Opción 2 — Con MySQL local

1. Tener MySQL corriendo con usuario `root` y contraseña `root123`.
2. Las bases de datos se crean automáticamente gracias a `createDatabaseIfNotExist=true`.
3. Flyway aplicará las migraciones SQL al iniciar cada servicio.
4. Ejecutar cada microservicio:

```bash
cd <nombre-microservicio>
./mvnw spring-boot:run
```

> **Orden recomendado de inicio:** `autenticacion` → `catalogo` → resto de servicios. Los microservicios que consumen `catalogo` u `ordenes` via WebClient requieren que esos servicios estén corriendo primero.

---

## API Gateway

El módulo `gateway` centraliza todas las rutas bajo el puerto **8000**. Incluye Circuit Breaker con fallback automático y configuración CORS global.

Para iniciarlo:

```bash
cd gateway
./mvnw spring-boot:run
```

### Rutas principales del Gateway

| Microservicio | Ruta en Gateway | Puerto destino |
|---------------|----------------|----------------|
| Autenticación | `http://localhost:8000/api/autenticaciones/` | 8080 |
| Catálogo | `http://localhost:8000/api/catalogo/` | 8081 |
| Clientes | `http://localhost:8000/api/clientes/` | 8082 |
| Compatibilidad | `http://localhost:8000/api/compatibilidades/` | 8083 |
| Envíos | `http://localhost:8000/api/envios/` | 8084 |
| Garantías | `http://localhost:8000/api/garantias/` | 8085 |
| Inventario | `http://localhost:8000/api/inventario/` | 8086 |
| Órdenes | `http://localhost:8000/api/ordenes/` | 8087 |
| Pagos | `http://localhost:8000/api/pagos/` | 8088 |
| Preventas | `http://localhost:8000/api/preventas/` | 8089 |
| Promociones | `http://localhost:8000/api/promociones/` | 8090 |
| Reseñas | `http://localhost:8000/api/resenas/` | 8091 |

Si un microservicio no responde, el Gateway retorna `HTTP 503` con un JSON descriptivo gracias al Circuit Breaker configurado.

### Perfiles de configuración del Gateway

El Gateway soporta tres perfiles Spring:

| Perfil | Uso | Nivel de log |
|--------|-----|-------------|
| *(default)* | Desarrollo local | `DEBUG` para gateway |
| `dev` | Desarrollo local explícito | `DEBUG` para gateway y aplicación |
| `prod` | Producción (servicios remotos) | `WARN` para gateway, `INFO` para aplicación |

Para activar un perfil:
```bash
cd gateway
./mvnw spring-boot:run -Dspring-boot.run.profiles=prod
```

### Monitoreo con Spring Boot Actuator

El Gateway expone todos los endpoints de Actuator para monitoreo de salud:

```
GET http://localhost:8000/actuator/health     → Estado de salud con detalle
GET http://localhost:8000/actuator/gateway    → Rutas activas del Gateway
GET http://localhost:8000/actuator/*          → Todos los endpoints disponibles
```

---

## Documentación Swagger / OpenAPI

Cada microservicio expone su documentación interactiva en `/swagger-ui/index.html`. Incluye autenticación JWT Bearer y descripción de todos los endpoints.

| Microservicio | Swagger UI local |
|---------------|-----------------|
| Autenticación | http://localhost:8080/swagger-ui/index.html |
| Catálogo | http://localhost:8081/swagger-ui/index.html |
| Clientes | http://localhost:8082/swagger-ui/index.html |
| Compatibilidad | http://localhost:8083/swagger-ui/index.html |
| Envíos | http://localhost:8084/swagger-ui/index.html |
| Garantías | http://localhost:8085/swagger-ui/index.html |
| Inventario | http://localhost:8086/swagger-ui/index.html |
| Órdenes | http://localhost:8087/swagger-ui/index.html |
| Pagos | http://localhost:8088/swagger-ui/index.html |
| Preventas | http://localhost:8089/swagger-ui/index.html |
| Promociones | http://localhost:8090/swagger-ui/index.html |
| Reseñas | http://localhost:8091/swagger-ui/index.html |

La especificación OpenAPI en formato JSON está disponible en `/v3/api-docs` de cada servicio.

---

## Pruebas unitarias

Todos los microservicios tienen pruebas unitarias en `src/test/java/` que cubren la capa de servicio utilizando **JUnit 5** y **Mockito** (sin cargar contexto Spring). Se sigue la estructura Given–When–Then.

Para ejecutar las pruebas con reporte de cobertura:

```bash
cd <nombre-microservicio>
./mvnw test
```

El reporte de cobertura JaCoCo se genera en `target/site/jacoco/index.html`.

---

## Resumen de puertos

```
gateway        → localhost:8000
autenticacion  → localhost:8080
catalogo       → localhost:8081
clientes       → localhost:8082
compatibilidad → localhost:8083
envios         → localhost:8084
garantias      → localhost:8085
inventario     → localhost:8086
ordenes        → localhost:8087
pagos          → localhost:8088
preventas      → localhost:8089
promociones    → localhost:8090
resenas        → localhost:8091
```
