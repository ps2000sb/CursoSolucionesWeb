# Mantenimiento de Trámites — Integrante 3

Permite **registrar, listar, buscar, ver, editar y eliminar** trámites desde el menú **Trámites**. Sigue la misma organización que el mantenimiento de Áreas (Angular + Spring Boot + MySQL).

## Datos del trámite

| Campo | Regla |
| --- | --- |
| Código | Obligatorio, hasta 30 caracteres, único (sin distinguir mayúsculas) |
| Tipo de trámite | `Solicitud`, `Queja` o `Permiso` (la queja exige descripción) |
| Solicitante | Obligatorio, hasta 150 caracteres |
| DNI/RUC | Solo números: 8 dígitos (DNI) u 11 dígitos (RUC) |
| Asunto | Obligatorio, hasta 255 caracteres |
| Descripción | Hasta 1000 caracteres (obligatoria en Queja) |
| Fecha | Obligatoria (por defecto, la de hoy) |
| Área responsable | Obligatoria; solo áreas activas (al editar se conserva la actual) |
| Estado | `PENDIENTE`, `EN_PROCESO`, `DERIVADO`, `ATENDIDO`, `CONCLUIDO` |

## API

| Operación | Método y ruta |
| --- | --- |
| Listar / buscar | `GET /api/tramites?q=texto` |
| Ver detalle | `GET /api/tramites/{id}` |
| Registrar | `POST /api/tramites` → 201 |
| Editar | `PUT /api/tramites/{id}` |
| Eliminar | `DELETE /api/tramites/{id}` → 204 |

La búsqueda admite ID exacto o parte del código, tipo, solicitante, DNI/RUC, asunto, descripción, área responsable y estado.
Errores: datos inválidos 400, ID inexistente 404, código repetido 409; el cuerpo trae `mensaje`.
Al eliminar un trámite se borran también su historial y sus derivaciones. Si se cambia el estado al editar, se agrega una línea al historial.

Las rutas de derivación, cambio de estado e historial (`/derivaciones`, `/estado`, `/historial`) se mantienen; en el detalle del mantenimiento, el botón **Derivación e historial** abre esa pantalla.

## Demostración

1. **Registrar:** `TRM-0003`, tipo Queja, solicitante, DNI de 8 dígitos, asunto, descripción, área y estado.
2. **Listar** y **Buscar** por código, solicitante, tipo o estado; **Limpiar** vuelve a la lista completa.
3. **Ver** el detalle y **Editar** (cambia asunto o estado y comprueba que se guardó).
4. **Eliminar** con confirmación.
5. Validaciones: código repetido, DNI con letras, queja sin descripción, área vacía.

## Archivos

- Frontend: `frontend/src/app/tramites/` (componente), `services/tramite.service.ts`, integración en `app.component.*`.
- Backend: `TramiteController`, `TramiteService`, `TramiteRepository`, y `deleteByTramiteId` en historial y derivaciones.
- Prueba: `backend/src/test/java/pe/edu/solucionesweb/tramite/TramiteIntegrationTest.java` (H2 temporal): `cd backend && mvn test`.
