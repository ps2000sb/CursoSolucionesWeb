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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(properties={
 "spring.datasource.url=jdbc:h2:mem:areas;MODE=MySQL;DB_CLOSE_DELAY=-1",
 "spring.datasource.driver-class-name=org.h2.Driver",
 "spring.datasource.username=sa", "spring.datasource.password=",
 "spring.jpa.hibernate.ddl-auto=create-drop"
})
@AutoConfigureMockMvc
class AreaIntegrationTest {
 @Autowired MockMvc mvc;
 @Autowired ObjectMapper json;
 @Autowired JdbcTemplate jdbc;

 @BeforeEach void limpiar(){
  for(String tabla:new String[]{"derivaciones","historial_tramites","documentos","tramites","usuarios","areas"})jdbc.update("DELETE FROM "+tabla);
 }
 private long crear(String nombre)throws Exception{
  String respuesta=mvc.perform(post("/api/areas").contentType(MediaType.APPLICATION_JSON)
   .content(json.writeValueAsString(java.util.Map.of("nombre",nombre,"descripcion","Oficina municipal","estado","ACTIVO"))))
   .andExpect(status().isCreated()).andReturn().getResponse().getContentAsString();
  return json.readTree(respuesta).get("id").asLong();
 }
 @Test void cicloCompleto()throws Exception{
  long id=crear("Archivo Municipal");
  mvc.perform(get("/api/areas")).andExpect(status().isOk()).andExpect(jsonPath("$[0].id").value(id));
  mvc.perform(get("/api/areas").param("q","ARCHIVO")).andExpect(jsonPath("$.length()").value(1));
  mvc.perform(get("/api/areas").param("q",Long.toString(id))).andExpect(jsonPath("$.length()").value(1));
  mvc.perform(get("/api/areas").param("q","inexistente")).andExpect(jsonPath("$.length()").value(0));
  mvc.perform(get("/api/areas/"+id)).andExpect(status().isOk()).andExpect(jsonPath("$.nombre").value("Archivo Municipal"));
  mvc.perform(put("/api/areas/"+id).contentType(MediaType.APPLICATION_JSON)
   .content("{\"nombre\":\" Archivo Central \",\"descripcion\":\"Actualizada\",\"estado\":\"INACTIVO\"}"))
   .andExpect(status().isOk()).andExpect(jsonPath("$.id").value(id)).andExpect(jsonPath("$.nombre").value("Archivo Central"));
  mvc.perform(get("/api/areas/"+id)).andExpect(jsonPath("$.estado").value("INACTIVO"));
  mvc.perform(get("/api/areas").param("q","activo")).andExpect(jsonPath("$.length()").value(0));
  mvc.perform(get("/api/areas").param("q","inactivo")).andExpect(jsonPath("$.length()").value(1));
  mvc.perform(delete("/api/areas/"+id)).andExpect(status().isNoContent());
  mvc.perform(get("/api/areas/"+id)).andExpect(status().isNotFound());
 }
 @Test void validaDuplicadosEnRegistroYEdicion()throws Exception{
  crear("Archivo"); long otro=crear("Tesorería");
  String datos="{\"nombre\":\" archivo \",\"estado\":\"ACTIVO\"}";
  mvc.perform(post("/api/areas").contentType(MediaType.APPLICATION_JSON).content(datos)).andExpect(status().isConflict());
  mvc.perform(put("/api/areas/"+otro).contentType(MediaType.APPLICATION_JSON).content(datos)).andExpect(status().isConflict());
 }
 @Test void rechazaCamposInvalidos()throws Exception{
  for(String datos:new String[]{"{\"nombre\":\"   \"}","{\"nombre\":\"Área\",\"estado\":\"OTRO\"}",
   json.writeValueAsString(java.util.Map.of("nombre","x".repeat(121))),
   json.writeValueAsString(java.util.Map.of("nombre","Área","descripcion","x".repeat(256)))}){
   mvc.perform(post("/api/areas").contentType(MediaType.APPLICATION_JSON).content(datos)).andExpect(status().isBadRequest()).andExpect(jsonPath("$.mensaje").exists());
  }
 }
 @Test void editarYEliminarInexistente()throws Exception{
  mvc.perform(put("/api/areas/999999").contentType(MediaType.APPLICATION_JSON).content("{\"nombre\":\"Área\"}")).andExpect(status().isNotFound());
  mvc.perform(delete("/api/areas/999999")).andExpect(status().isNotFound());
 }
 @Test void noEliminaAreaVinculada()throws Exception{
  long id=crear("Mesa de Partes");
  jdbc.update("INSERT INTO usuarios(nombres,correo,activo,area_id) VALUES (?,?,TRUE,?)","Funcionario","prueba@municipalidad.pe",id);
  mvc.perform(delete("/api/areas/"+id)).andExpect(status().isConflict()).andExpect(jsonPath("$.mensaje").exists());
  mvc.perform(get("/api/areas/"+id)).andExpect(status().isOk());
 }
}
