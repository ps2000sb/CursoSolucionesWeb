package pe.edu.solucionesweb.tramite.model;
import jakarta.persistence.*; import java.time.LocalDate;
@Entity @Table(name="tramites") public class Tramite {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id; @Column(nullable=false,unique=true) private String codigo;
 @Column(nullable=false) private String tipoTramite; @Column(nullable=false) private String solicitante; @Column(nullable=false,length=15) private String dniRuc; @Column(nullable=false) private String asunto; @Column(length=1000) private String descripcion; private LocalDate fechaRegistro;
 @Enumerated(EnumType.STRING) private EstadoTramite estado=EstadoTramite.PENDIENTE;
 @ManyToOne @JoinColumn(name="area_responsable_id") private Area areaResponsable;
 public Long getId(){return id;} public void setId(Long v){id=v;} public String getCodigo(){return codigo;} public void setCodigo(String v){codigo=v;} public String getTipoTramite(){return tipoTramite;} public void setTipoTramite(String v){tipoTramite=v;} public String getSolicitante(){return solicitante;} public void setSolicitante(String v){solicitante=v;} public String getDniRuc(){return dniRuc;} public void setDniRuc(String v){dniRuc=v;} public String getAsunto(){return asunto;} public void setAsunto(String v){asunto=v;} public String getDescripcion(){return descripcion;} public void setDescripcion(String v){descripcion=v;} public LocalDate getFechaRegistro(){return fechaRegistro;} public void setFechaRegistro(LocalDate v){fechaRegistro=v;} public EstadoTramite getEstado(){return estado;} public void setEstado(EstadoTramite v){estado=v;} public Area getAreaResponsable(){return areaResponsable;} public void setAreaResponsable(Area v){areaResponsable=v;}
}
