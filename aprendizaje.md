# Aprendizaje — Mini Spring Boot Tasks

Registro de los ciclos TDD y de las decisiones del proyecto. Mini Tasks aplica
en contexto pequeño los contenidos de las sesiones de Battleship (H2/JPA,
controladores, capas, DTOs y pruebas).

## Sesión 3 — GET /v1/tasks/{id} en V1

V1 guarda las tareas en memoria (`List<SimpleTask>` dentro de
`TaskControllerV1`), así que cada test crea y consulta su propia tarea dentro
del mismo método, sin depender de lo que hiciera otro test.

| Test | Rojo observado | Cambio que lo hizo pasar | Verde |
|------|----------------|--------------------------|-------|
| `TaskControllerV1Test#getsExistingTask` | `Status expected:<200> but was:<404>`: V1 todavía no tenía ruta `GET /v1/tasks/{id}` | Método `get` en `TaskControllerV1` con `orElseThrow()` sobre la lista en memoria | `mvn test` en verde |
| `TaskControllerV1Test#missingTaskReturns404` | `java.util.NoSuchElementException: No value present`: `orElseThrow()` lanzaba una excepción sin traducir a HTTP | Cambio a `map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build())` | `mvn test` en verde |

Antes de esos dos ciclos, `listsTasks`, `createsTask` (V1) y
`createsTaskWithLocation` (V2) fijaron por test los contratos ya implementados:
listado y creación con `201` en V1, creación con `201 + Location: /v2/tasks/1`
en V2. Leer y probar lo ya implementado no sustituye al TDD: el avance de esta
sesión es el ciclo rojo-verde del GET por id.

### Responsabilidades de las capas (estado de la sesión 3)

- **`TaskControllerV1`** — rutas `/v1/tasks`: listado, consulta por id y
  creación. Los datos siguen en memoria; todavía no hay servicio ni
  persistencia en el camino HTTP.
- **`TaskControllerV2`** — ruta `/v2/tasks`: creación con `201` y `Location`.
- **`SimpleTask`** — representación pública de la tarea en memoria
  (`id`, `title`, `done`).
- **`CreateTaskDTO`** — dato de entrada del POST (`title`).
- **`Task`** — entidad JPA (`id`, `title`, `done`) con su migración Flyway
  `V1__create_tasks_table.sql`; por ahora solo la usan sus tests.

## Sesión 4 — capa de servicio y normalización del título

El recorrido de una creación pasa a ser
`petición → controlador → servicio → repositorio → H2`, y la respuesta pública
es ya `TaskResponseDTO` (`id`, `title`, `done`).

| Test | Rojo observado | Cambio que lo hizo pasar | Verde |
|------|----------------|--------------------------|-------|
| `TaskServiceTest#createStripsTitleEdges` | `AssertionFailedError`: el servicio guardaba `dto.title()` sin normalizar, así que devolvía `"  Repasar Spring Boot  "` con los espacios | `dto.title().strip()` en `TaskService.createTask` | `mvn test` en verde |
| `TaskServiceTest#createKeepsCleanTitleAndDoneFalse` | Nace en verde | — | Un título ya limpio se guarda igual y `done=false` |

### Responsabilidades de las capas (estado de la sesión 4)

- **`TaskService`** — casos de uso: crea tareas con el título sin espacios en
  los extremos y traduce `Task` a `TaskResponseDTO`. No conoce HTTP.
- **`TaskRepository`** — interfaz `JpaRepository<Task, Long>`; el acceso a los
  datos pasa por Hibernate sobre H2, con `ddl-auto: validate` contra lo que
  crea Flyway.
- **`TaskResponseDTO`** — contrato de salida de la API (`id`, `title`,
  `done`).
- **`CreateTaskDTO`** — contrato de entrada del POST, compartido por V1 y V2.

## Cierre — slice vertical: PATCH /v2/tasks/{id}/complete

Conecta las cuatro capas (`TaskControllerV2` → `TaskService` → `TaskRepository`
→ H2) para completar una tarea de extremo a extremo, respondiendo `404` cuando el
id no existe.

| Test | Rojo observado | Cambio que lo hizo pasar | Verde |
|------|----------------|--------------------------|-------|
| `TaskServiceTest#completeMarksTaskDone` | `UnsupportedOperationException: Pending implementation` | `completeTask`: busca por id, `setDone(true)` y guarda | `mvn test` en verde |
| `TaskServiceTest#completeTwiceKeepsTaskDone` | `UnsupportedOperationException: Pending implementation` | Comprueba que `completeTask` es idempotente: una tarea ya completada permanece en `done=true` | `mvn test` en verde |
| `TaskControllerV2Test#completesTask` | `Status expected:<200> but was:<404>`: no existía el mapeo `@PatchMapping` | Método `complete` en `TaskControllerV2` delegando en el servicio | `mvn test` en verde |
| `TaskControllerV2Test#completingMissingTaskReturns404` | `ServletException` por `TaskNotFoundException` sin captura | Bloque `try/catch` en el controlador que traduce la excepción a `ResponseEntity.notFound().build()` | `mvn test` en verde |
| `TaskHttpIntegrationTest#createsCompletesAndFailsForMissingTask` | Nace en verde | — | Recorrido HTTP real con MockMvc sobre H2 real: POST con espacios → `201` con título limpio; PATCH → `200` y `done=true`; PATCH inexistente → `404` |

### Responsabilidades finales por capas

- **`TaskControllerV2`** — controlador REST de V2. Delegación limpia en
  `TaskService`, construye `201` con `Location` en la creación y captura
  `TaskNotFoundException` para responder `404` sin cuerpo en la modificación.
- **`TaskService`** — capa de negocio. Contiene los casos de uso
  (`createTask`, `completeTask`), aplica las reglas (normalización del título
  con `strip()`, cambio de estado `done=true`) y transforma las entidades
  `Task` a `TaskResponseDTO`. Lanza la excepción de dominio `TaskNotFoundException`.
- **`TaskRepository`** — capa de acceso a datos (`JpaRepository<Task, Long>`).
  Gestionada por Spring Data JPA y Hibernate con `ddl-auto: validate`.
- **`Task`** — entidad persistente JPA mapeada a la tabla `tasks` creada por
  la migración V1 de Flyway.
- **`TaskResponseDTO` / `CreateTaskDTO`** — representaciones públicas de
  salida y entrada, aislando los contratos JSON de la entidad JPA.
- **`TaskNotFoundException`** — excepción de dominio de la capa de servicio
  cuando la tarea no existe en base de datos.

