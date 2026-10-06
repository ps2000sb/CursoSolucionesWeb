package pe.edu.solucionesweb.tramite.model;
import jakarta.persistence.*;
@Entity @Table(name="areas") public class Area {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 @Column(nullable=false,unique=true,length=120) private String nombre;
 @Column(length=255) private String descripcion;
 @Column(nullable=false,length=10) private String estado="ACTIVO";
 public String getEstado(){return estado;} public void setEstado(String v){estado=v;}
 public Long getId(){return id;} public String getNombre(){return nombre;} public void setNombre(String v){nombre=v;} public String getDescripcion(){return descripcion;} public void setDescripcion(String v){descripcion=v;}
}
