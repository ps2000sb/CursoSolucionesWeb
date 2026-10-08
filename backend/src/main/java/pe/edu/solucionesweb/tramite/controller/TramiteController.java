package pe.edu.solucionesweb.tramite.controller;
import jakarta.validation.Valid; import org.springframework.http.*; import org.springframework.web.bind.annotation.*; import pe.edu.solucionesweb.tramite.dto.DerivacionRequest; import pe.edu.solucionesweb.tramite.model.*; import pe.edu.solucionesweb.tramite.service.TramiteService; import java.util.*;

/** Mantenimiento de Trámites: listar/buscar, ver, registrar, editar y eliminar (la validación vive en TramiteService). */
@RestController @RequestMapping("/api/tramites") @CrossOrigin(origins="http://localhost:4200") public class TramiteController { private final TramiteService service; public TramiteController(TramiteService s){service=s;}
 @GetMapping public List<Tramite> listar(@RequestParam(required=false) String q){return service.listar(q);}
 @GetMapping("/{id}") public Tramite obtener(@PathVariable Long id){return service.obtener(id);}
 @PostMapping @ResponseStatus(HttpStatus.CREATED) public Tramite crear(@RequestBody Tramite t){return service.registrar(t);}
 @PutMapping("/{id}") public Tramite editar(@PathVariable Long id,@RequestBody Tramite t){return service.editar(id,t);}
 @DeleteMapping("/{id}") @ResponseStatus(HttpStatus.NO_CONTENT) public void eliminar(@PathVariable Long id){service.eliminar(id);}
 @PostMapping("/{id}/derivaciones") public Tramite derivar(@PathVariable Long id,@Valid @RequestBody DerivacionRequest r){return service.derivar(id,r);}
 @PatchMapping("/{id}/estado") public Tramite estado(@PathVariable Long id,@RequestParam EstadoTramite estado,@RequestParam(required=false) String observacion){return service.actualizarEstado(id,estado,observacion);}
 @GetMapping("/{id}/historial") public List<HistorialTramite> historial(@PathVariable Long id){return service.historial(id);}
}
