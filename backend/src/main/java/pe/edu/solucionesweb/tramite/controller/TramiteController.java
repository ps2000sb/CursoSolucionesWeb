package pe.edu.solucionesweb.tramite.controller;
import jakarta.validation.Valid; import org.springframework.http.*; import org.springframework.web.bind.annotation.*; import pe.edu.solucionesweb.tramite.dto.DerivacionRequest; import pe.edu.solucionesweb.tramite.model.*; import pe.edu.solucionesweb.tramite.repository.TramiteRepository; import pe.edu.solucionesweb.tramite.service.TramiteService; import java.util.*;
@RestController @RequestMapping("/api/tramites") @CrossOrigin(origins="http://localhost:4200") public class TramiteController { private final TramiteService service; private final TramiteRepository repo; public TramiteController(TramiteService s,TramiteRepository r){service=s;repo=r;}
 @GetMapping public List<Tramite> listar(@RequestParam(required=false) String q){return service.listar(q);} @GetMapping("/{id}") public Tramite obtener(@PathVariable Long id){return service.obtener(id);}
 @PostMapping @ResponseStatus(HttpStatus.CREATED) public Tramite crear(@Valid @RequestBody Tramite t){return service.registrar(t);} @PutMapping("/{id}") public Tramite editar(@PathVariable Long id,@Valid @RequestBody Tramite t){service.obtener(id);t.setId(id);return repo.save(t);}
 @DeleteMapping("/{id}") @ResponseStatus(HttpStatus.NO_CONTENT) public void eliminar(@PathVariable Long id){repo.delete(service.obtener(id));}
 @PostMapping("/{id}/derivaciones") public Tramite derivar(@PathVariable Long id,@Valid @RequestBody DerivacionRequest r){return service.derivar(id,r);}
 @PatchMapping("/{id}/estado") public Tramite estado(@PathVariable Long id,@RequestParam EstadoTramite estado,@RequestParam(required=false) String observacion){return service.actualizarEstado(id,estado,observacion);}
 @GetMapping("/{id}/historial") public List<HistorialTramite> historial(@PathVariable Long id){return service.historial(id);}
}
