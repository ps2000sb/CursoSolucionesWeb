package pe.edu.solucionesweb.tramite.service; import org.springframework.stereotype.Service; import pe.edu.solucionesweb.tramite.model.Tramite;
@Service public class NotificacionConsolaService implements NotificacionService { public void notificarDerivacion(Tramite tramite){System.out.println("Notificación: trámite "+tramite.getCodigo()+" derivado.");} }
