import { Component, EventEmitter, OnInit, Output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Area, Tramite } from '../models/modelos';
import { AreaService } from '../services/area.service';
import { TramiteService } from '../services/tramite.service';

@Component({
 selector: 'app-tramites', standalone: true, imports: [CommonModule, FormsModule],
 templateUrl: './tramites.component.html'
})
export class TramitesComponent implements OnInit {
 /** Avisa al contenedor para abrir el seguimiento (derivación e historial) de un trámite. */
 @Output() seguimiento = new EventEmitter<Tramite>();
 @Output() actualizado = new EventEmitter<void>();
 tramites: Tramite[] = []; areas: Area[] = [];
 readonly tipos = ['Solicitud', 'Queja', 'Permiso'];
 readonly estados = ['PENDIENTE', 'EN_PROCESO', 'DERIVADO', 'ATENDIDO', 'CONCLUIDO'];
 busqueda = ''; mensaje = ''; error = ''; cargando = false; guardando = false;
 vista: 'lista' | 'formulario' | 'detalle' = 'lista';
 formulario: Tramite = this.vacio(); areaId?: number; detalle?: Tramite; pendienteEliminar?: Tramite;
 constructor(private servicio: TramiteService, private areaServicio: AreaService) {}
 ngOnInit() { this.listar(); this.areaServicio.listar().subscribe({ next: a => this.areas = a, error: e => this.fallo(e) }); }
 /** Áreas activas; al editar se conserva también el área actual aunque esté inactiva. */
 get areasDisponibles() { return this.areas.filter(a => a.estado === 'ACTIVO' || a.id === this.areaId); }
 listar() {
  this.cargando = true; this.error = '';
  this.servicio.listar(this.busqueda.trim()).subscribe({
   next: t => { this.tramites = t; this.cargando = false; },
   error: e => { this.cargando = false; this.fallo(e); }
  });
 }
 limpiar() { this.busqueda = ''; this.listar(); }
 registrar() { this.formulario = this.vacio(); this.areaId = undefined; this.vista = 'formulario'; this.reiniciarMensajes(); }
 ver(t: Tramite, editar = false) {
  this.reiniciarMensajes();
  this.servicio.obtener(t.id!).subscribe({ next: datos => {
   if (editar) { this.formulario = { ...datos }; this.areaId = datos.areaResponsable?.id; this.vista = 'formulario'; }
   else { this.detalle = datos; this.vista = 'detalle'; }
  }, error: e => this.fallo(e) });
 }
 guardar() {
  if (this.guardando) return;
  this.error = '';
  const f = this.formulario;
  f.codigo = f.codigo.trim(); f.solicitante = f.solicitante.trim(); f.dniRuc = f.dniRuc.trim();
  f.asunto = f.asunto.trim(); f.descripcion = (f.descripcion || '').trim();
  if (!f.codigo || f.codigo.length > 30 || !f.solicitante || f.solicitante.length > 150 || !f.asunto || f.asunto.length > 255 || f.descripcion.length > 1000) {
   this.error = 'Complete código (máx. 30), solicitante (máx. 150), asunto (máx. 255) y una descripción de hasta 1000 caracteres.'; return;
  }
  if (!/^(\d{8}|\d{11})$/.test(f.dniRuc)) { this.error = 'El DNI debe tener 8 dígitos o el RUC 11 dígitos (solo números).'; return; }
  if (f.tipoTramite === 'Queja' && !f.descripcion) { this.error = 'La descripción de la queja es obligatoria.'; return; }
  if (!this.areaId) { this.error = 'Seleccione el área responsable.'; return; }
  f.areaResponsable = this.areas.find(a => a.id === this.areaId);
  const editando = f.id !== undefined;
  const operacion = editando ? this.servicio.editar(f.id!, f) : this.servicio.crear(f);
  this.guardando = true;
  operacion.subscribe({ next: () => {
   this.guardando = false; this.vista = 'lista'; this.busqueda = '';
   this.mensaje = editando ? 'Trámite actualizado correctamente.' : 'Trámite registrado correctamente.';
   this.listar(); this.actualizado.emit();
  }, error: e => { this.guardando = false; this.fallo(e); } });
 }
 eliminar() {
  if (!this.pendienteEliminar || this.guardando) return;
  this.guardando = true; this.error = ''; this.mensaje = '';
  this.servicio.eliminar(this.pendienteEliminar.id!).subscribe({ next: () => {
   this.guardando = false; this.pendienteEliminar = undefined;
   this.mensaje = 'Trámite eliminado correctamente.'; this.listar(); this.actualizado.emit();
  }, error: e => { this.guardando = false; this.pendienteEliminar = undefined; this.fallo(e); } });
 }
 volver() { this.vista = 'lista'; this.error = ''; this.listar(); }
 badge(e: string) { return e === 'CONCLUIDO' || e === 'ATENDIDO' ? 'text-bg-success' : e === 'PENDIENTE' || e === 'EN_PROCESO' ? 'text-bg-warning' : 'text-bg-primary'; }
 private reiniciarMensajes() { this.error = ''; this.mensaje = ''; this.pendienteEliminar = undefined; }
 private vacio(): Tramite { return { codigo: '', tipoTramite: 'Solicitud', solicitante: '', dniRuc: '', asunto: '', descripcion: '', fechaRegistro: new Date().toISOString().slice(0, 10), estado: 'PENDIENTE' }; }
 private fallo(e: any) { this.error = e?.error?.mensaje || 'No se pudo completar la operación. Verifique que el backend esté iniciado.'; }
}
