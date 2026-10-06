-- Para una base de datos YA existente: ejecutar UNA SOLA VEZ en MySQL Workbench.
-- No ejecutar si se acaba de importar tramite_documentario.sql actualizado.
-- Conserva los registros y las relaciones existentes.
USE tramite_documentario;
ALTER TABLE areas
 ADD COLUMN estado VARCHAR(10) NOT NULL DEFAULT 'ACTIVO',
 ADD CONSTRAINT chk_area_estado CHECK (estado IN ('ACTIVO', 'INACTIVO'));
