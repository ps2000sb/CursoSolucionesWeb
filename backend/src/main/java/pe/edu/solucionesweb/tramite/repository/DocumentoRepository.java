package pe.edu.solucionesweb.tramite.repository; import org.springframework.data.jpa.repository.JpaRepository; import pe.edu.solucionesweb.tramite.model.Documento; import java.util.List;
public interface DocumentoRepository extends JpaRepository<Documento,Long>{ List<Documento> findByAsuntoContainingIgnoreCaseOrRemitenteContainingIgnoreCase(String asunto,String remitente); }
