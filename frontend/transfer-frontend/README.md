# Frontend - Transfer UI (Angular)

Aplicacion web Angular para consumir la API de transferencias del backend.

## Arquitectura utilizada

Frontend en Angular 21 con componentes standalone y separacion por responsabilidad:

- `core/`
  - `models`: contratos tipados de API
  - `services`: consumo HTTP
  - `interceptors`: inyeccion de `X-API-Key`
- `features/transfers/`
  - `components`: formulario, filtros, tabla/paginacion
  - `pages`: orquestacion de estado de la pantalla

Se usa:
- formularios reactivos
- manejo de estados `loading/error`
- servicios para capa API

## Decisiones tecnicas

- **Standalone components**: menor friccion y estructura simple para prueba tecnica.
- **`TransferApiService` centralizado**: evita duplicar llamadas HTTP en componentes.
- **Interceptor para API key**: mantiene seguridad transversal.
- **Estado de pagina en `TransfersPageComponent`**: facilita mantenimiento del flujo.
- **Tipos estrictos (`Transfer`, `ApiResponse`)**: menos errores de contrato backend/frontend.

## Instrucciones de ejecucion

### Requisitos
- Node compatible con Angular 21 (`>=20.19` o `>=22.12`)
- npm
- backend ejecutandose en `http://localhost:8080`

### Configuracion

Revisar archivo:

- `src/environments/environment.ts`

Valores por defecto:
- `apiBaseUrl: http://localhost:8080/api/v1`
- `apiKey: my-secret-api-key-123`

### Levantar en desarrollo

```bash
npm install
npm start
```

App disponible en:
- `http://localhost:4200`

### Build

```bash
npm run build
```

### Pruebas

```bash
npm run test -- --watch=false
```

## Ejemplos de uso

### 1) Listar transferencias
1. Ingresar `clientId` en "Filtros de busqueda".
2. Elegir estado (opcional), por ejemplo `PENDING`.
3. Click en `Aplicar filtros`.

### 2) Crear transferencia
1. Completar `clientId`.
2. Completar cuentas, monto y moneda.
3. Click en `Crear transferencia`.

### 3) Actualizar estado
1. En la tabla de resultados, cambiar estado en el `select`.
2. El frontend ejecuta `PATCH /transfers/{id}/status` y recarga listado.
