# Declaración de uso de IA

He utilizado asistencia de Inteligencia Artificial durante el desarrollo de la
práctica **Mini Spring Boot Tasks**, acogiéndome a la política de IA del
enunciado, que permite su uso como asistente para comprender errores y guiar el
desarrollo de las pruebas.

## Detalles del uso material

- **Lectura y traslación de las guías:** utilicé la IA para revisar las guías
  de Battleship (sesiones 2, 3, 4 y 4b) y abstraer las partes que
  correspondían a la tarea individual Mini Tasks (entidad `Task`, controlador V1
  en memoria, controlador V2 con capas, servicio con normalización del título y
  slice de cierre `PATCH /v2/tasks/{id}/complete`).
- **Diagnóstico de errores y fallos TDD:** interpretación de mensajes de Maven,
  de las diferencias entre los códigos HTTP esperados y recibidos (`200` vs
  `404` en V1; `500` vs `404` en V2) y de las excepciones en la capa de
  servicio (`TaskNotFoundException`).
- **Diseño de casos de prueba:** apoyo para escribir las aserciones
  unitarias de `TaskServiceTest` (`createStripsTitleEdges`,
  `completeMarksTaskDone`, `completeTwiceKeepsTaskDone`) y el escenario de
  integración `TaskHttpIntegrationTest` con `@SpringBootTest`,
  `@AutoConfigureMockMvc` y `@Transactional`.

## Aspectos no delegados

- **Comprensión del código:** entiendo la responsabilidad de cada capa
  (controlador REST, servicio con `@Transactional`, repositorio
  `JpaRepository`, DTOs de entrada y salida, entidad JPA y migración Flyway).
- **Ejecución y verificación:** cada commit fue verificado individualmente con
  `mvn test` en mi entorno local sobre Java 25.
- **Redacción final:** la autoría intelectual del código, la configuración del
  proyecto y la redacción de los ficheros de evidencia (`aprendizaje.md`,
  `autoria.md` y este documento) han sido supervisadas y asumidas por mí.

## Herramienta utilizada

- **OpenCode** harness con el modelo **Gemini 3.6 Flash** para consulta,
  depuración de tests y asistencia en el ciclo TDD.
