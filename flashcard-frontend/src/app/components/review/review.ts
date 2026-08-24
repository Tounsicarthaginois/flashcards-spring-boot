// Session de révision : les cartes du jour défilent une par une.
// Tout l'écran repose sur trois variables — le paquet de cartes,
// l'endroit où on en est, et si la carte est retournée ou non.
// Le calcul des prochaines dates appartient au back end.

import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router } from '@angular/router';
import { ProgressionService } from '../../services/progression.service';
import { FlashcardDto } from '../../dto/flashcard.dto';

@Component({
  selector: 'app-review',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './review.html',
  styleUrl: './review.scss'
})
export class ReviewComponent implements OnInit {

  // Le paquet de cartes à réviser aujourd'hui.
  progressions: FlashcardDto[] = [];

  // Où on en est dans le paquet, comme un doigt qui marque la carte du dessus.
  currentIndex = 0;

  // La carte est-elle retournée ? false = on voit le mot, true = les traductions.
  showAnswer = false;

  sessionFinished = false;
  deckId!: number;

  isLoading = false;
  errorMessage = '';

  constructor(
    private progressionService: ProgressionService,
    private route: ActivatedRoute,
    private router: Router,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.deckId = Number(this.route.snapshot.paramMap.get('deckId'));
    this.loadCardsToReview();
  }

  // On ne demande que les cartes du jour : les nouvelles, et celles dont
  // la date prévue est dépassée. Le tri est fait par le back end.
  loadCardsToReview(): void {
    this.isLoading = true;
    this.progressionService.getCardsToReviewByDeck(this.deckId).subscribe({
      next: (cards) => {
        this.progressions = cards;
        this.isLoading = false;
        // Rien à réviser aujourd'hui : on montre directement l'écran de fin.
        if (cards.length === 0) {
          this.sessionFinished = true;
        }
        this.cdr.detectChanges();
      },
      error: () => {
        this.errorMessage = 'Erreur lors du chargement des cartes.';
        this.isLoading = false;
        this.cdr.detectChanges();
      }
    });
  }

  get currentFlashcard(): FlashcardDto {
    return this.progressions[this.currentIndex];
  }

  get remainingCards(): number {
    return this.progressions.length - this.currentIndex;
  }

  // Il n'y a pas de carte qui pivote : le recto et le verso sont deux
  // blocs du HTML, et cette variable décide lequel est affiché.
  revealAnswer(): void {
    this.showAnswer = true;
  }

  // On envoie juste "réussi ou raté" ; c'est le back end qui décide de
  // la prochaine date. Le faire côté navigateur permettrait de tricher.
  //
  // On attend sa confirmation avant de passer à la carte suivante :
  // pas question d'afficher un progrès qui n'aurait pas été enregistré.
  submitResult(reussi: boolean): void {
    this.progressionService.enregistrerRevision({
      flashcardId: this.currentFlashcard.id!,
      reussi: reussi
    }).subscribe({
      next: () => {
        if (this.currentIndex < this.progressions.length - 1) {
          // On avance d'une carte et on la remet face cachée.
          this.currentIndex++;
          this.showAnswer = false;
        } else {
          this.sessionFinished = true;
        }
        this.cdr.detectChanges();
      },
      error: () => {
        this.errorMessage = "Erreur lors de l'enregistrement de la révision.";
        this.cdr.detectChanges();
      }
    });
  }

  // Retourne à la page des flashcards de ce deck
  goBack(): void {
    this.router.navigate(['/flashcards', this.deckId]);
  }
}