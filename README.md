# Sistema Web para mejorar la gestión del trámite documentario

Proyecto académico de la Municipalidad de Los Olivos, construido con Angular, Spring Boot y MySQL.

El mantenimiento completo de **Áreas (Integrante 4)** está integrado. Consulte [GUIA_AREAS.md](GUIA_AREAS.md) para instalar, actualizar una base existente y demostrar las seis operaciones.

El mantenimiento de **Trámites (Integrante 3)** también está integrado; vea [GUIA_TRAMITES.md](GUIA_TRAMITES.md).

## Ejecución

1. Ejecute `database/tramite_documentario.sql` en MySQL Workbench.
2. Configure el usuario y contraseña de MySQL en `backend/src/main/resources/application.properties`.
3. En `backend`, ejecute `mvn spring-boot:run`.
4. En `frontend`, ejecute `npm install` y luego `npm start`.
5. Abra `http://localhost:4200`.

## Principios SOLID

- SRP: `DocumentoController` se ocupa exclusivamente del recurso documento.
- OCP: `TipoTramite` y sus implementaciones extienden el comportamiento sin cambiar `TramiteService`.
- LSP: los usuarios comparten el modelo `Usuario` y se distinguen por `Rol`.
- ISP: `NotificacionService` contiene solo la operación que necesita el trámite.
- DIP: `TramiteService` depende de la interfaz `NotificacionService`, no de una tecnología de envío concreta.
