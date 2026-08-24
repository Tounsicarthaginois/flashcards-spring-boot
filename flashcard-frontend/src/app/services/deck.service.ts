// Toutes les requêtes concernant les decks. Aucune ligne sur le token
// ici : l'interceptor s'en charge avant l'envoi.

import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { DeckDto } from '../dto/deck.dto';
import { environment } from '../../environments/environment';

@Injectable({ providedIn: 'root' })
export class DeckService {

  private apiUrl = `${environment.apiUrl}/decks`;

  constructor(private http: HttpClient) {}

  // Pas d'identifiant à passer : le back end reconnaît l'utilisateur
  // à partir du token et ne renvoie que ses decks.
  getAll(): Observable<DeckDto[]> {
    return this.http.get<DeckDto[]>(this.apiUrl);
  }

  // GET /api/decks/{id} → un deck par son id
  getById(id: number): Observable<DeckDto> {
    return this.http.get<DeckDto>(`${this.apiUrl}/${id}`);
  }

  // POST /api/decks → crée un nouveau deck
  create(deck: Partial<DeckDto>): Observable<DeckDto> {
    return this.http.post<DeckDto>(this.apiUrl, deck);
  }

  // PUT /api/decks/{id} → modifie un deck
  update(id: number, deck: Partial<DeckDto>): Observable<DeckDto> {
    return this.http.put<DeckDto>(`${this.apiUrl}/${id}`, deck);
  }

  // DELETE /api/decks/{id} → supprime un deck
  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }

  // GET /api/decks/search?name=xxx → recherche par nom
  search(name: string): Observable<DeckDto[]> {
    return this.http.get<DeckDto[]>(`${this.apiUrl}/search?name=${name}`);
  }

  // GET /api/decks/publiques → decks en attente de validation (GESTIONNAIRE)
  getPubliques(): Observable<DeckDto[]> {
    return this.http.get<DeckDto[]>(`${this.apiUrl}/publiques`);
  }

  // PUT /api/decks/{id}/valider → valide un deck PUBLIQUE → OFFICIELLE (GESTIONNAIRE)
  valider(id: number): Observable<DeckDto> {
    return this.http.put<DeckDto>(`${this.apiUrl}/${id}/valider`, {});
  }

  // GET /api/decks/officiels → tous les decks officiels (visible par tous)
  getOfficiels(): Observable<DeckDto[]> {
    return this.http.get<DeckDto[]>(`${this.apiUrl}/officiels`);
  }
}
