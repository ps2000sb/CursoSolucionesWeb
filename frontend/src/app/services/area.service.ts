import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Area } from '../models/modelos';

@Injectable({ providedIn: 'root' })
export class AreaService {
 private api = 'http://localhost:8080/api/areas';
 constructor(private http: HttpClient) {}
 listar(q = '') { return this.http.get<Area[]>(this.api, { params: { q } }); }
 obtener(id: number) { return this.http.get<Area>(`${this.api}/${id}`); }
 crear(area: Area) { return this.http.post<Area>(this.api, area); }
 editar(id: number, area: Area) { return this.http.put<Area>(`${this.api}/${id}`, area); }
 eliminar(id: number) { return this.http.delete<void>(`${this.api}/${id}`); }
}
