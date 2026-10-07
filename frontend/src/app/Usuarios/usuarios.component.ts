import { Component, OnInit } from '@angular/core';
import { HttpClient } from '@angular/common/http';

@Component({
  selector: 'app-usuarios',
  templateUrl: './usuarios.component.html',
  styleUrls: ['./usuarios.component.css']
})
export class UsuariosComponent implements OnInit {
  usuarios: any[] = [];
  filtro: string = '';
  isEditando: boolean = false;
  
  usuarioSeleccionado: any = {
    idUsuario: null,
    nombre: '',
    apellidos: '',
    dni: '',
    correo: '',
    contrasena: '',
    idRol: 3,
    estado: true
  };

  private apiUrl = 'http://localhost:8080/api/usuarios';

  constructor(private http: HttpClient) {}

  ngOnInit(): void {
    this.listarUsuarios();
  }

  listarUsuarios(): void {
    let url = this.apiUrl;
    if (this.filtro && this.filtro.trim() !== '') {
      url += `?filtro=${this.filtro}`;
    }
    this.http.get<any[]>(url).subscribe(
      (data) => { this.usuarios = data; },
      (error) => { console.error('Error al listar', error); }
    );
  }

  guardarUsuario(): void {
    if (this.isEditando) {
      this.http.put(`${this.apiUrl}/${this.usuarioSeleccionado.idUsuario}`, this.usuarioSeleccionado).subscribe(
        () => {
          alert('Usuario actualizado con éxito');
          this.resetFormulario();
          this.listarUsuarios();
        },
        (error) => { console.error('Error al actualizar', error); }
      );
    } else {
      this.http.post(this.apiUrl, this.usuarioSeleccionado).subscribe(
        () => {
          alert('Usuario registrado con éxito');
          this.resetFormulario();
          this.listarUsuarios();
        },
        (error) => { console.error('Error al registrar', error); }
      );
    }
  }

  cargarParaEditar(usuario: any): void {
    this.usuarioSeleccionado = { ...usuario };
    this.isEditando = true;
  }

  eliminarUsuario(id: number): void {
    if (confirm('¿Deseas dar de baja a este usuario?')) {
      this.http.delete(`${this.apiUrl}/${id}`).subscribe(
        () => {
          alert('Usuario inactivado correctamente');
          this.listarUsuarios();
        },
        (error) => { console.error('Error al eliminar', error); }
      );
    }
  }

  resetFormulario(): void {
    this.usuarioSeleccionado = {
      idUsuario: null,
      nombre: '',
      apellidos: '',
      dni: '',
      correo: '',
      contrasena: '',
      idRol: 3,
      estado: true
    };
    this.isEditando = false;
  }

  obtenerNombreRol(idRol: number): string {
    if (idRol === 1) return 'Administrador';
    if (idRol === 2) return 'Funcionario';
    return 'Ciudadano';
  }
}