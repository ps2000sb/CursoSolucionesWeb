# Mantenimiento de Áreas — Integrante 4

Este proyecto incluye registrar, listar, buscar, consultar, editar y eliminar áreas desde el menú **Áreas**. Usa Angular, Spring Boot y MySQL, conservando el diseño Bootstrap y la organización del proyecto original.

## Preparación y ejecución en Visual Studio Code

1. Descomprime el ZIP y abre la carpeta `CursoSolucionesWeb-main` en VS Code.
2. Necesitas Node.js compatible con Angular 19 (por ejemplo, Node 22), JDK 25 (LTS), Maven 3.9 y MySQL 8. Puedes editar el proyecto en VS Code o Eclipse.
3. Prepara la base de datos en MySQL Workbench:
   - **Instalación nueva:** ejecuta `database/tramite_documentario.sql` en una base nueva. Incluye las áreas de ejemplo y la columna `estado`.
   - **Base del proyecto original ya instalada:** ejecuta **solo** `database/actualizar_areas.sql`, una vez. Conserva los registros. No vuelvas a ejecutar el script de instalación sobre las tablas existentes.
4. En `backend/src/main/resources/application.properties`, configura `spring.datasource.username` con tu usuario de MySQL. Para la contraseña, define `DB_PASSWORD` en la terminal del backend:

   ```powershell
   $env:DB_PASSWORD = 'tu_contraseña_mysql'
   cd backend
   mvn spring-boot:run
   ```

   Si tu MySQL no tiene contraseña, no necesitas definir esa variable. Mantén abierta esta terminal.
5. Abre otra terminal desde la carpeta del proyecto:

   ```powershell
   cd frontend
   npm.cmd ci
   npm.cmd start
   ```

6. Abre `http://localhost:4200` y selecciona **Áreas** en el menú lateral.

## Demostración para la actividad

1. **Registrar:** crea `Archivo Municipal`, con descripción y estado Activo. El ID se genera automáticamente.
2. **Listar:** verifica que el registro aparece en la tabla.
3. **Buscar:** escribe parte del nombre y pulsa Buscar. También admite ID exacto, descripción y estado exacto (`ACTIVO` / `INACTIVO`). Limpiar recupera la lista completa.
4. **Ver:** pulsa Ver para consultar los cuatro campos guardados.
5. **Editar:** modifica el nombre, descripción o estado y guarda; vuelve a consultar el registro para comprobar la persistencia.
6. **Eliminar:** elimina el área de prueba y confirma la operación. No uses para esta demostración las áreas iniciales vinculadas a usuarios o trámites.
7. Comprueba las validaciones: nombre vacío, nombre repetido y longitudes máximas.

El nombre admite hasta 120 caracteres y la descripción hasta 255. Se eliminan los espacios al inicio y al final; los nombres repetidos se rechazan sin distinguir mayúsculas. El estado es Activo o Inactivo.

Las claves foráneas de MySQL impiden eliminar áreas usadas en usuarios, documentos, trámites, historial o derivaciones. El frontend muestra un mensaje explicativo. Puedes editar su estado a Inactivo conservando las relaciones. Los selectores existentes de área responsable y área de derivación muestran áreas activas; los registros anteriores conservan sus áreas.

## API del mantenimiento

| Operación | Método y ruta |
| --- | --- |
| Listar / buscar | `GET /api/areas?q=texto` |
| Consultar | `GET /api/areas/{id}` |
| Registrar | `POST /api/areas` |
| Editar | `PUT /api/areas/{id}` |
| Eliminar | `DELETE /api/areas/{id}` |

Ejemplo de cuerpo para registrar o editar:

```json
{"nombre":"Archivo Municipal","descripcion":"Custodia documental","estado":"ACTIVO"}
```

Los errores devuelven `mensaje`. El registro devuelve HTTP 201, la eliminación 204, los datos inválidos 400, un ID inexistente 404 y los conflictos 409. La edición conserva el ID de la ruta; el registro genera uno nuevo.

## Archivos principales

- Frontend: `areas/areas.component.ts`, su HTML y `services/area.service.ts` dentro de `frontend/src/app`.
- Integración del menú: `app.component.html` y `app.component.ts`.
- Backend: `controller/AreaController.java`, `model/Area.java`, `repository/AreaRepository.java` y manejo de errores.
- SQL: instalación actualizada y migración independiente para bases existentes.
- Pruebas: `backend/src/test/java/pe/edu/solucionesweb/tramite/AreaIntegrationTest.java`.

También se agregó el método `cargarDocumentos` que el HTML original ya invocaba, para resolver un error previo de compilación. Los otros cuatro mantenimientos no se completaron en esta entrega.

## Verificación

Desde `frontend`, ejecuta `npm.cmd run build`. Desde `backend`, ejecuta `mvn test`. Las pruebas de API usan una base H2 temporal y no modifican tu base MySQL. Incluyen el ciclo CRUD, búsqueda, duplicados, validación, IDs inexistentes y protección de áreas vinculadas.

Consulta `VERIFICACION.md` para conocer las pruebas ejecutadas en esta entrega y sus límites.
