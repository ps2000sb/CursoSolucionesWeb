package pe.edu.solucionesweb.tramite.model;
import jakarta.persistence.*;
@Entity @Table(name="areas") public class Area {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 @Column(nullable=false,unique=true) private String nombre;
 private String descripcion;
 public Long getId(){return id;} public String getNombre(){return nombre;} public void setNombre(String v){nombre=v;} public String getDescripcion(){return descripcion;} public void setDescripcion(String v){descripcion=v;}
}
