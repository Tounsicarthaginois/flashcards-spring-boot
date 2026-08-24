// Écran des cartes d'un deck. Il gère la création de flashcards
// (avec plusieurs traductions et exemples imbriqués), et affiche pour
// chaque carte un badge indiquant où elle en est dans les révisions.

import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { FlashcardService } from '../../services/flashcard.service';
import { LangueService } from '../../services/langue.service';
import { DeckService } from '../../services/deck.service';
import { ProgressionService } from '../../services/progression.service';
import { AuthService } from '../../services/auth.service';
import { FlashcardDto, TraductionDto } from '../../dto/flashcard.dto';
import { LangueDto } from '../../dto/langue.dto';
import { DeckDto } from '../../dto/deck.dto';
import { ProgressionDto } from '../../dto/progression.dto';

@Component({
  selector: 'app-flashcards',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './flashcards.html',
  styleUrl: './flashcards.scss'
})
export class FlashcardsComponent implements OnInit {

  flashcards: FlashcardDto[] = [];
  langues: LangueDto[] = [];
  deckInfo: DeckDto | null = null;
  deckId!: number;
  showForm = false;

  // Une Map plutôt qu'un tableau : on retrouve la progression d'une carte
  // directement par son identifiant, sans reparcourir toute la liste
  // à chaque carte affichée.
  progressionsMap = new Map<number, ProgressionDto>();

  formData: FlashcardDto = {
    question: '',
    deckId: 0,
    traductions: [{ texte: '', langueId: 0, exemples: [] }]
  };

  isLoading = false;
  errorMessage = '';

  constructor(
    private flashcardService: FlashcardService,
    private langueService: LangueService,
    private deckService: DeckService,
    private progressionService: ProgressionService,
    public authService: AuthService,
    private route: ActivatedRoute,
    private router: Router,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    // On lit le numéro du deck dans l'adresse (/flashcards/12 → 12).
    // C'est la contrepartie du ":deckId" déclaré dans app.routes.ts.
    this.deckId = Number(this.route.snapshot.paramMap.get('deckId'));
    this.formData.deckId = this.deckId;
    this.loadDeckInfo();
    this.loadFlashcards();
    this.loadLangues();
    this.loadProgressions();
  }

  loadDeckInfo(): void {
    this.deckService.getById(this.deckId).subscribe({
      next: (deck) => { this.deckInfo = deck; this.cdr.detectChanges(); },
      error: () => console.error('Impossible de charger les infos du deck')
    });
  }

  get isOfficiel(): boolean {
    return this.deckInfo?.type === 'OFFICIELLE';
  }

  // Décide de l'affichage des boutons modifier et supprimer.
  // Un deck officiel appartient à la communauté : seul son créateur
  // garde le droit d'y toucher. Les autres decks restent libres.
  // Dans le HTML on écrit simplement *ngIf="canEdit".
  get canEdit(): boolean {
    if (!this.isOfficiel) return true;
    const currentUserId = this.authService.getUserId();
    return currentUserId !== null && this.deckInfo?.userId === currentUserId;
  }

  loadFlashcards(): void {
    this.isLoading = true;
    this.flashcardService.getByDeck(this.deckId).subscribe({
      next: (cards) => {
        this.flashcards = cards;
        this.isLoading = false;
        this.cdr.detectChanges();
      },
      error: () => {
        this.errorMessage = 'Erreur lors du chargement des flashcards.';
        this.isLoading = false;
        this.cdr.detectChanges();
      }
    });
  }

  // On range les progressions dans la Map, rangées par numéro de carte.
  // Les cartes absentes de cette liste n'ont jamais été révisées.
  loadProgressions(): void {
    this.progressionService.getProgressionsByDeck(this.deckId).subscribe({
      next: (progressions) => {
        this.progressionsMap.clear();
        for (const prog of progressions) {
          this.progressionsMap.set(prog.flashcardId, prog);
        }
        this.cdr.detectChanges();
      },
      error: () => console.error('Erreur chargement progressions')
    });
  }

  // Traduit les chiffres bruts du back end en badge lisible.
  // C'est le travail d'interprétation du front : le calcul des dates,
  // lui, est fait côté serveur (1j après un échec ou une 1re réussite,
  // puis 7j, puis 30j quand la carte est maîtrisée).
  getSrsBadge(cardId: number): { label: string, classe: string } {
    const prog = this.progressionsMap.get(cardId);

    // Absente de la Map = carte encore jamais révisée.
    if (!prog) {
      return { label: '🆕 Nouvelle', classe: 'badge-srs-new' };
    }

    const now = new Date();
    const prochaine = prog.prochaineRevision ? new Date(prog.prochaineRevision) : null;

    // La date prévue est dépassée : la carte est à revoir aujourd'hui.
    if (!prochaine || prochaine <= now) {
      return { label: '🔁 À réviser', classe: 'badge-srs-due' };
    }

    // Conversion de l'écart en jours : millisecondes → secondes → minutes
    // → heures → jours. On arrondit au-dessus pour ne jamais afficher "0j".
    const diffJours = Math.ceil((prochaine.getTime() - now.getTime()) / (1000 * 60 * 60 * 24));

    // Le niveau, c'est le nombre de bonnes réponses d'affilée.
    // Il retombe à zéro dès qu'on se trompe.
    const niveau = prog.nbRevisionsReussies;

    if (niveau === 0) {
      return { label: `❌ Raté — dans ${diffJours}j`, classe: 'badge-srs-failed' };
    } else if (niveau === 1) {
      return { label: `📗 Niveau 1/3 — dans ${diffJours}j`, classe: 'badge-srs-level1' };
    } else if (niveau === 2) {
      return { label: `📘 Niveau 2/3 — dans ${diffJours}j`, classe: 'badge-srs-level2' };
    } else {
      return { label: `✅ Maîtrisée — dans ${diffJours}j`, classe: 'badge-srs-mastered' };
    }
  }

  loadLangues(): void {
    this.langueService.getAll().subscribe({
      next: (langues) => { this.langues = langues; this.cdr.detectChanges(); },
      error: () => console.error('Erreur chargement langues')
    });
  }

  openCreateForm(): void {
    this.formData = {
      question: '',
      deckId: this.deckId,
      traductions: [{ texte: '', langueId: 0, exemples: [] }]
    };
    this.showForm = true;
  }

  // Ces quatre méthodes ne touchent jamais à l'affichage : elles se
  // contentent d'ajouter ou retirer une case dans un tableau. Comme le
  // HTML parcourt ces tableaux avec *ngFor, les champs apparaissent et
  // disparaissent tout seuls. C'est le principe de base d'Angular :
  // on modifie les données, l'écran suit.
  addTraduction(): void {
    this.formData.traductions.push({ texte: '', langueId: 0, exemples: [] });
  }

  removeTraduction(index: number): void {
    this.formData.traductions.splice(index, 1);
  }

  addExemple(trad: TraductionDto): void {
    trad.exemples.push({ phraseOriginal: '', phraseTraduite: '' });
  }

  removeExemple(trad: TraductionDto, index: number): void {
    trad.exemples.splice(index, 1);
  }

  // La carte, ses traductions et ses exemples partent en une seule
  // requête : le back end se charge de tout enregistrer d'un bloc.
  onSubmit(): void {
    this.flashcardService.create(this.formData).subscribe({
      next: () => {
        this.showForm = false;
        this.loadFlashcards();
        this.loadProgressions();
      },
      error: () => {
        this.errorMessage = 'Erreur lors de la création.';
        this.cdr.detectChanges();
      }
    });
  }

  deleteFlashcard(id: number): void {
    if (confirm('Supprimer cette flashcard ?')) {
      this.flashcardService.delete(id).subscribe({
        next: () => { this.loadFlashcards(); this.loadProgressions(); }
      });
    }
  }

  startReview(): void {
    this.router.navigate(['/review', this.deckId]);
  }

  goBack(): void {
    this.router.navigate(['/decks']);
  }
}