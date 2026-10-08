package pe.edu.solucionesweb.tramite;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.LinkedHashMap;
import java.util.Map;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(properties={
 "spring.datasource.url=jdbc:h2:mem:tramites;MODE=MySQL;DB_CLOSE_DELAY=-1",
 "spring.datasource.driver-class-name=org.h2.Driver",
 "spring.datasource.username=sa", "spring.datasource.password=",
 "spring.jpa.hibernate.ddl-auto=create-drop"
})
@AutoConfigureMockMvc
class TramiteIntegrationTest {
 @Autowired MockMvc mvc;
 @Autowired ObjectMapper json;
 @Autowired JdbcTemplate jdbc;
 long areaId;

 @BeforeEach void preparar(){
  for(String tabla:new String[]{"derivaciones","historial_tramites","documentos","tramites","usuarios","areas"})jdbc.update("DELETE FROM "+tabla);
  jdbc.update("INSERT INTO areas(nombre,descripcion,estado) VALUES ('Mesa de Partes','Recepción','ACTIVO')");
  areaId=jdbc.queryForObject("SELECT id FROM areas WHERE nombre='Mesa de Partes'",Long.class);
 }
 private Map<String,Object> datos(String codigo){
  Map<String,Object> m=new LinkedHashMap<>();
  m.put("codigo",codigo); m.put("tipoTramite","Solicitud"); m.put("solicitante","María Pérez"); m.put("dniRuc","45678901");
  m.put("asunto","Solicitud de información"); m.put("descripcion","Detalle del trámite"); m.put("fechaRegistro","2026-10-01");
  m.put("estado","PENDIENTE"); m.put("areaResponsable",Map.of("id",areaId));
  return m;
 }
 private String cuerpo(Map<String,Object> m)throws Exception{return json.writeValueAsString(m);}
 private long crear(String codigo)throws Exception{
  String r=mvc.perform(post("/api/tramites").contentType(MediaType.APPLICATION_JSON).content(cuerpo(datos(codigo))))
   .andExpect(status().isCreated()).andReturn().getResponse().getContentAsString();
  return json.readTree(r).get("id").asLong();
 }
 @Test void cicloCompleto()throws Exception{
  long id=crear("TRM-100");
  mvc.perform(get("/api/tramites")).andExpect(status().isOk()).andExpect(jsonPath("$[0].id").value(id));
  for(String q:new String[]{"trm-100","pérez","45678","información","mesa de","pendiente",Long.toString(id)})
   mvc.perform(get("/api/tramites").param("q",q)).andExpect(jsonPath("$.length()").value(1));
  mvc.perform(get("/api/tramites").param("q","inexistente")).andExpect(jsonPath("$.length()").value(0));
  mvc.perform(get("/api/tramites/"+id)).andExpect(status().isOk()).andExpect(jsonPath("$.areaResponsable.nombre").value("Mesa de Partes"));
  Map<String,Object> cambio=datos("  TRM-100B ");
  cambio.put("tipoTramite","permiso"); cambio.put("estado","EN_PROCESO"); cambio.put("asunto","Permiso de evento");
  mvc.perform(put("/api/tramites/"+id).contentType(MediaType.APPLICATION_JSON).content(cuerpo(cambio)))
   .andExpect(status().isOk()).andExpect(jsonPath("$.id").value(id)).andExpect(jsonPath("$.codigo").value("TRM-100B"))
   .andExpect(jsonPath("$.tipoTramite").value("Permiso"));
  mvc.perform(get("/api/tramites/"+id)).andExpect(jsonPath("$.estado").value("EN_PROCESO")).andExpect(jsonPath("$.asunto").value("Permiso de evento"));
  mvc.perform(get("/api/tramites/"+id+"/historial")).andExpect(jsonPath("$.length()").value(2));
  mvc.perform(delete("/api/tramites/"+id)).andExpect(status().isNoContent());
  mvc.perform(get("/api/tramites/"+id)).andExpect(status().isNotFound());
  mvc.perform(delete("/api/tramites/"+id)).andExpect(status().isNotFound());
 }
 @Test void eliminaTramiteConHistorialYDerivaciones()throws Exception{
  long id=crear("TRM-200");
  jdbc.update("INSERT INTO areas(nombre,estado) VALUES ('Archivo','ACTIVO')");
  long destino=jdbc.queryForObject("SELECT id FROM areas WHERE nombre='Archivo'",Long.class);
  mvc.perform(post("/api/tramites/"+id+"/derivaciones").contentType(MediaType.APPLICATION_JSON)
   .content("{\"areaDestinoId\":"+destino+",\"responsable\":\"Ana\",\"observacion\":\"Derivar\"}")).andExpect(status().isOk());
  mvc.perform(delete("/api/tramites/"+id)).andExpect(status().isNoContent());
  mvc.perform(get("/api/tramites")).andExpect(jsonPath("$.length()").value(0));
 }
 @Test void validaCodigoDuplicadoEnRegistroYEdicion()throws Exception{
  crear("TRM-1"); long otro=crear("TRM-2");
  mvc.perform(post("/api/tramites").contentType(MediaType.APPLICATION_JSON).content(cuerpo(datos(" trm-1 ")))).andExpect(status().isConflict());
  mvc.perform(put("/api/tramites/"+otro).contentType(MediaType.APPLICATION_JSON).content(cuerpo(datos("TRM-1")))).andExpect(status().isConflict());
  mvc.perform(put("/api/tramites/"+otro).contentType(MediaType.APPLICATION_JSON).content(cuerpo(datos("TRM-2")))).andExpect(status().isOk());
 }
 @Test void rechazaCamposInvalidos()throws Exception{
  Map<String,Object> m;
  m=datos(" "); mvc.perform(post("/api/tramites").contentType(MediaType.APPLICATION_JSON).content(cuerpo(m))).andExpect(status().isBadRequest());
  m=datos("A1"); m.put("tipoTramite","Otro"); mvc.perform(post("/api/tramites").contentType(MediaType.APPLICATION_JSON).content(cuerpo(m))).andExpect(status().isBadRequest());
  m=datos("A2"); m.put("dniRuc","123"); mvc.perform(post("/api/tramites").contentType(MediaType.APPLICATION_JSON).content(cuerpo(m))).andExpect(status().isBadRequest());
  m=datos("A3"); m.put("dniRuc","4567890A"); mvc.perform(post("/api/tramites").contentType(MediaType.APPLICATION_JSON).content(cuerpo(m))).andExpect(status().isBadRequest());
  m=datos("A4"); m.put("asunto","x".repeat(256)); mvc.perform(post("/api/tramites").contentType(MediaType.APPLICATION_JSON).content(cuerpo(m))).andExpect(status().isBadRequest());
  m=datos("A5"); m.put("estado","OTRO"); mvc.perform(post("/api/tramites").contentType(MediaType.APPLICATION_JSON).content(cuerpo(m))).andExpect(status().isBadRequest());
  m=datos("A6"); m.remove("areaResponsable"); mvc.perform(post("/api/tramites").contentType(MediaType.APPLICATION_JSON).content(cuerpo(m))).andExpect(status().isBadRequest());
  m=datos("A7"); m.put("areaResponsable",Map.of("id",999999)); mvc.perform(post("/api/tramites").contentType(MediaType.APPLICATION_JSON).content(cuerpo(m))).andExpect(status().isBadRequest());
  m=datos("A8"); m.put("tipoTramite","Queja"); m.put("descripcion",""); mvc.perform(post("/api/tramites").contentType(MediaType.APPLICATION_JSON).content(cuerpo(m))).andExpect(status().isBadRequest());
  mvc.perform(get("/api/tramites")).andExpect(jsonPath("$.length()").value(0));
 }
 @Test void rechazaAreaInactivaPeroConservaLaActualAlEditar()throws Exception{
  long id=crear("TRM-9");
  jdbc.update("UPDATE areas SET estado='INACTIVO' WHERE id=?",areaId);
  mvc.perform(put("/api/tramites/"+id).contentType(MediaType.APPLICATION_JSON).content(cuerpo(datos("TRM-9")))).andExpect(status().isOk());
  mvc.perform(post("/api/tramites").contentType(MediaType.APPLICATION_JSON).content(cuerpo(datos("TRM-10")))).andExpect(status().isBadRequest());
  mvc.perform(put("/api/tramites/999999").contentType(MediaType.APPLICATION_JSON).content(cuerpo(datos("X")))).andExpect(status().isNotFound());
 }
}
