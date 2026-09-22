package pe.edu.solucionesweb.tramite.controller;
import jakarta.validation.Valid; import org.springframework.http.*; import org.springframework.web.bind.annotation.*; import pe.edu.solucionesweb.tramite.model.Documento; import pe.edu.solucionesweb.tramite.repository.DocumentoRepository; import java.util.*;
@RestController @RequestMapping("/api/documentos") @CrossOrigin(origins="http://localhost:4200") public class DocumentoController {
 private final DocumentoRepository repo; public DocumentoController(DocumentoRepository r){repo=r;}
 @GetMapping public List<Documento> listar(@RequestParam(required=false) String q){return q==null||q.isBlank()?repo.findAll():repo.findByAsuntoContainingIgnoreCaseOrRemitenteContainingIgnoreCase(q,q);}
 @GetMapping("/{id}") public Documento obtener(@PathVariable Long id){return repo.findById(id).orElseThrow(()->new NoSuchElementException("No se encontró el documento."));}
 @PostMapping @ResponseStatus(HttpStatus.CREATED) public Documento crear(@Valid @RequestBody Documento d){return repo.save(d);}
 @PutMapping("/{id}") public Documento editar(@PathVariable Long id,@Valid @RequestBody Documento d){obtener(id); d.setId(id); return repo.save(d);}
 @DeleteMapping("/{id}") @ResponseStatus(HttpStatus.NO_CONTENT) public void eliminar(@PathVariable Long id){repo.delete(obtener(id));}
}
