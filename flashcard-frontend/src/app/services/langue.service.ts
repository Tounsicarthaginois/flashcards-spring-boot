// Les langues. Tout le monde peut les lire, mais seul un gestionnaire
// peut en créer, modifier ou supprimer. Cette restriction est appliquée
// par le back end, pas ici : un appel non autorisé reviendrait en 403.

import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { LangueDto } from '../dto/langue.dto';
import { environment } from '../../environments/environment';

@Injectable({ providedIn: 'root' })
export class LangueService {

  private apiUrl = `${environment.apiUrl}/langues`;

  constructor(private http: HttpClient) {}

  // GET /api/langues → liste toutes les langues disponibles
  getAll(): Observable<LangueDto[]> {
    return this.http.get<LangueDto[]>(this.apiUrl);
  }

  // POST /api/langues → crée une nouvelle langue (GESTIONNAIRE uniquement)
  create(langue: Partial<LangueDto>): Observable<LangueDto> {
    return this.http.post<LangueDto>(this.apiUrl, langue);
  }

  // PUT /api/langues/{id} → modifie une langue (GESTIONNAIRE uniquement)
  update(id: number, langue: Partial<LangueDto>): Observable<LangueDto> {
    return this.http.put<LangueDto>(`${this.apiUrl}/${id}`, langue);
  }

  // DELETE /api/langues/{id} → supprime une langue (GESTIONNAIRE uniquement)
  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}
