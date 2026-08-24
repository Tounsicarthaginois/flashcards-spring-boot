// ============================================
// UTILISATEUR SERVICE - Gestion des utilisateurs
// Liste et suppression uniquement
// (le rôle ne peut pas être changé via l'API)
// ============================================

import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { UtilisateurDto } from '../dto/utilisateur.dto';
import { environment } from '../../environments/environment';

@Injectable({ providedIn: 'root' })
export class UtilisateurService {

  private apiUrl = `${environment.apiUrl}/utilisateurs`;

  constructor(private http: HttpClient) {}

  // GET /api/utilisateurs → liste tous les utilisateurs (sans password)
  getAll(): Observable<UtilisateurDto[]> {
    return this.http.get<UtilisateurDto[]>(this.apiUrl);
  }

  // DELETE /api/utilisateurs/{id} → supprime un utilisateur
  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}
