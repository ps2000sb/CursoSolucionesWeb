package pe.edu.solucionesweb.tramite.service;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import pe.edu.solucionesweb.tramite.dto.DerivacionRequest;
import pe.edu.solucionesweb.tramite.model.*;
import pe.edu.solucionesweb.tramite.repository.*;
import java.time.*;
import java.util.*;

@Service public class TramiteService {
 private final TramiteRepository tramites; private final AreaRepository areas; private final HistorialRepository historiales; private final DerivacionRepository derivaciones; private final NotificacionService notificaciones; private final Map<String,TipoTramite> tipos;
 public TramiteService(TramiteRepository t,AreaRepository a,HistorialRepository h,DerivacionRepository d,NotificacionService n,List<TipoTramite> estrategias){tramites=t;areas=a;historiales=h;derivaciones=d;notificaciones=n;tipos=new HashMap<>(); estrategias.forEach(e->tipos.put(e.nombre().toLowerCase(),e));}

 // ---------- Mantenimiento de Trámites (Integrante 3) ----------
 public List<Tramite> listar(String q){
  String termino=q==null?"":q.strip();
  return termino.isEmpty()?tramites.findAll(org.springframework.data.domain.Sort.by("id")):tramites.buscar(termino);
 }
 public Tramite obtener(Long id){return tramites.findById(id).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"No se encontró el trámite."));}

 @Transactional public Tramite registrar(Tramite datos){
  Tramite t=new Tramite();
  copiar(datos,t,null);
  Tramite guardado=guardar(t);
  registrarHistorial(guardado,guardado.getEstado(),"Sistema","Trámite registrado",guardado.getAreaResponsable());
  return guardado;
 }
 @Transactional public Tramite editar(Long id,Tramite datos){
  Tramite t=obtener(id);
  EstadoTramite estadoAnterior=t.getEstado();
  Long areaAnterior=t.getAreaResponsable()==null?null:t.getAreaResponsable().getId();
  copiar(datos,t,areaAnterior);
  Tramite guardado=guardar(t);
  if(estadoAnterior!=guardado.getEstado())registrarHistorial(guardado,guardado.getEstado(),"Sistema","Estado modificado desde el mantenimiento de trámites",guardado.getAreaResponsable());
  return guardado;
 }
 @Transactional public void eliminar(Long id){
  Tramite t=obtener(id);
  historiales.deleteByTramiteId(id); derivaciones.deleteByTramiteId(id);
  try{tramites.delete(t); tramites.flush();}
  catch(DataIntegrityViolationException e){throw new ResponseStatusException(HttpStatus.CONFLICT,"No se pudo eliminar el trámite porque tiene información vinculada.");}
 }
 private Tramite guardar(Tramite t){
  try{return tramites.saveAndFlush(t);}
  catch(DataIntegrityViolationException e){throw new ResponseStatusException(HttpStatus.CONFLICT,"No se pudo guardar el trámite. Verifique que el código no esté repetido.");}
 }
 /** Valida los datos recibidos y los copia (ya normalizados) al trámite destino; el ID nunca se toma del cuerpo. */
 private void copiar(Tramite d,Tramite t,Long areaActualId){
  String codigo=limpio(d.getCodigo()), tipo=limpio(d.getTipoTramite()), solicitante=limpio(d.getSolicitante());
  String dniRuc=limpio(d.getDniRuc()), asunto=limpio(d.getAsunto()), descripcion=limpio(d.getDescripcion());
  if(codigo.isEmpty()||codigo.length()>30)throw malo("El código es obligatorio y admite hasta 30 caracteres.");
  TipoTramite estrategia=tipos.get(tipo.toLowerCase());
  if(estrategia==null)throw malo("El tipo de trámite debe ser Solicitud, Queja o Permiso.");
  if(solicitante.isEmpty()||solicitante.length()>150)throw malo("El solicitante es obligatorio y admite hasta 150 caracteres.");
  if(!dniRuc.matches("\\d{8}|\\d{11}"))throw malo("El DNI debe tener 8 dígitos o el RUC 11 dígitos (solo números).");
  if(asunto.isEmpty()||asunto.length()>255)throw malo("El asunto es obligatorio y admite hasta 255 caracteres.");
  if(descripcion.length()>1000)throw malo("La descripción admite hasta 1000 caracteres.");
  if(d.getFechaRegistro()==null)throw malo("La fecha es obligatoria.");
  if(d.getEstado()==null)throw malo("El estado es obligatorio.");
  if(d.getAreaResponsable()==null||d.getAreaResponsable().getId()==null)throw malo("El área responsable es obligatoria.");
  Area area=areas.findById(d.getAreaResponsable().getId()).orElseThrow(()->malo("El área responsable no existe."));
  if(!"INACTIVO".equals(area.getEstado())&&!area.getId().equals(areaActualId))throw malo("El área responsable seleccionada está inactiva.");
  // El código repetido se comprueba antes de modificar la entidad, para no forzar un flush con datos inválidos.
  boolean repetido=t.getId()==null?tramites.existsByCodigoIgnoreCase(codigo):tramites.existsByCodigoIgnoreCaseAndIdNot(codigo,t.getId());
  if(repetido)throw new ResponseStatusException(HttpStatus.CONFLICT,"Ya existe un trámite con ese código.");
  t.setCodigo(codigo); t.setTipoTramite(estrategia.nombre()); t.setSolicitante(solicitante); t.setDniRuc(dniRuc); t.setAsunto(asunto); t.setDescripcion(descripcion);
  t.setFechaRegistro(d.getFechaRegistro()); t.setEstado(d.getEstado()); t.setAreaResponsable(area);
  estrategia.validar(t);
 }
 private static String limpio(String v){return v==null?"":v.strip();}
 private static ResponseStatusException malo(String m){return new ResponseStatusException(HttpStatus.BAD_REQUEST,m);}

 // ---------- Flujo existente: derivación, estado e historial ----------
 @Transactional public Tramite derivar(Long id,DerivacionRequest r){Tramite t=obtener(id); Area destino=areas.findById(r.areaDestinoId()).orElseThrow(()->new NoSuchElementException("No se encontró el área destino.")); t.setAreaResponsable(destino); t.setEstado(EstadoTramite.DERIVADO); Derivacion d=new Derivacion();d.setTramite(t);d.setAreaDestino(destino);d.setResponsable(r.responsable());d.setObservacion(r.observacion());d.setFecha(LocalDateTime.now());derivaciones.save(d); registrarHistorial(t,EstadoTramite.DERIVADO,r.responsable(),r.observacion(),destino); notificaciones.notificarDerivacion(t);return tramites.save(t);}
 @Transactional public Tramite actualizarEstado(Long id,EstadoTramite estado,String observacion){Tramite t=obtener(id);t.setEstado(estado);registrarHistorial(t,estado,"Funcionario",observacion,t.getAreaResponsable());return tramites.save(t);}
 public List<HistorialTramite> historial(Long id){obtener(id);return historiales.findByTramiteIdOrderByFechaAsc(id);} private void registrarHistorial(Tramite t,EstadoTramite e,String responsable,String observacion,Area a){HistorialTramite h=new HistorialTramite();h.setTramite(t);h.setEstado(e);h.setFecha(LocalDateTime.now());h.setResponsable(responsable);h.setObservacion(observacion);h.setArea(a);historiales.save(h);}
}
