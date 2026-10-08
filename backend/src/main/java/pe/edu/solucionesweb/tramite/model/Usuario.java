package pe.edu.solucionesweb.tramite.model;
import jakarta.persistence.*;
@Entity @Table(name="usuarios") public class Usuario {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 @Column(nullable=false) private String nombres; @Column(nullable=false,unique=true) private String correo;
 private String clave; private boolean activo=true;
 @ManyToOne @JoinColumn(name="rol_id") private Rol rol;
 @ManyToOne @JoinColumn(name="area_id") private Area area;
 public Long getId(){return id;} public String getNombres(){return nombres;} public void setNombres(String v){nombres=v;} public String getCorreo(){return correo;} public void setCorreo(String v){correo=v;} public String getClave(){return clave;} public void setClave(String v){clave=v;} public boolean isActivo(){return activo;} public void setActivo(boolean v){activo=v;} public Rol getRol(){return rol;} public void setRol(Rol v){rol=v;} public Area getArea(){return area;} public void setArea(Area v){area=v;}
}
