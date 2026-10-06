import { Component, EventEmitter, OnInit, Output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Area } from '../models/modelos';
import { AreaService } from '../services/area.service';

@Component({
 selector: 'app-areas', standalone: true, imports: [CommonModule, FormsModule],
 templateUrl: './areas.component.html'
})
export class AreasComponent implements OnInit {
 @Output() actualizado = new EventEmitter<void>();
 areas: Area[] = [];
 busqueda = ''; mensaje = ''; error = ''; cargando = false; guardando = false;
 vista: 'lista' | 'formulario' | 'detalle' = 'lista';
 formulario: Area = this.vacia(); detalle?: Area; pendienteEliminar?: Area;
 constructor(private servicio: AreaService) {}
 ngOnInit() { this.listar(); }
 listar() {
  this.cargando = true; this.error = '';
  this.servicio.listar(this.busqueda.trim()).subscribe({
   next: areas => { this.areas = areas; this.cargando = false; },
   error: e => { this.cargando = false; this.fallo(e); }
  });
 }
 limpiar() { this.busqueda = ''; this.listar(); }
 registrar() { this.formulario = this.vacia(); this.vista = 'formulario'; this.error = ''; this.mensaje = ''; this.pendienteEliminar = undefined; }
 ver(area: Area, editar = false) {
  this.error = ''; this.mensaje = ''; this.pendienteEliminar = undefined;
  this.servicio.obtener(area.id!).subscribe({ next: datos => {
   if (editar) { this.formulario = { ...datos }; this.vista = 'formulario'; }
   else { this.detalle = datos; this.vista = 'detalle'; }
  }, error: e => this.fallo(e) });
 }
 guardar() {
  if (this.guardando) return;
  this.error = ''; this.formulario.nombre = this.formulario.nombre.trim();
  this.formulario.descripcion = (this.formulario.descripcion || '').trim();
  if (!this.formulario.nombre || this.formulario.nombre.length > 120 || this.formulario.descripcion.length > 255) {
   this.error = 'Ingrese un nombre de hasta 120 caracteres y una descripción de hasta 255 caracteres.'; return;
  }
  const editando = this.formulario.id !== undefined;
  const operacion = editando ? this.servicio.editar(this.formulario.id!, this.formulario) : this.servicio.crear(this.formulario);
  this.guardando = true;
  operacion.subscribe({ next: () => {
   this.guardando = false; this.vista = 'lista'; this.busqueda = '';
   this.mensaje = editando ? 'Área actualizada correctamente.' : 'Área registrada correctamente.';
   this.listar(); this.actualizado.emit();
  }, error: e => { this.guardando = false; this.fallo(e); } });
 }
 eliminar() {
  if (!this.pendienteEliminar || this.guardando) return;
  this.guardando = true; this.error = ''; this.mensaje = '';
  this.servicio.eliminar(this.pendienteEliminar.id!).subscribe({ next: () => {
   this.guardando = false; this.pendienteEliminar = undefined;
   this.mensaje = 'Área eliminada correctamente.'; this.listar(); this.actualizado.emit();
  }, error: e => { this.guardando = false; this.pendienteEliminar = undefined; this.fallo(e); } });
 }
 volver() { this.vista = 'lista'; this.error = ''; this.listar(); }
 private vacia(): Area { return { nombre: '', descripcion: '', estado: 'ACTIVO' }; }
 private fallo(e: any) { this.error = e?.error?.mensaje || 'No se pudo completar la operación. Verifique que el backend esté iniciado.'; }
}
