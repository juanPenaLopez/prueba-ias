# Transfer Service - Prueba Tecnica

Servicio backend para gestion de transferencias bancarias, construido con Java 17, Spring Boot WebFlux, R2DBC y Kafka.

El objetivo del proyecto es mostrar una solucion mantenible y cercana a un entorno real, con foco en:
- arquitectura limpia
- reglas de negocio explicitas
- trazabilidad
- manejo de errores
- idempotencia
- pruebas unitarias

## Alcance funcional

La API expone tres capacidades principales:
- crear transferencias
- listar transferencias con filtros y paginacion
- actualizar estado de transferencias

Adicionalmente incluye:
- autenticacion por API Key
- respuesta estandar con `traceId`
- publicacion y consumo de eventos en Kafka

## Arquitectura

El proyecto esta organizado por capas:

```
Domain -> Application -> Infrastructure -> Bootstrap
```

### Domain
Contiene el modelo de negocio puro:
- entidades y reglas (`Transfer`, `Client`)
- estados (`TransferStatus`, `ClientStatus`)
- excepciones de negocio
- eventos de dominio

Aqui viven validaciones y transiciones de estado.

### Application
Implementa casos de uso:
- `CreateTransferService`
- `ListTransfersService`
- `UpdateTransferStatusService`

Se apoya en puertos de entrada/salida para no acoplar negocio con infraestructura.

### Infrastructure
Implementa detalles tecnicos:
- controladores REST y DTOs
- adaptadores de persistencia reactiva
- productor y consumidor Kafka
- configuracion de seguridad, OpenAPI, CORS, logs y trazabilidad

### Bootstrap
Punto de entrada de Spring Boot.

## Endpoints

Base path: `/api/v1`

- `POST /transfers`  
  Crea una transferencia. Requiere header `Idempotency-Key`.

- `GET /transfers?clientId=...&status=...&page=...&size=...`  
  Lista transferencias por cliente, con filtro opcional por estado y paginacion.

- `PATCH /transfers/{id}/status`  
  Actualiza estado de transferencia.

- `GET /health`  
  Health check del servicio.

## Formato de respuesta

Para operaciones que retornan wrapper, se usa:

```json
{
  "data": {},
  "status": 200,
  "message": "Respuesta ok",
  "traceId": "..."
}
```

## Idempotencia

En creacion de transferencias se usa `Idempotency-Key` para evitar duplicados por reintentos.

Flujo:
- llega solicitud con clave
- se consulta si ya existe transferencia para esa clave
- si existe, se retorna la existente
- si no existe, se procesa normalmente

## Eventos y procesamiento asincrono

Se publican eventos de dominio en Kafka para desacoplar procesos secundarios.

Eventos implementados:
- `TransferCreatedEvent`
- `TransferProcessedEvent` (cuando la transferencia llega a estado final)

El consumidor aplica control basico de idempotencia por `eventId` usando tabla `processed_events`.

## Seguridad

La API usa autenticacion por API Key:
- header requerido: `X-API-Key`
- valor por defecto local: `my-secret-api-key-123` (configurable)

Rutas publicas:
- `/api/v1/health`
- Swagger/OpenAPI

## Trazabilidad y logging

- cada request recibe un `traceId`
- el `traceId` se propaga a logs y respuestas
- configuracion de logs:
  - formato legible en `dev`
  - formato JSON en `prod`

## Stack tecnico

- Java 17
- Spring Boot 3 (WebFlux)
- Spring Data R2DBC + PostgreSQL
- Spring Security
- Kafka
- OpenAPI (springdoc)
- Maven

## Ejecucion local

Desde la carpeta `backend`:

```bash
docker-compose up -d
mvn spring-boot:run
```

Verificar:

```bash
curl http://localhost:8080/api/v1/health
```

Documentacion API:
- `http://localhost:8080/swagger-ui.html`
- `http://localhost:8080/v3/api-docs`

> Nota: para una guia mas operativa (scripts, troubleshooting, reset), revisar `STARTUP.md`.

## Pruebas

Ejecutar:

```bash
mvn test
```

El proyecto incluye pruebas unitarias en dominio y casos de uso, y verificacion de cobertura configurada con JaCoCo.

## Estructura de carpetas (resumen)

```text
src/main/java/com/ias/transfer
  |- domain
  |- application
  |- infrastructure
  |- bootstrap
```

## Mejoras futuras sugeridas

- pruebas de integracion (controller + persistencia + seguridad)
- politicas de retry/DLQ para eventos Kafka
- paginacion y ordenamiento soportados directamente por base de datos
- hardening de observabilidad (metricas y alertas)

## Autor

Juan

