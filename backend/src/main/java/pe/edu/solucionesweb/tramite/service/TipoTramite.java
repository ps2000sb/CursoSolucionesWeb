package pe.edu.solucionesweb.tramite.service;
import pe.edu.solucionesweb.tramite.model.Tramite;
/** Abstracción OCP: se añade un nuevo tipo creando otra implementación. */
public interface TipoTramite { String nombre(); void validar(Tramite tramite); }
