// Le suivi des révisions. Le front envoie un simple "réussi ou raté" ;
// c'est le back end qui applique l'algorithme de répétition espacée et
// renvoie la date de la prochaine révision.

import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ProgressionDto, RevisionRequest } from '../dto/progression.dto';
import { FlashcardDto } from '../dto/flashcard.dto';
import { environment } from '../../environments/environment';

@Injectable({ providedIn: 'root' })
export class ProgressionService {

  private apiUrl = `${environment.apiUrl}/progressions`;

  constructor(private http: HttpClient) {}

  // 1 jour en cas d'échec, sinon 1 puis 7 puis 30 jours selon le niveau.
  enregistrerRevision(request: RevisionRequest): Observable<ProgressionDto> {
    return this.http.post<ProgressionDto>(`${this.apiUrl}/revision`, request);
  }

  // GET /api/progressions/a-reviser/{deckId}
  // Cartes à réviser aujourd'hui : nouvelles + dont la date est passée
  getCardsToReviewByDeck(deckId: number): Observable<FlashcardDto[]> {
    return this.http.get<FlashcardDto[]>(`${this.apiUrl}/a-reviser/${deckId}`);
  }

  // GET /api/progressions/deck/{deckId}
  // Progressions existantes pour ce deck (pour afficher les badges SRS)
  // Les cartes absentes de la liste = jamais révisées = "Nouvelle"
  getProgressionsByDeck(deckId: number): Observable<ProgressionDto[]> {
    return this.http.get<ProgressionDto[]>(`${this.apiUrl}/deck/${deckId}`);
  }

  // GET /api/progressions/a-reviser → tous decks confondus
  getCardsToReview(): Observable<ProgressionDto[]> {
    return this.http.get<ProgressionDto[]>(`${this.apiUrl}/a-reviser`);
  }

  // GET /api/progressions/mes-progressions → toutes mes stats
  getMesProgressions(): Observable<ProgressionDto[]> {
    return this.http.get<ProgressionDto[]>(`${this.apiUrl}/mes-progressions`);
  }
}
