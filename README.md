# Prueba Tecnica - Transferencias (Full Stack)

Este repositorio contiene la solucion full stack de la prueba tecnica:

- `backend/`: API de transferencias (Java + Spring WebFlux + Kafka + PostgreSQL)
- `frontend/transfer-frontend/`: interfaz Angular para consumir la API

## Arquitectura utilizada

## Backend

El backend aplica arquitectura por capas:

```
Domain -> Application -> Infrastructure -> Bootstrap
```

Puntos clave:
- reglas de negocio en dominio
- casos de uso en aplicacion
- adaptadores para web, persistencia y mensajeria
- trazabilidad por `traceId`
- idempotencia en creacion y consumo de eventos

## Frontend

Angular 21 con estructura por `core` y `features`:
- servicios HTTP centralizados
- formularios reactivos
- componentes separados por responsabilidad
- manejo de estados `loading/error`

## Decisiones tecnicas relevantes

- **WebFlux + R2DBC** para flujo reactivo no bloqueante.
- **Kafka** para desacoplar procesos secundarios de negocio.
- **API Key** para seguridad simple en contexto de prueba.
- **DTOs + mappers** para separar contrato web de dominio.
- **Pruebas unitarias e integracion web** para validar reglas y contrato HTTP.

## Instrucciones de ejecucion

## 1) Backend

Desde `backend/`:

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

## 2) Frontend

Desde `frontend/transfer-frontend/`:

```bash
npm install
npm start
```

Abrir:
- `http://localhost:4200`

## 3) Pruebas

Backend:

```bash
cd backend
mvn test
```

Frontend:

```bash
cd frontend/transfer-frontend
npm run test -- --watch=false
```

## Ejemplos de uso

## Crear transferencia (API)

```bash
curl -X POST http://localhost:8080/api/v1/transfers \
  -H "Content-Type: application/json" \
  -H "X-API-Key: my-secret-api-key-123" \
  -H "Idempotency-Key: idem-001" \
  -d '{
    "clientId":"550e8400-e29b-41d4-a716-446655440000",
    "sourceAccountId":"ACC-001",
    "targetAccountId":"ACC-002",
    "amount":100.50,
    "currency":"USD",
    "description":"Pago proveedor"
  }'
```

## Listar transferencias por cliente y estado

```bash
curl "http://localhost:8080/api/v1/transfers?clientId=550e8400-e29b-41d4-a716-446655440000&status=PENDING&page=0&size=10" \
  -H "X-API-Key: my-secret-api-key-123"
```

## Actualizar estado de transferencia

```bash
curl -X PATCH http://localhost:8080/api/v1/transfers/{transferId}/status \
  -H "Content-Type: application/json" \
  -H "X-API-Key: my-secret-api-key-123" \
  -d '{"status":"APPROVED"}'
```

## Notas

- Documentacion detallada del backend: `backend/README.md`
