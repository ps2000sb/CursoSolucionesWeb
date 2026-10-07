package pe.edu.solucionesweb.tramite.model;

import jakarta.persistence.*;

@Entity
@Table(name = "usuarios")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idUsuario;

    private String nombre;
    private String apellidos;
    
    @Column(unique = true, length = 8)
    private String dni;
    
    @Column(unique = true)
    private String correo;
    
    private String contrasena;
    
    private Long idRol; // 1: Administrador, 2: Funcionario, 3: Ciudadano
    
    private Boolean estado = true; // true: Activo, false: Inactivo

    // Getters y Setters
    public Long getIdUsuario() { return idUsuario; }
    public void setIdUsuario(Long idUsuario) { this.idUsuario = idUsuario; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getApellidos() { return apellidos; }
    public void setApellidos(String apellidos) { this.apellidos = apellidos; }

    public String getDni() { return dni; }
    public void setDni(String dni) { this.dni = dni; }

    public String getCorreo() { return correo; }
    public void setCorreo(String correo) { this.correo = correo; }

    public String getContrasena() { return contrasena; }
    public void setContrasena(String contrasena) { this.contrasena = contrasena; }

    public Long getIdRol() { return idRol; }
    public void setIdRol(Long idRol) { this.idRol = idRol; }

    public Boolean getEstado() { return estado; }
    public void setEstado(Boolean estado) { this.estado = estado; }
}