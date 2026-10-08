package pe.edu.solucionesweb.tramite.repository;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pe.edu.solucionesweb.tramite.model.EstadoTramite;
import pe.edu.solucionesweb.tramite.model.Tramite;

public interface TramiteRepository extends JpaRepository<Tramite,Long>{
 long countByEstado(EstadoTramite estado);
 boolean existsByCodigoIgnoreCase(String codigo);
 boolean existsByCodigoIgnoreCaseAndIdNot(String codigo,Long id);

 /** Búsqueda del mantenimiento: ID exacto o coincidencia parcial en los datos principales. */
 @Query("select t from Tramite t left join t.areaResponsable a where "
  + "cast(t.id as string) = :q "
  + "or lower(t.codigo) like lower(concat('%',:q,'%')) "
  + "or lower(t.tipoTramite) like lower(concat('%',:q,'%')) "
  + "or lower(t.solicitante) like lower(concat('%',:q,'%')) "
  + "or t.dniRuc like concat('%',:q,'%') "
  + "or lower(t.asunto) like lower(concat('%',:q,'%')) "
  + "or lower(coalesce(t.descripcion,'')) like lower(concat('%',:q,'%')) "
  + "or lower(a.nombre) like lower(concat('%',:q,'%')) "
  + "or lower(cast(t.estado as string)) like lower(concat('%',replace(:q,' ','_'),'%')) "
  + "order by t.id")
 List<Tramite> buscar(@Param("q") String q);
}
