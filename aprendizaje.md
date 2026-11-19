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
