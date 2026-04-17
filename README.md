# Training Log MVP

Aplicación web personal para registrar entrenamientos, planificar próximas sesiones y consultar estadísticas básicas. Es un MVP intencionadamente simple: backend monolítico con Spring Boot, frontend React con estado local y MySQL como base de datos.

## Stack

Backend:
- Java 21
- Spring Boot
- Spring Web
- Spring Data JPA
- Spring Security
- JWT
- MySQL
- Maven

Frontend:
- React
- Vite
- React Router
- Axios
- Recharts
- CSS simple

## Estructura

```text
.
├── backend/
│   ├── pom.xml
│   ├── .env.example
│   └── src/main/
│       ├── java/com/personaltraining/app/
│       │   ├── config/
│       │   ├── controller/
│       │   ├── dto/
│       │   ├── entity/
│       │   ├── exception/
│       │   ├── repository/
│       │   ├── security/
│       │   └── service/
│       └── resources/application.yml
├── frontend/
│   ├── package.json
│   ├── .env.example
│   ├── vite.config.js
│   └── src/
│       ├── components/
│       ├── context/
│       ├── pages/
│       ├── services/
│       ├── styles/
│       └── utils/
└── README.md
```

## Modelo de datos

Entidades principales:
- `User`: usuario registrado. Tiene `name`, `email`, `passwordHash`, `createdAt`, `updatedAt`.
- `Activity`: actividad realizada por un usuario. Incluye fecha, deporte, título, duración, distancia, ritmo opcional, ubicación y notas.
- `PlannedWorkout`: entrenamiento planificado por un usuario. Incluye fecha, deporte, objetivos opcionales y estado `PENDING`, `DONE` o `CANCELLED`.

Relaciones:
- `User` 1:N `Activity`
- `User` 1:N `PlannedWorkout`

## Requisitos previos

- Java 21
- Maven
- Node.js 20 o superior
- MySQL 8 o superior

## Configurar MySQL

Puedes dejar que Hibernate cree las tablas usando `JPA_DDL_AUTO=update`. La base puede crearse automáticamente si el usuario de MySQL tiene permisos, porque la URL incluye `createDatabaseIfNotExist=true`.

O puedes crearla manualmente:

```sql
CREATE DATABASE training_app CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

## Variables de entorno

Backend: copia `backend/.env.example` como referencia y configura estas variables en tu terminal o en tu IDE:

```text
DB_URL=jdbc:mysql://localhost:3306/training_app?createDatabaseIfNotExist=true&useSSL=false&serverTimezone=UTC
DB_USERNAME=root
DB_PASSWORD=change_me
JPA_DDL_AUTO=update
JWT_SECRET=change_this_to_a_long_random_secret_with_at_least_32_characters
JWT_EXPIRATION_MS=86400000
CORS_ALLOWED_ORIGINS=http://localhost:5173
```

Frontend: copia `frontend/.env.example` a `frontend/.env` si quieres cambiar la URL de la API.

```text
VITE_API_URL=http://localhost:8080/api
```

## Arrancar backend

Desde `backend/`:

```bash
mvn spring-boot:run
```

La API quedará disponible en:

```text
http://localhost:8080/api
```

## Arrancar frontend

Desde `frontend/`:

```bash
npm install
npm run dev
```

La app quedará disponible en:

```text
http://localhost:5173
```

## Uso básico

1. Abre `http://localhost:5173`.
2. Registra un usuario.
3. Crea varias actividades desde `Actividades`.
4. Crea entrenamientos desde `Planificados`.
5. Revisa el `Dashboard` y la vista de `Estadísticas`.

## Endpoints

Todos los endpoints salvo autenticación requieren:

```text
Authorization: Bearer <token>
```

### Auth

| Método | Endpoint | Descripción |
| --- | --- | --- |
| POST | `/api/auth/register` | Registra usuario y devuelve JWT |
| POST | `/api/auth/login` | Inicia sesión y devuelve JWT |

### Activities

| Método | Endpoint | Descripción |
| --- | --- | --- |
| GET | `/api/activities` | Lista actividades del usuario autenticado |
| GET | `/api/activities/{id}` | Obtiene detalle de una actividad propia |
| POST | `/api/activities` | Crea una actividad |
| PUT | `/api/activities/{id}` | Edita una actividad propia |
| DELETE | `/api/activities/{id}` | Elimina una actividad propia |

Ejemplo de actividad:

```json
{
  "activityDate": "2026-04-17",
  "sportType": "RUNNING",
  "title": "Rodaje suave",
  "description": "Entrenamiento cómodo",
  "durationMinutes": 45,
  "distanceKm": 8.5,
  "averagePace": 5.18,
  "location": "Parque",
  "notes": "Buenas sensaciones"
}
```

### Planned Workouts

| Método | Endpoint | Descripción |
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

### Dashboard y estadísticas

| Método | Endpoint | Descripción |
| --- | --- | --- |
| GET | `/api/dashboard/summary` | Resumen general y próximos 5 entrenamientos |
| GET | `/api/stats/monthly` | Distancia y tiempo por mes |
| GET | `/api/stats/sports` | Número de actividades por deporte |
| GET | `/api/stats/pace-summary` | Ritmo medio general si existe |

## Decisiones técnicas

- Se usa una arquitectura clásica por capas (`controller`, `service`, `repository`) porque es suficiente para un MVP y fácil de entender.
- No hay Redux. La sesión vive en `AuthContext` y el token se guarda en `localStorage`.
- No hay capa de mappers separada. Los servicios convierten entidad a DTO con métodos privados para evitar abstracción prematura.
- Las estadísticas se calculan con agregaciones simples en memoria sobre las actividades del usuario. Para uso personal es más legible y suficiente.
- La seguridad es stateless con JWT. El backend no guarda sesiones.
- `JPA_DDL_AUTO=update` se usa para desarrollo local. En una versión más madura convendría usar migraciones con Flyway o Liquibase.

## Checklist manual

1. Arrancar MySQL.
2. Arrancar backend con `mvn spring-boot:run`.
3. Arrancar frontend con `npm install` y `npm run dev`.
4. Registrar un usuario.
5. Crear una actividad y comprobar que aparece en listado, detalle y dashboard.
6. Editar y eliminar una actividad.
7. Crear un entrenamiento planificado.
8. Cambiar su estado a `DONE` o `CANCELLED`.
9. Comprobar que las estadísticas cambian al añadir actividades.
10. Hacer logout y confirmar que las rutas privadas vuelven a login.

## Mejoras futuras

- Tests de integración backend.
- Migraciones de base de datos con Flyway.
- Paginación en listados.
- Filtros por fecha/deporte.
- Perfil de usuario.
- Exportación CSV.
- Mejoras visuales y accesibilidad avanzada.
