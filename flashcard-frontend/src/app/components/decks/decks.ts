// Page principale de l'application. Elle affiche deux listes distinctes :
// les decks personnels de l'utilisateur, et les decks officiels validés
// par un administrateur, visibles par tout le monde.

import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';

import { DeckService } from '../../services/deck.service';
import { AuthService } from '../../services/auth.service';
import { LangueService } from '../../services/langue.service';

import { DeckDto } from '../../dto/deck.dto';
import { LangueDto } from '../../dto/langue.dto';

@Component({
  selector: 'app-decks',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './decks.html',
  styleUrl: './decks.scss'
})
export class DecksComponent implements OnInit {

  decks: DeckDto[] = [];
  decksOfficiels: DeckDto[] = [];
  langues: LangueDto[] = [];

  showForm = false;

  // Sert de mémoire au formulaire : vide = on crée, rempli = on modifie.
  // C'est ce qui permet d'utiliser un seul formulaire pour les deux cas.
  editingDeck: DeckDto | null = null;

  formData: Partial<DeckDto> = { name: '', description: '', type: 'PRIVEE', langueId: 0 };

  isLoading = false;
  errorMessage = '';

  constructor(
    private deckService: DeckService,
    // "public" et pas "private" : le HTML a besoin d'appeler getEmail()
    // et getRole() directement, ce qu'il ne pourrait pas faire sinon.
    public authService: AuthService,
    private langueService: LangueService,
    private router: Router,
    private cdr: ChangeDetectorRef
  ) {}

  // Angular appelle cette méthode tout seul à l'ouverture de la page.
  // C'est l'endroit où on va chercher les données.
  ngOnInit(): void {
    this.loadDecks();
    this.loadDecksOfficiels();
    this.loadLangues();
  }

  // Le back end sait quel utilisateur demande, grâce au token joint
  // par l'interceptor : on n'envoie aucun identifiant nous-mêmes.
  loadDecks(): void {
    this.isLoading = true;
    this.deckService.getAll().subscribe({
      next: (decks) => {
        this.decks = decks;
        this.isLoading = false;
        this.cdr.detectChanges();
      },
      error: () => {
        this.errorMessage = 'Erreur lors du chargement des decks.';
        this.isLoading = false;
        this.cdr.detectChanges();
      }
    });
  }

  loadDecksOfficiels(): void {
    this.deckService.getOfficiels().subscribe({
      next: (decks) => {
        this.decksOfficiels = decks;
        this.cdr.detectChanges();
      },
      error: () => {
        // Volontairement silencieux : si les decks officiels ne se
        // chargent pas, l'utilisateur peut quand même travailler
        // sur les siens. On n'affiche pas d'erreur bloquante.
        console.error('Erreur chargement decks officiels');
        this.cdr.detectChanges();
      }
    });
  }

  // Alimente la liste déroulante des langues du formulaire.
  loadLangues(): void {
    this.langueService.getAll().subscribe({
      next: (langues) => {
        this.langues = langues;
        this.cdr.detectChanges();
      },
      error: () => console.error('Erreur chargement langues')
    });
  }

  // editingDeck remis à null : on repart sur une création.
  openCreateForm(): void {
    this.editingDeck = null;
    this.formData = { name: '', description: '', type: 'PRIVEE', langueId: 0 };
    this.showForm = true;
  }

  // On garde le deck de côté et on pré-remplit les champs avec ses valeurs.
  openEditForm(deck: DeckDto): void {
    this.editingDeck = deck;
    this.formData = { name: deck.name, description: deck.description, type: deck.type, langueId: deck.langueId };
    this.showForm = true;
  }

  // Un seul bouton pour deux actions : c'est editingDeck qui décide.
  // On recharge ensuite la liste plutôt que de la modifier à la main,
  // parce que le back end calcule des champs qu'on ne connaît pas encore
  // (identifiant du nouveau deck, nom de la langue, nombre de cartes).
  onSubmit(): void {
    if (this.editingDeck) {
      this.deckService.update(this.editingDeck.id, this.formData).subscribe({
        next: () => {
          this.showForm = false;
          this.loadDecks();
        }
      });
    } else {
      this.deckService.create(this.formData).subscribe({
        next: () => {
          this.showForm = false;
          this.loadDecks();
        }
      });
    }
  }

  deleteDeck(id: number): void {
    if (confirm('Supprimer ce deck ?')) {
      this.deckService.delete(id).subscribe({
        next: () => this.loadDecks()
      });
    }
  }

  // Construit l'adresse /flashcards/12 : c'est le ":deckId" des routes.
  openDeck(deckId: number): void {
    this.router.navigate(['/flashcards', deckId]);
  }

  goToAdmin(): void {
    this.router.navigate(['/admin']);
  }

  logout(): void {
    this.authService.logout();
    this.router.navigate(['/login']);
  }
}