# Training Log MVP

Aplicacion web personal para registrar entrenamientos, planificar proximas sesiones y consultar estadisticas basicas. Es un MVP simple y mantenible: backend monolitico con Spring Boot, frontend React con estado local y MySQL como base de datos.

## Arquitectura propuesta

El proyecto se divide en dos carpetas raiz:

- `backend/`: API REST con Java 21, Spring Boot, Spring Security, JWT, Spring Data JPA y MySQL.
- `frontend/`: SPA con React, Vite, React Router, Axios, Recharts y CSS simple.

No se usan microservicios, Redux, Docker, WebSockets, CQRS ni integraciones externas. La prioridad es una base clara para seguir creciendo.

## Arbol de carpetas

```text
.
|-- backend/
|   |-- pom.xml
|   |-- .env.example
|   `-- src/
|       |-- main/
|       |   |-- java/com/personaltraining/app/
|       |   |   |-- config/
|       |   |   |-- controller/
|       |   |   |-- dto/
|       |   |   |-- entity/
|       |   |   |-- exception/
|       |   |   |-- repository/
|       |   |   |-- security/
|       |   |   `-- service/
|       |   `-- resources/application.yml
|       `-- test/
|           |-- java/com/personaltraining/app/ApiIntegrationTest.java
|           `-- resources/application-test.yml
|-- frontend/
|   |-- package.json
|   |-- package-lock.json
|   |-- .env.example
|   |-- vite.config.js
|   `-- src/
|       |-- components/
|       |-- context/
|       |-- pages/
|       |-- services/
|       |-- styles/
|       |-- test/
|       `-- utils/
|-- tools/
|   `-- node-v22.22.2-win-x64/   # Node portable local, ignorado por Git
|-- .gitignore
`-- README.md
```

## Decisiones tecnicas principales

- Arquitectura clasica por capas (`controller`, `service`, `repository`) porque es suficiente para un MVP y facil de entender.
- DTOs para requests y responses donde aportan claridad.
- Validacion con Bean Validation.
- Seguridad stateless con JWT. El backend no guarda sesiones.
- Cada consulta o modificacion de actividades y entrenamientos filtra por el usuario autenticado.
- No hay capa `mapper` separada: los servicios convierten entidades a DTOs con metodos privados para evitar abstraccion prematura.
- Las estadisticas se calculan con agregaciones simples sobre las actividades del usuario.
- `JPA_DDL_AUTO=update` se usa para desarrollo local. En una version posterior convendria usar Flyway o Liquibase.
- Los tests de backend usan H2 en memoria con perfil `test` para no depender de XAMPP.
- Los tests de frontend mockean las llamadas HTTP para validar comportamiento de UI sin arrancar el backend.

## Stack

Backend:

- Java 21
- Spring Boot 3
- Spring Web
- Spring Data JPA
- Spring Security
- JWT
- MySQL
- Maven
- JUnit 5
- MockMvc
- H2 en scope test

Frontend:

- React
- Vite
- React Router
- Axios
- Recharts
- CSS simple
- Vitest
- Testing Library
- jsdom

## Modelo de datos

Entidades principales:

- `User`: usuario registrado. Campos principales: `name`, `email`, `passwordHash`, `createdAt`, `updatedAt`.
- `Activity`: actividad realizada. Incluye fecha, deporte, titulo, descripcion, duracion, distancia, ritmo opcional, ubicacion y notas.
- `PlannedWorkout`: entrenamiento planificado. Incluye fecha, deporte, objetivos opcionales y estado `PENDING`, `DONE` o `CANCELLED`.

Relaciones:

- `User` 1:N `Activity`
- `User` 1:N `PlannedWorkout`

## Requisitos previos

- Java 21 o superior disponible para Maven.
- Maven instalado.
- XAMPP con MySQL arrancado.
- Node.js. En esta carpeta ya se dejo una instalacion portable en `tools/node-v22.22.2-win-x64/`, asi que no hace falta instalar Node globalmente para ejecutar este proyecto.

## Configurar MySQL con XAMPP

1. Abre XAMPP Control Panel.
2. Arranca el modulo `MySQL`.
3. El backend usa por defecto:

```text
host: localhost
port: 3306
database: training_app
user: root
password: vacia
```

La URL incluye `createDatabaseIfNotExist=true`, asi que la base puede crearse automaticamente si el usuario tiene permisos.

Tambien puedes crearla manualmente desde phpMyAdmin o consola:

```sql
CREATE DATABASE training_app CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

## Variables de entorno

Backend: usa `backend/.env.example` como referencia.

```text
DB_URL=jdbc:mysql://localhost:3306/training_app?createDatabaseIfNotExist=true&useSSL=false&serverTimezone=UTC
DB_USERNAME=root
DB_PASSWORD=
JPA_DDL_AUTO=update
JWT_SECRET=change_this_to_a_long_random_secret_with_at_least_32_characters
JWT_EXPIRATION_MS=86400000
CORS_ALLOWED_ORIGINS=http://localhost:5173
```

Frontend: usa `frontend/.env.example` como referencia.

```text
VITE_API_URL=http://localhost:8080/api
```

Para desarrollo local no es obligatorio crear `.env` si aceptas los valores por defecto.

## Arrancar backend

Desde `backend/`:

```bash
mvn spring-boot:run
```

La API queda disponible en:

```text
http://localhost:8080/api
```

## Arrancar frontend

Si tienes Node/npm global:

```bash
cd frontend
npm install
npm run dev
```

Con el Node portable incluido en `tools/`, desde `frontend/` en PowerShell:

```powershell
$env:PATH="C:\Users\plusg\Desktop\Dani\app running\tools\node-v22.22.2-win-x64;$env:PATH"
..\tools\node-v22.22.2-win-x64\npm.cmd install
..\tools\node-v22.22.2-win-x64\npm.cmd run dev
```

La app queda disponible en:

```text
http://localhost:5173
```

## Uso basico

1. Abre `http://localhost:5173`.
2. Registra un usuario.
3. Crea actividades desde `Actividades`.
4. Crea entrenamientos desde `Planificados`.
5. Revisa el `Dashboard` y la vista de `Estadisticas`.
6. Usa logout para cerrar sesion.

## Tests

### Backend

Los tests de backend estan en:

```text
backend/src/test/java/com/personaltraining/app/ApiIntegrationTest.java
backend/src/test/resources/application-test.yml
```

Cubren:

- Registro, login, rechazo de email duplicado y bloqueo de rutas privadas sin token.
- Flujo principal autenticado: CRUD de actividades, aislamiento entre usuarios, CRUD de entrenamientos planificados, cambio de estado, dashboard y estadisticas.

Ejecutar desde `backend/`:

```bash
mvn test
```

No hace falta tener MySQL arrancado para estos tests porque usan H2 en memoria con el perfil `test`.

### Frontend

Los tests de frontend estan en:

```text
frontend/src/pages/LoginPage.test.jsx
frontend/src/pages/ActivitiesPage.test.jsx
frontend/src/test/setup.js
```

Cubren:

- Login correcto, llamada al contexto de autenticacion y redireccion.
- Error visible cuando falla el login.
- Carga del listado de actividades.
- Eliminacion de actividad con confirmacion y recarga de datos.

Ejecutar desde `frontend/` con Node/npm global:

```bash
npm test
```

Con el Node portable incluido:

```powershell
$env:PATH="C:\Users\plusg\Desktop\Dani\app running\tools\node-v22.22.2-win-x64;$env:PATH"
..\tools\node-v22.22.2-win-x64\npm.cmd test
```

### Validacion completa recomendada

Desde las carpetas correspondientes:

```bash
mvn test
npm test
npm run build
```

## Endpoints de la API

Todos los endpoints salvo autenticacion requieren:

```text
Authorization: Bearer <token>
```

### Auth

| Metodo | Endpoint | Descripcion |
| --- | --- | --- |
| POST | `/api/auth/register` | Registra usuario y devuelve JWT |
| POST | `/api/auth/login` | Inicia sesion y devuelve JWT |

### Activities

| Metodo | Endpoint | Descripcion |
| --- | --- | --- |
| GET | `/api/activities` | Lista actividades del usuario autenticado |
| GET | `/api/activities/{id}` | Obtiene detalle de una actividad propia |
| POST | `/api/activities` | Crea una actividad |
| PUT | `/api/activities/{id}` | Edita una actividad propia |
| DELETE | `/api/activities/{id}` | Elimina una actividad propia |

Ejemplo:

```json
{
  "activityDate": "2026-04-17",
  "sportType": "RUNNING",
  "title": "Rodaje suave",
  "description": "Entrenamiento comodo",
  "durationMinutes": 45,
  "distanceKm": 8.5,
  "averagePace": 5.18,
  "location": "Parque",
  "notes": "Buenas sensaciones"
}
```

### Planned Workouts

| Metodo | Endpoint | Descripcion |
| --- | --- | --- |
| GET | `/api/planned-workouts` | Lista entrenamientos planificados |
| POST | `/api/planned-workouts` | Crea entrenamiento planificado |
| PUT | `/api/planned-workouts/{id}` | Edita entrenamiento planificado |
| PATCH | `/api/planned-workouts/{id}/status` | Cambia estado |
| DELETE | `/api/planned-workouts/{id}` | Elimina entrenamiento planificado |

Ejemplo:

```json
{
  "plannedDate": "2026-04-20",
  "title": "Series cortas",
  "description": "6x400m",
  "sportType": "RUNNING",
  "targetDurationMinutes": 50,
  "targetDistanceKm": 9,
  "status": "PENDING"
}
```

Actualizar estado:

```json
{
  "status": "DONE"
}
```

### Dashboard y estadisticas

| Metodo | Endpoint | Descripcion |
| --- | --- | --- |
| GET | `/api/dashboard/summary` | Resumen general y proximos 5 entrenamientos |
| GET | `/api/stats/monthly` | Distancia y tiempo por mes |
| GET | `/api/stats/sports` | Numero de actividades por deporte |
| GET | `/api/stats/pace-summary` | Ritmo medio general si existe |

## Checklist manual

1. Arrancar MySQL desde XAMPP.
2. Arrancar backend con `mvn spring-boot:run`.
3. Arrancar frontend con `npm run dev` o con el Node portable.
4. Ejecutar `mvn test`.
5. Ejecutar `npm test`.
6. Registrar un usuario.
7. Crear una actividad.
8. Comprobar que aparece en listado, detalle, dashboard y estadisticas.
9. Editar y eliminar una actividad.
10. Crear un entrenamiento planificado.
11. Cambiar su estado a `DONE` o `CANCELLED`.
12. Hacer logout y confirmar que las rutas privadas redirigen a login.

## Mejoras futuras

- Migraciones con Flyway.
- Paginacion en listados.
- Filtros por fecha y deporte.
- Perfil de usuario.
- Exportacion CSV.
- Mejoras visuales y accesibilidad avanzada.
- Tests end-to-end con Playwright cuando el flujo crezca.
