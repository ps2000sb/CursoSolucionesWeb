package pe.edu.solucionesweb.tramite.model;
import jakarta.persistence.*; import java.time.LocalDate;
@Entity @Table(name="documentos") public class Documento {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 @Column(nullable=false,unique=true) private String codigo; @Column(nullable=false) private String tipoDocumento;
 @Column(nullable=false) private String remitente; @Column(nullable=false,length=15) private String dniRuc; @Column(nullable=false) private String asunto;
 private LocalDate fechaRecepcion; @Column(length=1000) private String descripcion; private String estado="PENDIENTE";
 @ManyToOne @JoinColumn(name="area_destino_id") private Area areaDestino;
 public Long getId(){return id;} public void setId(Long v){id=v;} public String getCodigo(){return codigo;} public void setCodigo(String v){codigo=v;} public String getTipoDocumento(){return tipoDocumento;} public void setTipoDocumento(String v){tipoDocumento=v;} public String getRemitente(){return remitente;} public void setRemitente(String v){remitente=v;} public String getDniRuc(){return dniRuc;} public void setDniRuc(String v){dniRuc=v;} public String getAsunto(){return asunto;} public void setAsunto(String v){asunto=v;} public LocalDate getFechaRecepcion(){return fechaRecepcion;} public void setFechaRecepcion(LocalDate v){fechaRecepcion=v;} public String getDescripcion(){return descripcion;} public void setDescripcion(String v){descripcion=v;} public String getEstado(){return estado;} public void setEstado(String v){estado=v;} public Area getAreaDestino(){return areaDestino;} public void setAreaDestino(Area v){areaDestino=v;}
}
