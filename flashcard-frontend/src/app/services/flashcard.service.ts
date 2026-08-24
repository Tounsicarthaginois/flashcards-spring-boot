// Une flashcard, c'est un mot accompagné de ses traductions et de
// phrases d'exemple. L'ensemble part et revient en une seule requête.

import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { FlashcardDto } from '../dto/flashcard.dto';
import { environment } from '../../environments/environment';

@Injectable({ providedIn: 'root' })
export class FlashcardService {

  private apiUrl = `${environment.apiUrl}/flashcards`;

  constructor(private http: HttpClient) {}

  // GET /api/flashcards/deck/{deckId} → toutes les flashcards d'un deck
  getByDeck(deckId: number): Observable<FlashcardDto[]> {
    return this.http.get<FlashcardDto[]>(`${this.apiUrl}/deck/${deckId}`);
  }

  // GET /api/flashcards/{id} → une flashcard complète avec traductions
  getById(id: number): Observable<FlashcardDto> {
    return this.http.get<FlashcardDto>(`${this.apiUrl}/${id}`);
  }

  // POST /api/flashcards → crée une flashcard avec ses traductions et exemples
  create(flashcard: FlashcardDto): Observable<FlashcardDto> {
    return this.http.post<FlashcardDto>(this.apiUrl, flashcard);
  }

  // DELETE /api/flashcards/{id} → supprime une flashcard
  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}
