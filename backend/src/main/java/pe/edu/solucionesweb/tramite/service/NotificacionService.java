package pe.edu.solucionesweb.tramite.service; import pe.edu.solucionesweb.tramite.model.Tramite;
/** DIP: el servicio de negocio depende de esta interfaz, no de correo o SMS concretos. */ public interface NotificacionService { void notificarDerivacion(Tramite tramite); }
