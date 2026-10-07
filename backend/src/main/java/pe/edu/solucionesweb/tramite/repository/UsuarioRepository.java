package pe.edu.solucionesweb.tramite.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pe.edu.solucionesweb.tramite.model.Usuario;
import java.util.List;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    
    @Query("SELECT u FROM Usuario u WHERE :filtro IS NULL OR u.nombre LIKE %:filtro% OR u.apellidos LIKE %:filtro% OR u.dni LIKE %:filtro%")
    List<Usuario> buscarPorFiltro(@Param("filtro") String filtro);
}