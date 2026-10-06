package pe.edu.solucionesweb.tramite.controller;

import java.util.List;
import java.util.Locale;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import pe.edu.solucionesweb.tramite.model.Area;
import pe.edu.solucionesweb.tramite.repository.AreaRepository;

@RestController
@RequestMapping("/api/areas")
@CrossOrigin(origins="http://localhost:4200")
public class AreaController {
 private final AreaRepository areas;
 public AreaController(AreaRepository areas){this.areas=areas;}

 @GetMapping public List<Area> listar(@RequestParam(defaultValue="") String q){
  String termino=q.strip().toLowerCase(Locale.ROOT);
  return areas.findAll().stream().filter(a -> termino.isEmpty()
   || a.getId().toString().equals(termino)
   || a.getNombre().toLowerCase(Locale.ROOT).contains(termino)
   || (a.getDescripcion()!=null && a.getDescripcion().toLowerCase(Locale.ROOT).contains(termino))
   || a.getEstado().toLowerCase(Locale.ROOT).equals(termino)).toList();
 }
 @GetMapping("/{id}") public Area obtener(@PathVariable Long id){
  return areas.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,"No se encontró el área."));
 }
 @PostMapping @ResponseStatus(HttpStatus.CREATED) public Area registrar(@RequestBody Area datos){
  validar(datos);
  if(areas.existsByNombreIgnoreCase(datos.getNombre()))throw new ResponseStatusException(HttpStatus.CONFLICT,"Ya existe un área con ese nombre.");
  Area area=new Area(); copiar(datos,area); return guardar(area);
 }
 @PutMapping("/{id}") public Area editar(@PathVariable Long id,@RequestBody Area datos){
  Area area=obtener(id); validar(datos);
  if(areas.existsByNombreIgnoreCaseAndIdNot(datos.getNombre(),id))throw new ResponseStatusException(HttpStatus.CONFLICT,"Ya existe un área con ese nombre.");
  copiar(datos,area); return guardar(area);
 }
 @DeleteMapping("/{id}") @ResponseStatus(HttpStatus.NO_CONTENT) public void eliminar(@PathVariable Long id){
  Area area=obtener(id);
  try{areas.delete(area); areas.flush();}
  catch(DataIntegrityViolationException e){throw new ResponseStatusException(HttpStatus.CONFLICT,"El área está vinculada a usuarios, documentos, trámites o derivaciones. Puede marcarla como INACTIVO en lugar de eliminarla.");}
 }
 private Area guardar(Area area){
  try{return areas.saveAndFlush(area);}
  catch(DataIntegrityViolationException e){throw new ResponseStatusException(HttpStatus.CONFLICT,"No se pudo guardar el área. Verifique que el nombre no esté repetido.");}
 }
 private void copiar(Area origen,Area destino){destino.setNombre(origen.getNombre());destino.setDescripcion(origen.getDescripcion());destino.setEstado(origen.getEstado());}
 private void validar(Area area){
  String nombre=area.getNombre()==null?"":area.getNombre().strip();
  String descripcion=area.getDescripcion()==null?"":area.getDescripcion().strip();
  if(nombre.isEmpty()||nombre.length()>120)throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"El nombre es obligatorio y admite hasta 120 caracteres.");
  if(descripcion.length()>255)throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"La descripción admite hasta 255 caracteres.");
  if(!"ACTIVO".equals(area.getEstado())&&!"INACTIVO".equals(area.getEstado()))throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"El estado debe ser ACTIVO o INACTIVO.");
  area.setNombre(nombre);area.setDescripcion(descripcion);
 }
}
