# Verificación de la entrega

## Comprobado

- Compilación de código TypeScript y plantillas Angular con comprobación estricta: `node node_modules/@angular/compiler-cli/bundles/src/bin/ngc.js -p tsconfig.app.json` completó con código 0.
- Backend Spring Boot y repositorios JPA ejecutados en las pruebas de integración con H2 en modo MySQL.
- **5 pruebas de integración aprobadas; 0 fallos y 0 errores**:
  1. Registrar, listar, buscar por texto e ID, consultar, editar, comprobar persistencia y estado, eliminar y comprobar ausencia.
  2. Rechazar nombres duplicados al registrar y editar, ignorando mayúsculas y espacios exteriores.
  3. Rechazar nombre vacío, estado inválido y campos demasiado largos.
  4. Devolver 404 para editar o eliminar IDs inexistentes.
  5. Rechazar la eliminación de un área vinculada a un usuario y conservarla.

## Límites de la verificación

- La verificación original se ejecutó con JDK 17 y `-Djava.version=17`; en ese momento el `pom.xml` conservaba Java 21 para la entrega. Este es un registro histórico: el runtime objetivo de la entrega se ha actualizado a Java 25.
- Maven generó las clases, pero el cierre de algunos archivos JAR produjo errores de acceso del entorno. Se ejecutaron las clases generadas mediante `mvn -Djava.version=17 surefire:test`, que finalizó con `BUILD SUCCESS` y las cinco pruebas aprobadas.
- `npm run build` no completó el empaquetado: el ejecutable de esbuild no pudo leer una carpeta superior en este entorno restringido de Windows. La compilación independiente de Angular con plantillas estrictas sí pasó. Ejecuta `npm.cmd run build` en tu equipo siguiendo la guía.
- No se ejecutó una prueba visual en navegador ni una instalación real en MySQL. Los scripts SQL se revisaron y se entregan separados para instalación nueva y actualización; las pruebas de persistencia se hicieron con H2, no con el servidor MySQL del usuario.

Esta entrega implementa exclusivamente el mantenimiento de Áreas. Para usarlo en tu equipo es necesario preparar MySQL y configurar sus credenciales, luego iniciar frontend y backend según `GUIA_AREAS.md`.
