package pe.edu.solucionesweb.tramite.service; import org.springframework.stereotype.Component; import pe.edu.solucionesweb.tramite.model.Tramite;
@Component public class PermisoTipoTramite implements TipoTramite { public String nombre(){return "Permiso";} public void validar(Tramite t){ SolicitudTipoTramite.validarComun(t); } }
